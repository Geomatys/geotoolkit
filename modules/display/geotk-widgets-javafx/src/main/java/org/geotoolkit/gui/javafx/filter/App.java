/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.gui.javafx.filter;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author husky
 */
public class App extends Application {

    public static void main(String[] args) {
        new App().launch();
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        final Scene scene = new Scene(new FXCQLPane());

        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
