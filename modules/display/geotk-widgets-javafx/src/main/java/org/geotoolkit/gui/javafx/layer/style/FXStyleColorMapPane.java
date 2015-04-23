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

package org.geotoolkit.gui.javafx.layer.style;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javax.measure.unit.NonSI;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.gui.javafx.layer.FXLayerStylePane;
import org.geotoolkit.gui.javafx.style.FXColorMap;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import static org.geotoolkit.style.StyleConstants.DEFAULT_CONTRAST_ENHANCEMENT;
import static org.geotoolkit.style.StyleConstants.DEFAULT_GEOM;
import static org.geotoolkit.style.StyleConstants.LITERAL_ONE_FLOAT;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;
import org.opengis.style.Description;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStyleColorMapPane extends FXLayerStylePane {
    
    @FXML
    private FXColorMap uiColorMap;
    @FXML
    private Button uiApply;
    
    private CoverageMapLayer layer;
    //keep track of where the symbolizer was to avoid rewriting the complete style
    private MutableRule parentRule = null;
    private int parentIndex = 0;
    
    public FXStyleColorMapPane() {
        GeotkFX.loadJRXML(this,FXStyleColorMapPane.class);
    }

    @Override
    public String getTitle() {
        return GeotkFX.getString(this,"title");
    }
    
    @Override
    public String getCategory() {
        return GeotkFX.getString(this,"category");
    }

    @FXML
    private void apply(ActionEvent event) {
        if(layer==null) return;

        final ChannelSelection selection = GO2Utilities.STYLE_FACTORY.channelSelection(
                GO2Utilities.STYLE_FACTORY.selectedChannelType(""+uiColorMap.getSelectedBand(),DEFAULT_CONTRAST_ENHANCEMENT));

        final ColorMap colorMap = uiColorMap.valueProperty().get();
        final ContrastEnhancement enchance = GO2Utilities.STYLE_FACTORY.contrastEnhancement(LITERAL_ONE_FLOAT,ContrastMethod.NONE);
        final ShadedRelief relief = GO2Utilities.STYLE_FACTORY.shadedRelief(LITERAL_ONE_FLOAT);
        final Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        final RasterSymbolizer symbol = GO2Utilities.STYLE_FACTORY.rasterSymbolizer(
                "",DEFAULT_GEOM,desc,NonSI.PIXEL,LITERAL_ONE_FLOAT,
                selection, OverlapBehavior.LATEST_ON_TOP, colorMap, enchance, relief, null);

        if(parentRule!=null){
            parentRule.symbolizers().set(parentIndex,symbol);
        }else{
            //style did not exist, add a new feature type style for it
            final MutableFeatureTypeStyle fts = GO2Utilities.STYLE_FACTORY.featureTypeStyle(symbol);
            fts.setDescription(GO2Utilities.STYLE_FACTORY.description("analyze", "analyze"));
            layer.getStyle().featureTypeStyles().add(fts);
        }

    }

    /**
     * Called by FXMLLoader after creating controller.
     */
    public void initialize(){
        //move apply button on same line as add/remove values of colormap panel
        final GridPane grid = (GridPane) uiColorMap.getChildren().get(0);
        getChildren().remove(uiApply);

        grid.add(uiApply, 4, 8);
    }
    
    @Override
    public boolean init(MapLayer candidate, Object StyleElement) {
        if(!(candidate instanceof CoverageMapLayer)) return false;       
        
        layer = (CoverageMapLayer)candidate;
        
        RasterSymbolizer rs = null;
        parentRule = null;
        parentIndex = 0;
        search:
        for(final MutableFeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
            for(MutableRule r : fts.rules()){
                for(int i=0,n=r.symbolizers().size();i<n;i++){
                    Symbolizer s = r.symbolizers().get(i);
                    if(s instanceof RasterSymbolizer){
                        rs = (RasterSymbolizer) s;
                        parentRule = r;
                        parentIndex = i;
                        break search;
                    }
                }
            }
        }
        
        uiColorMap.setLayer(layer);
        if(rs!=null){
            final ColorMap cm = rs.getColorMap();
            uiColorMap.valueProperty().set(cm);
        }
        
        return true;
    }
    
    @Override
    public MutableStyle getMutableStyle() {
        return null;
    }
    
}
