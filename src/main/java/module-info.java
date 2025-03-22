module com.example.shop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires lombok;
    requires jbcrypt;
    requires itextpdf;
    requires java.desktop;

    opens com.example.shop to javafx.fxml;
    exports com.example.shop;
}