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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.controlsfx.control.action.ActionUtils;
import org.geotoolkit.gui.javafx.action.CommitAction;
import org.geotoolkit.gui.javafx.action.RollbackAction;
import org.geotoolkit.gui.javafx.chooser.FXMapLayerComboBox;
import org.geotoolkit.gui.javafx.render2d.FXCanvasHandler;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXToolBox extends BorderPane {

    private final ObservableList<EditionTool.Spi> tools = FXCollections.observableArrayList();
    private final IntegerProperty toolPerRow = new SimpleIntegerProperty(-1);
    private int dynamicNbCol = 1;

    private final GridPane grid = new GridPane();
    private final Accordion accordion = new Accordion();
    private final TitledPane helpPane = new TitledPane(GeotkFX.getString(FXToolBox.class, "help"), null);
    private final TitledPane paramsPane = new TitledPane(GeotkFX.getString(FXToolBox.class, "params"), null);
    private final HBox commitRollBackBar = new HBox();
    private final FXMapLayerComboBox combo = new FXMapLayerComboBox();
    private final FXMap map;
    private final CommitAction commitAction = new CommitAction();
    private final RollbackAction rollbackAction = new RollbackAction();

    private final ChangeListener<FXCanvasHandler> lst = new ChangeListener<FXCanvasHandler>() {
        @Override
        public void changed(ObservableValue<? extends FXCanvasHandler> observable, FXCanvasHandler oldValue, FXCanvasHandler newValue) {
            //listen to map handler change, unselect buttons
            for(Node n : grid.getChildren()){
                final ToggleButton tb = (ToggleButton) n;
                final EditionTool editionTool = (EditionTool) tb.getUserData();
                tb.setSelected( newValue == editionTool);
            }

            if(newValue instanceof EditionTool){
                final EditionTool et = (EditionTool) newValue;
                helpPane.setContent(et.getHelpPane());
                paramsPane.setContent(et.getConfigurationPane());
                helpPane.setExpanded(helpPane.getContent()!=null);
                paramsPane.setExpanded(paramsPane.getContent()!=null);
            }else{
                helpPane.setContent(null);
                paramsPane.setContent(null);
                helpPane.setExpanded(false);
                paramsPane.setExpanded(false);
            }
        }
    };

    /**
     * Create tool box with all map layers.
     *
     * @param map source map widget
     */
    public FXToolBox(final FXMap map) {
        this.map = map;
        init(map.getContainer().getContext());
    }

    /**
     * Create toolbox for a single layer.
     * 
     * @param map source map widget
     * @param layer edited layer
     */
    public FXToolBox(final FXMap map, final MapLayer layer) {
        this.map = map;
        final MapContext context = MapBuilder.createContext();
        context.layers().add(layer);
        init(context);
        //hide compbo box, we have only one layer.
        combo.setVisible(false);
        combo.setManaged(false);
        commitRollBackBar.managedProperty().bind(commitRollBackBar.visibleProperty());
        map.getHandlerProperty().addListener(new WeakChangeListener(lst));
    }

    /**
     * Control visibility of commit/rollback buttons.
     * @return commit rollback property
     */
    public BooleanProperty commitRollbackVisibleProperty(){
        return commitRollBackBar.visibleProperty();
    }

    private void init(final MapContext context) {
        getStylesheets().add("/org/geotoolkit/gui/javafx/buttonbar.css");

        //commit and rollback buttons
        final Button rollback = rollbackAction.createButton(ActionUtils.ActionTextBehavior.HIDE);
        final Button commit = commitAction.createButton(ActionUtils.ActionTextBehavior.HIDE);
        rollback.styleProperty().unbind();
        rollback.setStyle("-fx-base : #FFAAAA;");
        rollback.getStyleClass().add("buttongroup-left");
        commit.styleProperty().unbind();
        commit.setStyle("-fx-base : #AAFFAA;");
        commit.getStyleClass().add("buttongroup-right");
        commitRollBackBar.getChildren().addAll(rollback,commit);


        grid.setMaxWidth(Double.MAX_VALUE);
        tools.addListener((Change<? extends EditionTool.Spi> c) -> updateGrid());
        toolPerRow.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> updateGrid());
        final GridPane top = new GridPane();
        top.getColumnConstraints().add(new ColumnConstraints(250, GridPane.USE_COMPUTED_SIZE, Double.MAX_VALUE, Priority.ALWAYS, HPos.CENTER, true));
        top.getRowConstraints().add(new RowConstraints(USE_PREF_SIZE, USE_COMPUTED_SIZE, USE_PREF_SIZE, Priority.NEVER, VPos.CENTER, false));
        top.getRowConstraints().add(new RowConstraints(USE_PREF_SIZE, USE_COMPUTED_SIZE, USE_PREF_SIZE, Priority.NEVER, VPos.CENTER, false));
        top.getRowConstraints().add(new RowConstraints(USE_PREF_SIZE, USE_COMPUTED_SIZE, Double.MAX_VALUE, Priority.ALWAYS, VPos.TOP, true));
        top.add(combo, 0, 0);
        top.add(commitRollBackBar, 1, 0);
        top.add(grid, 0, 1, 2, 1);
        top.add(accordion, 0, 2, 2, 1);
        top.setVgap(10);
        top.setHgap(10);
        setCenter(top);

        accordion.getPanes().add(helpPane);
        accordion.getPanes().add(paramsPane);
        helpPane.disableProperty().bind(helpPane.contentProperty().isNull());
        paramsPane.disableProperty().bind(paramsPane.contentProperty().isNull());

        combo.setMapContext(context);
        combo.setPrefWidth(50);
        combo.setMinWidth(50);
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.valueProperty().addListener((ObservableValue<? extends MapLayer> observable, MapLayer oldValue, MapLayer newValue) -> updateGrid());

        commitAction.layerProperty().bind(GeotkFX.isInstance(combo.valueProperty(), FeatureMapLayer.class));
        rollbackAction.layerProperty().bind(GeotkFX.isInstance(combo.valueProperty(), FeatureMapLayer.class));

        //listen to grid size change
        final ToggleButton button = new ToggleButton();
        button.getStyleClass().add("buttongroup-right");
        button.setGraphic(new ImageView(GeotkFX.ICON_ADD));
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setAlignment(Pos.CENTER);
        button.setMaxHeight(GridPane.USE_PREF_SIZE);
        button.setMaxWidth(GridPane.USE_PREF_SIZE);
        final Scene snapScene = new Scene(button);
        snapScene.snapshot(null);
        final double buttonSize = button.getWidth();
        top.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                dynamicNbCol = Math.max(1, (int)(newValue.intValue()/buttonSize) );
                if(toolPerRow.get()==-1) updateGrid();
            }
        });

    }


    /**
     * Live list of tools displayed.
     * @return list of tools SPI
     */
    public ObservableList<EditionTool.Spi> getTools() {
        return tools;
    }

    /**
     * Number of tools on each row.
     * @return number of tools per line property
     */
    public IntegerProperty getToolPerRow() {
        return toolPerRow;
    }

    private void handle(ActionEvent event){
        final ToggleButton button = (ToggleButton) event.getSource();
        if(button.isSelected()){
            final EditionTool tool = (EditionTool) button.getUserData();
            this.map.setHandler(tool);
        }
    }

    private void updateGrid(){
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        
        final ColumnConstraints colFirst = new ColumnConstraints(1, 1, Double.MAX_VALUE,Priority.ALWAYS,HPos.CENTER,true);
        final ColumnConstraints colLast = new ColumnConstraints(1, 1, Double.MAX_VALUE,Priority.ALWAYS,HPos.CENTER,true);

        final MapLayer layer = combo.getValue();

        int nbCol = toolPerRow.intValue();
        if(nbCol==-1) nbCol = dynamicNbCol;

        final FilteredList<EditionTool.Spi> validTools = tools.filtered((EditionTool.Spi t) -> t.canHandle(layer));
        final int nbRow = (int)Math.max(1, Math.ceil((float)validTools.size()/nbCol));

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
                button.setMaxHeight(Double.MAX_VALUE);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                button.setAlignment(Pos.CENTER);

                if(toolIdx<validTools.size()){
                    final EditionTool tool = validTools.get(toolIdx).create(map,layer);
                    button.setGraphic(new ImageView(tool.getSpi().getIcon()));
                    final InternationalString title = tool.getSpi().getTitle();
                    if(title!=null){
                        button.setTooltip(new Tooltip(title.toString()));
                    }
                    button.setUserData(tool);
                    button.setOnAction(this::handle);

                }else{
                    button.setGraphic(new ImageView(GeotkFX.ICON_EMPTY));
                    button.setDisable(true);
                }

                grid.add(button, x+1, y, 1, 1);
            }
        }
    }

}
