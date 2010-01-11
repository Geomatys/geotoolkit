/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.map.AbstractMapLayer;
import org.geotoolkit.map.DynamicMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.WebMapServer;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSMapLayer extends AbstractMapLayer implements DynamicMapLayer{

    private static final CoordinateReferenceSystem EPSG_4326;

    static {
        CoordinateReferenceSystem crs = null;
        try {
            crs = CRS.decode("EPSG:4326");
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(WMSMapLayer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(WMSMapLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        EPSG_4326 = crs;
    }

    /**
     * Configure the politic when the requested envelope is in CRS:84.
     * Some servers are not strict on axis order or crs definitions.
     * that's why we need this.
     */
    public static enum CRS84Politic{
        STRICT,
        CONVERT_TO_EPSG4326
    }

    /**
     * Configure the politic when the requested envelope is in EPSG:4326.
     * Some servers are not strict on axis order or crs definitions.
     * that's why we need this.
     */
    public static enum EPSG4326Politic{
        STRICT,
        CONVERT_TO_CRS84
    }


    //TODO : we should use the envelope profided by the wms capabilities
    private static final Envelope MAXEXTEND_ENV = new Envelope2D(DefaultGeographicCRS.WGS84, -180, -90, 360, 180);

    private final WebMapServer server;
    private final Map<String,String> dims = new HashMap<String, String>();
    private String[] layers;
    private String styles = null;
    private String sld = null;
    private String sldBody = null;
    private String format = "image/png";
    private CRS84Politic crs84Politic = CRS84Politic.STRICT;
    private EPSG4326Politic epsg4326Politic = EPSG4326Politic.STRICT;

    public WMSMapLayer(WebMapServer server,String ... layers) {
        super(new DefaultStyleFactory().style());
        this.server = server;
        this.layers = layers;
    }

    public WebMapServer getServer(){
        return server;
    }

    public void setCrs84Politic(CRS84Politic crs84Politic) {
        if(crs84Politic == null){
            throw new NullPointerException("CRS84 politic can not be null.");
        }
        this.crs84Politic = crs84Politic;
    }

    public CRS84Politic getCrs84Politic() {
        return crs84Politic;
    }

    public void setEpsg4326Politic(EPSG4326Politic epsg4326Politic) {
        if(epsg4326Politic == null){
            throw new NullPointerException("EPSG4326 politic can not be null.");
        }
        this.epsg4326Politic = epsg4326Politic;
    }

    public EPSG4326Politic getEpsg4326Politic() {
        return epsg4326Politic;
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
        Envelope env = context2D.getCanvasObjectiveBounds();

        //check the politics, the distant wms server might not be strict on axis orders
        // nor in it's crs definitions between CRS:84 and EPSG:4326

        //check CRS84 politic---------------------------------------------------
        if(crs84Politic != CRS84Politic.STRICT){
            if(CRS.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), DefaultGeographicCRS.WGS84)){
                switch(crs84Politic){
                    case CONVERT_TO_EPSG4326 :
                        env = new Envelope2D(env);
                        ((Envelope2D)env).setCoordinateReferenceSystem(EPSG_4326);
                        break;
                }
            }
        }

        //check EPSG4326 politic------------------------------------------------
        if(epsg4326Politic != EPSG4326Politic.STRICT){
            if(CRS.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), DefaultGeographicCRS.WGS84)){
                switch(epsg4326Politic){
                    case CONVERT_TO_CRS84:
                        env = new Envelope2D(env);
                        ((Envelope2D)env).setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);
                        break;
                }
            }
        }

        Shape rect = context2D.getCanvasDisplayBounds();

        final double rotation = context2D.getCanvas().getController().getRotation();
        final AffineTransform trs = new AffineTransform();
        trs.rotate(rotation);
        rect = trs.createTransformedShape(rect);

        try {
            return query(env, rect.getBounds().getSize());
        } catch (MalformedURLException ex) {
            Logging.getLogger(WMSMapLayer.class).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private URL query(final Envelope env, final Dimension rect) throws MalformedURLException  {
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

    public void setLayerNames(String ... names){
        this.layers = names;
    }

    public String[] getLayerNames(){
        return layers.clone();
    }

    public String getCombinedLayerNames(){
        StringBuilder sb = new StringBuilder();
        for(String str : layers){
            sb.append(str).append(',');
        }
        if(sb.toString().endsWith(",")){
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
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
