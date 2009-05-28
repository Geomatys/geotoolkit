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

import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.map.FeatureMapLayer;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.operation.TransformException;

/**
 * GraphicJ2D for feature objects.
 *
 * @author Johann Sorel (Geomatys)
 */
public class StatefullProjectedFeature implements ProjectedFeature,Graphic {

    private final StatefullContextParams params;
    private SimpleFeature feature;
    private com.vividsolutions.jts.geom.Geometry geom = null;

    private com.vividsolutions.jts.geom.Geometry objectiveGeometryJTS = null;
    private Geometry    objectiveGeometryISO = null;
    private Shape       objectiveShape = null;
    private Rectangle2D objectiveBounds = null;

    private com.vividsolutions.jts.geom.Geometry displayGeometryJTS = null;
    private Geometry    displayGeometryISO = null;
    private Shape       displayShape = null;
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
        objectiveGeometryISO = null;
        objectiveGeometryJTS = null;
        objectiveShape = null;
        objectiveBounds = null;
    }
    
    public synchronized void clearDisplayCache(){
        displayGeometryISO = null;
        displayGeometryJTS = null;
        displayShape = null;
        displayBounds = null;
    }

    private com.vividsolutions.jts.geom.Geometry getGeometry(){
        if(geom == null){
            geom = GO2Utilities.getGeometry(feature, "");
            if(params.decimate && params.decimation != 0){
                geom = DouglasPeuckerSimplifier.simplify(geom, params.decimation);
            }
        }
        return geom;
    }

    @Override
    public com.vividsolutions.jts.geom.Geometry getObjectiveGeometry() throws TransformException{
        if(objectiveGeometryJTS == null){
            objectiveGeometryJTS = params.dataToObjectiveTransformer.transform(getGeometry());
        }
        return objectiveGeometryJTS;
    }

    @Override
    public com.vividsolutions.jts.geom.Geometry getDisplayGeometry() throws TransformException{
        if(displayGeometryJTS == null){
            displayGeometryJTS = params.dataToDisplayTransformer.transform(getGeometry());
        }
        return displayGeometryJTS;
    }

    @Override
    public Shape getObjectiveShape() throws TransformException{
        if(objectiveShape == null){
            objectiveShape = GO2Utilities.toJava2D(getObjectiveGeometryISO());
//            objectiveShape = GO2Utilities.toJava2D(getObjectiveGeometry());
        }
        return objectiveShape;
    }

    @Override
    public Shape getDisplayShape() throws TransformException{
        if(displayShape == null){
            displayShape = GO2Utilities.toJava2D(getDisplayGeometryISO());
//            displayShape = GO2Utilities.toJava2D(getDisplayGeometry());
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
    public Geometry getObjectiveGeometryISO() throws TransformException {
        if(objectiveGeometryISO == null){
            objectiveGeometryISO = JTSUtils.toISO(getObjectiveGeometry(), params.objectiveCRS);
        }
        return objectiveGeometryISO;
    }

    @Override
    public Geometry getDisplayGeometryISO() throws TransformException {
        if(displayGeometryISO == null){
            displayGeometryISO = JTSUtils.toISO(getDisplayGeometry(), params.displayCRS);
        }
        return displayGeometryISO;
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
