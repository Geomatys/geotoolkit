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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessingRegistry;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXExecutionPane extends SplitPane {

    private final FXRegistryTree registryTree = new FXRegistryTree();
    private final FXProcessPane processPane = new FXProcessPane();
    private final FXRegistryPane registryPane = new FXRegistryPane();

    public FXExecutionPane() {
        setOrientation(Orientation.HORIZONTAL);

        registryTree.valueProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<? extends Object> ov, Object old, Object ne) {
                if (ne instanceof ProcessingRegistry) {
                    registryPane.valueProperty().setValue((ProcessingRegistry) ne);
                    getItems().set(1, registryPane);
                } else if (ne instanceof ProcessDescriptor) {
                    processPane.setVisible(true);
                    processPane.valueProperty().setValue((ProcessDescriptor) ne);
                    getItems().set(1, processPane);
                } else {
                    processPane.setVisible(true);
                    processPane.valueProperty().setValue(null);
                    getItems().set(1, processPane);
                }
            }
        });

        registryTree.setMinWidth(200);
        registryTree.setMaxWidth(300);
        getItems().addAll(registryTree, processPane);
    }

    public void setProcessingRegistry(Collection<? extends ProcessingRegistry> registries) {
        registryTree.setProcessingRegistry(registries);
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
                pane.registryTree.setProcessingRegistry(registries);
            }
            final Scene scene = new Scene(pane, 1024, 768);
            stage.setTitle("Process execution");
            stage.setScene(scene);
            stage.show();
        }

    }
}
