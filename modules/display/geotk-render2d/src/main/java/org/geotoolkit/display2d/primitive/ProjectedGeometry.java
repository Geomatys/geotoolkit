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

import com.bric.geom.Clipper;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
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
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceWrapTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.math.XMath;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.opengis.geometry.Envelope;
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
    private Rectangle2D dataClipRectangle;

    //Geometry is data CRS
    private com.vividsolutions.jts.geom.Geometry    dataGeometryJTS = null;
    private Geometry                                dataGeometryISO = null;
    private Shape                                   dataShape = null;

    //Geometry in objective CRS
    private com.vividsolutions.jts.geom.Geometry[]    objectiveGeometryJTS = null;
    private Geometry[]                                objectiveGeometryISO = null;
    private Shape[]                                   objectiveShape = null;

    //Geometry in display CRS
    private com.vividsolutions.jts.geom.Geometry[]    displayGeometryJTS = null;
    private Geometry[]                                displayGeometryISO = null;
    private Shape[]                                   displayShape = null;

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
                final Envelope env = CRS.transform(params.context.getCanvasObjectiveBounds2D(), dataCRS);
                final double marginX = env.getSpan(0)/10;
                final double marginY = env.getSpan(1)/10;
                dataClipRectangle = new Rectangle2D.Double(
                        env.getMinimum(0)-marginX, env.getMinimum(1)-marginY, 
                        env.getSpan(0)+marginX*2, env.getSpan(1)+marginY*2);
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
    public com.vividsolutions.jts.geom.Geometry[] getObjectiveGeometryJTS() throws TransformException {
        if(objectiveGeometryJTS == null && geomSet){
            
            objectiveGeometryJTS = new com.vividsolutions.jts.geom.Geometry[1];
            
            final com.vividsolutions.jts.geom.Geometry objBase;
            if(dataToObjective == null){
                //we assume data and objective are in the same crs
                objBase = dataGeometryJTS;
            }else{
                final GeometryCSTransformer transformer = new GeometryCSTransformer(new CoordinateSequenceMathTransformer(dataToObjective));
                objBase = transformer.transform(getDataGeometryJTS());
            }

            
            if(params.context.wraps != null){
                
                final com.vividsolutions.jts.geom.Geometry objEnv = objBase.getBoundary();
                
                int nbIncRep = params.context.wraps.wrapIncNb;
                int nbDecRep = params.context.wraps.wrapDecNb;
                
                // geometry cross the far east meridian, geometry is like : 
                // POLYGON(-179,10,  181,10,  181,-10,  179,-10)
                if(objEnv.intersects(params.context.wraps.wrapIncLine)){
                    //duplicate geometry on the other warp line
                    nbDecRep++;
                }
                // geometry cross the far west meridian, geometry is like : 
                // POLYGON(-179,10, -181,10, -181,-10,  -179,-10)
                else if(objEnv.intersects(params.context.wraps.wrapDecLine)){
                    //duplicate geometry on the other warp line
                    nbIncRep++;
                }
                
                objectiveGeometryJTS = new com.vividsolutions.jts.geom.Geometry[nbIncRep+nbDecRep+1];
                int n=0;
                for(int i=0;i<nbIncRep;i++){
                    objectiveGeometryJTS[n++] = JTS.transform(dataGeometryJTS, params.context.wraps.wrapIncObj[i]);
                }
                objectiveGeometryJTS[n++] = dataGeometryJTS;
                for(int i=0;i<nbDecRep;i++){
                    objectiveGeometryJTS[n++] = JTS.transform(dataGeometryJTS, params.context.wraps.wrapDecObj[i]);
                }
                
            }else{
                //geometry is valid with no modifications or repetition
                objectiveGeometryJTS = new com.vividsolutions.jts.geom.Geometry[1];
                objectiveGeometryJTS[0] = dataGeometryJTS;
            }
            
            
            
//            //check if geometries cross the meridian
//            if(params.context.wrapArea != null){
//                final double dx = params.context.wrapPoints[1].getOrdinate(0) - params.context.wrapPoints[0].getOrdinate(0);
//                final double dy = params.context.wrapPoints[1].getOrdinate(1) - params.context.wrapPoints[0].getOrdinate(1);
//                final double[] wrapTranslate = new double[]{-dx,-dy};
//                final double[] wrapDistance = new double[]{dx/2,dy/2};
//                final CoordinateSequenceWrapTransformer cstrs = new CoordinateSequenceWrapTransformer(wrapDistance,wrapTranslate);
//                final GeometryCSTransformer transformer = new GeometryCSTransformer(cstrs);
//                objectiveGeometryJTS = transformer.transform(objectiveGeometryJTS);
//            }
//
//            //check if we need to demultiply the geometry
//            if(params.context.wrapArea != null){
//                final List<com.vividsolutions.jts.geom.Geometry> geoms = new  ArrayList();
//                final GeometryFactory GF = new GeometryFactory();
//                for(AffineTransform2D trs : params.context.wrapsObjectives){
//                    final com.vividsolutions.jts.geom.Geometry g = JTS.transform(objectiveGeometryJTS, trs);
//                    if(g instanceof GeometryCollection){
//                        GeometryCollection gc = (GeometryCollection) g;
//                        for(int i=0;i<gc.getNumGeometries();i++){
//                            geoms.add(gc.getGeometryN(i));
//                        }
//                    }else{
//                        geoms.add(g);
//                    }
//                }
//
//                if(objectiveGeometryJTS instanceof com.vividsolutions.jts.geom.Point){
//                    objectiveGeometryJTS = GF.createMultiPoint(geoms.toArray(new com.vividsolutions.jts.geom.Point[0]));
//                }else if(objectiveGeometryJTS instanceof com.vividsolutions.jts.geom.LineString){
//                    objectiveGeometryJTS = GF.createMultiLineString(geoms.toArray(new com.vividsolutions.jts.geom.LineString[0]));
//                }else if(objectiveGeometryJTS instanceof com.vividsolutions.jts.geom.Polygon){
//                    objectiveGeometryJTS = GF.createMultiPolygon(geoms.toArray(new com.vividsolutions.jts.geom.Polygon[0]));
//                }else if(objectiveGeometryJTS instanceof com.vividsolutions.jts.geom.MultiPoint){
//                    objectiveGeometryJTS = GF.createMultiPoint(geoms.toArray(new com.vividsolutions.jts.geom.Point[0]));
//                }else if(objectiveGeometryJTS instanceof com.vividsolutions.jts.geom.MultiLineString){
//                    objectiveGeometryJTS = GF.createMultiLineString(geoms.toArray(new com.vividsolutions.jts.geom.LineString[0]));
//                }else if(objectiveGeometryJTS instanceof com.vividsolutions.jts.geom.MultiPolygon){
//                    objectiveGeometryJTS = GF.createMultiPolygon(geoms.toArray(new com.vividsolutions.jts.geom.Polygon[0]));
//                }else{
//                    objectiveGeometryJTS = GF.createGeometryCollection(geoms.toArray(new com.vividsolutions.jts.geom.Geometry[0]));
//                }
//            }

        }
        return objectiveGeometryJTS;
    }

    /**
     * Get a JTS representation of the geometry in display CRS.
     *
     * @return JTS Geometry
     * @throws TransformException if geometry could not be reprojected.
     */
    public com.vividsolutions.jts.geom.Geometry[] getDisplayGeometryJTS() throws TransformException{
        if(displayGeometryJTS == null && geomSet){
            getObjectiveGeometryJTS();
            displayGeometryJTS = new com.vividsolutions.jts.geom.Geometry[objectiveGeometryJTS.length];
            for(int i=0;i<displayGeometryJTS.length;i++){
                displayGeometryJTS[i] = params.objToDisplayTransformer.transform(objectiveGeometryJTS[i]);
            }
        }
        return displayGeometryJTS;
    }

    /**
     * Get a Java2D representation of the geometry in objective CRS.
     *
     * @return Java2D shape
     * @throws TransformException if geometry could not be reprojected.
     */
    public Shape[] getObjectiveShape() throws TransformException{
        if(objectiveShape == null && geomSet){
            getObjectiveGeometryJTS();
            objectiveShape = new Shape[objectiveGeometryJTS.length];
            for(int i=0;i<objectiveShape.length;i++){
                objectiveShape[i] = new JTSGeometryJ2D(objectiveGeometryJTS[i]);
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
    public Shape[] getDisplayShape() throws TransformException{
        if(displayShape == null && geomSet){
            getDisplayGeometryJTS();
            displayShape = new Shape[displayGeometryJTS.length];
            for(int i=0;i<displayShape.length;i++){
                displayShape[i] = new JTSGeometryJ2D(displayGeometryJTS[i]);
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
    public Geometry[] getObjectiveGeometry() throws TransformException {
        if(objectiveGeometryISO == null && geomSet){
            getObjectiveGeometryJTS();
            objectiveGeometryISO = new Geometry[objectiveGeometryJTS.length];
            for(int i=0;i<objectiveGeometryISO.length;i++){
                objectiveGeometryISO[i] = JTSUtils.toISO(objectiveGeometryJTS[i], params.objectiveCRS);
            }
        }
        return objectiveGeometryISO;
    }

    /**
     * Get a ISO representation of the geometry in display CRS.
     *
     * @return ISO Geometry
     * @throws TransformException if geometry could not be reprojected.
     */
    public Geometry[] getDisplayGeometry() throws TransformException {
        if(displayGeometryISO == null && geomSet){
            getDataGeometryJTS();
            displayGeometryISO = new Geometry[displayGeometryJTS.length];
            for(int i=0;i<displayGeometryISO.length;i++){
                displayGeometryISO[i] = JTSUtils.toISO(displayGeometryJTS[i], params.displayCRS);
            }
        }
        return displayGeometryISO;
    }

}
