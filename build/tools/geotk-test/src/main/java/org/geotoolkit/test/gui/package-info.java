/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
 * Tools for Geotk test suites on widgets. The base class provided in this package
 * manage a {@link javax.swing.JDesktopPane} with menu for a few actions like taking
 * a screenshot of the active internal frame.
 * <p>
 * By default the tests display nothing; it merely checks that no exception is thrown during
 * widget construction. However if the "{@code org.geotoolkit.showWidgetTests}" system property
 * is set to "{@code true}", then the widgets will be shown as an internal frame in the desktop.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 */
package org.geotoolkit.test.gui;
