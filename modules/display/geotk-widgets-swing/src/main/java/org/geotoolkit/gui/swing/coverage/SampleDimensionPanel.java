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
package org.geotoolkit.gui.swing.coverage;

import java.util.List;
import java.util.Locale;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.opengis.util.InternationalString;
import javax.measure.unit.Unit;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.internal.swing.table.JTables;
import org.geotoolkit.resources.Vocabulary;


/**
 * An editable table listing the categories in a {@link GridSampleDimension}. The table is
 * backed by a {@link CategoryTable} model.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.13
 * @module
 */
@SuppressWarnings("serial")
public class SampleDimensionPanel extends JComponent {
    /**
     * Name of the sample dimension.
     */
    private final JTextField name;

    /**
     * Units of measurement.
     */
    private final JTextField units;

    /**
     * The model for categories table.
     */
    private final CategoryTable categories;

    /**
     * Creates a new, initially empty, panel.
     */
    public SampleDimensionPanel() {
        setLayout(new GridBagLayout());
        final Locale     locale    = getLocale();
        final Vocabulary resources = Vocabulary.getResources(locale);

        categories = new CategoryTable(locale);
        final JTable table = new JTable(categories);
        JTables.setHeaderCenterAlignment(table);
        categories.configure(table);

        name  = new JTextField();
        units = new JTextField();
        final JLabel nameLabel = new JLabel(resources.getLabel(Vocabulary.Keys.NAME));
        final JLabel unitLabel = new JLabel(resources.getLabel(Vocabulary.Keys.UNITS));
        nameLabel.setLabelFor(name);
        unitLabel.setLabelFor(units);

        final GridBagConstraints c = new GridBagConstraints();
        c.gridy=0; c.fill=GridBagConstraints.HORIZONTAL;
        c.gridx=0; c.weightx=0; add(nameLabel, c);
        c.gridx++; c.weightx=1; add(name, c);
        c.gridx++; c.weightx=0; c.insets.left=12; add(unitLabel, c);
        c.gridx++; c.ipadx=60;  c.insets.left= 0; add(units, c);
        c.gridx=0; c.ipadx=0;   c.weightx=0; c.weighty=1;
        c.gridy++; c.gridwidth=4; c.insets.top=3;
        c.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(table), c);

        final Dimension size = table.getPreferredSize();
        size.width  += 8;  // Some approximative size for scrollbar.
        size.height += 16; // Some approximative size for component above the table.
        setPreferredSize(size);
    }

    /**
     * Sets the sample dimension to show in this panel.
     *
     * @param band The sample dimension to show, or {@code null} if none.
     */
    public void setSampleDimension(final GridSampleDimension band) {
        String description = null;
        String unitSymbol  = null;
        List<Category> cat = null;
        if (band != null) {
            final InternationalString desc = band.getDescription();
            if (desc != null) {
                description = desc.toString(getLocale());
            }
            final Unit<?> uom = band.getUnits();
            if (uom != null) {
                unitSymbol = uom.toString();
            }
            cat = band.getCategories();
        }
        name.setText(description);
        units.setText(unitSymbol);
        categories.setCategories(cat);
    }

    /**
     * Returns {@code true} if the sample dimension is editable.
     *
     * @return {@code true} if the sample dimension is editable.
     */
    public boolean isEditable() {
        return categories.isEditable();
    }

    /**
     * Sets whatever edition should be allowed for this component.
     * Editions are enabled by default, like most <cite>Swing</cite> components.
     *
     * @param editable {@code false} for disabling edition, or {@code true} for re-enabling it.
     */
    public void setEditable(final boolean editable) {
        name      .setEditable(editable);
        units     .setEditable(editable);
        categories.setEditable(editable);
    }
}
