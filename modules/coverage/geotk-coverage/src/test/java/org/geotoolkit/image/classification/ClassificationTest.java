/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.classification;

import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test Classification class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class ClassificationTest {

    /**
     * Tested classification object.
     */
    private Classification classification;

    /**
     * Verified results.
     */
    List<double[]> result;

    /**
     * Data table which is classified.
     */
    double[] data ;


    public ClassificationTest() {
    }

    /**
     * Test about Quantile classification.
     * Test with odd table value and 1 class.
     */
    @Test
    public void quantileI1Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       classification = new Classification(data, 1);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 1);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8, 12, 14, 17, 21, 33, 35.5, 47, 56, 58}));
    }

    /**
     * Test about Quantile classification.
     * Test with odd table value and 2 class.
     */
    @Test
    public void quantileI2Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       classification = new Classification(data, 2);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 2);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8, 12, 14, 17}));
       assertTrue(compareTab(result.get(1), new double[]{21, 33, 35.5, 47, 56, 58}));
    }

    /**
     * Test about Quantile classification.
     * Test with odd table value and 3 class.
     */
    @Test
    public void quantileI3Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       classification = new Classification(data, 3);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 3);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8}));
       assertTrue(compareTab(result.get(1), new double[]{12, 14, 17,21, 33}));
       assertTrue(compareTab(result.get(2), new double[]{35.5, 47, 56, 58}));

    }

    /**
     * Test about Quantile classification.
     * Test with odd table value and 4 class.
     */
    @Test
    public void quantileI4Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       classification = new Classification(data, 4);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 4);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7}));
       assertTrue(compareTab(result.get(1), new double[]{8, 12, 14, 17}));
       assertTrue(compareTab(result.get(2), new double[]{21, 33, 35.5}));
       assertTrue(compareTab(result.get(3), new double[]{47, 56, 58}));
    }

    /**
     * Test about Quantile classification.
     * Test with even table value and 2 class.
     */
    @Test
    public void quantileP2Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2};
       classification = new Classification(data, 2);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 2);
       assertTrue(compareTab(result.get(0), new double[]{2, 3.2, 5, 7, 8, 12, 14}));
       assertTrue(compareTab(result.get(1), new double[]{17, 21, 33, 35.5, 47, 56, 58}));
    }

    /**
     * Test about Quantile classification.
     * Test with even table value and 3 class.
     */
    @Test
    public void quantileP3Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2};
       classification = new Classification(data, 3);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 3);
       assertTrue(compareTab(result.get(0), new double[]{2, 3.2, 5, 7, 8 }));
       assertTrue(compareTab(result.get(1), new double[]{12, 14, 17, 21}));
       assertTrue(compareTab(result.get(2), new double[]{33, 35.5, 47, 56, 58}));
    }

    /**
     * Test about Quantile classification.
     * Test with even table value and 4 class.
     */
    @Test
    public void quantileP4Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2};
       classification = new Classification(data, 4);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 4);
       assertTrue(compareTab(result.get(0), new double[]{2, 3.2, 5, 7}));
       assertTrue(compareTab(result.get(1), new double[]{8, 12, 14}));
       assertTrue(compareTab(result.get(2), new double[]{17, 21, 33, 35.5}));
       assertTrue(compareTab(result.get(3), new double[]{47, 56, 58}));
    }

    /**
     * Test about Jenks classification.
     * Test with odd table value and 1 class.
     */
    @Test
    public void jenksI1Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       classification = new Classification(data, 1);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 1);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8, 12, 14, 17, 21, 33, 35.5, 47, 56, 58}));
    }

    /**
     * Test about Jenks classification.
     * Test with odd table value and 2 class.
     */
    @Test
    public void jenksI2Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       classification = new Classification(data, 2);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 2);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8, 12, 14, 17, 21, 33, 35.5}));
       assertTrue(compareTab(result.get(1), new double[]{ 47, 56, 58}));
    }

    /**
     * Test about Jenks classification.
     * Test with odd table value and 3 class.
     */
    @Test
    public void jenksI3Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       classification = new Classification(data, 3);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 3);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8, 12, 14, 17,21}));
       assertTrue(compareTab(result.get(1), new double[]{33, 35.5}));
       assertTrue(compareTab(result.get(2), new double[]{47, 56, 58}));

    }

    /**
     * Test about Jenks classification.
     * Test with odd table value and 4 class.
     */
    @Test
    public void jenksI4Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       classification = new Classification(data, 4);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 4);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8}));
       assertTrue(compareTab(result.get(1), new double[]{12, 14, 17, 21}));
       assertTrue(compareTab(result.get(2), new double[]{33, 35.5}));
       assertTrue(compareTab(result.get(3), new double[]{47, 56, 58}));
    }


    /**
     * Test about Jenks classification.
     * Test with even table value and 2 class.
     */
    @Test
    public void jenksP2Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2};
       classification = new Classification(data, 2);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 2);
       assertTrue(compareTab(result.get(0), new double[]{2, 3.2, 5, 7, 8, 12, 14, 17, 21, 33, 35.5}));
       assertTrue(compareTab(result.get(1), new double[]{47, 56, 58}));
    }

    /**
     * Test about Jenks classification.
     * Test with even table value and 3 class.
     */
    @Test
    public void jenksP3Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2};
       classification = new Classification(data, 3);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 3);
       assertTrue(compareTab(result.get(0), new double[]{2, 3.2, 5, 7, 8, 12, 14, 17, 21}));
       assertTrue(compareTab(result.get(1), new double[]{33, 35.5}));
       assertTrue(compareTab(result.get(2), new double[]{47, 56, 58}));
    }

    /**
     * Test about Jenks classification.
     * Test with even table value and 4 class.
     */
    @Test
    public void jenksP4Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2};
       classification = new Classification(data, 4);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 4);
       assertTrue(compareTab(result.get(0), new double[]{2, 3.2, 5, 7, 8}));
       assertTrue(compareTab(result.get(1), new double[]{12, 14, 17, 21}));
       assertTrue(compareTab(result.get(2), new double[]{33, 35.5}));
       assertTrue(compareTab(result.get(3), new double[]{47, 56, 58}));
    }

    /**
     * Compare 2 double tables.
     *
     * @param tabA table resulting raster iterate.
     * @param tabB table resulting raster iterate.
     * @return true if tables are identical.
     */
    private boolean compareTab(double[] tabA, double[] tabB) {
        int length = tabA.length;
        if (length != tabB.length) return false;
        for (int i = 0; i<length; i++) {
            if (tabA[i] != tabB[i]) return false;
        }
        return true;
    }

    /**
     * Test Classification variance method.
     */
    @Test
    public void varianceTest() {
        classification = new Classification(new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47}, 2);
        double var = classification.getVariance(new double[]{1, 2, 3});
        assertTrue(Math.abs(var - 0.6666666666) <= 1E-9);
        var = classification.getVariance(new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2});
        assertTrue(Math.abs(var - 358.96515306122) <= 1E-9);
    }
}
