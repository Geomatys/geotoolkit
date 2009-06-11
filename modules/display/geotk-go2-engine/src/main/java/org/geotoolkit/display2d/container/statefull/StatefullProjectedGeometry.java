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

import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import java.awt.Shape;

import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.geometry.isoonjts.JTSUtils;

import org.opengis.geometry.Geometry;
import org.opengis.referencing.operation.TransformException;

/**
 * GraphicJ2D for feature objects.
 *
 * @author Johann Sorel (Geomatys)
 */
public class StatefullProjectedGeometry implements ProjectedGeometry {

    private final StatefullContextParams params;

    //Geometry in data CRS
    private com.vividsolutions.jts.geom.Geometry    dataGeometryJTS = null;

    //Geometry in data CRS decimated
    private com.vividsolutions.jts.geom.Geometry    decimatedGeometryJTS = null;

    //Geometry in objective CRS
    private com.vividsolutions.jts.geom.Geometry    objectiveGeometryJTS = null;
    private Geometry                                objectiveGeometryISO = null;
    private Shape                                   objectiveShape = null;

    //Geometry in display CRS
    private com.vividsolutions.jts.geom.Geometry    displayGeometryJTS = null;
    private Geometry                                displayGeometryISO = null;
    private Shape                                   displayShape = null;

    public StatefullProjectedGeometry(StatefullContextParams params, com.vividsolutions.jts.geom.Geometry geom){
        this.params = params;
        this.dataGeometryJTS = geom;
    }

    public synchronized void clearDataCache(){
        clearObjectiveCache();
        this.decimatedGeometryJTS = null;
    }

    public synchronized void clearObjectiveCache(){
        clearDisplayCache();
        objectiveGeometryISO = null;
        objectiveGeometryJTS = null;
        objectiveShape = null;
    }
    
    public synchronized void clearDisplayCache(){
        displayGeometryISO = null;
        displayGeometryJTS = null;
        displayShape = null;
    }

    private com.vividsolutions.jts.geom.Geometry getGeometryJTS(){
        if(decimatedGeometryJTS == null){
            decimatedGeometryJTS = dataGeometryJTS;
            if(params.decimate && params.decimation != 0){
                decimatedGeometryJTS = DouglasPeuckerSimplifier.simplify(decimatedGeometryJTS, params.decimation);
            }
        }
        return decimatedGeometryJTS;
    }

    public com.vividsolutions.jts.geom.Geometry getObjectiveGeometryJTS() throws TransformException{
        if(objectiveGeometryJTS == null){
            objectiveGeometryJTS = params.dataToObjectiveTransformer.transform(getGeometryJTS());
        }
        return objectiveGeometryJTS;
    }

    public com.vividsolutions.jts.geom.Geometry getDisplayGeometryJTS() throws TransformException{
        if(displayGeometryJTS == null){
            displayGeometryJTS = params.dataToDisplayTransformer.transform(getGeometryJTS());
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
        if(displayShape == null){
            displayShape = GO2Utilities.toJava2D(getDisplayGeometryJTS());
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
