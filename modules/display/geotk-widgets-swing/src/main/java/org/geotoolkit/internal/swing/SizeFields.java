/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.geotoolkit.resources.Vocabulary;


/**
 * A panel containing a "width" and a "height" field, typically for image size.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public class SizeFields extends JPanel {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 174936953329285487L;

    /**
     * The field for selecting the width of target tiles.
     */
    private final JSpinner width;

    /**
     * The field for selecting the height of target tiles.
     */
    private final JSpinner height;

    /**
     * Creates a new panel initialized to the given value.
     *
     * @param locale The locale to use for creating the panel.
     * @param size The initial value to display in the fields.
     */
    public SizeFields(final Locale locale, final Dimension size) {
        super(new GridBagLayout());
        width  = new JSpinner((new SpinnerNumberModel(size.width,  10, null, 1)));
        height = new JSpinner((new SpinnerNumberModel(size.height, 10, null, 1)));
        final Vocabulary resources = Vocabulary.getResources(locale);
        setBorder(BorderFactory.createTitledBorder(resources.getString(Vocabulary.Keys.TILES_SIZE)));
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx=0; c.insets.left=9;
        c.gridy=0; add(new JLabel(resources.getLabel(Vocabulary.Keys.WIDTH)),  c);
        c.gridy++; add(new JLabel(resources.getLabel(Vocabulary.Keys.HEIGHT)), c);
        c.gridx++; c.weightx=1; c.insets.left=3; c.insets.right=9;
        c.gridy=0; add(width,  c);
        c.gridy++; add(height, c);
    }

    /**
     * Returns the size value currently defined by the user.
     *
     * @return The current size value.
     */
    public Dimension getSizeValue() {
        return new Dimension(((Number) width .getValue()).intValue(),
                             ((Number) height.getValue()).intValue());
    }

    /**
     * Sets the size displayed in the fields to the given value.
     *
     * @param size The value to be displayed in the fields.
     */
    public void setSizeValue(final Dimension size) {
        width .setValue(size.width);
        height.setValue(size.height);
    }
}
