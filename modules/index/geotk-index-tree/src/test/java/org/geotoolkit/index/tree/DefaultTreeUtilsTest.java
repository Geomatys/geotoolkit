/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.index.tree;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.junit.Assert;

/**
 * Test {@link DefaultTreeUtils} methods.
 *
 * @author Remi Marechal (Geomatys)
 */
public class DefaultTreeUtilsTest {

    private static final double EPSILON = 1E-12;
    
    public DefaultTreeUtilsTest() {
    }
    
    /**
     * Test minimum, maximum and median on envelope.
     */
    @Test
    public void getPointsTest() {
        final double[] expectedResult = new double[3];
        final double[] envelope  = new double[]{-3, 2, 0, 1, 4, 0};
        
        //lower corner
        System.arraycopy(envelope, 0, expectedResult, 0, 3);
        assertTrue(Arrays.equals(expectedResult, getLowerCorner(envelope)));
        
        //upper corner
        System.arraycopy(envelope, 3, expectedResult, 0, 3);
        assertTrue(Arrays.equals(expectedResult, getUpperCorner(envelope)));
        
        //median
        expectedResult[0] = -1;
        expectedResult[1] = 3;
        expectedResult[2] = 0;
        assertTrue(Arrays.equals(expectedResult, getMedian(envelope)));
    }
    
    /**
     * Test minimum, maximum and median on each envelope dimension.
     */
    @Test
    public void getValuesTest() {
        final double[] envelope  = new double[]{-3, 2, 0, 1, 4, 0};
        
        //mins
        assertTrue(envelope[0] == getMinimum(envelope, 0));
        assertTrue(envelope[1] == getMinimum(envelope, 1));
        assertTrue(envelope[2] == getMinimum(envelope, 2));
        
        //maxs
        assertTrue(envelope[3] == getMaximum(envelope, 0));
        assertTrue(envelope[4] == getMaximum(envelope, 1));
        assertTrue(envelope[5] == getMaximum(envelope, 2));
        
        //spans
        assertTrue(4 == getSpan(envelope, 0));
        assertTrue(2 == getSpan(envelope, 1));
        assertTrue(0 == getSpan(envelope, 2));
    }
    
    /**
     * Test contains.
     */
    @Test
    public void containsTest(){
        final double[] envelopeA  = new double[6];
        final double[] envelopeB  = new double[6];
        final double[] point  = new double[3];
        
        //touch
        envelopeA[0] = -4;envelopeA[3] = 3;
        envelopeA[1] = -6;envelopeA[4] = 2;
        envelopeA[2] = -3;envelopeA[5] = 1;
        
        envelopeB[0] = 0;envelopeB[3] = 3;
        envelopeB[1] = 1;envelopeB[4] = 2;
        envelopeB[2] = -2;envelopeB[5] = 1;
        
        assertTrue(contains(envelopeA, envelopeB, true));
        assertFalse(contains(envelopeA, envelopeB, false));
        
        //8th corners contains point test
        point[0] = -4; point[1] = -6; point[2] = -3;
        assertTrue(contains(envelopeA, point));
        point[0] = 3; point[1] = -6; point[2] = -3;
        assertTrue(contains(envelopeA, point));
        point[0] = 3; point[1] = 2; point[2] = -3;
        assertTrue(contains(envelopeA, point));
        point[0] = -4; point[1] = 2; point[2] = -3;
        assertTrue(contains(envelopeA, point));
        point[0] = -4; point[1] = -6; point[2] = 1;
        assertTrue(contains(envelopeA, point));
        point[0] = 3; point[1] = -6; point[2] = 1;
        assertTrue(contains(envelopeA, point));
        point[0] = 3; point[1] = 2; point[2] = 1;
        assertTrue(contains(envelopeA, point));
        point[0] = -4; point[1] = 2; point[2] = 1;
        assertTrue(contains(envelopeA, point));
        
        
        envelopeB[0] = 3;envelopeB[3] = 4;
        envelopeB[1] = 2;envelopeB[4] = 5;
        envelopeB[2] = 1;envelopeB[5] = 6;
        
        assertFalse(contains(envelopeA, envelopeB, true));
        assertFalse(contains(envelopeA, envelopeB, false));
    }
    
