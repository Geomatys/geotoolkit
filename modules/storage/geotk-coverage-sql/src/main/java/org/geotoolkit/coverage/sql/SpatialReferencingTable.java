/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.sis.internal.util.Constants;
import org.apache.sis.io.wkt.Convention;
import org.apache.sis.io.wkt.WKTFormat;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AbstractCS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.referencing.factory.IdentifiedObjectFinder;
import org.apache.sis.util.ComparisonMode;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.util.FactoryException;

/**
 * Connection to postgis spatial_ref_sys table.
 * This table search and builds {@link CoordinateReferenceSystem} objects.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class SpatialReferencingTable extends Table {

    private static final int USER_RANGE_START = 910000;

    /**
     * Changes to try on axis order when checking if the EPSG code for a coordinate reference system
     * can be used for the {@code "srid"} column of the {@code "GridGeometris"} table. Those changes
     * are cumulative: {@code CHANGES_TO_TRY[2]} is applied on the result of {@code CHANGES_TO_TRY[1]}.
     */
    private static final AxesConvention[] CHANGES_TO_TRY = {
        null, AxesConvention.RIGHT_HANDED, AxesConvention.POSITIVE_RANGE
    };

    SpatialReferencingTable(Transaction transaction) {
        super(transaction);
    }

    /**
     * Finds a {@code spatial_ref_sys} code for the given coordinate reference system.
     */
    int findOrInsertCRS(final CoordinateReferenceSystem crs) throws FactoryException, IllegalUpdateException, SQLException {
        final IdentifiedObjectFinder finder = IdentifiedObjects.newFinder(Constants.EPSG);
        finder.setIgnoringAxes(true);
        final CoordinateReferenceSystem found = (CoordinateReferenceSystem) finder.findSingleton(crs);
        if (found != null) {
            AbstractCS expected = AbstractCS.castOrCopy(crs.getCoordinateSystem());
            AbstractCS actual = AbstractCS.castOrCopy(found.getCoordinateSystem());
            for (final AxesConvention convention : CHANGES_TO_TRY) {
                if (convention != null) {
                    expected = expected.forConvention(convention);
                    actual   = actual  .forConvention(convention);
                }
                if (actual.equals(expected, ComparisonMode.APPROXIMATIVE)) {
                    final Identifier id = IdentifiedObjects.getIdentifier(found, Citations.EPSG);
                    if (id != null) try {
                        return Integer.valueOf(id.getCode());
                    } catch (NumberFormatException e) {
                        throw new IllegalUpdateException("Illegal SRID: " + id);
                    }
                }
            }
        }
        /*
         * Temporary hack for Coriolis data (to be removed in a future version).
         */
        if (Entry.HACK) {
            if (IdentifiedObjects.isHeuristicMatchForName(crs, "Mercator_1SP (Unspecified datum based upon the GRS 1980 Authalic Sphere)")) {
                return 3395;
            } else if (CRS.findOperation(crs, CommonCRS.defaultGeographic(), null).getMathTransform().isIdentity()) {
                return 4326;
            }
        }

        // search user custom crs list
        try (Statement stmt = getConnection().createStatement()) {
            final ResultSet rs = stmt.executeQuery("SELECT srid, srtext FROM public.spatial_ref_sys WHERE srid > 909999");
            if (rs.next()) {
                int srid = rs.getInt(1);
                String srtext = rs.getString(2);
                CoordinateReferenceSystem cdt = CRS.fromWKT(srtext);
                CoordinateSystem cs = cdt.getCoordinateSystem();
                if (AbstractCS.castOrCopy(crs.getCoordinateSystem()).equals(cs, ComparisonMode.APPROXIMATIVE)) {
                    return srid;
                }
            }
        }

        //store new crs
        return insertCrs(crs);
    }

    private int insertCrs(final CoordinateReferenceSystem crs) throws FactoryException, SQLException {

        if (!(crs instanceof SingleCRS)) {
            throw new FactoryException("CoordinateReferenceSystem must be a SingleCRS");
        }

        //format to WKT-1
        final WKTFormat format = new WKTFormat(null, null);
        format.setIndentation(WKTFormat.SINGLE_LINE);
        format.setConvention(Convention.WKT1_COMMON_UNITS);
        String srtext = format.format(crs);
        if (format.getWarnings() != null) {
            //try WKT-2
            format.setConvention(Convention.WKT2);
            srtext = format.format(crs);
            if (format.getWarnings() != null) {
                throw new FactoryException("No valid WKT-1 or WKT-2 for given CRS.");
            }
        }

        int srid = nextSrid();
        String auth_name = "coveragesql";
        int auth_srid = srid;
        String proj4text = "";

        PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO public.spatial_ref_sys(srid, auth_name, auth_srid, srtext, proj4text) VALUES (?, ?, ?, ?, ?)");
        stmt.setInt(1, srid);
        stmt.setString(2, auth_name);
        stmt.setInt(3, auth_srid);
        stmt.setString(4, srtext);
        stmt.setString(5, proj4text);

        stmt.execute();
        return srid;
    }

    /**
     * The range of SRIDs reserved for user custom SRIDs is from 910000 to 998999.
     * ref : https://gis.stackexchange.com/questions/145017/why-is-there-an-upper-limit-to-the-srid-value-in-the-spatial-ref-sys-table-in-po
     *
     * @return
     * @throws SQLException
     */
    private int nextSrid() throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            final ResultSet rs = stmt.executeQuery("SELECT max(srid) FROM public.spatial_ref_sys WHERE srid >= " + USER_RANGE_START);
            if (rs.next()) {
                int max = rs.getInt(1);
                if (max >= USER_RANGE_START) { //can be 0 if no records exist
                    return max + 1;
                }
            }
        }
        return USER_RANGE_START;
    }

    CoordinateReferenceSystem getCRS(int srid) throws FactoryException, SQLException {
        if (srid >= USER_RANGE_START) {
            try (Statement stmt = getConnection().createStatement()) {
                final ResultSet rs = stmt.executeQuery("SELECT srtext FROM public.spatial_ref_sys WHERE srid = " + srid);
                if (rs.next()) {
                    return CRS.fromWKT(rs.getString(1));
                } else {
                    throw new FactoryException("Unknown srid " + srid);
                }
            }
        }

        /*
        * TODO: the CRS codes are PostGIS codes, not EPSG codes. For now we simulate PostGIS codes
        * by changing axis order after decoding, but we should really use a PostGIS factory instead.
        */
       CoordinateReferenceSystem crs;
       crs = transaction.database.getCRSAuthorityFactory().createCoordinateReferenceSystem(String.valueOf(srid));
       return AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.RIGHT_HANDED);
    }

}
