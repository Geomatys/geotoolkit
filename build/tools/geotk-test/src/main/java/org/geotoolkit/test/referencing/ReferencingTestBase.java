/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.test.referencing;

import org.geotoolkit.test.TestBase;


/**
 * Base class for tests on referencing objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.16
 */
public abstract strictfp class ReferencingTestBase extends TestBase {
    /**
     * The length of a centimetre, which is {@value} metre. This value is used as a tolerance
     * threshold in JUnit {@code assert} statements for checking projected coordinate values
     * with centimetric precision.
     *
     * @since 3.20
     */
    public static final double PROJECTED_CENTIMETRE = 0.01;

    /**
     * The number of decimal degrees for one centimetre, which is fixed to {@value}. This is
     * used as a tolerance threshold in JUnit {@code assert} statements for checking geographic
     * coordinate values with centimetric precision.
     *
     * @since 3.20
     */
    public static final double GEOGRAPHIC_CENTIMETRE = PROJECTED_CENTIMETRE / (60*1852);

    /**
     * Creates a new test suite.
     */
    protected ReferencingTestBase() {
    }
}
