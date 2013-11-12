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
package org.geotoolkit.display2d.primitive;

import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.display.shape.ProjectedShape;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

/**
 * convenient class to manipulate geometry in the go2 engine.
 * The geometry may be asked in different format depending of the needs.
 * </br>
 * For example it is interesting to use the java2d shape for painting and the
 * ISO/JTS geometries for intersections tests.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ProjectedGeometry  {

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

    private CoordinateReferenceSystem dataCRS = null;

    public ProjectedGeometry(final StatelessContextParams params){
        this.params = params;
    }

    public ProjectedGeometry(final ProjectedGeometry copy){
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
            if(dataCRS != null && this.dataCRS!=dataCRS){
                this.dataCRS = dataCRS;
                dataCRS = CRSUtilities.getCRS2D(dataCRS);
                dataToObjective = (MathTransform2D) CRS.findMathTransform(dataCRS, params.context.getObjectiveCRS2D());
                dataToDisplay = (MathTransform2D) CRS.findMathTransform(dataCRS, params.displayCRS);
            }
        } catch (Exception ex) {
            Logger.getLogger(ProjectedGeometry.class.getName()).log(Level.WARNING, null, ex);
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

    /**
     * Get a JTS representation of the geometry in objective CRS.
     *
     * @return JTS Geometry
     * @throws TransformException if geometry could not be reprojected.
     */
    public com.vividsolutions.jts.geom.Geometry getObjectiveGeometryJTS() throws TransformException {
        if(objectiveGeometryJTS == null && geomSet){
            if(dataToObjective == null){
                //we assume data and objective are in the same crs
                objectiveGeometryJTS = dataGeometryJTS;
            }else{
                final GeometryCSTransformer transformer = new GeometryCSTransformer(new CoordinateSequenceMathTransformer(dataToObjective));
                objectiveGeometryJTS = transformer.transform(getDataGeometryJTS());
            }

            //check if we need to demultiply the geometry
            if(params.context.wrapArea != null){
                final List<com.vividsolutions.jts.geom.Geometry> geoms = new  ArrayList();
                final GeometryFactory GF = new GeometryFactory();
                for(AffineTransform2D trs : params.context.wrapsObjectives){
                    final com.vividsolutions.jts.geom.Geometry g = JTS.transform(objectiveGeometryJTS, trs);
                    if(g instanceof GeometryCollection){
                        GeometryCollection gc = (GeometryCollection) g;
                        for(int i=0;i<gc.getNumGeometries();i++){
                            geoms.add(gc.getGeometryN(i));
                        }
                    }else{
                        geoms.add(g);
                    }
                }

                if(objectiveGeometryJTS instanceof com.vividsolutions.jts.geom.Point){
                    objectiveGeometryJTS = GF.createMultiPoint(geoms.toArray(new com.vividsolutions.jts.geom.Point[0]));
                }else if(objectiveGeometryJTS instanceof com.vividsolutions.jts.geom.LineString){
                    objectiveGeometryJTS = GF.createMultiLineString(geoms.toArray(new com.vividsolutions.jts.geom.LineString[0]));
                }else if(objectiveGeometryJTS instanceof com.vividsolutions.jts.geom.Polygon){
                    objectiveGeometryJTS = GF.createMultiPolygon(geoms.toArray(new com.vividsolutions.jts.geom.Polygon[0]));
                }else if(objectiveGeometryJTS instanceof com.vividsolutions.jts.geom.MultiPoint){
                    objectiveGeometryJTS = GF.createMultiPoint(geoms.toArray(new com.vividsolutions.jts.geom.Point[0]));
                }else if(objectiveGeometryJTS instanceof com.vividsolutions.jts.geom.MultiLineString){
                    objectiveGeometryJTS = GF.createMultiLineString(geoms.toArray(new com.vividsolutions.jts.geom.LineString[0]));
                }else if(objectiveGeometryJTS instanceof com.vividsolutions.jts.geom.MultiPolygon){
                    objectiveGeometryJTS = GF.createMultiPolygon(geoms.toArray(new com.vividsolutions.jts.geom.Polygon[0]));
                }else{
                    objectiveGeometryJTS = GF.createGeometryCollection(geoms.toArray(new com.vividsolutions.jts.geom.Geometry[0]));
                }
            }

        }
        return objectiveGeometryJTS;
    }

    /**
     * Get a JTS representation of the geometry in display CRS.
     *
     * @return JTS Geometry
     * @throws TransformException if geometry could not be reprojected.
     */
    public com.vividsolutions.jts.geom.Geometry getDisplayGeometryJTS() throws TransformException{
        if(displayGeometryJTS == null && geomSet){
            displayGeometryJTS = params.objToDisplayTransformer.transform(getObjectiveGeometryJTS());
        }
        return displayGeometryJTS;
    }

    /**
     * Get a Java2D representation of the geometry in objective CRS.
     *
     * @return Java2D shape
     * @throws TransformException if geometry could not be reprojected.
     */
    public Shape getObjectiveShape() throws TransformException{
        if(objectiveShape == null && geomSet){
            if(params.context.wrapArea != null){
                //we need to rely on the objective geometry which has been
                //demultiplied/clipped as necessary for the map wrap
                objectiveShape = new JTSGeometryJ2D(getObjectiveGeometryJTS());
            }else{
                objectiveShape = ProjectedShape.wrap(getDataShape(), dataToObjective);
            }
        }
        return objectiveShape;
    }

    /**
     * Get a Java2D representation of the geometry in display CRS.
     *
     * @return Java2D shape
     * @throws TransformException if geometry could not be reprojected.
     */
    public Shape getDisplayShape() throws TransformException{
        if(displayShape == null && geomSet){
            if(params.context.wrapArea != null){
                //we need to rely on the objective geometry which has been
                //demultiplied/clipped as necessary for the map wrap
                displayShape = new JTSGeometryJ2D(getDisplayGeometryJTS());
            }else{
                displayShape = ProjectedShape.wrap(getDataShape(), dataToDisplay);
            }
        }
        return displayShape;
    }

    /**
     * Get an ISO representation of the geometry in objective CRS.
     *
     * @return ISO Geometry
     * @throws TransformException if geometry could not be reprojected.
     */
    public Geometry getObjectiveGeometry() throws TransformException {
        if(objectiveGeometryISO == null && geomSet){
            objectiveGeometryISO = JTSUtils.toISO(getObjectiveGeometryJTS(), params.objectiveCRS);
        }
        return objectiveGeometryISO;
    }

    /**
     * Get a ISO representation of the geometry in display CRS.
     *
     * @return ISO Geometry
     * @throws TransformException if geometry could not be reprojected.
     */
    public Geometry getDisplayGeometry() throws TransformException {
        if(displayGeometryISO == null && geomSet){
            displayGeometryISO = JTSUtils.toISO(getDisplayGeometryJTS(), params.displayCRS);
        }
        return displayGeometryISO;
    }

}
