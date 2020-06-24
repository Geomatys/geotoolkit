/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2015, Geomatys
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
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedHalo;
import org.geotoolkit.display2d.style.CachedLabelPlacement;
import org.geotoolkit.display2d.style.CachedLinePlacement;
import org.geotoolkit.display2d.style.CachedPointPlacement;
import org.geotoolkit.display2d.style.CachedTextSymbolizer;
import org.geotoolkit.display2d.style.labeling.DefaultLinearLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.DefaultPointLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.LabelDescriptor;
import org.geotoolkit.display2d.style.labeling.LabelLayer;
import org.geotoolkit.display2d.style.labeling.LabelRenderer;


/**
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TextSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedTextSymbolizer>{


    private LabelLayer labelLayer;

    public TextSymbolizerRenderer(final SymbolizerRendererService service, final CachedTextSymbolizer symbol, final RenderingContext2D context){
        super(service,symbol,context);
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
    public boolean portray(final ProjectedCoverage projectedCoverage) throws PortrayalException{
        //portray the border of the coverage
        final ProjectedGeometry projectedGeometry = projectedCoverage.getEnvelopeGeometry(renderingContext);

        //could not find the border geometry
        if(projectedGeometry == null) return false;

        return portray(projectedGeometry, null, projectedCoverage);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean portray(final ProjectedObject projectedFeature) throws PortrayalException{

        final Object candidate = projectedFeature.getCandidate();

        //test if the symbol is visible on this feature
        if(symbol.isVisible(candidate)){
            ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(geomPropertyName);
            return portray(projectedGeometry, candidate, projectedFeature);
        }
        return false;
    }

    public boolean portray(ProjectedGeometry projectedGeometry, Object candidate, final ProjectedObject projectedFeature) throws PortrayalException{

        //test if the symbol is visible on this feature
        if(!symbol.isVisible(candidate)) return false;

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
            coeff *= Math.abs(AffineTransforms2D.getScale(inverse));
        }


        //start to extract label parameters---------------------------------
        String label = symbol.getLabel(candidate);
        if(label == null) return false; //nothing to paint
        label = label.trim();
        if(label.isEmpty()) return false; //nothing to paint
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

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return false;

        projectedGeometry = new ProjectedGeometry(projectedGeometry);

        //add in futur painting layer
        final LabelLayer labelLayer = getLabelLayer();
        final Object feature = projectedFeature.getCandidate();

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
                rotation, renderingContext.getDisplayCRS(),
                projectedGeometry);
            labelLayer.labels().add(descriptor);
            return true;
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
            labelLayer.labels().add(descriptor);
            return true;
        }else{
            throw new PortrayalException("Text symbolizer has no label placement, this should not be possible.");
        }

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
