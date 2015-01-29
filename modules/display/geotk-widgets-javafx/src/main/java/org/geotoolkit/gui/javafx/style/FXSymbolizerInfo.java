/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2015, Geomatys
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
package org.geotoolkit.gui.javafx.style;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXSymbolizerInfo extends GridPane{

    protected MapLayer layer = null;
    protected volatile boolean updating = false;

    private final SimpleBooleanProperty value = new SimpleBooleanProperty();

    @FXML private TextField uiName;
    @FXML private TextField uititle;
    @FXML private TextField uiAbstract;
    @FXML private ChoiceBox<Unit> uiUnit;
    @FXML private FXTextExpression uiGeom;


    public FXSymbolizerInfo() {
        GeotkFX.loadJRXML(this);
    }

    /**
     *
     * @return fake property, just to have events
     */
    public SimpleBooleanProperty valueProperty(){
        return value;
    }

    public String getName(){
        return uiName.getText();
    }

    public Description getDescription(){
        return GO2Utilities.STYLE_FACTORY.description(uititle.getText(), uiAbstract.getText());
    }

    public Unit getUnit(){
        return uiUnit.getValue();
    }

    public Expression getGeom(){
        return uiGeom.valueProperty().get();
    }

    public void initialize() {
        uiUnit.setItems(FXCollections.observableArrayList(NonSI.PIXEL,SI.METRE,NonSI.INCH,NonSI.MILE,NonSI.FOOT));
        uiUnit.getSelectionModel().select(0);
        uiUnit.setConverter(new StringConverter<Unit>() {

            @Override
            public String toString(Unit object) {
                if(object == NonSI.PIXEL) return "pixel";
                else if(object == SI.METRE) return "metre";
                else if(object == NonSI.INCH) return "inch";
                else if(object == NonSI.MILE) return "mile";
                else if(object == NonSI.FOOT) return "foot";
                else return "pixel";
            }

            @Override
            public Unit fromString(String string) {
                if("pixel".equals(string)) return NonSI.PIXEL;
                else if("metre".equals(string)) return SI.METRE;
                else if("inch".equals(string)) return NonSI.INCH;
                else if("mile".equals(string)) return NonSI.MILE;
                else if("foot".equals(string)) return NonSI.FOOT;
                else return NonSI.PIXEL;
            }
        });

        //catch change events
        final EventHandler<ActionEvent> eventHandler = (ActionEvent event) -> {
            if(updating) return;
            value.setValue(!value.get());
        };
        uiName.setOnAction(eventHandler);
        uititle.setOnAction(eventHandler);
        uiAbstract.setOnAction(eventHandler);

        final ChangeListener changeListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(updating) return;
                value.setValue(!value.get());
            }
        };

        uiUnit.valueProperty().addListener(changeListener);
        uiGeom.valueProperty().addListener(changeListener);

    }

    public MapLayer getLayer() {
        return layer;
    }

    public void setLayer(MapLayer layer) {
        this.layer = layer;
        uiGeom.setLayer(layer);
    }

    public void parse(Symbolizer styleElement) {
        updating = true;

        uiName.setText(notNull(styleElement.getName()));
        final Description desc = styleElement.getDescription();
        if(desc!=null){
            uititle.setText(notNull(desc.getTitle()));
            uiAbstract.setText(notNull(desc.getAbstract()));
        }else{
            uititle.setText("");
            uiAbstract.setText("");
        }
        final Unit<Length> uom = styleElement.getUnitOfMeasure();
        uiUnit.setValue(uom==null? NonSI.PIXEL : uom);
        uiGeom.valueProperty().set(styleElement.getGeometry());

        updating = false;
    }

    private static String notNull(CharSequence str){
        return (str==null) ? "" : str.toString();
    }

}
