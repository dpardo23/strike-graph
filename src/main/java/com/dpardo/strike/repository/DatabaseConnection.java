package com.dpardo.strike.repository;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class DatabaseConnection {

    private static final String URI = "neo4j+s://3e7fcd36.databases.neo4j.io";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "coJM-JGGtfBRG8pONd74RdkcQid3xuVV9XxwGo2fpd4";

    private static Driver driver;

    private DatabaseConnection() {
        // Constructor privado para Singleton
    }

    public static Driver getDriver() {
        if (driver == null) {
            try {
                driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD));
                System.out.println("✅ Conexión a Neo4j establecida exitosamente.");
            } catch (Exception e) {
                System.err.println("❌ Error conectando a Neo4j: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return driver;
    }

    public static void closeDriver() {
        if (driver != null) {
            driver.close();
            System.out.println("🔒 Conexión a Neo4j cerrada.");
        }
    }
}