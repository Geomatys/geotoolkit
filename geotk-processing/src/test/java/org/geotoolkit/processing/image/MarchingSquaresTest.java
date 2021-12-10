/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.processing.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import org.apache.sis.image.PixelIterator;
import org.geotoolkit.image.BufferedImages;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MarchingSquaresTest {

    private static final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();

    @Test
    public void test2x2() {
        Geometry geom;

        { //case 0 & 15
            geom = createImage(0, 0, 0, 0, 2);
            Assert.assertNull(geom);

            geom = createImage(0, 0, 0, 0, -5);
            Assert.assertNull(geom);
        }

        { //case 1 & 14
            /*
            0--0
            |  |
            5--1
            */
            geom = createImage(5, 1, 0, 0, 2);
            Assert.assertEquals(mline(0, 0.6, 0.75, 0), geom);

            /*
            10--20
            |   |
            0---5
            */
            geom = createImage(0, 5, 10, 20, 2);
            Assert.assertEquals(mline(0, 0.2, 0.4, 0), geom);
        }

        { //case 2 & 13
            /*
            0--0
            |  |
            1--5
            */
            geom = createImage(1, 5, 0, 0, 2);
            Assert.assertEquals(mline(1, 0.6, 0.25, 0), geom);

            /*
            20--10
            |   |
            5---0
            */
            geom = createImage(5, 0, 20, 10, 2);
            Assert.assertEquals(mline(1, 0.2, 0.6, 0), geom);
        }

        { //case 3 & 12
            /*
            5--10
            |  |
            1--0
            */
            geom = createImage(1, 0, 5, 10, 2);
            Assert.assertEquals(mline(0, 0.25, 1, 0.2), geom);

            /*
            1--0
            |  |
            5--10
            */
            geom = createImage(5, 10, 1, 0, 2);
            Assert.assertEquals(mline(0, 0.75, 1, 0.8), geom);
        }

        { //case 4 & 11
            /*
            1--5
            |  |
            0--0
            */
            geom = createImage(0, 0, 1, 5, 2);
            Assert.assertEquals(mline(1, 0.4, 0.25, 1), geom);

            /*
            5---0
            |   |
            20--10
            */
            geom = createImage(20, 10, 5, 0, 2);
            Assert.assertEquals(mline(1, 0.8, 0.6, 1), geom);
        }

        { //case 5 & 10
            /*
            5--1
            |  |
            1--11
            */
            geom = createImage(1, 11, 5, 1, 2);
            Assert.assertEquals(mline(1, 0.9, 0.75, 1,    0, 0.25, 0.1, 0), geom);

            /*
            1---5
            |   |
            11--0
            */
            geom = createImage(11, 1, 1, 5, 2);
            Assert.assertEquals(mline(1, 0.25, 0.9, 0,    0, 0.9, 0.25, 1), geom);
        }

        { //case 6 & 9
            /*
            10--0
            |  |
            5--1
            */
            geom = createImage(5, 1, 10, 0, 2);
            Assert.assertEquals(mline(0.75, 0, 0.8, 1), geom);

            /*
            0--10
            |  |
            1--5
            */
            geom = createImage(1, 5, 0, 10, 2);
            Assert.assertEquals(mline(0.25, 0, 0.2, 1), geom);
        }

        { //case 7 & 8
            /*
            5--1
            |  |
            0--0
            */
            geom = createImage(0, 0, 5, 1, 2);
            Assert.assertEquals(mline(0, 0.4, 0.75, 1), geom);

            /*
            0---5
            |   |
            10--20
            */
            geom = createImage(10, 20, 0, 5, 2);
            Assert.assertEquals(mline(0, 0.8, 0.4, 1), geom);
        }
    }

    /**
     * Test created geometry is closed.
     */
    @Test
    public void testClosedSquare() {
        final double[][] array = new double[][]{
            {0,0,0},
            {0,1,0},
            {0,0,0}
        };
        final MultiLineString multiline = createImage(array, 0.5, true);
        Assert.assertEquals(1, multiline.getNumGeometries());
        final LineString line = (LineString) multiline.getGeometryN(0);

        final LineString expected = GF.createLineString(new PackedCoordinateSequence.Double(new double[]{0.5,1, 1,1.5, 1.5,1, 1,0.5, 0.5,1}, 2, 0));
        Assert.assertTrue(expected.equalsExact(line));
    }

    @Test
    public void testNaN() {
        Geometry geom;

        { //case 1
            /*
            0--0
            |  |
            5--NaN
            */
            geom = createImage(5, Double.NaN, 0, 0, 2);
            Assert.assertNull(geom);
        }
    }

    private static MultiLineString createImage(double x0y0, double x1y0, double x0y1, double x1y1, double threshold) {
        return createImage(new double[][] {
            {x0y0, x1y0},
            {x0y1, x1y1},
        }, threshold, false);
    }

    private static MultiLineString createImage(double[][] array, double threshold, boolean mergeLines) {
        final BufferedImage image = BufferedImages.createImage(array[0].length, array.length, 1, DataBuffer.TYPE_DOUBLE);
        final WritableRaster raster = image.getRaster();
        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array[0].length; x++) {
                raster.setSample(x, y, 0, array[y][x]);
            }
        }
        final PixelIterator ite = PixelIterator.create(image);
        return MarchingSquares.build(ite, threshold, 0, mergeLines);
    }

    private static LineString line(double x0, double y0, double x1, double y1) {
        return GF.createLineString(new Coordinate[]{
           new Coordinate(x0, y0),
            new Coordinate(x1, y1),
        });
    }

    private static MultiLineString mline(double x0, double y0, double x1, double y1) {
        return GF.createMultiLineString(new LineString[]{
            line(x0,y0,x1,y1)
        });
    }

    private static MultiLineString mline(double x0, double y0, double x1, double y1,
                                         double x2, double y2, double x3, double y3) {
        return GF.createMultiLineString(new LineString[]{
            line(x0,y0,x1,y1),
            line(x2,y2,x3,y3)
        });
    }

}
