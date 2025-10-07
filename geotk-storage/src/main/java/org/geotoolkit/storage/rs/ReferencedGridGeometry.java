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
package org.geotoolkit.storage.rs;

import org.geotoolkit.referencing.rs.ReferenceSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.IncompleteGridGeometryException;
import org.apache.sis.coverage.grid.PixelInCell;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.rs.internal.shared.ReferencedGridTransforms;
import org.geotoolkit.util.StringUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.OperationNotFoundException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Referenced system grid geometry.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ReferencedGridGeometry {

    /**
     * A bitmask to specify the validity of the Reference System property.
     *
     * @see #isDefined(int)
     * @see #getReferenceSystem()
     */
    public static final int RS = 1;

    /**
     * A bitmask to specify the validity of the grid extent property.
     *
     * @see #isDefined(int)
     * @see #getExtent()
     */
    public static final int EXTENT = 2;

    /**
     * A bitmask to specify the validity of the <q>grid to RS</q> transform.
     *
     * @see #isDefined(int)
     * @see #getGridToRS(PixelInCell)
     */
    public static final int GRID_TO_RS = 4;

    /**
     * A bitmask to specify the validity of the geographic bounding box.
     *
     * @see #getGeographicExtent()
     */
    public static final int GEOGRAPHIC_EXTENT = 8;


    private final ReferenceSystem rs;
    private final GeographicBoundingBox bbox;
    private final GridExtent extent;
    private final ReferencedGridTransform gridToRS;

    public ReferencedGridGeometry(GridGeometry grid) {
        this(grid.isDefined(GridGeometry.CRS) ? grid.getCoordinateReferenceSystem() : null,
             grid.isDefined(GridGeometry.EXTENT) ? grid.getExtent() : null,
             grid.isDefined(GridGeometry.GRID_TO_CRS) ? ReferencedGridTransforms.toTransform(grid) : null,
             null);
    }

    public ReferencedGridGeometry(ReferenceSystem rs, GridExtent extent, ReferencedGridTransform gridToRS, GeographicBoundingBox bbox) {
        this.rs = rs;
        this.extent = extent;
        this.gridToRS = gridToRS;
        this.bbox = bbox;

        final int nbDim = (rs != null) ? ReferenceSystems.getDimension(rs) : ((extent != null) ? extent.getDimension() : 0);
        if (extent != null && extent.getDimension() != nbDim) {
            throw new IllegalArgumentException("Extent does not have the same number of dimension as the reference system, expected " + nbDim);
        }
        if (gridToRS != null && gridToRS.getDimension() != nbDim) {
            throw new IllegalArgumentException("Transform does not have the same number of dimension as the reference system, expected " + nbDim);
        }
    }

    /**
     * Returns the ReferenceSystem.
     *
     * @return ReferenceSystem, never null
     */
    public ReferenceSystem getReferenceSystem() {
        return rs;
    }

    public GridExtent getExtent() {
        return extent;
    }

    public ReferencedGridTransform getGridToRS() {
        return gridToRS;
    }

    /**
     * Returns an <em>estimation</em> of the grid resolution, in units of the reference system axes.
     * The length of the returned array is the number of RS dimensions, with {@code resolution[0]}
     * being the resolution along the first RS, {@code resolution[1]} the resolution along the second RS,
     * <i>etc</i>. Note that this order is not necessarily the same as grid axis order.
     *
     * <p>If the resolution at RS dimension <var>i</var> is not a constant factor
     * then {@code resolution[i]} is set to one of the following values:</p>
     *
     * <ul>
     *   <li>{@link Double#NaN} if {@code allowEstimates} is {@code false}.</li>
     *   <li>An arbitrary representative resolution otherwise.</li>
     * </ul>
     *
     * @param allowEstimates whether to provide some values even for resolutions that are not constant factors.
     * @return an <em>estimation</em> of the grid resolution or null
     */
    public double[] getResolution(final boolean allowEstimates) {
        if (rs == null || extent == null || gridToRS == null) return null;

        final List<ReferenceSystem> singles = ReferenceSystems.getSingleComponents(rs, true);
        if (singles.size() == 1) {
            if (gridToRS instanceof ReferencedGridTransforms.Grid g) {
                return g.getGrid().getResolution(allowEstimates);
            } else {
                final double[] res = new double[gridToRS.getDimension()];
                Arrays.fill(res, Double.NaN);
                return res;
            }
        } else {
            //slice it to evaluate it
            double[] res = new double[0];
            for (ReferenceSystem rs : singles) {
                final double[] rg = slice(rs).get().getResolution(allowEstimates);
                res = ArraysExt.concatenate(res, rg);
            }
            return res;
        }
    }

    /**
     * Resolution in CRS units.
     */
    public double[] getResolutionProjected(final boolean allowEstimates) {
        if (rs == null || extent == null || gridToRS == null) return null;

        final List<ReferenceSystem> singles = ReferenceSystems.getSingleComponents(rs, true);
        if (singles.size() == 1) {
            if (gridToRS instanceof ReferencedGridTransforms.Grid g) {
                return g.getGrid().getResolution(allowEstimates);
            } else {
                final double[] res = new double[gridToRS.getDimension()];
                Arrays.fill(res, Double.NaN);
                return res;
            }
        } else {
            //slice it to evaluate it
            double[] res = new double[0];
            for (ReferenceSystem rs : singles) {
                final double[] rg = slice(rs).get().getResolutionProjected(allowEstimates);
                res = ArraysExt.concatenate(res, rg);
            }
            return res;
        }
    }

    public Envelope getEnvelope() {
        final List<ReferenceSystem> singles = ReferenceSystems.getSingleComponents(rs, true);
        if (singles.size() == 1) {
            if (gridToRS instanceof ReferencedGridTransforms.Grid rgg) {
                return rgg.getGrid().getEnvelope();
            } else {
                throw new UnsupportedOperationException("todo");
            }
        } else {
            try {
                Envelope env = null;
                for (ReferenceSystem rs : singles) {
                    Envelope se = slice(rs).get().getEnvelope();
                    env = (env == null) ? se : Envelopes.compound(env, se);
                }
                return env;
            } catch (FactoryException ex) {
                return null;
            }
        }
    }

    public Envelope getEnvelope(CoordinateReferenceSystem crs) throws TransformException {
        final List<ReferenceSystem> singles = ReferenceSystems.getSingleComponents(rs, true);
        if (singles.size() == 1) {
            if (gridToRS instanceof ReferencedGridTransforms.Grid rgg) {
                return rgg.getGrid().getEnvelope(crs);
            } else {
                throw new UnsupportedOperationException("todo");
            }
        } else {
            try {
                Envelope env = null;
                for (ReferenceSystem rs : singles) {
                    ReferencedGridGeometry sbs = slice(rs).get();
                    try {
                        Envelope se = sbs.getEnvelope(crs);
                        env = (env == null) ? se : Envelopes.compound(env, se);
                    } catch (TransformException ex) {
                        if (ex.getCause() instanceof OperationNotFoundException e) {
                            //not compatible crs, skip it
                        } else {
                            throw ex;
                        }
                    }
                }
                return env;
            } catch (FactoryException ex) {
                return null;
            }
        }
    }

   /**
     * Returns the approximate latitude and longitude coordinates of the grid.
     * The prime meridian is Greenwich, but the geodetic reference frame is not necessarily WGS 84.
     * This is computed from the {@linkplain #getEnvelope() envelope} if the coordinate reference system
     * contains an horizontal component such as a geographic or projected CRS.
     *
     * @return the geographic bounding box in "real world" coordinates.
     */
    public GeographicBoundingBox getGeographicExtent() {
        return bbox;
    }

    /**
     * Returns {@code true} if all the properties specified by the argument are set.
     * If this method returns {@code true}, then invoking the corresponding getter
     * methods will not throw {@link IncompleteGridGeometryException}.
     *
     * @param  bitmask  any combination of {@link #RS}, {@link #ENVELOPE}, {@link #EXTENT},
     *         {@link #GRID_TO_RS} and derived bit masks.
     * @return {@code true} if all specified properties are defined (i.e. invoking the
     *         corresponding getter methods will not throw {@link IncompleteGridGeometryException}).
     * @throws IllegalArgumentException if the specified bitmask is not a combination of known masks.
     *
     * @see #getReferenceSystem()
     * @see #getEnvelope()
     * @see #getExtent()
     * @see #getGridToRS()
     */
    public boolean isDefined(final int bitmask) {
        if ((bitmask & ~(RS | EXTENT | GRID_TO_RS | GEOGRAPHIC_EXTENT)) != 0) {
            throw new IllegalArgumentException("Incorrect bitmask values");
        }
        return ((bitmask & RS)               == 0 || (null != getReferenceSystem()))
            && ((bitmask & EXTENT)            == 0 || (null != extent))
            && ((bitmask & GRID_TO_RS)       == 0 || (null != gridToRS))
            && ((bitmask & GEOGRAPHIC_EXTENT) == 0 || (null != getGeographicExtent()));
    }

    public Optional<ReferencedGridGeometry> slice(ReferenceSystem rs) {
        if (this.rs.equals(rs)) {
            return Optional.of(this);
        }

        int offset = 0;
        for (ReferenceSystem single : ReferenceSystems.getSingleComponents(this.rs, true)) {
            if (single.equals(rs)) {
                if (rs instanceof DiscreteGlobalGridReferenceSystem dggrs) {
                    final int crsDim = ReferenceSystems.getDimension(dggrs);
                    GridExtent ext = null;
                    if (extent != null) {
                        final int[] select = new int[crsDim];
                        for (int i = 0; i < select.length; i++) {
                            select[i] = offset + i;
                        }
                        ext = extent.selectDimensions(select);
                    }
                    ReferencedGridTransform subtrs = null;
                    if (gridToRS != null) {
                        subtrs = gridToRS.split(offset, crsDim);
                    }
                    return Optional.of(new DiscreteGlobalGridGeometry(dggrs, ext, subtrs, bbox));

                } else if (rs instanceof CoordinateReferenceSystem crs) {
                    final int crsDim = ReferenceSystems.getDimension(crs);
                    GridExtent ext = null;
                    if (extent != null) {
                        final int[] select = new int[crsDim];
                        for (int i = 0; i < select.length; i++) {
                            select[i] = offset + i;
                        }
                        ext = extent.selectDimensions(select);
                    }
                    ReferencedGridTransform subtrs = null;
                    if (gridToRS != null) {
                        subtrs = gridToRS.split(offset, crsDim);
                    }

                    return Optional.of(new ReferencedGridGeometry(rs, ext, subtrs, null));
                } else {
                    throw new UnsupportedOperationException("Unexpected reference system " + rs.getClass().getName());
                }
            }
            offset += ReferenceSystems.getDimension(single);
        }

        return Optional.empty();
    }

    public Optional<GridGeometry> isRegularGrid() {
        if (gridToRS instanceof ReferencedGridTransforms.Grid g) {
            return Optional.of(g.getGrid());
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        final List lst = new ArrayList();
        if (rs != null) lst.add(rs);
        if (extent != null) lst.add(extent);
        if (gridToRS != null) lst.add(gridToRS);
        return StringUtilities.toStringTree("ReferencedGridGeometry", lst);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.rs);
        hash = 23 * hash + Objects.hashCode(this.bbox);
        hash = 23 * hash + Objects.hashCode(this.extent);
        hash = 23 * hash + Objects.hashCode(this.gridToRS);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReferencedGridGeometry other = (ReferencedGridGeometry) obj;
        if (!Objects.equals(this.rs, other.rs)) {
            return false;
        }
        if (!Objects.equals(this.bbox, other.bbox)) {
            return false;
        }
        if (!Objects.equals(this.extent, other.extent)) {
            return false;
        }
        return Objects.equals(this.gridToRS, other.gridToRS);
    }

    public static ReferencedGridGeometry compound(ReferencedGridGeometry ... grids) {
        if (grids.length == 0) return null;
        if (grids.length == 1) return grids[0];


        long[] low = grids[0].getExtent().getLow().getCoordinateValues();
        long[] high = grids[0].getExtent().getHigh().getCoordinateValues();
        ReferencedGridTransform gridToRS = grids[0].getGridToRS();
        ReferenceSystem rs = grids[0].getReferenceSystem();

        for (int i = 1; i < grids.length; i++) {
            final ReferencedGridGeometry rgg = grids[i];
            final GridExtent subExtent = rgg.getExtent();
            final ReferencedGridTransform subGridToRS = rgg.getGridToRS();
            final ReferenceSystem subrs = rgg.getReferenceSystem();
            low = ArraysExt.concatenate(low, subExtent.getLow().getCoordinateValues());
            high = ArraysExt.concatenate(high, subExtent.getHigh().getCoordinateValues());
            gridToRS = ReferencedGridTransforms.compound(gridToRS, subGridToRS);
            rs = ReferenceSystems.createCompound(rs, subrs);
        }

        final GridExtent extent = new GridExtent(null, low, high, true);
        return new ReferencedGridGeometry(rs, extent, gridToRS, null);
    }
}
