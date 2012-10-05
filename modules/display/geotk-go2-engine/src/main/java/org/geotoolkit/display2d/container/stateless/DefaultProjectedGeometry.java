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
package org.geotoolkit.display2d.container.stateless;

import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import java.awt.Shape;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.display.shape.ProjectedShape;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

/**
 * Not thread safe.
 * Use it knowing you make clear cache operation in a syncrhonize way.
 * GraphicJ2D for feature objects.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultProjectedGeometry implements ProjectedGeometry {

    private final StatelessContextParams params;
    private MathTransform2D dataToObjective;
    private MathTransform2D dataToDisplay;

    //Geometry is data CRS
    private com.vividsolutions.jts.geom.Geometry    dataGeometryJTS = null;
    private Geometry                                dataGeometryISO = null;
    private Shape                                   dataShape = null;
    
    //Geometry in objective CRS
    private com.vividsolutions.jts.geom.Geometry    objectiveGeometryJTS = null;
    private Geometry                                objectiveGeometryISO = null;
    private Shape                                   objectiveShape = null;

    //Geometry in display CRS
    private com.vividsolutions.jts.geom.Geometry    displayGeometryJTS = null;
    private Geometry                                displayGeometryISO = null;
    private Shape                                   displayShape = null;
    
    private boolean geomSet = false;

    public DefaultProjectedGeometry(final StatelessContextParams params){
        this.params = params;
    }

    public DefaultProjectedGeometry(final DefaultProjectedGeometry copy){
        this.params = copy.params;
        this.dataToObjective = copy.dataToObjective;
        this.dataToDisplay = copy.dataToDisplay;
        this.dataGeometryJTS = copy.dataGeometryJTS;
        this.dataGeometryISO = copy.dataGeometryISO;
        this.dataShape       = copy.dataShape;
        this.objectiveGeometryJTS = copy.objectiveGeometryJTS;
        this.objectiveGeometryISO = copy.objectiveGeometryISO;
        this.objectiveShape       = copy.objectiveShape;
        this.displayGeometryJTS = null;
        this.displayGeometryISO = null;
        this.displayShape       = null;
        this.geomSet = copy.geomSet;
    }

    public void setDataGeometry(final com.vividsolutions.jts.geom.Geometry geom, CoordinateReferenceSystem dataCRS){
        clearDataCache();
        this.dataGeometryJTS = geom;
        this.geomSet = this.dataGeometryJTS != null;
        
        try {
            if(dataCRS == null){
                //try to extract data crs from geometry
                dataCRS = JTS.findCoordinateReferenceSystem(geom);
            }
            if(dataCRS != null){
                dataCRS = CRSUtilities.getCRS2D(dataCRS);
                dataToObjective = (MathTransform2D) CRS.findMathTransform(dataCRS, params.context.getObjectiveCRS2D());
                dataToDisplay = (MathTransform2D) CRS.findMathTransform(dataCRS, params.displayCRS);
            }
        } catch (Exception ex) {
            Logger.getLogger(DefaultProjectedGeometry.class.getName()).log(Level.WARNING, null, ex);
        }
    }
    
    public boolean isSet(){
        return this.dataGeometryJTS != null;
    }
    
    public void clearAll(){
        clearDataCache();
    }
    
    public void clearDataCache(){
        clearObjectiveCache();
        dataGeometryISO = null;
        dataGeometryJTS = null;
        dataShape = null;
    }
    
    public void clearObjectiveCache(){
        clearDisplayCache();
        objectiveGeometryISO = null;
        objectiveGeometryJTS = null;
        objectiveShape = null;
    }
    
    public void clearDisplayCache(){
        displayGeometryISO = null;
        displayGeometryJTS = null;
        displayShape = null;
    }

    public Geometry getDataGeometryISO() {
        return dataGeometryISO;
    }

    public com.vividsolutions.jts.geom.Geometry getDataGeometryJTS() {
        return dataGeometryJTS;
    }

    public Shape getDataShape() {
        if(dataShape == null && geomSet){
            dataShape = new JTSGeometryJ2D(dataGeometryJTS);
        }
        
        return dataShape;
    }

    @Override
    public com.vividsolutions.jts.geom.Geometry getObjectiveGeometryJTS() throws TransformException {
        if(objectiveGeometryJTS == null && geomSet){
            if(dataToObjective == null){
                //we assume data and objective are in the same crs
                objectiveGeometryJTS = dataGeometryJTS;
            }else{
                final GeometryCSTransformer transformer = new GeometryCSTransformer(new CoordinateSequenceMathTransformer(dataToObjective));
                objectiveGeometryJTS = transformer.transform(getDataGeometryJTS());
            }
        }
        return objectiveGeometryJTS;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Geometry getObjectiveGeometry() throws TransformException {
        if(objectiveGeometryISO == null && geomSet){
            objectiveGeometryISO = JTSUtils.toISO(getObjectiveGeometryJTS(), params.objectiveCRS);
        }
        return objectiveGeometryISO;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Shape getObjectiveShape() throws TransformException{
        if(objectiveShape == null && geomSet){
            objectiveShape = ProjectedShape.wrap(getDataShape(), dataToObjective);
        }
        return objectiveShape;
    }
    
    @Override
    public com.vividsolutions.jts.geom.Geometry getDisplayGeometryJTS() throws TransformException{
        if(displayGeometryJTS == null && geomSet){
            displayGeometryJTS = params.objToDisplayTransformer.transform(getObjectiveGeometryJTS());
        }
        return displayGeometryJTS;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Geometry getDisplayGeometry() throws TransformException {
        if(displayGeometryISO == null && geomSet){
            displayGeometryISO = JTSUtils.toISO(getDisplayGeometryJTS(), params.displayCRS);
        }
        return displayGeometryISO;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Shape getDisplayShape() throws TransformException{
        if(displayShape == null && geomSet){
            displayShape = ProjectedShape.wrap(getDataShape(), dataToDisplay);
        }
        return displayShape;
    }

}
