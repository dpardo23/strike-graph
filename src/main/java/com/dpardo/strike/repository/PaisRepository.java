package com.dpardo.strike.repository;

import com.dpardo.strike.domain.Pais;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class PaisRepository {

    // Instancia del Logger
    private final LogRepository logger = new LogRepository();

    public List<Pais> findAll() {
        // ... (código igual al anterior) ...
        Driver driver = DatabaseConnection.getDriver();
        List<Pais> paises = new ArrayList<>();
        String query = "MATCH (p:Pais) RETURN p.cod_fifa, p.nombre, p.continente ORDER BY p.nombre";
        try (Session session = driver.session()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                var record = result.next();
                paises.add(new Pais(
                        record.get("p.cod_fifa").asString(),
                        record.get("p.nombre").asString(),
                        record.get("p.continente").asString()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paises;
    }

    public void save(Pais pais) {
        Driver driver = DatabaseConnection.getDriver();

        // Verificación de duplicados (Igual que antes)
        String checkQuery = "MATCH (p:Pais {cod_fifa: $cod}) RETURN count(p) as cant";

        try (Session session = driver.session()) {
            int existing = session.run(checkQuery, parameters("cod", pais.getCodFifa()))
                    .single().get("cant").asInt();
            if (existing > 0) throw new RuntimeException("El código FIFA '" + pais.getCodFifa() + "' ya existe.");

            // 1. Crear el nodo (Acción Principal)
            String query = "CREATE (p:Pais {cod_fifa: $cod, nombre: $nom, continente: $cont})";
            session.run(query, parameters(
                    "cod", pais.getCodFifa(),
                    "nom", pais.getNombre(),
                    "cont", pais.getContinente()
            ));

            // 2. REGISTRAR LOG (Acción Secundaria)
            // dato_new: Simulamos un JSON simple con los datos
            String datoNew = "{cod: '" + pais.getCodFifa() + "', nombre: '" + pais.getNombre() + "'}";
            logger.registrarLog("INSERT", "pais", pais.getCodFifa(), null, datoNew);

            System.out.println("✅ País guardado y auditado.");

        } catch (Exception e) {
            throw new RuntimeException("Error BD: " + e.getMessage());
        }
    }

    public boolean update(Pais pais) {
        Driver driver = DatabaseConnection.getDriver();
        // Antes de actualizar, podríamos querer leer el dato viejo para el log (Opcional, consume recursos)
        // Por simplicidad, registraremos el update directo.

        String query = "MATCH (p:Pais {cod_fifa: $cod}) SET p.nombre = $nom, p.continente = $cont";
        try (Session session = driver.session()) {
            session.run(query, parameters(
                    "cod", pais.getCodFifa(),
                    "nom", pais.getNombre(),
                    "cont", pais.getContinente()
            ));

            // LOG
            String datoNew = "{nombre: '" + pais.getNombre() + "', continente: '" + pais.getContinente() + "'}";
            logger.registrarLog("UPDATE", "pais", pais.getCodFifa(), "Dato anterior desconocido (optimización)", datoNew);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(String codFifa) {
        Driver driver = DatabaseConnection.getDriver();
        String query = "MATCH (p:Pais {cod_fifa: $cod}) DETACH DELETE p";
        try (Session session = driver.session()) {
            session.run(query, parameters("cod", codFifa));

            // LOG
            logger.registrarLog("DELETE", "pais", codFifa, "{cod: '" + codFifa + "'}", null);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}