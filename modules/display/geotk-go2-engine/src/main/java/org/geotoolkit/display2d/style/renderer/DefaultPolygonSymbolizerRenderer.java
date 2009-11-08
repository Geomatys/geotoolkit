/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
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

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.PolygonSymbolizer;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPolygonSymbolizerRenderer extends AbstractSymbolizerRenderer<PolygonSymbolizer, CachedPolygonSymbolizer>{

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<PolygonSymbolizer> getSymbolizerClass() {
        return PolygonSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<CachedPolygonSymbolizer> getCachedSymbolizerClass() {
        return CachedPolygonSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CachedPolygonSymbolizer createCachedSymbolizer(PolygonSymbolizer symbol) {
        return new CachedPolygonSymbolizer(symbol);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(ProjectedFeature projectedFeature, CachedPolygonSymbolizer symbol, RenderingContext2D context) throws PortrayalException{

        final Feature feature = projectedFeature.getFeature();

        //test if the symbol is visible on this feature
        if(symbol.isVisible(feature)){

            final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(symbol.getSource().getGeometryPropertyName());

            //symbolizer doesnt match the featuretype, no geometry found with this name.
            if(projectedGeometry == null) return;

            portray(context, symbol, projectedGeometry, feature);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedCoverage projectedCoverage, CachedPolygonSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException{
        //portray the border of the coverage
        final ProjectedGeometry projectedGeometry = projectedCoverage.getEnvelopeGeometry();

        //could not find the border geometry
        if(projectedGeometry == null) return;

        portray(context, symbol, projectedGeometry, null);
    }

    private static void portray(RenderingContext2D context, CachedPolygonSymbolizer symbol,
            ProjectedGeometry projectedGeometry, Feature feature) throws PortrayalException{

        final Graphics2D g2 = context.getGraphics();
        final RenderingHints hints = g2.getRenderingHints();
        final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
        final float coeff = context.getUnitCoefficient(symbolUnit);
        final float offset = symbol.getOffset(feature, coeff);
        final Shape shape;

        try {
            if(NonSI.PIXEL.equals(symbolUnit)){
                context.switchToDisplayCRS();
                shape = (offset != 0) ? bufferDisplayGeometry(context, projectedGeometry, offset)
                                      : projectedGeometry.getDisplayShape();
            }else{
                context.switchToObjectiveCRS();
                shape = (offset != 0) ? bufferObjectiveGeometry(context, projectedGeometry, symbolUnit, offset)
                                      : projectedGeometry.getObjectiveShape();
            }
        }catch (TransformException ex){
            throw new PortrayalException("Could not calculate projected geometry",ex);
        }


        //we apply the displacement ---------------------------------------
        final float[] disps = symbol.getDisplacement(feature);
        Point2D dispStep = null;
        if(disps[0] != 0 || disps[1] != 0){
            final AffineTransform inverse = context.getDisplayToObjective();
            dispStep = new Point2D.Float(disps[0], -disps[1]);
            dispStep = inverse.deltaTransform(dispStep, dispStep);
            g2.translate(dispStep.getX(), dispStep.getY());
        }

        final float margin = symbol.getMargin(feature, coeff) /2f;
        final Rectangle2D bounds = shape.getBounds2D();
        final int x = (int) (bounds.getMinX() - margin);
        final int y = (int) (bounds.getMinY() - margin);

        g2.setComposite( symbol.getJ2DFillComposite(feature) );
        g2.setPaint( symbol.getJ2DFillPaint(feature, x, y,coeff, hints) );
        g2.fill(shape);
        if(symbol.isStrokeVisible(feature)){
            g2.setComposite( symbol.getJ2DStrokeComposite(feature) );
            g2.setPaint( symbol.getJ2DStrokePaint(feature, x, y, coeff, hints) );
            g2.setStroke( symbol.getJ2DStroke(feature,coeff) );
            g2.draw(shape);
        }

        //restore the displacement
        if(dispStep != null){
            g2.translate(-dispStep.getX(), -dispStep.getY());
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedFeature projectedFeature, final CachedPolygonSymbolizer symbol,
            final RenderingContext2D context, final SearchAreaJ2D search, final VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Shape mask = search.getDisplayShape();

        final SimpleFeature feature = projectedFeature.getFeature();

        //test if the symbol is visible on this feature
        if(!symbol.isVisible(feature)) return false;

        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(symbol.getSource().getGeometryPropertyName());

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return false;

        final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
        final float coeff = context.getUnitCoefficient(symbolUnit);
        final float offset = symbol.getOffset(feature, coeff);

        //we switch to  more appropriate context CRS for rendering -------------
        final Shape CRSShape;
        final Shape j2dShape;

        try{
            if(NonSI.PIXEL.equals(symbolUnit)){
                CRSShape = mask;

                j2dShape = (offset != 0) ? bufferDisplayGeometry(context, projectedGeometry, offset)
                                         : projectedGeometry.getDisplayShape();
            }else{
                try{
                    CRSShape = new TransformedShape();
                    ((TransformedShape)CRSShape).setTransform(context.getAffineTransform(context.getDisplayCRS(), context.getObjectiveCRS()));
                    ((TransformedShape)CRSShape).setOriginalShape(mask);
                }catch(FactoryException ex){
                    ex.printStackTrace();
                    return false;
                }

                j2dShape = (offset != 0) ? bufferObjectiveGeometry(context, projectedGeometry, symbolUnit, offset)
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
        final float strokeAlpha = symbol.getJ2DStrokeComposite(feature).getAlpha();

        final Area area ;
        if(fillAlpha >= GO2Utilities.SELECTION_LOWER_ALPHA){
            area = new Area(j2dShape);
            if(strokeAlpha >= GO2Utilities.SELECTION_LOWER_ALPHA){
                final java.awt.Stroke stroke = symbol.getJ2DStroke(feature,coeff);
                area.add( new Area(stroke.createStrokedShape(j2dShape) ));
            }
        }else if(strokeAlpha >= GO2Utilities.SELECTION_LOWER_ALPHA){
            final java.awt.Stroke stroke = symbol.getJ2DStroke(feature,coeff);
            area = new Area(stroke.createStrokedShape(j2dShape));
        }else{
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
    public boolean hit(ProjectedCoverage graphic, CachedPolygonSymbolizer symbol,
            RenderingContext2D renderingContext, SearchAreaJ2D mask, VisitFilter filter) {
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Rectangle2D glyphPreferredSize(CachedPolygonSymbolizer symbol) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void glyph(Graphics2D g, Rectangle2D rectangle, CachedPolygonSymbolizer symbol) {
        final AffineTransform affine = new AffineTransform(rectangle.getWidth(), 0, 0,
                rectangle.getHeight(), rectangle.getX(), rectangle.getY());

        g.setClip(rectangle);
        final TransformedShape shape = new TransformedShape();
        shape.setOriginalShape(POLYGON);
        shape.setTransform(affine);

        renderFill(shape, symbol.getSource().getFill(), g);
        renderStroke(shape, symbol.getSource().getStroke(), symbol.getSource().getUnitOfMeasure(), g);
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
