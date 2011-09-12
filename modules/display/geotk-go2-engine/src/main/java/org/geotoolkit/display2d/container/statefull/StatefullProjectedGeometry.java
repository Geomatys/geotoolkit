/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import java.awt.Shape;

import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.jts.AbstractJTSGeometryJ2D;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.geometry.isoonjts.JTSUtils;

import org.opengis.geometry.Geometry;
import org.opengis.referencing.operation.TransformException;

/**
 * Not thread safe.
 * Use it knowing you make clear cache operation in a syncrhonize way.
 * GraphicJ2D for feature objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatefullProjectedGeometry implements ProjectedGeometry {

    private final StatefullContextParams params;

    //Geometry in objective CRS
    private com.vividsolutions.jts.geom.Geometry    objectiveGeometryJTS = null;
    private Geometry                                objectiveGeometryISO = null;
    private Shape                                   objectiveShape = null;

    //Geometry in display CRS
    private com.vividsolutions.jts.geom.Geometry    displayGeometryJTS = null;
    private Geometry                                displayGeometryISO = null;
    private final AbstractJTSGeometryJ2D            displayShape;

    public StatefullProjectedGeometry(final StatefullContextParams params, final Class GeometryClazz,
            final com.vividsolutions.jts.geom.Geometry geom){
        this.params = params;
        this.objectiveGeometryJTS = geom;
        this.displayShape = JTSGeometryJ2D.best(GeometryClazz,params.objectiveToDisplay);
        this.displayShape.setGeometry(objectiveGeometryJTS);
    }

    public StatefullProjectedGeometry(final StatefullProjectedGeometry copy){
        this.params = copy.params;
        this.objectiveGeometryJTS = copy.objectiveGeometryJTS;
        this.objectiveGeometryISO = copy.objectiveGeometryISO;
        this.objectiveShape = copy.objectiveShape;
        this.displayGeometryJTS = copy.displayGeometryJTS;
        this.displayGeometryISO = copy.displayGeometryISO;
        this.displayShape = copy.displayShape.clone();
    }

    public void setObjectiveGeometry(final com.vividsolutions.jts.geom.Geometry geom){
        clearObjectiveCache();
        this.objectiveGeometryJTS = geom;
        this.displayShape.setGeometry(objectiveGeometryJTS);
    }

    public void clearObjectiveCache(){
        clearDisplayCache();
        objectiveGeometryISO = null;
        objectiveShape = null;
    }
    
    public void clearDisplayCache(){
        displayGeometryISO = null;
        displayGeometryJTS = null;
    }

    @Override
    public com.vividsolutions.jts.geom.Geometry getObjectiveGeometryJTS() {
        return objectiveGeometryJTS;
    }

    @Override
    public com.vividsolutions.jts.geom.Geometry getDisplayGeometryJTS() throws TransformException{
        if(displayGeometryJTS == null){
            displayGeometryJTS = params.objToDisplayTransformer.transform(getObjectiveGeometryJTS());
        }
        return displayGeometryJTS;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Shape getObjectiveShape() throws TransformException{
        if(objectiveShape == null){
            objectiveShape = GO2Utilities.toJava2D(getObjectiveGeometryJTS());
        }
        return objectiveShape;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Shape getDisplayShape() throws TransformException{
        if(objectiveGeometryJTS == null){
            return null;
        }
        return displayShape;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Geometry getObjectiveGeometry() throws TransformException {
        if(objectiveGeometryISO == null){
            objectiveGeometryISO = JTSUtils.toISO(getObjectiveGeometryJTS(), params.objectiveCRS);
        }
        return objectiveGeometryISO;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Geometry getDisplayGeometry() throws TransformException {
        if(displayGeometryISO == null){
            displayGeometryISO = JTSUtils.toISO(getDisplayGeometryJTS(), params.displayCRS);
        }
        return displayGeometryISO;
    }

}
