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
    public static final String CORE_OAS = "http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/oas30";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC API - Common - Part 2: Collections
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Key used in ConfClasses to identify a resource compliant with this Api.
     */
    public static final String COLLECTIONS = "http://www.opengis.net/spec/ogcapi-common-2/1.0/conf/collections";

    //proposal

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API - Features - Part 1 : Core
    // https://docs.ogc.org/is/17-069r4/17-069r4.html
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @see https://docs.ogc.org/is/17-069r4/17-069r4.html#ats_core
     */
    public static final String FEATURE_CORE = "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/core";
    /**
     * @see https://docs.ogc.org/is/17-069r4/17-069r4.html#ats_html
     */
    public static final String FEATURE_HTML = "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/html";
    /**
     * @see https://docs.ogc.org/is/17-069r4/17-069r4.html#ats_geojson
     */
    public static final String FEATURE_GEOJSON = "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/geojson";
    /**
     * @see https://docs.ogc.org/is/17-069r4/17-069r4.html#ats_gmlsf0
     */
    public static final String FEATURE_GML_LEVEL0 = "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/gmlsf0";
    /**
     * @see https://docs.ogc.org/is/17-069r4/17-069r4.html#ats_gmlsf2
     */
    public static final String FEATURE_GML_LEVEL2 = "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/gmlsf2";
    /**
     * @see https://docs.ogc.org/is/17-069r4/17-069r4.html#ats_oas30
     */
    public static final String FEATURE_OPENAPI3 = "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/oas30";


    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API - Features - Part 2 : Coordinate Reference Systems by Reference
    // https://docs.ogc.org/is/18-058r1/18-058r1.html
    // /////////////////////////////////////////////////////////////////////////////////////////

    public static final String FEATURE_CRS = "http://www.opengis.net/spec/ogcapi-features-2/1.0/conf/crs";


    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API - Features - Part 3 : Filtering
    // https://docs.ogc.org/is/19-079r2/19-079r2.html
    // /////////////////////////////////////////////////////////////////////////////////////////

    public static final String FEATURE_FILTERING_QUERYABLES = "http://www.opengis.net/spec/ogcapi-features-3/1.0/conf/queryables";
    public static final String FEATURE_FILTERING_QUERYABLES_AS_PARAMETERS = "http://www.opengis.net/spec/ogcapi-features-3/1.0/conf/queryables-query-parameters";
    public static final String FEATURE_FILTERING_FILTER = "http://www.opengis.net/spec/ogcapi-features-3/1.0/conf/filter";
    public static final String FEATURE_FILTERING_FEATURES_FILTER = "http://www.opengis.net/spec/ogcapi-features-3/1.0/conf/features-filter";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API - Features - Part 5 : Schema
    // https://docs.ogc.org/DRAFTS/23-058r1.html
    // /////////////////////////////////////////////////////////////////////////////////////////

    public static final String FEATURE_SCHEMAS = "http://www.opengis.net/spec/ogcapi-features-5/1.0/conf/schemas";

    public static final String FEATURE_CORE_ROLES_FOR_FEATURES = "http://www.opengis.net/spec/ogcapi-features-5/1.0/conf/advanced-property-roles";

    public static final String FEATURE_REFERENCES = "http://www.opengis.net/spec/ogcapi-features-5/1.0/conf/references";

    public static final String FEATURE_RETURNABLES_AND_RECEIVABLES = "http://www.opengis.net/spec/ogcapi-features-5/1.0/conf/returnables-and-receivables";

    public static final String FEATURE_QUERYABLES = "http://www.opengis.net/spec/ogcapi-features-5/1.0/conf/queryables";

    public static final String FEATURE_SORTABLES = "http://www.opengis.net/spec/ogcapi-features-5/1.0/conf/sortables";

    public static final String FEATURE_PROFILE_QUERY_PARAMETER = "http://www.opengis.net/spec/ogcapi-features-5/1.0/conf/profile-parameter";

    public static final String FEATURE_PROFILES_FOR_REFERENCES = "http://www.opengis.net/spec/ogcapi-features-5/1.0/conf/profile-references";

    public static final String FEATURE_PROFILES_FOR_CODELISTS = "http://www.opengis.net/spec/ogcapi-features-5/1.0/conf/profile-codelists";

    public static final String FEATURE_PROFILES_FOR_VALUE_DOMAINS = "http://www.opengis.net/spec/ogcapi-features-5/1.0/conf/profile-domains";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API - Coverages
    // https://docs.ogc.org/DRAFTS/19-087.html
    // /////////////////////////////////////////////////////////////////////////////////////////

    public static final String COVERAGE_CORE = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/core";

    public static final String COVERAGE_SPATIAL_SCALING = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/scaling-spatial";

    public static final String COVERAGE_TEMPORAL_SCALING = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/scaling-temporal";

    public static final String COVERAGE_GENERAL_SCALING = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/scaling-general";

    public static final String COVERAGE_SPATIAL_SUBSETTING = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/subsetting-spatial";

    public static final String COVERAGE_TEMPORAL_SUBSETTING = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/subsetting-temporal";

    public static final String COVERAGE_GENERAL_SUBSETTING = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/subsetting-general";

    public static final String COVERAGE_FIELD_SELECTION = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/fieldselection";

    public static final String COVERAGE_CRS = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/crs";

    public static final String COVERAGE_COVERAGE_TILES = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/tiles";

    public static final String COVERAGE_HTML = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/html";

    public static final String COVERAGE_GEOTIFF = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/geotiff";

    public static final String COVERAGE_NETCDF = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/netcdf";

    public static final String COVERAGE_CIS_JSON = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/cisjson";

    public static final String COVERAGE_COVERAGEJSON = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/coveragejson";

    public static final String COVERAGE_LAS = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/las";

    public static final String COVERAGE_LASZIP = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/laszip";

    public static final String COVERAGE_PNG = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/png";

    public static final String COVERAGE_JPEG_XL = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/jpegxl";

    public static final String COVERAGE_JPEG_2000 = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/jpeg2000";

    public static final String COVERAGE_ZARR = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/zarr";

    public static final String COVERAGE_OPENAPI_3 = "http://www.opengis.net/spec/ogcapi-coverages-1/1.0/conf/oas30";


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
