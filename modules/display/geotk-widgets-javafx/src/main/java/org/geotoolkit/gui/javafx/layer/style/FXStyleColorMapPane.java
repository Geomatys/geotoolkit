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

package org.geotoolkit.gui.javafx.layer.style;

import javafx.fxml.FXML;
import org.geotoolkit.gui.javafx.layer.FXLayerStylePane;
import org.geotoolkit.gui.javafx.style.FXColorMap;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.opengis.style.ColorMap;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStyleColorMapPane extends FXLayerStylePane {
    
    @FXML
    private FXColorMap uiColorMap;
    
    private CoverageMapLayer layer;
    //keep track of where the symbolizer was to avoid rewriting the complete style
    private MutableRule parentRule = null;
    private int parentIndex = 0;
    
    public FXStyleColorMapPane() {
        GeotkFX.loadJRXML(this);
    }

    @Override
    public String getTitle() {
        return GeotkFX.getString(this,"title");
    }
    
    @Override
    public String getCategory() {
        return GeotkFX.getString(this,"category");
    }
    
    /**
     * Called by FXMLLoader after creating controller.
     */
    public void initialize(){
    }
    
    @Override
    public boolean init(Object candidate) {
        if(!(candidate instanceof CoverageMapLayer)) return false;       
        
        layer = (CoverageMapLayer)candidate;
        
//        RasterSymbolizer rs = null;
//        parentRule = null;
//        parentIndex = 0;
//        search:
//        for(final MutableFeatureTypeStyle fts : layer.getStyle().featureTypeStyles()){
//            for(MutableRule r : fts.rules()){
//                for(int i=0,n=r.symbolizers().size();i<n;i++){
//                    Symbolizer s = r.symbolizers().get(i);
//                    if(s instanceof RasterSymbolizer){
//                        rs = (RasterSymbolizer) s;
//                        parentRule = r;
//                        parentIndex = i;
//                        break search;
//                    }
//                }
//            }
//        }
//        
//        uiColorMap.setLayer(layer);
//        if(rs!=null){
//            final ColorMap cm = rs.getColorMap();
//            uiColorMap.valueProperty().set(cm);
//        }
        
        return true;
    }
    
    @Override
    public MutableStyle getMutableStyle() {
        return null;
    }
    
}
