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
import java.text.ParseException;
import org.apache.sis.util.privy.Constants;
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
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.util.ComparisonMode;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.Matrix;
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

    /**
     * The object to use for reading and writing Well Known Text.
     * Created when first needed.
     *
     * @see #getWKTFormat()
     */
    private WKTFormat wktFormat;

    SpatialReferencingTable(Transaction transaction) {
        super(transaction);
    }

    /**
     * Finds a {@code spatial_ref_sys} code for the given coordinate reference system.
     */
    final int findOrInsert(final CoordinateReferenceSystem crs) throws FactoryException, IllegalUpdateException, SQLException {
        final IdentifiedObjectFinder finder = IdentifiedObjects.newFinder(Constants.EPSG);
        finder.setIgnoringAxes(true);
        final CoordinateReferenceSystem found = (CoordinateReferenceSystem) finder.findSingleton(crs);
        if (found != null) {
            AbstractCS expected = AbstractCS.castOrCopy(crs.getCoordinateSystem());
            AbstractCS actual   = AbstractCS.castOrCopy(found.getCoordinateSystem());
            for (final AxesConvention convention : CHANGES_TO_TRY) {
                if (convention != null) {
                    expected = expected.forConvention(convention);
                    actual   = actual  .forConvention(convention);
                }
                if (actual.equals(expected, ComparisonMode.APPROXIMATE)) {
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
        /*
         * Search user custom CRS list.
         */
        try (Statement stmt = getConnection().createStatement()) {
            final ResultSet rs = stmt.executeQuery("SELECT srid, srtext FROM spatial_ref_sys WHERE srid >= " + USER_RANGE_START);
            if (rs.next()) {
                int srid = rs.getInt(1);
                String srtext = rs.getString(2);
                CoordinateReferenceSystem cdt = CRS.fromWKT(srtext);
                CoordinateSystem cs = cdt.getCoordinateSystem();
                if (AbstractCS.castOrCopy(crs.getCoordinateSystem()).equals(cs, ComparisonMode.APPROXIMATE)) {
                    return srid;
                }
            }
        }
        /*
         * Store new CRS.
         */
        return insert(crs);
    }

    private int insert(final CoordinateReferenceSystem crs) throws FactoryException, SQLException {

        if (!(crs instanceof SingleCRS)) {
            throw new FactoryException("CoordinateReferenceSystem must be a SingleCRS");
        }

        // format to WKT-1
        final WKTFormat wktFormat = getWKTFormat();
        String srtext = wktFormat.format(crs);
        if (wktFormat.getWarnings() != null) try {
            // try WKT-2
            srtext = crs.toWKT();
        } catch (UnsupportedOperationException e) {
            throw new FactoryException("No valid WKT-1 or WKT-2 for given CRS.", e);
        }
        int srid = nextSRID();
        String auth_name = "coveragesql";
        int auth_srid = srid;
        String proj4text = "";

        // Following statement will be closed by Table.close().
        PreparedStatement stmt = prepareStatement("INSERT INTO spatial_ref_sys(srid, auth_name, auth_srid, srtext, proj4text) VALUES (?, ?, ?, ?, ?)");
        stmt.setInt   (1, srid);
        stmt.setString(2, auth_name);
        stmt.setInt   (3, auth_srid);
        stmt.setString(4, srtext);
        stmt.setString(5, proj4text);

        stmt.execute();
        return srid;
    }

    /**
     * The range of SRIDs reserved for user custom SRIDs is from 910000 to 998999.
     *
     * @see <a href="https://gis.stackexchange.com/questions/145017/why-is-there-an-upper-limit-to-the-srid-value-in-the-spatial-ref-sys-table-in-po">Stackexchange</a>
     */
    private int nextSRID() throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            final ResultSet rs = stmt.executeQuery("SELECT max(srid) FROM spatial_ref_sys WHERE srid >= " + USER_RANGE_START);
            if (rs.next()) {
                int max = rs.getInt(1);
                if (max >= USER_RANGE_START) {          // can be 0 if no records exist
                    return max + 1;
                }
            }
        }
        return USER_RANGE_START;
    }

    /**
     * After insertion of a new entry, verify if the given {@code gridToCRS} needs to be adjusted for axis order.
     *
     * @todo it is probably to perform a cheaper test.
     */
    final Matrix adjustGridToCRS(Matrix gridToCRS, final CoordinateReferenceSystem crs, final int srid)
            throws FactoryException, SQLException, ParseException
    {
        final CoordinateReferenceSystem actual = getCRS(srid);
        if (!CRS.equivalent(crs, actual)) {
            Matrix adjust = MathTransforms.getMatrix(CRS.findOperation(crs, actual, null).getMathTransform());
            if (adjust == null) {
                throw new FactoryException("Unexpected non-linear transform with SRID " + srid);
            }
            if (!adjust.isIdentity()) {
                gridToCRS = MatrixSIS.castOrCopy(adjust).multiply(gridToCRS);
            }
        }
        return gridToCRS;
    }

    CoordinateReferenceSystem getCRS(int srid) throws FactoryException, SQLException, ParseException {
        if (srid >= USER_RANGE_START) {
            try (Statement stmt = getConnection().createStatement()) {
                final ResultSet rs = stmt.executeQuery("SELECT srtext FROM spatial_ref_sys WHERE srid = " + srid);
                if (rs.next()) {
                    return (CoordinateReferenceSystem) getWKTFormat().parseObject(rs.getString(1));
                } else {
                    throw new NoSuchAuthorityCodeException("Unknown srid " + srid, null, String.valueOf(srid), null);
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

    private WKTFormat getWKTFormat() {
        if (wktFormat == null) {
            wktFormat = new WKTFormat();
            wktFormat.setIndentation(WKTFormat.SINGLE_LINE);
            wktFormat.setConvention(Convention.WKT1_COMMON_UNITS);
        }
        return wktFormat;
    }
}
