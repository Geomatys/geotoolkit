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
package org.geotoolkit.googlemaps.model;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.CachedPyramidSet;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.data.multires.Pyramids;
import org.geotoolkit.googlemaps.GetMapRequest;
import org.geotoolkit.googlemaps.GoogleCoverageResource;
import org.geotoolkit.googlemaps.StaticGoogleMapsClient;
import org.geotoolkit.data.multires.DefaultPyramid;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GoogleMapsPyramidSet extends CachedPyramidSet{

    public static final int BASE_TILE_SIZE = 256;

    public static final CoordinateReferenceSystem GOOGLE_MERCATOR;
    public static final Envelope MERCATOR_EXTEND;
    public static final BufferedImage OVERLOAD;


    /**
     * Resolution at zoom 0 level. in meter by pixel.
     */
    public static final double ZOOM_ZERO_RESOLUTION;

    static {
        try {
            GOOGLE_MERCATOR = CRS.forCode("EPSG:3857");
        } catch (FactoryException ex) {
            throw new RuntimeException(ex);
        }


        //this is not an exact extent, google once again do not respect anything
        //MERCATOR_EXTEND = CRS.getEnvelope(GoogleMapsUtilities.GOOGLE_MERCATOR);
        MERCATOR_EXTEND = new GeneralEnvelope(GOOGLE_MERCATOR);
        ((GeneralEnvelope)MERCATOR_EXTEND).setRange(0, -20037508.342789244d, 20037508.342789244d);
        ((GeneralEnvelope)MERCATOR_EXTEND).setRange(1, -20037508.342789244d, 20037508.342789244d);

        ZOOM_ZERO_RESOLUTION = MERCATOR_EXTEND.getSpan(0) / BASE_TILE_SIZE;
        try {
            OVERLOAD = ImageIO.read(GoogleMapsPyramidSet.class.getResourceAsStream("/org/geotoolkit/googlemaps/staticmap.png"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private final GoogleCoverageResource ref;
    private final String mapType;

    public GoogleMapsPyramidSet(final GoogleCoverageResource ref, final boolean cacheImage) throws DataStoreException{
        super((StaticGoogleMapsClient)ref.getStore(),true,cacheImage);
        this.ref = ref;
        this.mapType = ref.getIdentifier().tip().toString();

        final int maxScale;
        if (GetMapRequest.TYPE_HYBRID.equalsIgnoreCase(mapType)) {
            maxScale = 18;
        } else if (GetMapRequest.TYPE_ROADMAP.equalsIgnoreCase(mapType)) {
            maxScale = 18;
        } else if (GetMapRequest.TYPE_SATELLITE.equalsIgnoreCase(mapType)) {
            maxScale = 18;
        } else if (GetMapRequest.TYPE_TERRAIN.equalsIgnoreCase(mapType)) {
            maxScale = 18;
        } else {
            throw new DataStoreException("Unknowned google maps layer : " + mapType);
        }


        final DefaultPyramid pyramid = new DefaultPyramid(GOOGLE_MERCATOR);

        final int tileWidth = BASE_TILE_SIZE;
        final int tileHeight = BASE_TILE_SIZE;
        final Envelope extent = MERCATOR_EXTEND;
        final GeneralDirectPosition upperLeft = new GeneralDirectPosition(GOOGLE_MERCATOR);
        upperLeft.setOrdinate(0, extent.getMinimum(0));
        upperLeft.setOrdinate(1, extent.getMaximum(1));
        final double scale0Resolution = extent.getSpan(0) / BASE_TILE_SIZE;

        for(int i=0; i<=maxScale; i++){

            final int size = (int) Math.pow(2, i);
            final double scale = scale0Resolution / size;

            final GoogleMapsMosaic mosaic = new GoogleMapsMosaic(
                    this, pyramid, upperLeft,
                    new Dimension(size,size),
                    new Dimension(tileHeight, tileWidth),
                    scale,
                    i);

            pyramid.getMosaicsInternal().add(mosaic);
        }

        getPyramids().add(pyramid);
    }

    @Override
    protected StaticGoogleMapsClient getServer() {
        return (StaticGoogleMapsClient)super.getServer();
    }

    @Override
    public Request getTileRequest(Pyramid pyramid, Mosaic mosaic, int col, int row, Map hints) throws DataStoreException {
        final int zoom = ((GoogleMapsMosaic)mosaic).getScaleLevel();

        final GetMapRequest request = ref.createGetMap();

        Object format = hints.get(Pyramids.HINT_FORMAT);
        if(format == null){
            //set a default value
            format = "image/png";
        }

        request.setFormat(format.toString());
        request.setMapType(mapType);
        request.setDimension(new Dimension(BASE_TILE_SIZE, BASE_TILE_SIZE));
        request.setZoom(zoom);

        final DirectPosition position = getCenter(zoom, col, row);
        request.setCenter(position);

        return request;
    }

    /**
     * Returns resolution at zoom given level. in meter by pixel.
     * @param zoom
     * @return
     */
    public static double getZoomResolution(final int zoom){
        return ZOOM_ZERO_RESOLUTION * Math.pow(0.5d, zoom);
    }

    public static DirectPosition getCenter(final int zoom, final int col, final int row){
        final GeneralDirectPosition position = new GeneralDirectPosition(GOOGLE_MERCATOR);

        //we look for the center, like searching for a corner if we had two times more cells
        final double zoomRes = getZoomResolution(zoom + 1);
        position.setOrdinate(0, MERCATOR_EXTEND.getMinimum(0) + zoomRes* BASE_TILE_SIZE * ( (col)*2 + 1) );
        position.setOrdinate(1, MERCATOR_EXTEND.getMaximum(1) - zoomRes* BASE_TILE_SIZE * ( (row)*2 + 1) );

        return position;
    }

}