    /**
     * Test touches.
     */
    @Test
    public void touchesTest(){
        final double[] envelopeA  = new double[4];
        final double[] envelopeB  = new double[4];
        
        //touch 2D
        envelopeA[0] = -4; envelopeA[2] = 3;
        envelopeA[1] = -6; envelopeA[3] = 2;
        
        envelopeB[0] = 3; envelopeB[2] = 10;
        envelopeB[1] = 1; envelopeB[3] = 4;
        
        assertTrue(intersects(envelopeA, envelopeB, true));
        assertFalse(intersects(envelopeA, envelopeB, false));
        assertTrue(touches(envelopeA, envelopeB));
        
        envelopeB[0] = 1; envelopeB[2] = 1;
        envelopeB[1] = 0; envelopeB[3] = 5;
        assertFalse(touches(envelopeA, envelopeB));
        
        envelopeB[0] = 3; envelopeB[2] = 3;
        envelopeB[1] = 0; envelopeB[3] = 5;
        assertTrue(touches(envelopeA, envelopeB));
        
        envelopeB[0] = -4; envelopeB[2] = -4;
        envelopeB[1] = 0; envelopeB[3] = 5;
        assertTrue(touches(envelopeA, envelopeB));
        
        envelopeB[0] = -5; envelopeB[2] = 4;
        envelopeB[1] = -6; envelopeB[3] = -6;
        assertTrue(touches(envelopeA, envelopeB));
        
        envelopeB[0] = -5; envelopeB[2] = 4;
        envelopeB[1] = 2; envelopeB[3] = 2;
        assertTrue(touches(envelopeA, envelopeB));
        
        envelopeB[0] = -5; envelopeB[2] = 4;
        envelopeB[1] = 1; envelopeB[3] = 1;
        assertFalse(touches(envelopeA, envelopeB));
    }
    
