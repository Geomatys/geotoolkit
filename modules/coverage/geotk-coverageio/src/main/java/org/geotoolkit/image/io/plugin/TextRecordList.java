/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.plugin;

import java.util.Arrays;
import javax.imageio.IIOException;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;


/**
 * List of data records in an image. One instance of this class is created by
 * {@link TextRecordImageReader} for every image in a file. A {@code TextRecordList}
 * contains a list of records where each record contains data for one pixel. A record
 * contains usually the following information:
 * <p>
 * <ul>
 *   <li>Pixel's x and y coordinate.</li>
 *   <li>Pixel's values for each band.</li>
 * </li>
 * <p>
 * Those information can appear in arbitrary columns, providing that the column order stay
 * the same for every record in a particular {@code TextRecordList} instance. Records can appear
 * in arbitrary order.
 * <p>
 * Data are floating point value ({@code float} type). Current implementation expects pixels
 * distributed on a regular grid. The grid interval will be automatically computed when needed.
 * The interval computation should be accurate even if there is missing and/or duplicated records.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.08
 *
 * @since 3.08 (derived from 1.2)
 * @module
 */
final class TextRecordList {
    /**
     * The minimal values found in every columns.
     */
    private final double[] min;

    /**
     * The maximal values found in every columns.
     */
    private final double[] max;

    /**
     * The number of points in the axis represented by the given column. The sentinel value 0
     * means that the number of points for an individual column has not yet been computed.
     * This information is typically computed only for {@link #xColumn} and {@link #yColumn}.
     */
    private final int[] pointCount;

    /**
     * A flat array of every records read so far, or {@code null} if the read process has
     * not yet been started. Each row in this matrix has a length of {@link #columnCount}.
     */
    private float[] data;

    /**
     * The column of <var>x</var> or <var>y</var> values.
     */
    final int xColumn, yColumn;

    /**
     * Number of columns in each record.
     */
    final int columnCount;

    /**
     * Index of the first element to write in the {@code #data} array. This is always a multiple
     * of {@link #columnCount} and is incremented after each call to {@link #add(double[])}.
     */
    private int upper;

    /**
     * Tolerance factor when determining if the interval is constant between
     * values in consecutive records.
     */
    private final float gridTolerance;

    /**
     * Creates a new {@code TextRecordList} initialized to the given line.
     *
     * @param firstLine The first line having been read.
     * @param expectedLineCount Number of expected lines. This information is approximative,
     *        but array allocations will be reduced if an exact value is provided.
     */
    public TextRecordList(final double[] firstLine, final int expectedLineCount,
            final int xColumn, final int yColumn, final float gridTolerance)
    {
        min = firstLine.clone();
        max = firstLine.clone();
        this.xColumn = xColumn;
        this.yColumn = yColumn;
        columnCount = firstLine.length;
        for (int i=0; i<firstLine.length; i++) {
            if (Double.isNaN(firstLine[i])) {
                min[i] = Double.POSITIVE_INFINITY;
                max[i] = Double.NEGATIVE_INFINITY;
            }
        }
        data = new float [columnCount * expectedLineCount];
        pointCount = new int[firstLine.length];
        this.gridTolerance = gridTolerance;
    }

    /**
     * Adds the given line. If the given line is shorter than expected, then the missing values
     * are assumed to be NaN. If the given line is longer than the expected length, then the
     * extra values are ignored.
     */
    public void add(final double[] line) {
        final int limit = Math.min(columnCount, line.length);
        final int nextUpper = upper + columnCount;
        if (nextUpper >= data.length) {
            data = Arrays.copyOf(data, nextUpper * 2);
        }
        for (int i=0; i<limit; i++) {
            final double value = line[i];
            if (value < min[i]) min[i] = value;
            if (value > max[i]) max[i] = value;
            data[upper + i] = (float) value;
        }
        Arrays.fill(data, upper + limit, nextUpper, Float.NaN);
        upper = nextUpper;
    }

    /**
     * Trims the internal array to the minimal size needed for holding all data. This method
     * should be invoked only when reading is finished and the array will be kept for a while
     * because {@link TextRecordImageReader#seekForwardOnly} is {@code false}.
     */
    public void trimToSize() {
        data = ArraysExt.resize(data, upper);
    }

    /**
     * Returns a direct reference to internal data. <strong>Do not modify the values</strong>.
     * Valid index range from 0 inclusive to {@link #getDataCount} exclusive.
     */
    final float[] getData() {
        return data;
    }

    /**
     * Returns the length of valid element in the array returned by {@link #getData()}.
     */
    final int getDataCount() {
        return upper;
    }

    /**
     * Returns the number of line read so far.
     */
    public int getLineCount() {
        assert (upper % columnCount) == 0;
        return upper / columnCount;
    }

