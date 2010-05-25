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
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedPolygonSymbolizer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display.shape.TransformedShape;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedGraphicStroke;
import org.geotoolkit.display2d.style.CachedStroke;
import org.geotoolkit.display2d.style.CachedStrokeGraphic;
import org.geotoolkit.display2d.style.CachedStrokeSimple;
import org.geotoolkit.display2d.style.j2d.DefaultPathWalker;
import org.geotoolkit.display2d.style.j2d.PathWalker;

import org.opengis.feature.Feature;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPolygonSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedPolygonSymbolizer>{

    public DefaultPolygonSymbolizerRenderer(CachedPolygonSymbolizer symbol, RenderingContext2D context){
        super(symbol,context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(ProjectedFeature projectedFeature) throws PortrayalException{

        final Feature feature = projectedFeature.getFeature();

        //test if the symbol is visible on this feature
        if(symbol.isVisible(feature)){

            final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(symbol.getSource().getGeometryPropertyName());

            //symbolizer doesnt match the featuretype, no geometry found with this name.
            if(projectedGeometry == null) return;

            portray(projectedGeometry, feature);
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

    private void portray(ProjectedGeometry projectedGeometry, Feature feature) throws PortrayalException{

        final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
        final float coeff = renderingContext.getUnitCoefficient(symbolUnit);
        final float offset = symbol.getOffset(feature, coeff);
        final Shape shape;

        try {
            if(NonSI.PIXEL.equals(symbolUnit)){
                renderingContext.switchToDisplayCRS();
                shape = (offset != 0) ? bufferDisplayGeometry(renderingContext, projectedGeometry, offset)
                                      : projectedGeometry.getDisplayShape();
            }else{
                renderingContext.switchToObjectiveCRS();
                shape = (offset != 0) ? bufferObjectiveGeometry(renderingContext, projectedGeometry, symbolUnit, offset)
                                      : projectedGeometry.getObjectiveShape();
            }
        }catch (TransformException ex){
            throw new PortrayalException("Could not calculate projected geometry",ex);
        }

        if(shape == null){
            //no geometry, end here
            return;
        }

        //we apply the displacement ---------------------------------------
        final float[] disps = symbol.getDisplacement(feature);
        Point2D dispStep = null;
        if(disps[0] != 0 || disps[1] != 0){
            final AffineTransform inverse = renderingContext.getDisplayToObjective();
            dispStep = new Point2D.Float(disps[0], -disps[1]);
            dispStep = inverse.deltaTransform(dispStep, dispStep);
            g2d.translate(dispStep.getX(), dispStep.getY());
        }

        final float margin = symbol.getMargin(feature, coeff) /2f;
        final Rectangle2D bounds = shape.getBounds2D();
        final int x = (int) (bounds.getMinX() - margin);
        final int y = (int) (bounds.getMinY() - margin);

        g2d.setComposite( symbol.getJ2DFillComposite(feature) );
        g2d.setPaint( symbol.getJ2DFillPaint(feature, x, y,coeff, hints) );
        g2d.fill(shape);
        if(symbol.isStrokeVisible(feature)){
            final CachedStroke cachedStroke = symbol.getCachedStroke();
        if(cachedStroke instanceof CachedStrokeSimple){
            final CachedStrokeSimple cs = (CachedStrokeSimple)cachedStroke;
                g2d.setComposite(cs.getJ2DComposite(feature));
                g2d.setPaint(cs.getJ2DPaint(feature, x, y, coeff, hints));
                g2d.setStroke(cs.getJ2DStroke(feature,coeff));
                g2d.draw(shape);
            }else if(cachedStroke instanceof CachedStrokeGraphic){
                final CachedStrokeGraphic gc = (CachedStrokeGraphic)cachedStroke;
                final float initGap = gc.getInitialGap(feature);
                final Point2D pt = new Point2D.Double();
                final CachedGraphicStroke cgs = gc.getCachedGraphic();
                final Image img = cgs.getImage(feature, 1, hints);
                final float imgWidth = img.getWidth(null);
                final float imgHeight = img.getHeight(null);
                final float gap = gc.getGap(feature)+ imgWidth;
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
                    final float[] anchor = cgs.getAnchor(feature, null);
                    final float[] disp = cgs.getDisplacement(feature, null);
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
    public boolean hit(final ProjectedFeature projectedFeature, final SearchAreaJ2D search, final VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Shape mask = search.getDisplayShape();

        final Feature feature = projectedFeature.getFeature();

        //test if the symbol is visible on this feature
        if(!symbol.isVisible(feature)) return false;

        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(symbol.getSource().getGeometryPropertyName());

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return false;

        final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
        final float coeff = renderingContext.getUnitCoefficient(symbolUnit);
        final float offset = symbol.getOffset(feature, coeff);

        //we switch to  more appropriate context CRS for rendering -------------
        final Shape CRSShape;
        final Shape j2dShape;

        try{
            if(NonSI.PIXEL.equals(symbolUnit)){
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
        final float fillAlpha   = symbol.getJ2DFillComposite(feature).getAlpha();

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
    public boolean hit(ProjectedCoverage graphic, SearchAreaJ2D mask, VisitFilter filter) {
        return false;
    }

    /**
     * Recalculate objective geometry with the given offset,
     * for polygon this act like a buffer
     */
    private static Shape bufferObjectiveGeometry(RenderingContext2D context, ProjectedGeometry projectedFeature,
            Unit symbolUnit, float offset) throws TransformException{
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
    private static  Shape bufferDisplayGeometry(RenderingContext2D context, ProjectedGeometry projectedFeature,
            float offset) throws TransformException{
        final Shape shape;

        Geometry geom = projectedFeature.getDisplayGeometry();
        geom = geom.getBuffer(offset);
        shape = GO2Utilities.toJava2D(geom);

        return shape;
    }

}
