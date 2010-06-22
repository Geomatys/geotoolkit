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
package org.geotoolkit.gui.swing.image;

import org.geotoolkit.test.gui.SwingBase;
import static org.junit.Assert.*;


/**
 * Tests the {@link PaletteComboBox}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.13
 */
public class PaletteComboBoxTest extends SwingBase<PaletteComboBox> {
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
    protected PaletteComboBox create() {
        final PaletteComboBox widget = new PaletteComboBox();
        assertNull("No colors should be selected by default.", widget.getSelectedItem());

        widget.setSelectedItem("#0000FF");
        assertNull("Color not in the list can not be selected.", widget.getSelectedItem());

        widget.addDefaultColors();
        widget.setSelectedItem("#0000FF");
        assertEquals("Should find the color in the list.", "#0000FF", widget.getSelectedItem());

        widget.setSelectedItem("rainbow");
        assertEquals("Should find the palette in the list.", "rainbow", widget.getSelectedItem());
        return widget;
    }
}
