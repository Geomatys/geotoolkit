/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025-2026, Geomatys
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
package org.geotoolkit.dggs.a5;

import java.math.BigInteger;
import org.geotoolkit.dggs.a5.internal.core.Cell;
import org.geotoolkit.dggs.a5.internal.core.CellInfo;
import org.geotoolkit.dggs.a5.internal.core.CompactionMarker;
import org.geotoolkit.dggs.a5.internal.core.Hex;
import org.geotoolkit.dggs.a5.internal.core.Migrate;
import org.geotoolkit.dggs.a5.internal.core.Serialization;
import org.geotoolkit.dggs.a5.internal.coverings.Compact;
import org.geotoolkit.dggs.a5.internal.coverings.Measures;
import org.geotoolkit.dggs.a5.internal.coverings.Resolution;
import org.geotoolkit.dggs.a5.internal.coverings.SetOperations;
import org.geotoolkit.dggs.a5.internal.regions.Polygon;
import org.geotoolkit.dggs.a5.internal.regions.Polygon.PolygonContainment;
import org.geotoolkit.dggs.a5.internal.regions.Subcell;
import org.geotoolkit.dggs.a5.internal.traversal.Cap;
import org.geotoolkit.dggs.a5.internal.traversal.GridDisk;
import org.geotoolkit.dggs.a5.internal.traversal.Line;

