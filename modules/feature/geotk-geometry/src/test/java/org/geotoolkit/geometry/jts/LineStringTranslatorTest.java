/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.geometry.jts;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import static org.junit.Assert.*;
import org.junit.Test;
import static java.lang.StrictMath.*;


/**
 * Test translation on {@link LineString} in different cases.
 *
 * @author Remi Marechal (Geomatys).
 */
public class LineStringTranslatorTest {
    
    /**
     * Accepted test tolerance.
     */
    private static double TOLERANCE = 1E-9;
    
    /**
     * A static value use to create offset.
     */
    private static double SQRT_2 = sqrt(2.0);
    
    /**
     * Default factory to create lineString.
     */
    private static GeometryFactory GEOM_FACTORY = new GeometryFactory();
    
    /**
     * Create {@link LineString} from coordinates given parameters.
     * 
     * coords is ordonnanced like : point0x, point0y, ... pointnx, pointny.
     * 
     * @param coords coordinates array.
     * @return {@link LineString} create from given points coordinates.
     */
    public LineString createLineString(double ...coords) {
        if (coords.length % 2 != 0)
            throw new IllegalArgumentException("LinStringCreation : coordinates number must be modulo 2.");
        final int coordsLength = coords.length >>> 1;
        final Coordinate[] lineCoords = new Coordinate[coordsLength];
        int c = 0;
        for (int lc = 0; lc < coordsLength; lc++) {
            lineCoords[lc] = new Coordinate(coords[c++], coords[c++]);
        }
        return GEOM_FACTORY.createLineString(lineCoords);
    }
    
