module io.github.petarmilunovic {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.naming;
    requires java.sql;

    opens io.github.petarmilunovic to org.hibernate.orm.core, javafx.fxml;

    exports io.github.petarmilunovic;
}
