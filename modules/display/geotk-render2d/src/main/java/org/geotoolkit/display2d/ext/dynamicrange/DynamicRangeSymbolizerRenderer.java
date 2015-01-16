/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.display2d.ext.dynamicrange;

import java.awt.AlphaComposite;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.Map;
import java.util.logging.Level;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import static org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer.extractQuery;
import static org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer.fixEnvelopeWithQuery;
import static org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer.fixResolutionWithCRS;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.process.image.dynamicrange.DynamicRangeStretchProcess;
import org.geotoolkit.referencing.CRS;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DynamicRangeSymbolizerRenderer extends AbstractCoverageSymbolizerRenderer<CachedDynamicRangeSymbolizer>{

    public DynamicRangeSymbolizerRenderer(SymbolizerRendererService service, CachedDynamicRangeSymbolizer symbol, RenderingContext2D context) {
        super(service, symbol, context);
    }

    @Override
    public void portray(ProjectedCoverage projectedCoverage) throws PortrayalException {
        
        try{
            final GridCoverage2D dataCoverage = getCoverage(projectedCoverage);
            if(dataCoverage == null){
                return;
            }
            
            final RenderedImage ri = dataCoverage.getRenderedImage();
            final DynamicRangeSymbolizer symbolizer = symbol.getSource();
            
            final int[] bands = new int[]{-1,-1,-1,-1};
            final double[][] ranges = new double[][]{{-1,-1},{-1,-1},{-1,-1},{-1,-1}};
            
            for(DynamicRangeSymbolizer.DRChannel channel : symbolizer.getChannels()){
                final Integer bandIdx;
                try{
                    bandIdx = Integer.valueOf(channel.getBand());
                }catch(NumberFormatException ex){
                    //not a number index
                    continue;
                }
                final String cs = channel.getColorSpaceComponent().trim();
                final int idx;
                if(DynamicRangeSymbolizer.DRChannel.BAND_RED.equalsIgnoreCase(cs)) idx=0;
                else if(DynamicRangeSymbolizer.DRChannel.BAND_GREEN.equalsIgnoreCase(cs)) idx=1;
                else if(DynamicRangeSymbolizer.DRChannel.BAND_BLUE.equalsIgnoreCase(cs)) idx=2;
                else if(DynamicRangeSymbolizer.DRChannel.BAND_ALPHA.equalsIgnoreCase(cs)) idx=3;
                else {
                    //no mapping
                    continue;
                }
                
                bands[idx] = bandIdx;
                
                final Object stats = null;
                ranges[idx][0] = evaluate(channel.getLower(), stats);
                ranges[idx][1] = evaluate(channel.getUpper(), stats);
            }
            
            final DynamicRangeStretchProcess p = new DynamicRangeStretchProcess(ri, bands, ranges);
            final BufferedImage img = p.executeNow();
            final MathTransform2D trs2D = dataCoverage.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);
            renderCoverage(img, trs2D);
        
        } catch (Exception e) {
            monitor.exceptionOccured(e, Level.WARNING);
        }
        
    }
    
    private static double evaluate(DynamicRangeSymbolizer.DRBound bound, Object stats) throws PortrayalException{
        final String mode = bound.getMode();
        if(DynamicRangeSymbolizer.DRBound.MODE_EXPRESSION.equalsIgnoreCase(mode)){
            final Expression exp = bound.getValue();
            if(GO2Utilities.isStatic(exp)){
                final Number val = exp.evaluate(null, Number.class);
                return val.doubleValue();
            }else{
                throw new PortrayalException("dynamic expression not supported yet.");
            }

        }else if(DynamicRangeSymbolizer.DRBound.MODE_PERCENT.equalsIgnoreCase(mode)){
            throw new PortrayalException("Percent not supported yet.");
        }else{
            throw new PortrayalException("Unknwoned mode "+mode);
        }
    }
    
    
    private void renderCoverage(RenderedImage img, MathTransform2D trs2D) throws PortrayalException{
        if (trs2D instanceof AffineTransform) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1));
            try {
                g2d.drawRenderedImage(img, (AffineTransform)trs2D);
            } catch (Exception ex) {
                //plenty of errors can happen when painting an image
                monitor.exceptionOccured(ex, Level.WARNING);
            }
        }else if (trs2D instanceof LinearTransform) {
            final LinearTransform lt = (LinearTransform) trs2D;
            final int col = lt.getMatrix().getNumCol();
            final int row = lt.getMatrix().getNumRow();
            //TODO using only the first parameters of the linear transform
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass());
        }else{
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass() );
        }

    }
    
    private GridCoverage2D getCoverage(ProjectedCoverage projectedCoverage) throws PortrayalException, TransformException, CoverageStoreException{
        double[] resolution = renderingContext.getResolution();
        Envelope bounds = new GeneralEnvelope(renderingContext.getCanvasObjectiveBounds());
        resolution = checkResolution(resolution,bounds);
        final CoverageMapLayer coverageLayer = projectedCoverage.getLayer();
        final Envelope layerBounds = coverageLayer.getBounds();
        final CoordinateReferenceSystem coverageMapLayerCRS = layerBounds.getCoordinateReferenceSystem();

        final Map<String,Double> queryValues = extractQuery(projectedCoverage.getLayer());
        if (queryValues != null && !queryValues.isEmpty()) {
            bounds = fixEnvelopeWithQuery(queryValues, bounds, coverageMapLayerCRS);
            resolution = fixResolutionWithCRS(resolution, coverageMapLayerCRS);
        }

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(bounds);
        param.setResolution(resolution);

        GridCoverage2D dataCoverage;
        try {
            dataCoverage = projectedCoverage.getCoverage(param);
        } catch (DisjointCoverageDomainException ex) {
            //since the visible envelope can be much larger, we may obtain NaN when transforming the envelope
            //which causes the disjoint domain exception
            final GeneralEnvelope objCovEnv = new GeneralEnvelope(CRS.transform(layerBounds, bounds.getCoordinateReferenceSystem()));
            objCovEnv.intersect(bounds);
            if(objCovEnv.isEmpty()){
                return null; //the coverage envelope does not intersect the canvas envelope.
            }
            param.setEnvelope(objCovEnv);
            try {
                dataCoverage = projectedCoverage.getCoverage(param);
            } catch (DisjointCoverageDomainException exd) {
                //we tried
                return null;
            }
        } catch (CoverageStoreException ex) {
            throw new PortrayalException(ex);
        }
        return dataCoverage;
    }
    
}
