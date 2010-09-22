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
import java.util.logging.Logger;
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
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.util.NumberRange;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DimRangeRendererService extends AbstractSymbolizerRendererService<DimRangeSymbolizer,CachedDimRangeSymbolizer>{

    @Override
    public Class<DimRangeSymbolizer> getSymbolizerClass() {
        return DimRangeSymbolizer.class;
    }

    @Override
    public Class<CachedDimRangeSymbolizer> getCachedSymbolizerClass() {
        return CachedDimRangeSymbolizer.class;
    }

    @Override
    public CachedDimRangeSymbolizer createCachedSymbolizer(DimRangeSymbolizer symbol) {
        return new CachedDimRangeSymbolizer(symbol,this);
    }

    @Override
    public SymbolizerRenderer createRenderer(CachedDimRangeSymbolizer symbol, RenderingContext2D context) {
        return new DimRangeRenderer(symbol, context);
    }

    @Override
    public Rectangle2D glyphPreferredSize(CachedDimRangeSymbolizer symbol,MapLayer layer) {
        return new Rectangle2D.Double(0, 0, 120, 20);
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CachedDimRangeSymbolizer symbol, MapLayer layer) {

        int[] ARGB = new int[]{Color.RED.getRGB(),Color.GREEN.getRGB(),Color.BLUE.getRGB()};

        if(layer instanceof CoverageMapLayer){
            final CoverageMapLayer cml = (CoverageMapLayer) layer;
            final GridCoverageReader reader = cml.getCoverageReader();


            final GridCoverageReadParam param = new GridCoverageReadParam();
            param.setResolution(1,1);
            try {
                GridCoverage2D cov = (GridCoverage2D) reader.read(0, param);
                cov = cov.view(ViewType.NATIVE);
                RenderedImage img = cov.getRenderedImage();
                ColorModel cm = img.getColorModel();

                if(cm instanceof IndexColorModel){
                    final IndexColorModel icm = (IndexColorModel) cm;
                    
                    final GridSampleDimension sampleDim = cov.getSampleDimension(0);

                    int size = icm.getMapSize();
                    ARGB = new int[size];
                    icm.getRGBs(ARGB);
                    final double minVal = sampleDim.getMinimumValue();
                    final double maxVal = sampleDim.getMaximumValue();

                    final ColorMap colorMap = new ColorMap();
                    colorMap.setGeophysicsRange(ColorMap.ANY_QUANTITATIVE_CATEGORY,
                            new MeasurementRange(NumberRange.create(minVal, maxVal),sampleDim.getUnits()));

                    GridSampleDimension ret = colorMap.recolor(sampleDim, ARGB);
                }

            } catch (CoverageStoreException ex) {
                Logger.getLogger(DimRangeRendererService.class.getName()).log(Level.WARNING, null, ex);
            } catch (CancellationException ex) {
                Logger.getLogger(DimRangeRendererService.class.getName()).log(Level.WARNING, null, ex);
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
