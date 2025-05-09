import { MongoClient } from "mongodb";
export let client;
export let db;
export async function connectToMongoDB(databaseUrl) {
    try {
        client = new MongoClient(databaseUrl);
        await client.connect();
        const resourceBaseUrl = new URL(databaseUrl);
        const dbName = resourceBaseUrl.pathname.split("/")[1] || "test";
        console.error(`Connecting to database: ${dbName}`);
        db = client.db(dbName);
    }
    catch (error) {
        console.error("MongoDB connection error:", error);
        throw error;
    }
}
export async function closeMongoDB() {
    await client?.close();
}
