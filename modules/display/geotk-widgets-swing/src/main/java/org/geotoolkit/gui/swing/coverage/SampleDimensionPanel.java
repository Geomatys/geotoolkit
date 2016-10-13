/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
import java.util.Arrays;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JFormattedTextField;
import javax.swing.DefaultComboBoxModel;
import java.text.ParseException;

import org.opengis.util.InternationalString;
import javax.measure.Unit;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.internal.swing.table.JTables;
import org.geotoolkit.internal.swing.UnitFormatter;
import org.geotoolkit.image.palette.PaletteFactory;
import org.geotoolkit.resources.Vocabulary;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;


/**
 * An editable table listing the categories in a {@link GridSampleDimension}. The table is
 * backed by a {@link CategoryTable} model.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.13
 * @module
 */
@SuppressWarnings("serial")
public class SampleDimensionPanel extends JComponent {
    /**
     * The type of elements to be stored in the {@link JComboBox} for enumerating the
     * sample dimension names. We don't use {@link String} because we want to work with
     * duplicated names.
     */
    private static final class BandName {
        /** The band number. This is used for determining if two elements are equal. */
        private final int band;

        /** The band name. This is the value shown in the combo box. */
        String name;

        /** Creates a new {@link JComboBox} item. */
        BandName(final int band, final String name) {
            this.band = band;
            this.name = name;
        }

        /** Returns the value to be shown in the {@link JComboBox}. */
        @Override public String toString() {
            return name;
        }

        /** Returns a hash code value for this item. */
        @Override public int hashCode() {
            return band;
        }

        /** Compare the given object with this item for equality. */
        @Override public boolean equals(final Object object) {
            return (object instanceof BandName) && ((BandName) object).band == band;
        }
    }

    /**
     * Names of the sample dimensions.
     */
    private final JComboBox<BandName> nameField;

    /**
     * Units of measurement.
     */
    private final JFormattedTextField unitField;

    /**
     * The model for categories table.
     */
    private final CategoryTable categories;

    /**
     * The sample dimensions, or {@code null} if none.
     */
    private GridSampleDimension[] sampleDimensions;

    /**
     * The records for each sample dimensions. This is remembered in order to avoid
     * the lost of edited values if the user switch between different bands.
     */
    private CategoryRecord[][] records;

    /**
     * The unit for each sample dimensions.
     */
    private Unit<?>[] units;

    /**
     * The index of the currently selected sample dimension, or {@code -1} if none.
     */
    private int selectedBandIndex = -1;

    /**
     * Creates a new panel which will use the
     * {@linkplain PaletteFactory#getDefault() default palette factory}.
     */
    public SampleDimensionPanel() {
        this(null);
    }

    /**
     * Creates a new panel which will use the given factory for creating color palettes.
     *
     * @param paletteFactory The factory to use for loading colors from a palette name,
     *        or {@code null} for the {@linkplain PaletteFactory#getDefault() default}.
     *
     * @since 3.14
     */
    public SampleDimensionPanel(final PaletteFactory paletteFactory) {
        setLayout(new GridBagLayout());
        final Locale     locale    = getLocale();
        final Vocabulary resources = Vocabulary.getResources(locale);

        categories = new CategoryTable(locale, paletteFactory);
        final JTable table = new JTable(categories);
        JTables.setHeaderCenterAlignment(table);
        categories.configure(table);
        table.setRowSelectionAllowed(false);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        nameField = new JComboBox<>();
        unitField = new JFormattedTextField(new UnitFormatter(locale));
        final JLabel nameLabel = new JLabel(resources.getLabel(Vocabulary.Keys.Band));
        final JLabel unitLabel = new JLabel(resources.getLabel(Vocabulary.Keys.Units));
        nameLabel.setLabelFor(nameField);
        unitLabel.setLabelFor(unitField);

        final GridBagConstraints c = new GridBagConstraints();
        c.gridy=0; c.fill=GridBagConstraints.HORIZONTAL;
        c.gridx=0; c.weightx=0; add(nameLabel, c);
        c.gridx++; c.weightx=1; add(nameField, c);
        c.gridx++; c.weightx=0; c.insets.left=12; add(unitLabel, c);
        c.gridx++; c.ipadx=60;  c.insets.left= 0; add(unitField, c);
        c.gridx=0; c.ipadx=0;   c.weightx=0; c.weighty=1;
        c.gridy++; c.gridwidth=4; c.insets.top=3;
        c.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(table), c);

        final Dimension size = table.getPreferredSize();
        size.width  += 8;  // Some approximative size for scrollbar.
        size.height += 80; // Some approximative size for component above the table.
        setPreferredSize(size);

