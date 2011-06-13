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
package org.geotoolkit.googlemaps;

import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.logging.Logging;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultGetMap extends AbstractRequest implements GetMapRequest{

    private static final Logger LOGGER = Logging.getLogger(DefaultGetMap.class);
    
    private static final String PARAMETER_MAPTYPE   = "maptype";
    private static final String PARAMETER_ZOOM      = "zoom";
    private static final String PARAMETER_CENTER    = "center";
    private static final String PARAMETER_DIMENSION = "size";
    private static final String PARAMETER_FORMAT    = "format";
    private static final String PARAMETER_SENSOR    = "sensor";
    private static final String PARAMETER_KEY       = "key";
    
    private String mapType = null;
    private int zoom = 0;
    private DirectPosition center = null;
    private Dimension dimension = null;
    private String format = null;
    private String key = null;
    
    public DefaultGetMap(final StaticGoogleMapsServer server, final String key){
        super(server);
        this.key = key;
    }
    
    @Override
    public String getMapType() {
        return mapType;
    }

    @Override
    public void setMapType(final String maptype) {
        this.mapType = maptype;
    }

    @Override
    public int getZoom() {
        return zoom;
    }

    @Override
    public void setZoom(final int zoom) {
        this.zoom = zoom;
    }

    @Override
    public DirectPosition getCenter() {
        return center;
    }

    @Override
    public void setCenter(final DirectPosition position) {
        this.center = position;
    }

    @Override
    public Dimension getDimension() {
        return dimension;
    }

    @Override
    public void setDimension(final Dimension dim) {
        this.dimension = dim;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(final String format) {
        this.format = format;
    }
    
    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        
        ArgumentChecks.ensureNonNull("map type", mapType);
        ArgumentChecks.ensureNonNull("dimension", dimension);
        ArgumentChecks.ensureNonNull("center", center);
        ArgumentChecks.ensureNonNull("format", format);
        
        
        //center must be expressed in lat/lon
        DirectPosition position = center;
        try{
            final MathTransform trs = CRS.findMathTransform(
                    center.getCoordinateReferenceSystem(), DefaultGeographicCRS.WGS84);
            position = trs.transform(position, null);
        }catch(TransformException ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }catch(FactoryException ex){
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }
        
        requestParameters.put(PARAMETER_MAPTYPE, mapType);
        requestParameters.put(PARAMETER_FORMAT, format);
        requestParameters.put(PARAMETER_ZOOM, Integer.toString(zoom));
        requestParameters.put(PARAMETER_DIMENSION,  (int)dimension.getWidth() +"x"+ (int)dimension.getHeight() );        
        requestParameters.put(PARAMETER_CENTER, position.getOrdinate(1) +","+ position.getOrdinate(0));
        
        //dont know what exactly this do but necessary
        requestParameters.put(PARAMETER_SENSOR, "false");
        
        //user key if present
        if(key != null){
            requestParameters.put(PARAMETER_KEY, key);
        }
        
    }

}