    /**
     * Test translation on {@link LineString} composed by only one segment.
     */
    @Test
    public void oneLineTest() {
       /* 
        * B
        * |  
        * A  
        */
        LineString line = createLineString(2, 1, 2, 4);
        
        /*
         * Translate on the right of the segment.
         */
        LineString resultLineString = LineStringTranslator.translateLineString(line, 3);
        Coordinate[] expectedCoords = new Coordinate[]{new Coordinate(5, 1), new Coordinate(5, 4)};
        
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        /*
         * Translate on the left of the segment.
         */
        resultLineString = LineStringTranslator.translateLineString(line, -5);
        expectedCoords = new Coordinate[]{new Coordinate(-3, 1), new Coordinate(-3, 4)};
        
        /*
         *    B
         *   /
         *  A
         */
        line = createLineString(2, 1, 4, 3);
        /*
         * Translate on the right of the segment.
         */
        resultLineString = LineStringTranslator.translateLineString(line, 3 * SQRT_2);
        
        expectedCoords = new Coordinate[]{new Coordinate(5, -2), new Coordinate(7, 0)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        /*
         * Translate on the left of the segment.
         */
        resultLineString = LineStringTranslator.translateLineString(line, -5 * SQRT_2);
        
        expectedCoords = new Coordinate[]{new Coordinate(-3, 6), new Coordinate(-1, 8)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        /*
         *  A ____ B
         */
        line = createLineString(2, 1, 4, 1);
        resultLineString = LineStringTranslator.translateLineString(line, 3);
        /*
         * Translate on the right of the segment.
         */
        expectedCoords = new Coordinate[]{new Coordinate(2, -2), new Coordinate(4, -2)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        
        resultLineString = LineStringTranslator.translateLineString(line, -7);
        /*
         * Translate on the left of the segment.
         */
        expectedCoords = new Coordinate[]{new Coordinate(2, 8), new Coordinate(4, 8)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        /*
         *  A 
         *   \
         *    \
         *     B
         */
        line = createLineString(-1, 3, 6, -4);
        resultLineString = LineStringTranslator.translateLineString(line, 7 * SQRT_2);
        /*
         * Translate on the right of the segment.
         */
        expectedCoords = new Coordinate[]{new Coordinate(-8, -4), new Coordinate(-1, -11)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        
        resultLineString = LineStringTranslator.translateLineString(line, -7 * SQRT_2);
        /*
         * Translate on the left of the segment.
         */
        expectedCoords = new Coordinate[]{new Coordinate(6, 10), new Coordinate(13, 3)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
       /* 
        * A
        * |  
        * B  
        */
        line = createLineString(-1, 3, -1, -4);
        resultLineString = LineStringTranslator.translateLineString(line, 2);
        /*
         * Translate on the right of the segment.
         */
        expectedCoords = new Coordinate[]{new Coordinate(-3, 3), new Coordinate(-3, -4)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        
        resultLineString = LineStringTranslator.translateLineString(line, -5);
        /*
         * Translate on the left of the segment.
         */
        expectedCoords = new Coordinate[]{new Coordinate(4, 3), new Coordinate(4, -4)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        /*
         *       A
         *      /
         *     /
         *    B
         */
        line = createLineString(1, 3, -4, -2);
        resultLineString = LineStringTranslator.translateLineString(line, 5 * SQRT_2);
        /*
         * Translate on the right of the segment.
         */
        expectedCoords = new Coordinate[]{new Coordinate(-4, 8), new Coordinate(-9, 3)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        
        resultLineString = LineStringTranslator.translateLineString(line, -5 * SQRT_2);
        /*
         * Translate on the left of the segment.
         */
        expectedCoords = new Coordinate[]{new Coordinate(6, -2), new Coordinate(1, -7)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        /*
         *  B ____ A
         */
        line = createLineString(4, 3, -10, 3);
        resultLineString = LineStringTranslator.translateLineString(line, 2);
        /*
         * Translate on the right of the segment.
         */
        expectedCoords = new Coordinate[]{new Coordinate(4, 5), new Coordinate(-10, 5)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        
        resultLineString = LineStringTranslator.translateLineString(line, -5);
        /*
         * Translate on the left of the segment.
         */
        expectedCoords = new Coordinate[]{new Coordinate(4, -2), new Coordinate(-10, -2)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        /*
         *  B
         *   \
         *    \
         *     A
         */
        line = createLineString(7, -3, -6, 10);
        resultLineString = LineStringTranslator.translateLineString(line, 9 * SQRT_2);
        /* 
         * Translate on the right of the segment.
         */
        expectedCoords = new Coordinate[]{new Coordinate(16, 6), new Coordinate(3, 19)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        
        resultLineString = LineStringTranslator.translateLineString(line, -5 * SQRT_2);
        /*
         * Translate on the left of the segment.
         */
        expectedCoords = new Coordinate[]{new Coordinate(2, -8), new Coordinate(-11, 5)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
    }
    
    /**
     * Test translation on {@link LineString} composed by several segments.
     */
    @Test
    public void testMultiLineTest() {
        /*
         * B_______C
         * |       |
         * |       |
         * |       |
         * A_______D
         * 
         * Anti trigonometric sens
         * 
         */
        LineString line = createLineString(-2, -2, -2, 3, 5, 3, 5, -2, -2, -2);
        
        /*
         * Translate on the right of the segment.(square internal)
         */
        LineString resultLineString = LineStringTranslator.translateLineString(line, 1.5);
        Coordinate[] expectedCoords = new Coordinate[]{new Coordinate(-0.5, -0.5), new Coordinate(-0.5, 1.5),
                                                       new Coordinate(3.5, 1.5),   new Coordinate(3.5, -0.5),
                                                       new Coordinate(-0.5, -0.5)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        /*
         * Translate on the left of the segment.(square external)
         */
        resultLineString = LineStringTranslator.translateLineString(line, -7.2);
        expectedCoords = new Coordinate[]{new Coordinate(-9.2, -9.2), new Coordinate(-9.2, 10.2),
                                          new Coordinate(12.2, 10.2),   new Coordinate(12.2, -9.2),
                                          new Coordinate(-9.2, -9.2)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        /*
         *       D
         *      / \
         *     /   \
         *  A /     \ 
         *    \     / C
         *     \   /
         *      \ /
         *       B
         * 
         * Trigonometric sens
         */ 
        line = createLineString(-1, -1, 3, -5,  7, -1, 3, 3, -1, -1);
        
        /*
         * Translate on the right of the segment.(square internal)
         */
        resultLineString = LineStringTranslator.translateLineString(line, - SQRT_2);
        expectedCoords   = new Coordinate[]{new Coordinate(1, -1), new Coordinate(3, -3),
                                            new Coordinate(5, -1), new Coordinate(3, 1),
                                            new Coordinate(1, -1)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        /*
         * Translate on the left of the segment.(square external)
         */
        resultLineString = LineStringTranslator.translateLineString(line, 3.3 * SQRT_2);
        expectedCoords = new Coordinate[]{new Coordinate(-7.6, -1), new Coordinate(3, -11.6),
                                          new Coordinate(13.6, -1), new Coordinate(3, 9.6),
                                          new Coordinate(-7.6, -1)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        /*
         * B____C_____D     F          K
         * |          \    /|         /
         * |           \  / |        /
         * |            \/  G       J
         * A             E  |      /
         *                  |_____/
         *                  H     I
         * 
         * None closed form.
         */
        line = createLineString(-2, -3, -2, 5,  1, 5, 4, 5, 6, 1, 9, 5, 9, 2, 9, -1, 12, -1, 16, 0, 20, 1);
        
        /*
         * Translate on the right of the segment.
         */
        resultLineString = LineStringTranslator.translateLineString(line, 1);
        expectedCoords   = new Coordinate[]{new Coordinate(-1, -3), new Coordinate(-1, 4),
                                            new Coordinate(1, 4), new Coordinate(3.381966011250105, 4),
                                            new Coordinate(5.829179606750063, -0.8944271909999157), new Coordinate(8 , 2), 
                                            new Coordinate(8,2), new Coordinate(8, -2),
                                            new Coordinate(12.12310562561766, -2), new Coordinate(16.242535625036332, -0.9701425001453319),
                                            new Coordinate(20.242535625036332, 0.029857499854668124)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
        /*
         * Translate on the left of the segment.(square external)
         */
        resultLineString = LineStringTranslator.translateLineString(line, -2);
        expectedCoords   = new Coordinate[]{new Coordinate(-4, -3), new Coordinate(-4, 7),
                                            new Coordinate(1, 7), new Coordinate(5.23606797749979, 7),
                                            new Coordinate(6.341640786499873, 4.7888543819998315), new Coordinate(11, 11), 
                                            new Coordinate(11,2), new Coordinate(11, 1),
                                            new Coordinate(11.753788748764679, 1), new Coordinate(15.514928749927334, 1.9402850002906638),
                                            new Coordinate(19.514928749927336, 2.9402850002906638)};
        
        assertArrayEquals(expectedCoords, resultLineString.getCoordinates());
        
    }
    
    /**
     * Verify that each {@link Coordinate} from each array are respectively equals.
     * 
     * @param expected array which contains expected {@link Coordinate}s.
     * @param tested array which will be compare.
     */
    private void assertArrayEquals(Coordinate[] expected, Coordinate[] tested) {
        final int expectedLength = expected.length;
        assertEquals("Different array lenght : Expected : "+expected.length+". Found : "+tested.length, expectedLength, tested.length);
        for (int c = 0; c < expectedLength; c++) {
            assertEquals("at coordinate "+c+" expected x = "+expected[c].x+" found : "+tested[c].x, expected[c].x, tested[c].x, TOLERANCE);
            assertEquals("at coordinate "+c+" expected y = "+expected[c].y+" found : "+tested[c].y, expected[c].y, tested[c].y, TOLERANCE);
        }
    }
} 
