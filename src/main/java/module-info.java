module com.remberall.remberall {
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.controls;
    requires java.sql;
    requires java.desktop;

    opens com.remberall.remberall to javafx.fxml;
    exports com.remberall.remberall;
    exports com.remberall.remberall.controller;
    opens com.remberall.remberall.controller to javafx.fxml;
}