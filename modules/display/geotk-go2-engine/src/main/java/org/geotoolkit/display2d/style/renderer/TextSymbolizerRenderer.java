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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GraphicCoverageJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display.primitive.ReferencedGraphic.SearchArea;
import org.geotoolkit.display2d.style.CachedHalo;
import org.geotoolkit.display2d.style.CachedLabelPlacement;
import org.geotoolkit.display2d.style.CachedLinePlacement;
import org.geotoolkit.display2d.style.CachedPointPlacement;
import org.geotoolkit.display2d.style.CachedTextSymbolizer;
import org.geotoolkit.display2d.style.GO2Utilities;
import org.geotoolkit.display2d.style.renderer.DefaultLinearLabelDescriptor;
import org.geotoolkit.display2d.style.renderer.DefaultPointLabelDescriptor;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.display.shape.XRectangle2D;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.TextSymbolizer;

/**
 * @author Johann Sorel (Geomatys)
 */
public class TextSymbolizerRenderer implements SymbolizerRenderer<TextSymbolizer, CachedTextSymbolizer>{

    @Override
    public Class<TextSymbolizer> getSymbolizerClass() {
        return TextSymbolizer.class;
    }

    @Override
    public Class<CachedTextSymbolizer> getCachedSymbolizerClass() {
        return CachedTextSymbolizer.class;
    }

    @Override
    public CachedTextSymbolizer createCachedSymbolizer(TextSymbolizer symbol) {
        return new CachedTextSymbolizer(symbol);
    }

    @Override
    public void portray(ProjectedFeature projectedFeature, CachedTextSymbolizer symbol, RenderingContext2D context) throws PortrayalException{
        
        final Feature feature = projectedFeature.getFeature();

        //test if the symbol is visible on this feature
        if(symbol.isVisible(feature)){
            final Graphics2D g2 = context.getGraphics();
            final RenderingHints hints = g2.getRenderingHints();

            final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
            final Geometry geom;
            try {
                geom = projectedFeature.getDisplayGeometry();
            } catch (TransformException ex) {
                throw new PortrayalException(ex);
            }

            //we switch to  more appropriate context CRS for rendering ---------
            // a point symbol always paint in display unit

            context.switchToDisplayCRS();

            //we adjust coefficient for rendering ------------------------------
            float coeff = 1;
            if(symbolUnit.equals(NonSI.PIXEL)){
                //symbol is in display unit
                coeff = 1;
            }else{
                //we have a special unit we must adjust the coefficient
                coeff = context.getUnitCoefficient(symbolUnit);
                // calculate scale difference between objective and display
                try{
                    final AffineTransform inverse = context.getAffineTransform(context.getObjectiveCRS(), context.getDisplayCRS());
                    coeff *= Math.abs(XAffineTransform.getScale(inverse));
                }catch(FactoryException ex){
                    throw new PortrayalException(ex);
                }
            }


            //strat to extract label parameters---------------------------------
            final String label = symbol.getLabel(feature).trim();
            if(label.isEmpty()) return; //nothing to paint
            final CachedHalo halo = symbol.getHalo();
            final CachedLabelPlacement placement = symbol.getPlacement();
            final LabelRenderer renderer = context.getLabelRenderer(true);

            //extract halo parameters
            final float haloWidth;
            final Paint haloPaint;
            if(halo != null){
                haloWidth = halo.getWidth(feature);
                haloPaint = halo.getJ2DPaint(feature, 0, 0, hints);
            }else{
                haloWidth = 0;
                haloPaint = Color.WHITE;
            }

            //extract text parameters
            final Paint fontPaint = symbol.getFontPaint(feature, 0,0, coeff, hints);
            final Font j2dFont = symbol.getJ2dFont(feature, coeff);

            if(geom instanceof Point || geom instanceof MultiPoint){

                final Coordinate[] coords = geom.getCoordinates();
                final Coordinate coord = coords[0];
                DirectPosition pt2d = new DirectPosition2D(coord.x, coord.y);

                final int x = (int) (pt2d.getOrdinate(0) );
                final int y = (int) (pt2d.getOrdinate(1) );

                final float anchorX;
                final float anchorY;
                final float dispX;
                final float dispY;
                final float rotation;
                if(placement instanceof CachedPointPlacement){
                    final CachedPointPlacement pp = (CachedPointPlacement) placement;
                    anchorX = pp.getAnchorX(feature);
                    anchorY = pp.getAnchorY(feature);
                    dispX = pp.getDisplacementX(feature);
                    dispY = pp.getDisplacementY(feature);
                    rotation = pp.getRotation(feature);
                }else if(placement instanceof CachedLinePlacement){
                    //Using a line placement on points, strange but can happen
                    //we replace by the default point placement parameters.
                    anchorX = 0.5f;
                    anchorY = 0.5f;
                    dispX = 0f;
                    dispY = 0f;
                    rotation = 0f;
                }else{
                    throw new PortrayalException("Text symbolizer has no label placement, this should not be possible.");
                }

                final LabelDescriptor descriptor = new DefaultPointLabelDescriptor(
                    label, j2dFont, fontPaint,
                    haloWidth, haloPaint,
                    x, y,
                    anchorX, anchorY,
                    dispX, dispY,
                    rotation, context.getDisplayCRS());
                renderer.append(descriptor);

            }else if( geom instanceof LineString || geom instanceof MultiLineString
                    || geom instanceof Polygon || geom instanceof MultiPolygon ){


                if(placement instanceof CachedPointPlacement){
                    final CachedPointPlacement pp = (CachedPointPlacement) placement;

                    final Coordinate[] coords = geom.getCentroid().getCoordinates();
                    final Coordinate coord = coords[0];
                    DirectPosition pt2d = new DirectPosition2D(coord.x, coord.y);

                    final int x = (int) (pt2d.getOrdinate(0) );
                    final int y = (int) (pt2d.getOrdinate(1) );

                    float anchorX = pp.getAnchorX(feature);
                    float anchorY = pp.getAnchorY(feature);
                    float dispX = pp.getDisplacementX(feature);
                    float dispY = pp.getDisplacementY(feature);
                    float rotation = pp.getRotation(feature);

                    final LabelDescriptor descriptor = new DefaultPointLabelDescriptor(
                        label, j2dFont, fontPaint,
                        haloWidth, haloPaint,
                        x, y,
                        anchorX, anchorY,
                        dispX, dispY,
                        rotation, context.getDisplayCRS());
                    renderer.append(descriptor);

                }else if(placement instanceof CachedLinePlacement){
                    //Using a line placement on points, strange but can happen
                    //we replace by the default point placement parameters.
                    final CachedLinePlacement lp = (CachedLinePlacement) placement;

                    //we need the reprojected shape since the text symbol will need
                    //the path
                    Shape j2dShape = GO2Utilities.createShape(geom);

                    final LabelDescriptor descriptor = new DefaultLinearLabelDescriptor(
                            label,
                            j2dFont,
                            fontPaint,
                            haloWidth,
                            haloPaint,
                            lp.getGap(feature),
                            lp.getInitialGap(feature),
                            lp.getOffset(feature),
                            lp.isRepeated(),
                            lp.isAligned(),
                            lp.isGeneralizeLine(),
                            j2dShape);
                    renderer.append(descriptor);
                }else{
                    throw new PortrayalException("Text symbolizer has no label placement, this should not be possible.");
                }

            }

        }

    }