    /**
     * Test intersection.
     */
    @Test
    public void intersectsTest(){
        final double[] envelopeA  = new double[6];
        final double[] envelopeB  = new double[6];
        
        //touch 
        envelopeA[0] = -4; envelopeA[3] = 3;
        envelopeA[1] = -6; envelopeA[4] = 2;
        envelopeA[2] = -3; envelopeA[5] = 1;
        
        envelopeB[0] = 3;  envelopeB[3] = 4;
        envelopeB[1] = -6; envelopeB[4] = 5;
        envelopeB[2] = -3; envelopeB[5] = 1;
        
        assertTrue(intersects(envelopeA, envelopeB, true));
        assertFalse(intersects(envelopeA, envelopeB, false));
        
        //on 8th corners
        envelopeB[0] = -5;  envelopeB[3] = -4;
        envelopeB[1] = -7; envelopeB[4] = -6;
        envelopeB[2] = -4; envelopeB[5] = -3;
        assertTrue(intersects(envelopeA, envelopeB, true));
        assertFalse(intersects(envelopeA, envelopeB, false));
        
        envelopeB[0] = 3;  envelopeB[3] = 5;
        envelopeB[1] = -7; envelopeB[4] = -6;
        envelopeB[2] = -4; envelopeB[5] = -3;
        assertTrue(intersects(envelopeA, envelopeB, true));
        assertFalse(intersects(envelopeA, envelopeB, false));
        
        envelopeB[0] = 3;  envelopeB[3] = 5;
        envelopeB[1] = 2; envelopeB[4] = 7;
        envelopeB[2] = -4; envelopeB[5] = -3;
        assertTrue(intersects(envelopeA, envelopeB, true));
        assertFalse(intersects(envelopeA, envelopeB, false));
        
        envelopeB[0] = -5;  envelopeB[3] = -4;
        envelopeB[1] = 2; envelopeB[4] = 4;
        envelopeB[2] = -4; envelopeB[5] = -3;
        assertTrue(intersects(envelopeA, envelopeB, true));
        assertFalse(intersects(envelopeA, envelopeB, false));
        
        envelopeB[0] = -5;  envelopeB[3] = -4;
        envelopeB[1] = -7; envelopeB[4] = -6;
        envelopeB[2] = 1; envelopeB[5] = 3;
        assertTrue(intersects(envelopeA, envelopeB, true));
        assertFalse(intersects(envelopeA, envelopeB, false));
        
        envelopeB[0] = 3;  envelopeB[3] = 5;
        envelopeB[1] = -7; envelopeB[4] = -6;
        envelopeB[2] = 1; envelopeB[5] = 3;
        assertTrue(intersects(envelopeA, envelopeB, true));
        assertFalse(intersects(envelopeA, envelopeB, false));
        
        envelopeB[0] = 3;  envelopeB[3] = 5;
        envelopeB[1] = 2; envelopeB[4] = 7;
        envelopeB[2] = 1; envelopeB[5] = 3;
        assertTrue(intersects(envelopeA, envelopeB, true));
        assertFalse(intersects(envelopeA, envelopeB, false));
        
        envelopeB[0] = -5;  envelopeB[3] = -4;
        envelopeB[1] = 2; envelopeB[4] = 4;
        envelopeB[2] = 1; envelopeB[5] = 3;
        assertTrue(intersects(envelopeA, envelopeB, true));
        assertFalse(intersects(envelopeA, envelopeB, false));
        
        //contains
        envelopeB[0] = -2;  envelopeB[3] = 1;
        envelopeB[1] = -5; envelopeB[4] = 0;
        envelopeB[2] = -2; envelopeB[5] = -1;
        assertTrue(intersects(envelopeA, envelopeB, true));
        assertTrue(intersects(envelopeA, envelopeB, false));
        
        //out 
        envelopeB[0] = -10;  envelopeB[3] = -9;
        envelopeB[1] = 50; envelopeB[4] = 70;
        envelopeB[2] = -100; envelopeB[5] = 100;
        assertFalse(intersects(envelopeA, envelopeB, true));
        assertFalse(intersects(envelopeA, envelopeB, false));
    }
    
    /**
     * Test union.
     */
    @Test
    public void addTest() {
        final double[] envelopeA  = new double[6];
        final double[] envelopeB  = new double[6];
        
        //add same
        envelopeA[0] = -10; envelopeA[3] = 9;
        envelopeA[1] = 2;   envelopeA[4] = 7;
        envelopeA[2] = -15; envelopeA[5] = -5;
        final double[] result  = envelopeA.clone();
        assertTrue(Arrays.equals(result, add(envelopeA, envelopeA.clone())));
        
        System.arraycopy(result, 0, envelopeA, 0, result.length);
        //add lesser
        envelopeB[0] = -7;  envelopeB[3] = 2;
        envelopeB[1] = 4;   envelopeB[4] = 6;
        envelopeB[2] = -12; envelopeB[5] = -7;
        assertTrue(Arrays.equals(result, add(envelopeA, envelopeB)));
        
        //add bigger
        //add lesser
        envelopeB[0] = -17; envelopeB[3] = 29;
        envelopeB[1] = -3;  envelopeB[4] = 10;
        envelopeB[2] = -21; envelopeB[5] = -1;
        assertTrue(Arrays.equals(envelopeB, add(envelopeA, envelopeB)));
        
        //add distincts
        System.arraycopy(result, 0, envelopeA, 0, result.length);
        envelopeB[0] = -25; envelopeB[3] = -17;
        envelopeB[1] = 75;  envelopeB[4] = 82;
        envelopeB[2] = -125; envelopeB[5] = -42;
        assertTrue(Arrays.equals(new double[]{-25, 2, -125, 9, 82, -5}, add(envelopeA, envelopeB)));
    }
    
