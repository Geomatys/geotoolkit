/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2014, Geomatys
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
import com.vividsolutions.jts.geom.Envelope;
import java.awt.Shape;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.geometry.isoonjts.JTSUtils;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceMathTransformer;
import org.geotoolkit.geometry.jts.transform.CoordinateSequenceWrapTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryCSTransformer;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

/**
 * convenient class to manipulate geometry in the 2d engine.
 * The geometry may be asked in different format depending of the needs.
 * <br/>
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
        this.params                 = copy.params;
        this.dataToObjective        = copy.dataToObjective;
        this.dataToDisplay          = copy.dataToDisplay;
        this.dataGeometryJTS        = copy.dataGeometryJTS;
        this.dataGeometryISO        = copy.dataGeometryISO;
        this.dataShape              = copy.dataShape;
        this.objectiveGeometryJTS   = copy.objectiveGeometryJTS;
        this.objectiveGeometryISO   = copy.objectiveGeometryISO;
        this.objectiveShape         = copy.objectiveShape;
        this.displayGeometryJTS     = null;
        this.displayGeometryISO     = null;
        this.displayShape           = null;
        this.geomSet                = copy.geomSet;
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

    public MathTransform2D getDataToDisplay() {
        return dataToDisplay;
    }

    public MathTransform2D getDataToObjective() {
        return dataToObjective;
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
            
            com.vividsolutions.jts.geom.Geometry objBase;
            if(dataToObjective == null){
                //we assume data and objective are in the same crs
                objBase = dataGeometryJTS;
            }else{
                final GeometryCSTransformer transformer = new GeometryCSTransformer(new CoordinateSequenceMathTransformer(dataToObjective));
                objBase = transformer.transform(getDataGeometryJTS());
            }

            
            if(params.context.wraps != null){
                
                com.vividsolutions.jts.geom.Envelope objBounds = objBase.getEnvelopeInternal();
                final double dx = params.context.wraps.wrapPoints[1].getOrdinate(0) - params.context.wraps.wrapPoints[0].getOrdinate(0);
                final double dy = params.context.wraps.wrapPoints[1].getOrdinate(1) - params.context.wraps.wrapPoints[0].getOrdinate(1);
                
                // fix the geometry if some points wrap around the meridian
                // we expect the warp points to be axis aligned, TODO handle other cases
                if(dx!=0 && dy!=0){
                    throw new TransformException("Coordinate Reference System, wrap around points are not axis aligned.");
                }
                
                if( (dx>0 && (objBounds.getWidth() > (dx/2.0))) || 
                    (dy>0 && (objBounds.getHeight() > (dy/2.0)))){
                    // this is a possible wrap around geometry
                    final double[] wrapTranslate = new double[]{dx,dy};
                    final CoordinateSequenceWrapTransformer cstrs = new CoordinateSequenceWrapTransformer(wrapTranslate);
                    final GeometryCSTransformer transformer = new GeometryCSTransformer(cstrs);
                    objBase = transformer.transform(objBase);
                    objBounds = objBase.getEnvelopeInternal();
                }
                
                //check if the geometry overlaps the meridian
                int nbIncRep = params.context.wraps.wrapIncNb;
                int nbDecRep = params.context.wraps.wrapDecNb;
                com.vividsolutions.jts.geom.Geometry objBoundsGeom = JTS.toGeometry(objBounds);
                
                // geometry cross the far east meridian, geometry is like : 
                // POLYGON(-179,10,  181,10,  181,-10,  179,-10)
                if(objBoundsGeom.intersects(params.context.wraps.wrapIncLine)){
                    //duplicate geometry on the other warp line
                    nbDecRep++;
                }
                // geometry cross the far west meridian, geometry is like : 
                // POLYGON(-179,10, -181,10, -181,-10,  -179,-10)
                else if(objBoundsGeom.intersects(params.context.wraps.wrapDecLine)){
                    //duplicate geometry on the other warp line
                    nbIncRep++;
                }
                
                objectiveGeometryJTS = new com.vividsolutions.jts.geom.Geometry[nbIncRep+nbDecRep+1];
                int n=0;
                for(int i=0;i<nbIncRep;i++){
                    //check that the futur geometry will intersect the visible area
                    final com.vividsolutions.jts.geom.Envelope candidate = JTS.transform(objBounds, params.context.wraps.wrapIncObj[i]);
                    if(candidate.intersects(params.objectiveJTSEnvelope)){
                        objectiveGeometryJTS[n++] = JTS.transform(objBase, params.context.wraps.wrapIncObj[i]);
                    }
                }
                if(objBounds.intersects(params.objectiveJTSEnvelope)){
                    objectiveGeometryJTS[n++] = objBase;
                }
                for(int i=0;i<nbDecRep;i++){
                    //check that the futur geometry will intersect the visible area
                    final com.vividsolutions.jts.geom.Envelope candidate = JTS.transform(objBounds, params.context.wraps.wrapDecObj[i]);
                    if(candidate.intersects(params.objectiveJTSEnvelope)){
                        objectiveGeometryJTS[n++] = JTS.transform(objBase, params.context.wraps.wrapDecObj[i]);
                    }
                }
                if(n!=objectiveGeometryJTS.length){
                    //some of the wrapped geometries do not intersect the visible area
                    objectiveGeometryJTS = Arrays.copyOf(objectiveGeometryJTS, n);
                }
                
                
            }else{
                //geometry is valid with no modifications or repetition
                objectiveGeometryJTS = new com.vividsolutions.jts.geom.Geometry[1];
                objectiveGeometryJTS[0] = objBase;
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
                if(params.displayClipRect!=null){
                    //check envelopes
                    final Envelope env = displayGeometryJTS[i].getEnvelopeInternal();
                    if(!env.isNull() && !params.displayClip.getEnvelopeInternal().contains(env)){
                        //clip to display bounds
                        displayShape[i] = Clipper.clipToRect(displayShape[i], params.displayClipRect);
                    }
                    
                }
                //TODO find a way to reactive curves is there is no transformation
                //displayShape = ProjectedShape.wrap(shape, dataToDisplay);
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
