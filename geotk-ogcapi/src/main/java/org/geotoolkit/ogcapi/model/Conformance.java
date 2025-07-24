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
package org.geotoolkit.ogcapi.model;

/**
 * List all known OGC API conformance types.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Conformance {

    private Conformance() {}

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC API - Common - Part 1: Core
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Key used in ConfClasses to identify a resource compliant with this Api.
     */
    public static final String CORE = "http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/core";
    public static final String CORE_LANDINGPAGE = "http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/landing-page";
    public static final String CORE_HTML = "http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/html";
    public static final String CORE_JSON = "http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/json";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC API - Common - Part 2: Collections
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Key used in ConfClasses to identify a resource compliant with this Api.
     */
    public static final String COLLECTIONS = "http://www.opengis.net/spec/ogcapi-common-2/1.0/conf/collections";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API - Features - Part 3 : Filtering
    // https://docs.ogc.org/is/19-079r2/19-079r2.html
    // /////////////////////////////////////////////////////////////////////////////////////////

    public static final String FEATURE_FILTERING_QUERYABLES = "http://www.opengis.net/spec/ogcapi-features-3/1.0/conf/queryables";
    public static final String FEATURE_FILTERING_QUERYABLES_AS_PARAMETERS = "http://www.opengis.net/spec/ogcapi-features-3/1.0/conf/queryables-query-parameters";
    public static final String FEATURE_FILTERING_FILTER = "http://www.opengis.net/spec/ogcapi-features-3/1.0/conf/filter";
    public static final String FEATURE_FILTERING_FEATURES_FILTER = "http://www.opengis.net/spec/ogcapi-features-3/1.0/conf/features-filter";


    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API DGGRS
    // https://docs.ogc.org/DRAFTS/21-038r1.html
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_core
     */
    public static final String DGGS_CORE = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/core";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-retrieval
     */
    public static final String DGGS_DATA_RETRIEVAL = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-retrieval";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-subsetting
     */
    public static final String DGGS_DATA_SUBSETTING = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-subsetting";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-custom-depths
     */
    public static final String DGGS_DATA_CUSTOM_DEPTHS = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-custom-depths";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-cql2-filter
     */
    public static final String DGGS_FILTERING_ZONE_DATA_WITH_CQL2 = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-cql2-filter";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_zone-query
     */
    public static final String DGGS_ZONE_QUERY = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/zone-query";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_zone-query-cql2-filter
     */
    public static final String DGGS_FILTERING_ZONE_QUERY_WITH_CQL2 = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/zone-query-cql2-filter";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_root-dggs
     */
    public static final String DGGS_ROOT_DGGS = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/root-dggs";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_collection-dggs
     */
    public static final String DGGS_COLLECTION = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/collection-dggs";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_table-data_json
     */
    public static final String DGGS_DATA_JSON = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-json";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-ubjson
     */
    public static final String DGGS_DATA_UBJSON = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-ubjson";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-dggs-jsonfg
     */
    public static final String DGGS_DATA_JSONFG = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-dggs-jsonfg";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-dggs-ubjsonfg
     */
    public static final String DGGS_DATA_UBJSONFG = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-dggs-ubjsonfg";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-geotiff
     */
    public static final String DGGS_DATA_GEOTIFF = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-geotiff";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-geojson
     */
    public static final String DGGS_DATA_GEOJSON = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-geojson";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-netcdf
     */
    public static final String DGGS_DATA_NETCDF = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-netcdf";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-zarr
     */
    public static final String DGGS_DATA_ZARR = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-zarr";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-coveragejson
     */
    public static final String DGGS_DATA_COVERAGEJSON = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-coveragejson";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-jpegxl
     */
    public static final String DGGS_DATA_JPEGXL = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-jpegxl";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_data-png
     */
    public static final String DGGS_DATA_PNG = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/data-png";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_zone-html
     */
    public static final String DGGS_ZONELIST_HTML = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/zone-html";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_zone-uint64
     */
    public static final String DGGS_ZONELIST_UINT64 = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/zone-uint64";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_zone-geojson
     */
    public static final String DGGS_ZONELIST_GEOJSON = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/zone-geojson";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#rc_zone-geotiff
     */
    public static final String DGGS_ZONELIST_GEOTIFF = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/zone-geotiff";
    /**
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#operation-ids
     */
    public static final String DGGS_ZONELIST_OPERATIONIDS = "https://www.opengis.net/spec/ogcapi-dggs-1/1.0/conf/operation-ids";

    
}
