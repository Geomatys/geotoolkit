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
import java.util.stream.Stream;
import org.apache.sis.internal.map.ExceptionPresentation;
import org.apache.sis.internal.map.Presentation;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.presentation.TextPresentation;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.style.CachedHalo;
import org.geotoolkit.display2d.style.CachedLabelPlacement;
import org.geotoolkit.display2d.style.CachedLinePlacement;
import org.geotoolkit.display2d.style.CachedPointPlacement;
import org.geotoolkit.display2d.style.CachedTextSymbolizer;
import org.geotoolkit.display2d.style.labeling.DefaultLinearLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.DefaultPointLabelDescriptor;
import org.geotoolkit.display2d.style.labeling.LabelDescriptor;
import org.opengis.feature.Feature;


/**
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TextSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedTextSymbolizer>{


    public TextSymbolizerRenderer(final SymbolizerRendererService service, final CachedTextSymbolizer symbol, final RenderingContext2D context){
        super(service,symbol,context);
    }

    @Override
    public Stream<Presentation> presentations(MapLayer layer, Feature feature) {

        //test if the symbol is visible on this feature
        if (!symbol.isVisible(feature)) return Stream.empty();

        //we adjust coefficient for rendering ------------------------------
        final float coeff;
        if (dispGeom) {
            //symbol is in display unit
            coeff = 1;
        } else {
            //we have a special unit we must adjust the coefficient. We adapt it to the current region of interest,
            // by computin scale difference between objective and display
            final AffineTransform inverse = renderingContext.getObjectiveToDisplay();
            coeff = (float) (this.coeff * Math.abs(AffineTransforms2D.getScale(inverse)));
        }


        //start to extract label parameters---------------------------------
        String label = symbol.getLabel(feature);
        if (label == null) return Stream.empty(); //nothing to paint
        label = label.trim();
        if (label.isEmpty()) return Stream.empty(); //nothing to paint
        final CachedHalo halo = symbol.getHalo();
        final CachedLabelPlacement placement = symbol.getPlacement();

        //extract halo parameters
        final float haloWidth;
        final Paint haloPaint;
        if (halo != null) {
            haloWidth = halo.getWidth(feature);
            haloPaint = halo.getJ2DPaint(feature, 0, 0, hints);
        } else {
            haloWidth = 0;
            haloPaint = Color.WHITE;
        }

        //extract text parameters
        final Paint fontPaint = symbol.getFontPaint(feature, 0,0, coeff, hints);
        final Font j2dFont = symbol.getJ2dFont(feature, coeff);

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        final ProjectedGeometry projectedGeometry = new ProjectedGeometry(renderingContext);
        projectedGeometry.setDataGeometry(GO2Utilities.getGeometry(feature, symbol.getSource().getGeometry()), null);

        if (placement instanceof CachedPointPlacement) {
            final CachedPointPlacement pp = (CachedPointPlacement) placement;

            final float[] anchor = pp.getAnchor(feature, null);
            final float[] disp = pp.getDisplacement(feature,null);
            final float rotation = pp.getRotation(feature);

            final LabelDescriptor descriptor = new DefaultPointLabelDescriptor(
                label, j2dFont, fontPaint,
                haloWidth, haloPaint,
                anchor[0], anchor[1],
                // SE 11.3.2: displacement is expressed in defined unit of measurement
                disp[0] * coeff, disp[1] * coeff,
                rotation, renderingContext.getDisplayCRS(),
                projectedGeometry);

            final TextPresentation tp = new TextPresentation(layer, layer.getData(), feature);
            tp.forGrid(renderingContext);
            tp.labelDesc = descriptor;
            return Stream.of(tp);
        } else if (placement instanceof CachedLinePlacement) {
            final CachedLinePlacement lp = (CachedLinePlacement) placement;

            final LabelDescriptor descriptor = new DefaultLinearLabelDescriptor(
                    label,
                    j2dFont,
                    fontPaint,
                    haloWidth,
                    haloPaint,
                    // SE section 11: Gap and initial gap are defined using UOM.
                    lp.getGap(feature) * coeff,
                    lp.getInitialGap(feature) * coeff,
                    // SE 11.1.4 and 11.4.4: Perpendicular offset is in defined unit of measurement
                    lp.getOffset(feature) * coeff,
                    lp.isRepeated(),
                    lp.isAligned(),
                    lp.isGeneralizeLine(),
                    projectedGeometry);

            final TextPresentation tp = new TextPresentation(layer, layer.getData(), feature);
            tp.forGrid(renderingContext);
            tp.labelDesc = descriptor;
            return Stream.of(tp);
        } else {
            ExceptionPresentation ep = new ExceptionPresentation(new PortrayalException("Text symbolizer has no label placement, this should not be possible."));
            ep.setLayer(layer);
            ep.setResource(layer.getData());
            return Stream.of(ep);
        }

    }

}
