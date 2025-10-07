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
package org.geotoolkit.storage.rs.internal.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.PixelInCell;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.geotoolkit.storage.rs.Address;
import org.geotoolkit.referencing.rs.ReferenceSystems;
import org.geotoolkit.storage.rs.ReferencedGridTransform;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ReferencedGridTransforms {

    private ReferencedGridTransforms(){}

    public static ReferencedGridTransform toTransform(GridGeometry gg) {
        return new Grid(gg);
    }

    public static ReferencedGridTransform toTransform(ReferenceSystem rs) {
        return new Undefined(rs);
    }

    public static ReferencedGridTransform toTransform(ReferenceSystem rs, List<?> zids) {
        return new Listed(rs, zids);
    }

    public static ReferencedGridTransform compound(ReferencedGridTransform ... trss) {
        if (trss == null || trss.length == 0) return null;
        if (trss.length == 1) return trss[0];
        SubTransform rgt = new Compound((SubTransform)trss[0], (SubTransform)trss[1]);
        for (int i = 2; i < trss.length; i++) {
            rgt = new Compound(rgt, (SubTransform) trss[2]);
        }
        return rgt;
    }

    /**
     * Try to extract a crs part from grid geometry.
     *
     * @param base
     * @param crs
     * @return
     * @throws FactoryException
     */
    public static GridGeometry slice(GridGeometry base, CoordinateReferenceSystem crs) throws FactoryException {
        final List<SingleCRS> singles = (List) ReferenceSystems.getSingleComponents(base.getCoordinateReferenceSystem(), true);
        int idx = 0;
        for (SingleCRS s : singles) {
            if (s == crs) {
                break;
            }
            idx += s.getCoordinateSystem().getDimension();
        }
        if (idx == base.getDimension()) {
            throw new FactoryException("Slice crs not found");
        }

        final MathTransform gridToCRS = base.getGridToCRS(PixelInCell.CELL_CENTER);
        final TransformSeparator ts = new TransformSeparator(gridToCRS);
        ts.addTargetDimensionRange(idx, idx+crs.getCoordinateSystem().getDimension());
        final MathTransform trs = ts.separate();
        final int[] sourceDimensions = ts.getSourceDimensions();

        return base.selectDimensions(sourceDimensions);
    }

    private static abstract class SubTransform implements ReferencedGridTransform {

        @Override
        public Address toAddress(int[] gridPosition) throws TransformException {
            final Object[] ordinates = new Object[getDimension()];
            toAddress(gridPosition, ordinates, 0);
            return new DefaultAddress(getRS(), ordinates);
        }

        @Override
        public int[] toGrid(Address location) throws TransformException {
            final int[] gridPosition = new int[getDimension()];
            toGrid(location.getOrdinates(), gridPosition, 0);
            return gridPosition;
        }

        abstract void toAddress(int[] gridPosition, Object[] location, int offset) throws TransformException;

        abstract void toGrid(Object[] location, int[] gridPosition, int offset) throws TransformException;
    }

    private static class Undefined extends SubTransform {

        private final ReferenceSystem rs;

        public Undefined(ReferenceSystem rs) {
            this.rs = rs;
        }

        @Override
        void toAddress(int[] gridPosition, Object[] location, int offset) throws TransformException {
            throw new TransformException("Not supported.");
        }

        @Override
        void toGrid(Object[] location, int[] gridPosition, int offset) throws TransformException {
            throw new TransformException("Not supported.");
        }

        @Override
        public ReferenceSystem getRS() {
            return rs;
        }

        @Override
        public int getDimension() {
            return ReferenceSystems.getDimension(rs);
        }

        @Override
        public ReferencedGridTransform split(int offset, int size) {
            if (offset == 0 && size == getDimension()) return this;
            throw new IllegalArgumentException("Can not split transform at offset " + offset +" with size " + size);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 11 * hash + Objects.hashCode(this.rs);
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
            final Undefined other = (Undefined) obj;
            return Objects.equals(this.rs, other.rs);
        }
    }
    public static class Grid extends SubTransform {

        private final GridGeometry grid;
        private final MathTransform gridToCRS;
        private MathTransform crsToGrid;
        private final int dimension;

        public Grid(GridGeometry grid) {
            this.grid = grid;
            this.gridToCRS = grid.getGridToCRS(PixelInCell.CELL_CENTER);
            this.dimension = grid.getDimension();
        }

        public GridGeometry getGrid() {
            return grid;
        }

        @Override
        public ReferenceSystem getRS() {
            return grid.isDefined(GridGeometry.CRS) ? grid.getCoordinateReferenceSystem() : null;
        }

        @Override
        public int getDimension() {
            return dimension;
        }

        @Override
        public void toAddress(int[] gridPosition, Object[] location, int offset) throws TransformException {
            final double[] gp = new double[dimension];
            for (int i = 0; i < dimension; i++) gp[i] = gridPosition[offset+i];
            gridToCRS.transform(gp, 0, gp, 0, 1);
            for (int i = 0; i < dimension; i++) location[offset+i] = gp[i];
        }

        @Override
        public void toGrid(Object[] location, int[] gridPosition, int offset) throws TransformException {
            if (crsToGrid == null) {
                //no synchronisation here, in worse case it will be computed a few times
                // but the result will always be the same
                this.crsToGrid = this.gridToCRS.inverse();
            }

            final double[] gp = new double[dimension];
            for(int i = 0; i < dimension; i++) gp[i] = (double) location[offset+i];
            crsToGrid.transform(gp, 0, gp, 0, 1);
            for(int i = 0; i < dimension; i++) gridPosition[offset+i] = (int) gp[i];
        }

        @Override
        public ReferencedGridTransform split(int offset, int size) {
            if (offset == 0 && size == dimension) return this;

            final int[] selection = new int[size];
            for (int i = 0; i < size; i++) selection[i] = offset + i;

            final TransformSeparator ts = new TransformSeparator(grid.getGridToCRS(PixelInCell.CELL_CORNER));
            ts.addSourceDimensions(selection);
            final MathTransform trs;
            try {
                trs = ts.separate();
            } catch (FactoryException ex) {
                throw new IllegalArgumentException("Can not split transform at offset " + offset +" with size " + size);
            }

            int idx = 0;
            int crsSize = 0;
            final List<ReferenceSystem> singleComponents = ReferenceSystems.getSingleComponents(grid.getCoordinateReferenceSystem(), true);
            final List<CoordinateReferenceSystem> toAgg = new ArrayList<>();
            for (ReferenceSystem rs : singleComponents) {
                CoordinateReferenceSystem crs = (CoordinateReferenceSystem) rs;
                if (idx >= offset) {
                    toAgg.add(crs);
                    crsSize += crs.getCoordinateSystem().getDimension();
                    if (crsSize >= size) break;
                }
                idx += crs.getCoordinateSystem().getDimension();
            }
            if (crsSize != size) throw new IllegalArgumentException("Can not split transform at offset " + offset +" with size " + size);

            final CoordinateReferenceSystem sliceCrs;
            try {
                sliceCrs = CRS.compound(toAgg.toArray(CoordinateReferenceSystem[]::new));
            } catch (FactoryException ex) {
                throw new IllegalArgumentException("Can not split transform at offset " + offset +" with size " + size, ex);
            }
            final GridExtent ext = grid.getExtent().selectDimensions(selection);
            final GridGeometry slice = new GridGeometry(ext, PixelInCell.CELL_CORNER, trs, sliceCrs);
            return new Grid(slice);
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
            final Grid other = (Grid) obj;
            return Objects.equals(this.grid, other.grid);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + Objects.hashCode(this.grid);
            return hash;
        }
    }

    public static class Listed extends SubTransform {

        private final ReferenceSystem rs;
        private final List<?> zids;

        public Listed(ReferenceSystem rs, List<?> zids) {
            this.rs = rs;
            this.zids = zids;
        }

        public List<?> getList() {
            return zids;
        }

        @Override
        public ReferenceSystem getRS() {
            return rs;
        }

        @Override
        public int getDimension() {
            return 1;
        }

        @Override
        public Address toAddress(int[] gridPosition) throws TransformException {
            return new DefaultAddress(rs, new Object[]{zids.get(gridPosition[0])});
        }

        @Override
        public int[] toGrid(Address location) throws TransformException {
            return new int[]{zids.indexOf(location.getOrdinate(0))};
        }

        @Override
        public void toAddress(int[] gridPosition, Object[] location, int offset) throws TransformException {
            location[offset] = zids.get(gridPosition[offset]);
        }

        @Override
        public void toGrid(Object[] location, int[] gridPosition, int offset) throws TransformException {
            gridPosition[offset] = zids.indexOf(location[offset]);
        }

        @Override
        public ReferencedGridTransform split(int offset, int size) {
            if (offset == 0 && size == 1) return this;
            throw new IllegalArgumentException("Can not split transform at offset " + offset +" with size " + size);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + Objects.hashCode(this.rs);
            hash = 89 * hash + Objects.hashCode(this.zids);
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
            final Listed other = (Listed) obj;
            if (!Objects.equals(this.rs, other.rs)) {
                return false;
            }
            return Objects.equals(this.zids, other.zids);
        }

    }

    private static class Compound extends SubTransform {

        private final SubTransform trs1;
        private final SubTransform trs2;
        private final int dimension1;
        private final int dimension;
        private final ReferenceSystem rs;

        private Compound(SubTransform trs1, SubTransform trs2) {
            this.trs1 = trs1;
            this.trs2 = trs2;
            dimension1 = trs1.getDimension();
            dimension = dimension1 + trs2.getDimension();
            rs = ReferenceSystems.createCompound(trs1.getRS(), trs2.getRS());
        }

        @Override
        public ReferenceSystem getRS() {
            return rs;
        }

        @Override
        public int getDimension() {
            return dimension;
        }

        @Override
        public void toAddress(int[] gridPosition, Object[] location, int offset) throws TransformException {
            trs1.toAddress(gridPosition, location, offset);
            trs2.toAddress(gridPosition, location, offset+trs1.getDimension());
        }

        @Override
        public void toGrid(Object[] location, int[] gridPosition, int offset) throws TransformException {
            trs1.toGrid(location, gridPosition, offset);
            trs2.toGrid(location, gridPosition, offset+trs1.getDimension());
        }

        @Override
        public ReferencedGridTransform split(int offset, int size) {
            if (offset == 0 && size == dimension) {
                return this;
            } else if (offset < dimension1) {
                return trs1.split(offset, size);
            } else {
                return trs2.split(offset-dimension1, size);
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + Objects.hashCode(this.trs1);
            hash = 89 * hash + Objects.hashCode(this.trs2);
            hash = 89 * hash + Objects.hashCode(this.rs);
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
            final Compound other = (Compound) obj;
            if (!Objects.equals(this.trs1, other.trs1)) {
                return false;
            }
            if (!Objects.equals(this.trs2, other.trs2)) {
                return false;
            }
            return Objects.equals(this.rs, other.rs);
        }
    }


}
