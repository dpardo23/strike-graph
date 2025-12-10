package com.dpardo.strike.repository;

import com.dpardo.strike.domain.EquipoComboItem;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class EquipoRepository {

    /**
     * Obtiene una lista con todos los códigos FIFA de los países.
     * Idiom: Búsqueda simple de nodos básicos.
     */
    public List<String> obtenerCodigosPaises() {
        Driver driver = DatabaseConnection.getDriver();
        List<String> codigos = new ArrayList<>();

        String query = "MATCH (p:Pais) RETURN p.cod_fifa ORDER BY p.cod_fifa";

        try (Session session = driver.session()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                codigos.add(result.next().get("p.cod_fifa").asString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codigos;
    }

    /**
     * Obtiene items para el ComboBox de equipos.
     */
    public List<EquipoComboItem> obtenerEquiposParaCombo() {
        Driver driver = DatabaseConnection.getDriver();
        List<EquipoComboItem> equipos = new ArrayList<>();

        String query = "MATCH (e:Equipo) RETURN e.id_equipo, e.nombre ORDER BY e.id_equipo";

        try (Session session = driver.session()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                var record = result.next();
                equipos.add(new EquipoComboItem(
                        record.get("e.id_equipo").asInt(),
                        record.get("e.nombre").asString()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return equipos;
    }

    /**
     * Inserta un nuevo equipo y lo conecta a su País.
     * Idiom: Clasificación (Equipo ES_DE_PAIS Pais).
     */
    public void insertarEquipo(int id, String nombre, String paisCodFifa, String ciudad, LocalDate fechaFundacion, String dt) {
        Driver driver = DatabaseConnection.getDriver();

        // Query: Crea el equipo y hace MERGE de la relación con el País existente
        String query =
                "MATCH (p:Pais {cod_fifa: $codFifa}) " +
                        "CREATE (e:Equipo { " +
                        "   id_equipo: $id, " +
                        "   nombre: $nombre, " +
                        "   ciudad: $ciudad, " +
                        "   f_fundacion: $fecha, " +
                        "   director_tecnico: $dt " +
                        "}) " +
                        "MERGE (e)-[:ES_DE_PAIS]->(p)";

        try (Session session = driver.session()) {
            session.run(query, parameters(
                    "id", id,
                    "nombre", nombre,
                    "codFifa", paisCodFifa,
                    "ciudad", ciudad,
                    "fecha", fechaFundacion, // El driver maneja LocalDate automáticamente
                    "dt", dt
            ));
            System.out.println("✅ Equipo guardado en Neo4j: " + nombre);
        } catch (Exception e) {
            System.err.println("Error insertando equipo: " + e.getMessage());
            throw new RuntimeException(e); // Relanzamos para que la UI muestre el error
        }
    }
}