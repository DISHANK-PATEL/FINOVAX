import { Collection } from "mongodb";
export interface MongoFieldSchema {
    field: string;
    type: string;
    isRequired: boolean;
    subFields?: MongoFieldSchema[];
}
export interface MongoCollectionSchema {
    collection: string;
    fields: MongoFieldSchema[];
    count: number;
    indexes?: unknown[];
}
export declare function inferSchemaFromValue(value: unknown): string;
export declare function inferSchemaFromDocument(doc: Record<string, unknown>, parentPath?: string): MongoFieldSchema[];
export declare function buildCollectionSchema(collection: Collection, sampleSize?: number): Promise<MongoCollectionSchema>;
