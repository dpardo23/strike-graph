module com.dpardo.strike {
    requires javafx.controls;
    requires javafx.fxml;

    // Librerías de UI que ya tenías
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.javafx;

    // IMPORTANTE: El nuevo driver de Neo4j
    requires org.neo4j.driver;

    // Para los tipos Date y Timestamp que usamos en el Domain
    requires java.sql;
    requires org.kordamp.ikonli.fontawesome5;

    // Permitir que JavaFX acceda a tus controladores y clases principales
    opens com.dpardo.strike to javafx.fxml;
    exports com.dpardo.strike;

    // Abrir paquetes de UI para que el FXML pueda inyectar los controladores
    exports com.dpardo.strike.ui.login;
    opens com.dpardo.strike.ui.login to javafx.fxml;

    exports com.dpardo.strike.ui.data_writer;
    opens com.dpardo.strike.ui.data_writer to javafx.fxml;

    exports com.dpardo.strike.ui.read_only;
    opens com.dpardo.strike.ui.read_only to javafx.fxml;

    exports com.dpardo.strike.ui.super_user;
    opens com.dpardo.strike.ui.super_user to javafx.fxml;

    // Exportar dominio para que las Tablas puedan leer los getters
    exports com.dpardo.strike.domain;
}