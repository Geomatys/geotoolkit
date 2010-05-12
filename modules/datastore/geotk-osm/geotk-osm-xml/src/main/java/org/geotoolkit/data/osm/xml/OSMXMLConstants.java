/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.osm.xml;

/**
 * Open Street Map xml tags and attributs.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMXMLConstants {

    /**
     * Main OSM xml tag.
     */
    public static final String TAG_OSM = "osm";

    /**
     * Main OSM xml tag.
     */
    public static final String TAG_OSM_CHANGE = "osmChange";

    /**
     * Identified objects attributs.
     */
    public static final String ATT_ID = "id";
    public static final String ATT_VERSION = "version";
    public static final String ATT_CHANGESET = "changeset";
    public static final String ATT_USER = "user";
    public static final String ATT_UID = "uid";
    public static final String ATT_TIMESTAMP = "timestamp";
    /*
     * Generator, used only in Create, Modify and Delete tags.
     */
    public static final String ATT_GENERATOR = "generator";

    /**
     * Tag tag.
     */
    public static final String TAG_TAG = "tag";
    public static final String ATT_TAG_KEY = "k";
    public static final String ATT_TAG_VALUE = "v";

    /**
     * Node tag.
     */
    public static final String TAG_NODE = "node";
    public static final String ATT_NODE_LAT = "lat";
    public static final String ATT_NODE_LON = "lon";

    /**
     * Way tag.
     */
    public static final String TAG_WAY = "way";
    public static final String TAG_WAYND = "nd";
    public static final String ATT_WAYND_REF = "ref";

    /**
     * Relation tag.
     */
    public static final String TAG_REL = "relation";
    public static final String TAG_RELMB = "member";
    public static final String ATT_RELMB_TYPE = "type";
    public static final String ATT_RELMB_REF = "ref";
    public static final String ATT_RELMB_ROLE = "role";

    /**
     * Bounds tag.
     */
    public static final String TAG_BOUNDS = "bounds";
    public static final String ATT_BOUNDS_MINLAT = "minlat";
    public static final String ATT_BOUNDS_MINLON = "minlon";
    public static final String ATT_BOUNDS_MAXLAT = "maxlat";
    public static final String ATT_BOUNDS_MAXLON = "maxlon";

    /**
     * Api tag, used in osm server capabilities.
     */
    public static final String TAG_API = "api";
    public static final String TAG_API_VERSION = "version";
    public static final String TAG_API_AREA = "area";
    public static final String TAG_API_TRACEPOINTS = "tracepoints";
    public static final String TAG_API_WAYNODES = "waynodes";
    public static final String TAG_API_CHANGESETS = "changesets";
    public static final String TAG_API_TIMEOUT = "timeout";
    public static final String ATT_API_MINIMUM = "minimum";
    public static final String ATT_API_MAXIMUM = "maximum";
    public static final String ATT_API_MAXIMUM_ELEMENTS = "maximum_elements";
    public static final String ATT_API_PER_PAGE = "per_page";
    public static final String ATT_API_SECONDS = "seconds";

    /**
     * ChangeSet tag.
     */
    public static final String TAG_CHANGESET = "changeset";
    public static final String ATT_CHANGESET_CREATEDAT = "created_at";
    public static final String ATT_CHANGESET_OPEN = "open";
    public static final String ATT_CHANGESET_MINLAT = "min_lat";
    public static final String ATT_CHANGESET_MINLON = "min_lon";
    public static final String ATT_CHANGESET_MAXLAT = "max_lat";
    public static final String ATT_CHANGESET_MAXLON = "max_lon";

    /**
     * Delete tag.
     */
    public static final String TAG_DELETE = "delete";

    /**
     * Modify tag.
     */
    public static final String TAG_MODIFY = "modify";

    /**
     * Create tag.
     */
    public static final String TAG_CREATE = "create";

    private OSMXMLConstants(){};

}