    @Override
    public void portray(final GraphicCoverageJ2D graphic, CachedTextSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException{
        //nothing to portray
    }

    @Override
    public boolean hit(ProjectedFeature feature, CachedTextSymbolizer symbol, 
            RenderingContext2D context, SearchArea mask, VisitFilter filter) {
        //text symbolizer are not hittable
        return false;
    }

    @Override
    public boolean hit(GraphicCoverageJ2D graphic, CachedTextSymbolizer symbol, 
            RenderingContext2D renderingContext, SearchArea mask, VisitFilter filter) {
        return false;
    }

    @Override
    public Rectangle2D estimate(ProjectedFeature feature, CachedTextSymbolizer symbol,
            RenderingContext2D context, Rectangle2D rect) {
        //text symbolizer are not hittable
        return XRectangle2D.INFINITY;
    }

    @Override
    public Rectangle2D glyphPreferredSize(CachedTextSymbolizer symbol) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rectangle, CachedTextSymbolizer symbol) {
        g.setClip(rectangle);

        final String family;
        if(!symbol.getSource().getFont().getFamily().isEmpty()){
            family = symbol.getSource().getFont().getFamily().get(0).toString();
        }else{
            family = "Dialog";
        }

        final Font font = new Font(family, Font.PLAIN, (int)rectangle.getHeight()/2);
        final FontRenderContext frc = g.getFontRenderContext();
        final GlyphVector glyphVector = font.createGlyphVector(frc, "T");
        final Shape shape = glyphVector.getOutline();

        g.translate(rectangle.getMinX()+3, rectangle.getMaxY()-3);

        if(symbol.getHalo() != null){

            Paint paint = null;
            float width = 1;

            if(GO2Utilities.isStatic(symbol.getSource().getHalo().getFill().getColor())){
                paint = symbol.getSource().getHalo().getFill().getColor().evaluate(null, Color.class);
            }

            if(paint == null){
                paint = Color.WHITE;
            }

            final Expression expWidth = symbol.getSource().getHalo().getRadius();
            if(GO2Utilities.isStatic(expWidth)){
                width = expWidth.evaluate(null, Float.class);
            }else{
                width = 1;
            }

            if(width > 0){
                g.setStroke(new BasicStroke(width*2+1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setPaint(paint);
                g.draw(shape);
            }
        }
    }

}
