package com.dpardo.strike.repository;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values; // Importante para valores nulos

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class JugadorRepository {

    /**
     * Inserta un jugador conectándolo a su País y a su Equipo.
     * Idioms: Clasificación (País) y Catálogo (Equipo).
     */
    public void insertarJugador(int id, String nombre, LocalDate fechaNacimiento, char sexo,
                                String paisCodFifa, String posicion, int equipoId,
                                Integer estadisticas, int altura, BigDecimal peso, byte[] foto) {

        Driver driver = DatabaseConnection.getDriver();

        // Convertimos BigDecimal a Double para Neo4j
        Double pesoDouble = (peso != null) ? peso.doubleValue() : null;
        // Convertimos char a String
        String sexoStr = String.valueOf(sexo);

        String query =
                "MATCH (p:Pais {cod_fifa: $codFifa}) " +
                        "MATCH (e:Equipo {id_equipo: $equipoId}) " +
                        "CREATE (j:Jugador { " +
                        "   id_jugador: $id, " +
                        "   nombre: $nombre, " +
                        "   f_nacimiento: $fecha, " +
                        "   sexo: $sexo, " +
                        "   posicion: $posicion, " +
                        "   estadisticas: $stats, " +
                        "   altura: $altura, " +
                        "   peso: $peso, " +
                        "   foto: $foto " +
                        "}) " +
                        "MERGE (j)-[:NACIONALIDAD]->(p) " +
                        "MERGE (j)-[:PERTENECE_A]->(e)";

        try (Session session = driver.session()) {
            session.run(query, parameters(
                    "id", id,
                    "nombre", nombre,
                    "fecha", fechaNacimiento,
                    "sexo", sexoStr,
                    "codFifa", paisCodFifa,
                    "posicion", posicion,
                    "equipoId", equipoId,
                    "stats", (estadisticas != null) ? estadisticas : Values.NULL,
                    "altura", altura,
                    "peso", pesoDouble,
                    "foto", (foto != null) ? foto : Values.NULL
            ));
            System.out.println("✅ Jugador guardado en Neo4j: " + nombre);
        } catch (Exception e) {
            System.err.println("Error insertando jugador: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene nombres de jugadores ordenados.
     */
    public List<String> obtenerTodosLosNombresDeJugadores() {
        Driver driver = DatabaseConnection.getDriver();
        List<String> nombres = new ArrayList<>();

        String query = "MATCH (j:Jugador) RETURN j.nombre ORDER BY j.nombre";

        try (Session session = driver.session()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                nombres.add(result.next().get("j.nombre").asString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nombres;
    }

    /**
     * Elimina un jugador por nombre (y sus relaciones automáticamente).
     */
    public void eliminarJugadorPorNombre(String nombre) {
        Driver driver = DatabaseConnection.getDriver();

        // DETACH DELETE borra el nodo y sus flechas (nacionalidad, equipo)
        String query = "MATCH (j:Jugador {nombre: $nombre}) DETACH DELETE j";

        try (Session session = driver.session()) {
            session.run(query, parameters("nombre", nombre));
            System.out.println("🗑️ Jugador eliminado: " + nombre);
        } catch (Exception e) {
            System.err.println("Error eliminando jugador: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}