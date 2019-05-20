/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2013, Geomatys
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
package org.geotoolkit.display2d.style.renderer;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.function.Consumer;

import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedPointSymbolizer;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.apache.sis.measure.Units;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.referencing.operation.TransformException;

/**
 * TODO: remove duplicate code
 * TODO: Use Jacobian matrix OF EACH RENDERED POINT to take CRS orientation/deformation into account.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultPointSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedPointSymbolizer>{

    /**
     * Defines the absolute radian value below which rotation is ignored.
     */
    private static final double ROTATION_TOLERANCE = 5e-2;

    public DefaultPointSymbolizerRenderer(final SymbolizerRendererService service,final CachedPointSymbolizer symbol, final RenderingContext2D context){
        super(service,symbol,context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean portray(final ProjectedCoverage projectedCoverage) throws PortrayalException{
        //portray the border of the coverage
        final ProjectedGeometry projectedGeometry = projectedCoverage.getEnvelopeGeometry();

        //could not find the border geometry
        if(projectedGeometry == null) return false;

        return portray(projectedGeometry, null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean portray(final ProjectedObject projectedFeature) throws PortrayalException{

        final Object candidate = projectedFeature.getCandidate();

        //test if the symbol is visible on this feature
        if(!symbol.isVisible(candidate)) return false;

        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(geomPropertyName);

        return portray(projectedGeometry, candidate);
    }

    private boolean portray(final ProjectedGeometry projectedGeometry, Object candidate) throws PortrayalException{

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return false;

        g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);

        //we switch to  more appropriate context CRS for rendering ---------
        // a point symbolis always paint in display unit -------------------
        renderingContext.switchToDisplayCRS();

        //we adjust coefficient for rendering ------------------------------
        float coeff;
        if(symbolUnit.equals(Units.POINT)){
            //symbol is in display unit
            coeff = 1;
        }else{
            //we have a special unit we must adjust the coefficient
            coeff = renderingContext.getUnitCoefficient(symbolUnit);
            // calculate scale difference between objective and display
            final AffineTransform inverse = renderingContext.getObjectiveToDisplay();
            coeff *= Math.abs(XAffineTransform.getScale(inverse));
        }

        //create the image--------------------------------------------------
        final BufferedImage img = symbol.getImage(candidate,coeff,false,hints);

        if(img == null){
            //may be correct, image can be too small for rendering
            return false;
        }

        final float[] disps = new float[2];
        final float[] anchor = new float[2];
        symbol.getDisplacement(candidate,disps);
        symbol.getAnchor(candidate,anchor);
        disps[0] *= coeff ;
        disps[1] *= coeff ;

        final Geometry[] geoms;
        try {
            geoms = projectedGeometry.getDisplayGeometryJTS();
        } catch (TransformException ex) {
            throw new PortrayalException("Could not calculate display projected geometry",ex);
        }

        if(geoms == null){
            //no geometry
            return false;
        }

        /**
         * Rendering context defines a Java2D rotation (counter-clockwise), but symbolizer defines a clockwise one
         * (note: Cannot find this information in SLD specification, but that's documented in GeoServer documentation).
         * To properly manage rotations, we have to merge them immediately because they
         */
        final double ccwRotation = -symbol.getRotation(candidate);
        final boolean skipRotation = Math.abs(ccwRotation) < ROTATION_TOLERANCE;

        final int postx = (int) (-img.getWidth()*anchor[0] + disps[0]);
        final int posty = (int) (-img.getHeight()*anchor[1] - disps[1]);
        final Consumer<Coordinate> drawer;
        if (skipRotation) {
            //we use Math.floor and not a cast, for negative values this ensure
            //a regular displacement and avoid tile border artifacts
            drawer = point -> g2d.drawImage(img, (int)Math.floor(point.x)+postx, (int)Math.floor(point.y)+posty, null);
        } else {
            drawer = point -> {
                final AffineTransform positioning = AffineTransform.getTranslateInstance(point.x, point.y);
                positioning.rotate(ccwRotation);
                positioning.translate(postx, posty);
                g2d.drawImage(img, positioning, null);
            };
        }

        boolean dataRendered = false;
        for(Geometry geom : geoms){
            if(geom instanceof Point || geom instanceof MultiPoint){
                //TODO use generalisation on multipoints

                final Coordinate[] coords = geom.getCoordinates();
                for(int i=0, n = coords.length; i<n ; i++){
                    final Coordinate coord = coords[i];

                    drawer.accept(coord);
                    dataRendered = true;
                }

            }else{
                //get most appropriate point
                final Point pt2d = GO2Utilities.getBestPoint(geom);
                if(pt2d == null || pt2d.isEmpty()){
                    //no geometry
                    return dataRendered;
                }

                Coordinate pcoord = pt2d.getCoordinate();
                if(Double.isNaN(pcoord.x)){
                    pcoord = geom.getCoordinate();
                }

                drawer.accept(pcoord);
                dataRendered = true;
            }
        }
        return dataRendered;
    }

    @Override
    public boolean portray(final Iterator<? extends ProjectedObject> graphics) throws PortrayalException {

        g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);

        //we switch to  more appropriate context CRS for rendering ---------
        // a point symbolis always paint in display unit -------------------
        renderingContext.switchToDisplayCRS();
        //we adjust coefficient for rendering ------------------------------
        float coeff;
        if(symbolUnit.equals(Units.POINT)){
            //symbol is in display unit
            coeff = 1;
        }else{
            //we have a special unit we must adjust the coefficient
            coeff = renderingContext.getUnitCoefficient(symbolUnit);
            // calculate scale difference between objective and display
            final AffineTransform inverse = renderingContext.getObjectiveToDisplay();
            coeff *= Math.abs(XAffineTransform.getScale(inverse));
        }

        //caches
        ProjectedObject projectedobj;
        Object candidate;
        final float[] disps = new float[2];
        final float[] anchor = new float[2];
        final AffineTransform imgTrs = new AffineTransform();

        final double rot = renderingContext.getRotation();
        final AffineTransform mapRotationTrs = new AffineTransform();
        mapRotationTrs.rotate(-rot);

        boolean dataRendered = false;
        while(graphics.hasNext()){
            if(monitor.stopRequested()) return dataRendered;

            projectedobj = graphics.next();
            candidate = projectedobj.getCandidate();

            //test if the symbol is visible on this feature
            if(!symbol.isVisible(candidate)) continue;

            final ProjectedGeometry projectedGeometry = projectedobj.getGeometry(geomPropertyName);

            //symbolizer doesnt match the featuretype, no geometry found with this name.
            if(projectedGeometry == null) continue;

            //create the image--------------------------------------------------
            final BufferedImage img = symbol.getImage(candidate,coeff,hints);

            if(img == null) throw new PortrayalException("A null image has been generated by a Mark symbol.");

            symbol.getDisplacement(candidate,disps);
            symbol.getAnchor(candidate,anchor);
            disps[0] *= coeff ;
            disps[1] *= coeff ;

            final Geometry[] geoms;
            try {
                geoms = projectedGeometry.getDisplayGeometryJTS();
            } catch (TransformException ex) {
                throw new PortrayalException("Could not calculate display projected geometry",ex);
            }

            for(Geometry geom : geoms){

                if(geom instanceof Point || geom instanceof MultiPoint){

                    //TODO use generalisation on multipoints

                    final Coordinate[] coords = geom.getCoordinates();
                    for(int i=0, n = coords.length; i<n ; i++){
                        final Coordinate coord = coords[i];
                        if(rot==0){
                            imgTrs.setToTranslation(
                                    -img.getWidth()*anchor[0] + coord.x + disps[0],
                                    -img.getHeight()*anchor[1] + coord.y - disps[1]);
                            g2d.drawRenderedImage(img, imgTrs);
                            dataRendered = true;
                        }else{
                            final int postx = (int) (-img.getWidth()*anchor[0] + disps[0]);
                            final int posty = (int) (-img.getHeight()*anchor[1] - disps[1]);
                            final AffineTransform ptrs = new AffineTransform(mapRotationTrs);
                            ptrs.preConcatenate(new AffineTransform(1, 0, 0, 1, coord.x, coord.y));
                            ptrs.concatenate(new AffineTransform(1, 0, 0, 1, postx, posty));
                            g2d.drawImage(img, ptrs, null);
                            dataRendered = true;
                        }
                    }

                }else if(geom!=null){

                    //get most appropriate point
                    final Point pt2d = GO2Utilities.getBestPoint(geom);
                    if(pt2d == null || pt2d.isEmpty()){
                        //no geometry
                        return dataRendered;
                    }
                    Coordinate pcoord = pt2d.getCoordinate();
                    if(Double.isNaN(pcoord.x)){
                        pcoord = geom.getCoordinate();
                    }
                    if(rot==0){
                        imgTrs.setToTranslation(
                                    -img.getWidth()*anchor[0] + pcoord.x + disps[0],
                                    -img.getHeight()*anchor[1] + pcoord.y - disps[1]);
                        g2d.drawRenderedImage(img, imgTrs);
                        dataRendered = true;
                    }else{
                        final int postx = (int) (-img.getWidth()*anchor[0] + disps[0]);
                        final int posty = (int) (-img.getHeight()*anchor[1] - disps[1]);
                        final AffineTransform ptrs = new AffineTransform(mapRotationTrs);
                        ptrs.preConcatenate(new AffineTransform(1, 0, 0, 1, pcoord.x, pcoord.y));
                        ptrs.concatenate(new AffineTransform(1, 0, 0, 1, postx, posty));
                        g2d.drawImage(img, ptrs, null);
                        dataRendered = true;
                    }
                }
            }
        }
        return dataRendered;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedObject projectedFeature, final SearchAreaJ2D search, final VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Shape mask = search.getDisplayShape();
        Geometry maskArea = null;

        final Object candidate = projectedFeature.getCandidate();

        //test if the symbol is visible on this feature
        if(!(symbol.isVisible(candidate))) return false;

        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(geomPropertyName);

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return false;

        //we adjust coefficient for rendering ----------------------------------
        float coeff = 1;
        if(symbolUnit.equals(Units.POINT)){
            //symbol is in display unit
            coeff = 1;
        }else{
            //we have a special unit we must adjust the coefficient
            coeff = renderingContext.getUnitCoefficient(symbolUnit);
            // calculate scale difference between objective and display
            final AffineTransform inverse = renderingContext.getObjectiveToDisplay();
            coeff *= Math.abs(XAffineTransform.getScale(inverse));
        }

        //create the image------------------------------------------------------
        final BufferedImage img = symbol.getImage(candidate,coeff,false,null);
        final float imgRot = symbol.getRotation(candidate);
        final float[] disps = new float[2];
        symbol.getDisplacement(candidate,disps);
        disps[0] *= coeff ;
        disps[1] *= coeff ;

        final float[] anchor = new float[2];
        symbol.getAnchor(candidate,anchor);

        final Geometry[] geoms;
        try {
            geoms = projectedGeometry.getDisplayGeometryJTS();
        } catch (TransformException ex) {
            ex.printStackTrace();
            return false;
        }

        for(Geometry geom : geoms){
            if(geom instanceof Point || geom instanceof MultiPoint){

                //TODO use generalisation on multipoints

                final Coordinate[] coords = geom.getCoordinates();
                for(int i=0, n = coords.length; i<n ; i++){
                    final Coordinate coord = coords[i];
                    final int x = (int) (-img.getWidth()*anchor[0] + coord.x + disps[0]);
                    final int y = (int) (-img.getHeight()*anchor[1] + coord.y - disps[1]);

                    //TODO should make a better test for the alpha pixel values in image
                    if(imgRot==0){
                        if(VisitFilter.INTERSECTS.equals(filter)){
                            if(mask.intersects(x,y,img.getWidth(),img.getHeight())){
                                return true;
                            }
                        }else if(VisitFilter.WITHIN.equals(filter)){
                            if(mask.contains(x,y,img.getWidth(),img.getHeight())){
                                return true;
                            }
                        }
                    }else{
                        if(maskArea==null) maskArea = JTS.shapeToGeometry(mask,GO2Utilities.JTS_FACTORY);
                        if(maskArea instanceof LinearRing) maskArea = GO2Utilities.JTS_FACTORY.createPolygon((LinearRing)maskArea);
                        final Rectangle2D rect = new Rectangle2D.Double(x, y, img.getWidth(), img.getHeight());
                        final AffineTransform trs = new AffineTransform();
                        trs.translate(-rect.getWidth()/2.0, -rect.getHeight()/2.0);
                        trs.rotate(imgRot);
                        trs.translate(+rect.getWidth()/2.0, +rect.getHeight()/2.0);
                        Geometry rotatedImg = JTS.shapeToGeometry(rect, GO2Utilities.JTS_FACTORY);
                        if(rotatedImg instanceof LinearRing) rotatedImg = GO2Utilities.JTS_FACTORY.createPolygon((LinearRing)rotatedImg);
                        if(VisitFilter.INTERSECTS.equals(filter)){
                            if(maskArea.intersects(rotatedImg)){
                                return true;
                            }
                        }else if(VisitFilter.WITHIN.equals(filter)){
                            if(maskArea.contains(rotatedImg)){
                                return true;
                            }
                        }

                    }

                }

            }else{
                //get most appropriate point
                final Point pt2d = GO2Utilities.getBestPoint(geom);
                Coordinate pcoord = pt2d.getCoordinate();
                if(Double.isNaN(pcoord.x)){
                    pcoord = geom.getCoordinate();
                }

                final int x = (int) (-img.getWidth()*anchor[0] + pcoord.x + disps[0]);
                final int y = (int) (-img.getHeight()*anchor[1] + pcoord.y - disps[1]);

                switch(filter){
                    case INTERSECTS :
                        if(mask.intersects(x,y,img.getWidth(),img.getHeight())){
                            //TODO should make a better test for the alpha pixel values in image
                            return true;
                        }
                        break;
                    case WITHIN :
                        if(mask.contains(x,y,img.getWidth(),img.getHeight())){
                            //TODO should make a better test for the alpha pixel values in image
                            return true;
                        }
                        break;
                }

            }
        }

        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedCoverage graphic, final SearchAreaJ2D mask, final VisitFilter filter) {
        return false;
    }
}
