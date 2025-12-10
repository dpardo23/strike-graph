package com.dpardo.strike.repository;

import com.dpardo.strike.domain.SessionManager;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import static org.neo4j.driver.Values.parameters;

public class UserRepository {

    // Validar credenciales usando Cypher
    public boolean isValidUser(String username, String password) {
        Driver driver = DatabaseConnection.getDriver();
        // Usamos una consulta segura con parámetros
        String query = "MATCH (u:User {nombre_usuario: $user, contrasena: $pass}) RETURN u.id_user";

        try (Session session = driver.session()) {
            Result result = session.run(query, parameters("user", username, "pass", password));
            return result.hasNext();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener ID del usuario
    public int getUserId(String username) {
        Driver driver = DatabaseConnection.getDriver();
        String query = "MATCH (u:User {nombre_usuario: $user}) RETURN u.id_user AS id";

        try (Session session = driver.session()) {
            Result result = session.run(query, parameters("user", username));
            if (result.hasNext()) {
                return result.next().get("id").asInt();
            }
        }
        return -1;
    }

    // Obtener Rol Activo
    public String getUserRole(String username) {
        Driver driver = DatabaseConnection.getDriver();
        String query = "MATCH (u:User {nombre_usuario: $user})-[r:TIENE_ROL]->(rol:Rol) " +
                "WHERE r.activo = true " +
                "RETURN rol.nombre AS rolNombre LIMIT 1";

        try (Session session = driver.session()) {
            Result result = session.run(query, parameters("user", username));
            if (result.hasNext()) {
                return result.next().get("rolNombre").asString();
            }
        }
        return null;
    }

    public void registrarSesion() {
        Driver driver = DatabaseConnection.getDriver();
        int userId = SessionManager.getInstance().getUserId();

        // 1. Obtener PID REAL del Sistema Operativo
        long realPidLong = ProcessHandle.current().pid();
        int realPid = (int) realPidLong; // Casting seguro, los PIDs caben en int

        // 2. Obtener IP REAL de la máquina
        String realIp = "127.0.0.1"; // Valor por defecto (fallback)
        try {
            realIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("No se pudo determinar la IP real: " + e.getMessage());
        }

        // 3. Generar ID de Sesión
        int sessionId = Math.abs(new Random().nextInt()); // Math.abs para evitar negativos

        // 4. Puerto
        int port = 0;

        // Consulta Cypher
        String query =
                "MATCH (u:User {id_user: $userId}) " +
                        "CREATE (s:Session { " +
                        "   id_session: $sessionId, " +
                        "   pid: $pid, " +
                        "   user_addr: $ip, " +
                        "   user_port: $port, " +
                        "   fecha_inicio: datetime() " +
                        "}) " +
                        "MERGE (u)-[:TIENE_SESION]->(s) " +
                        "RETURN s.id_session, s.pid";

        try (Session session = driver.session()) {
            Result result = session.run(query, parameters(
                    "userId", userId,
                    "sessionId", sessionId,
                    "pid", realPid,
                    "ip", realIp,
                    "port", port
            ));

            if (result.hasNext()) {
                Record record = result.next();
                // Actualizamos el SessionManager con los datos confirmados
                SessionManager.getInstance().setSessionId(record.get("s.id_session").asInt());
                SessionManager.getInstance().setPid(record.get("s.pid").asInt());

                System.out.println("✅ Sesión REAL registrada en Neo4j.");
                System.out.println("   PID OS: " + realPid);
                System.out.println("   IP Local: " + realIp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error creando sesión en Neo4j");
        }
    }
}