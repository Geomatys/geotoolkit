/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.util;

import java.text.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XintegerTest {

    public XintegerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testXIntegerSpeed() throws ParseException {

        long before, after;
        final int nb = 10000000;
        final String str1 = "123456-123456";
        final String str2 = "123456-123456-123456-123456-123456-123456";

        assertEquals(123456, Integer.parseInt(str2.substring(0, 6)) );
        assertEquals(123456, Integer.parseInt(str2.substring(7, 13)) );
        assertEquals(123456, Integer.parseInt(str2.substring(14, 20)) );
        assertEquals(123456, Integer.parseInt(str2.substring(21, 27)) );
        assertEquals(123456, Integer.parseInt(str2.substring(28, 34)) );
        assertEquals(123456, Integer.parseInt(str2.substring(35, 41)) );

        assertEquals(123456, XInteger.parseIntSigned(str2, 0, 6) );
        assertEquals(123456, XInteger.parseIntSigned(str2, 7, 13) );
        assertEquals(123456, XInteger.parseIntSigned(str2, 14, 20) );
        assertEquals(123456, XInteger.parseIntSigned(str2, 21, 27) );
        assertEquals(123456, XInteger.parseIntSigned(str2, 28, 34) );
        assertEquals(123456, XInteger.parseIntSigned(str2, 35, 41) );
        
        assertEquals(123456, XInteger.parseIntUnsigned(str2, 0, 6) );
        assertEquals(123456, XInteger.parseIntUnsigned(str2, 7, 13) );
        assertEquals(123456, XInteger.parseIntUnsigned(str2, 14, 20) );
        assertEquals(123456, XInteger.parseIntUnsigned(str2, 21, 27) );
        assertEquals(123456, XInteger.parseIntUnsigned(str2, 28, 34) );
        assertEquals(123456, XInteger.parseIntUnsigned(str2, 35, 41) );

        //test speed on 2 split ------------------------------------------------
        //try to avoid gc call while doing this test
        System.gc();
        System.gc();
        System.gc();

        before = System.currentTimeMillis();
        for(int i=0; i<nb; i++){
            Integer.parseInt(str1.substring(0, 6));
            Integer.parseInt(str1.substring(7, 13));
        }
        after = System.currentTimeMillis();
        long iComplete = (after-before);
        System.out.println("Integer parse in = " + iComplete +" ms");


        before = System.currentTimeMillis();
        for(int i=0; i<nb; i++){
            XInteger.parseIntSigned(str1, 0, 6);
            XInteger.parseIntSigned(str1, 7, 13);
        }
        after = System.currentTimeMillis();
        long ixsComplete = (after-before);
        System.out.println("XInteger Signed parse in = " + ixsComplete +" ms");

        before = System.currentTimeMillis();
        for(int i=0; i<nb; i++){
            XInteger.parseIntUnsigned(str1, 0, 6);
            XInteger.parseIntUnsigned(str1, 7, 13);
        }
        after = System.currentTimeMillis();
        long ixuComplete = (after-before);
        System.out.println("XInteger Unsigned parse in = " + ixuComplete +" ms");


        if(ixsComplete > iComplete || ixuComplete > iComplete){
            System.out.println("[WARNING] XInteger is slower then Integer, try to run the test while no other process running.");
        }
        //assertTrue(ixsComplete < iComplete);
        //we should test compare to iwsComplete, but if a cpu is buzy at this moment
        //it will fail because the results are pretty close
        //assertTrue(ixuComplete < iComplete);


        //test speed on 6 split ------------------------------------------------
        //try to avoid gc call while doing this test
        System.gc();
        System.gc();
        System.gc();

        before = System.currentTimeMillis();
        for(int i=0; i<nb; i++){
            Integer.parseInt(str2.substring(0, 6));
            Integer.parseInt(str2.substring(7, 13));
            Integer.parseInt(str2.substring(14, 20));
            Integer.parseInt(str2.substring(21, 27));
            Integer.parseInt(str2.substring(28, 34));
            Integer.parseInt(str2.substring(35, 41));
        }
        after = System.currentTimeMillis();
        iComplete = (after-before);
        System.out.println("Integer parse in = " + iComplete +" ms");


        before = System.currentTimeMillis();
        for(int i=0; i<nb; i++){
            XInteger.parseIntSigned(str2, 0, 6);
            XInteger.parseIntSigned(str2, 7, 13);
            XInteger.parseIntSigned(str2, 14, 20);
            XInteger.parseIntSigned(str2, 21, 27);
            XInteger.parseIntSigned(str2, 28, 34);
            XInteger.parseIntSigned(str2, 35, 41);
        }
        after = System.currentTimeMillis();
        ixsComplete = (after-before);
        System.out.println("XInteger Signed parse in = " + ixsComplete +" ms");

        before = System.currentTimeMillis();
        for(int i=0; i<nb; i++){
            XInteger.parseIntUnsigned(str2, 0, 6);
            XInteger.parseIntUnsigned(str2, 7, 13);
            XInteger.parseIntUnsigned(str2, 14, 20);
            XInteger.parseIntUnsigned(str2, 21, 27);
            XInteger.parseIntUnsigned(str2, 28, 34);
            XInteger.parseIntUnsigned(str2, 35, 41);
        }
        after = System.currentTimeMillis();
        ixuComplete = (after-before);
        System.out.println("XInteger Unsigned parse in = " + ixuComplete +" ms");

        if(ixsComplete > iComplete || ixuComplete > iComplete){
            System.out.println("[WARNING] XInteger is slower then Integer, try to run the test while no other process running.");
        }
        //assertTrue(ixsComplete < iComplete);
        //we should test compare to iwsComplete, but if a cpu is buzy at this moment
        //it will fail because the results are pretty close
        //assertTrue(ixuComplete < iComplete);

    }

}
