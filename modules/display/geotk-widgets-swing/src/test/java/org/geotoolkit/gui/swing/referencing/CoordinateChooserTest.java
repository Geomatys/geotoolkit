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
package org.geotoolkit.gui.swing.referencing;

import org.geotoolkit.test.gui.SwingBase;


/**
 * Tests the {@link CoordinateChooser}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.01
 *
 * @since 2.3
 */
public final class CoordinateChooserTest extends SwingBase<CoordinateChooser> {
    /**
     * Constructs the test case.
     */
    public CoordinateChooserTest() {
        super(CoordinateChooser.class);
    }

    /**
     * Creates the widget.
     */
    @Override
    protected CoordinateChooser create() {
        return new CoordinateChooser();
    }
}
