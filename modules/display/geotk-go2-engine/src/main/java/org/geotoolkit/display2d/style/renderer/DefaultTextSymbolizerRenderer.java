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

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.AffineTransform;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.statefull.StatefullProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedObject;
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
import org.geotoolkit.display2d.style.labeling.LabelLayer;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;


/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultTextSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedTextSymbolizer>{


    private LabelLayer labelLayer;

    public DefaultTextSymbolizerRenderer(final CachedTextSymbolizer symbol, final RenderingContext2D context){
        super(symbol,context);
    }

    public LabelLayer getLabelLayer() {
        if(labelLayer == null){
            final LabelRenderer renderer = renderingContext.getLabelRenderer(true);
            labelLayer = renderer.createLabelLayer();
            renderer.append(labelLayer);
        }
        return labelLayer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedObject projectedFeature) throws PortrayalException{

        final Object candidate = projectedFeature.getCandidate();

        //test if the symbol is visible on this feature
        if(symbol.isVisible(candidate)){
            
            //we adjust coefficient for rendering ------------------------------
            float coeff = 1;
            if(dispGeom){
                //symbol is in display unit
                coeff = 1;
            }else{
                //we have a special unit we must adjust the coefficient
                coeff = renderingContext.getUnitCoefficient(symbolUnit);
                // calculate scale difference between objective and display
                final AffineTransform inverse = renderingContext.getObjectiveToDisplay();
                coeff *= Math.abs(XAffineTransform.getScale(inverse));
            }


            //strat to extract label parameters---------------------------------
            final String label = symbol.getLabel(candidate).trim();
            if(label.isEmpty()) return; //nothing to paint
            final CachedHalo halo = symbol.getHalo();
            final CachedLabelPlacement placement = symbol.getPlacement();

            //extract halo parameters
            final float haloWidth;
            final Paint haloPaint;
            if(halo != null){
                haloWidth = halo.getWidth(candidate);
                haloPaint = halo.getJ2DPaint(candidate, 0, 0, hints);
            }else{
                haloWidth = 0;
                haloPaint = Color.WHITE;
            }

            //extract text parameters
            final Paint fontPaint = symbol.getFontPaint(candidate, 0,0, coeff, hints);
            final Font j2dFont = symbol.getJ2dFont(candidate, coeff);

            ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(symbol.getSource().getGeometryPropertyName());

            //symbolizer doesnt match the featuretype, no geometry found with this name.
            if(projectedGeometry == null) return;

            projectedGeometry = new StatefullProjectedGeometry((StatefullProjectedGeometry)projectedGeometry);

            portray(projectedGeometry, renderingContext, projectedFeature, placement, haloWidth, haloPaint, fontPaint, j2dFont, label);
        }

    }

    private void portray(final ProjectedGeometry projectedGeometry, final RenderingContext2D context, 
            final ProjectedObject projectedFeature, final CachedLabelPlacement placement,
            final float haloWidth, final Paint haloPaint, final Paint fontPaint, final Font j2dFont,
            final String label) throws PortrayalException{

        final Object candidate = projectedFeature.getCandidate();

        final LabelLayer labelLayer = getLabelLayer();
        exploreAndPortray(projectedGeometry, candidate, context, placement, haloWidth,
                haloPaint, fontPaint, j2dFont, label, labelLayer);
    }

    private static void exploreAndPortray(final ProjectedGeometry projectedGeometry, final Object feature, final RenderingContext2D context,
            final CachedLabelPlacement placement, final float haloWidth, final Paint haloPaint, final Paint fontPaint, final Font j2dFont,
            final String label, final LabelLayer layer) throws PortrayalException{

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
    public boolean hit(final ProjectedObject candidate, final SearchAreaJ2D mask, final VisitFilter filter) {
        //text symbolizer are not hittable
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
