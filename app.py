import os
import json
import numpy as np
from flask import Flask, jsonify, request
from flask_cors import CORS
from googletrans import Translator
from dotenv import load_dotenv
import httpcore # For the monkeypatch if needed

# Langchain imports - adjust based on your langchain version
# For newer versions (e.g., >0.1.0):
from langchain_community.embeddings import SentenceTransformerEmbeddings
from langchain_community.vectorstores import Chroma
from langchain.memory import ConversationBufferMemory
from langchain.chains import ConversationalRetrievalChain
from langchain_google_genai import ChatGoogleGenerativeAI # Updated for Gemini

# Image processing
import easyocr
from tensorflow.keras.applications.efficientnet import preprocess_input # Corrected import path
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import load_img, img_to_array # Added
import matplotlib.image as mpimg # Added for reading image in /predict

# Google Gemini API
import google.generativeai as genai

# Monkeypatch for httpcore if required in your environment
# This is often a workaround for proxy issues with httpx
# If you don't need it, you can comment it out.
if hasattr(httpcore, 'SyncHTTPTransport'): # Check if the attribute exists
    setattr(httpcore, 'SyncHTTPTransport', httpcore.AsyncHTTPProxy) # Corrected target
else:
    print("Warning: httpcore.SyncHTTPTransport not found for monkeypatching.")


app = Flask(__name__)
CORS(app)

load_dotenv()

# Configure Gemini API Key
# Ensure your .env file has GOOGLE_API_KEY or GENIE_API_KEY
# The google-generativeai library typically expects GOOGLE_API_KEY
# If you are using GENIE_API_KEY, make sure genai.configure is called correctly.
api_key = os.getenv('GENIE_API_KEY') or os.getenv('GOOGLE_API_KEY')
if not api_key:
    raise ValueError("API key not found. Please set GENIE_API_KEY or GOOGLE_API_KEY in your .env file.")
genai.configure(api_key=api_key)


translator = Translator()

# Define classes for image classification model
classes = ['All Beauty', 'All Electronics', 'Appliances', 'Arts, Crafts & Sewing', 'Automotive',
           'Baby', 'Beauty', 'Cell Phones & Accessories', 'Clothing, Shoes & Jewelry', 'Electronics',
           'Grocery & Gourmet Food', 'Health & Personal Care', 'Industrial & Scientific', 'Musical Instruments',
           'Office Products', 'Patio, Lawn & Garden', 'Pet Supplies', 'Sports & Outdoors',
           'Tools & Home Improvement', 'Toys & Games']

# --- Global Initializations (Load models once) ---
try:
    image_classification_model = load_model('model.h5')
    ocr_reader = easyocr.Reader(['en'], gpu=False) # Consider gpu=True if available and faster
except Exception as e:
    print(f"Error loading models: {e}")
    image_classification_model = None
    ocr_reader = None

# For ChromaDB, define settings if you need custom ones. Otherwise, defaults are often fine.
# from chromadb.config import Settings
# CHROMA_SETTINGS = Settings(
#     chroma_db_impl="duckdb+parquet", # Example setting
#     persist_directory="db"          # Example setting
# )
# If you don't have specific CHROMA_SETTINGS, you can often omit it,
# and Chroma will use defaults. For persistence, `persist_directory` is key.
CHROMA_SETTINGS = None # Using None will use Chroma's default client settings if applicable for the constructor version.
                       # Or, if your Chroma version requires it for persistence, ensure it's configured.

# --- Helper function for image reading ---
def read_image_for_classification(filename):
    img = load_img(filename, target_size=(224, 224))
    x = img_to_array(img)
    x = np.expand_dims(x, axis=0)
    x = preprocess_input(x)
    return x

# --- Routes ---

# @app.route('/chatbot/<string:instruction>/<string:source>/<string:des>', methods=['GET', 'POST'])
# def chatgpt_call(instruction, source, des):
#     if not instruction:
#         return jsonify({"error": "Instruction cannot be empty"}), 400

