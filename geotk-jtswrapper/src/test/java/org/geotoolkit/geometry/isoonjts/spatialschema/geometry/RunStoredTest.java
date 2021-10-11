/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry;


import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Jody Garnett
 * @author Joel Skelton
 * @module
 */
public class RunStoredTest {

    private static final Logger LOG = Logging.getLogger("org.geotoolkit.geometry.isoonjts.spatialschema.geometry");
    private static String TEST_DIRECTORY = "src/main/resources/org/geotoolkit/test-data/xml/geometry";

    private FilenameFilter xmlFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.endsWith(".xml");
        }
    };

    /**
     * Load and run all test files.
     * @throws IOException
     */
    @Test
    public void testGeometriesFromXML() throws IOException {
        GeometryTestParser parser = new GeometryTestParser();
        File dir = new File(TEST_DIRECTORY);
        File[] listFiles = dir.listFiles(xmlFilter);
        if (dir.isDirectory()) {
            for (int i=0; i<listFiles.length; i++) {
                File testFile  = (File) listFiles[i];
                LOG.info("Loading test description file:" + testFile);
                FileInputStream inputStream = new FileInputStream(testFile);
                InputSource inputSource = new InputSource(inputStream);
                GeometryTestContainer tests = parser.parseTestDefinition(inputSource);
                assertTrue("Failed test(s) in: " + testFile.getName(), tests.runAllTestCases());
            }
        }
    }
}
