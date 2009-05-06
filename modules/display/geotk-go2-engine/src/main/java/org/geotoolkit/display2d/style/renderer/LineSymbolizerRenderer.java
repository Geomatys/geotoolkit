/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2009, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.display2d.style.renderer;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GraphicCoverageJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display.primitive.ReferencedGraphic.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedLineSymbolizer;
import org.geotoolkit.display2d.style.GO2Utilities;

import org.geotoolkit.display.shape.TransformedShape;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.LineSymbolizer;

/**
 * @author Johann Sorel (Geomatys)
 */
public class LineSymbolizerRenderer extends AbstractSymbolizerRenderer<LineSymbolizer, CachedLineSymbolizer>{

    @Override
    public Class<LineSymbolizer> getSymbolizerClass() {
        return LineSymbolizer.class;
    }

    @Override
    public Class<CachedLineSymbolizer> getCachedSymbolizerClass() {
        return CachedLineSymbolizer.class;
    }

    @Override
    public CachedLineSymbolizer createCachedSymbolizer(LineSymbolizer symbol) {
        return new CachedLineSymbolizer(symbol);
    }

    @Override
    public void portray(ProjectedFeature projectedFeature, CachedLineSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException{

        final Feature feature = projectedFeature.getFeature();

        //test if the symbol is visible on this feature
        if(symbol.isVisible(feature)){
            final Graphics2D g2 = context.getGraphics();
            final RenderingHints hints = g2.getRenderingHints();

            final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
            final float coeff = context.getUnitCoefficient(symbolUnit);
            final Shape j2dShape;

            if(NonSI.PIXEL == symbolUnit){
                context.switchToDisplayCRS();
                try {
                    j2dShape = projectedFeature.getDisplayShape();
                } catch (TransformException ex) {
                    throw new PortrayalException(ex);
                }
            }else{
                context.switchToObjectiveCRS();
                try {
                    j2dShape = projectedFeature.getObjectiveShape();
                } catch (TransformException ex) {
                    throw new PortrayalException(ex);
                }
            }

            final float margin = symbol.getMargin(feature, coeff) /2f;
            final Rectangle2D bounds = j2dShape.getBounds2D();
            final int x = (int) (bounds.getMinX() - margin);
            final int y = (int) (bounds.getMinY() - margin);

            final float offset = symbol.getOffset(feature, coeff);
            if(offset != 0){
                g2.translate(offset, 0);
                g2.setComposite(symbol.getJ2DComposite(feature));
                g2.setPaint(symbol.getJ2DPaint(feature, x, y, coeff, hints));
                g2.setStroke(symbol.getJ2DStroke(feature,coeff));
                g2.draw(j2dShape);
                g2.translate(-offset, 0);
            }else{
                g2.setComposite(symbol.getJ2DComposite(feature));
                g2.setPaint(symbol.getJ2DPaint(feature, x, y, coeff, hints));
                g2.setStroke(symbol.getJ2DStroke(feature,coeff));
                g2.draw(j2dShape);
            }

        }

    }

    @Override
    public void portray(final GraphicCoverageJ2D graphic, CachedLineSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException{
        //nothing to portray
    }



    @Override
    public boolean hit(final ProjectedFeature graphic, final CachedLineSymbolizer symbol,
            RenderingContext2D context, SearchArea search, VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Geometry mask = search.displayGeometry;

        final SimpleFeature feature = graphic.getFeature();

        //test if the symbol is visible on this feature
        if(!(symbol.isVisible(feature))) return false;

        //Test composites ------------------------------------------------------
        final float strokeAlpha = symbol.getJ2DComposite(feature).getAlpha();
        if(strokeAlpha < GO2Utilities.SELECTION_LOWER_ALPHA){
            //feature graphic is translucide, not selectable
            return false;
        }

        final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
        final float coeff = context.getUnitCoefficient(symbolUnit);

        

        if(NonSI.PIXEL.equals(symbolUnit)){

            final Geometry j2dShape;

            try {
                j2dShape = graphic.getDisplayGeometry();
            } catch (TransformException ex) {
                ex.printStackTrace();
                return false;
            }

            final int bufferWidth = (int) symbol.getStrokeWidth(feature);

            //test envelopes first
            Geometry CRSShape = mask.getEnvelope();
            CRSShape = mask.buffer(bufferWidth,1);
            boolean hit = testHit(filter,j2dShape.getEnvelope(),CRSShape);

            if(!hit) return false;

            //test real shape
            CRSShape = mask.buffer(bufferWidth,1);
            return testHit(filter,j2dShape,CRSShape);

        }else{            
            final Shape j2dShape;
            final Shape CRSShape;
            try {
                j2dShape = graphic.getObjectiveShape();
                CRSShape = new TransformedShape();
                ((TransformedShape)CRSShape).setTransform(
                        context.getAffineTransform(context.getDisplayCRS(), context.getObjectiveCRS()));
                ((TransformedShape)CRSShape).setOriginalShape(search.displayShape);
            } catch (TransformException ex) {
                ex.printStackTrace();
                return false;
            } catch (FactoryException ex) {
                ex.printStackTrace();
                return false;
            }

            final java.awt.Stroke stroke = symbol.getJ2DStroke(feature,coeff);
            final Area area = new Area(stroke.createStrokedShape(j2dShape));

            switch(filter){
                case INTERSECTS :
                    area.intersect(new Area(CRSShape));
                    return !area.isEmpty();
                case WITHIN :
                    Area start = new Area(area);
                    area.add(new Area(CRSShape));
                    return start.equals(area);
            }


        }

        return false;
    }

    private boolean testHit(VisitFilter filter, Geometry j2dShape, Geometry CRSShape){

        switch(filter){
            case INTERSECTS :
                return j2dShape.intersects(CRSShape);
            case WITHIN :
                return j2dShape.within(CRSShape);
        }

        return false;
    }


    @Override
    public Rectangle2D estimate(final ProjectedFeature graphic, final CachedLineSymbolizer symbol,
            final RenderingContext2D context, Rectangle2D rect) {

        final SimpleFeature feature = graphic.getFeature();

        //test if the symbol is visible on this feature
        if(!(symbol.isVisible(feature))){
            //return a rectangle outside the rendering area.
            if(rect == null) rect = new Rectangle(-2, -2, 0, 0);
            return rect;
        }

        final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
        Geometry geom = GO2Utilities.getGeometry(feature, symbol.getSource().getGeometryPropertyName());
        geom = geom.getEnvelope();

        final float coeff = context.getUnitCoefficient(symbolUnit);
        final Shape j2dShape;

        if(NonSI.PIXEL.equals(symbolUnit)){
            context.switchToDisplayCRS();
            try {
                j2dShape = graphic.getDisplayShape();
            } catch (TransformException ex) {
                ex.printStackTrace();
                return rect;
            }
        }else{
            context.switchToObjectiveCRS();
            try {
                j2dShape = graphic.getObjectiveShape();
            } catch (TransformException ex) {
                ex.printStackTrace();
                return rect;
            }
        }


        final java.awt.Stroke stroke = symbol.getJ2DStroke(feature,coeff);

        if(rect == null){
            return stroke.createStrokedShape(j2dShape).getBounds2D();
        }else{
            rect.add(stroke.createStrokedShape(j2dShape).getBounds2D());
            return rect;
        }
    }

    @Override
    public boolean hit(GraphicCoverageJ2D graphic, CachedLineSymbolizer symbol, 
            RenderingContext2D renderingContext, SearchArea mask, VisitFilter filter) {
        return false;
    }

    @Override
    public Rectangle2D glyphPreferredSize(CachedLineSymbolizer symbol) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rectangle, CachedLineSymbolizer symbol) {
        final AffineTransform affine = new AffineTransform(rectangle.getWidth(), 0, 0,
                rectangle.getHeight(), rectangle.getX(), rectangle.getY());

        g.setClip(rectangle);
        final TransformedShape shape = new TransformedShape();
        shape.setOriginalShape(LINE);
        shape.setTransform(affine);

        renderStroke(shape, symbol.getSource().getStroke(), symbol.getSource().getUnitOfMeasure(), g);
    }
    
}
