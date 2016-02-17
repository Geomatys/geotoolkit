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

import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Classification class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class ClassificationTest extends org.geotoolkit.test.TestBase {

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

    /**
     * Index table.
     */
    int[] index;

    public ClassificationTest() {
        classification = new Classification();
    }

    /**
     * Test about Quantile classification.
     * Test with odd number table value and 1 class.
     */
    @Test
    public void quantileI1Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(1);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 1);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8, 12, 14, 17, 21, 33, 35.5, 47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 1);
       assertTrue(index[0] == 13);
    }

    /**
     * Test about Quantile classification.
     * Test with odd number table value and 2 class.
     */
    @Test
    public void quantileI2Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(2);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 2);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8, 12, 14, 17}));
       assertTrue(compareTab(result.get(1), new double[]{21, 33, 35.5, 47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 2);
       assertTrue(index[0] == 7);
       assertTrue(index[1] == 13);
    }

    /**
     * Test about Quantile classification.
     * Test with odd number table value and 3 class.
     */
    @Test
    public void quantileI3Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(3);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 3);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8}));
       assertTrue(compareTab(result.get(1), new double[]{12, 14, 17,21, 33}));
       assertTrue(compareTab(result.get(2), new double[]{35.5, 47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 3);
       assertTrue(index[0] == 4);
       assertTrue(index[1] == 9);
       assertTrue(index[2] == 13);

    }

    /**
     * Test about Quantile classification.
     * Test with odd number table value and 4 class.
     */
    @Test
    public void quantileI4Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(4);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 4);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7}));
       assertTrue(compareTab(result.get(1), new double[]{8, 12, 14, 17}));
       assertTrue(compareTab(result.get(2), new double[]{21, 33, 35.5}));
       assertTrue(compareTab(result.get(3), new double[]{47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 4);
       assertTrue(index[0] == 3);
       assertTrue(index[1] == 7);
       assertTrue(index[2] == 10);
       assertTrue(index[3] == 13);
    }

    /**
     * Test about Quantile classification.
     * Test with even number table value and 2 class.
     */
    @Test
    public void quantileP2Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(2);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 2);
       assertTrue(compareTab(result.get(0), new double[]{2, 3.2, 5, 7, 8, 12, 14}));
       assertTrue(compareTab(result.get(1), new double[]{17, 21, 33, 35.5, 47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 2);
       assertTrue(index[0] == 7);
       assertTrue(index[1] == 14);
    }

    /**
     * Test about Quantile classification.
     * Test with even number table value and 3 class.
     */
    @Test
    public void quantileP3Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(3);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 3);
       assertTrue(compareTab(result.get(0), new double[]{2, 3.2, 5, 7, 8 }));
       assertTrue(compareTab(result.get(1), new double[]{12, 14, 17, 21}));
       assertTrue(compareTab(result.get(2), new double[]{33, 35.5, 47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 3);
       assertTrue(index[0] == 5);
       assertTrue(index[1] == 9);
       assertTrue(index[2] == 14);
    }

    /**
     * Test about Quantile classification.
     * Test with even number table value and 4 class.
     */
    @Test
    public void quantileP4Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(4);
       classification.computeQuantile();
       result = classification.getClasses();
       assertTrue(result.size() == 4);
       assertTrue(compareTab(result.get(0), new double[]{2, 3.2, 5, 7}));
       assertTrue(compareTab(result.get(1), new double[]{8, 12, 14}));
       assertTrue(compareTab(result.get(2), new double[]{17, 21, 33, 35.5}));
       assertTrue(compareTab(result.get(3), new double[]{47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 4);
       assertTrue(index[0] == 4);
       assertTrue(index[1] == 7);
       assertTrue(index[2] == 11);
       assertTrue(index[3] == 14);
    }

    /**
     * Test about Jenks classification.
     * Test with odd number table value and 1 class.
     */
    @Test
    public void jenksI1Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(1);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 1);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8, 12, 14, 17, 21, 33, 35.5, 47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 1);
       assertTrue(index[0] == 13);
    }

    /**
     * Test about Jenks classification.
     * Test with odd number table value and 2 class.
     */
    @Test
    public void jenksI2Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(2);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 2);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8, 12, 14, 17, 21}));
       assertTrue(compareTab(result.get(1), new double[]{33, 35.5, 47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 2);
       assertTrue(index[0] == 8);
       assertTrue(index[1] == 13);
    }

    /**
     * Test about Jenks classification.
     * Test with odd number table value and 3 class.
     */
    @Test
    public void jenksI3Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(3);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 3);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8, 12, 14, 17}));
       assertTrue(compareTab(result.get(1), new double[]{21, 33, 35.5}));
       assertTrue(compareTab(result.get(2), new double[]{47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 3);
       assertTrue(index[0] == 7);
       assertTrue(index[1] == 10);
       assertTrue(index[2] == 13);
    }

    /**
     * Test about Jenks classification.
     * Test with odd number table value and 4 class.
     */
    @Test
    public void jenksI4Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(4);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 4);
       assertTrue(compareTab(result.get(0), new double[]{2, 5, 7, 8}));
       assertTrue(compareTab(result.get(1), new double[]{12, 14, 17, 21}));
       assertTrue(compareTab(result.get(2), new double[]{33, 35.5}));
       assertTrue(compareTab(result.get(3), new double[]{47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 4);
       assertTrue(index[0] == 4);
       assertTrue(index[1] == 8);
       assertTrue(index[2] == 10);
       assertTrue(index[3] == 13);
    }


    /**
     * Test about Jenks classification.
     * Test with even number table value and 2 class.
     */
    @Test
    public void jenksP2Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(2);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 2);
       assertTrue(compareTab(result.get(0), new double[]{2, 3.2, 5, 7, 8, 12, 14, 17, 21}));
       assertTrue(compareTab(result.get(1), new double[]{33, 35.5, 47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 2);
       assertTrue(index[0] == 9);
       assertTrue(index[1] == 14);
    }

    /**
     * Test about Jenks classification.
     * Test with even number table value and 3 class.
     */
    @Test
    public void jenksP3Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(3);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 3);
       assertTrue(compareTab(result.get(0), new double[]{2, 3.2, 5, 7, 8, 12, 14, 17}));
       assertTrue(compareTab(result.get(1), new double[]{21, 33, 35.5}));
       assertTrue(compareTab(result.get(2), new double[]{47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 3);
       assertTrue(index[0] == 8);
       assertTrue(index[1] == 11);
       assertTrue(index[2] == 14);
    }

    /**
     * Test about Jenks classification.
     * Test with even number table value and 4 class.
     */
    @Test
    public void jenksP4Test() {
       data = new double[]{5, 12, 8, 35.5, 17, 56, 14, 2, 21, 58, 47, 33, 7, 3.2};
       Arrays.sort(data);
       classification.setData(data);
       classification.setClassNumber(4);
       classification.computeJenks();
       result = classification.getClasses();
       assertTrue(result.size() == 4);
       assertTrue(compareTab(result.get(0), new double[]{2, 3.2, 5, 7, 8}));
       assertTrue(compareTab(result.get(1), new double[]{12, 14, 17, 21}));
       assertTrue(compareTab(result.get(2), new double[]{33, 35.5}));
       assertTrue(compareTab(result.get(3), new double[]{47, 56, 58}));
       index = classification.getIndex();
       assertTrue(index.length == 4);
       assertTrue(index[0] == 5);
       assertTrue(index[1] == 9);
       assertTrue(index[2] == 11);
       assertTrue(index[3] == 14);
    }

    /**
     * Test with same value in data table.
     */
    @Test
    public void checkJenksDataValidityTest() {
        data = new double[]{1, 1, 1, 2, 2, 3, 4};
        classification.setData(data);
        classification.setClassNumber(4);
        try {
            classification.computeJenks();
        } catch(Exception e) {
            Assert.fail("test should had execute");
        }
        classification.setClassNumber(5);
        try {
            classification.computeJenks();
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }
    }

    /**
     * Test about exceptions.
     */
    @Test
    public void assertFailTest() {
        classification = new Classification();
        //ask result before compute
        try {
            classification.getIndex();
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }

        //ask compute Jenks before set
        try {
            classification.computeJenks();
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }

        //ask compute quantile before set
        try {
            classification.computeQuantile();
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }

        classification.setData(new double[10]);
        //set bad class number value
        try {
            classification.setClassNumber(0);
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }

        //set bad class number value and ask compute jenks
        try {
            classification.setClassNumber(11);
            classification.computeJenks();
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }

        //ask result without compute
        try {
            classification.getClasses();
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }

        //ask compute quantile with bad data and class number value
        try {
            classification.computeQuantile();
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }

        //set null data
        try {
            classification.setData(null);
            Assert.fail("test should had failed");
        } catch(Exception e) {
            //ok
        }
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
}
