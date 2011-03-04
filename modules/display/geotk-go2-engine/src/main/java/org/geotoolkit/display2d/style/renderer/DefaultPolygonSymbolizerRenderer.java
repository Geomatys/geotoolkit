/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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

import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedPolygonSymbolizer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display.shape.TransformedShape;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedGraphicStroke;
import org.geotoolkit.display2d.style.CachedStroke;
import org.geotoolkit.display2d.style.CachedStrokeGraphic;
import org.geotoolkit.display2d.style.CachedStrokeSimple;
import org.geotoolkit.display2d.style.j2d.DefaultPathWalker;
import org.geotoolkit.display2d.style.j2d.PathWalker;

import org.opengis.geometry.Geometry;
import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPolygonSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedPolygonSymbolizer>{

    public DefaultPolygonSymbolizerRenderer(final CachedPolygonSymbolizer symbol, final RenderingContext2D context){
        super(symbol,context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedObject projectedFeature) throws PortrayalException{

        final Object candidate = projectedFeature.getCandidate();

        //test if the symbol is visible on this feature
        if(symbol.isVisible(candidate)){

            final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(geomPropertyName);

            //symbolizer doesnt match the featuretype, no geometry found with this name.
            if(projectedGeometry == null) return;

            portray(projectedGeometry, candidate);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedCoverage projectedCoverage) throws PortrayalException{
        //portray the border of the coverage
        final ProjectedGeometry projectedGeometry = projectedCoverage.getEnvelopeGeometry();

        //could not find the border geometry
        if(projectedGeometry == null) return;

        portray(projectedGeometry, null);
    }

    private void portray(final ProjectedGeometry projectedGeometry, final Object candidate) throws PortrayalException{

        final float offset = symbol.getOffset(candidate, coeff);
        final Shape shape;

        //calculate displacement
        final float[] disps = symbol.getDisplacement(candidate);
        Point2D dispStep = null;
        if(disps[0] != 0 || disps[1] != 0){
            dispStep = new Point2D.Float(disps[0], -disps[1]);
        }

        try {
            if(dispGeom){
                renderingContext.switchToDisplayCRS();
                shape = (offset != 0) ? bufferDisplayGeometry(renderingContext, projectedGeometry, offset)
                                      : projectedGeometry.getDisplayShape();
            }else{
                renderingContext.switchToObjectiveCRS();
                shape = (offset != 0) ? bufferObjectiveGeometry(renderingContext, projectedGeometry, symbolUnit, offset)
                                      : projectedGeometry.getObjectiveShape();
                
                //adjust displacement, displacement is expressed in pixel units
                final AffineTransform inverse = renderingContext.getDisplayToObjective();
                dispStep = inverse.deltaTransform(dispStep, dispStep);
            }
        }catch (TransformException ex){
            throw new PortrayalException("Could not calculate projected geometry",ex);
        }

        if(shape == null){
            //no geometry, end here
            return;
        }

        //we apply the displacement ---------------------------------------        
        if(dispStep != null){
            g2d.translate(dispStep.getX(), dispStep.getY());
        }

        final float margin = symbol.getMargin(candidate, coeff) /2f;
        final Rectangle2D bounds = shape.getBounds2D();
        final int x = (int) (bounds.getMinX() - margin);
        final int y = (int) (bounds.getMinY() - margin);

        if(symbol.isFillVisible(candidate)){
            g2d.setComposite( symbol.getJ2DFillComposite(candidate) );
            g2d.setPaint( symbol.getJ2DFillPaint(candidate, x, y,coeff, hints) );
            g2d.fill(shape);
        }
        
        if(symbol.isStrokeVisible(candidate)){
            final CachedStroke cachedStroke = symbol.getCachedStroke();
            if(cachedStroke instanceof CachedStrokeSimple){
            final CachedStrokeSimple cs = (CachedStrokeSimple)cachedStroke;
                g2d.setComposite(cs.getJ2DComposite(candidate));
                g2d.setPaint(cs.getJ2DPaint(candidate, x, y, coeff, hints));
                g2d.setStroke(cs.getJ2DStroke(candidate,coeff));
                g2d.draw(shape);
            }else if(cachedStroke instanceof CachedStrokeGraphic){
                final CachedStrokeGraphic gc = (CachedStrokeGraphic)cachedStroke;
                final float initGap = gc.getInitialGap(candidate);
                final Point2D pt = new Point2D.Double();
                final CachedGraphicStroke cgs = gc.getCachedGraphic();
                final Image img = cgs.getImage(candidate, 1, hints);
                final float imgWidth = img.getWidth(null);
                final float imgHeight = img.getHeight(null);
                final float gap = gc.getGap(candidate)+ imgWidth;
                final AffineTransform trs = new AffineTransform();

                final PathIterator ite = shape.getPathIterator(null);
                final PathWalker walker = new DefaultPathWalker(ite);
                walker.walk(initGap);
                while(!walker.isFinished()){
                    //paint the motif --------------------------------------------------
                    walker.getPosition(pt);
                    final float angle = walker.getRotation();
                    trs.setToTranslation(pt.getX(), pt.getY());
                    trs.rotate(angle);
                    final float[] anchor = cgs.getAnchor(candidate, null);
                    final float[] disp = cgs.getDisplacement(candidate, null);
                    trs.translate(-imgWidth*anchor[0], -imgHeight*anchor[1]);
                    trs.translate(disp[0], -disp[1]);

                    g2d.drawImage(img, trs, null);

                    //walk over the gap ------------------------------------------------
                    walker.walk(gap);
                }
            }
        }

        //restore the displacement
        if(dispStep != null){
            g2d.translate(-dispStep.getX(), -dispStep.getY());
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedObject projectedFeature, final SearchAreaJ2D search, final VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Shape mask = search.getDisplayShape();

        final Object candidate = projectedFeature.getCandidate();

        //test if the symbol is visible on this feature
        if(!symbol.isVisible(candidate)) return false;

        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(geomPropertyName);

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return false;

        final float offset = symbol.getOffset(candidate, coeff);

        //we switch to  more appropriate context CRS for rendering -------------
        final Shape CRSShape;
        final Shape j2dShape;

        try{
            if(dispGeom){
                CRSShape = mask;

                j2dShape = (offset != 0) ? bufferDisplayGeometry(renderingContext, projectedGeometry, offset)
                                         : projectedGeometry.getDisplayShape();
            }else{
                try{
                    CRSShape = new TransformedShape();
                    ((TransformedShape)CRSShape).setTransform(renderingContext.getAffineTransform(renderingContext.getDisplayCRS(), renderingContext.getObjectiveCRS()));
                    ((TransformedShape)CRSShape).setOriginalShape(mask);
                }catch(FactoryException ex){
                    ex.printStackTrace();
                    return false;
                }

                j2dShape = (offset != 0) ? bufferObjectiveGeometry(renderingContext, projectedGeometry, symbolUnit, offset)
                                         : projectedGeometry.getObjectiveShape();
            }
        }catch (TransformException ex) {
            ex.printStackTrace();
            return false;
        }


        //we apply the displacement --------------------------------------------
        //TODO handle displacement
//        final float[] disps = symbol.getDisplacement(feature);
//        final Point2D displacedPoint = new Point2D.Float((float)mask.getX() - disps[0], (float)mask.getY() + disps[1] );


        //Test composites ------------------------------------------------------
        final float fillAlpha   = symbol.getJ2DFillComposite(candidate).getAlpha();

        //todo must hanlde graphic stroke
        //final float strokeAlpha = symbol.getJ2DStrokeComposite(feature).getAlpha();

        final Area area ;
        if(fillAlpha >= GO2Utilities.SELECTION_LOWER_ALPHA){
            area = new Area(j2dShape);
//            if(strokeAlpha >= GO2Utilities.SELECTION_LOWER_ALPHA){
//                final java.awt.Stroke stroke = symbol.getJ2DStroke(feature,coeff);
//                area.add( new Area(stroke.createStrokedShape(j2dShape) ));
//            }
        }
//        else if(strokeAlpha >= GO2Utilities.SELECTION_LOWER_ALPHA){
//            final java.awt.Stroke stroke = symbol.getJ2DStroke(feature,coeff);
//            area = new Area(stroke.createStrokedShape(j2dShape));
//        }
        else{
            //feature graphic is translucide, not selectable
            return false;
        }

        switch(filter){
            case INTERSECTS :
                area.intersect(new Area(CRSShape));
                return !area.isEmpty();
            case WITHIN :
                Area start = new Area(area);
                area.add(new Area(CRSShape));
                return start.equals(area);
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

    /**
     * Recalculate objective geometry with the given offset,
     * for polygon this act like a buffer
     */
    private static Shape bufferObjectiveGeometry(final RenderingContext2D context, final ProjectedGeometry projectedFeature,
            final Unit symbolUnit, final float offset) throws TransformException{
        final Shape shape;

        //TODO use symbol unit to adjust offset
        Geometry geom = projectedFeature.getObjectiveGeometry();
        geom = geom.getBuffer(offset);
        shape = GO2Utilities.toJava2D(geom);
        
        return shape;
    }

    /**
     * Recalculate display geometry with the given offset,
     * for polygon this act like a buffer
     */
    private static  Shape bufferDisplayGeometry(final RenderingContext2D context, final ProjectedGeometry projectedFeature,
            final float offset) throws TransformException{
        final Shape shape;

        Geometry geom = projectedFeature.getDisplayGeometry();
        try{
            geom = geom.getBuffer(offset);
        }catch(IllegalArgumentException ex){
            //can happen if the geometry has too few points, like a ring of 3points
        }
        shape = GO2Utilities.toJava2D(geom);

        return shape;
    }

}
