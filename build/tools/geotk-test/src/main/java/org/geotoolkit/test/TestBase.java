/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test;

import java.util.logging.Level;

import org.junit.Before;


/**
 * Base class of Geotoolkit.org tests. This base class provides some configuration that
 * are commons to all subclasses.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 */
public abstract class TestBase {
    /**
     * Creates a new test case.
     */
    protected TestBase() {
    }

    /**
     * Configures the logging handler and the logging level to use for the test suite.
     * This method uses reflection for installing the handler provided in Geotk.
     */
    @Before
    public void loggingSetup() {
        try {
            final Class<?> logging = Class.forName("org.geotoolkit.util.logging.Logging");
            logging.getMethod("forceMonolineConsoleOutput", Level.class).invoke(
                    logging.getField("GEOTOOLKIT").get(null), new Object[] {null});
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