#     try:
#         memory = ConversationBufferMemory(memory_key="chat_history", return_messages=True, output_key='answer') # added output_key
#         embeddings = SentenceTransformerEmbeddings(model_name="all-MiniLM-L6-v2")

#         # For persisting ChromaDB, ensure the directory "db" exists or Chroma can create it.
#         # client_settings might be needed depending on your Chroma version and how you manage persistence.
#         # If CHROMA_SETTINGS is None, it uses defaults.
#         db = Chroma(persist_directory="db", embedding_function=embeddings) # Removed client_settings if not strictly needed or defined

#         retriever = db.as_retriever()
        
#         # Initialize Gemini LLM for Langchain
#         llm = ChatGoogleGenerativeAI(model="gemini-pro", temperature=0.7, convert_system_message_to_human=True) # Added convert_system_message_to_human for safety

#         qa = ConversationalRetrievalChain.from_llm(llm=llm, retriever=retriever, memory=memory)

#         detected_lang = translator.detect(instruction).lang
#         translated_instruction = instruction

#         if source.lower() != des.lower():
#             if detected_lang != source.lower():
#                 return jsonify({"error": f"Instruction language '{detected_lang}' does not match source language '{source}'"}), 400
#             translated_instruction = translator.translate(instruction, src=source, dest=des).text
        
#         response = qa.invoke({"question": translated_instruction}) # use invoke for newer langchain
#         answer = response.get('answer', "Sorry, I couldn't find an answer.")

#         ans_response = {
#             "Answer": answer
#         }
#         print(ans_response)
#         return jsonify(ans_response)

#     except Exception as e:
#         print(f"Error in /chatbot: {e}")
#         return jsonify({"error": str(e)}), 500


@app.route('/predict/<path:file_path_encoded>', methods=['GET', 'POST']) # Use path converter for '/')
def classify(file_path_encoded):
    if not image_classification_model or not ocr_reader:
        return jsonify({"error": "Models not loaded. Cannot predict."}), 500

    file_path = file_path_encoded.replace('$', '/') # Keep this if client sends $ instead of /
    print(f"Processing file: {file_path}")

    if not os.path.exists(file_path):
        return jsonify({"error": f"File not found: {file_path}"}), 404

    try:
        # Perform OCR
        results = ocr_reader.readtext(file_path)
        extracted_text = " ".join([detection[1] for detection in results])
        print(f"Extracted text length: {len(extracted_text)}")

        if len(extracted_text) > 50: # Adjusted threshold, consider what's meaningful
            print(f"Extracted text: {extracted_text[:200]}...") # Print a snippet
            
            # Define categories carefully, ensure Gemini can map to them
            bill_categories = ['Appliances', 'Arts, Crafts & Sewing', 'Automotive', 'Baby Products',
                               'Beauty Products', 'Cell Phones & Accessories', 'Clothing, Shoes & Jewelry',
                               'Electronics', 'Grocery & Gourmet Food', 'Health & Personal Care',
                               'Musical Instruments', 'Patio, Lawn & Garden', 'Pet Supplies',
                               'Sports & Outdoors', 'Toys & Games', 'Beverages', 'Restaurant', 'Services', 'Other']

            prompt = f"""You are an AI assistant specialized in bill processing.
Read the following text extracted from a bill image:
---
{extracted_text}
---
Return a JSON object with the following keys: "receiver", "totalAmount", "category", "items".
- "receiver": The name of the store or service provider.
- "totalAmount": The final total amount of the bill as a number (e.g., 123.45). Do not include currency symbols. If multiple totals are present, pick the grand total.
- "category": Categorize all items into a single most relevant category from this list: {bill_categories}.
- "items": A list of strings, where each string is a brief description of an item or service purchased. If not clearly itemized, provide a general description.

If you cannot confidently determine a value for any key, return null for that key.
Example of a valid JSON output:
{{
  "receiver": "SuperMart",
  "totalAmount": 75.99,
  "category": "Grocery & Gourmet Food",
  "items": ["Milk 1L", "Bread Loaf", "Eggs Dozen"]
}}
Output ONLY the JSON object.
"""
            gemini_model_direct = genai.GenerativeModel('gemini-pro')
            response = gemini_model_direct.generate_content(prompt)
            
            # Clean the response to ensure it's valid JSON
            cleaned_response_text = response.text.strip()
            if cleaned_response_text.startswith("```json"):
                cleaned_response_text = cleaned_response_text[7:]
            if cleaned_response_text.endswith("```"):
                cleaned_response_text = cleaned_response_text[:-3]
            
            print(f"Gemini Raw Response: {cleaned_response_text}")

            try:
                answer_json = json.loads(cleaned_response_text)
                ans = {
                    "category": answer_json.get("category"),
                    "receiver": answer_json.get("receiver"),
                    "amount": answer_json.get("totalAmount"),
                    "description": answer_json.get("items") # Changed to items, as per prompt
                }
            except json.JSONDecodeError as je:
                print(f"JSON Decode Error: {je}")
                print(f"Problematic Gemini response: {cleaned_response_text}")
                # Fallback or error message if JSON is not parsable
                ans = {
                    "category": "Error: Could not parse bill details",
                    "description": extracted_text[:200], # return some text
                    "receiver": None,
                    "amount": None,
                    "error_detail": "Gemini response was not valid JSON."
                }

        else:
            # Fallback to image classification if OCR text is too short
            img_array = read_image_for_classification(file_path)
            class_prediction = image_classification_model.predict(img_array)
            classes_x = np.argmax(class_prediction[0])
            pred = classes[classes_x] if classes_x < len(classes) else "Unknown"
            ans = {
                "category": pred,
                "description": "Image classified (OCR text was minimal)",
                "receiver": None,
                "amount": None
            }
        
        print(f"Final Answer for /predict: {ans}")
        return jsonify(ans)

    except Exception as e:
        print(f"Error in /predict: {e}")
        app.logger.error(f"Error in /predict for file {file_path}: {e}", exc_info=True)
        return jsonify({"error": str(e), "file": file_path}), 500


