/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.GO2Utilities;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotoolkit.map.FeatureMapLayer;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.operation.TransformException;

import static org.geotoolkit.display2d.style.GO2Utilities.*;

/**
 * GraphicJ2D for feature objects.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultGraphicFeatureJ2D extends GraphicJ2D implements ProjectedFeature {

    private final FeatureMapLayer layer;

    private final GeometryCoordinateSequenceTransformer dataToObjectiveTransformer = new GeometryCoordinateSequenceTransformer();
    private final GeometryCoordinateSequenceTransformer dataToDisplayTransformer = new GeometryCoordinateSequenceTransformer();
    private final GeometryJ2D objectiveShape = new GeometryJ2D(null);
    private final GeometryJ2D displayShape = new GeometryJ2D(null);

    private Geometry objectiveGeometry = null;
    private Geometry displayGeometry = null;
    private SimpleFeature feature = null;
    private Geometry geom = null;
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
        this.geom = GO2Utilities.getGeometry(feature, "");
        objectiveGeometry = null;
        displayGeometry = null;
        isObjectiveCalculated = false;
        isDisplayCalculated = false;
    }

    @Override
    public SimpleFeature getFeature(){
        try {
            return getCompleteFeature(getFeatureId());
        } catch (IOException ex) {
            Logger.getLogger(DefaultGraphicFeatureJ2D.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public Object getUserObject() {
        return getFeature();
    }
    
    @Override
    public Geometry getObjectiveGeometry() throws TransformException{
        //TODO decimation
        if(objectiveGeometry == null){
            objectiveGeometry = dataToObjectiveTransformer.transform(geom);
        }
        return objectiveGeometry;
    }

    @Override
    public Geometry getDisplayGeometry() throws TransformException{
        //TODO decimation
        if(displayGeometry == null){
            displayGeometry = dataToDisplayTransformer.transform(geom);
        }
        return displayGeometry;
    }

    @Override
    public Shape getObjectiveShape() throws TransformException{
        if(!isObjectiveCalculated){
            objectiveShape.setGeometry( getObjectiveGeometry() );
            isObjectiveCalculated = true;
        }
        return objectiveShape;
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

    @Override
    public Shape getDisplayShape() throws TransformException{
        if(!isDisplayCalculated){
            displayShape.setGeometry( getDisplayGeometry() );
            isDisplayCalculated = true;
        }

        return displayShape;
    }

    @Override
    public FeatureMapLayer getSource() {
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

    @Override
    public Shape getObjectiveBounds() throws TransformException {
        return getObjectiveShape().getBounds2D();
    }

    @Override
    public FeatureId getFeatureId() {
        return feature.getIdentifier();
    }

    private SimpleFeature getCompleteFeature(FeatureId id)throws IOException{
        Filter filter = FILTER_FACTORY.id(Collections.singleton(id));
        SimpleFeature feature = null;

        final FeatureCollection<SimpleFeatureType,SimpleFeature> collection = layer.getFeatureSource().getFeatures(filter);

        if(!collection.isEmpty()){
            final FeatureIterator<SimpleFeature> ite = collection.features();
            feature = ite.next();
            ite.close();
        }

        return feature;
    }

}
