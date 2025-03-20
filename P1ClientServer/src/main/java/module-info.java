module ro.mpp2025 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires java.desktop;

    opens ro.mpp2025 to javafx.fxml;
    exports ro.mpp2025;
    exports ro.mpp2025.controller;
    opens ro.mpp2025.controller to javafx.fxml;
    opens ro.mpp2025.model to javafx.base;

}