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

import java.util.Collection;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableObjectValue;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import org.geotoolkit.process.ProcessingRegistry;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXExecutionPane extends SplitPane {

    private final FXProcessRegistryPane registryPane = new FXProcessRegistryPane();
    private final FXProcessPane processPane = new FXProcessPane();

    public FXExecutionPane() {
        setOrientation(Orientation.HORIZONTAL);
        processPane.valueProperty().bind(registryPane.valueProperty());
        processPane.visibleProperty().bind(Bindings.isNotNull((ObservableObjectValue)registryPane.valueProperty()));

        registryPane.setMinWidth(200);
        registryPane.setMaxWidth(300);
        getItems().addAll(registryPane, processPane);
    }

    public void setProcessingRegistry(Collection<? extends ProcessingRegistry> registries) {
        registryPane.setProcessingRegistry(registries);
    }

    /**
     * Used by ProcessConsole.
     *
     * @param registries
     */
    public static void show(Collection<? extends ProcessingRegistry> registries) {

        //Init JavaFX, ugly, but we only have 2 choices, extent Application or create this.
        new JFXPanel();
        App.registries = registries;
        App.launch(App.class);
    }

    public static class App extends Application {

        private static Collection<? extends ProcessingRegistry> registries;

        public App(){

        }

        @Override
        public void start(Stage stage) throws Exception {
            final FXExecutionPane pane = new FXExecutionPane();
            if (registries != null) {
                pane.registryPane.setProcessingRegistry(registries);
            }
            final Scene scene = new Scene(pane, 1024, 768);
            stage.setTitle("Process execution");
            stage.setScene(scene);
            stage.show();
        }

    }
}
