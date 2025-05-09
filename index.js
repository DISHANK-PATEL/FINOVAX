/*
Prerequisites:
  npm install whatsapp-web.js mongoose express cors dotenv
*/
require('dotenv').config();
const { Client, LocalAuth } = require('whatsapp-web.js');
const mongoose = require('mongoose');
const express = require('express');
const cors = require('cors');

// Configuration
const PORT = process.env.PORT || 5000;
const MONGO_URI = process.env.MONGO_URI || 'mongodb://localhost:27017/whatsappDB';
const TARGET_NUMBER = '9663083809@c.us';  // WhatsApp ID format

// Connect to MongoDB
(async () => {
  try {
    await mongoose.connect(MONGO_URI);
    console.log('✅ MongoDB connected');
  } catch (err) {
    console.error('❌ MongoDB connection error:', err);
    process.exit(1);
  }
})();

// Define schema and model for messages
const messageSchema = new mongoose.Schema({
  body: String,
  sender: String,
  recipient: String,
  timestamp: Date,
});
const Message = mongoose.model('Message', messageSchema);

// Initialize WhatsApp client with local auth
const client = new Client({ authStrategy: new LocalAuth() });

client.on('qr', qr => console.log('🔗 QR code received, scan with your phone'));
client.on('ready', () => console.log('✅ WhatsApp client is ready'));

// Track incoming messages from the target number
client.on('message', async msg => {
  if (msg.from === TARGET_NUMBER) {
    console.log(`📥 Incoming message from ${TARGET_NUMBER}: ${msg.body}`);
    const record = new Message({
      body: msg.body,
      sender: msg.from,
      recipient: msg.to,
      timestamp: msg.timestamp ? new Date(msg.timestamp * 1000) : new Date(),
    });
    try {
      await record.save();
      console.log(`💾 Saved incoming message from ${TARGET_NUMBER}`);
    } catch (err) {
      console.error('❌ Error saving incoming message:', err);
    }
  }
});

// Track outgoing messages to the target number
client.on('message_create', async msg => {
  if (msg.fromMe && msg.to === TARGET_NUMBER) {
    console.log(`📩 Outgoing message to ${TARGET_NUMBER}: ${msg.body}`);
    const record = new Message({
      body: msg.body,
      sender: msg.from,
      recipient: msg.to,
      timestamp: msg.timestamp ? new Date(msg.timestamp * 1000) : new Date(),
    });
    try {
      await record.save();
      console.log(`💾 Saved outgoing message to ${TARGET_NUMBER}`);
    } catch (err) {
      console.error('❌ Error saving outgoing message:', err);
    }
  }
});

// Start WhatsApp client
client.initialize();

// Express app for optional API endpoints
const app = express();
app.use(cors());
app.use(express.json());

app.get('/', (req, res) => res.send('WhatsApp-MongoDB bridge running'));

// Return last 100 saved messages
app.get('/messages', async (req, res) => {
  try {
    const messages = await Message.find({ recipient: TARGET_NUMBER })
      .sort({ timestamp: -1 })
      .limit(100);
    res.json(messages);
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch messages' });
  }
});

// Start Express server
app.listen(PORT, () => console.log(`🚀 Server listening on port ${PORT}`));