    /**
     * Test distance computing between point -> point or boundary -> point or boundary -> boundary.
     */
    @Test
    public void getDistancesTest() {
        final double[] pointA  = new double[3];
        final double[] pointB  = new double[3];
        double result;
        
        //points
        //A->B
        pointA[0] = -4; pointA[1] = -6; pointA[2] = -3;
        pointB[0] = 2; pointB[1] = 3; pointB[2] = 4;
        result = Math.sqrt(166);
        Assert.assertEquals("getDistanceBetween2Envelopes : A -> B", result, getDistanceBetween2Positions(pointA, pointB), EPSILON);
        //b->A
        Assert.assertEquals("getDistanceBetween2Envelopes : B -> A", result, getDistanceBetween2Positions(pointB, pointA), EPSILON);
        
        final double[] envelopeA  = new double[6];
        final double[] envelopeB  = new double[6];
        
        //envelopes
        //A->B
        envelopeA[0] = -6; envelopeA[3] = -2;
        envelopeA[1] = -12;  envelopeA[4] = 0;
        envelopeA[2] = -3.5; envelopeA[5] = -2.5;
        
        envelopeB[0] = 1; envelopeB[3] = 3;
        envelopeB[1] = 0;  envelopeB[4] = 6;
        envelopeB[2] = 3.5; envelopeB[5] = 4.5;
        Assert.assertEquals("getDistanceBetween2Envelopes : A -> B", result, getDistanceBetween2Envelopes(envelopeA, envelopeB), EPSILON);
        //b->A
        Assert.assertEquals("getDistanceBetween2Envelopes : B -> A", result, getDistanceBetween2Envelopes(envelopeB, envelopeA), EPSILON);
    }
    
    /**
     * Test bulk, edge and perimeter computing.
     */
    @Test
    public void computeSpaceTest() {
        //2D test
        double[] envelopeA = new double[4];
        double[] envelopeB = new double[4];
        
        envelopeA[0] = -6; envelopeA[2] = 2;
        envelopeA[1] = -2;  envelopeA[3] = 7;
        
        //edge
        Assert.assertEquals("computeSpaceTest : get edge (perimeter)", 34, getPerimeter(envelopeA), EPSILON);
        
        //area 
        Assert.assertEquals("computeSpaceTest : get area", 72, getArea(envelopeA), EPSILON);
        
        //bulk
        try {
            getBulk(envelopeA);
            Assert.fail("test should had fail");
        } catch (Exception ex) {
            //ok
        }
        
        //overlaps
        envelopeB[0] = 0; envelopeB[2] = 4;
        envelopeB[1] = 3;  envelopeB[3] = 10;
        Assert.assertEquals("computeSpaceTest : getOverlaps", 8, getOverlapValue(envelopeA, envelopeB), EPSILON);
        Assert.assertEquals("computeSpaceTest : getOverlaps", 8, getOverlapValue(envelopeB, envelopeA), EPSILON);
        assertEquals("computeSpaceTest : getOverlaps with intersection", 8, getArea(intersect(envelopeA, envelopeB)), EPSILON);
        
        //3D+ test
        envelopeA = new double[6];
        envelopeB = new double[6];
        
        envelopeA[0] = -6; envelopeA[3] = 2;
        envelopeA[1] = -2;  envelopeA[4] = 7;
        envelopeA[2] = -9;  envelopeA[5] = 5;
        
        //area 
        Assert.assertEquals("computeSpaceTest : get area", 620, getArea(envelopeA), EPSILON);
        
        //bulk
        Assert.assertEquals("computeSpaceTest : get bulk", 1008, getBulk(envelopeA), EPSILON);
        
        //overlaps
        envelopeB[0] = 0;  envelopeB[3] = 4;
        envelopeB[1] = 3;  envelopeB[4] = 10;
        envelopeB[2] = -2;  envelopeB[5] = 3;
        Assert.assertEquals("computeSpaceTest : getOverlaps", 40, getOverlapValue(envelopeA, envelopeB), EPSILON);
        Assert.assertEquals("computeSpaceTest : getOverlaps", 40, getOverlapValue(envelopeB, envelopeA), EPSILON);
        assertEquals("computeSpaceTest : getOverlaps with intersection", 40, getBulk(intersect(envelopeA, envelopeB)), EPSILON);
    }
}
