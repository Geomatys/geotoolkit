/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
import org.geotoolkit.test.gui.SwingTestBase;
import static org.junit.Assert.*;


/**
 * Tests the {@link PaletteComboBox}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.13
 */
public strictfp class PaletteComboBoxTest extends SwingTestBase<PaletteComboBox> {
    /**
     * Constructs the test case.
     */
    public PaletteComboBoxTest() {
        super(PaletteComboBox.class);
    }

    /**
     * Creates the widget.
     */
    @Override
    protected PaletteComboBox create(final int index) {
        final PaletteComboBox widget = new PaletteComboBox();
        assertNull("No colors should be selected by default.", widget.getSelectedItem());
        assertNull("No colors should be selected by default.", widget.getSelectedColors());

        widget.setSelectedItem("#0000FF");
        assertNull("Color not in the list can not be selected.", widget.getSelectedItem());
        assertNull("Color not in the list can not be selected.", widget.getSelectedColors());

        widget.addDefaultColors();
        widget.setSelectedItem("#0000FF");
        assertEquals("Should find the color in the list.", "#0000FF", widget.getSelectedItem());
        assertArrayEquals(new Color[] {Color.BLUE}, widget.getSelectedColors());

        widget.setSelectedColors(Color.RED);
        assertEquals("Should find the color in the list.", "#FF0000", widget.getSelectedItem());
        assertArrayEquals(new Color[] {Color.RED}, widget.getSelectedColors());

        widget.setSelectedItem("rainbow-t");
        assertEquals("Should find the palette in the list.", "rainbow-t", widget.getSelectedItem());
        assertEquals(63, widget.getSelectedColors().length);

        widget.setSelectedColors(Color.BLUE, Color.WHITE, Color.RED);
        assertEquals("Should find the palette in the list.", "blue-red", widget.getSelectedItem());
        assertEquals(3, widget.getSelectedColors().length);

        return widget;
    }
}
