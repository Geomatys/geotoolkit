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

package org.geotoolkit.display2d.ext.isoline;

import com.vividsolutions.jts.geom.Coordinate;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessFeatureLayerJ2D;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.container.statefull.StatefullCoverageLayerJ2D;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessMonitor;
import org.geotoolkit.process.ProcessMonitorAdapter;
import org.geotoolkit.process.coverage.kriging.KrigingDescriptor;
import org.geotoolkit.style.MutableStyle;
import org.opengis.feature.Feature;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Sorel Johann (Geomatys)
 * @module pending
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
    public void paintLayer(final RenderingContext2D context) {

        //we abort painting if the layer is not visible.
        if (!item.isVisible()) {
            return;
        }
        
        final CanvasMonitor monitor = context.getMonitor();
        
        final Graphics2D g2 = context.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g2.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);

        FeatureCollection<? extends Feature> collection = item.getCollection();
        try {
            collection = collection.subCollection(item.getQuery());
        } catch (DataStoreException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        }
        

        try {
            final List<DirectPosition> coordinates = new ArrayList<DirectPosition>();
            final FeatureIterator<? extends Feature> iterator = collection.iterator();
            try {
                while (iterator.hasNext()) {
                    final Feature feature = iterator.next();
                    final Coordinate coord = extractor.getValues(context, feature);
                    if(coord == null) continue;
                    final GeneralDirectPosition pos = new GeneralDirectPosition(coord.x, coord.y, coord.z);
                    coordinates.add(pos);
                }
            }catch(Exception ex){
                monitor.exceptionOccured(ex, Level.WARNING);
                return;
            }finally {
                iterator.close();
            }

            final ProcessMonitor redirect = new ProcessMonitorAdapter(){
                @Override
                public void failed(ProcessEvent event) {
                    if(event.getThrowable() != null){
                        monitor.exceptionOccured((Exception)event.getThrowable(), Level.WARNING);
                    }
                }
                @Override
                public void progressing(ProcessEvent event) {
                    if(event.getThrowable() != null){
                        monitor.exceptionOccured((Exception)event.getThrowable(), Level.WARNING);
                    }
                }
            };
            
            final ProcessDescriptor desc = KrigingDescriptor.INSTANCE;
            final Process p = desc.createProcess();
            final ParameterValueGroup input = desc.getInputDescriptor().createValue();
            Parameters.getOrCreate(KrigingDescriptor.IN_POINTS, input)
                    .setValue(coordinates.toArray(new DirectPosition[coordinates.size()]));
            Parameters.getOrCreate(KrigingDescriptor.IN_CRS, input)
                    .setValue(collection.getFeatureType().getCoordinateReferenceSystem());
            Parameters.getOrCreate(KrigingDescriptor.IN_STEP, input)
                    .setValue(step);
            
            p.setInput(input);
            p.setMonitor(redirect);
            p.run();
            final ParameterValueGroup output = p.getOutput();
            final GridCoverage2D coverage = Parameters.value(KrigingDescriptor.OUT_COVERAGE, output);
            final FeatureCollection isolines = Parameters.value(KrigingDescriptor.OUT_LINES, output);
            
            if(coverage != null){            
                if(interpolateCoverageColor){
                    //paint with the black and white palette
                    try {
                        GO2Utilities.portray(context, coverage);
                    } catch (PortrayalException ex) {
                        context.getMonitor().exceptionOccured(ex, Level.WARNING);
                        return;
                    }
                }else if(coverageStyle != null){
                    //paint with the style
                    final CoverageMapLayer covlayer = MapBuilder.createCoverageLayer(coverage, coverageStyle, "test");
                    final StatefullCoverageLayerJ2D graphic = new StatefullCoverageLayerJ2D(getCanvas(), covlayer);
                    graphic.paint(context);
                }
            }
            
            if(isolines != null && isoLineStyle != null){
                final FeatureMapLayer flayer = MapBuilder.createFeatureLayer(isolines, isoLineStyle);
                final StatelessFeatureLayerJ2D graphic = new StatelessFeatureLayerJ2D(getCanvas(), flayer);
                graphic.paint(context);
            }
            
        } catch (DataStoreRuntimeException ex) {
            Logger.getLogger(IsolineGraphicJ2D.class.getName()).log(Level.WARNING, null, ex);
        }

    }

}
