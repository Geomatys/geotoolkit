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
import java.awt.geom.Rectangle2D;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedLineSymbolizer;
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
import org.opengis.style.LineSymbolizer;

/**
 * @author Johann Sorel (Geomatys)
 */
public class DefaultLineSymbolizerRenderer extends AbstractSymbolizerRenderer<LineSymbolizer, CachedLineSymbolizer>{

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<LineSymbolizer> getSymbolizerClass() {
        return LineSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<CachedLineSymbolizer> getCachedSymbolizerClass() {
        return CachedLineSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CachedLineSymbolizer createCachedSymbolizer(LineSymbolizer symbol) {
        return new CachedLineSymbolizer(symbol);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(ProjectedFeature projectedFeature, CachedLineSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException{

        final Feature feature = projectedFeature.getFeature();
        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(symbol.getSource().getGeometryPropertyName());

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return;

        //test if the symbol is visible on this feature
        if(symbol.isVisible(feature)){
            portray(context, symbol, projectedGeometry, feature);
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedCoverage projectedCoverage, CachedLineSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException{
        //portray the border of the coverage
        final ProjectedGeometry projectedGeometry = projectedCoverage.getEnvelopeGeometry();

        //could not find the border geometry
        if(projectedGeometry == null) return;

        portray(context, symbol, projectedGeometry, null);
    }

    private static void portray(RenderingContext2D context, CachedLineSymbolizer symbol,
            ProjectedGeometry projectedGeometry, Feature feature) throws PortrayalException{

        final Graphics2D g2 = context.getGraphics();
        final RenderingHints hints = g2.getRenderingHints();

        final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
        final float coeff = context.getUnitCoefficient(symbolUnit);
        final Shape j2dShape;

        if(NonSI.PIXEL == symbolUnit){
            context.switchToDisplayCRS();
            try {
                j2dShape = projectedGeometry.getDisplayShape();
            } catch (TransformException ex) {
                throw new PortrayalException(ex);
            }
        }else{
            context.switchToObjectiveCRS();
            try {
                j2dShape = projectedGeometry.getObjectiveShape();
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

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedFeature projectedFeature, final CachedLineSymbolizer symbol,
            RenderingContext2D context, SearchAreaJ2D search, VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Geometry mask = search.getDisplayGeometry();

        final SimpleFeature feature = projectedFeature.getFeature();

        //test if the symbol is visible on this feature
        if(!(symbol.isVisible(feature))) return false;

        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(symbol.getSource().getGeometryPropertyName());

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return false;

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
                j2dShape = projectedGeometry.getDisplayGeometry();
            } catch (TransformException ex) {
                ex.printStackTrace();
                return false;
            }

            final int bufferWidth = (int) symbol.getStrokeWidth(feature);

            //test envelopes first
//            Geometry CRSShape = mask.getEnvelope();
//            CRSShape = mask.buffer(bufferWidth,1);
//            boolean hit = testHit(filter,CRSShape,j2dShape.getEnvelope());

//            if(!hit) return false;

            //test real shape
            Geometry CRSShape = mask.getBuffer(bufferWidth);
            return GO2Utilities.testHit(filter,CRSShape,j2dShape);

        }else{            
            final Shape j2dShape;
            final Shape CRSShape;
            try {
                j2dShape = projectedGeometry.getObjectiveShape();
                CRSShape = new TransformedShape();
                ((TransformedShape)CRSShape).setTransform(
                        context.getAffineTransform(context.getDisplayCRS(), context.getObjectiveCRS()));
                ((TransformedShape)CRSShape).setOriginalShape(search.getDisplayShape());
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

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(ProjectedCoverage graphic, CachedLineSymbolizer symbol,
            RenderingContext2D renderingContext, SearchAreaJ2D mask, VisitFilter filter) {
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Rectangle2D glyphPreferredSize(CachedLineSymbolizer symbol) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
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
