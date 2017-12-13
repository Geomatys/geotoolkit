package org.geotoolkit.coverage.grid;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.coverage.grid.GridEnvelope;

/**
 * Try to slice a given N-D grid envelope. The aim is to provide an iterator
 * which makes flat subset of an envelope on desired axes.
 *
 * By default, the iterator moves from the lower coordinates to the upper one.
 * For axes declared unmovables, the iterator returns the same boundary as input
 * envelope.
 *
 * For example, if you've got a 3D grid, and you want to move along its third
 * axis, you can build an iterator like that :
 *
 * <pre>
 * {@code
 * // Your 3D envelope
 * final GridEnvelope grid3D = new GeneralGridEnvelope({0, 0, 0}, {2, 2, 2} true);
 * // This is an important structure. Each value represents the value to step by
 * // for each dimension on each move. Here, we specify that we want to move
 * // only on the third dimension, from lower to upper corner, with a step of 1
 * final int[] increments = {0, 0, 1};
 * final GridIterator iterator = new GridIterator(grid3D, increments);
 * iterator.next(); // lower={0, 0, 0}; upper={2, 2, 0}
 * iterator.next(); // lower={0, 0, 1}; upper={2, 2, 1}
 * iterator.next(); // lower={0, 0, 2}; upper={2, 2, 2}
 * assert !iterator.hasNext();
 * ...
 * }
 * </pre>
 *
 * @author Alexis Manin (Geomatys)
 */
public class GridIterator implements Iterator<GridEnvelope> {

    /**
     * The source gridEnvelope to decompose into ordered slices.
     */
    final GridEnvelope source;

    /**
     * Contains increments to apply for each dimension. Note that if an
     * increment is equal to 0, it means that we must not iterate over the
     * corresponding dimension, and the generated grid will always have min and
     * max matching source grid for it.
     */
    final int steps[];

    /**
     * Cache lower coordinates of the next grid iteration, for performance purpose.
     */
    private final int[] nextLower;
    /**
     * Cache upper coordinates of the next grid iteration, for performance purpose.
     */
    private final int[] nextUpper;

    /**
     * The next element in the iteration process. Used for getting current state
     * of the iterator.
     */
    private GridEnvelope next;

    /**
     * Create a new iterator to move along specified dimensions of input
     * envelope.
     *
     * @param source The grid envelope to split in subsets.
     * @param steps Definition of the dimensions to move along. Its dimension
     * must be the same as the given grid envelope. Each index corresponds to
     * its counterpart in input envelope. A value of zero means the axis must
     * not be touched, so its boundary will be returned as in source envelope.
     * If the value is strictly positive, the iterator will serve suites of
     * envelopes where the target dimension moves from lower to upper boundary,
     * using the given value as increment.
     */
    public GridIterator(GridEnvelope source, int[] steps) {
        ArgumentChecks.ensureNonNull("Source grid", source);
        ArgumentChecks.ensureNonNull("Movement definition", steps);

        this.source = source;
        this.steps = steps;

        nextLower = source.getLow().getCoordinateValues();
        nextUpper = source.getHigh().getCoordinateValues();
        for (int i = 0; i < steps.length; i++) {
            if (steps[i] < 0) {
                // TODO : reverse browsing
                throw new UnsupportedOperationException("Browsing a grid from high to low is not supported for now");
            } else if (steps[i] > 0) {
                nextUpper[i] = nextLower[i];
            }
        }

        // Prepare immediately the first element, so we do not have to apply
        // minus offset to compute initial element.
        next = new GeneralGridEnvelope(nextLower, nextUpper, true);
    }

    @Override
    public boolean hasNext() {
        for (int i = steps.length - 1; i >= 0 && next == null; i--) {
            if (steps[i] == 0) {
                continue;
            }
            final int nextStep = nextLower[i] + steps[i];
            if (nextStep > source.getHigh(i)) {
                nextLower[i] = nextUpper[i] = source.getLow(i);
            } else {
                nextLower[i] = nextUpper[i] = nextStep;
                next = new GeneralGridEnvelope(nextLower, nextUpper, true);
            }
        }

        return next != null;
    }

    @Override
    public GridEnvelope next() {
        if (hasNext()) {
            final GridEnvelope buffer = next;
            next = null;
            return buffer;
        }

        throw new NoSuchElementException("No more slice available");
    }
}
