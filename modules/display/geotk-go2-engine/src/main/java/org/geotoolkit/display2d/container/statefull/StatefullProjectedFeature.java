/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.container.statefull;

import com.vividsolutions.jts.geom.Geometry;

import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.map.FeatureMapLayer;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.identity.FeatureId;

/**
 * Not thread safe.
 * Use it knowing you make clear cache operation in a syncrhonize way.
 * GraphicJ2D for feature objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullProjectedFeature implements ProjectedFeature,Graphic {

    private static final String DEFAULT_GEOM = "";

    private final StatefullContextParams params;
    private final Map<String,StatefullProjectedGeometry> geometries = new HashMap<String,StatefullProjectedGeometry>();
    private SimpleFeature feature;


    public StatefullProjectedFeature(StatefullContextParams params){
        this(params,null);
    }

    public StatefullProjectedFeature(StatefullContextParams params, SimpleFeature feature){
        this.params = params;
        this.feature = feature;
    }

    public void setFeature(SimpleFeature feature) {
        //we dont test if it is the same feature or not, even
        //if it's the same feature, it might have change so we clear the cache anyway.
        clearDataCache();
        this.feature = feature;
    }

    public void clearDataCache(){
        for(StatefullProjectedGeometry sg : geometries.values()){
            sg.setDataGeometry(null);
        }
    }

    public void clearObjectiveCache(){
        for(StatefullProjectedGeometry geom : geometries.values()){
            geom.clearObjectiveCache();
        }
    }
    
    public void clearDisplayCache(){
        for(StatefullProjectedGeometry geom : geometries.values()){
            geom.clearDisplayCache();
        }
    }

    @Override
    public ProjectedGeometry getGeometry(String name) {
        if(name == null) name = DEFAULT_GEOM;

        StatefullProjectedGeometry proj = geometries.get(name);
        if(proj == null){
            Geometry geom = GO2Utilities.getGeometry(feature, name);
            if(geom != null){
                StatefullProjectedGeometry projectedGeom = new StatefullProjectedGeometry(params, geom);
                geometries.put(name, projectedGeom);
                return projectedGeom;
            }
        }else{
            //check that the geometry is set
            if(proj.getDataGeometry() == null){
                proj.setDataGeometry(GO2Utilities.getGeometry(feature, name));
            }
        }
        return proj;
    }

    @Override
    public SimpleFeature getFeature(){
        return feature;
    }

    @Override
    public FeatureMapLayer getFeatureLayer() {
        return params.layer;
    }

    @Override
    public FeatureId getFeatureId() {
        return feature.getIdentifier();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public ReferencedCanvas2D getCanvas() {
        return params.canvas;
    }

}
