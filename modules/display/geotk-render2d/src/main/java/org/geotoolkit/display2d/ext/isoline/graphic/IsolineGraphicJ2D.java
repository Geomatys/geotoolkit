/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Geomatys
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

package org.geotoolkit.display2d.ext.isoline.graphic;

import org.locationtech.jts.geom.Coordinate;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessCoverageLayerJ2D;
import org.geotoolkit.display2d.container.stateless.StatelessFeatureLayerJ2D;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessListener;
import org.geotoolkit.processing.ProcessListenerAdapter;
import org.geotoolkit.processing.coverage.kriging.KrigingDescriptor;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.map.MapLayer;
import org.opengis.feature.Feature;

/**
 *
 * @author Sorel Johann (Geomatys)
 * @module
 */
public class IsolineGraphicJ2D extends StatelessFeatureLayerJ2D {

    private final ValueExtractor extractor;
    private MutableStyle isoPointStyle = null;
    private MutableStyle isoLineStyle = null;
    private MutableStyle coverageStyle = null;
    private boolean interpolateCoverageColor = true;
    private int step = 10;

    public IsolineGraphicJ2D(final J2DCanvas canvas, final FeatureMapLayer layer, final ValueExtractor extractor) {
        super(canvas, layer);
        this.extractor = extractor;
    }

    public void setStep(final int step) {
        this.step = step;
    }

    public double getStep() {
        return step;
    }

    public void setCoverageStyle(final MutableStyle coverageStyle) {
        this.coverageStyle = coverageStyle;
    }

    public void setIsoLineStyle(final MutableStyle isoLineStyle) {
        this.isoLineStyle = isoLineStyle;
    }

    public void setIsoPointStyle(final MutableStyle isoPointStyle) {
        this.isoPointStyle = isoPointStyle;
    }

    public MutableStyle getCoverageStyle() {
        return coverageStyle;
    }

    public MutableStyle getIsoLineStyle() {
        return isoLineStyle;
    }

    public MutableStyle getIsoPointStyle() {
        return isoPointStyle;
    }

    public void setInterpolateCoverageColor(final boolean interpolateCoverageColor) {
        this.interpolateCoverageColor = interpolateCoverageColor;
    }

    public boolean isInterpolateCoverageColor() {
        return interpolateCoverageColor;
    }

    @Override
    public boolean paintLayer(final RenderingContext2D context) {

        //we abort painting if the layer is not visible.
        if (!item.isVisible()) {
            return false;
        }

        final CanvasMonitor monitor = context.getMonitor();

        final Graphics2D g2 = context.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g2.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);

        FeatureSet collection = item.getResource();
        try {
            collection = collection.subset(item.getQuery());
        } catch (DataStoreException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return false;
        }

        boolean isRendered = false;
        double minx = Double.NaN;
        double miny = Double.NaN;
        double maxx = Double.NaN;
        double maxy = Double.NaN;
        try {
            final List<DirectPosition> coordinates = new ArrayList<>();
            try (Stream<Feature> stream = collection.features(false)){
                final Iterator<Feature> iterator = stream.iterator();
                while (iterator.hasNext()) {
                    final Feature feature = iterator.next();
                    final Coordinate coord = extractor.getValues(context, feature);
                    if(coord == null) continue;
                    final GeneralDirectPosition pos = new GeneralDirectPosition(coord.x, coord.y, coord.z);
                    coordinates.add(pos);
                    minx = Double.isNaN(minx) ? coord.x : Math.min(minx, coord.x);
                    maxx = Double.isNaN(maxx) ? coord.x : Math.max(maxx, coord.x);
                    miny = Double.isNaN(miny) ? coord.y : Math.min(miny, coord.y);
                    maxy = Double.isNaN(maxy) ? coord.y : Math.max(maxy, coord.y);
                }
            }catch(Exception ex){
                monitor.exceptionOccured(ex, Level.WARNING);
                return false;
            }

            if(coordinates.isEmpty()){
                //nothing to render
                return false;
            }

            final CoordinateReferenceSystem crs = FeatureExt.getCRS(collection.getType());

            final GeneralEnvelope env = new GeneralEnvelope(crs);
            env.setRange(0, minx, maxx);
            env.setRange(1, miny, maxy);
            final Envelope objenv = Envelopes.transform(env, context.getObjectiveCRS2D());
            final double[] res = context.getResolution();

            if(objenv.getSpan(0) <= res[0]*8 || objenv.getSpan(1) <= res[1]*8){
                //envelope is too small, do not paint
                return false;
            }


            final ProcessListener redirect = new ProcessListenerAdapter(){
                @Override
                public void failed(ProcessEvent event) {
                    if(event.getException() != null){
                        monitor.exceptionOccured((Exception)event.getException(), Level.WARNING);
                    }
                }
                @Override
                public void progressing(ProcessEvent event) {
                    if(event.getException() != null){
                        monitor.exceptionOccured((Exception)event.getException(), Level.WARNING);
                    }
                }
            };

            final ProcessDescriptor desc = KrigingDescriptor.INSTANCE;
            final Parameters input = Parameters.castOrWrap(desc.getInputDescriptor().createValue());
            input.getOrCreate(KrigingDescriptor.IN_POINTS)
                    .setValue(coordinates.toArray(new DirectPosition[coordinates.size()]));
            input.getOrCreate(KrigingDescriptor.IN_CRS)
                    .setValue(crs);
            input.getOrCreate(KrigingDescriptor.IN_STEP)
                    .setValue(step);
            input.getOrCreate(KrigingDescriptor.IN_DIMENSION)
                    .setValue(new Dimension(150, 150));
            final Process p = desc.createProcess(input);

            p.addListener(redirect);
            final Parameters output;
            try {
                output = Parameters.castOrWrap(p.call());
            } catch (ProcessException ex) {
                getLogger().log(Level.WARNING, null, ex);
                return false;
            }
            final GridCoverage2D coverage = output.getValue(KrigingDescriptor.OUT_COVERAGE);
            final FeatureCollection isolines = output.getValue(KrigingDescriptor.OUT_LINES);

            if(coverage != null){
                if(interpolateCoverageColor){
                    //paint with the black and white palette
                    try {
                        isRendered = GO2Utilities.portray(context, coverage);
                    } catch (PortrayalException ex) {
                        context.getMonitor().exceptionOccured(ex, Level.WARNING);
                        return false;
                    }
                }else if(coverageStyle != null){
                    //paint with the style
                    final MapLayer covlayer = MapBuilder.createCoverageLayer(coverage, coverageStyle, "test");
                    final StatelessCoverageLayerJ2D graphic = new StatelessCoverageLayerJ2D(getCanvas(), covlayer);
                    isRendered = graphic.paint(context);
                }
            }

            if(isolines != null && isoLineStyle != null){
                final FeatureMapLayer flayer = MapBuilder.createFeatureLayer(isolines, isoLineStyle);
                final StatelessFeatureLayerJ2D graphic = new StatelessFeatureLayerJ2D(getCanvas(), flayer);
                isRendered |= graphic.paint(context);
            }
        } catch (TransformException ex) {
            getLogger().log(Level.WARNING, null, ex);
        } catch (FeatureStoreRuntimeException ex) {
            getLogger().log(Level.WARNING, null, ex);
        } catch (DataStoreException ex) {
            getLogger().log(Level.WARNING, null, ex);
        }
        return isRendered;
    }

}
