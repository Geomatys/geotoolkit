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
 * List all known OGC API relation links types.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class LinkRelations {

    private LinkRelations() {}

    // /////////////////////////////////////////////////////////////////////////////////////////
    // IANA
    // https://www.iana.org/assignments/link-relations/link-relations.xhtml
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Refers to a substitute for this context.
     * Refers to a representation of the current resource which is encoded using another media type (the media type is specified in the type link attribute).
     */
    public static final String ALTERNATE = "alternate";
    /**
     * Conveys an identifier for the link’s context.
     */
    public static final String SELF = "self";
    /**
     * Identifies service description for the context that is primarily intended for consumption by machines (Web API definitions are considered service descriptions).
     */
    public static final String SERVICE_DESC = "service-desc";
    /**
     * Identifies service documentation for the context that is primarily intended for human consumption.
     */
    public static final String SERVICE_DOC = "service-doc";
    /**
     * Identifies general metadata for the context that is primarily intended for consumption by machines.
     */
    public static final String SERVICE_META = "service-meta";
    /**
     * The target IRI points to a resource which represents the collection resource for the context IRI.
     */
    public static final String COLLECTION = "collection";
    /**
     * Refers to a resource providing information about the link’s context.
     * Links to external resources which further describe the subject resource.
     */
    public static final String DESCRIBEDBY = "describedby";
    /**
     * The target IRI points to a resource that is a member of the collection represented by the context IRI.
     */
    public static final String ITEM = "item";
    /**
     * Refers to a license associated with this context.
     */
    public static final String LICENSE = "license";
    /**
     * Indicates that the link target is preferred over the link context for the purpose of permanent citation.
     */
    public static final String CITE_AS = "cite-as";
    /**
     * Refers to a resource that is the subject of the link’s context.
     */
    public static final String ABOUT = "about";
    /**
     * Refers to the context’s author.
     */
    public static final String AUTHOR = "author";
    /**
     * Refers to a list of patent disclosures made with respect to material for which ‘disclosure’ relation is specified.
     */
    public static final String DISCLOSURE = "disclosure";
    /**
     * Refers to a copyright statement that applies to the link’s context.
     */
    public static final String COPYRIGHT = "copyright";
    /**
     * Refers to a resource that is within a context that is sponsored (such as advertising or another compensation agreement).
     */
    public static final String SPONSORED = "sponsored";
    /**
     * Refers to the terms of service associated with the link’s context.
     */
    public static final String TERMS_OF_SERVICE = "terms-of-service";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API CORE
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Refers to a resource that identifies the specifications that the link’s context conforms to.
     */
    public static final String OGC_CORE_CONFORMANCE = "https://www.opengis.net/def/rel/ogc/1.0/conformance";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API OGC API - Features Part 1:
    // Core or OGC API - Common Part 2: Geospatial data
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Refers to the list of collections available for a dataset.
     */
    public static final String OGC_DATA = "https://www.opengis.net/def/rel/ogc/1.0/data";
    /**
     * The target IRI points to a resource representing general metadata for the collection of geospatial data
     * (e.g., ISO-19115 — not the domain/application metadata as defined in CIS).
     */
    public static final String OGC_DATA_META = "https://www.opengis.net/def/rel/ogc/1.0/data-meta";
    /**
     * The target IRI points to a resource that describes the TileMatrixSet according to the 2D-TMS standard.
     */
    public static final String OGC_TILING_SCHEME = "http://www.opengis.net/def/rel/ogc/1.0/tiling-scheme";
    /**
     * The target IRI points to a resource representing the dataset (e.g., the root of an OGC Web API).
     */
    public static final String OGC_DATASET = "https://www.opengis.net/def/rel/ogc/1.0/dataset";
    /**
     * The target IRI points to a resource representing a collection of geospatial data.
     */
    public static final String OGC_GEODATA = "https://www.opengis.net/def/rel/ogc/1.0/geodata";
    /**
     * Refers to a resource that is comprised of members of the collection represented by the link’s context.
     */
    public static final String OGC_ITEMS = "https://www.opengis.net/def/rel/ogc/1.0/items";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API - Features - Part 3 : Filtering
    // https://docs.ogc.org/is/19-079r2/19-079r2.html
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @see https://docs.ogc.org/is/19-079r2/19-079r2.html#queryables
     */
    public static final String OGC_FEATURE_QUERYABLES = "http://www.opengis.net/def/rel/ogc/1.0/queryables";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API - Features - Part 5 : Schema
    // https://docs.ogc.org/DRAFTS/23-058r1.html
    // /////////////////////////////////////////////////////////////////////////////////////////

    public static final String OGC_FEATURE_SCHEMA = "http://www.opengis.net/def/rel/ogc/1.0/schema";

    // http://www.opengis.net/def/rel/ogc/1.0/queryables already in part 3

    public static final String OGC_FEATURE_SORTABLES = "http://www.opengis.net/def/rel/ogc/1.0/sortables";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API - Coverage
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The target IRI points to a resource representing the coverage, including self-description aspects supported by
     * the encoding (such as the domain set, range type, range set and metadata for the Coverage Implementation Schema).
     */
    public static final String OGC_COVERAGE = "http://www.opengis.net/def/rel/ogc/1.0/coverage";
    /**
     * The target IRI points to a list of scenes of which the coverage collection is comprised, as defined by the
     * "Scenes" requirements class.
     */
    public static final String OGC_COVERAGE_SCENE = "http://www.opengis.net/def/rel/ogc/1.0/coverage-scenes";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API - Tiles
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * http://www.opengis.net/def/rel/ogc/1.0/tilesets-coverage
     */
    public static final String OGC_TILES_COVERAGE = "http://www.opengis.net/def/rel/ogc/1.0/tilesets-coverage";
    /**
     * http://www.opengis.net/def/rel/ogc/1.0/tiling-schemes
     */
    public static final String OGC_TILES_SCHEME = "http://www.opengis.net/def/rel/ogc/1.0/tiling-schemes";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API DGGRS
    // https://docs.ogc.org/DRAFTS/21-038r1.html
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The target IRI points to the description, which includes a link to the definition as well as deployment-specific
     * elements, of the Discrete Global Grid Reference System related to the link’s context.
     */
    public static final String OGC_DGGRS = "https://www.opengis.net/def/rel/ogc/1.0/dggrs";
    /**
     * The target IRI points to the description, which includes a link to the definition as well as deployment-specific
     * elements, of the Discrete Global Grid Reference System related to the link’s context.
     */
    public static final String OGC_DGGRS_LIST = "https://www.opengis.net/def/rel/ogc/1.0/dggrs-list";
    /**
     * The target IRI points to the definition of the Discrete Global Grid Reference System using a well-defined
     * schema (see Annex B), for example as available from an authoritative DGGRS registry.
     */
    public static final String OGC_DGGRS_DEFINITION = "https://www.opengis.net/def/rel/ogc/1.0/dggrs-definition";
    /**
     * The target IRI points to information about a particular zone of a Discrete Global Grid Reference System,
     * such as its geometry, area/volume, and any relevant links (e.g., to retrieve data from the zone).
     */
    public static final String OGC_DGGRS_ZONE_INFO = "https://www.opengis.net/def/rel/ogc/1.0/dggrs-zone-info";
    /**
     * The target IRI points to data pertaining to a particular zone of a Discrete Global Grid Reference System.
     * This link relation is typically used with a link template where a zone identifier can be substituted.
     */
    public static final String OGC_DGGRS_ZONE_DATA = "https://www.opengis.net/def/rel/ogc/1.0/dggrs-zone-data";
    /**
     * The target IRI points to a zone listing resource allowing to perform queries for a particular
     * Discrete Global Grid Reference System.
     */
    public static final String OGC_DGGRS_ZONE_QUERY = "https://www.opengis.net/def/rel/ogc/1.0/dggrs-zone-query";
    /**
     * The target IRI points to the information resource of a zone which is a parent of the link’s context zone.
     */
    public static final String OGC_DGGRS_ZONE_PARENT = "https://www.opengis.net/def/rel/ogc/1.0/dggrs-zone-parent";
    /**
     * The target IRI points to the information resource of a zone which is an immediate child of the link’s context zone.
     */
    public static final String OGC_DGGRS_ZONE_CHILD = "https://www.opengis.net/def/rel/ogc/1.0/dggrs-zone-child";
    /**
     * The target IRI points to the information resource of a zone which is a neighbor of the link’s context zone.
     */
    public static final String OGC_DGGRS_ZONE_NEIGHBOR = "https://www.opengis.net/def/rel/ogc/1.0/dggrs-zone-neighbor";

}
