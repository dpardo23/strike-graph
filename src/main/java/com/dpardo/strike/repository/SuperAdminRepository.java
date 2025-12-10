package com.dpardo.strike.repository;

import com.dpardo.strike.domain.SessionViewModel;
import com.dpardo.strike.domain.UiComboItem;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

/**
 * Repositorio para gestionar las operaciones específicas de la vista de Super Administrador.
 * Migrado de PostgreSQL a Neo4j.
 */
public class SuperAdminRepository {

    /**
     * Obtiene una lista detallada de todas las sesiones activas.
     * Recorre el grafo: Session <- User -[TIENE_ROL]-> Rol -[TIENE_PERMISO]-> Permission -[CONTROLA_UI]-> Ui
     */
    public List<SessionViewModel> obtenerSesionesActivas() {
        Driver driver = DatabaseConnection.getDriver();
        List<SessionViewModel> sesiones = new ArrayList<>();

        // Esta consulta replica la lógica de tu vista SQL 'obtener_sesiones_activas_detalladas'
        // Usamos OPTIONAL MATCH para imitar LEFT JOINs (por si falta rol o UI)
        String query =
                "MATCH (s:Session)<-[:TIENE_SESION]-(u:User) " +
                        "OPTIONAL MATCH (u)-[ur:TIENE_ROL]->(r:Rol) WHERE ur.activo = true " +
                        "OPTIONAL MATCH (r)-[:TIENE_PERMISO]->(:Permission)-[:CONTROLA_UI]->(ui:Ui) " +
                        "WITH s, u, ur, r, ui " +
                        "ORDER BY ui.id_ui DESC " + // Priorizamos la UI de mayor nivel como en tu SQL
                        "RETURN " +
                        "   s.pid AS pid, " +
                        "   u.nombre_usuario AS nombre_usuario, " +
                        "   u.email AS correo, " +
                        "   u.fecha_creacion AS fec_creacion_usuario, " +
                        "   r.nombre AS nombre_rol, " +
                        "   collect(ui.cod_componente)[0] AS cod_componente_ui, " + // Tomamos solo 1 UI
                        "   s.user_addr AS direccion_ip, " +
                        "   s.user_port AS puerto, " +
                        "   ur.fecha_asignacion AS fecha_asignacion_rol, " +
                        "   ur.activo AS rol_activo";

        try (Session session = driver.session()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                var record = result.next();

                // Conversión de tipos Neo4j -> Java SQL Types para compatibilidad con tu ViewModel
                Timestamp fecCreacion = record.get("fec_creacion_usuario").isNull() ? null :
                        Timestamp.from(record.get("fec_creacion_usuario").asZonedDateTime().toInstant());

                Timestamp fecAsignacion = record.get("fecha_asignacion_rol").isNull() ? null :
                        Timestamp.from(record.get("fecha_asignacion_rol").asZonedDateTime().toInstant());

                String nombreRol = record.get("nombre_rol").isNull() ? "Sin Rol" : record.get("nombre_rol").asString();
                String codUi = record.get("cod_componente_ui").isNull() ? "N/A" : record.get("cod_componente_ui").asString();
                boolean rolActivo = !record.get("rol_activo").isNull() && record.get("rol_activo").asBoolean();

                sesiones.add(new SessionViewModel(
                        record.get("pid").asInt(),
                        record.get("nombre_usuario").asString(),
                        record.get("correo").asString(),
                        fecCreacion,
                        nombreRol,
                        codUi,
                        record.get("direccion_ip").asString(),
                        record.get("puerto").asInt(),
                        fecAsignacion,
                        rolActivo
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sesiones;
    }

    /**
     * Obtiene una lista de componentes de UI a los que un usuario específico tiene permiso.
     * Lógica: User -> Rol (Activo) -> Permission (Activo) -> Ui (Activa)
     */
    public List<UiComboItem> obtenerUis(int userId) {
        Driver driver = DatabaseConnection.getDriver();
        List<UiComboItem> uis = new ArrayList<>();

        // Consulta
        String query =
                "MATCH (u:User {id_user: $userId})-[ur:TIENE_ROL]->(r:Rol) " +
                        "-[rp:TIENE_PERMISO]->(p:Permission)-[pui:CONTROLA_UI]->(i:Ui) " +
                        "WHERE ur.activo = true AND rp.activo = true AND pui.activo = true " +
                        "RETURN DISTINCT i.id_ui, i.cod_componente, i.descripcion " +
                        "ORDER BY i.id_ui";

        try (Session session = driver.session()) {
            Result result = session.run(query, parameters("userId", userId));
            while (result.hasNext()) {
                var record = result.next();
                uis.add(new UiComboItem(
                        record.get("i.id_ui").asInt(),
                        record.get("i.cod_componente").asString(),
                        record.get("i.descripcion").asString()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uis;
    }
}