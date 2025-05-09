import { MongoClient, Db } from "mongodb";
export declare let client: MongoClient;
export declare let db: Db;
export declare function connectToMongoDB(databaseUrl: string): Promise<void>;
export declare function closeMongoDB(): Promise<void>;
