package com.dpardo.strike.repository;

import com.dpardo.strike.domain.Liga;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class LigaRepository {

    /**
     * Obtener todas las ligas.
     * Nota: En Neo4j, también traemos el ID del país navegando la relación.
     */
    public List<Liga> findAll() {
        Driver driver = DatabaseConnection.getDriver();
        List<Liga> ligas = new ArrayList<>();

        // Buscamos Ligas y sus Paises conectados
        String query =
                "MATCH (l:Liga)-[:ES_DE_PAIS]->(p:Pais) " +
                        "RETURN l.id_liga, l.nombre, l.tipo, p.cod_fifa ORDER BY l.nombre";

        try (Session session = driver.session()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                var record = result.next();
                // Mapeo del Record de Neo4j al Objeto Java Liga
                Liga liga = new Liga(
                        record.get("l.id_liga").asInt(),
                        record.get("l.nombre").asString(),
                        record.get("l.tipo").asString(),
                        record.get("p.cod_fifa").asString() // FK hacia País
                );
                ligas.add(liga);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ligas;
    }

    /**
     * Guardar Liga con Autoincremental Simulado y Relación.
     * Se usa executeWrite (API moderna) en lugar de writeTransaction.
     */
    public boolean save(Liga liga) {
        Driver driver = DatabaseConnection.getDriver();

        try (Session session = driver.session()) {
            // Usamos executeWrite para garantizar atomicidad
            return session.executeWrite(tx -> {
                // 1. Obtener el siguiente ID (Max + 1)
                // Usamos coalesce para manejar el caso de la primera liga (cuando max es null)
                Result resultId = tx.run("MATCH (l:Liga) RETURN coalesce(max(l.id_liga), 0) + 1 AS nextId");
                int nextId = resultId.single().get("nextId").asInt();

                // 2. Crear Nodo Liga y Conectar con País
                String query =
                        "MATCH (p:Pais {cod_fifa: $codPais}) " +
                                "CREATE (l:Liga {id_liga: $id, nombre: $nom, tipo: $tipo}) " +
                                "MERGE (l)-[:ES_DE_PAIS]->(p)"; // Idiom Clasificación

                tx.run(query, parameters(
                        "codPais", liga.getIdPais(),
                        "id", nextId,
                        "nom", liga.getNombre(),
                        "tipo", liga.getTipo()
                ));
                return true;
            });
        } catch (Exception e) {
            System.err.println("Error guardando liga: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Liga liga) {
        Driver driver = DatabaseConnection.getDriver();

        // Lógica de actualización:
        // 1. Actualizar propiedades simples.
        // 2. Borrar relación antigua con país (si existía).
        // 3. Crear nueva relación con país.
        String query =
                "MATCH (l:Liga {id_liga: $id}) " +
                        "SET l.nombre = $nom, l.tipo = $tipo " +
                        "WITH l " +
                        "OPTIONAL MATCH (l)-[r:ES_DE_PAIS]->() DELETE r " +
                        "WITH l " +
                        "MATCH (p:Pais {cod_fifa: $codPais}) " +
                        "MERGE (l)-[:ES_DE_PAIS]->(p)";

        try (Session session = driver.session()) {
            session.run(query, parameters(
                    "id", liga.getId(),
                    "nom", liga.getNombre(),
                    "tipo", liga.getTipo(),
                    "codPais", liga.getIdPais()
            ));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int idLiga) {
        Driver driver = DatabaseConnection.getDriver();
        // DETACH DELETE elimina el nodo y sus relaciones de golpe
        String query = "MATCH (l:Liga {id_liga: $id}) DETACH DELETE l";

        try (Session session = driver.session()) {
            session.run(query, parameters("id", idLiga));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}