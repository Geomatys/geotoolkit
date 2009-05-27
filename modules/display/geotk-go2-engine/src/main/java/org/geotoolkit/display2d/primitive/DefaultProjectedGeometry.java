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
import java.awt.Shape;

import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;

import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * GraphicJ2D for feature objects.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultProjectedGeometry implements ProjectedGeometry {

    private final GeometryCoordinateSequenceTransformer objToDisplayTransformer = new GeometryCoordinateSequenceTransformer();

    private final Geometry objectiveGeometry;
    private Geometry displayGeometry = null;
    private final GeometryJ2D objectiveShape = new GeometryJ2D(null);
    private final GeometryJ2D displayShape = new GeometryJ2D(null);
    private boolean isObjectiveCalculated = false;
    private boolean isDisplayCalculated = false;
    

    public DefaultProjectedGeometry(Geometry objGeom){
        this.objectiveGeometry = objGeom;
    }

    public void setObjToDisplay(MathTransform trs){
        objToDisplayTransformer.setMathTransform(trs);
        displayGeometry = null;
        isObjectiveCalculated = false;
        isDisplayCalculated = false;
    }

    @Override
    public Geometry getObjectiveGeometry() throws TransformException{
        return objectiveGeometry;
    }

    @Override
    public Geometry getDisplayGeometry() throws TransformException{
        //TODO decimation
        if(displayGeometry == null){
            displayGeometry = objToDisplayTransformer.transform(objectiveGeometry);
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
    public Shape getObjectiveBounds() throws TransformException {
        return getObjectiveShape().getBounds2D();
    }

    @Override
    public Shape getDisplayBounds() throws TransformException {
        return getDisplayShape().getBounds2D();
    }

    @Override
    public org.opengis.geometry.Geometry getObjectiveGeometryISO() throws TransformException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public org.opengis.geometry.Geometry getDisplayGeometryISO() throws TransformException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
