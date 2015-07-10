/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.render2d.edition;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.gui.javafx.chooser.FXMapLayerComboBox;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.navigation.FXPanHandler;
import org.geotoolkit.internal.GeotkFX;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXToolBox extends BorderPane {

    private final ObservableList<EditionTool> tools = FXCollections.observableArrayList();
    private final IntegerProperty toolPerRow = new SimpleIntegerProperty(8);
    private final GridPane grid = new GridPane();
    private final Accordion accordion = new Accordion();
    private final ToggleGroup group = new ToggleGroup();
    private final FXMapLayerComboBox combo = new FXMapLayerComboBox();
    private final FXMap map;

    public FXToolBox(FXMap map) {
        this.map = map;
        getStylesheets().add("/org/geotoolkit/gui/javafx/buttonbar.css");
        
        tools.addListener((Change<? extends EditionTool> c) -> updateGrid());
        toolPerRow.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateGrid());
        setTop(new VBox(5, combo,grid));
        setCenter(accordion);
        setMargin(accordion, new Insets(10));

        group.selectedToggleProperty().addListener(this::editorChange);

        final ContextContainer2D container = map.getContainer();
        combo.setMapContext(container.getContext());
    }

    /**
     * Live list of tools displayed.
     * @return
     */
    public ObservableList<EditionTool> getTools() {
        return tools;
    }

    /**
     * Number of tools on each row.
     * @return
     */
    public IntegerProperty getToolPerRow() {
        return toolPerRow;
    }

    private void editorChange(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue){
        if(oldValue!=null && oldValue.getUserData() instanceof EditionTool){
            //uninstall previous tool
            final EditionTool tool = (EditionTool) oldValue.getUserData();
            map.setHandler(new FXPanHandler(false));
            accordion.getPanes().clear();
        }
        if(newValue!=null && newValue.getUserData() instanceof EditionTool){
            final EditionTool tool = (EditionTool) newValue.getUserData();
            final Node configPane = tool.getConfigurationPane();
            final Node helpPane = tool.getHelpPane();

            if(helpPane!=null){
                final TitledPane pane = new TitledPane(GeotkFX.getString(FXToolBox.class, "help"), helpPane);
                accordion.getPanes().add(pane);
            }
            if(configPane!=null){
                final TitledPane pane = new TitledPane(GeotkFX.getString(FXToolBox.class, "params"), configPane);
                accordion.getPanes().add(pane);
            }

        }
    }

    private void updateGrid(){
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        group.getToggles().clear();
        
        final ColumnConstraints colFirst = new ColumnConstraints(1, 1, Double.MAX_VALUE,Priority.ALWAYS,HPos.CENTER,true);
        final ColumnConstraints colLast = new ColumnConstraints(1, 1, Double.MAX_VALUE,Priority.ALWAYS,HPos.CENTER,true);

        final int nbCol = toolPerRow.intValue();
        final int nbRow = (int)Math.ceil((float)tools.size()/nbCol);

        //create column constraints
        grid.getColumnConstraints().add(colFirst);
        for(int i=0;i<nbCol;i++){
            grid.getColumnConstraints().add(new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE,Priority.NEVER,HPos.CENTER,true));
        }
        grid.getColumnConstraints().add(colLast);

        //first column constraint, resizable
        int toolIdx = 0;
        for(int y=0;y<nbRow;y++){
            for(int x=0;x<nbCol;x++,toolIdx++){
                //get style
                String styleName = "buttongroup-";
                if(nbRow==1){
                    styleName += ((x==0)?"left":(x==nbCol-1)?"right":"center");
                }else{
                    styleName += ((y==0)?"top-":(y==nbRow-1)?"bottom-":"center-");
                    styleName += ((x==0)?"left":(x==nbCol-1)?"right":"center");
                }

                final ToggleButton button = new ToggleButton();
                button.getStyleClass().add(styleName);
                button.setToggleGroup(group);
                button.setMaxHeight(Double.MAX_VALUE);
                button.setMaxWidth(Double.MAX_VALUE);

                if(toolIdx<tools.size()){
                    final EditionTool tool = tools.get(toolIdx);
                    button.setGraphic(new ImageView(tool.getIcon()));
                    button.setUserData(tool);
                }else{
                    button.setDisable(true);
                }

                grid.add(button, x+1, y, 1, 1);
            }
        }
    }

    private void addTool(){
        
    }

}
