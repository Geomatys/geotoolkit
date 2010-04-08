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
package org.geotoolkit.display2d.primitive;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.geotoolkit.storage.DataStoreException;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import static org.geotoolkit.display2d.GO2Utilities.*;

/**
 * GraphicJ2D for feature objects. This object is valid only for the time of a portraying
 * operation. The objective and display crs may be obsolete if use a second time.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultGraphicFeatureJ2D extends AbstractGraphicJ2D implements ProjectedFeature {

    private final FeatureMapLayer layer;

    private final GeometryCoordinateSequenceTransformer dataToObjectiveTransformer = new GeometryCoordinateSequenceTransformer();
    private final GeometryCoordinateSequenceTransformer dataToDisplayTransformer = new GeometryCoordinateSequenceTransformer();
    private final JTSGeometryJ2D objectiveShape = new JTSGeometryJ2D(null);
    private final JTSGeometryJ2D displayShape = new JTSGeometryJ2D(null);

    private com.vividsolutions.jts.geom.Geometry defaultGeom = null;
    private com.vividsolutions.jts.geom.Geometry objectiveGeometry = null;
    private com.vividsolutions.jts.geom.Geometry displayGeometry = null;
    private Geometry objectiveGeometryISO = null;
    private Geometry displayGeometryISO = null;

    private SimpleFeature feature = null;
    private Rectangle dispBounds = null;
    private boolean isObjectiveCalculated = false;
    private boolean isDisplayCalculated = false;

    
    public DefaultGraphicFeatureJ2D(ReferencedCanvas2D canvas, FeatureMapLayer layer, SimpleFeature feature){
        super(canvas,feature.getType().getCoordinateReferenceSystem());
        this.layer = layer;
        initFeature(feature);
    }
    
    public void initFeature(SimpleFeature feature){
        this.feature = feature;
        this.defaultGeom = GO2Utilities.getGeometry(feature, "");
        objectiveGeometry = null;
        displayGeometry = null;
        objectiveGeometryISO = null;
        displayGeometryISO = null;
        isObjectiveCalculated = false;
        isDisplayCalculated = false;
    }

    @Override
    public SimpleFeature getFeature(){
        try {
            return getCompleteFeature(getFeatureId());
        } catch (DataStoreException ex) {
            Logging.getLogger(DefaultGraphicFeatureJ2D.class).log(Level.SEVERE, null, ex);
        }

        //worst case, return the partial feature
        return feature;
    }

    @Override
    public Object getUserObject() {
        return getFeature();
    }
    
    public com.vividsolutions.jts.geom.Geometry getObjectiveGeometry() throws TransformException{
        //TODO decimation
        if(objectiveGeometry == null){

            CoordinateReferenceSystem dataCRS = feature.getType().getCoordinateReferenceSystem();
            CoordinateReferenceSystem objectiveCRS = getCanvas().getObjectiveCRS();

            try {
                dataToObjectiveTransformer.setMathTransform(CRS.findMathTransform(dataCRS, objectiveCRS,true));
            } catch (FactoryException ex) {
                ex.printStackTrace();
            }

            objectiveGeometry = dataToObjectiveTransformer.transform(defaultGeom);
        }
        return objectiveGeometry;
    }

    public com.vividsolutions.jts.geom.Geometry getDisplayGeometry() throws TransformException{
        //TODO decimation
        if(displayGeometry == null){

            CoordinateReferenceSystem dataCRS = feature.getType().getCoordinateReferenceSystem();
            CoordinateReferenceSystem displayCRS = getCanvas().getDisplayCRS();

            try {
                dataToDisplayTransformer.setMathTransform(CRS.findMathTransform(dataCRS, displayCRS,true));
            } catch (FactoryException ex) {
                ex.printStackTrace();
            }

            displayGeometry = dataToDisplayTransformer.transform(defaultGeom);
        }
        return displayGeometry;
    }

    public Shape getObjectiveShape() throws TransformException{
        if(!isObjectiveCalculated){
            objectiveShape.setGeometry( getObjectiveGeometry() );
            isObjectiveCalculated = true;
        }
        return objectiveShape;
    }

    public Shape getDisplayShape() throws TransformException{
        if(!isDisplayCalculated){
            displayShape.setGeometry( getDisplayGeometry() );
            isDisplayCalculated = true;
        }

        return displayShape;
    }

    @Override
    public FeatureMapLayer getFeatureLayer() {
        return layer;
    }

    @Override
    public void paint(RenderingContext2D context) {
    }

    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask,
            VisitFilter filter, List<Graphic> graphics) {
        return graphics;
    }

    public Shape getObjectiveBounds() throws TransformException {
        return getObjectiveShape().getBounds2D();
    }

    @Override
    public FeatureId getFeatureId() {
        return feature.getIdentifier();
    }

    public org.opengis.geometry.Geometry getObjectiveGeometryISO() throws TransformException {
        if(objectiveGeometryISO == null){
            objectiveGeometryISO = JTSUtils.toISO(getObjectiveGeometry(), getCanvas().getObjectiveCRS());
        }
        return objectiveGeometryISO;
    }

    public org.opengis.geometry.Geometry getDisplayGeometryISO() throws TransformException {
        if(displayGeometryISO == null){
            displayGeometryISO = JTSUtils.toISO(getDisplayGeometry(), getCanvas().getDisplayCRS());
        }
        return displayGeometryISO;
    }

    public Rectangle getDispBounds(){
        if(dispBounds == null){
            try {
                Rectangle2D rect = getDisplayShape().getBounds2D();
                dispBounds = rect.getBounds();
            } catch (TransformException ex) {
                ex.printStackTrace();
            }
        }
        return dispBounds;
    }

    private SimpleFeature getCompleteFeature(FeatureId id)throws DataStoreException{

        if(layer != null){
            Filter filter = FILTER_FACTORY.id(Collections.singleton(id));

            SimpleFeature feature = null;

            final FeatureCollection<? extends Feature> collection =
                    layer.getCollection().subCollection(
                    QueryBuilder.filtered(layer.getCollection().getFeatureType().getName(), filter));

            if(!collection.isEmpty()){
                final FeatureIterator<? extends Feature> ite = collection.iterator();
                if(ite.hasNext()){
                    feature = (SimpleFeature) ite.next();
                }
                ite.close();
            }

            if(feature == null){
                //worst case, return the partial feature
                return this.feature;
            }

            return feature;
        }else{
            //worst case, return the partial feature
            return feature;
        }
    }

    @Override
    public ProjectedGeometry getGeometry(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
