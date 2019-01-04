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
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.util.List;
import java.util.logging.Level;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.processing.ColorMap;
import org.geotoolkit.coverage.processing.CoverageProcessingException;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;

/**
 * Renderer for DimRange symbolizer.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DimRangeRenderer extends AbstractCoverageSymbolizerRenderer<CachedDimRangeSymbolizer>{

    public DimRangeRenderer(final SymbolizerRendererService service,final CachedDimRangeSymbolizer symbol, final RenderingContext2D context){
        super(service,symbol,context);
    }

    @Override
    public boolean portray(final ProjectedCoverage projectedCoverage) throws PortrayalException {

        double[] resolution = renderingContext.getResolution();
        final Envelope bounds = new GeneralEnvelope(renderingContext.getCanvasObjectiveBounds());
        resolution = checkResolution(resolution,bounds);
        final GridCoverageReadParam param = new GridCoverageReadParam();

        param.setEnvelope(bounds);
        param.setResolution(resolution);

        GridCoverage2D dataCoverage;
        try {
            dataCoverage = projectedCoverage.getCoverage(param);
        } catch (CoverageStoreException ex) {
            throw new PortrayalException(ex);
        }

        if(dataCoverage == null){
            LOGGER.log(Level.WARNING, "Requested an area where no coverage where found.");
            return false;
        }

        final CoordinateReferenceSystem coverageCRS = dataCoverage.getCoordinateReferenceSystem();
        try{
            final CoordinateReferenceSystem candidate2D = CRSUtilities.getCRS2D(coverageCRS);
            if(!Utilities.equalsIgnoreMetadata(candidate2D,renderingContext.getObjectiveCRS2D()) ){

                dataCoverage = (GridCoverage2D) Operations.DEFAULT.resample(dataCoverage.view(ViewType.NATIVE), renderingContext.getObjectiveCRS2D());

                if(dataCoverage != null){
                    dataCoverage = dataCoverage.view(ViewType.RENDERED);
                }
            }
        } catch (CoverageProcessingException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return false;
        } catch(Exception ex){
            //several kind of errors can happen here, we catch anything to avoid blocking the map component.
            monitor.exceptionOccured(
                new IllegalStateException("Coverage is not in the requested CRS, found : " +
                "\n"+ coverageCRS +
                " was expecting : \n" +
                renderingContext.getObjectiveCRS() +
                "\nOriginal Cause:"+ ex.getMessage(), ex), Level.WARNING);
            return false;
        }

        if(dataCoverage == null){
            LOGGER.log(Level.WARNING, "Reprojected coverage is null.");
            return false;
        }


        final Graphics2D g2 = renderingContext.getGraphics();

        //we must switch to objectiveCRS for grid coverage
        renderingContext.switchToObjectiveCRS();

        MeasurementRange dimRange = symbol.getSource().getDimRange();
        if (dimRange != null) {
            final List<GridSampleDimension> samples = dataCoverage.getSampleDimensions();
            if (samples != null && samples.size() == 1 && samples.get(0) != null) {
                if (samples.get(0).getSampleToGeophysics() != null) {
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


        boolean dataRendered = false;
        final MathTransform2D trs2D = dataCoverage.getGridGeometry().getGridToCRS2D();
        if(trs2D instanceof AffineTransform){
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1));
            g2.drawRenderedImage(img, (AffineTransform)trs2D);
            dataRendered = true;
        }else if (trs2D instanceof LinearTransform) {
            final LinearTransform lt = (LinearTransform) trs2D;
            final int col = lt.getMatrix().getNumCol();
            final int row = lt.getMatrix().getNumRow();
            //TODO using only the first parameters of the linear transform
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass());
        }else{
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass() );
        }

        renderingContext.switchToDisplayCRS();
        return dataRendered;
    }
}