        final Listeners listeners = new Listeners();
        nameField.addActionListener(listeners);
        nameField.setEditable(true);
    }

    /**
     * Implements various listeners used by the enclosing class.
     */
    private final class Listeners implements ActionListener {
        @Override public void actionPerformed(final ActionEvent event) {
            bandSelected();
        }
    }

    /**
     * Returns the sample dimensions. This method returns a list with the same elements than
     * the elements specified to the last call to {@link #setSampleDimensions(List)} if the
     * user didn't edited the values, or a list containing new {@code GridSampleDimension}
     * instances otherwise.
     * <p>
     * <b>Tip:</b> consider invoking {@link #commitEdit()} before to invoke this method.
     *
     * @return The sample dimensions, or {@code null} if none.
     * @throws ParseException If at least two categories have overlapping range of sample values.
     */
    public List<GridSampleDimension> getSampleDimensions() throws ParseException {
        GridSampleDimension[] bands = sampleDimensions;
        if (bands == null) {
            return null;
        }
        final PaletteFactory paletteFactory = categories.paletteFactory;
        bands = bands.clone();
        for (int i=0; i<bands.length; i++) {
            final CategoryRecord[] records = this.records[i];
            if (records != null) { // If null, the GridSampleDimension is unmodified.
                final Category[] categories = new Category[records.length];
                for (int j=0; j<categories.length; j++) {
                    categories[j] = records[j].getCategory(paletteFactory);
                }
                final String name = nameField.getModel().getElementAt(i).name;
                final GridSampleDimension band;
                try {
                    band = new GridSampleDimension(name, categories, units[i]);
                } catch (IllegalArgumentException e) {
                    ParseException ex = new ParseException(e.getLocalizedMessage(), 0);
                    ex.initCause(e);
                    throw ex;
                }
                if (!band.equals(bands[i])) {
                    bands[i] = band;
                }
            }
        }
        return Arrays.asList(bands);
    }

    /**
     * Sets the sample dimensions to make available in this panel. This method
     * will initially show the first sample dimension found in the list, if any.
     *
     * @param bands The sample dimensions to show, or {@code null} if none.
     */
    public void setSampleDimensions(final List<GridSampleDimension> bands) {
        final Locale locale = getLocale();
        final Vocabulary resources = Vocabulary.getResources(locale);
        final DefaultComboBoxModel<BandName> nameList = (DefaultComboBoxModel<BandName>) nameField.getModel();
        nameList.removeAllElements();
        units   = null;
        records = null;
        sampleDimensions = null;
        if (!isNullOrEmpty(bands)) {
            final int n = bands.size();
            sampleDimensions = bands.toArray(new GridSampleDimension[n]);
            records = new CategoryRecord[n][];
            units   = new Unit<?>[n];
            for (int i=0; i<n; i++) {
                final GridSampleDimension band = sampleDimensions[i];
                units[i] = band.getUnits();
                final InternationalString desc = band.getDescription();
                String name = null;
                if (desc != null) {
                    name = desc.toString(locale);
                }
                if (name == null) {
                    name = resources.getString(Vocabulary.Keys.Band_1, i+1);
                }
                nameList.addElement(new BandName(i, name));
            }
        }
    }

    /**
     * Invoked when a particular band has been selected in the list of sample dimensions.
     */
    private void bandSelected() {
        final int bandIndex = nameField.getSelectedIndex();
        if (bandIndex >= 0) {
            final GridSampleDimension band = sampleDimensions[bandIndex];
            /*
             * Before to assign a new list of categories to the CategoryTable,
             * save the current values (in case the user edited them).
             */
            final CategoryRecord[][] records = this.records;
            if (selectedBandIndex >= 0) {
                if (records[selectedBandIndex] == null) {
                    records[selectedBandIndex] = categories.getElements();
                }
                units[selectedBandIndex] = (Unit<?>) unitField.getValue();
            }
            /*
             * If the CategoryRecords have been previously created for the sample
             * dimension to display, reuse them (so we get any user edited values).
             */
            if (records[bandIndex] != null) {
                categories.setElements(records[bandIndex]);
            } else {
                /*
                 * Otherwise create a new set of CategoryRecords.
                 */
                categories.setCategories(band.getCategories());
            }
            unitField.setValue(units[bandIndex]);
            selectedBandIndex = bandIndex;
        } else if (selectedBandIndex >= 0) {
            /*
             * If the user edited the name of an existing item, just update the name.
             * The categories and units are left untouched.
             */
            final String text = (String) nameField.getSelectedItem();
            if (text != null) {
                nameField.getModel().getElementAt(selectedBandIndex).name = text;
            } else {
                /*
                 * Replacing the current selection by a null selection.
                 */
                selectedBandIndex = -1;
                categories.setCategories(null);
                unitField.setValue(null);
            }
        }
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
        nameField     .setEditable(editable);
        unitField     .setEditable(editable);
        categories.setEditable(editable);
    }

    /**
     * Forces the current value to be taken from the editable fields and set them as the
     * current values. If this operation fails for at least one field, this method will
     * set the focus on the offending field before to throw the exception.
     *
     * @throws ParseException If at least one values couldn't be commited.
     */
    public void commitEdit() throws ParseException {
        try {
            unitField.commitEdit();
        } catch (ParseException e) {
            unitField.requestFocus();
            throw e;
        }
        if (selectedBandIndex >= 0) {
            units[selectedBandIndex] = (Unit<?>) unitField.getValue();
            records[selectedBandIndex] = categories.getElements();
        }
    }
}
