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
package org.geotoolkit.gui.swing.image;

import java.awt.Color;
import javax.swing.SwingConstants;
import static java.awt.Color.*;

import org.apache.sis.measure.Units;
import org.geotoolkit.test.gui.SwingTestBase;
import org.geotoolkit.display.axis.NumberGraduation;


/**
 * Tests the {@link ColorRamp}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.10
 *
 * @since 2.0
 */
public final strictfp class ColorRampTest extends SwingTestBase<ColorRamp> {
    /**
     * Constructs the test case.
     */
    public ColorRampTest() {
        super(ColorRamp.class, 4);
    }

    /**
     * Creates the widget.
     */
    @Override
    protected ColorRamp create(final int index) {
        final NumberGraduation graduation = new NumberGraduation(Units.CELSIUS);
        graduation.setMinimum(-3);
        graduation.setMaximum(40);

        final ColorRamp test = new ColorRamp();
        test.setColors(new Color[] {CYAN, YELLOW, ORANGE, RED});
        test.setOrientation((index & 2) == 0 ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL);
        test.setInterpolationEnabled((index & 1) != 0);
        test.setGraduation(graduation);
        return test;
    }
}
