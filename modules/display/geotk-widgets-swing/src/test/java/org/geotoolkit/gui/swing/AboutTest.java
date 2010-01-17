/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.gui.swing;

import org.geotoolkit.gui.test.SwingBase;
import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.io.plugin.WorldFileImageWriter;

import org.junit.*;


/**
 * Tests the {@link About} dialog.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.07
 *
 * @since 2.0
 */
public final class AboutTest extends SwingBase<About> {
    /**
     * Ensures that the Geotk plugins are registered.
     */
    @BeforeClass
    public static void setDefaultCodecPreferences() {
        Registry.setDefaultCodecPreferences();
        WorldFileImageReader.Spi.registerDefaults(null);
        WorldFileImageWriter.Spi.registerDefaults(null);
    }

    /**
     * Constructs the test case.
     */
    public AboutTest() {
        super(About.class);
    }

    /**
     * Creates the widget.
     */
    @Override
    protected About create() {
        return new About();
    }
}
