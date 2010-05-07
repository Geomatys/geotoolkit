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

package org.geotoolkit.data.gpx.xml;

/**
 * GPX xml tags and attributs.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GPXConstants {

    /**
     * Main GPX xml tag.
     */
    public static final String TAG_GPX = "gpx";
    public static final String ATT_GPX_VERSION = "version";
    public static final String ATT_GPX_CREATOR = "creator";

    /**
     * Attributs used a bit everywhere.
     */
    public static final String TAG_NAME = "name";
    public static final String TAG_LINK = "link";
    public static final String TAG_DESC = "desc";
    public static final String TAG_CMT = "cmt";
    public static final String TAG_SRC = "src";
    public static final String TAG_TYPE = "type";
    public static final String TAG_NUMBER = "number";

    /**
     * Metadata tag.
     */
    public static final String TAG_METADATA = "metadata";
    public static final String TAG_METADATA_TIME = "time";
    public static final String TAG_METADATA_KEYWORDS = "keywords";

    /**
     * Person tag.
     */
    public static final String TAG_AUTHOR = "author";
    public static final String TAG_AUTHOR_EMAIL = "email";

    /**
     * CopyRight tag.
     */
    public static final String TAG_COPYRIGHT = "copyright";
    public static final String TAG_COPYRIGHT_YEAR = "year";
    public static final String TAG_COPYRIGHT_LICENSE = "license";
    public static final String ATT_COPYRIGHT_AUTHOR = "author";

    /**
     * Bounds tag.
     */
    public static final String TAG_BOUNDS = "bounds";
    public static final String ATT_BOUNDS_MINLAT = "minlat";
    public static final String ATT_BOUNDS_MINLON = "minlon";
    public static final String ATT_BOUNDS_MAXLAT = "maxlat";
    public static final String ATT_BOUNDS_MAXLON = "maxlon";

    /**
     * Link tag.
     */
    public static final String TAG_LINK_TEXT = "text";
    public static final String TAG_LINK_TYPE = "type";
    public static final String ATT_LINK_HREF = "href";

    /**
     * WPT tag.
     */
    public static final String TAG_WPT = "wpt";
    public static final String ATT_WPT_LAT = "lat";
    public static final String ATT_WPT_LON = "lon";
    public static final String TAG_WPT_ELE = "ele";
    public static final String TAG_WPT_TIME = "time";
    public static final String TAG_WPT_MAGVAR = "magvar";
    public static final String TAG_WPT_GEOIHEIGHT = "geoidheight";
    public static final String TAG_WPT_SYM = "sym";
    public static final String TAG_WPT_FIX = "fix";
    public static final String TAG_WPT_SAT = "sat";
    public static final String TAG_WPT_HDOP = "hdop";
    public static final String TAG_WPT_VDOP = "vdop";
    public static final String TAG_WPT_PDOP = "pdop";
    public static final String TAG_WPT_AGEOFGPSDATA = "ageofdgpsdata";
    public static final String TAG_WPT_DGPSID = "dgpsid";

    /**
     * RTE tag.
     */
    public static final String TAG_RTE = "rte";
    public static final String TAG_RTE_RTEPT = "rtept";

    /**
     * TRK tag.
     */
    public static final String TAG_TRK = "trk";
    public static final String TAG_TRK_SEG = "trkseg";
    public static final String TAG_TRK_SEG_PT = "trkpt";

    private GPXConstants(){};

}