    /**
     * Returns the number of bands. This is the number of columns not
     * counting the longitude or latitude columns.
     */
    public int getNumBands() {
        return columnCount - (xColumn == yColumn ? 1 : 2);
    }

    /**
     * Returns the column number where to get the data for the given band. In most typical
     * cases, this method just add 2 to the given value in order to skip the longitude and
     * latitude columns. However this method is robust to the cases where the longitude and
     * latitude columns are not in their usual place.
     *
     * @param  band Index of the band to read.
     * @return Index of the column in a record to read.
     */
    public int getColumnForBand(int band) {
        if (band >= xColumn) band++;
        if (band >= yColumn) band++;
        return band;
    }

    /**
     * Returns the minimal value in the given column.
     */
    public double getMinimum(final int column) {
        final double value = min[column];
        return value <= max[column] ? value : Double.NaN;
    }

    /**
     * Returns the maximal value in the given column.
     */
    public double getMaximum(final int column) {
        final double value = max[column];
        return min[column] <= value ? value : Double.NaN;
    }

    /**
     * Returns the interval between the values in the given column. This method checks if
     * the interval is constant, with a tolerance factor given by the {@link #gridTolerance}
     * value.
     *
     * @param  column Column for which the interval is desired.
     * @throws IIOException  If the interval is not constant in the given column.
     */
    private float getInterval(final int column) throws IIOException {
        /*
         * Copies the values of the given column in a temporary array and sort them.
         */
        int count = 0;
        final float[] array = new float[getLineCount()];
        for (int i=column; i<upper; i += columnCount) {
            array[count++] = data[i];
        }
        assert count == array.length;
        Arrays.sort(array);
        /*
         * Removes duplicates values. When duplicates are found, they range
         * from 'lower' to 'upper' inclusive ('upper' is inclusive too!).
         */
        int upper = count-1;
        int lower = count;
        while (--lower >= 1) {
            if (array[upper] != array[lower-1]) {
                if (upper != lower) {
                    System.arraycopy(array, upper, array, lower, count-upper);
                    final int oldCount = count;
                    count -= (upper-lower);
                    Arrays.fill(array, count, oldCount, Float.NaN); // For safety.
                }
                upper = lower-1;
            }
        }
        if (upper != lower) {
            System.arraycopy(array, upper, array, lower, count-upper);
            final int oldCount = count;
            count -= (upper-lower);
            Arrays.fill(array, count, oldCount, Float.NaN); // For safety.
        }
        /*
         * Searches the smallest interval between two records. Next, checks if the interval
         * between every consecutive records is a multiple of that value. This algorithm
         * allow the check to be tolerant to missing records.
         */
        float delta = Float.POSITIVE_INFINITY;
        for (int i=1; i<count; i++) {
            final float d = array[i] - array[i-1];
            assert d > 0;
            if (d < delta) {
                delta = d;
            }
        }
        for (int i=1; i<count; i++) {
            final float  e = (array[i] - array[i-1]) / delta;
            final float re = (float) Math.rint(e);
            if (Math.abs(e - re) > re*gridTolerance) {
                throw new IIOException(Errors.format(Errors.Keys.NOT_A_GRID));
            }
        }
        return delta;
    }

    /**
     * Returns the number of distinct values in the given columns, as if no records were missing.
     * This method assumes that the interval between values in the given column is constant.
     * <p>
     * This method can be invoked only when the image reading is finished, because it caches
     * the value for future reuse (this method is typically invoked more than once for the
     * same column).
     *
     * @param  column Column for which the number of points is desired.
     * @throws IIOException  If the interval is not constant in the given column.
     */
    public int getPointCount(final int column) throws IIOException {
        int n = pointCount[column];
        if (n == 0) {
            n = (int) Math.round((getMaximum(column) - getMinimum(column)) / getInterval(column)) +1;
            pointCount[column] = n;
        }
        return n;
    }

    /**
     * Returns a string representation of this {@code TextRecordList} for debugging purpose.
     */
    @Override
    public String toString() {
        final int oldX = pointCount[xColumn];
        final int oldY = pointCount[yColumn];
        Object xCount, yCount;
        try {
            xCount = getPointCount(xColumn);
        } catch (IIOException exception) {
            xCount = exception.getLocalizedMessage();
        }
        try {
            yCount = getPointCount(yColumn);
        } catch (IIOException exception) {
            yCount = exception.getLocalizedMessage();
        }
        /*
         * Resets the counts to the old value, because this method may be invoked in the debugger
         * while the reading is still under progress. In such case we want to reset the 0 value for
         * forcing getPointCount(...) to compute the new value when it will be requested so.
         */
        pointCount[xColumn] = oldX;
        pointCount[yColumn] = oldY;
        return Vocabulary.format(Vocabulary.Keys.POINT_COUNT_IN_GRID_$3, upper, xCount, yCount);
    }
}
