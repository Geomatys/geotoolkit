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
     * The target IRI points to a resource representing the dataset (e.g., the root of an OGC Web API).
     */
    public static final String OGC_DATASET = "https://www.opengis.net/def/rel/ogc/1.0/dataset";
    /**
     * The target IRI points to a resource representing a collection of geospatial data.
     */
    public static final String OGC_GEODATA = "https://www.opengis.net/def/rel/ogc/1.0/geodata";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API CORE
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Refers to a resource that identifies the specifications that the link’s context conforms to.
     */
    public static final String OGC_CORE_CONFORMANCE = "https://www.opengis.net/def/rel/ogc/1.0/conformance";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API Geospatial data
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Refers to the list of collections available for a dataset.
     */
    public static final String OGC_DATA = "https://www.opengis.net/def/rel/ogc/1.0/data";

    // /////////////////////////////////////////////////////////////////////////////////////////
    // OGC-API - Features - Part 3 : Filtering
    // https://docs.ogc.org/is/19-079r2/19-079r2.html
    // /////////////////////////////////////////////////////////////////////////////////////////

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
