/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.test;

import org.junit.Test;

/**
 * Test URL comparator.
 *
 * @author Johann Sorel (Geomatys)
 */
public class URLComparatorTest {

    /**
     * Test valid compares.
     */
    @Test
    public void comparatorTest() {

        URLComparator comparator = new URLComparator("http://test.com", "http://test.com");
        comparator.compare();

        comparator = new URLComparator("http://test.com?", "http://test.com?");
        comparator.compare();

        comparator = new URLComparator("http://test.com?key1=value1", "http://test.com?key1=value1");
        comparator.compare();
        comparator = new URLComparator("http://test.com?key1=value1&key2=value2", "http://test.com?key1=value1&key2=value2");
        comparator.compare();

        //invert parameter order
        comparator = new URLComparator("http://test.com?key1=value1&key2=value2", "http://test.com?key2=value2&key1=value1");
        comparator.compare();

        comparator = new URLComparator("http://test.com?key1=value1", "http://test.com?KEY1=value1");
        comparator.setParameterNameCaseSensitive(false);
        comparator.compare();
    }

    /**
     * Test different base URL.
     */
    @Test(expected = AssertionError.class)
    public void testDifferentBase() {
        URLComparator comparator = new URLComparator("http://test.com", "http://test2.com");
        comparator.compare();
    }

    /**
     * Test different parameter value
     */
    @Test(expected = AssertionError.class)
    public void testDifferentParameterValue() {
        URLComparator comparator = new URLComparator("http://test.com?key1=value1", "http://test.com?key1=value0");
        comparator.compare();
    }

    /**
     * Test parameter name case sensitive
     */
    @Test(expected = AssertionError.class)
    public void testDifferentCaseSensitive() {
        URLComparator comparator = new URLComparator("http://test.com?key1=value1", "http://test.com?KEY1=value1");
        comparator.compare();
    }
}
