/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.ext.dimrange;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.processing.ColorMap;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRendererService;
import org.geotoolkit.display2d.style.renderer.SymbolizerRenderer;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.coverage.GridCoverageResource;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DimRangeRendererService extends AbstractSymbolizerRendererService<DimRangeSymbolizer,CachedDimRangeSymbolizer>{

    @Override
    public boolean isGroupSymbolizer() {
        return false;
    }

    @Override
    public Class<DimRangeSymbolizer> getSymbolizerClass() {
        return DimRangeSymbolizer.class;
    }

    @Override
    public Class<CachedDimRangeSymbolizer> getCachedSymbolizerClass() {
        return CachedDimRangeSymbolizer.class;
    }

    @Override
    public CachedDimRangeSymbolizer createCachedSymbolizer(final DimRangeSymbolizer symbol) {
        return new CachedDimRangeSymbolizer(symbol,this);
    }

    @Override
    public SymbolizerRenderer createRenderer(final CachedDimRangeSymbolizer symbol, final RenderingContext2D context) {
        return new DimRangeRenderer(this,symbol, context);
    }

    @Override
    public Rectangle2D glyphPreferredSize(final CachedDimRangeSymbolizer symbol,final MapLayer layer) {
        return new Rectangle2D.Double(0, 0, 120, 20);
    }

    @Override
    public void glyph(final Graphics2D g, final Rectangle2D rect, final CachedDimRangeSymbolizer symbol, final MapLayer layer) {

        int[] ARGB = new int[]{Color.RED.getRGB(),Color.GREEN.getRGB(),Color.BLUE.getRGB()};

        if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer) layer;
            final GridCoverageResource ref = cml.getCoverageReference();
            try {
                final GridCoverageReader reader = ref.acquireReader();
                final GridCoverageReadParam param = new GridCoverageReadParam();
                param.setResolution(1,1);
                GridCoverage2D cov = (GridCoverage2D) reader.read(0, param);
                ref.recycle(reader);
                cov = cov.view(ViewType.NATIVE);
                RenderedImage img = cov.getRenderedImage();
                ColorModel cm = img.getColorModel();

                if(cm instanceof IndexColorModel){
                    final IndexColorModel icm = (IndexColorModel) cm;

                    final GridSampleDimension sampleDim = cov.getSampleDimensions().get(0);

                    int size = icm.getMapSize();
                    ARGB = new int[size];
                    icm.getRGBs(ARGB);
                    final double minVal = sampleDim.getMinimumValue();
                    final double maxVal = sampleDim.getMaximumValue();

                    final ColorMap colorMap = new ColorMap();
                    colorMap.setGeophysicsRange(ColorMap.ANY_QUANTITATIVE_CATEGORY,
                            new MeasurementRange(NumberRange.create(minVal, true, maxVal, true),sampleDim.getUnits()));

                    GridSampleDimension ret = colorMap.recolor(sampleDim, ARGB);
                }

            } catch (CoverageStoreException | CancellationException ex) {
                Logging.getLogger("org.geotoolkit.display2d.ext.dimrange").log(Level.WARNING, null, ex);
            }
        }

        final float[] space = new float[ARGB.length];
        final Color[] colors = new Color[ARGB.length];
        for(int i=0;i<space.length;i++){
            space[i] = (float)i/(space.length-1);
            colors[i] = new Color(ARGB[i]);
        }

        final LinearGradientPaint paint = new LinearGradientPaint(
                (float)rect.getMinX(), (float)rect.getMinY(),
                (float)rect.getMaxX(), (float)rect.getMinY(),
                space, colors);

        g.setPaint(paint);
        g.fill(rect);
    }

}
