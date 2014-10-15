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

package org.geotoolkit.gui.javafx.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXDialog {
    
    private final Stage stage = new Stage();
    private final ObservableList<Action> actions = FXCollections.observableArrayList();
    private final BorderPane allPane = new BorderPane();
    private final BorderPane south = new BorderPane();
    private final HBox buttonBox = new HBox(8);
    private Node content = null;
    private String title = null;
    private boolean modal = false;
    
    public FXDialog(){        
        south.setRight(buttonBox);        
        south.setPadding(new Insets(8, 8, 8, 8));
        allPane.setBottom(south);  
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }
    
    public Node getContent() {
        return content;
    }

    public void setContent(Node content) {
        this.content = content;
    }
    
    public ObservableList<Action> getActions() {
        return actions;
    }
    
    public void setVisible(Node owner, boolean state){
        if(state){
            final Window window = owner==null ? null : owner.getScene().getWindow();
            if(window!=null) stage.initOwner(window);
            allPane.setCenter(content);
            buttonBox.getChildren().clear();
            for(Action act : actions){
                buttonBox.getChildren().add(ActionUtils.createButton(act, ActionUtils.ActionTextBehavior.SHOW));
            }
            stage.setTitle(title);
            stage.setScene(new Scene(allPane));
            stage.initModality(modal?Modality.APPLICATION_MODAL:Modality.NONE);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.centerOnScreen();
            if(modal){
                stage.showAndWait();
            }else{
                stage.show();
            }
            
            stage.toFront();
        }else{
            stage.hide();
        }
    }
    
}
