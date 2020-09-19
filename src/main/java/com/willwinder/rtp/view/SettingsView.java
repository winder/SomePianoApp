package com.willwinder.rtp.view;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Section;
import com.dlsc.formsfx.model.validators.IntegerRangeValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;

import com.willwinder.rtp.model.params.AllParams;
import com.willwinder.rtp.model.params.BPMParams;
import com.willwinder.rtp.model.params.KeyPointCacheParams;
import com.willwinder.rtp.model.params.TimelineParams;
import com.willwinder.rtp.util.BorderToolBar;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.willwinder.rtp.Constants.DEFAULT_WIDTH;

/**
 * A window for configuring parameters.
 */
public class SettingsView extends Stage {
    private final AllParams params;
    private final Form form;

    public SettingsView(AllParams allParams, Stage parent) {
        VBox content = new VBox();

        this.params = allParams;
        this.form = makeForm(allParams);

        BorderToolBar toolbar = new BorderToolBar(50, 1.0);
        Button save = new Button("Save");
        Button reset = new Button("Reset");
        Button close = new Button("Close");

        save.disableProperty().bind(form.persistableProperty().not());
        reset.disableProperty().bind(form.persistableProperty().not());

        reset.setOnAction(e -> form.reset());
        save.setOnAction(e -> form.persist());
        close.setOnAction(e -> {
            form.reset();
            close();
        });

        toolbar.addLeft(close);
        toolbar.addCenter(reset);
        toolbar.addRight(save);

        var formView = new FormRenderer(form);

        content.getChildren().add(toolbar);
        content.getChildren().add(formView);

        Scene scene = new Scene(content, DEFAULT_WIDTH, 400);
        setScene(scene);
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
    }

    private static Form makeForm(AllParams allParams) {
        KeyPointCacheParams keyPointCacheParams = allParams.keyPointCacheParams;
        BPMParams bpmParams = allParams.bpmParams;
        TimelineParams timelineParams = allParams.timelineParams;
        return Form.of(
            Section.of(
                Field.ofDoubleType(allParams.grandStaffParams.offsetPercentTest)
                    .label("Testing the offset into the staff.")
            ).title("test"),
            Section.of(
                Field.ofIntegerType(keyPointCacheParams.firstKey)
                    .label("First Key")
                    .labelDescription("First key of keyboard in MIDI key code. A0 = 21.")
                    .validate(IntegerRangeValidator.between(0, 127, "Must be between 0-127")),
                Field.ofIntegerType(keyPointCacheParams.numKeys)
                    .label("Num Keys")
                    .labelDescription("Number of keys on keyboard.")
                    .validate(IntegerRangeValidator.between(1, 127, "What kind of piano do you have anyway...?!")),
                Field.ofDoubleType(keyPointCacheParams.whiteKeyHeightRatio)
                    .label("WhiteKeyHeightRatio")
                    .labelDescription("Ratio from white key width to white key height."),
                Field.ofDoubleType(keyPointCacheParams.blackKeyHeightRatio)
                    .label("BlackKeyHeightRatio")
                    .labelDescription("Ratio between white key height and black key height."),
                Field.ofDoubleType(keyPointCacheParams.blackKeyWidthRatio)
                    .label("BlackKeyWidthRatio")
                    .labelDescription("Ratio between white key width and black key width."),
                Field.ofDoubleType(keyPointCacheParams.leftMargin)
                    .label("Left Margin")
                    .labelDescription("Margin to left of keys."),
                Field.ofDoubleType(keyPointCacheParams.rightMargin)
                    .label("Right Margin")
                    .labelDescription("Margin to right of keys."),
                Field.ofDoubleType(keyPointCacheParams.padding)
                    .label("padding")
                    .labelDescription("Space between keys.")
            ).title("Keyboard"),
            Section.of(
                Field.ofIntegerType(bpmParams.bpm)
                    .label("BPM"),
                Field.ofIntegerType(timelineParams.timelineDurationMs)
                    .label("Timeline Duration Milliseconds"),
                Field.ofBooleanType(timelineParams.out)
                    .label("Outgoing (realtime)")
            ).title("Timeline")

        );
    }
}
