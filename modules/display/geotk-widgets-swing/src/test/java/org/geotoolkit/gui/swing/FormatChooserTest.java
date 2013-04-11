/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing;

import org.geotoolkit.test.gui.SwingTestBase;
import org.apache.sis.measure.AngleFormat;


/**
 * Tests the {@link FormatChooser}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.01
 *
 * @since 2.0
 */
public final strictfp class FormatChooserTest extends SwingTestBase<FormatChooser> {
    /**
     * Constructs the test case.
     */
    public FormatChooserTest() {
        super(FormatChooser.class);
    }

    /**
     * Creates the widget.
     */
    @Override
    protected FormatChooser create(final int index) {
        return new FormatChooser(new AngleFormat());
    }
}