/**
 * Public API of the A5 DGGS, mirroring the exports of upstream a5 {@code modules/index.ts} (v1.0.0).
 *
 * <p>Conventions :</p>
 * <ul>
 *   <li>Cell identifiers are unsigned 64 bits integers stored in a {@code long}. Use {@link Long#compareUnsigned(long, long)}
 *       to order them and {@link #u64ToHex(long)} / {@link #hexToU64(String)} for their text form.</li>
 *   <li>Geographic coordinates are {@code double[] {longitude, latitude}} in degrees.</li>
 *   <li>Coverings (sets of cells at one resolution) are {@code long[]}. When produced by this API they are compacted
 *       and end with a compaction marker (see {@link #isCompactionMarker(long)}), use {@link #uncompact(long[])}
 *       to obtain the cells at the covering resolution.</li>
 * </ul>
 *
 * <p>Cell identifiers of A5 v1 are not compatible with those of A5 0.x, use {@link #migrate(long)} to convert them.</p>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class A5 {

    /**
     * Finest resolution supported.
     */
    public static final int MAX_RESOLUTION = Serialization.MAX_RESOLUTION;

    /**
     * Cell covering the whole world, resolution -1.
     */
    public static final long WORLD_CELL = Serialization.WORLD_CELL;

    private A5(){}

    // Indexing ////////////////////////////////////////////////////////////////

    /**
     * Returns the boundary of a cell, as a closed ring with automatic edge segmentation.
     *
     * @param cellId cell identifier
     * @return boundary as {lon, lat} points in degrees
     */
    public static double[][] cellToBoundary(long cellId) {
        return Cell.cellToBoundary(cellId);
    }

    /**
     * Returns the boundary of a cell.
     *
     * @param cellId cell identifier
     * @param closedRing whether to repeat the first point at the end
     * @param segments number of segments per edge, or {@link Cell.CellToBoundaryOptions#AUTO}
     * @return boundary as {lon, lat} points in degrees
     */
    public static double[][] cellToBoundary(long cellId, boolean closedRing, int segments) {
        return Cell.cellToBoundary(cellId, new Cell.CellToBoundaryOptions(closedRing, segments));
    }

    /**
     * @param cellId cell identifier
     * @return cell center as {lon, lat} in degrees
     */
    public static double[] cellToLonLat(long cellId) {
        return Cell.cellToLonLat(cellId);
    }

    /**
     * @param lonLat {lon, lat} in degrees
     * @param resolution cell resolution
     * @return identifier of the cell containing the point
     */
    public static long lonLatToCell(double[] lonLat, int resolution) {
        return Cell.lonLatToCell(lonLat, resolution);
    }

    /**
     * @param hex hexadecimal text form
     * @return cell identifier
     */
    public static long hexToU64(String hex) {
        return Hex.hexToU64(hex);
    }

    /**
     * @param index cell identifier
     * @return hexadecimal text form, lower case without padding
     */
    public static String u64ToHex(long index) {
        return Hex.u64ToHex(index);
    }

    // Hierarchy ///////////////////////////////////////////////////////////////

    /**
     * @param index cell identifier
     * @return parent cell at the previous resolution
     */
    public static long cellToParent(long index) {
        return Serialization.cellToParent(index);
    }

    /**
     * @param index cell identifier
     * @param parentResolution parent resolution
     * @return parent cell at the given resolution
     */
    public static long cellToParent(long index, int parentResolution) {
        return Serialization.cellToParent(index, parentResolution);
    }

    /**
     * @param index cell identifier
     * @return children cells at the next resolution
     */
    public static long[] cellToChildren(long index) {
        return Serialization.cellToChildren(index);
    }

    /**
     * @param index cell identifier
     * @param childResolution children resolution
     * @return children cells at the given resolution
     */
    public static long[] cellToChildren(long index, int childResolution) {
        return Serialization.cellToChildren(index, childResolution);
    }

    /**
     * @param index cell identifier
     * @return cell resolution, -1 for the world cell
     */
    public static int getResolution(long index) {
        return Serialization.getResolution(index);
    }

    /**
     * @return the 12 cells of resolution 0
     */
    public static long[] getRes0Cells() {
        return Serialization.getRes0Cells();
    }

    /**
     * @param cell value to test
     * @return true if the value is a valid cell identifier
     */
    public static boolean isValidCell(long cell) {
        return Serialization.isValidCell(cell);
    }

    /**
     * @param resolution cell resolution
     * @return number of cells at the given resolution (exact while lower than 2^53)
     */
    public static double getNumCells(int resolution) {
        return CellInfo.getNumCells(resolution);
    }

    /**
     * @param resolution cell resolution
     * @return exact number of cells at the given resolution
     */
    public static BigInteger getNumCellsExact(int resolution) {
        return CellInfo.getNumCells(BigInteger.valueOf(resolution));
    }

    /**
     * @param parentResolution parent resolution, -1 for the world cell
     * @param childResolution descendants resolution
     * @return number of descendants a cell has at the finer resolution
     */
    public static double getNumChildren(int parentResolution, int childResolution) {
        return CellInfo.getNumChildren(parentResolution, childResolution);
    }

    /**
     * @param resolution cell resolution
     * @return area of a cell in square meters
     */
    public static double cellArea(int resolution) {
        return CellInfo.cellArea(resolution);
    }

    /**
     * @param resolution cell resolution
     * @return average edge length of a cell in meters
     */
    public static double cellEdgeLengthAvg(int resolution) {
        return CellInfo.cellEdgeLengthAvg(resolution);
    }

    /**
     * @param cell cell identifier
     * @param resolution finer resolution
     * @return cells at the finer resolution whose centers lie in the cell
     */
    public static long[] cellToSubcells(long cell, int resolution) {
        return Subcell.cellToSubcells(cell, resolution);
    }

    /**
     * @param cell cell identifier
     * @param resolution coarser resolution
     * @return cell at the coarser resolution containing the center of the cell
     */
    public static long cellToSupercell(long cell, int resolution) {
        return Subcell.cellToSupercell(cell, resolution);
    }

    /**
     * @param cells cells at one resolution
     * @return compacted cells, ending with a compaction marker
     */
    public static long[] compact(long[] cells) {
        return Compact.compact(cells);
    }

    /**
     * @param cells compacted cells
     * @return all cells at the covering resolution
     */
    public static long[] uncompact(long[] cells) {
        return Compact.uncompact(cells);
    }

    /**
     * @param value value to test
     * @return true if the value is a compaction marker, not a cell
     */
    public static boolean isCompactionMarker(long value) {
        return CompactionMarker.isCompactionMarker(value);
    }

    // Coverings ///////////////////////////////////////////////////////////////

    /**
     * @param cells covering
     * @return resolution of the covering
     */
    public static int coveringResolution(long[] cells) {
        return Resolution.coveringResolution(cells);
    }

    /**
     * @param cells covering
     * @param cell cell to test
     * @return true if the cell is entirely covered
     */
    public static boolean contains(long[] cells, long cell) {
        return SetOperations.contains(cells, cell);
    }

    /**
     * @return cells in a but not in b, compacted
     */
    public static long[] difference(long[] a, long[] b) {
        return SetOperations.difference(a, b);
    }

    /**
     * @return cells in both a and b, compacted
     */
    public static long[] intersect(long[] a, long[] b) {
        return SetOperations.intersect(a, b);
    }

    /**
     * @return true if a and b share at least one cell
     */
    public static boolean overlaps(long[] a, long[] b) {
        return SetOperations.overlaps(a, b);
    }

    /**
     * @return cells in either a or b, compacted
     */
    public static long[] union(long[] a, long[] b) {
        return SetOperations.union(a, b);
    }

    /**
     * @param cells covering
     * @return area in square meters
     */
    public static double area(long[] cells) {
        return Measures.area(cells);
    }

    /**
     * @param cells covering
     * @return number of cells at the covering resolution
     */
    public static BigInteger count(long[] cells) {
        return Measures.count(cells);
    }

    // Migration ///////////////////////////////////////////////////////////////

    /**
     * @param cell cell identifier of A5 0.x
     * @return identifier of the same cell in A5 v1
     */
    public static long migrate(long cell) {
        return Migrate.migrate(cell);
    }

    // Traversal ///////////////////////////////////////////////////////////////

    /**
     * @param cellId center cell
     * @param k number of hops
     * @return compacted cells sharing an edge within k hops, center included, ending with a compaction marker
     */
    public static long[] gridDisk(long cellId, int k) {
        return GridDisk.gridDisk(cellId, k);
    }

    /**
     * @param cellId center cell
     * @param k number of hops
     * @return compacted cells sharing an edge or vertex within k hops, center included, ending with a compaction marker
     */
    public static long[] gridDiskVertex(long cellId, int k) {
        return GridDisk.gridDiskVertex(cellId, k);
    }

    /**
     * @param cellId center cell
     * @param radius great-circle radius in meters
     * @return compacted cells, ending with a compaction marker
     */
    public static long[] sphericalCap(long cellId, double radius) {
        return Cap.sphericalCap(cellId, radius);
    }

    /**
     * @param waypoints {lon, lat} points in degrees
     * @param resolution cell resolution
     * @return cells along the great-circle arcs, in path order
     */
    public static long[] lineStringToCells(double[][] waypoints, int resolution) {
        return Line.lineStringToCells(waypoints, resolution);
    }

    // Regions /////////////////////////////////////////////////////////////////

    /**
     * @param ring {lon, lat} points in degrees
     * @param resolution cell resolution
     * @param overlapping true to include cells overlapping the polygon, false for cells whose center is inside
     * @return compacted cells, ending with a compaction marker
     */
    public static long[] polygonToCells(double[][] ring, int resolution, boolean overlapping) {
        return Polygon.polygonToCells(ring, resolution, overlapping ? PolygonContainment.overlapping : PolygonContainment.center);
    }

    /**
     * @param rings exterior ring followed by holes, {lon, lat} points in degrees
     * @param resolution cell resolution
     * @param overlapping true to include cells overlapping the polygon, false for cells whose center is inside
     * @return compacted cells, ending with a compaction marker
     */
    public static long[] polygonToCells(double[][][] rings, int resolution, boolean overlapping) {
        return Polygon.polygonToCells(rings, resolution, overlapping ? PolygonContainment.overlapping : PolygonContainment.center);
    }
}
