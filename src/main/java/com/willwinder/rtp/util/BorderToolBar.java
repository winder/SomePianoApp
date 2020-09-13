package com.willwinder.rtp.util;

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class BorderToolBar extends BorderPane {
    private final ToolBar left;
    private final ToolBar center;
    private final ToolBar right;

    public BorderToolBar(double height, double opacity) {
        super();

        // Toolbar pane
        left = new ToolBar();
        center = new ToolBar();
        right = new ToolBar();

        String style = "-fx-background-color: rgba(100, 100, 100, " + opacity + ");";
        left.setStyle(style);
        center.setStyle(style);
        right.setStyle(style);

        left.setPrefHeight(height);
        center.setPrefHeight(height);
        right.setPrefHeight(height);

        var changeListener = (ListChangeListener<Node>) c -> updateHeight();
        left.getChildrenUnmodifiable().addListener(changeListener);
        center.getChildrenUnmodifiable().addListener(changeListener);
        right.getChildrenUnmodifiable().addListener(changeListener);

        HBox hbox = new HBox(left, center, right);
        hbox.setAlignment(Pos.CENTER);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(center, Priority.ALWAYS);

        this.setTop(hbox);
    }

    private void updateHeight() {
        // not sure what to put here.
    }

    public void addLeft(Node n) {
        left.getItems().add(n);
    }

    public void addCenter(Node n) {
        center.getItems().add(n);
    }

    public void addRight(Node n) {
        right.getItems().add(n);
    }
}
