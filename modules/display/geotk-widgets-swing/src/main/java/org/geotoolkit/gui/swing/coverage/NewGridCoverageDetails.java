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

import java.io.File;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.BorderFactory;

import org.jdesktop.swingx.JXTitledPanel;
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
import org.geotoolkit.gui.swing.referencing.AuthorityCodesComboBox;
import org.geotoolkit.internal.swing.SwingUtilities;


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
final class NewGridCoverageDetails extends JComponent implements CoverageDatabaseController, ActionListener {
    /**
     * Action commands.
     */
    private static final String OK="OK", CANCEL="CANCEL";

    /**
     * The {@link CoverageList} that created this panel.
     */
    private final CoverageList owner;

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
     * The coverage reference in process of being edited, or {@code null} if none.
     */
    private transient NewGridCoverageReference reference;

    /**
     * Creates a new panel.
     *
     * @param owner The {@link CoverageList} that created this panel.
     * @param crsFactory The authority factory to use for fetching the list of available CRS.
     */
    @SuppressWarnings("unchecked")
    NewGridCoverageDetails(final CoverageList owner, final CRSAuthorityFactory crsFactory) {
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
        addLabel(resources.getLabel(Vocabulary.Keys.NAME), filename, c);
        addLabel(resources.getLabel(Vocabulary.Keys.FORMAT), format, c);
        addSeparator(resources.getString(Vocabulary.Keys.COORDINATE_REFERENCE_SYSTEM), c);
        addLabel(resources.getLabel(Vocabulary.Keys.HORIZONTAL), horizontalCRS, c);
        addLabel(resources.getLabel(Vocabulary.Keys.VERTICAL),   verticalCRS,   c);

        final JButton okButton, cancelButton;
        okButton     = new JButton(resources.getString(Vocabulary.Keys.OK));
        cancelButton = new JButton(resources.getString(Vocabulary.Keys.CANCEL));
        okButton.setActionCommand(OK);
        cancelButton.setActionCommand(CANCEL);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        final JPanel buttonBar = new JPanel(new GridLayout(1, 2));
        buttonBar.setOpaque(false);
        buttonBar.add(okButton);
        buttonBar.add(cancelButton);

        final Insets insets = c.insets;
        insets.left=24; insets.right=24; insets.top=15; insets.bottom=9;
        c.gridx=0; c.gridwidth=2;
        c.weighty=1; c.fill=GridBagConstraints.NONE; c.anchor=GridBagConstraints.SOUTH;
        add(buttonBar, c);
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
     * Returns this panel in a {@link JXTitledPanel}.
     */
    final JComponent createTitledPane() {
        return new JXTitledPanel(Widgets.getResources(getLocale()).getString(Widgets.Keys.CONFIRM_ADD_DATA), this);
    }

    /**
     * Invoked when a new coverage is about to be added. This method set the fields value to
     * the values declared in the given {@code reference} argument, and shows the window.
     * This method needs to be invoked in a thread different than <cite>Swing</cite>.
     *
     * @throws DatabaseVetoException If the user clicked on the "Cancel" button.
     */
    @Override
    public synchronized void coverageAdding(final CoverageDatabaseEvent event, final NewGridCoverageReference reference)
            throws DatabaseVetoException
    {
        /*
         * Do not show the widget if this method is invoked after the insertion (because
         * it is too late for editing the values), or invoked for record removal.
         */
        if (!event.isBefore() || event.getNumEntryChange() <= 0) {
            return;
        }
        /*
         * Copies the information from the given reference to the fields in this widget.
         */
        final File file = reference.getFile();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                filename.setText(file.getName());
                format.setText(reference.format);
                setSelectedCode(horizontalCRS, reference.horizontalSRID);
                setSelectedCode(verticalCRS,   reference.verticalSRID);
                owner.setSelectionPanel(CoverageList.CONTROLLER);
                owner.properties.setImageLater(file);
                NewGridCoverageDetails.this.reference = reference;
            }
        });
        try {
            wait();
        } catch (InterruptedException e) {
            throw new DatabaseVetoException(e);
        }
        /*
         * At this point, we have been weakup by a button pressed by the user.
         * If it was the "Ok" button, the fields are already updated. If it was
         * the "Cancel" button, then the reference has been set to null.
         */
        if (this.reference == null) {
            throw new DatabaseVetoException();
        }
    }

    /**
     * Invoked when the user pressed the "Ok" or "Cancel" button.
     *
     * @todo switch(String) with Java 7.
     */
    @Override
    public synchronized void actionPerformed(final ActionEvent event) {
        final String action = event.getActionCommand();
        if (OK.equals(action)) {
            if (reference != null) try {
                reference.format = format.getText();
                reference.horizontalSRID = getSelectedCode(horizontalCRS);
                reference.verticalSRID   = getSelectedCode(verticalCRS);
            } catch (NumberFormatException e) {
                // Do not weakup the sleeping thread.
                // User will need to make an other selection.
                return;
            }
        } else {
            reference = null;
        }
        notifyAll(); // Weakup the sleeping 'coverageAdding' method.
    }

    /**
     * Parses the selected authority code from the given combo box,
     * or {@code 0} if there is no selection.
     */
    private static int getSelectedCode(final AuthorityCodesComboBox choices) throws NumberFormatException {
        String code = choices.getSelectedCode();
        if (code != null && (code = code.trim()).length() != 0) {
            return Integer.parseInt(code);
        }
        return 0;
    }

    /**
     * Sets the selected authority code to the given combo box.
     */
    private static void setSelectedCode(final AuthorityCodesComboBox choices, final int code) {
        choices.setSelectedCode(code != 0 ? String.valueOf(code) : null);
    }
}