@app.route('/market/<string:company>', methods=['POST']) # Changed to POST to accept JSON body
def news_summariser(company):
    if not request.is_json:
        return jsonify({"error": "Request must be JSON"}), 400
    
    data = request.get_json()
    text = data.get('text') # 'text' should contain the news articles

    if not text:
        return jsonify({"error": "Missing 'text' (news content) in request body"}), 400

    instruction = f"""You are an AI assistant providing financial expertise.
Summarize the following recent news about {company}. Focus on the overall important implications for the market and the company's stock, if discernible.
Be concise and objective.

News Text:
---
{text}
---

Summary:
"""
    try:
        gemini_model_direct = genai.GenerativeModel('gemini-pro') # Or gemini-1.5-flash for speed
        response = gemini_model_direct.generate_content(instruction)
        
        summary = response.text

        ans_response = {
            "summary": summary
        }
        print(ans_response)
        return jsonify(ans_response)
    except Exception as e:
        print(f"Error in /market: {e}")
        return jsonify({"error": str(e)}), 500


@app.route('/expense/', methods=['POST']) # Changed to POST, assuming it will take input
def expense_predictor():
    if not request.is_json:
        return jsonify({"error": "Request must be JSON"}), 400
        
    data = request.get_json()
    # Example: text = data.get('transaction_history', '')
    # Implement your expense prediction logic here
    # This is a placeholder
    
    # For now, just acknowledging the input
    # You would replace this with actual logic using Gemini or another model
    user_text = data.get('text', 'No text provided for expense prediction.')

    # Placeholder: Echo input or provide a dummy prediction
    ans = {
        "message": "Expense predictor endpoint called.",
        "received_text": user_text,
        "prediction": "Placeholder prediction - e.g., 'Future expenses likely to increase in travel category.'"
        # Add your actual prediction logic here
    }
    print(ans)
    return jsonify(ans)


if __name__ == "__main__":
    # Make sure 'db' directory exists for Chroma, or Chroma can create it.
    if not os.path.exists("db"):
        os.makedirs("db", exist_ok=True)
    app.run(debug=True, host='0.0.0.0', port=5000) # Added host and port for clarity