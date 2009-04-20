/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gui.swing.misc;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geotools.geometry.jts.JTS;

import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * facilities factory
 * 
 * @author Johann Sorel
 */
public class FacilitiesFactory {

    
    public MapContext[] duplicateContexts(MapContext[] contexts){
        
        if(contexts != null){
            MapContext[] copys = new MapContext[contexts.length];
            
            for(int i=0;i<contexts.length;i++){
                copys[i] = duplicateContext(contexts[i]);
            }
            
            return copys;
        }else{
            return null;
        }
        
    }
    
    public MapContext duplicateContext(MapContext context) {

        if (context != null) {
            MapContext copycontext = MapBuilder.createContext(context.getCoordinateReferenceSystem());
            copycontext.layers().addAll(duplicateLayers(context.layers()));
            copycontext.setDescription(context.getDescription());

            return copycontext;
        } else {
            return null;
        }

    }
    
    public List<MapLayer> duplicateLayers(List<MapLayer> layers){
        
        if(layers != null && !layers.isEmpty()){
            List<MapLayer> copy = new ArrayList<MapLayer>();
            
            for(MapLayer layer : layers){
                copy.add(duplicateLayer(layer));
            }
            
            return copy;
        }else{
            return Collections.emptyList();
        }
        
    }
    
    public MapLayer duplicateLayer(MapLayer layer) {

        if (layer != null) {

            //TODO fix this !
//            MapLayer copy = new DefaultMapLayer((FeatureSource<SimpleFeatureType, SimpleFeature>) layer.getFeatureSource(), layer.getStyle());
//            copy.setDescription(layer.getDescription());
//            copy.setQuery(layer.getQuery());
//            copy.setVisible(layer.isVisible());

            return null;
        } else {
            return null;
        }

    }
    

    /**
     * reproject a geometry from a CRS to another
     * @param geom
     * @param inCRS
     * @param outCRS
     * @return
     */
    public Geometry projectGeometry(Geometry geom, CoordinateReferenceSystem inCRS, CoordinateReferenceSystem outCRS) {
        MathTransform transform = null;

        if (outCRS == null) {
            outCRS = inCRS;
        }


        if (!inCRS.equals(outCRS)) {
            try {
                transform = CRS.findMathTransform(inCRS, outCRS, true);
                geom = JTS.transform(geom, transform);
            } catch (Exception ex) {
                System.out.println("Error using default layer CRS, searching for a close CRS");

                try {
                    Integer epsgId = CRS.lookupEpsgCode(outCRS, true);
                    if (epsgId != null) {
                        System.out.println("Close CRS found, will replace original CRS for convertion");
                        CoordinateReferenceSystem newCRS = CRS.decode("EPSG:" + epsgId);
                        outCRS = newCRS;
                        transform = CRS.findMathTransform(inCRS, outCRS);
                    } else {
                        System.out.println("No close CRS found, will force convert");
                        try {
                            transform = CRS.findMathTransform(inCRS, outCRS, true);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Search Error, no close CRS found, will force convertion");

                    try {
                        transform = CRS.findMathTransform(inCRS, outCRS, true);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                ex.printStackTrace();
            }
        }

        return geom;
    }
}
