/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.io;


/**
 * MimeType list based on OGC 12-029 paper. 
 * (http://www.opengis.net/doc/wps1.0-best-practice-dp)
 *
 * @author Quentin Boileau (Geomatys)
 */
public enum WPSMimeType {

    IMG_JPEG("image/jpeg"),
    IMG_JPEG2000("image/jpeg2000"),
    IMG_PNG("image/png"),
    IMG_TIFF("image/tiff"),
    IMG_GEOTIFF("image/tiff;subtype=geotiff"),
    IMG_BMP("image/bmp"),
    IMG_GIF("image/gif"),

    APP_OCTET("application/octet-stream"),
    APP_ZIP("application/zip"),
    APP_JSON("application/json"),
    APP_GEOJSON("application/geojson"),
    APP_GML("application/gml+xml"),
    APP_SHP("application/x-zipped-shp"),
    APP_NETCDF("application/netcdf"),
    APP_GRIB("application/x-ogc-grib"),
    
    OGC_WFS("application/x-ogc-wfs"),
    OGC_WMS("application/x-ogc-wms"),

    //not recommended in paper
    TEXT_XML("text/xml"),
    IMG_GEOTIFF_BIS("image/x-geotiff"),
    TEXT_GML("text/gml"),
    TEXT_PLAIN("text/plain"),
    APP_ZIP_BIS("application/x-zip"),
    APP_GZIP("application/x-gzip");

    private String mime;

    private WPSMimeType(final String mime) {
        this.mime = mime;
    }

    public String val() {
        return mime;
    }

    /**
     * Search a WPSMimeType from a String code.
     * @param str
     * @return the searched WPSMimeType or {@code null} if not found.
     */
    public static WPSMimeType customValueOf(final String str) {

        for (final WPSMimeType mime : values()) {
            if (mime.val() != null) {
                if (mime.val().equalsIgnoreCase(str)) {
                    return mime;
                }
            }
        }
        return null;
    }
}
