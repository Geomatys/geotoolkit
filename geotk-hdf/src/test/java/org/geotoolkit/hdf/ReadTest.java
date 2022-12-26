/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.hdf;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStores;
import org.geotoolkit.hdf.api.Dataset;
import org.geotoolkit.hdf.datatype.Chars;
import org.geotoolkit.hdf.datatype.Compound;
import org.geotoolkit.hdf.datatype.FixedPoint;
import org.geotoolkit.hdf.datatype.FloatingPoint;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author husky
 */
public class ReadTest {

    private static final double TOLERANCE = 0.0;

    @Test
    @org.junit.Ignore("Needs debugging")
    public void testCompound2D() throws DataStoreException, IOException {

        URL url = ReadTest.class.getResource("test_data.h5");
        try (HDF5Store store = (HDF5Store) DataStores.open(url)) {
//            System.out.println(store.toString());

            { // 1D floating point
                final Dataset dataset = (Dataset) store.getComponent("array_1D");
                assertTrue(dataset.getDataType() instanceof FloatingPoint);
                assertArrayEquals(new int[]{50}, dataset.getDataspace().getDimensionSizes());
                double[] values;

                //read all
                values = (double[]) dataset.read(null);
                assertEquals(50, values.length);
                assertArrayEquals(new double[]{0.0,0.1,0.2,0.3,0.4,0.5}, Arrays.copyOf(values, 6), TOLERANCE);

                //read part
                values = (double[]) dataset.read(new GridExtent(null, new long[]{5}, new long[]{10}, false), 0);
                assertEquals(5, values.length);
                assertArrayEquals(new double[]{0.5,0.6,0.7,0.8,0.9}, values, TOLERANCE);
            }

            { // 2D floating point
                final Dataset dataset = (Dataset) store.getComponent("array_2D");
                assertTrue(dataset.getDataType() instanceof FixedPoint);
                assertArrayEquals(new int[]{20,20}, dataset.getDataspace().getDimensionSizes());
                int[][] values;

                //read all
                values = (int[][]) dataset.read(null);
                assertEquals(20, values.length);
                assertEquals(20, values[19].length);

                //read part
                values = (int[][]) dataset.read(new GridExtent(null, new long[]{5,12}, new long[]{8,14}, false));
                assertArrayEquals(new int[]{10512,10513}, values[0]);
                assertArrayEquals(new int[]{10612,10613}, values[1]);
                assertArrayEquals(new int[]{10712,10713}, values[2]);
            }

            { // 3D floating point
                final Dataset dataset = (Dataset) store.getComponent("array_3D");
                assertTrue(dataset.getDataType() instanceof FixedPoint);
                assertArrayEquals(new int[]{20,20,20}, dataset.getDataspace().getDimensionSizes());
                int[][][] values;

                //read all
                values = (int[][][]) dataset.read(null);
                assertEquals(20, values.length);
                assertEquals(20, values[19].length);
                assertEquals(20, values[10][19].length);

                //read part
                values = (int[][][]) dataset.read(new GridExtent(null, new long[]{5,12,17}, new long[]{8,14,19}, false));
                assertArrayEquals(new int[]{1051217,1051218}, values[0][0]);
                assertArrayEquals(new int[]{1061217,1061218}, values[1][0]);
                assertArrayEquals(new int[]{1071217,1071218}, values[2][0]);
                assertArrayEquals(new int[]{1051317,1051318}, values[0][1]);
                assertArrayEquals(new int[]{1061317,1061318}, values[1][1]);
                assertArrayEquals(new int[]{1071317,1071318}, values[2][1]);
            }

            { // 1D floating point
                final Dataset dataset = (Dataset) store.getComponent("table");
                assertTrue(dataset.getDataType() instanceof Compound);
                Compound cmpd = (Compound) dataset.getDataType();
                assertEquals(Object[].class, cmpd.getValueClass());
                assertEquals(4, cmpd.members.length);
                assertTrue(cmpd.members[0].memberType instanceof FixedPoint);
                assertTrue(cmpd.members[1].memberType instanceof FloatingPoint);
                assertTrue(cmpd.members[2].memberType instanceof FixedPoint);
                assertTrue(cmpd.members[3].memberType instanceof Chars);
                assertEquals("index", cmpd.members[0].name);
                assertEquals("index_divided_by_ten", cmpd.members[1].name);
                assertEquals("index_multiplied_by_ten", cmpd.members[2].name);
                assertEquals("status", cmpd.members[3].name);
                assertArrayEquals(new int[]{50}, dataset.getDataspace().getDimensionSizes());
                Object[][] values;

                //read all
                values = (Object[][]) dataset.read(null);
                assertEquals(50, values.length);
                assertArrayEquals(new Object[]{(byte)0, 0.0f,  0, "OK"}, values[0]);
                assertArrayEquals(new Object[]{(byte)1, 0.1f, 10, "OK"}, values[1]);
                assertArrayEquals(new Object[]{(byte)2, 0.2f, 20, "OK"}, values[2]);
                assertArrayEquals(new Object[]{(byte)3, 0.3f, 30, "OK"}, values[3]);
                assertArrayEquals(new Object[]{(byte)4, 0.4f, 40, "OK"}, values[4]);

                //read part
                values = (Object[][]) dataset.read(new GridExtent(null, new long[]{5}, new long[]{10}, false));
                assertEquals(5, values.length);
                assertArrayEquals(new Object[]{(byte)5, 0.5f, 50, "OK"}, values[0]);
                assertArrayEquals(new Object[]{(byte)6, 0.6f, 60, "OK"}, values[1]);
                assertArrayEquals(new Object[]{(byte)7, 0.7f, 70, "OK"}, values[2]);
                assertArrayEquals(new Object[]{(byte)8, 0.8f, 80, "OK"}, values[3]);
                assertArrayEquals(new Object[]{(byte)9, 0.9f, 90, "OK"}, values[4]);

                //read part with compound index
                values = (Object[][]) dataset.read(new GridExtent(null, new long[]{5}, new long[]{10}, false),1,3);
                assertEquals(5, values.length);
                assertArrayEquals(new Object[]{0.5f, "OK"}, values[0]);
                assertArrayEquals(new Object[]{0.6f, "OK"}, values[1]);
                assertArrayEquals(new Object[]{0.7f, "OK"}, values[2]);
                assertArrayEquals(new Object[]{0.8f, "OK"}, values[3]);
                assertArrayEquals(new Object[]{0.9f, "OK"}, values[4]);
            }

            { // 2D floating point
                final Dataset dataset = (Dataset) store.getComponent("table_2D");
                assertTrue(dataset.getDataType() instanceof Compound);
                Compound cmpd = (Compound) dataset.getDataType();
                assertEquals(Object[].class, cmpd.getValueClass());
                assertEquals(2, cmpd.members.length);
                assertTrue(cmpd.members[0].memberType instanceof FixedPoint);
                assertTrue(cmpd.members[1].memberType instanceof FixedPoint);
                assertEquals("j", cmpd.members[0].name);
                assertEquals("i", cmpd.members[1].name);
                assertArrayEquals(new int[]{20,20}, dataset.getDataspace().getDimensionSizes());
                Object[][][] values;

                //read all
                values = (Object[][][]) dataset.read(null);
                assertEquals(20, values.length);
                assertEquals(20, values[19].length);
                assertArrayEquals(new Object[]{(byte)0, (byte)0}, values[0][0]);
                assertArrayEquals(new Object[]{(byte)1, (byte)0}, values[1][0]);
                assertArrayEquals(new Object[]{(byte)0, (byte)1}, values[0][1]);
                assertArrayEquals(new Object[]{(byte)19, (byte)0}, values[19][0]);
                assertArrayEquals(new Object[]{(byte)0, (byte)19}, values[0][19]);
                assertArrayEquals(new Object[]{(byte)19, (byte)19}, values[19][19]);

                //read part
                values = (Object[][][]) dataset.read(new GridExtent(null, new long[]{5,12}, new long[]{8,14}, false));
                assertArrayEquals(new Object[]{(byte)5, (byte)12}, values[0][0]);
                assertArrayEquals(new Object[]{(byte)6, (byte)12}, values[1][0]);
                assertArrayEquals(new Object[]{(byte)7, (byte)12}, values[2][0]);
                assertArrayEquals(new Object[]{(byte)5, (byte)13}, values[0][1]);
                assertArrayEquals(new Object[]{(byte)6, (byte)13}, values[1][1]);
                assertArrayEquals(new Object[]{(byte)7, (byte)13}, values[2][1]);
            }

            { // 3D floating point
                final Dataset dataset = (Dataset) store.getComponent("table_3D");
                assertTrue(dataset.getDataType() instanceof Compound);
                Compound cmpd = (Compound) dataset.getDataType();
                assertEquals(Object[].class, cmpd.getValueClass());
                assertEquals(3, cmpd.members.length);
                assertTrue(cmpd.members[0].memberType instanceof FixedPoint);
                assertTrue(cmpd.members[1].memberType instanceof FixedPoint);
                assertTrue(cmpd.members[2].memberType instanceof FixedPoint);
                assertEquals("k", cmpd.members[0].name);
                assertEquals("j", cmpd.members[1].name);
                assertEquals("i", cmpd.members[2].name);
                assertArrayEquals(new int[]{20,20,20}, dataset.getDataspace().getDimensionSizes());
                Object[][][][] values;

                //read all
                values = (Object[][][][]) dataset.read(null);
                assertEquals(20, values.length);
                assertEquals(20, values[19].length);
                assertEquals(20, values[10][19].length);
                for (int x = 0; x < values.length; x++) {
                    for (int y = 0; y < values.length; y++) {
                        for (int z = 0; z < values.length; z++) {
                            assertEquals((byte)x, values[x][y][z][0]);
                            assertEquals((byte)y, values[x][y][z][1]);
                            assertEquals((byte)z, values[x][y][z][2]);
                        }
                    }
                }

                //read part
                values = (Object[][][][]) dataset.read(new GridExtent(null, new long[]{5,12,17}, new long[]{8,14,19}, false));
                assertArrayEquals(new Object[]{(byte)5, (byte)12, (byte)17}, values[0][0][0]);
                assertArrayEquals(new Object[]{(byte)6, (byte)12, (byte)17}, values[1][0][0]);
                assertArrayEquals(new Object[]{(byte)7, (byte)12, (byte)17}, values[2][0][0]);
                assertArrayEquals(new Object[]{(byte)5, (byte)13, (byte)17}, values[0][1][0]);
                assertArrayEquals(new Object[]{(byte)6, (byte)13, (byte)17}, values[1][1][0]);
                assertArrayEquals(new Object[]{(byte)7, (byte)13, (byte)17}, values[2][1][0]);
                assertArrayEquals(new Object[]{(byte)5, (byte)12, (byte)18}, values[0][0][1]);
                assertArrayEquals(new Object[]{(byte)6, (byte)12, (byte)18}, values[1][0][1]);
                assertArrayEquals(new Object[]{(byte)7, (byte)12, (byte)18}, values[2][0][1]);
                assertArrayEquals(new Object[]{(byte)5, (byte)13, (byte)18}, values[0][1][1]);
                assertArrayEquals(new Object[]{(byte)6, (byte)13, (byte)18}, values[1][1][1]);
                assertArrayEquals(new Object[]{(byte)7, (byte)13, (byte)18}, values[2][1][1]);
            }

        }

    }

}
