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
package org.geotoolkit.display2d.container.fx;

import javafx.scene.Group;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.style.CachedLineSymbolizer;
import org.geotoolkit.display2d.style.CachedPointSymbolizer;
import org.geotoolkit.display2d.style.CachedPolygonSymbolizer;
import org.geotoolkit.display2d.style.CachedRasterSymbolizer;
import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.CachedTextSymbolizer;
import org.opengis.feature.Feature;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXFeature extends Group {
    
    private static final CachedSymbolizer[] EMPTY = new CachedSymbolizer[0];
    
    final FXRenderingContext context;
    final Feature feature;
    private CachedSymbolizer[] symbolizers = EMPTY;

    public FXFeature(FXRenderingContext context, Feature feature) {
        setCache(false);
        this.context = context;
        this.feature = feature;
    }

    public Feature getFeature() {
        return feature;
    }

    public CachedSymbolizer[] getSymbolizers() {
        return symbolizers;
    }

    public void setSymbolizers(Symbolizer ... symbolizers) {
        final CachedSymbolizer[] css = new CachedSymbolizer[symbolizers.length];
        for(int i=0;i<symbolizers.length;i++){
            css[i] = GO2Utilities.getCached(symbolizers[i], feature.getType());
        }
        setSymbolizers(css);
    }
    
    public void setSymbolizers(CachedSymbolizer ... symbolizers) {
        this.symbolizers = symbolizers;
        updateGraphic();
    }
    
    private void updateGraphic(){
        getChildren().clear();
        
        if(feature==null || symbolizers.length==0) return;
                
        for(CachedSymbolizer s : symbolizers){
            if(s==null) continue;
            
            if(s instanceof CachedPointSymbolizer){
                
            }else if(s instanceof CachedLineSymbolizer){
                getChildren().add(new FXLineSymbolizer(this, (CachedLineSymbolizer) s));
            }else if(s instanceof CachedPolygonSymbolizer){
                
            }else if(s instanceof CachedTextSymbolizer){
                
            }else if(s instanceof CachedRasterSymbolizer){
                
            }
        }
        
    }
    
}
