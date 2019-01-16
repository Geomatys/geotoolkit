/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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

import java.util.List;
import java.util.ArrayList;
import java.sql.Array;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import org.opengis.metadata.Identifier;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.SingleCRS;

import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.referencing.factory.IdentifiedObjectFinder;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ComparisonMode;

import static org.geotoolkit.coverage.sql.GridGeometryEntry.AFFINE_DIMENSION;


/**
 * Connection to a table of grid geometries.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Antoine Hnawia (IRD)
 */
final class GridGeometryTable extends CachedTable<Integer,GridGeometryEntry> {
    /**
     * Name of this table in the database.
     */
    static final String TABLE = "GridGeometries";

    /**
     * Tables of additional axes, created when first needed.
     *
     * @see #getAxisTable()
     */
    private AdditionalAxisTable axisTable;

    /**
     * Creates a new {@code GridGeometryTable}.
     */
    GridGeometryTable(final Transaction transaction) {
        super(Target.GRID_GEOMETRY, transaction);
    }

    /**
     * Returns the SQL {@code SELECT} statement.
     */
    @Override
    String select() {
        return "SELECT \"width\", \"height\", \"scaleX\", \"shearY\", \"shearX\", \"scaleY\", \"translateX\", \"translateY\","
                + " \"approximate\",  \"srid\",\"additionalAxes\""
                + " FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"identifier\" = ?";
    }

