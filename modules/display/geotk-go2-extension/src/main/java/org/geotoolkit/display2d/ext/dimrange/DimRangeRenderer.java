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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageReadParam;
import org.geotoolkit.coverage.processing.ColorMap;
import org.geotoolkit.coverage.processing.CoverageProcessingException;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.renderer.AbstractSymbolizerRenderer;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.util.logging.Logging;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DimRangeRenderer extends AbstractSymbolizerRenderer<DimRangeSymbolizer,CachedDimRangeSymbolizer>{

    private static final Logger LOGGER = Logging.getLogger(DimRangeRenderer.class);
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
    public void portray(ProjectedFeature graphic, CachedDimRangeSymbolizer symbol, RenderingContext2D context) throws PortrayalException {
        //nothing to portray;
    }

    @Override
    public void portray(ProjectedCoverage projectedCoverage, CachedDimRangeSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException {

        double[] resolution = context.getResolution();

        final CoordinateReferenceSystem gridCRS = projectedCoverage.getCoverageLayer().getBounds().getCoordinateReferenceSystem();

        Envelope bounds = new GeneralEnvelope(context.getCanvasObjectiveBounds());
        //bounds.setCoordinateReferenceSystem(context.getObjectiveCRS());

        if(!gridCRS.equals(bounds.getCoordinateReferenceSystem())){
            try {
                bounds = CRS.transform(bounds, gridCRS);
            } catch (TransformException ex) {
                Logging.getLogger(DimRangeRenderer.class).log(Level.SEVERE, null, ex);
            }

            DirectPosition2D pos = new DirectPosition2D(context.getObjectiveCRS());
            pos.x = resolution[0];
            pos.y = resolution[1];

            try{
                MathTransform trs = context.getMathTransform(context.getObjectiveCRS(), gridCRS);
                DirectPosition pos2 = trs.transform(pos, null);
                resolution[0] = pos2.getOrdinate(0);
                resolution[1] = pos2.getOrdinate(1);
            }catch(Exception ex){
                throw new PortrayalException(ex);
            }

        }

        final CoverageReadParam param = new CoverageReadParam(bounds, resolution);

        GridCoverage2D dataCoverage;
        try {
            dataCoverage = projectedCoverage.getCoverage(param);
        } catch (FactoryException ex) {
            throw new PortrayalException(ex);
        } catch (IOException ex) {
            throw new PortrayalException(ex);
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        if(!CRS.equalsIgnoreMetadata(dataCoverage.getCoordinateReferenceSystem2D(), context.getObjectiveCRS())){
            //coverage is not in objective crs, resample it
            try{
                //we resample the native view of the coverage only, the style will be applied later.
                dataCoverage = (GridCoverage2D) Operations.DEFAULT.resample(
                        dataCoverage.view(ViewType.NATIVE),
                        context.getObjectiveCRS());
            }catch(Exception ex){
                System.out.println("ERROR resample in raster symbolizer renderer: " + ex.getMessage());
            }
        }

        final Graphics2D g2 = context.getGraphics();

        //we must switch to objectiveCRS for grid coverage
        context.switchToObjectiveCRS();

        MeasurementRange dimRange = symbol.getSource().getDimRange();
        if (dimRange != null && dataCoverage.getCoordinateReferenceSystem().getCoordinateSystem().getDimension() <= 2) {
            final GridSampleDimension[] samples = dataCoverage.getSampleDimensions();
            if (samples != null && samples.length == 1 && samples[0] != null) {
                if (samples[0].getSampleToGeophysics() != null) {
                    final ColorMap colorMap = new ColorMap();
                    colorMap.setGeophysicsRange(ColorMap.ANY_QUANTITATIVE_CATEGORY, dimRange);
                    try {
                        dataCoverage = (GridCoverage2D) Operations.DEFAULT.recolor(dataCoverage, new ColorMap[]{colorMap});
                    } catch (CoverageProcessingException c) {
                        throw new PortrayalException(c);
                    }
                }
            }
        }
        dataCoverage = dataCoverage.view(ViewType.RENDERED);
        final RenderedImage img = dataCoverage.getRenderableImage(0, 1).createDefaultRendering();



        final MathTransform2D trs2D = dataCoverage.getGridGeometry().getGridToCRS2D();
        if(trs2D instanceof AffineTransform){
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1));
            g2.drawRenderedImage(img, (AffineTransform)trs2D);
        }else if (trs2D instanceof LinearTransform) {
            final LinearTransform lt = (LinearTransform) trs2D;
            final int col = lt.getMatrix().getNumCol();
            final int row = lt.getMatrix().getNumRow();
            //TODO using only the first parameters of the linear transform
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass());
        }else{
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass() );
        }


        context.switchToDisplayCRS();
    }

    @Override
    public boolean hit(ProjectedFeature graphic, CachedDimRangeSymbolizer symbol, RenderingContext2D context, SearchAreaJ2D mask, VisitFilter filter) {
        return false;
    }

    @Override
    public boolean hit(final ProjectedCoverage projectedCoverage, final CachedDimRangeSymbolizer symbol,
            final RenderingContext2D context, final SearchAreaJ2D search, final VisitFilter filter) {

        
        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Shape mask = search.getDisplayShape();
        final Shape shape;
        try {
            shape = projectedCoverage.getEnvelopeGeometry().getDisplayShape();
        } catch (TransformException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }

        final Area area = new Area(mask);

        switch(filter){
            case INTERSECTS :
                area.intersect(new Area(shape));
                return !area.isEmpty();
            case WITHIN :
                Area start = new Area(area);
                area.add(new Area(shape));
                return start.equals(area);
        }

        return false;
    }

    @Override
    public Rectangle2D glyphPreferredSize(CachedDimRangeSymbolizer symbol,MapLayer layer) {
        return null;
    }

    @Override
    public void glyph(Graphics2D g, Rectangle2D rect, CachedDimRangeSymbolizer symbol, MapLayer layer) {
        //no glyph
    }

}
