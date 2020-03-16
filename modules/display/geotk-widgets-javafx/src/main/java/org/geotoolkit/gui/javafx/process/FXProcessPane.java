/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.javafx.process;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.web.WebView;
import org.apache.sis.internal.util.X364;
import static org.apache.sis.internal.util.X364.BOLD;
import static org.apache.sis.internal.util.X364.FOREGROUND_DEFAULT;
import static org.apache.sis.internal.util.X364.FOREGROUND_GREEN;
import static org.apache.sis.internal.util.X364.FOREGROUND_RED;
import static org.apache.sis.internal.util.X364.RESET;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.geotoolkit.gui.javafx.parameter.FXParameterEditor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessListener;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXProcessPane extends SplitPane {

    private final SimpleObjectProperty<ProcessDescriptor> processProperty = new SimpleObjectProperty<>();

    private final WebView browser = new WebView();
    private final FXParameterEditor editorInput = new FXParameterEditor();
    private final FXParameterEditor editorOuput = new FXParameterEditor();
    private final TitledPane titlein = new TitledPane("Inputs", editorInput);
    private final TitledPane titleout = new TitledPane("Outputs", editorOuput);
    private final Accordion accordion = new Accordion();
    private final Button executeButton = new Button("Execute", new Glyph("FontAwesome", FontAwesome.Glyph.PLAY));
    private final TextArea progress = new TextArea();

    private final ProcessListener processListener = new ProcessListener() {
            @Override
            public void started(final ProcessEvent event) {
                printEvent(event, FOREGROUND_DEFAULT);
            }
            @Override
            public void progressing(final ProcessEvent event) {
                printEvent(event, FOREGROUND_DEFAULT);
            }
            @Override
            public void dismissed(final ProcessEvent event) {
                printEvent(event, FOREGROUND_RED);
            }
            @Override
            public void completed(final ProcessEvent event) {
                printEvent(event, BOLD, FOREGROUND_GREEN);
            }
            @Override
            public void failed(final ProcessEvent event) {
                printEvent(event, FOREGROUND_RED);
            }
            @Override
            public void paused(final ProcessEvent event) {
                printEvent(event, FOREGROUND_DEFAULT);
            }
            @Override
            public void resumed(final ProcessEvent event) {
                printEvent(event, FOREGROUND_DEFAULT);
            }
        };

    public FXProcessPane() {
        setOrientation(Orientation.HORIZONTAL);
        progress.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Consolas; -fx-highlight-fill: #ffffff; -fx-highlight-text-fill: #000000; -fx-text-fill: #ffffff; ");
        progress.setEditable(false);

        processProperty.addListener((ObservableValue<? extends ProcessDescriptor> ov, ProcessDescriptor t, ProcessDescriptor t1) -> {
            update(t1);
        });
        executeButton.setOnAction((ActionEvent t) -> {
            execute();
        });

        accordion.getPanes().add(titlein);
        accordion.getPanes().add(titleout);
        accordion.setExpandedPane(titlein);

        final FlowPane flow = new FlowPane(executeButton);
        flow.setAlignment(Pos.CENTER_RIGHT);
        flow.setPadding(new Insets(4));

        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(browser);
        scrollPane.setMinSize(100, 100);
        scrollPane.setMinViewportWidth(100);

        final BorderPane border = new BorderPane();
        border.setCenter(flow);
        border.setBottom(accordion);

        final BorderPane left = new BorderPane();
        left.setCenter(browser);
        left.setBottom(border);

        getItems().addAll(left, progress);
        update(null);
    }

    public Property<ProcessDescriptor> valueProperty() {
        return processProperty;
    }

    private void update(ProcessDescriptor desc) {
        accordion.setExpandedPane(titlein);

        if (desc == null) {
            editorInput.setParameter(null);
            editorOuput.setParameter(null);
            editorInput.setDisable(true);
            editorOuput.setDisable(true);
            executeButton.setDisable(true);
            progress.setDisable(true);
            browser.getEngine().loadContent("");
            return;
        }

        final StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        if (desc.getIdentifier() != null) {
            sb.append("<h1>").append(String.valueOf(desc.getIdentifier().getCode()).toUpperCase()).append("</h1>");
        }
        if (desc.getProcedureDescription() != null) {
            sb.append("<p>").append(desc.getProcedureDescription()).append("</p>");
        }
        sb.append("</body></html>");
        browser.getEngine().loadContent(sb.toString());

        progress.setText("");

        editorInput.setDisable(false);
        editorOuput.setDisable(false);
        executeButton.setDisable(false);
        progress.setDisable(false);

        ParameterValueGroup inputs = desc.getInputDescriptor().createValue();
        ParameterValueGroup outputs = desc.getOutputDescriptor().createValue();
        editorInput.setParameter(inputs);
        editorOuput.setParameter(outputs);
    }

    private void execute() {
        progress.setText("");

        try {
            final ProcessDescriptor desc = processProperty.get();
            final Process process = desc.createProcess(editorInput.getParameter());
            process.addListener(processListener);
            final ParameterValueGroup results = process.call();
            editorOuput.setParameter(results);
            accordion.setExpandedPane(titleout);
        } catch (Exception ex) {
            accordion.setExpandedPane(titleout);
            print(FOREGROUND_RED,ex.getLocalizedMessage(),FOREGROUND_DEFAULT,"\n");
        }
    }

    private void printEvent(final ProcessEvent event, final Object... colors) {
        final List sb = new ArrayList();
        sb.addAll(Arrays.asList(colors));
        sb.add(BOLD);
        sb.add(event.getProgress());
        sb.add("%\t");
        sb.add(RESET);
        sb.addAll(Arrays.asList(colors));

        final InternationalString message = event.getTask();
        if(message != null){
            sb.add(message.toString());
        }

        final Throwable ex = event.getException();
        if(ex != null && message == null){
            sb.add(FOREGROUND_RED);
            sb.add(ex.getMessage());
            sb.add(FOREGROUND_DEFAULT);
        }
        if(ex != null){
            final StringWriter buffer = new StringWriter();
            final PrintWriter writer = new PrintWriter(buffer);
            ex.printStackTrace(writer);
            writer.flush();
            buffer.flush();
            final String str = buffer.toString();
            sb.add("\n");
            sb.add(FOREGROUND_RED);
            sb.add(str);
            sb.add(FOREGROUND_DEFAULT);
        }

        sb.add(RESET);
        sb.add("\n");

        print(sb.toArray());
    }

    /**
     * Print in the console the given objects.
     * X364 object are automatically removed if the console does not handle them.
     */
    private void print(final Object ... texts) {
        final String text;
        if (texts.length == 1) {
            text = String.valueOf(texts[0]);
        } else {
            final StringBuilder sb = new StringBuilder();
            for (Object obj : texts) {
                if (obj instanceof X364) {
                    if (false) { //no X364 , toto
                        sb.append( ((X364)obj).sequence() );
                    }
                } else {
                    sb.append(obj);
                }
            }
            text = sb.toString();
        }

        progress.setText(progress.getText()+text);
    }

}
