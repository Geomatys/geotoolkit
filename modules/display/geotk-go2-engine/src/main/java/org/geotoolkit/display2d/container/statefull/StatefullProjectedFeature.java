/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2009, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.display2d.container.statefull;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.display2d.primitive.GeometryJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.style.GO2Utilities;
import org.geotoolkit.map.FeatureMapLayer;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.operation.TransformException;

/**
 * GraphicJ2D for feature objects.
 *
 * @author Johann Sorel (Geomatys)
 */
public class StatefullProjectedFeature implements ProjectedFeature,Graphic {

    private final StatefullContextParams params;
    private SimpleFeature feature;
    private Geometry geom = null;

    private Geometry    objectiveGeometry = null;
    private GeometryJ2D objectiveShape = null;
    private Rectangle2D objectiveBounds = null;

    private Geometry    displayGeometry = null;
    private GeometryJ2D displayShape = null;
    private Rectangle   displayBounds = null;


    public StatefullProjectedFeature(StatefullContextParams params){
        this(params,null);
    }

    public StatefullProjectedFeature(StatefullContextParams params, SimpleFeature feature){
        this.params = params;
        this.feature = feature;
    }

    public synchronized void setFeature(SimpleFeature feature) {
        if(this.feature != feature){
            clearDataCache();
        }
        this.feature = feature;
    }

    public synchronized void clearDataCache(){
        clearObjectiveCache();
        this.geom = null;
    }

    public synchronized void clearObjectiveCache(){
        clearDisplayCache();
        objectiveGeometry = null;
        objectiveShape = null;
        objectiveBounds = null;
    }
    
    public synchronized void clearDisplayCache(){
        displayGeometry = null;
        displayShape = null;
        displayBounds = null;
    }

    private Geometry getGeometry(){
        if(geom == null){
            geom = GO2Utilities.getGeometry(feature, "");
            if(params.decimate && params.decimation != 0){
                geom = DouglasPeuckerSimplifier.simplify(geom, params.decimation);
            }
        }
        return geom;
    }

    @Override
    public Geometry getObjectiveGeometry() throws TransformException{
        if(objectiveGeometry == null){
            objectiveGeometry = params.dataToObjectiveTransformer.transform(getGeometry());
        }
        return objectiveGeometry;
    }

    @Override
    public Geometry getDisplayGeometry() throws TransformException{
        if(displayGeometry == null){
            displayGeometry = params.dataToDisplayTransformer.transform(getGeometry());
        }
        return displayGeometry;
    }

    @Override
    public Shape getObjectiveShape() throws TransformException{
        if(objectiveShape == null){
            objectiveShape = new GeometryJ2D(null);
            objectiveShape.setGeometry(getObjectiveGeometry());
        }
        return objectiveShape;
    }

    @Override
    public Shape getDisplayShape() throws TransformException{
        if(displayShape == null){
            displayShape = new GeometryJ2D(null);
            displayShape.setGeometry(getDisplayGeometry());
        }
        return displayShape;
    }

    @Override
    public Rectangle2D getObjectiveBounds() throws TransformException{
        if(objectiveBounds == null){
            objectiveBounds = getObjectiveShape().getBounds2D();
        }
        return objectiveBounds;
    }

    @Override
    public Rectangle getDisplayBounds() throws TransformException{
        if(displayBounds == null){
            displayBounds = getDisplayShape().getBounds();
        }
        return displayBounds;
    }

    @Override
    public SimpleFeature getFeature(){
        return feature;
    }

    @Override
    public FeatureMapLayer getSource() {
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
    public void setVisible(boolean arg0) {
    }

    @Override
    public void dispose() {
    }

}
