/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.util.grid;

import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;
import org.geotoolkit.test.Assert;
import org.junit.Test;

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
        MonoDimensionMove it = new MonoDimensionMove(0, new double[]{2.4, 7.6}, 3.8);
        double[] values = StreamSupport.stream(it, parallel)
                .flatMapToDouble(pt -> DoubleStream.of(pt))
                .toArray();

        double[] expectedValues = {
            3.0, 7.6,
            4.0, 7.6,
            5.0, 7.6,
            6.0, 7.6,
            6.2, 7.6
        };

        Assert.assertArrayEquals("Segment populated with crossed grid points", expectedValues, values, 1e-11);

        it = new MonoDimensionMove(1, new double[]{2.4, 7.0}, 3.8);
        values = StreamSupport.stream(it, parallel)
                .flatMapToDouble(pt -> DoubleStream.of(pt))
                .toArray();

        expectedValues = new double[] {
            2.4,  8.0,
            2.4,  9.0,
            2.4, 10.0,
            2.4, 10.8
        };

        Assert.assertArrayEquals("Segment populated with crossed grid points", expectedValues, values, 1e-11);
    }
}