    /**
     * Creates a grid geometry from the current row in the specified result set.
     *
     * @param  results     the result set to read.
     * @param  identifier  the identifier of the grid geometry to create.
     * @return the entry for current row in the specified result set.
     * @throws SQLException if an error occurred while reading the database.
     */
    @Override
    @SuppressWarnings("fallthrough")
    GridGeometryEntry createEntry(final ResultSet results, final Integer identifier) throws SQLException, DataStoreException {
        final long width  = results.getLong(1);
        final long height = results.getLong(2);
        final AffineTransform2D gridToCRS = new AffineTransform2D(
                results.getDouble(3),
                results.getDouble(4),
                results.getDouble(5),
                results.getDouble(6),
                results.getDouble(7),
                results.getDouble(8));

        final boolean approximate = results.getBoolean(9);
        final int srid = results.getInt(10);
        final Array refs = results.getArray(11);
        AdditionalAxisEntry[] axes = null;
        String extraDimName = null;
        if (refs != null) {
            final Object data = refs.getArray();
            final int length = java.lang.reflect.Array.getLength(data);
            if (length != 0) {
                axes = new AdditionalAxisEntry[length];
                for (int i=0; i<axes.length; i++) {
                    final String id = (String) java.lang.reflect.Array.get(data, i);
                    extraDimName = (i == 0) ? id : extraDimName + " + " + id;
                    axes[i] = getAxisTable().getEntry(id);
                }
            }
            refs.free();
        }
        try {
            /*
             * TODO: the CRS codes are PostGIS codes, not EPSG codes. For now we simulate PostGIS codes
             * by changing axis order after decoding, but we should really use a PostGIS factory instead.
             */
            CoordinateReferenceSystem crs;
            crs = transaction.database.authorityFactory.createCoordinateReferenceSystem(String.valueOf(srid));
            crs = AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.RIGHT_HANDED);
            return new GridGeometryEntry(width, height, gridToCRS, approximate, crs, axes, extraDimName, transaction.database);
        } catch (FactoryException | TransformException exception) {
            throw new IllegalRecordException(exception, results, 9, identifier);
        }
    }

    /**
     * Returns the identifier for the specified grid geometry. If a suitable entry already exists,
     * its identifier is returned. Otherwise a new entry is inserted and its identifier is returned.
     * Only the {@value GridGeometryEntry#AFFINE_DIMENSION} first dimensions of the given extent and
     * transform are used.
     *
     * @param  extent          width (dimension 0) and height (dimension 1) of the grid.
     * @param  gridToCRS       the transform from grid coordinates to "real world" coordinates.
     * @param  approximate     whether the "grid to CRS" transform is only an approximation of non-linear transform.
     * @param  horizontalSRID  the coordinate reference system for the {@value GridGeometryEntry#AFFINE_DIMENSION} first dimensions.
     * @param  additionalAxes  names of additional axes, or {@code null} if none.
     * @return the identifier of a matching entry.
     * @throws SQLException if the operation failed.
     */
    private int findOrInsert(final GridExtent extent, final Matrix gridToCRS, final boolean approximate, final int horizontalSRID,
            final Array additionalAxes) throws SQLException, IllegalUpdateException
    {
        if (extent.getLow(0) != 0 || extent.getLow(1) != 0) {
            throw new IllegalUpdateException("Grid extent must start at (0,0).");
        }
        boolean insert = false;
        do {
            final PreparedStatement statement;
            if (!insert) {
                statement = prepareStatement("SELECT \"identifier\" FROM " + SCHEMA + ".\"" + TABLE + '"'
                        + " WHERE \"width\"=? AND \"height\"=? AND \"scaleX\"=? AND \"shearY\"=? AND \"shearX\"=?"
                        + " AND \"scaleY\"=? AND \"translateX\"=? AND \"translateY\"=? AND \"approximate\"=? AND \"srid\"=?"
                        + " AND \"additionalAxes\" IS NOT DISTINCT FROM ?");
                /*
                 * Use "IS NOT DISTINCT FROM" instead of "=" for the field where '?' may be NULL.
                 * This is needed because expression like "A = NULL" always evaluate to 'false'.
                 */
            } else {
                statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\"("
                        + "\"width\", \"height\", \"scaleX\", \"shearY\", \"shearX\", \"scaleY\", "
                        + "\"translateX\", \"translateY\", \"approximate\", \"srid\", \"additionalAxes\")"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?)", "identifier");
            }
            final int trc = gridToCRS.getNumCol() - 1;
            statement.setLong   (1, extent.getSize(0));                // width
            statement.setLong   (2, extent.getSize(1));                // height
            statement.setDouble (3, gridToCRS.getElement(0, 0));       // scaleX
            statement.setDouble (4, gridToCRS.getElement(1, 0));       // shearY
            statement.setDouble (5, gridToCRS.getElement(0, 1));       // shearX
            statement.setDouble (6, gridToCRS.getElement(1, 1));       // scaleY
            statement.setDouble (7, gridToCRS.getElement(0, trc));     // translateX
            statement.setDouble (8, gridToCRS.getElement(1, trc));     // translateY
            statement.setBoolean(9, approximate);
            statement.setInt   (10, horizontalSRID);
            if (additionalAxes != null) {
                statement.setArray(11, additionalAxes);
            } else {
                statement.setNull(11, Types.ARRAY);
            }
            if (insert) {
                if (statement.executeUpdate() == 0) {
                    continue;                                           // Should never happen, but we are paranoiac.
                }
            }
            try (ResultSet results = insert ? statement.getGeneratedKeys() : statement.executeQuery()) {
                while (results.next()) {
                    final int identifier = results.getInt(1);
                    if (!results.wasNull()) return identifier;          // Should never be null, but we are paranoiac.
                }
            }
        } while ((insert = !insert) == true);
        throw new IllegalUpdateException("Failed to insert the grid geometry in database.");
    }

    /**
     * Returns the identifier for the specified grid geometry. If a suitable entry already exists,
     * its identifier is returned. Otherwise a new entry is inserted and its identifier is returned.
     *
     * @param  geometry     the geometry to find or inserts.
     * @param  period       an array of length 2 where to store start time and end time.
     * @param  suggestedID  suggested identifier if additional axes need to be inserted.
     * @return the identifier of a matching entry.
     * @throws SQLException if the operation failed.
     */
    final int findOrInsert(final GridGeometry geometry, final Instant[] period, final String suggestedID)
            throws SQLException, FactoryException, TransformException, CatalogException
    {
        final List<String> additionalAxes = new ArrayList<>();
        /*
         * Find grid geometry of the two first source dimensions. This is usually the horizontal axes of the data cube,
         * but not necessarily. The corresponding part of the CRS is usually at the two first dimensions too, but this
         * is not mandatory - the requirement about being the two first axes applies to grid coordinates, not to the CRS.
         */
        final int dimension = geometry.getDimension();          // Dimension of the grid (not necessarily the CRS).
        if (dimension >= AFFINE_DIMENSION) {
            final TransformSeparator sep = new TransformSeparator(geometry.getGridToCRS(GridGeometryEntry.CELL_ORIGIN));
            sep.addSourceDimensionRange(0, AFFINE_DIMENSION);
            final MathTransform gridToCRS2D = sep.separate();
            int[] targetDims = sep.getTargetDimensions();
            if (targetDims.length == AFFINE_DIMENSION) {
                int lower = targetDims[0];
                int upper = targetDims[1];
                if (upper < lower) {
                    lower = upper;
                    upper = targetDims[0];
                }
                if (upper - lower == 1) {
                    final CoordinateReferenceSystem crs = geometry.getCoordinateReferenceSystem();
                    final CoordinateReferenceSystem crs2D = CRS.getComponentAt(crs, lower, upper+1);
                    /*
                     * Iterates over all other dimensions. One of them may be a temporal dimension, which will be handled
                     * in a special way by storing dates in in the "GridCoverages" table instead than coefficients in the
                     * "GridGeometries" table. If more than one temporal dimension exist, only the first one is processed
                     * in that special way; all other temporal dimension are handled like any other additional axis.
                     */
                    final GridExtent extent = geometry.getExtent();
                    for (int i=AFFINE_DIMENSION; i<dimension; i++) {
                        sep.clear();
                        sep.addSourceDimensionRange(i, i+1);
                        final MathTransform gridToCRS = sep.separate();
                        targetDims = sep.getTargetDimensions();
                        if (targetDims.length != 1) {
                            throw new IllegalUpdateException("Unexpected number of target dimensions for source dimension " + i + ".");
                        }
                        final int dim = targetDims[0];
                        final CoordinateReferenceSystem crs1D = CRS.getComponentAt(crs, dim, dim+1);
                        if (period[0] == null && period[1] == null && crs1D instanceof TemporalCRS) {
                            final DefaultTemporalCRS temporal = DefaultTemporalCRS.castOrCopy((TemporalCRS) crs1D);
                            final Envelope range = geometry.getEnvelope();
                            period[0] = temporal.toInstant(range.getMinimum(dim));
                            period[1] = temporal.toInstant(range.getMaximum(dim));
                            if (extent.getSize(dim) > 1) {
                                // add a fake temporal axis with offsets from range start.
                                final double[] values = new double[Math.toIntExact(extent.getSize(dim) + 1)];
                                for (int j=1; j<values.length; j++) values[j] = j;
                                gridToCRS.transform(values, 0, values, 0, values.length);
                                //convert to distance in days
                                for (int j=0; j<values.length; j++) {
                                    values[j] = period[0].until(temporal.toInstant(values[j]), ChronoUnit.DAYS);
                                }
                                additionalAxes.add(getAxisTable().findOrInsert(AdditionalAxisTable.OFFSET, values, suggestedID));
                            }
                        } else {
                            final double[] values = new double[Math.toIntExact(extent.getSize(dim) + 1)];
                            for (int j=1; j<values.length; j++) values[j] = j;
                            gridToCRS.transform(values, 0, values, 0, values.length);
                            additionalAxes.add(getAxisTable().findOrInsert((SingleCRS) crs1D, values, suggestedID));
                            // Above cast should never fail since a 1D CRS can not be compound.
                        }
                    }
                    /*
                     * At this point we collected all additional axes. Process to the insertion.
                     * The first two dimensions should be linear. But if this is not the case,
                     * we will use the coefficients of an approximation at the raster center.
                     */
                    Matrix gridToCRS = MathTransforms.getMatrix(gridToCRS2D);
                    final boolean isLinear = (gridToCRS != null);
                    if (!isLinear) {
                        final GeneralDirectPosition center = new GeneralDirectPosition(AFFINE_DIMENSION);
                        for (int i=0; i<AFFINE_DIMENSION; i++) {
                            center.setOrdinate(i, 0.5*(extent.getLow(i) + (double) extent.getHigh(i)));
                        }
                        gridToCRS = MathTransforms.getMatrix(gridToCRS2D, center);
                        /*
                         * Above linear approximation has been computed for raster center. It may resut in unrealistic coordinates
                         * on raster borders. We adjust by comparing the envelopes, then change the matrix coefficients for having
                         * the same envelope size. The center may be slightly shifted. We keep using the transform for pixel centers
                         * because this adjustment is only approximate anyway.
                         */
                        final GeneralEnvelope env = new GeneralEnvelope(AFFINE_DIMENSION);
                        for (int j=0; j<AFFINE_DIMENSION; j++) {
                            env.setRange(j, extent.getLow(j), extent.getHigh(j));
                        }
                        final Envelope expected = Envelopes.transform(gridToCRS2D, env);
                        final Envelope actual   = Envelopes.transform(MathTransforms.linear(gridToCRS), env);
                        for (int j=0; j<AFFINE_DIMENSION; j++) {
                            final double scale = expected.getSpan(j) / actual.getSpan(j);
                            final double shift = expected.getMedian(j) - actual.getMedian(j);
                            double s = 0;
                            for (int i=0; i<AFFINE_DIMENSION; i++) {
                                double c = gridToCRS.getElement(j, i);
                                gridToCRS.setElement(j, i, c * scale);
                                s += c * center.getOrdinate(i);
                            }
                            s *= (1 - scale);
                            s += gridToCRS.getElement(j, AFFINE_DIMENSION);
                            gridToCRS.setElement(j, AFFINE_DIMENSION, s + shift);
                        }
                    }
                    final Array axes = getConnection().createArrayOf("VARCHAR", additionalAxes.toArray());
                    return findOrInsert(extent, gridToCRS, !isLinear, findCRS(crs2D), axes);
                }
            }
        }
        throw new IllegalUpdateException("Illegal grid geometry.");
    }

    /**
     * Finds a {@code spatial_ref_sys} code for the given coordinate reference system.
     */
    private static int findCRS(CoordinateReferenceSystem crs) throws FactoryException, IllegalUpdateException {
        final IdentifiedObjectFinder finder = IdentifiedObjects.newFinder("EPSG");
        finder.setIgnoringAxes(true);
        AbstractCRS c = AbstractCRS.castOrCopy((CoordinateReferenceSystem) finder.findSingleton(crs));

        if (c != null) {
            boolean match = false;
            AbstractCRS cdt = c;
            match = cdt.equals(crs, ComparisonMode.APPROXIMATIVE);
            if (!match) {
                cdt = cdt.forConvention(AxesConvention.RIGHT_HANDED);
                crs = ((AbstractCRS) crs).forConvention(AxesConvention.RIGHT_HANDED);
                match = cdt.equals(crs, ComparisonMode.APPROXIMATIVE);
            }
            if (!match) {
                cdt = cdt.forConvention(AxesConvention.POSITIVE_RANGE);
                crs = ((AbstractCRS) crs).forConvention(AxesConvention.POSITIVE_RANGE);
                match = cdt.equals(crs, ComparisonMode.APPROXIMATIVE);
            }
            if (match) {
                Identifier id = IdentifiedObjects.getIdentifier(c, Citations.EPSG);
                if (id != null) try {
                    return Integer.valueOf(id.getCode());
                } catch (NumberFormatException e) {
                    throw new IllegalUpdateException("Illegal SRID: " + id);
                }
            }
        }

        /*
         * Temporary hack for Coriolis data (to be removed in a future version).
         */
        if (IdentifiedObjects.isHeuristicMatchForName(crs, "Mercator_1SP (Unspecified datum based upon the GRS 1980 Authalic Sphere)")) {
            return 3395;
        } else if (CRS.findOperation(crs, CommonCRS.defaultGeographic(), null).getMathTransform().isIdentity()) {
            return 4326;
        } else {
            throw new IllegalUpdateException("SRID not found.");
        }
    }

    /**
     * Returns the table of axis, creating it when first needed. We do not create this table at
     * construction time because it is not uncommon to have rasters without vertical dimension.
     */
    private AdditionalAxisTable getAxisTable() {
        if (axisTable == null) {
            axisTable = new AdditionalAxisTable(transaction);
        }
        return axisTable;
    }

    /**
     * Closes the statements used by this table.
     */
    @Override
    public void close() throws SQLException {
        super.close();
        final AdditionalAxisTable t = axisTable;
        if (t != null) {
            axisTable = null;
            t.close();
        }
    }
}
