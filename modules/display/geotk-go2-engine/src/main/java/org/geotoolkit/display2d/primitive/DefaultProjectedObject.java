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

import com.vividsolutions.jts.geom.Geometry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.container.statefull.StatefullContextParams;
import org.geotoolkit.display2d.container.statefull.StatefullProjectedGeometry;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * Not thread safe.
 * Use it knowing you make clear cache operation in a synchronize way.
 * GraphicJ2D for custom objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultProjectedObject<T> implements ProjectedObject {

    protected static final String DEFAULT_GEOM = "";

    protected final StatefullContextParams params;
    protected final Map<String,StatefullProjectedGeometry> geometries =
            new LinkedHashMap<String,StatefullProjectedGeometry>(); //linked hashmap is faster than hashmap on iteration.
    protected T candidate;


    public DefaultProjectedObject(final StatefullContextParams params){
        this(params,null);
    }

    public DefaultProjectedObject(final StatefullContextParams params, final T candidate){
        this.params = params;
        this.candidate = candidate;
    }

    public void setCandidate(final T candidate) {
        //we dont test if it is the same object or not, even
        //if it's the same object, it might have change so we clear the cache anyway.
        clearDataCache();
        this.candidate = candidate;
    }

    public void clearDataCache(){
        for(StatefullProjectedGeometry sg : geometries.values()){
            sg.setObjectiveGeometry(null);
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
            final Geometry geom = getGeometryObjective(name);
            proj = new StatefullProjectedGeometry(params, Geometry.class, geom);
            geometries.put(name, proj);
        }else{
            //check that the geometry is set
            if(proj.getObjectiveGeometryJTS() == null){
                proj.setObjectiveGeometry(getGeometryObjective(name));
            }
        }
        return proj;
    }

    /**
     * Returns the geometry is objective crs.
     * @param name
     * @return Geometry
     */
    private Geometry getGeometryObjective(final String name){
        Geometry geom = GO2Utilities.getGeometry(candidate, name);

        if(geom == null){
            return null;
        }

        //we don't know in which crs it is, try to find it
        CoordinateReferenceSystem crs = null;
        try{
            crs = JTS.findCoordinateReferenceSystem(geom);
        }catch(IllegalArgumentException ex){
            params.context.getMonitor().exceptionOccured(ex, Level.FINE);
        }catch(NoSuchAuthorityCodeException ex){
            params.context.getMonitor().exceptionOccured(ex, Level.FINE);
        }catch(FactoryException ex){
            params.context.getMonitor().exceptionOccured(ex, Level.FINE);
        }
        
        //if we don't know the crs, we will assume it's the objective crs already
        if(crs != null){
            //reproject in objective crs if needed
            if(!CRS.equalsIgnoreMetadata(params.objectiveCRS,crs)){
                try {
                    geom = JTS.transform(geom, CRS.findMathTransform(crs, params.objectiveCRS));
                } catch (MismatchedDimensionException ex) {
                    params.context.getMonitor().exceptionOccured(ex, Level.WARNING);
                } catch (TransformException ex) {
                    params.context.getMonitor().exceptionOccured(ex, Level.WARNING);
                } catch (FactoryException ex) {
                    params.context.getMonitor().exceptionOccured(ex, Level.WARNING);
                }
            }
        }

        return geom;
    }


    @Override
    public T getCandidate(){
        return candidate;
    }

    @Override
    public MapLayer getLayer() {
        return params.layer;
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

    @Override
    public ReferencedCanvas2D getCanvas() {
        return params.canvas;
    }

}
