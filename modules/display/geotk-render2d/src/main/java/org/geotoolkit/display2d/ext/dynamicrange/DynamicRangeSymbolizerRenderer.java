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
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.math.Histogram;
import org.geotoolkit.metadata.DefaultSampleDimensionExt;
import org.geotoolkit.processing.image.dynamicrange.DynamicRangeStretchProcess;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.metadata.content.AttributeGroup;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.SampleDimension;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.operation.MathTransform2D;
import org.geotoolkit.storage.coverage.CoverageResource;

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
            final CoverageResource covref = projectedCoverage.getCandidate().getCoverageReference();

            final DynamicRangeSymbolizer symbolizer = symbol.getSource();

            final int[] bands = new int[]{-1,-1,-1,-1};
            final double[][] ranges = new double[][]{{-1,-1},{-1,-1},{-1,-1},{-1,-1}};

            final Map<String,Object> stats = new HashMap<>();

            //we can avoid loading metadata if all ranges are literals,
            //metadata can be expensive because of histogram informations
            boolean allLiteral = true;
            for(DynamicRangeSymbolizer.DRChannel channel : symbolizer.getChannels()){
                allLiteral &= channel.getLower().getValue() instanceof Literal;
                allLiteral &= channel.getUpper().getValue() instanceof Literal;
            }
            final CoverageDescription covdesc = allLiteral ? null : covref.getCoverageDescription();

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
                if (!allLiteral) {
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
                }

                ranges[idx][0] = evaluate(channel.getLower(), stats);
                ranges[idx][1] = evaluate(channel.getUpper(), stats);
            }


            //read only requested coverage bands
            int[] toRead = new int[0];
            final int[] mapping = new int[4];
            for(int i=0;i<4;i++){
                if (bands[i]!=-1){
                    int index = Arrays.binarySearch(toRead, bands[i]);
                    if(index<0) {
                        toRead = Arrays.copyOf(toRead, toRead.length+1);
                        index = toRead.length-1;
                        toRead[index] = bands[i];
                    }
                    mapping[i] = index;
                }
            }
            GridCoverage2D dataCoverage = getObjectiveCoverage(projectedCoverage,
                    renderingContext.getCanvasObjectiveBounds(),
                    renderingContext.getResolution(),
                    renderingContext.getObjectiveToDisplay(),
                    false,toRead);
            if(dataCoverage == null){
                return;
            }

            //check if the reader honored the band request
            final GridSampleDimension[] readDimensions = dataCoverage.getSampleDimensions();
            final List<GridSampleDimension> sampleDimensions = covref.acquireReader().getSampleDimensions(covref.getImageIndex());
            boolean bandReadHonored = (readDimensions.length == toRead.length);
            for (int i=0;bandReadHonored && i<toRead.length;i++) {
                bandReadHonored &= Objects.equals(readDimensions[i].getDescription(), (sampleDimensions == null) ? null : sampleDimensions.get(toRead[i]).getDescription());
            }

            //swap new band indexes
            if (bandReadHonored) {
                for (int i=0;i<4;i++) {
                    if (bands[i]!=-1) bands[i] = mapping[i];
                }
            }

            if (dataCoverage.getViewTypes().contains(ViewType.GEOPHYSICS)) {
                dataCoverage = dataCoverage.view(ViewType.GEOPHYSICS);
            }
            final RenderedImage ri = dataCoverage.getRenderedImage();

            final DynamicRangeStretchProcess p = new DynamicRangeStretchProcess(ri, bands, ranges);
            RenderedImage img = p.executeNow();
            if (img instanceof WritableRenderedImage) GO2Utilities.removeBlackBorder((WritableRenderedImage)img);
            final MathTransform2D trs2D = dataCoverage.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);

            renderCoverage(img, trs2D);

        } catch (CoverageStoreException e) {
            if(e.getCause() instanceof ProjectionException) {
                //out of domain exception
                monitor.exceptionOccured(e, Level.FINE);
            } else {
                monitor.exceptionOccured(e, Level.WARNING);
            }
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
}
