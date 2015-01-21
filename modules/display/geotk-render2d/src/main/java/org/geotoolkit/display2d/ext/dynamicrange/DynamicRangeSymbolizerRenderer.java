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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageUtilities;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.processing.CoverageProcessingException;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import static org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer.extractQuery;
import static org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer.fixEnvelopeWithQuery;
import static org.geotoolkit.display2d.style.renderer.DefaultRasterSymbolizerRenderer.fixResolutionWithCRS;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.math.Histogram;
import org.geotoolkit.metadata.DefaultSampleDimensionExt;
import org.geotoolkit.parameter.ParametersExt;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.coverage.resample.ResampleDescriptor;
import org.geotoolkit.process.image.dynamicrange.DynamicRangeStretchProcess;
import org.geotoolkit.referencing.CRS;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.content.AttributeGroup;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.SampleDimension;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
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
            GridCoverage2D dataCoverage = getCoverage(projectedCoverage);
            if(dataCoverage == null){
                return;
            }

            final CoverageReference covref = projectedCoverage.getCandidate().getCoverageReference();
            final CoverageDescription covdesc = covref.getMetadata();
            
            dataCoverage = dataCoverage.view(ViewType.GEOPHYSICS);
            final RenderedImage ri = dataCoverage.getRenderedImage();
            final DynamicRangeSymbolizer symbolizer = symbol.getSource();
            
            final int[] bands = new int[]{-1,-1,-1,-1};
            final double[][] ranges = new double[][]{{-1,-1},{-1,-1},{-1,-1},{-1,-1}};

            final Map<String,Object> stats = new HashMap<>();

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

                //search for band statistics
                stats.clear();
                search:
                for(AttributeGroup attg : covdesc.getAttributeGroups()){
                    for(RangeDimension rd : attg.getAttributes()){
                        if(!(rd instanceof SampleDimension)) continue;
                        final int i = Integer.parseInt(rd.getSequenceIdentifier().tip().toString());
                        if(i==bandIdx){
                            final SampleDimension sd = (SampleDimension) rd;
                            stats.put(DynamicRangeSymbolizer.PROPERTY_MIN, sd.getMinValue());
                            stats.put(DynamicRangeSymbolizer.PROPERTY_MAX, sd.getMaxValue());
                            stats.put(DynamicRangeSymbolizer.PROPERTY_MEAN, sd.getMeanValue());
                            stats.put(DynamicRangeSymbolizer.PROPERTY_STD, sd.getStandardDeviation());
                            if(sd instanceof DefaultSampleDimensionExt){
                                final DefaultSampleDimensionExt dsd = (DefaultSampleDimensionExt) sd;
                                stats.put(DynamicRangeSymbolizer.PROPERTY_HISTO, dsd.getHistogram());
                                stats.put(DynamicRangeSymbolizer.PROPERTY_HISTO_MIN, dsd.getHistogramMin());
                                stats.put(DynamicRangeSymbolizer.PROPERTY_HISTO_MAX, dsd.getHistogramMax());
                            }
                            break search;
                        }
                    }
                }

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
    
    private static double evaluate(DynamicRangeSymbolizer.DRBound bound, Map<String,Object> stats) throws PortrayalException{
        final String mode = bound.getMode();
        if(DynamicRangeSymbolizer.DRBound.MODE_EXPRESSION.equalsIgnoreCase(mode)){
            final Expression exp = bound.getValue();
            final Number val = exp.evaluate(stats, Number.class);
            return (val==null) ? Double.NaN : val.doubleValue();
        }else if(DynamicRangeSymbolizer.DRBound.MODE_PERCENT.equalsIgnoreCase(mode)){
            final long[] histo = (long[]) stats.get(DynamicRangeSymbolizer.PROPERTY_HISTO);
            final Double histoMin = (Double) stats.get(DynamicRangeSymbolizer.PROPERTY_HISTO_MIN);
            final Double histoMax = (Double) stats.get(DynamicRangeSymbolizer.PROPERTY_HISTO_MAX);
            if(histo==null || histoMin==null || histoMax==null){
                //we don't have the informations
                LOGGER.log(Level.INFO, "Missing histogram information for correct rendering.");
                return Double.NaN;
            }else{
                final Expression exp = bound.getValue();
                final Number val = exp.evaluate(stats, Number.class);
                final Histogram h = new Histogram(histo, histoMin, histoMax);
                return h.getValueAt(val.doubleValue()/100.0);
            }


        }else{
            throw new PortrayalException("Unknwoned mode "+mode);
        }
    }
    
    
    private void renderCoverage(RenderedImage img, MathTransform2D trs2D) throws PortrayalException{
        renderingContext.switchToObjectiveCRS();
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
        
        ////////////////////////////////////////////////////////////////////
        // 2 - Reproject data                                             //
        ////////////////////////////////////////////////////////////////////

        boolean isReprojected;
        final MathTransform coverageToObjective;
        final CoordinateReferenceSystem coverageCRS = dataCoverage.getCoordinateReferenceSystem();
        try{
            final CoordinateReferenceSystem targetCRS = renderingContext.getObjectiveCRS2D();
            final CoordinateReferenceSystem candidate2D = CRSUtilities.getCRS2D(coverageCRS);
            /* It appears EqualsIgnoreMetadata can return false sometimes, even if the two CRS are equivalent.
             * But mathematics don't lie, so if they really describe the same transformation, conversion from
             * one to another will give us an identity matrix.
             */
            coverageToObjective = CRS.findMathTransform(candidate2D, targetCRS);
            isReprojected = !coverageToObjective.isIdentity();

            if (isReprojected) {
                //calculate best intersection area
                final GeneralEnvelope tmp = new GeneralEnvelope(renderingContext.getPaintingObjectiveBounds2D());
                final int xAxis = Math.max(0, CoverageUtilities.getMinOrdinate(coverageCRS));

//                    tmp.intersect(CRS.transform(coverageToObjective, new GeneralEnvelope(
//                            dataCoverage.getEnvelope()).subEnvelope(xAxis, xAxis + 2)));
                final GeneralEnvelope coverageEnv2D =new GeneralEnvelope(dataCoverage.getEnvelope2D());
                final Envelope transformed = CRS.transform(coverageEnv2D, renderingContext.getObjectiveCRS2D());
                tmp.intersect(transformed);

                if (tmp.isEmpty()) {
                    dataCoverage = null;

                } else {
                    //calculate gridgeometry
                    final AffineTransform2D trs = renderingContext.getObjectiveToDisplay();
                    final GeneralEnvelope dispEnv = CRS.transform(trs, tmp);
                    final int width = (int)Math.ceil(dispEnv.getSpan(0));
                    final int height = (int)Math.ceil(dispEnv.getSpan(1));

                    if (width <= 0 || height <= 0) {
                        dataCoverage = null;
                    } else {
                        final GridGeometry2D gg = new GridGeometry2D(new GridEnvelope2D(0, 0, width, height), tmp);

                        final ProcessDescriptor desc = ResampleDescriptor.INSTANCE;
                        final ParameterValueGroup params = desc.getInputDescriptor().createValue();
                        ParametersExt.getOrCreateValue(params, ResampleDescriptor.IN_COVERAGE.getName().getCode()).setValue(dataCoverage);
                        ParametersExt.getOrCreateValue(params, ResampleDescriptor.IN_COORDINATE_REFERENCE_SYSTEM.getName().getCode()).setValue(targetCRS);
                        ParametersExt.getOrCreateValue(params, ResampleDescriptor.IN_GRID_GEOMETRY.getName().getCode()).setValue(gg);

                        final org.geotoolkit.process.Process process = desc.createProcess(params);
                        final ParameterValueGroup result = process.call();
                        dataCoverage = (GridCoverage2D) result.parameter("result").getValue();
                    }
                }
            }
        } catch (CoverageProcessingException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return null;
        } catch(Exception ex) {
            //several kind of errors can happen here, we catch anything to avoid blocking the map component.
            monitor.exceptionOccured(
                new IllegalStateException("Coverage is not in the requested CRS, found : \n" +
                coverageCRS +
                "\n Was expecting : \n" +
                renderingContext.getObjectiveCRS() +
                "\nOriginal Cause:"+ ex.getMessage(), ex), Level.WARNING);
            return null;
        }

        return dataCoverage;
    }
    
}
