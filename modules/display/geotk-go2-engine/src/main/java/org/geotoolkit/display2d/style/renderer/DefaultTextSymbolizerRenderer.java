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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.statefull.StatefullProjectedGeometry;
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
import org.opengis.referencing.FactoryException;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultTextSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedTextSymbolizer>{

    public DefaultTextSymbolizerRenderer(CachedTextSymbolizer symbol, RenderingContext2D context){
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
            final Graphics2D g2 = renderingContext.getGraphics();
            final RenderingHints hints = renderingContext.getRenderingHints();

            final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
            
            //we adjust coefficient for rendering ------------------------------
            float coeff = 1;
            if(symbolUnit.equals(NonSI.PIXEL)){
                //symbol is in display unit
                coeff = 1;
            }else{
                //we have a special unit we must adjust the coefficient
                coeff = renderingContext.getUnitCoefficient(symbolUnit);
                // calculate scale difference between objective and display
                try{
                    final AffineTransform inverse = renderingContext.getAffineTransform(renderingContext.getObjectiveCRS(), renderingContext.getDisplayCRS());
                    coeff *= Math.abs(XAffineTransform.getScale(inverse));
                }catch(FactoryException ex){
                    throw new PortrayalException("Could not calculate display to objective transform",ex);
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

            ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(symbol.getSource().getGeometryPropertyName());

            //symbolizer doesnt match the featuretype, no geometry found with this name.
            if(projectedGeometry == null) return;

            projectedGeometry = new StatefullProjectedGeometry((StatefullProjectedGeometry)projectedGeometry);

            portray(projectedGeometry, renderingContext, projectedFeature, placement, haloWidth, haloPaint, fontPaint, j2dFont, label);
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
            
            final float[] anchor = pp.getAnchor(feature, null);
            final float[] disp = pp.getDisplacement(feature,null);
            final float rotation = pp.getRotation(feature);

            final LabelDescriptor descriptor = new DefaultPointLabelDescriptor(
                label, j2dFont, fontPaint,
                haloWidth, haloPaint,
                anchor[0], anchor[1],
                disp[0], disp[1],
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
    public void portray(final ProjectedCoverage graphic) throws PortrayalException{
        //nothing to portray
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(ProjectedFeature feature, SearchAreaJ2D mask, VisitFilter filter) {
        //text symbolizer are not hittable
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(ProjectedCoverage graphic, SearchAreaJ2D mask, VisitFilter filter) {
        return false;
    }

}
