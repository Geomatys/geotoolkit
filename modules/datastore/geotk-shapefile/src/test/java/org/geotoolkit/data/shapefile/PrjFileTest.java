/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile;

import java.io.IOException;

/**
 * 
 * @version $Id$
 * @author Ian Schneider
 * @author James Macgill
 * @module pending
 */
public class PrjFileTest extends AbstractTestCaseSupport {

    static final String TEST_FILE = "wkt/cntbnd01.prj";


    public PrjFileTest(final String testName) throws IOException {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIgnoreEmptyTestCaseWarning() {
    }
}
