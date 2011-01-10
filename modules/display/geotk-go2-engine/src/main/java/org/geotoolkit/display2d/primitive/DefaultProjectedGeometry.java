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


import com.vividsolutions.jts.geom.Geometry;

import java.awt.Shape;

import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;

import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * GraphicJ2D for feature objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultProjectedGeometry implements ProjectedGeometry {

    private final GeometryCSTransformer objToDisplayTransformer =
            new GeometryCSTransformer(new CoordinateSequenceMathTransformer(null));

    private final Geometry objectiveGeometry;
    private Geometry displayGeometry = null;
    private final JTSGeometryJ2D objectiveShape = new JTSGeometryJ2D(null);
    private final JTSGeometryJ2D displayShape = new JTSGeometryJ2D(null);
    private boolean isObjectiveCalculated = false;
    private boolean isDisplayCalculated = false;
    

    public DefaultProjectedGeometry(final Geometry objGeom){
        this.objectiveGeometry = objGeom;
    }

    public void setObjToDisplay(final MathTransform trs){
        ((CoordinateSequenceMathTransformer)objToDisplayTransformer.getCSTransformer()).setTransform(trs);
        displayGeometry = null;
        isObjectiveCalculated = false;
        isDisplayCalculated = false;
    }

    @Override
    public Geometry getObjectiveGeometryJTS() throws TransformException{
        return objectiveGeometry;
    }

    @Override
    public Geometry getDisplayGeometryJTS() throws TransformException{
        //TODO decimation
        if(displayGeometry == null){
            displayGeometry = objToDisplayTransformer.transform(objectiveGeometry);
        }
        return displayGeometry;
    }

    @Override
    public Shape getObjectiveShape() throws TransformException{
        if(!isObjectiveCalculated){
            objectiveShape.setGeometry( getObjectiveGeometryJTS() );
            isObjectiveCalculated = true;
        }
        return objectiveShape;
    }

    @Override
    public Shape getDisplayShape() throws TransformException{
        if(!isDisplayCalculated){
            displayShape.setGeometry( getDisplayGeometryJTS() );
            isDisplayCalculated = true;
        }

        return displayShape;
    }

    @Override
    public org.opengis.geometry.Geometry getObjectiveGeometry() throws TransformException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public org.opengis.geometry.Geometry getDisplayGeometry() throws TransformException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
