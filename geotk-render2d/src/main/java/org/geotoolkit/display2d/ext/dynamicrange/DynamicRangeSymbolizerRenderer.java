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

import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.map.Presentation;
import org.apache.sis.metadata.iso.content.DefaultCoverageDescription;
import org.apache.sis.map.MapLayer;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.presentation.RasterPresentation;
import org.geotoolkit.display2d.style.renderer.AbstractCoverageSymbolizerRenderer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.geotoolkit.math.Histogram;
import org.geotoolkit.processing.image.dynamicrange.DynamicRangeStretchProcess;
import org.geotoolkit.storage.coverage.DefaultSampleDimensionExt;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.content.AttributeGroup;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.content.RangeDimension;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DynamicRangeSymbolizerRenderer extends AbstractCoverageSymbolizerRenderer<CachedDynamicRangeSymbolizer>{

    public DynamicRangeSymbolizerRenderer(SymbolizerRendererService service, CachedDynamicRangeSymbolizer symbol, RenderingContext2D context) {
        super(service, symbol, context);
    }

    @Override
    public Stream<Presentation> presentations(MapLayer layer, Resource resource) {
        if (resource instanceof GridCoverageResource) {
            try {
                final GridCoverageResource covref = (GridCoverageResource) resource;

                final DynamicRangeSymbolizer symbolizer = symbol.getSource();

                final int[] bands = new int[]{-1,-1,-1,-1};
                final double[][] ranges = new double[][]{{-1,-1},{-1,-1},{-1,-1},{-1,-1}};

                final Map<String,Object> stats = new HashMap<>();

                //we can avoid loading metadata if all ranges are literals,
                //metadata can be expensive because of histogram informations
                boolean allLiteral = true;
                for (DynamicRangeSymbolizer.DRChannel channel : symbolizer.getChannels()) {
                    allLiteral &= channel.getLower().getValue() instanceof Literal;
                    allLiteral &= channel.getUpper().getValue() instanceof Literal;
                }

                CoverageDescription covdesc = null;

                if (!allLiteral) {
                    if (covdesc == null) {
                        Metadata metadata = covref.getMetadata();
                        if (metadata != null) {
                            for (ContentInformation ci : metadata.getContentInfo()) {
                                if (ci instanceof CoverageDescription) {
                                    covdesc = (CoverageDescription) ci;
                                    break;
                                }
                            }
                        }
                    }
                    if (covdesc == null) {
                        //create an empty description
                        covdesc = new DefaultCoverageDescription();
                    }

                }

                for (DynamicRangeSymbolizer.DRChannel channel : symbolizer.getChannels()) {
                    final Integer bandIdx;
                    try {
                        bandIdx = Integer.valueOf(channel.getBand());
                    } catch(NumberFormatException ex) {
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
                        for (AttributeGroup attg : covdesc.getAttributeGroups()){
                            for (RangeDimension rd : attg.getAttributes()){
                                if (!(rd instanceof org.opengis.metadata.content.SampleDimension)) continue;
                                final int i = Integer.parseInt(rd.getSequenceIdentifier().tip().toString());
                                if(i==bandIdx){
                                    final org.opengis.metadata.content.SampleDimension sd = (org.opengis.metadata.content.SampleDimension) rd;
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
                for (int i=0;i<4;i++) {
                    if (bands[i]!=-1){
                        int index = 0;
                        boolean contained = false;
                        for (int k=0;k<toRead.length;k++) {
                            if(toRead[k]==bands[i]) {
                                index = k;
                                contained = true;
                                break;
                            }
                        }
                        if (!contained) {
                            toRead = Arrays.copyOf(toRead, toRead.length+1);
                            index = toRead.length-1;
                            toRead[index] = bands[i];
                        }
                        mapping[i] = index;
                    }
                }
                GridCoverage dataCoverage;
                try {
                    dataCoverage = getObjectiveCoverage(covref, renderingContext.getGridGeometry(), false, toRead);
                } catch (NoSuchDataException ex) {
                    return Stream.empty();
                }
                if (dataCoverage == null) {
                    return Stream.empty();
                }

                //check if the reader honored the band request
                final List<SampleDimension> readDimensions = dataCoverage.getSampleDimensions();
                final List<SampleDimension> sampleDimensions = covref.getSampleDimensions();
                boolean bandReadHonored = (readDimensions.size() == toRead.length);
                for (int i=0;bandReadHonored && i<toRead.length;i++) {
                    bandReadHonored &= Objects.equals(readDimensions.get(i).getName(), (sampleDimensions == null) ? null : sampleDimensions.get(toRead[i]).getName());
                }

                //swap new band indexes
                if (bandReadHonored) {
                    for (int i=0;i<4;i++) {
                        if (bands[i]!=-1) bands[i] = mapping[i];
                    }
                }

                dataCoverage = dataCoverage.forConvertedValues(true);
                final RenderedImage ri = dataCoverage.render(null);

                final DynamicRangeStretchProcess p = new DynamicRangeStretchProcess(ri, bands, ranges);
                RenderedImage img = p.executeNow();
                if (img instanceof WritableRenderedImage) GO2Utilities.removeBlackBorder((WritableRenderedImage)img);

                final GridCoverageBuilder gcb = new GridCoverageBuilder();
                gcb.setDomain(dataCoverage.getGridGeometry());
                gcb.setValues(img);
                final GridCoverage recolored = gcb.build();

                final RasterPresentation presentation = new RasterPresentation(layer, layer.getData(), recolored);
                presentation.forGrid(renderingContext);
                return Stream.of(presentation);

            } catch (DataStoreException e) {
                if (e.getCause() instanceof ProjectionException) {
                    //out of domain exception
                    monitor.exceptionOccured(e, Level.FINE);
                } else {
                    monitor.exceptionOccured(e, Level.WARNING);
                }
            } catch (Exception e) {
                monitor.exceptionOccured(e, Level.WARNING);
            }
        }

        return Stream.empty();
    }

    private static double evaluate(DynamicRangeSymbolizer.DRBound bound, Map<String,Object> stats) throws PortrayalException {
        final String mode = bound.getMode();
        if (DynamicRangeSymbolizer.DRBound.MODE_EXPRESSION.equalsIgnoreCase(mode)) {
            final Expression exp = bound.getValue();
            final Number val = (Number) exp.apply(stats);
            return (val==null) ? Double.NaN : val.doubleValue();
        } else if (DynamicRangeSymbolizer.DRBound.MODE_PERCENT.equalsIgnoreCase(mode)) {
            final long[] histo = (long[]) stats.get(DynamicRangeSymbolizer.PROPERTY_HISTO);
            final Double histoMin = (Double) stats.get(DynamicRangeSymbolizer.PROPERTY_HISTO_MIN);
            final Double histoMax = (Double) stats.get(DynamicRangeSymbolizer.PROPERTY_HISTO_MAX);
            if (histo == null || histoMin == null || histoMax == null) {
                //we don't have the informations
                LOGGER.log(Level.INFO, "Missing histogram information for correct rendering.");
                return Double.NaN;
            } else {
                final Expression exp = bound.getValue();
                final Number val = (Number) exp.apply(stats);
                final Histogram h = new Histogram(histo, histoMin, histoMax);
                return h.getValueAt(val.doubleValue()/100.0);
            }
        } else {
            throw new PortrayalException("Unknwoned mode "+mode);
        }
    }
}
