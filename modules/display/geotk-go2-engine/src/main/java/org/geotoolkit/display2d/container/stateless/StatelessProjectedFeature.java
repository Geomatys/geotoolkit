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

package org.geotoolkit.display2d.container.stateless;

import org.geotoolkit.display2d.primitive.*;
import com.vividsolutions.jts.geom.Geometry;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.GO2Utilities;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotoolkit.map.FeatureMapLayer;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class StatelessProjectedFeature extends GraphicJ2D implements ProjectedFeature{

    private final GeometryCoordinateSequenceTransformer dataToObjectiveTransformer = new GeometryCoordinateSequenceTransformer();
    private final GeometryCoordinateSequenceTransformer dataToDisplayTransformer = new GeometryCoordinateSequenceTransformer();
    private final GeometryJ2D objectiveShape = new GeometryJ2D(null);
    private final GeometryJ2D displayShape = new GeometryJ2D(null);
    
    private Geometry objectiveGeometry = null;
    private Geometry displayGeometry = null;
    private SimpleFeature feature = null;
    private Geometry geom = null;
    private boolean isObjectiveCalculated = false;
    private boolean isDisplayCalculated = false;

    public StatelessProjectedFeature(ReferencedCanvas2D canvas, CoordinateReferenceSystem crs){
        super(canvas,crs);
    }

    public void initContext(MathTransform dataToDisplay, MathTransform dataToObjective){
        dataToObjectiveTransformer.setMathTransform(dataToObjective);
        dataToDisplayTransformer.setMathTransform(dataToDisplay);
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
        return feature;
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
        return null;
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
    public Rectangle2D getObjectiveBounds() throws TransformException {
        return getObjectiveShape().getBounds2D();
    }

    @Override
    public FeatureId getFeatureId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
