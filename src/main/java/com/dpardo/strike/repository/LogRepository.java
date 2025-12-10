package com.dpardo.strike.repository;

import com.dpardo.strike.domain.SessionManager;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

import java.time.LocalDateTime;
import java.util.Random;

import static org.neo4j.driver.Values.parameters;

public class LogRepository {

    public void registrarLog(String accion, String tablaAfectada, String idRegistro, String datoOld, String datoNew) {
        Driver driver = DatabaseConnection.getDriver();

        // 1. Obtener datos de la sesión actual
        SessionManager sm = SessionManager.getInstance();
        int userId = sm.getUserId();
        String username = sm.getUsername(); // Para redundancia rápida en el nodo Log

        // Si no hay usuario logueado (ej: carga inicial), podemos usar un usuario sistema o saltar
        if (userId == 0) {
            System.out.println("⚠️ Auditoría: No hay usuario en sesión. Saltando log.");
            return;
        }

        // 2. Generar ID único para el Log
        int logId = Math.abs(new Random().nextInt());

        // 3. Consulta Cypher
        // Crea el nodo Log, busca al Usuario y la Acción, y crea las relaciones.
        String query =
                "MATCH (u:User {id_user: $userId}) " +
                        "MATCH (a:Action {nombre_accion: $accion}) " +
                        "CREATE (l:Log { " +
                        "   id_log: $logId, " +
                        "   nombre_usuario: $username, " +
                        "   nombre_accion: $accion, " +
                        "   tabla_afectada: $tabla, " +
                        "   id_registro_afectado: $idReg, " +
                        "   fecha_hora: datetime(), " +
                        "   dato_old: $old, " +
                        "   dato_new: $new " +
                        "}) " +
                        "MERGE (u)-[:GENERO_LOG]->(l) " +
                        "MERGE (l)-[:TIPO_ACCION]->(a)";

        try (Session session = driver.session()) {
            session.run(query, parameters(
                    "userId", userId,
                    "accion", accion,
                    "logId", logId,
                    "username", username,
                    "tabla", tablaAfectada,
                    "idReg", idRegistro,
                    "old", (datoOld == null ? "" : datoOld),
                    "new", (datoNew == null ? "" : datoNew)
            ));
            System.out.println("📝 Auditoría registrada: " + accion + " en " + tablaAfectada);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error al registrar log de auditoría: " + e.getMessage());
        }
    }
}