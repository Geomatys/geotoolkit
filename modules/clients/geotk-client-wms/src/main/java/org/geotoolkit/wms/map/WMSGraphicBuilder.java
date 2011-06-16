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
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.imageio.ImageIO;

import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.AbstractTiledGraphic;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.wms.GetLegendRequest;
import org.geotoolkit.wms.GetMapRequest;

import org.opengis.display.canvas.Canvas;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Render WMS layer in default geotoolkit rendering engine.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSGraphicBuilder implements GraphicBuilder<GraphicJ2D>{

    /**
     * One instance for all WMS map layers. Object is concurrent.
     */
    static final WMSGraphicBuilder INSTANCE = new WMSGraphicBuilder();

    protected WMSGraphicBuilder(){};

    @Override
    public Collection<GraphicJ2D> createGraphics(final MapLayer layer, final Canvas canvas) {
        if(layer instanceof WMSMapLayer && canvas instanceof J2DCanvas){
            return Collections.singleton((GraphicJ2D)
                    new WMSGraphic((J2DCanvas)canvas, (WMSMapLayer)layer));
        }else{
            return Collections.emptyList();
        }
    }

    @Override
    public Class<GraphicJ2D> getGraphicType() {
        return GraphicJ2D.class;
    }

    @Override
    public Image getLegend(final MapLayer layer) throws PortrayalException {
        final WMSMapLayer wmslayer = (WMSMapLayer) layer;

        final GetLegendRequest request = wmslayer.getServer().createGetLegend();
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

    public static class WMSGraphic extends AbstractTiledGraphic{

        protected final WMSMapLayer layer;

        protected WMSGraphic(final J2DCanvas canvas, final WMSMapLayer layer){
            super(canvas,canvas.getObjectiveCRS2D());
            this.layer = layer;
        }

        @Override
        public void paint(final RenderingContext2D context2D) {
            final CanvasMonitor monitor = context2D.getMonitor();

            final GetMapRequest request = layer.getServer().createGetMap();

            //Filling the request header map from the map of the layer's server
            final Map<String, String> headerMap = layer.getServer().getRequestHeaderMap();
            if (headerMap != null) {
                request.getHeaderMap().putAll(headerMap);
            }

            final GeneralEnvelope env = new GeneralEnvelope(context2D.getCanvasObjectiveBounds());
            final Dimension dim = context2D.getCanvasDisplayBounds().getSize();
            final double[] resolution = context2D.getResolution(context2D.getDisplayCRS());

            //resolution contain dpi adjustments, to obtain an image of the correct dpi
            //we raise the request dimension so that when we reduce it it will have the
            //wanted dpi.
            dim.width /= resolution[0];
            dim.height /= resolution[1];

            try {
                layer.prepareQuery(request, env, dim, null);
            } catch (TransformException ex) {
                monitor.exceptionOccured(new PortrayalException(ex), Level.WARNING);
                return;
            } catch (FactoryException ex) {
                monitor.exceptionOccured(new PortrayalException(ex), Level.WARNING);
                return;
            }

            //render a single tile            
            final Map<Entry<CoordinateReferenceSystem,MathTransform>,Request> queries = 
                    new HashMap<Entry<CoordinateReferenceSystem, MathTransform>, Request>();
            
            final Entry<CoordinateReferenceSystem,MathTransform> key;
            try {
                final CoordinateReferenceSystem crs2d = CRSUtilities.getCRS2D(env.getCoordinateReferenceSystem());
                final Envelope env2D = CRS.transform(env, crs2d);
                final AffineTransform2D gridToCRS = new AffineTransform2D(GO2Utilities.toAffine(dim, env2D));

                key = new SimpleImmutableEntry<CoordinateReferenceSystem, MathTransform>(
                        CRSUtilities.getCRS2D(env.getCoordinateReferenceSystem()), gridToCRS);
            } catch (TransformException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
                return;
            }
            
            queries.put(key, request);            
            paint(context2D, queries);            
        }

        @Override
        public List<Graphic> getGraphicAt(final RenderingContext context, final SearchArea mask, final VisitFilter filter, final List<Graphic> graphics) {

            if(!(context instanceof RenderingContext2D)){
                return graphics;
            }

            graphics.add(this);
            return graphics;
        }

        public URL getFeatureInfo(final RenderingContext context, final SearchArea mask, 
                final String infoFormat, final int featureCount)
                throws TransformException, FactoryException,
                MalformedURLException, NoninvertibleTransformException{
            
            final RenderingContext2D ctx2D = (RenderingContext2D) context;
            final DirectPosition center = mask.getDisplayGeometry().getCentroid();

            final URL url;
                url = layer.queryFeatureInfo(
                        ctx2D.getCanvasObjectiveBounds(),
                        ctx2D.getCanvasDisplayBounds().getSize(),
                        (int) center.getOrdinate(0),
                        (int) center.getOrdinate(1),
                        layer.getLayerNames(),
                        infoFormat,featureCount);

            return url;
        }

    }

}
