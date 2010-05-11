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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComponent;

import org.jdesktop.swingx.JXTitledSeparator;

import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CRSAuthorityFactory;

import org.geotoolkit.resources.Widgets;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.coverage.sql.CoverageDatabaseEvent;
import org.geotoolkit.coverage.sql.DatabaseVetoException;
import org.geotoolkit.coverage.sql.NewGridCoverageReference;
import org.geotoolkit.coverage.sql.CoverageDatabaseController;
import org.geotoolkit.gui.swing.Dialog;
import org.geotoolkit.gui.swing.referencing.AuthorityCodesComboBox;
import org.geotoolkit.internal.SwingUtilities;


/**
 * A form showing details about a {@link NewGridCoverageReference}.
 * Users can verify and modify those information before they are written in the database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.12
 * @module
 */
@SuppressWarnings("serial")
final class NewGridCoverageDetails extends JComponent implements Dialog, CoverageDatabaseController {
    /**
     * The {@link CoverageList} that created this panel.
     */
    private final Component owner;

    /**
     * The label showing the filename, including its extension.
     */
    private final JTextField filename;

    /**
     * The format (editable).
     */
    private final JTextField format;

    /**
     * The combo box for horizontal CRS.
     */
    private final AuthorityCodesComboBox horizontalCRS;

    /**
     * The combo box for vertical CRS.
     */
    private final AuthorityCodesComboBox verticalCRS;

    /**
     * Creates a new panel.
     *
     * @param owner The {@link CoverageList} that created this panel.
     * @param crsFactory The authority factory to use for fetching the list of available CRS.
     */
    @SuppressWarnings("unchecked")
    public NewGridCoverageDetails(final Component owner, final CRSAuthorityFactory crsFactory) {
        this.owner = owner;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));
        final Vocabulary resources = Vocabulary.getResources(getLocale());

        filename      = new JTextField();
        format        = new JTextField();
        horizontalCRS = new AuthorityCodesComboBox(crsFactory, GeographicCRS.class, ProjectedCRS.class);
        verticalCRS   = new AuthorityCodesComboBox(crsFactory, VerticalCRS.class);

        filename.setEditable(false);

        final GridBagConstraints c = new GridBagConstraints();
        c.gridy=0; c.fill=GridBagConstraints.HORIZONTAL;
        addSeparator(resources.getString(Vocabulary.Keys.FILE), c);
        addLabel(resources.getLabel(Vocabulary.Keys.NAME),   filename, c);
        addLabel(resources.getLabel(Vocabulary.Keys.FORMAT), format,   c);
        addSeparator(resources.getString(Vocabulary.Keys.COORDINATE_REFERENCE_SYSTEM), c);
        addLabel(resources.getLabel(Vocabulary.Keys.HORIZONTAL), horizontalCRS, c);
        addLabel(resources.getLabel(Vocabulary.Keys.VERTICAL),   verticalCRS,   c);
    }

    /**
     * Adds a titles separator.
     */
    private void addSeparator(final String text, final GridBagConstraints c) {
        final Insets insets = c.insets;
        insets.left=0; insets.top=9; insets.bottom=3;
        c.gridx=0; c.gridwidth=2;
        add(new JXTitledSeparator(text), c);
        insets.left=18; insets.top=0; insets.bottom=0;
        c.gridy++; c.gridwidth=1;
    }

    /**
     * Adds a label associated with a field.
     */
    private void addLabel(final String text, final JComponent value, final GridBagConstraints c) {
        final JLabel label = new JLabel(text);
        label.setLabelFor(value);
        c.gridx=0; c.weightx=0; add(label, c);
        c.gridx++; c.weightx=1; add(value, c);
        c.gridy++;
    }

    /**
     * Invoked when a new coverage is about to be added. This method set the fields value to
     * the values declared in the given {@code reference} argument, and shows the window.
     *
     * @throws DatabaseVetoException If the user clicked on the "Cancel" button.
     */
    @Override
    public void coverageAdding(final CoverageDatabaseEvent event, final NewGridCoverageReference reference)
            throws DatabaseVetoException
    {
        if (event.isBefore() && event.getNumEntryChange() > 0) {
            filename.setText(reference.getFile().getPath());
            format  .setText(reference.format);
            if (reference.horizontalSRID != 0) {
                horizontalCRS.setSelectedCode(String.valueOf(reference.horizontalSRID));
            }
            if (reference.verticalSRID != 0) {
                verticalCRS.setSelectedCode(String.valueOf(reference.verticalSRID));
            }
            if (showDialog(owner, Widgets.getResources(getLocale()).getString(Widgets.Keys.CONFIRM_ADD_DATA))) {
                // TODO
            } else {
                throw new DatabaseVetoException();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showDialog(final Component owner, final String title) {
        while (SwingUtilities.showOptionDialog(owner, this, title)) {
            return true;
        }
        return false;
    }
}
