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
    public static final String TAG_GPX = "gpx";                         // 1.0 and 1.1
    public static final String ATT_GPX_VERSION = "version";             // 1.0 and 1.1
    public static final String ATT_GPX_CREATOR = "creator";             // 1.0 and 1.1

    /**
     * Attributs used a bit everywhere.
     */
    public static final String TAG_NAME = "name";                       // 1.0 and 1.1
    public static final String TAG_URL = "url";                         // 1.0
    public static final String TAG_URLNAME = "urlname";                 // 1.0
    public static final String TAG_LINK = "link";                       // 1.1
    public static final String TAG_DESC = "desc";                       // 1.0 and 1.1
    public static final String TAG_CMT = "cmt";                         // 1.0 and 1.1
    public static final String TAG_SRC = "src";                         // 1.0 and 1.1
    public static final String TAG_TYPE = "type";                       // 1.0 and 1.1
    public static final String TAG_NUMBER = "number";                   // 1.0 and 1.1

    /**
     * Metadata tag.
     */
    public static final String TAG_METADATA = "metadata";               // 1.1
    public static final String TAG_METADATA_TIME = "time";              // 1.0 and 1.1
    public static final String TAG_METADATA_KEYWORDS = "keywords";      // 1.0 and 1.1

    /**
     * Person tag.
     */
    public static final String TAG_AUTHOR = "author";                   // 1.0(as attribut) and 1.1(as tag)
    public static final String TAG_AUTHOR_EMAIL = "email";              // 1.0 and 1.1

    /**
     * CopyRight tag.
     */
    public static final String TAG_COPYRIGHT = "copyright";             // 1.1
    public static final String TAG_COPYRIGHT_YEAR = "year";             // 1.1
    public static final String TAG_COPYRIGHT_LICENSE = "license";       // 1.1
    public static final String ATT_COPYRIGHT_AUTHOR = "author";         // 1.1

    /**
     * Bounds tag.
     */
    public static final String TAG_BOUNDS = "bounds";                   // 1.0 and 1.1
    public static final String ATT_BOUNDS_MINLAT = "minlat";            // 1.0 and 1.1
    public static final String ATT_BOUNDS_MINLON = "minlon";            // 1.0 and 1.1
    public static final String ATT_BOUNDS_MAXLAT = "maxlat";            // 1.0 and 1.1
    public static final String ATT_BOUNDS_MAXLON = "maxlon";            // 1.0 and 1.1

    /**
     * Link tag.
     */
    public static final String TAG_LINK_TEXT = "text";                  // 1.1
    public static final String TAG_LINK_TYPE = "type";                  // 1.1
    public static final String ATT_LINK_HREF = "href";                  // 1.1

    /**
     * WPT tag.
     */
    public static final String TAG_WPT = "wpt";                         // 1.0 and 1.1
    public static final String ATT_WPT_LAT = "lat";                     // 1.0 and 1.1
    public static final String ATT_WPT_LON = "lon";                     // 1.0 and 1.1
    public static final String TAG_WPT_ELE = "ele";                     // 1.0 and 1.1
    public static final String TAG_WPT_TIME = "time";                   // 1.0 and 1.1
    public static final String TAG_WPT_MAGVAR = "magvar";               // 1.0 and 1.1
    public static final String TAG_WPT_GEOIHEIGHT = "geoidheight";      // 1.0 and 1.1
    public static final String TAG_WPT_SYM = "sym";                     // 1.0 and 1.1
    public static final String TAG_WPT_FIX = "fix";                     // 1.0 and 1.1
    public static final String TAG_WPT_SAT = "sat";                     // 1.0 and 1.1
    public static final String TAG_WPT_HDOP = "hdop";                   // 1.0 and 1.1
    public static final String TAG_WPT_VDOP = "vdop";                   // 1.0 and 1.1
    public static final String TAG_WPT_PDOP = "pdop";                   // 1.0 and 1.1
    public static final String TAG_WPT_AGEOFGPSDATA = "ageofdgpsdata";  // 1.0 and 1.1
    public static final String TAG_WPT_DGPSID = "dgpsid";               // 1.0 and 1.1

    /**
     * RTE tag.
     */
    public static final String TAG_RTE = "rte";                         // 1.0 and 1.1
    public static final String TAG_RTE_RTEPT = "rtept";                 // 1.0 and 1.1

    /**
     * TRK tag.
     */
    public static final String TAG_TRK = "trk";                         // 1.0 and 1.1
    public static final String TAG_TRK_SEG = "trkseg";                  // 1.0 and 1.1
    public static final String TAG_TRK_SEG_PT = "trkpt";                // 1.0 and 1.1

    private GPXConstants(){};

}
