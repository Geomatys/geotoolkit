package org.geotoolkit.coverage.grid;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.metadata.AxisDirections;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.datum.PixelInCell;

import static org.opengis.referencing.datum.PixelInCell.CELL_CENTER;

/**
 * Try to generate subsets of a grid. The aim is to create slices over specified
 * dimension, to move along an axis and retrieve all its possible values.
 *
 * @author Alexis Manin (Geomatys)
 */
public class GridGeometryIterator implements Iterator<GridGeometry> {

    /**
     * Source grid. It defines the entire space this iterator will move into.
     */
    final GridGeometry source;

    /**
     * Component in charge of grid iteration.
     */
    private final GridIterator gridIterator;

    /**
     * Create an iterator which will try to split given geometry as a series of
     * 2D geometries.
     *
     * @param source The grid geometry to split.
     * @throws IllegalArgumentException If we cannot determine horizontal axes
     * of the given geometry. We do so because we cannot safely determine axes
     * to iterate over without this information. If you know the indices you
     * must iterate on, you can use {@link #GridGeometryIterator(org.apache.sis.coverage.grid.GridGeometry, int...) },
     * which won't make any analysis of the geometry, and just iterate over
     * specified dimensions.
     */
    public GridGeometryIterator(final GridGeometry source) throws IllegalArgumentException {
        this(GridGeometryIterator.buildSteps(source), source);
    }

    /**
     * Create subsets of the given geometry along specified axes. Note that this
     * constructor does not enforce generation of 2D geometries. It will produce
     * geometries whose dimension match the number of fixed axis.
     * Example : If you've got a 5D geometry, and specify only 2 indices to
     * iterate on, this iterator will produce 3D geometries.
     *
     * @param source The geometry to split.
     * @param movableIndices indices for the dimensions to split and move along.
     */
    public GridGeometryIterator(final GridGeometry source, final int... movableIndices) {
        this(buildSteps(source, movableIndices), source);
    }

    /**
     * Create an iterator whose subsets will be configured using increments
     * given for each dimension.
     * @param steps The increments (in grid term) for each of the geometry axes.
     * This parameter meaning is the same as {@link GridIterator#steps}.
     * @param source The geometry to move into.
     */
    GridGeometryIterator(final int[] steps, final GridGeometry source) {
        ArgumentChecks.ensureNonNull("Source grid", source);
        this.source = source;
        gridIterator = new GridIterator(source.getExtent(), steps);
    }

    @Override
    public boolean hasNext() {
        return gridIterator.hasNext();
    }

    @Override
    public GridGeometry next() {
        if (hasNext()) {
            final GridExtent nextExtent = gridIterator.next();
            return new GridGeometry(nextExtent, CELL_CENTER, source.getGridToCRS(CELL_CENTER), source.getCoordinateReferenceSystem());
        }

        throw new NoSuchElementException("No more slice to iterate over");
    }

    /**
     * Try to deduce iteration strategy to adopt for a given grid geometry.
     * This method will try to extract horizontal component from given geometry
     * referencing. The aim is to mark horizontal axes the only fixed ones. All
     * other will be marked as unitary movable (increment = 1). If no horizontal
     * part is found, an error is raised.
     *
     * @param source The grid geometry to analyze and build a movement pattern
     * for. Cannot be null.
     * @return An array (never null) whose length is source geometry dimension.
     * It contains increments for each dimensions (0 or 1).
     * @throws IllegalArgumentException If no horizontal coordinate system can
     * be found in input geometry.
     */
    static int[] buildSteps(final GridGeometry source) throws IllegalArgumentException {
        ArgumentChecks.ensureNonNull("Source grid", source);
        final GridExtent extent = source.getExtent();
        final CoordinateReferenceSystem crs = source.getCoordinateReferenceSystem();
        if (crs != null) {
            final SingleCRS horizontal = CRS.getHorizontalComponent(crs);
            if (horizontal != null) {
                final int xAxis = AxisDirections.indexOfColinear(crs.getCoordinateSystem(), horizontal.getCoordinateSystem());
                if (xAxis >= 0) {
                    final int[] steps = new int[extent.getDimension()];
                    Arrays.fill(steps, 1);
                    steps[xAxis] = 0;
                    steps[xAxis + 1] = 0;

                    return steps;
                }
            }
        }

        throw new IllegalArgumentException("No horizontal referencing found in given geometry.");
    }

    /**
     * Create unitary increments (for underlying {@link GridIterator} component)
     * for specified dimensions.
     * @param source The source geometry to build increments for. Cannot be null.
     * @param movableIndices Indices of the axes we want to move upon.
     * @return A set of ready-to use increments for iterating over a grid envelope.
     */
    static int[] buildSteps(final GridGeometry source, final int... movableIndices) {
        ArgumentChecks.ensureNonNull("Source grid", source);
        final int[] steps = new int[source.getExtent().getDimension()];
        for (int idx : movableIndices) {
            steps[idx] = 1;
        }

        return steps;
    }

    public static long[] getLow(GridExtent extent) {
        final long[] array = new long[extent.getDimension()];
        for (int i=0;i<array.length;i++) {
            array[i] = extent.getLow(i);
        }
        return array;
    }

    public static long[] getHigh(GridExtent extent) {
        final long[] array = new long[extent.getDimension()];
        for (int i=0;i<array.length;i++) {
            array[i] = extent.getHigh(i);
        }
        return array;
    }

    public static Rectangle toRectangle(GridExtent extent) {
        return new Rectangle(
                (int) extent.getLow(0),
                (int) extent.getLow(1),
                (int) extent.getSize(0),
                (int) extent.getSize(1));
    }

    /**
     * Note : logic copied from Rectangle class, long primitive type variant
     * @param parent
     * @param child
     * @return
     */
    public static boolean isContained2D(GridExtent parent, GridExtent child) {

        long X = child.getLow(0);
        long Y = child.getLow(1);
        long W = child.getSize(0);
        long H = child.getSize(1);

        long x = parent.getLow(0);
        long y = parent.getLow(1);
        long w = parent.getSize(0);
        long h = parent.getSize(1);

        if ((w | h | W | H) < 0) {
            return false;
        }
        if (X < x || Y < y) {
            return false;
        }
        w += x;
        W += X;
        if (W <= X) {
            if (w >= x || W > w) return false;
        } else {
            if (w >= x && W > w) return false;
        }
        h += y;
        H += Y;
        if (H <= Y) {
            if (h >= y || H > h) return false;
        } else {
            if (h >= y && H > h) return false;
        }
        return true;
    }

}
