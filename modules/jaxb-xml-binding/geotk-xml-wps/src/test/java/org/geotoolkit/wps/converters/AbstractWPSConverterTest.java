/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wps.converters;

import org.geotoolkit.wps.WPSConverterTestSuit;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public abstract class AbstractWPSConverterTest extends org.geotoolkit.test.TestBase {

    private static boolean inSuit = true;
    /**
     * Only when test case is executed alone.
     */
    @BeforeClass
    public static void init() {
        if (!WPSConverterTestSuit.isImageIOInitialized()) {
            WPSConverterTestSuit.initImageIO();
            inSuit = false;
        }
    }

    @AfterClass
    public static void release() {
        if (!inSuit) {
            WPSConverterTestSuit.releaseImageIO();
        }
    }

}
