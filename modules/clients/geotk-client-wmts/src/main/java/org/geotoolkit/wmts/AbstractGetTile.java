/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.wmts;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.nio.IOUtilities;

/**
 * Abstract implementation of {@link GetTileRequest}, which defines the parameters for
 * a GetTile request.
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public abstract class AbstractGetTile extends AbstractRequest implements GetTileRequest {
    /**
     * Default logger for all GetMap requests.
     */
    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.wmts");

    /**
     * The version to use for this webservice request.
     */
    protected final String version;
    protected String layer         = null;
    protected String style         = null;
    protected String format        = "image/png";
    protected String tileMatrixSet = null;
    protected String tileMatrix    = null;
    protected Integer tileRow       = null;
    protected Integer tileCol       = null;
    protected String resourceUrl    = null;

    protected final Map<String,String> dims = new HashMap<String, String>();


    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetTile(final String serverURL,final String version, final ClientSecurity security){
        super(serverURL,security,null);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getLayer() {
        return layer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(String layer) {
        this.layer = layer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getTileMatrixSet() {
        return tileMatrixSet;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTileMatrixSet(String tm) {
        this.tileMatrixSet = tm;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getFormat() {
        return format;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getTileMatrix() {
        return tileMatrix;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTileMatrix(String ex) {
        this.tileMatrix = ex;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getStyle() {
        return style;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getTileRow(){
        return tileRow;
    }
        /**
     * {@inheritDoc }
     */
    @Override
    public void setTileRow(int tr){
        this.tileRow = tr;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getTileCol(){
        return tileCol;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTileCol(int tr){
        this.tileCol = tr;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getResourceURL() {
        return resourceUrl;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setResourceURL(String url) {
        this.resourceUrl = url;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String,String> dimensions(){
        return dims;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        if (layer == null) {
            throw new IllegalArgumentException("Layer is not defined");
        }
        
        
        String format = this.format;
        if(resourceUrl!=null) {
            //replace mime type by extension only if we use a Rest resource URL
            format = findExtension(this.format);
        }

        requestParameters.put("SERVICE",    "WMTS");
        requestParameters.put("REQUEST",    "GetTile");
        requestParameters.put("VERSION",    version);
        requestParameters.put("LAYER",      layer);
        requestParameters.put("FORMAT",     format);

        if (style != null) {
            requestParameters.put("STYLE", style);
        } else {
            requestParameters.put("STYLE","");
        }
        requestParameters.put("TILEMATRIXSET",tileMatrixSet);
        requestParameters.put("TILEMATRIX",   tileMatrix);
        requestParameters.put("TILECOL",      tileCol + "");
        requestParameters.put("TILEROW",      tileRow + "");

        requestParameters.putAll(dimensions());
    }

    /**
     * Generate mimetype file extension
     * 
     * @param mimeType
     * @return 
     */
    private static String findExtension(String mimeType) {
        if (mimeType==null) return null;
        mimeType = mimeType.toLowerCase();
        
        //known wmts cases
        switch (mimeType) {
            case "image/png" :
            case "image/png8" : return "png";
            case "image/jpeg" : return "jpeg";
            case "image/gif" : return "gif";
            case "image/bmp" : return "bmp";
        }
        
        //unknown case, try to split it based on usuel patterns
        //common case application/xml
        final int slashIdx = mimeType.indexOf('/');
        if (slashIdx>=0) {
            mimeType = mimeType.substring(slashIdx+1);
        }
        //case image/dicom+json
        final int plusIdx= mimeType.indexOf('+');
        if (plusIdx>=0) {
            mimeType = mimeType.substring(0,plusIdx);
        }
        //case image/dicom, format:gzip
        final int partIdx= mimeType.indexOf(',');
        if (partIdx>=0) {
            mimeType = mimeType.substring(0,partIdx);
        }
        
        return mimeType.trim();
    }
    
}
