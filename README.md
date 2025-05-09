# FINOVAX
FinnovaX is an AI-powered, real-time finance companion that unifies transactions, automation, and analytics. It integrates Generative AI (Claude/Gemini), AI agents, and agentic pipelines to deliver personalized, multilingual financial services—including OCR-based data extraction, autonomous trading, and a rich analytics dashboard.
## Overview

FinovaX is an AI-driven, full-stack personal finance platform designed to seamlessly capture, analyze, and act upon financial data. Built to unify fragmented financial streams like SMS, WhatsApp, and email, it empowers users with real-time insights, trend forecasting, and autonomous trading capabilities.

With Gemini, Claude, Node.js, Flask, and MongoDB at its core, FinovaX makes financial clarity and growth accessible to everyone.

---

## Key Features

- Data Ingestion  
  - Real-time SMS parsing using Android's `ContentResolver` and `BroadcastReceiver`.  
  - WhatsApp financial data extraction via `whatsapp-web.js`.  
  - Email transaction parsing via Gmail API.

- OCR and SmartFill  
  - Extract data from uploaded PDFs and images using Tesseract OCR.  
  - Normalize and auto-fill forms via AI field classification.

- AI Agent Orchestration  
  - Schedule, track, and execute tasks (expense logging, investment tracking) via conversational commands.  
  - Context management with Redis for personalized memory.

- Autonomous Trading Module  
  - Natural language trading through Zerodha Kite API.  
  - Risk analysis using real-time market data.

- Analytics and Visualization  
  - Financial dashboard built with React.js, TradingView, and Matplotlib.  
  - Category-wise spending, portfolio tracking, and profit-and-loss visualizations.  
  - Scheduled financial reports delivered through Gmail and WhatsApp.

- Security and Multilingual Support  
  - OAuth2 and JWT Authentication.  
  - Encrypted storage for sensitive data.  
  - English and Hindi translation support.

- Extensibility  
  - PensionBox and eKYC modules (coming soon).  
  - Mutual Fund and SIP recommendation engine (coming soon).

---
---

## Tech Stack

FinovaX utilizes a comprehensive technology stack across different layers:

- **Mobile App:** Kotlin and Android XML for seamless SMS ingestion and UI layouts.
- **Backend:** Node.js (Express) for session handling and API routes, plus Flask (Python) for AI tasks like OCR, NLP, and summarization.
- **AI and NLP:** Gemini for transaction classification and summarization, and Claude for memory tracking and investment proposal generation.
- **Frontend:** React.js combined with TailwindCSS for styling, TradingView for live charts, and Chart.js for custom visualizations.
- **OCR:** Tesseract to extract and process text from images and PDFs.
- **Database:** MongoDB for primary data storage and Redis for contextual memory management.
- **Trading API:** Zerodha Kite Connect for executing market orders.
- **Messaging APIs:** WhatsApp Web.js and the Gmail API for notifications and report delivery.
- **Mongodb Compass APIs:** For seamless integration of mongodb with the claude desktop via prompts.
- **Security:** OAuth2 for authentication, JWT for session management, and HTTPS encryption to secure data in transit.

---
## Sequence Diagram:
![image](https://github.com/user-attachments/assets/dd9f286c-6292-4352-a505-40996e2ceebe)
---
## UseCase Diagram:
![f6bb6f18-7203-4565-bc13-45f9b30367c1](https://github.com/user-attachments/assets/c0b14fb4-97e3-40cf-8478-e5a1999993fa)

## Snapshots:
![db9242e9-2dbb-44a8-8078-bbaf249c9e3e](https://github.com/user-attachments/assets/422752f8-0508-4d53-852d-b693d95b9fee)
![cda61527-d80b-489e-af3d-bb06c02a456a](https://github.com/user-attachments/assets/fcdcf54d-3056-4370-99ee-fc7a3c4ce5a4)
![2433b2bb-2c54-4051-a00b-1896dda312d6](https://github.com/user-attachments/assets/0d3558ca-9095-4ba5-9dce-124796196534)
![9c2f2c90-eb57-4b31-b7ea-a43754623711](https://github.com/user-attachments/assets/0cdcb622-5a74-4609-a013-e4b7e2f54294)
![d34c31d9-3661-4836-8d9d-ac8a4c03d98b](https://github.com/user-attachments/assets/72d6d99d-e282-42fa-a76d-2a6055c71d1c)
![bce371ae-1a86-4a7a-a4d0-3f72a5097cfc](https://github.com/user-attachments/assets/1a9c543c-6cdb-4d87-b045-672d703b0ca8)
![7525b3b3-752d-4216-bd58-783392c2235e](https://github.com/user-attachments/assets/3b77a579-3a85-49e7-a1ba-7e72b4624c8a)
![2799cefa-5c70-4d1b-9dd8-50f131d50767](https://github.com/user-attachments/assets/59f193be-e4e8-4213-bc07-f1cabe6d7e7b)
![640498ac-ee71-4eb3-b835-4423862805b1](https://github.com/user-attachments/assets/9c606c8b-2847-4bdd-9688-eee4d8469044)
![302858a9-6c04-4dc2-8e81-60aef5028979](https://github.com/user-attachments/assets/2366e089-4075-45f1-97e8-559116c5573d)
![97433ccd-4440-49d5-8a90-1c4f78fdf4fb](https://github.com/user-attachments/assets/9de5fd23-ba1f-4d14-9677-468a4b3ae30a)
![image](https://github.com/user-attachments/assets/129acc8e-d509-4308-90f2-cc746e2377a9)
![image](https://github.com/user-attachments/assets/5437df1d-b000-4344-9c4c-db5f5fc26f81)
![image](https://github.com/user-attachments/assets/6e86fd37-2380-4631-9b5e-47aa1f43a984)
![image](https://github.com/user-attachments/assets/9875e934-a194-4eb3-b635-5292b9d885b0)
![image](https://github.com/user-attachments/assets/65897e8d-904b-4527-b660-21ff5f9ca2a5)
![image](https://github.com/user-attachments/assets/55db9b6d-503a-4109-9734-3b9852d6c434)

## Future Roadmap

- Mutual Fund auto‑SIP scheduling
- UPI‑based microtransaction tracking
- PensionBox NPS integration
- Aadhaar eKYC onboarding
- Personalized roadmaps with reinforcement learning
- Voice assistant in English and Hindi

---

##Installation

```bash
# 1. Clone the repository
git clone https://github.com/DISHANK-PATEL/FINOVAX.git
cd FINOVAX

# 2. Start the MCP Server (Node.js) with MongoDB connection
node dist/index.js "mongodb://localhost:27017/minorProject"

# 3. Start the Frontend
cd client
npm install
npm run dev

# 4. Start the Backend (Node.js)
cd server
npm install
npm start

# 5. Start the Flask Service
cd backend/flask
pip install -r requirements.txt
python app.py

# 6. Start the Zerodha MCP Server
cd zerodha-mcp
bun index.ts
```

