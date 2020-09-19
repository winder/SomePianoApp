module rtp {
    requires javafx.controls;
    requires java.desktop;
    requires com.google.common;
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.ikonli.javafx;
    requires com.dlsc.formsfx;

    exports com.willwinder.rtp;
    exports com.willwinder.rtp.model;
    exports com.willwinder.rtp.graphics.renderables;
}