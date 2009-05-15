/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.map.AbstractMapLayer;
import org.geotoolkit.map.DynamicMapLayer;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.WebMapServer;

import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WMSMapLayer extends AbstractMapLayer implements DynamicMapLayer{

    //TODO : we should use the envelope profided by the wms capabilities
    private static final Envelope MAXEXTEND_ENV = new Envelope2D(DefaultGeographicCRS.WGS84, -180, -90, 360, 180);

    private final WebMapServer server;
    private final String[] layers;
    private final Map<String,String> dims = new HashMap<String, String>();
    private String styles = null;
    private String sld = null;
    private String sldBody = null;
    private String format = "image/png";

    public WMSMapLayer(WebMapServer server,String ... layers) {
        super(new DefaultStyleFactory().style());
        this.server = server;
        this.layers = layers;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {
        return MAXEXTEND_ENV;
    }

    public GetMapRequest createGetMapRequest(){
        GetMapRequest request = server.createGetMap();
        request.setLayers(layers);
        return request;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL query(final RenderingContext context) throws PortrayalException{

        if( !(context instanceof RenderingContext2D)){
            throw new PortrayalException("WMSLayer only support rendering for RenderingContext2D");
        }

        final RenderingContext2D context2D = (RenderingContext2D) context;
        final Envelope env = context2D.getCanvasObjectiveBounds();
        Shape rect = context2D.getCanvasDisplayBounds();

        final double rotation = context2D.getCanvas().getController().getRotation();
        final AffineTransform trs = new AffineTransform();
        trs.rotate(rotation);
        rect = trs.createTransformedShape(rect);

        try {
            return query(env, rect.getBounds().getSize());
        } catch (MalformedURLException ex) {
            Logger.getLogger(WMSMapLayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public URL query(final Envelope env, final Dimension rect) throws MalformedURLException  {
        final GetMapRequest request = server.createGetMap();
        request.setEnvelope(env);
        request.setDimension(rect);
        request.setLayers(layers);
        request.setStyles(styles);
        request.setSld(sld);
        request.setSldBody(sldBody);
        request.setFormat(format);
        request.dimensions().putAll(dims);
        return request.getURL();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(RenderingContext context) throws PortrayalException {
        if( !(context instanceof RenderingContext2D)){
            throw new PortrayalException("WMSLayer only support rendering for RenderingContext2D");
        }

        final RenderingContext2D context2D = (RenderingContext2D) context;

        final URL url = query(context);
        final BufferedImage image;

        System.out.println("[WMSMapLayer] : GETMAP request : " + url.toString());

        try {
            image = ImageIO.read(url);
        } catch (IOException io) {
            throw new PortrayalException(io);
        }

        //switch to displayCRS
        context2D.switchToDisplayCRS();

        //draw image centered on top
        //we center it because rotation parameter may have caused the image
        // to be larger than the canvas size, this is a normal behavior since
        // wms layer can not handle rotations.
        Graphics2D g = context2D.getGraphics();
        Dimension dim = context2D.getCanvasDisplayBounds().getSize();
        if(image != null && dim != null){
            double rotation = context2D.getCanvas().getController().getRotation();
            g.translate(dim.width/2, dim.height/2);
            g.rotate(rotation);
            g.drawImage(image, -image.getWidth()/2, -image.getHeight()/2, null);
            g.rotate(-rotation);
            g.translate(-dim.width/2, -dim.height/2);
        }
    }

    public void setStyles(String styles) {
        this.styles = styles;
    }

    public String getStyles() {
        return styles;
    }

    public void setSld(String sld) {
        this.sld = sld;
    }

    public String getSld() {
        return sld;
    }

    public void setSldBody(String sldBody) {
        this.sldBody = sldBody;
    }

    public String getSldBody() {
        return sldBody;
    }

    public void setFormat(String format) {
        this.format = format;
        if(this.format == null){
            format = "image/png";
        }
    }

    public String getFormat() {
        return format;
    }

    public Map<String, String> dimensions() {
        return dims;
    }

}
