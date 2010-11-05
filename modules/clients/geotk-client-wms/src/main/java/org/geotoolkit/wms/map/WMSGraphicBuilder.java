/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.wms.map;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import javax.imageio.ImageIO;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.wms.GetLegendRequest;

import org.opengis.display.canvas.Canvas;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Render WMS layer in default geotoolkit rendering engine.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class WMSGraphicBuilder implements GraphicBuilder<GraphicJ2D>{

    /**
     * One instance for all WMS map layers. Object is concurrent.
     */
    static final WMSGraphicBuilder INSTANCE = new WMSGraphicBuilder();

    private WMSGraphicBuilder(){};

    @Override
    public Collection<GraphicJ2D> createGraphics(MapLayer layer, Canvas canvas) {
        if(layer instanceof WMSMapLayer && canvas instanceof ReferencedCanvas2D){
            return Collections.singleton((GraphicJ2D)
                    new WMSGraphic((ReferencedCanvas2D)canvas, (WMSMapLayer)layer));
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public Class<GraphicJ2D> getGraphicType() {
        return GraphicJ2D.class;
    }

    @Override
    public Image getLegend(MapLayer layer) throws PortrayalException {
        final WMSMapLayer wmslayer = (WMSMapLayer) layer;

        final GetLegendRequest request = wmslayer.getServer().creategetLegend();
        request.setLayer(wmslayer.getLayerNames()[0]);

        final BufferedImage buffer;
        try {
            buffer = ImageIO.read(request.getURL());
        } catch (MalformedURLException ex) {
            throw new PortrayalException(ex);
        } catch (IOException ex) {
            throw new PortrayalException(ex);
        }

        return buffer;
    }

    private static class WMSGraphic extends AbstractGraphicJ2D{

        private final WMSMapLayer layer;

        private WMSGraphic(ReferencedCanvas2D canvas, WMSMapLayer layer){
            super(canvas,canvas.getObjectiveCRS());
            this.layer = layer;
        }

        @Override
        public void paint(RenderingContext2D context2D) {
            final CanvasMonitor monitor = context2D.getMonitor();

            CoordinateReferenceSystem queryCrs = context2D.getObjectiveCRS2D();
            Envelope env = context2D.getCanvasObjectiveBounds();
            //check if we must make the  coverage reprojection ourself--------------
            if (layer.isUseLocalReprojection()) {
                try {
                    if (!layer.supportCRS(context2D.getObjectiveCRS2D())) {
                        queryCrs = layer.findOriginalCRS();
                        if(queryCrs == null){
                            //last chance use : CRS:84
                            queryCrs = DefaultGeographicCRS.WGS84;
                        }
                        env = CRS.transform(env, queryCrs);
                    }
                } catch (FactoryException ex) {
                    monitor.exceptionOccured(ex, Level.WARNING);
                } catch (TransformException ex) {
                    monitor.exceptionOccured(ex, Level.WARNING);
                }
            }

            final Dimension dim = context2D.getCanvasDisplayBounds().getSize();

            //resolution contain dpi adjustments, to obtain an image of the correct dpi
            //we raise the request dimension so that when we reduce it it will have the
            //wanted dpi.
            final double[] resolution = context2D.getResolution(context2D.getDisplayCRS());
            dim.width /= resolution[0];
            dim.height /= resolution[1];

            final URL url;
            try {
                url = layer.query(env, dim);
            } catch (MalformedURLException ex) {
                monitor.exceptionOccured(new PortrayalException(ex), Level.WARNING);
                return;
            }

            getLogger().log(Level.WARNING, "[WMSMapLayer] : GETMAP request : {0}", url);

            final BufferedImage image;
            try {
                image = ImageIO.read(url);
            } catch (IOException io) {
                monitor.exceptionOccured(new PortrayalException(io), Level.WARNING);
                return;
            }

            if (image == null) {
                monitor.exceptionOccured(new PortrayalException("WMS server didn't return an image."), Level.WARNING);
                return;
            }

            final GridCoverageFactory gc = new GridCoverageFactory();
            final GridCoverage2D coverage = gc.create("wms", image, env);
            try {
                GO2Utilities.portray(context2D, coverage);
            } catch (PortrayalException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
                return;
            }
        }

        @Override
        public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
            return graphics;
        }
        
    }

}
