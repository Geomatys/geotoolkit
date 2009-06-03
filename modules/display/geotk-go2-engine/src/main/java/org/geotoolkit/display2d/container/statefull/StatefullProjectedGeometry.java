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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

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
    private Geometry                                dataGeometryISO = null;

    //Geometry in data CRS decimated
    private com.vividsolutions.jts.geom.Geometry    decimatedGeometryJTS = null;
    private Geometry                                decimatedGeometryISO = null;

    //Geometry in objective CRS
    private com.vividsolutions.jts.geom.Geometry    objectiveGeometryJTS = null;
    private Geometry                                objectiveGeometryISO = null;
    private Shape                                   objectiveShape = null;
    private Rectangle2D                             objectiveBounds = null;

    //Geometry in display CRS
    private com.vividsolutions.jts.geom.Geometry    displayGeometryJTS = null;
    private Geometry                                displayGeometryISO = null;
    private Shape                                   displayShape = null;
    private Rectangle                               displayBounds = null;

    public StatefullProjectedGeometry(StatefullContextParams params, com.vividsolutions.jts.geom.Geometry geom){
        this.params = params;
        this.dataGeometryJTS = geom;
    }

    public synchronized void clearDataCache(){
        clearObjectiveCache();
        this.decimatedGeometryJTS = null;
        this.decimatedGeometryISO = null;
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

    private com.vividsolutions.jts.geom.Geometry getGeometryJTS(){
        if(decimatedGeometryJTS == null){
            decimatedGeometryJTS = dataGeometryJTS;
            if(params.decimate && params.decimation != 0){
                decimatedGeometryJTS = DouglasPeuckerSimplifier.simplify(decimatedGeometryJTS, params.decimation);
            }
        }
        return decimatedGeometryJTS;
    }

    @Override
    public com.vividsolutions.jts.geom.Geometry getObjectiveGeometry() throws TransformException{
        if(objectiveGeometryJTS == null){
            objectiveGeometryJTS = params.dataToObjectiveTransformer.transform(getGeometryJTS());
        }
        return objectiveGeometryJTS;
    }

    @Override
    public com.vividsolutions.jts.geom.Geometry getDisplayGeometry() throws TransformException{
        if(displayGeometryJTS == null){
            displayGeometryJTS = params.dataToDisplayTransformer.transform(getGeometryJTS());
        }
        return displayGeometryJTS;
    }

    @Override
    public Shape getObjectiveShape() throws TransformException{
        if(objectiveShape == null){
//            objectiveShape = GO2Utilities.toJava2D(getObjectiveGeometryISO());
            objectiveShape = GO2Utilities.toJava2D(getObjectiveGeometry());
        }
        return objectiveShape;
    }

    @Override
    public Shape getDisplayShape() throws TransformException{
        if(displayShape == null){
//            displayShape = GO2Utilities.toJava2D(getDisplayGeometryISO());
            displayShape = GO2Utilities.toJava2D(getDisplayGeometry());
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

}
