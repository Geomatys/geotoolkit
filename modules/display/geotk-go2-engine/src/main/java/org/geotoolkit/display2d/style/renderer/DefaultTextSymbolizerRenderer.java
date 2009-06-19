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
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedHalo;
import org.geotoolkit.display2d.style.CachedLabelPlacement;
import org.geotoolkit.display2d.style.CachedLinePlacement;
import org.geotoolkit.display2d.style.CachedPointPlacement;
import org.geotoolkit.display2d.style.CachedTextSymbolizer;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;
import org.geotoolkit.display2d.style.labeling.DefaultPointLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.LabelDescriptor;
import org.geotoolkit.display2d.style.labeling.DefaultLinearLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.DefaultLabelLayer;
import org.geotoolkit.display2d.style.labeling.LabelLayer;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;

import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.FactoryException;
import org.opengis.style.TextSymbolizer;

/**
 * @author Johann Sorel (Geomatys)
 */
public class DefaultTextSymbolizerRenderer implements SymbolizerRenderer<TextSymbolizer, CachedTextSymbolizer>{

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<TextSymbolizer> getSymbolizerClass() {
        return TextSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<CachedTextSymbolizer> getCachedSymbolizerClass() {
        return CachedTextSymbolizer.class;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CachedTextSymbolizer createCachedSymbolizer(TextSymbolizer symbol) {
        return new CachedTextSymbolizer(symbol);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(ProjectedFeature projectedFeature, CachedTextSymbolizer symbol, RenderingContext2D context) throws PortrayalException{

        final Feature feature = projectedFeature.getFeature();

        //test if the symbol is visible on this feature
        if(symbol.isVisible(feature)){
            final Graphics2D g2 = context.getGraphics();
            final RenderingHints hints = g2.getRenderingHints();

            final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
            
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

            final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(symbol.getSource().getGeometryPropertyName());

            //symbolizer doesnt match the featuretype, no geometry found with this name.
            if(projectedGeometry == null) return;

            portray(projectedGeometry, context, projectedFeature, placement, haloWidth, haloPaint, fontPaint, j2dFont, label);
        }

    }

    private static void portray(ProjectedGeometry projectedGeometry, RenderingContext2D context, ProjectedFeature projectedFeature,
            CachedLabelPlacement placement, float haloWidth, Paint haloPaint, Paint fontPaint, Font j2dFont,
            String label) throws PortrayalException{

        final Feature feature = projectedFeature.getFeature();
        final LabelRenderer renderer = context.getLabelRenderer(true);

        final LabelLayer labelLayer = new DefaultLabelLayer(false, true);

        exploreAndPortray(projectedGeometry, feature, context, placement, haloWidth, haloPaint, fontPaint, j2dFont, label, labelLayer);

        renderer.append(labelLayer);
    }

    private static void exploreAndPortray(ProjectedGeometry projectedGeometry, Feature feature, RenderingContext2D context,
            CachedLabelPlacement placement, float haloWidth, Paint haloPaint, Paint fontPaint, Font j2dFont,
            String label, LabelLayer layer) throws PortrayalException{

        if(placement instanceof CachedPointPlacement){
            final CachedPointPlacement pp = (CachedPointPlacement) placement;
            
            final float anchorX = pp.getAnchorX(feature);
            final float anchorY = pp.getAnchorY(feature);
            final float dispX = pp.getDisplacementX(feature);
            final float dispY = pp.getDisplacementY(feature);
            final float rotation = pp.getRotation(feature);

            final LabelDescriptor descriptor = new DefaultPointLabelDescriptor(
                label, j2dFont, fontPaint,
                haloWidth, haloPaint,
                anchorX, anchorY,
                dispX, dispY,
                rotation, context.getDisplayCRS(),
                projectedGeometry);
            layer.labels().add(descriptor);

        }else if(placement instanceof CachedLinePlacement){
            final CachedLinePlacement lp = (CachedLinePlacement) placement;

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
                    projectedGeometry);
            layer.labels().add(descriptor);

        }else{
            throw new PortrayalException("Text symbolizer has no label placement, this should not be possible.");
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedCoverage graphic, CachedTextSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException{
        //nothing to portray
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(ProjectedFeature feature, CachedTextSymbolizer symbol, 
            RenderingContext2D context, SearchAreaJ2D mask, VisitFilter filter) {
        //text symbolizer are not hittable
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(ProjectedCoverage graphic, CachedTextSymbolizer symbol,
            RenderingContext2D renderingContext, SearchAreaJ2D mask, VisitFilter filter) {
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Rectangle2D glyphPreferredSize(CachedTextSymbolizer symbol) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
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
                width = expWidth.evaluate(null, Number.class).floatValue();
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
