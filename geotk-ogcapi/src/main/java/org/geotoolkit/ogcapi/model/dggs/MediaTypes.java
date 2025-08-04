/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.model.dggs;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.ogc.org/DRAFTS/21-038r1.html#zone_data_media_types
 * @see https://docs.ogc.org/DRAFTS/21-038r1.html#zone_list_media_types
 */
public final class MediaTypes {

    private MediaTypes(){}

    // data types
    public static final String DATA_JSON                        = "application/json";
    public static final String DATA_UBJSON                      = "application/ubjson";
    public static final String DATA_JSON_FG                     = "application/geo+json";
    public static final String DATA_UBJSON_FG                   = "application/geo+ubjson";
    public static final String DATA_GEOJSON                     = "application/geo+json";
    public static final String DATA_GEOTIFF                     = "image/tiff; application=geotiff";
    public static final String DATA_NETCDF                      = "application/netcdf ";
    public static final String DATA_ZARR                        = "application/zarr+zip";
    public static final String DATA_COVERAGEJSON                = "application/prs.coverage+json ";
    public static final String DATA_PNG                         = "image/png";
    public static final String DATA_JPEGXL                      = "image/jxl";

    public static final String PROFILE_JSONFG_DGGS              = "jsonfg-dggs";
    public static final String PROFILE_JSONFG_DGGS_PLUS         = "jsonfg-dggs-plus";
    public static final String PROFILE_JSONFG_DGGS_ZONEIDS      = "jsonfg-dggs-zoneids";
    public static final String PROFILE_JSONFG_DGGS_ZONEIDS_PLUS = "jsonfg-dggs-zoneids-plus";
    public static final String PROFILE_RFC7946                  = "rfc7946";
    public static final String PROFILE_JSONFG                   = "jsonfg";
    public static final String PROFILE_JSONFG_PLUS              = "jsonfg-plus";
    public static final String PROFILE_NETCDF3_DGGS             = "netcdf3-dggs";
    public static final String PROFILE_NETCDF3_DGGS_ZONEIDS     = "netcdf3-dggs-zoneids";
    public static final String PROFILE_NETCDF4_DGGS             = "netcdf4-dggs";
    public static final String PROFILE_NETCDF4_DGGS_ZONEIDS     = "netcdf4-dggs-zoneids";
    public static final String PROFILE_ZARR_DGGS                = "zarr2-dggs";
    public static final String PROFILE_ZARR_DGGS_ZONEIDS        = "zarr2-dggs-zoneids";
    public static final String PROFILE_COVERAGEJSON_DGGS        = "covjson-dggs";
    public static final String PROFILE_COVERAGEJSON_DGGS_ZONEIDS= "covjson-dggs-zoneids";

    // zone list types
    public static final String ZONELIST_JSON                    = "application/json";
    public static final String ZONELIST_HTML                    = "text/html";
    public static final String ZONELIST_UINT64                  = "application/x-binary";
    public static final String ZONELIST_GEOJSON                 = "application/geo+json";
    public static final String ZONELIST_GEOTIFF                 = "image/tiff; application=geotiff";

}
