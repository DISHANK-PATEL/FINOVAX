import { KiteConnect } from "kiteconnect";

const apiKey = "nqcz9xotxgek0dr1";
const apiSecret = "a9fmk7o0ya42oyb75owlsqjzq55whiz2";
const requestToken = "BbXpZ9Z8yZDR9oh7Lp0FeoytuQrDbWEE";
let accessToken = "";

const kc = new KiteConnect({ api_key: apiKey });

console.log(kc.getLoginURL());

async function init() {
  try {
    await generateSession();
    await getProfile();
  } catch (err) {
    console.error(err);
  }
}

async function generateSession() {
  try {
    const response = await kc.generateSession(requestToken, apiSecret);
    kc.setAccessToken(response.access_token);
    console.log(accessToken);
    console.log("Session generated:", response);
  } catch (err) {
    console.error("Error generating session:", err);
  }
}

async function getProfile() {
  try {
    const profile = await kc.getProfile();
    console.log("Profile:", profile);
  } catch (err) {
    console.error("Error getting profile:", err);
  }
}

// Initialize the API calls
init();
