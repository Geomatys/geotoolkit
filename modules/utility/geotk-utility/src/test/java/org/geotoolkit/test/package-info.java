/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

/**
 * Tools for Geotk test suites. This package defines a base class, {@link org.geotoolkit.test.TestBase}, which
 * is extended by many Geotk test suite. This package defines also an {@link org.geotoolkit.test.Assert} class
 * which extends {@link org.junit.Assert} with the addition of assertion methods commonly used in Geotk test
 * suite.
 * <p>
 * By default, successful tests do not produce any output. However it is possible to ask for verbose
 * output, which is sometime useful for debugging purpose. The following properties can be set to
 * {@code true} for that purpose:
 * <p>
 * <ul>
 *   <li>{@value org.geotoolkit.test.TestBase#VERBOSE_KEY} for more console output</li>
 *   <li>{@value org.geotoolkit.test.gui.SwingTestBase#SHOW_PROPERTY_KEY} for showing images or widgets</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 */
package org.geotoolkit.test;
