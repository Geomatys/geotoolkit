/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.gui.javafx.filter;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import org.geotoolkit.gui.javafx.util.FXOptionDialog;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXCQLPane extends BorderPane {

    public FXCQLPane() {
        
        final HTMLEditor editor = new HTMLEditor();
        
        
    }
    
    public static void main(String[] args) throws Exception {
        
        Stage st = new Stage();
        st.setScene(new Scene(new FXCQLPane()));
        
        st.show();
        
        //new App().launch();
        
        
        
    }
    
    public static class App extends Application{

        @Override
        public void start(Stage primaryStage) throws Exception {
            final Scene scene = new Scene(new FXCQLPane());
                
                primaryStage.setScene(scene);
                primaryStage.show();
        }
        
    }
    
}
