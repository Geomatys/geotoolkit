/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.util.grid;

import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class MonoDimensionMoveTest {

    @Test
    public void test2DSequential() {
        test2D(false);
    }

    @Test
    public void test2DParallel() {
        test2D(true);
    }

    private void test2D(boolean parallel) {
        test2DThenInverse(
                parallel, new double[]{2.4, 7.6}, 3.8, 0,
                3.0, 7.6,
                4.0, 7.6,
                5.0, 7.6,
                6.0, 7.6,
                6.2, 7.6
        );

        test2DThenInverse(
                parallel, new double[]{2.4, 7.0}, 3.8, 1,
                2.4,  8.0,
                2.4,  9.0,
                2.4, 10.0,
                2.4, 10.8
        );
    }

    /**
     * Ensure that traversal along a dimension gives expected result, and that an opposite move gives equivalent steps.
     *
     * @param parallel True to activate threading, false otherwise.
     * @param startPoint Movement departure in right direction.
     * @param distance The distance to walk.
     * @param dimension Which dimension should we move along.
     * @param expectedValues Expected steps for traversal in right order (opposite order will be deduced).
     */
    private static void test2DThenInverse(boolean parallel, double[] startPoint, double distance, int dimension, double... expectedValues) {
        MonoDimensionMove it = new MonoDimensionMove(dimension, startPoint, distance);
        double[] values = StreamSupport.stream(it, parallel)
                .flatMapToDouble(pt -> DoubleStream.of(pt))
                .toArray();

        assertArrayEquals("Segment populated with crossed grid points", expectedValues, values, 1e-11);

        // Rewind
        final int length = values.length;
        final double[] inverseValues = new double[length];
        for (int i = length - 3, j = 0 ; i > 0 ; i-=2, j+= 2) {
            inverseValues[j] = values[i-1];
            inverseValues[j+1] = values[i];
        }
        inverseValues[length - 2] = startPoint[0];
        inverseValues[length - 1] = startPoint[1];
        startPoint = new double[]{values[length - 2], values[length - 1]};

        it = new MonoDimensionMove(dimension, startPoint, -distance);
        values = StreamSupport.stream(it, parallel)
                .flatMapToDouble(pt -> DoubleStream.of(pt))
                .toArray();

        assertArrayEquals("Segment populated with crossed grid points", inverseValues, values, 1e-11);
    }
}
