/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Geometry;
import org.opengis.util.FactoryException;
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

    private final GeometryCSTransformer dataToObjectiveTransformer =
            new GeometryCSTransformer(new CoordinateSequenceMathTransformer(null));
    private final GeometryCSTransformer dataToDisplayTransformer =
            new GeometryCSTransformer(new CoordinateSequenceMathTransformer(null));
    private final JTSGeometryJ2D objectiveShape = new JTSGeometryJ2D(null);
    private final JTSGeometryJ2D displayShape = new JTSGeometryJ2D(null);

    private com.vividsolutions.jts.geom.Geometry defaultGeom = null;
    private com.vividsolutions.jts.geom.Geometry objectiveGeometry = null;
    private com.vividsolutions.jts.geom.Geometry displayGeometry = null;
    private Geometry objectiveGeometryISO = null;
    private Geometry displayGeometryISO = null;

    private Feature feature = null;
    private Rectangle dispBounds = null;
    private boolean isObjectiveCalculated = false;
    private boolean isDisplayCalculated = false;

    
    public DefaultGraphicFeatureJ2D(final J2DCanvas canvas, final FeatureMapLayer layer, final Feature feature){
        super(canvas,feature.getType().getCoordinateReferenceSystem());
        this.layer = layer;
        initFeature(feature);
    }
    
    public void initFeature(final Feature feature){
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
    public Feature getFeature(){
        try {
            return getCompleteFeature(getFeatureId());
        } catch (DataStoreException ex) {
            Logging.getLogger(DefaultGraphicFeatureJ2D.class).log(Level.WARNING, null, ex);
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
                ((CoordinateSequenceMathTransformer)dataToObjectiveTransformer.getCSTransformer())
                        .setTransform(CRS.findMathTransform(dataCRS, objectiveCRS,true));
            } catch (FactoryException ex) {
                getLogger().log(Level.WARNING, "", ex);
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
                ((CoordinateSequenceMathTransformer)dataToDisplayTransformer.getCSTransformer())
                        .setTransform(CRS.findMathTransform(dataCRS, displayCRS,true));
            } catch (FactoryException ex) {
                getLogger().log(Level.WARNING, "", ex);
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
    public void paint(final RenderingContext2D context) {
    }

    @Override
    public List<Graphic> getGraphicAt(final RenderingContext context, final SearchArea mask,
            final VisitFilter filter, final List<Graphic> graphics) {
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
                getLogger().log(Level.WARNING, "", ex);
            }
        }
        return dispBounds;
    }

    private Feature getCompleteFeature(final FeatureId id)throws DataStoreException{

        if(layer != null){
            Filter filter = FILTER_FACTORY.id(Collections.singleton(id));

            Feature feature = null;

            final FeatureCollection<? extends Feature> collection =
                    layer.getCollection().subCollection(
                    QueryBuilder.filtered(layer.getCollection().getFeatureType().getName(), filter));

            if(!collection.isEmpty()){
                final FeatureIterator<? extends Feature> ite = collection.iterator();
                if(ite.hasNext()){
                    feature = ite.next();
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
    public ProjectedGeometry getGeometry(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
