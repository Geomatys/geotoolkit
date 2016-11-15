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

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test URL comparator.
 *
 * @author Johann Sorel (Geomatys)
 */
public class URLComparatorTest {

    @Test
    public void comparatorTest() {

        URLComparator comparator = new URLComparator("http://test.com", "http://test.com");
        comparator.compare();

        comparator = new URLComparator("http://test.com", "http://test2.com");
        try {
            comparator.compare();
            fail("URLs are not equal");
        } catch (AssertionError ex) {
            //ok
        }

        comparator = new URLComparator("http://test.com?", "http://test.com?");
        comparator.compare();

        comparator = new URLComparator("http://test.com?key1=value1", "http://test.com?key1=value1");
        comparator.compare();
        comparator = new URLComparator("http://test.com?key1=value1&key2=value2", "http://test.com?key1=value1&key2=value2");
        comparator.compare();
        comparator = new URLComparator("http://test.com?key1=value1", "http://test.com?key1=value0");
        try {
            comparator.compare();
            fail("URLs are not equal");
        } catch (AssertionError ex) {
            //ok
        }

        //invert parameter order
        comparator = new URLComparator("http://test.com?key1=value1&key2=value2", "http://test.com?key2=value2&key1=value1");
        comparator.compare();

        //test parameter name case sensitive
        comparator = new URLComparator("http://test.com?key1=value1", "http://test.com?KEY1=value1");
        try {
            comparator.compare();
            fail("URLs are not equal");
        } catch (AssertionError ex) {
            //ok
        }
        comparator = new URLComparator("http://test.com?key1=value1", "http://test.com?KEY1=value1");
        comparator.setParameterNameCaseSensitive(false);
        comparator.compare();


    }

}
