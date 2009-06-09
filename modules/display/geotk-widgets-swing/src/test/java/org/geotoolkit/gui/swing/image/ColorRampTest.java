/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.image;

import java.awt.Color;
import static java.awt.Color.*;

import org.geotoolkit.gui.swing.WidgetTestCase;
import org.geotoolkit.internal.image.ColorUtilities;


/**
 * Tests the {@link ColorRamp}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.01
 *
 * @since 2.0
 */
public final class ColorRampTest extends WidgetTestCase<ColorRamp> {
    /**
     * Constructs the test case.
     */
    public ColorRampTest() {
        super(ColorRamp.class);
        displayEnabled = false;
    }

    /**
     * Creates the widget.
     */
    @Override
    protected ColorRamp create() {
        final ColorRamp test = new ColorRamp();
        final int[] ARGB = new int[256];
        ColorUtilities.expand(new Color[] {RED, ORANGE, YELLOW, CYAN}, ARGB, 0, ARGB.length);
        test.setColors(ColorUtilities.getIndexColorModel(ARGB));
        return test;
    }
}
