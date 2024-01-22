/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.display2d.primitive;

import java.util.LinkedHashMap;
import java.util.Map;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.apache.sis.map.MapLayer;
import org.opengis.filter.Expression;


/**
 * Not thread safe.
 * Use it knowing you make clear cache operation in a synchronize way.
 * GraphicJ2D for custom objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultProjectedObject<T> implements ProjectedObject {

    protected static final Expression DEFAULT_GEOM = null;

    protected final RenderingContext2D params;
    protected final Map<Expression,ProjectedGeometry> geometries =
            new LinkedHashMap<>(); //linked hashmap is faster than hashmap on iteration.
    protected T candidate;
    protected MapLayer layer;


    public DefaultProjectedObject(final RenderingContext2D params){
        this(params,null);
    }

    public DefaultProjectedObject(final RenderingContext2D params, final T candidate){
        this.params = params;
        this.candidate = candidate;
    }

    public RenderingContext2D getParameters() {
        return params;
    }

    public void setCandidate(final T candidate) {
        //we dont test if it is the same object or not, even
        //if it's the same object, it might have change so we clear the cache anyway.
        clearDataCache();
        this.candidate = candidate;
    }

    public void clearDataCache(){
        for(ProjectedGeometry sg : geometries.values()){
            sg.clearAll();
        }
    }

    public void clearObjectiveCache(){
        for(ProjectedGeometry geom : geometries.values()){
            geom.clearObjectiveCache();
        }
    }

    public void clearDisplayCache(){
        for(ProjectedGeometry geom : geometries.values()){
            geom.clearDisplayCache();
        }
    }

    @Override
    public ProjectedGeometry getGeometry(Expression exp) {
        if(exp == null) exp = DEFAULT_GEOM;

        ProjectedGeometry proj = geometries.get(exp);
        if(proj == null){
            proj = new ProjectedGeometry(params);
            geometries.put(exp, proj);
        }

        //check that the geometry is set
        if(!proj.isSet()){
            proj.setDataGeometry(GO2Utilities.getGeometry(candidate, exp),null);
        }

        return proj;
    }

    @Override
    public T getCandidate(){
        return candidate;
    }

    @Override
    public MapLayer getLayer() {
        return layer;
    }

    public void setLayer(MapLayer layer) {
        this.layer = layer;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setVisible(final boolean visible) {
    }

    @Override
    public void dispose() {
    }

}
