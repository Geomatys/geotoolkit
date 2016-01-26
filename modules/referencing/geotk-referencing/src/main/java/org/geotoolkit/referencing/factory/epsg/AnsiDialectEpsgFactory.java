/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.referencing.factory.epsg;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;


/**
 * @deprecated Moved to {@link org.apache.sis.referencing.factory.sql.SQLTranslator} in Apache SIS.
 */
@Deprecated
final class AnsiDialectEpsgFactory {
    /**
     * Table names using as "sentinel value" for detecting the presence of an EPSG database.
     * This array lists different possible names for the same table. The first entry must be
     * the MS-Access name. Other names may be in any order. They will be tried in reverse order.
     */
    private static final String[] SENTINAL = {
        "Coordinate Reference System",
        "coordinatereferencesystem",
        "epsg_coordinatereferencesystem"
    };

    /**
     * The prefix in table names.
     */
    static final String TABLE_PREFIX = "epsg_";

    /**
     * Table names. The left side (elements at even index) contains the names in the MS-Access
     * database, while the right side (elements at odd index) contains the names in the SQL scripts.
     */
    static final String[] ACCESS_TO_ANSI = {
        "[Alias]",                                  "epsg_alias",
        "[Area]",                                   "epsg_area",
        "[Coordinate Axis]",                        "epsg_coordinateaxis",
        "[Coordinate Axis Name]",                   "epsg_coordinateaxisname",
        "[Coordinate_Operation]",                   "epsg_coordoperation",
        "[Coordinate_Operation Method]",            "epsg_coordoperationmethod",
        "[Coordinate_Operation Parameter]",         "epsg_coordoperationparam",
        "[Coordinate_Operation Parameter Usage]",   "epsg_coordoperationparamusage",
        "[Coordinate_Operation Parameter Value]",   "epsg_coordoperationparamvalue",
        "[Coordinate_Operation Path]",              "epsg_coordoperationpath",
        "[Coordinate Reference System]",            "epsg_coordinatereferencesystem",
        "[Coordinate System]",                      "epsg_coordinatesystem",
        "[Datum]",                                  "epsg_datum",
        "[Ellipsoid]",                              "epsg_ellipsoid",
        "[Naming System]",                          "epsg_namingsystem",
        "[Prime Meridian]",                         "epsg_primemeridian",
        "[Supersession]",                           "epsg_supersession",
        "[Unit of Measure]",                        "epsg_unitofmeasure",
        "[Version History]",                        "epsg_versionhistory",
        "[Change]",                                 "epsg_change",
        "[Deprecation]",                            "epsg_deprecation",
        "[ORDER]",                                  "coord_axis_order" // a field in epsg_coordinateaxis
    };

    private AnsiDialectEpsgFactory() {
    }

    /**
     * Returns {@code true} if the EPSG database seems to exists. This method
     * looks for the sentinel table documented in the {@link #autoconfig} method.
     */
    static boolean exists(final DatabaseMetaData metadata, final String schema) throws SQLException {
        final ResultSet result = metadata.getTables(null, schema, null, new String[] {"TABLE"});
        while (result.next()) {
            final String table = result.getString("TABLE_NAME");
            for (final String candidate : SENTINAL) {
                if (candidate.equalsIgnoreCase(table)) {
                    return true;
                }
            }
        }
        return false;
    }
}
