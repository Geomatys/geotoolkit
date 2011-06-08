/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.googlemaps.map;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.googlemaps.GetMapRequest;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;

import org.opengis.display.canvas.Canvas;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.openide.util.Exceptions;

/**
 * Render GoogleMaps layer in default geotoolkit rendering engine.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class GoogleMapsGraphicBuilder implements GraphicBuilder<GraphicJ2D>{

    /**
     * One instance for all GoogleMaps map layers. Object is concurrent.
     */
    static final GoogleMapsGraphicBuilder INSTANCE = new GoogleMapsGraphicBuilder();
    
    private GoogleMapsGraphicBuilder(){};
    
    @Override
    public Collection<GraphicJ2D> createGraphics(final MapLayer layer, final Canvas canvas) {
        if(layer instanceof GoogleMapsMapLayer && canvas instanceof J2DCanvas){
            return Collections.singleton((GraphicJ2D)
                    new GoogleMapsGraphic((J2DCanvas)canvas, (GoogleMapsMapLayer)layer));
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
        final GoogleMapsMapLayer gmlayer = (GoogleMapsMapLayer) layer;
        
        //TODO : how could we generate a proper legend for this layer ...
        final BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        return buffer;
    }

    public static class GoogleMapsGraphic extends AbstractGraphicJ2D{

        private final GoogleMapsMapLayer layer;

        private GoogleMapsGraphic(final J2DCanvas canvas, final GoogleMapsMapLayer layer){
            super(canvas,canvas.getObjectiveCRS2D());
            this.layer = layer;
        }

        @Override
        public void paint(final RenderingContext2D context2D) {
            final CanvasMonitor monitor = context2D.getMonitor();


            final GeneralEnvelope env = new GeneralEnvelope(context2D.getCanvasObjectiveBounds2D());
            final Dimension dim = context2D.getCanvasDisplayBounds().getSize();
            final double[] resolution = context2D.getResolution(context2D.getDisplayCRS());

            //resolution contain dpi adjustments, to obtain an image of the correct dpi
            //we raise the request dimension so that when we reduce it it will have the
            //wanted dpi.
            dim.width /= resolution[0];
            dim.height /= resolution[1];
            
            
            //we make a second correction, since google returned image have labels for a 72dpi
            //and the renderering engine works in 90 by default
//            Number dpi = (Number)context2D.getRenderingHints().get(GO2Hints.KEY_DPI);
//            if(dpi == null){
//                dpi = 90;
//            }
//            dim.width /= dpi.doubleValue()/72d;
//            dim.height /= dpi.doubleValue()/72d;
            
            
            final GeneralEnvelope gmEnv;
            try {
                gmEnv = new GeneralEnvelope(CRS.transform(env, GoogleMapsUtilities.GOOGLE_MERCATOR));
            } catch (TransformException ex) {
                monitor.exceptionOccured(ex, Level.WARNING);
                return;
            }
            
            //ensure we don't go out of the crs envelope
            final Envelope maxExt = CRS.getEnvelope(GoogleMapsUtilities.GOOGLE_MERCATOR);
            gmEnv.intersect(maxExt);
            if(Double.isNaN(gmEnv.getMinimum(0))){ gmEnv.setRange(0, maxExt.getMinimum(0), gmEnv.getMaximum(0));  }
            if(Double.isNaN(gmEnv.getMaximum(0))){ gmEnv.setRange(0, gmEnv.getMinimum(0), maxExt.getMaximum(0));  }
            if(Double.isNaN(gmEnv.getMinimum(1))){ gmEnv.setRange(1, maxExt.getMinimum(1), gmEnv.getMaximum(1));  }
            if(Double.isNaN(gmEnv.getMaximum(1))){ gmEnv.setRange(1, gmEnv.getMinimum(1), maxExt.getMaximum(1));  }
            
            
            final int bestZoomLevel = GoogleMapsUtilities.getBestZoomLevel(gmEnv, dim);            
            final Dimension dimension = new Dimension(640, 640);            
            final double imageResolution = GoogleMapsUtilities.getZoomResolution(bestZoomLevel);
            final List<GetMapRequest> requests = new ArrayList<GetMapRequest>();
                    
            final double mercatorTileWidth = imageResolution * dimension.width;
            final double mercatorTileHeight = imageResolution * dimension.height;
            
            double x= gmEnv.getMinimum(0);
            double y= gmEnv.getMaximum(1);
            //loop on y
            while(y > gmEnv.getMinimum(1)){
                x=gmEnv.getMinimum(0);
                
                //loop on x
                while(x < gmEnv.getMaximum(0)){
                    final GetMapRequest request = layer.getServer().createGetMap();
                    request.setFormat(layer.getFormat());
                    request.setMapType(layer.getMapType());
                    request.setDimension(dimension);
                    request.setZoom(bestZoomLevel);
                    
                    final double centerX = x + mercatorTileWidth/2;
                    final double centerY = y - mercatorTileHeight/2;
                    
                    
                    final GeneralDirectPosition center = new GeneralDirectPosition(GoogleMapsUtilities.GOOGLE_MERCATOR);
                    center.setOrdinate(0, centerX);
                    center.setOrdinate(1, centerY);
                    request.setCenter(center);
                    
                    
                    final Envelope tileEnv = GoogleMapsUtilities.getEnvelope(
                        request.getCenter(), request.getDimension(), request.getZoom());
                    System.out.println(tileEnv);
                    
                    requests.add(request);
                    x += mercatorTileWidth;
                }
                
                y -= mercatorTileHeight;
            }
            
            
            
            
            for(final GetMapRequest request : requests){
            
                final BufferedImage image;
                InputStream is = null;
                try {
                    is = request.getResponseStream();
                    image = ImageIO.read(is);
                } catch (IOException io) {
                    monitor.exceptionOccured(new PortrayalException(io), Level.WARNING);
                    continue;
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ex) {
                            monitor.exceptionOccured(ex, Level.WARNING);
                        }
                    }
                }

                if (image == null) {
                    monitor.exceptionOccured(new PortrayalException("GoogleMaps server didn't return an image."), Level.WARNING);
                    continue;
                }

                final Envelope tileEnv = GoogleMapsUtilities.getEnvelope(
                        request.getCenter(), request.getDimension(), request.getZoom());
                
                try {
                    final GridCoverageFactory gc = new GridCoverageFactory();
                    final GridCoverage2D coverage = gc.create("gm", image, tileEnv);
                    GO2Utilities.portray(context2D, coverage);
                } catch (PortrayalException ex) {
                    monitor.exceptionOccured(ex, Level.WARNING);
                    continue;
                }
            }
        }

        @Override
        public List<Graphic> getGraphicAt(final RenderingContext context, final SearchArea mask, final VisitFilter filter, final List<Graphic> graphics) {

            if(!(context instanceof RenderingContext2D)){
                return graphics;
            }

            graphics.add(this);

            return graphics;
        }

    }

}
