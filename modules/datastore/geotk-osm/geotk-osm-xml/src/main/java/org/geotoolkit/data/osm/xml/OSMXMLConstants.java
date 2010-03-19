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
    public static final String ATT_OSM_VERSION = "version";
    public static final String ATT_OSM_GENERATOR = "generator";

    /**
     * Identified objects attributs.
     */
    public static final String ATT_ID = "id";
    public static final String ATT_VERSION = "version";
    public static final String ATT_CHANGESET = "changeset";
    public static final String ATT_USER = "user";
    public static final String ATT_UID = "uid";
    public static final String ATT_TIMESTAMP = "timestamp";

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
     * ChangeSet tag.
     */
    public static final String TAG_CHANGESET = "changeset";

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
