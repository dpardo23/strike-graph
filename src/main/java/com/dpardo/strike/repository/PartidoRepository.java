package com.dpardo.strike.repository;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.neo4j.driver.Values.parameters;

/**
 * Repositorio para gestionar Partidos en Neo4j.
 * Implementa el Idiom de Reflexión Compuesta (Hyperedge).
 */
public class PartidoRepository {

    /**
     * Inserta un nuevo partido y establece todas las relaciones necesarias.
     * Estructura Grafo: (EquipoA)-[:JUEGA_LOCAL]->(Partido)<-[:JUEGA_VISITANTE]-(EquipoB)
     * (Liga)-[:CONTIENE_PARTIDO]->(Partido)
     */
    public void insertarPartido(int equipoLocalId, int equipoVisitanteId, LocalDate fecha, LocalTime hora, int ligaId, int historial) {
        Driver driver = DatabaseConnection.getDriver();

        // Validación lógica básica (Igual que en tu procedimiento almacenado SQL)
        if (equipoLocalId == equipoVisitanteId) {
            throw new RuntimeException("Un equipo no puede jugar contra sí mismo.");
        }

        String query =
                // 1. Encontramos los nodos participantes (Padres/Catálogos)
                "MATCH (local:Equipo {id_equipo: $localId}) " +
                        "MATCH (visita:Equipo {id_equipo: $visitaId}) " +
                        "MATCH (l:Liga {id_liga: $ligaId}) " +

                        // 2. Creamos el Nodo del Evento (Reflexión Compuesta)
                        "CREATE (p:Partido { " +
                        "   fecha: $fecha, " +
                        "   hora: $hora, " +
                        "   historial: $historial, " + // Tu ID único de negocio
                        "   equipo_local_id: $localId, " + // Redundancia útil para queries rápidas
                        "   equipo_visita_id: $visitaId " +
                        "}) " +

                        // 3. Establecemos las relaciones semánticas
                        "MERGE (local)-[:JUEGA_LOCAL]->(p) " +
                        "MERGE (visita)-[:JUEGA_VISITANTE]->(p) " +
                        "MERGE (l)-[:CONTIENE_PARTIDO]->(p)";

        try (Session session = driver.session()) {
            session.run(query, parameters(
                    "localId", equipoLocalId,
                    "visitaId", equipoVisitanteId,
                    "ligaId", ligaId,
                    "fecha", fecha, // El driver convierte LocalDate automáticamente a date() de Neo4j
                    "hora", hora,   // El driver convierte LocalTime automáticamente a localTime() de Neo4j
                    "historial", historial
            ));
            System.out.println("✅ Partido registrado correctamente en el grafo.");
        } catch (Exception e) {
            System.err.println("Error insertando partido: " + e.getMessage());
            // Aquí podrías manejar excepciones específicas de Neo4j si violas una constraint
            throw new RuntimeException(e);
        }
    }
}