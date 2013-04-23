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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.text.ParseException;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.DefaultComboBoxModel;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import org.opengis.util.InternationalString;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CRSAuthorityFactory;

import org.geotoolkit.resources.Widgets;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.Disposable;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.sql.CoverageDatabaseEvent;
import org.geotoolkit.coverage.sql.DatabaseVetoException;
import org.geotoolkit.coverage.sql.NewGridCoverageReference;
import org.geotoolkit.coverage.sql.CoverageDatabaseController;
import org.geotoolkit.gui.swing.WindowCreator;
import org.geotoolkit.gui.swing.referencing.AuthorityCodesComboBox;
import org.geotoolkit.internal.swing.ComponentDisposer;
import org.geotoolkit.internal.swing.SwingUtilities;

import static org.geotoolkit.util.collection.XCollections.isNullOrEmpty;


/**
 * A form showing details about a {@link NewGridCoverageReference}.
 * Users can verify and modify those information before they are written in the database.
 * <p>
 * An instance of this class is created by {@link CoverageList} when first needed,
 * and reused for all images to be inserted from the same {@code CoverageList}.
 * New images are declared by invoking {@link #coverageAdding}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.12
 * @module
 */
@SuppressWarnings("serial")
final class NewGridCoverageDetails extends WindowCreator implements CoverageDatabaseController, ActionListener, Disposable {
    /**
     * Action commands recognized by {@link #actionPerformed(ActionEvent)}.
     */
    static final String SELECT_FORMAT="SELECT_FORMAT", SELECT_VARIABLES="SELECT_VARIABLES", OK="OK", CANCEL="CANCEL";

    /**
     * The {@link CoverageList} that created this panel.
     */
    private final CoverageList owner;

    /**
     * The label showing the filename, including its extension.
     */
    private final JTextField filename;

    /**
     * The format (editable). Contains many {@link String} elements for each existing format
     * names, and a single {@link InternationalString} element for the "New format" choice.
     */
    private final JComboBox<CharSequence> format;

    /**
     * The currently selected format. Used only in order to avoid querying the database
     * for format twice, and potentially popup two error dialog boxes for the same error.
     */
    private String selectedFormat;

    /**
     * A note (whatever the format is editable or not) about the selected format.
     */
    private final JLabel formatNote;

    /**
     * An item in the {@link #format} combo box with the "New format" label.
     * The type is {@link InternationalString} because it is used as a sentinel
     * type to be replaced by {@link #defaultFormatName}.
     *
     * @since 3.16
     */
    private final InternationalString newFormat;

    /**
     * {@code true} if the format describe geophysics values, or {@code false} for packed values.
     */
    private final JCheckBox isGeophysics;

    /**
     * The combo box for horizontal CRS.
     */
    private final AuthorityCodesComboBox horizontalCRS;

    /**
     * The combo box for vertical CRS.
     */
    private final AuthorityCodesComboBox verticalCRS;

    /**
     * The component for the definition of categories.
     */
    private final SampleDimensionPanel sampleDimensionEditor;

    /**
     * The variables selected by the user after the {@link #filterImages} method.
     *
     * @since 3.15
     */
    private transient List<String> selectedVariables;

    /**
     * The coverage reference in process of being edited, or {@code null} if none.
     */
    private transient NewGridCoverageReference reference;

    /**
     * The default format name of the new {@linkplain #reference}. This is the format to be given
     * to {@link NewGridCoverageReference} when the user select "New format" in the combo box.
     *
     * @since 3.16
     */
    private transient String defaultFormatName;

    /**
     * Creates a new panel.
     *
     * @param owner The {@link CoverageList} that created this panel.
     * @param crsFactory The authority factory to use for fetching the list of available CRS.
     */
    @SuppressWarnings("unchecked")
    NewGridCoverageDetails(final CoverageList owner, final CRSAuthorityFactory crsFactory) {
        this.owner = owner;
        setLayout(new BorderLayout());
        final Locale     locale    = getLocale();
        final Widgets    guires    = Widgets.getResources(locale);
        final Vocabulary resources = Vocabulary.getResources(locale);
        newFormat     = new SimpleInternationalString("<html><i>" + resources .getString(Vocabulary.Keys.NEW_FORMAT) + "</i></html>");
        filename      = new JTextField();
        format        = new JComboBox<>();
        formatNote    = new JLabel();
        isGeophysics  = new JCheckBox(guires.getString(Widgets.Keys.RASTER_IS_GEOPHYSICS));
        horizontalCRS = new AuthorityCodesComboBox(crsFactory, GeographicCRS.class, ProjectedCRS.class);
        verticalCRS   = new AuthorityCodesComboBox(crsFactory, VerticalCRS.class);
        sampleDimensionEditor = new SampleDimensionPanel();
        filename.setEditable(false);
        format.setEditable(true);
        format.setActionCommand(SELECT_FORMAT);

        final JXTaskPaneContainer container = new JXTaskPaneContainer();
        final GridBagConstraints c = new GridBagConstraints();
        c.gridy=0; c.fill=GridBagConstraints.HORIZONTAL;

        JXTaskPane pane = new JXTaskPane();
        pane.setLayout(new GridBagLayout());
        pane.setTitle(resources.getString(Vocabulary.Keys.FILE));
        addRow(pane, resources.getLabel(Vocabulary.Keys.NAME), filename, c);
        addRow(pane, resources.getLabel(Vocabulary.Keys.FORMAT), format, c);
        c.insets.left=6; addRow(pane, null, formatNote, c);
        c.insets.left=0; addRow(pane, null, isGeophysics, c);
        container.add(pane);

        c.gridy=0;
        pane = new JXTaskPane();
        pane.setLayout(new GridBagLayout());
        pane.setTitle(resources.getString(Vocabulary.Keys.COORDINATE_REFERENCE_SYSTEM));
        addRow(pane, resources.getLabel(Vocabulary.Keys.HORIZONTAL), horizontalCRS, c);
        addRow(pane, resources.getLabel(Vocabulary.Keys.VERTICAL), verticalCRS, c);
        container.add(pane);

        c.gridy=0;
        pane = new JXTaskPane();
        pane.setTitle(resources.getString(Vocabulary.Keys.SAMPLE_DIMENSIONS));
        pane.add(sampleDimensionEditor);
        container.add(pane);

        final JButton okButton, cancelButton;
        okButton     = new JButton(resources.getString(Vocabulary.Keys.OK));
        cancelButton = new JButton(resources.getString(Vocabulary.Keys.CANCEL));
        okButton.setActionCommand(OK);
        cancelButton.setActionCommand(CANCEL);
        final JPanel buttonBar = new JPanel(new GridLayout(1, 2));
        buttonBar.setOpaque(false);
        buttonBar.add(okButton);
        buttonBar.add(cancelButton);

        // For centering the buttons
        final JComponent centered = new JPanel();
        centered.setOpaque(false);
        centered.add(buttonBar);

        add(new JScrollPane(container), BorderLayout.CENTER);
        add(centered, BorderLayout.SOUTH);

        format      .addActionListener(this);
        okButton    .addActionListener(this);
        cancelButton.addActionListener(this);
        addAncestorListener(ComponentDisposer.INSTANCE);
    }

    /**
     * Adds a label associated with a field.
     */
    private static void addRow(final Container pane, final String text, final JComponent value, final GridBagConstraints c) {
        c.gridx=0; c.weightx=0;
        if (text != null) {
            final JLabel label = new JLabel(text);
            label.setLabelFor(value);
            pane.add(label, c);
        }
        c.gridx++; c.weightx=1;
        pane.add(value, c);
        c.gridy++;
    }

    /**
     * Invoked when the "Ok" or "Cancel" button is pressed, or when a new format is selected,
     * or when a new variable is selected.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        final String action = event.getActionCommand();
        switch (action) {
            case SELECT_FORMAT:     formatSelected();           break;
            case SELECT_VARIABLES:  variableSelectionChanged(); break;
            case OK:                confirm();                  break;
            case CANCEL:            dispose();                  break;
        }
    }

    /**
     * Returns the name of the currently selected format.
     */
    private String getSelectedFormat() {
        final Object formatName = format.getSelectedItem();
        if (formatName instanceof InternationalString) {
            // InternationalString is used as a special value for "New format".
            return defaultFormatName;
        } else {
            return (String) formatName;
        }
    }

    /**
     * Invoked when the user selected a format in the {@link JComboBox}, or when the format
     * changed programmatically. If the format changed, gets the {@link GridSampleDimension}s
     * and updates the field with the new values.
     */
    private void formatSelected() {
        final String formatName = getSelectedFormat();
        if (!Objects.equals(formatName, selectedFormat)) {
            selectedFormat = formatName;
            List<GridSampleDimension> bands = null;
            CoverageStoreException failure = null;
            boolean editable = false;
            final NewGridCoverageReference reference = this.reference;
            if (reference != null) try {
                reference.format = formatName;
                reference.refresh();
                bands = reference.sampleDimensions;
                editable = !reference.isFormatDefined();
            } catch (CoverageStoreException e) {
                failure = e;
            }
            /*
             * Set the SampleDimensionPanel to the new values or clear the panel if there is no
             * bands, except if the format does not exist. In the later case, we assume that the
             * user wants to create a new format using the current values as a template.
             */
            if (!editable || !isNullOrEmpty(bands)) {
                boolean geophysics = false;
                if (bands != null) {
                    for (final GridSampleDimension band : bands) {
                        if (!band.getCategories().isEmpty() && band == band.geophysics(true)) {
                            geophysics = true;
                            break;
                        }
                    }
                }
                isGeophysics.setSelected(geophysics);
                sampleDimensionEditor.setSampleDimensions(bands);
            }
            /*
             * Sets whatever the format described in the "Sample dimensions" section is
             * editable. This also update the note label behind the "Format" field.
             */
            formatNote.setText(Widgets.getResources(getLocale()).getString(
                    editable ? Widgets.Keys.NEW_FORMAT : Widgets.Keys.RENAME_FORMAT_FOR_EDIT));
            sampleDimensionEditor.setEditable(editable);
            isGeophysics.setEnabled(editable);
            /*
             * Finally, report the error if there is any.
             */
            if (failure != null) {
                owner.exceptionOccured(failure);
            }
        }
        /*
         * Replaces the "{@code <html><i>New Format</i></html>} value in the combo box field
         * by the actual value.
         */
        if (format.getSelectedItem() instanceof InternationalString) {
            format.setSelectedItem(defaultFormatName);
        }
    }

    /**
     * Invoked before {@link #coverageAdding} in order to let the user select a variable among
     * a list of variables found in the file.
     * <p>
     * This method needs to be invoked in a thread different than the <cite>Swing</cite> thread.
     *
     * @param  images The variables found in the file.
     * @param  multiSelectionAllowed {@code true} if the {@link JList} shall allow multi-selection.
     * @return The variables selected by the user.
     * @throws DatabaseVetoException If the user clicked on the "Cancel" button.
     *
     * @since 3.15
     */
    @Override
    public synchronized Collection<String> filterImages(final List<String> images, final boolean multiSelectionAllowed)
            throws DatabaseVetoException
    {
        assert !EventQueue.isDispatchThread();
        SwingUtilities.invokeAndWait(new Runnable() {
           @Override public void run() {
               owner.showVariableChooser(images.toArray(new String[images.size()]), multiSelectionAllowed);
           }
        });
        try {
            wait(); // Weakup at the end of actionPerformed(boolean) below.
        } catch (InterruptedException e) {
            // This happen if the CoverageList frame has been closed
            // by CoverageList.Listeners.ancestorRemoved(AncestorEvent).
            throw new DatabaseVetoException(e);
        }
        /*
         * At this point, we have been weakup by a button pressed by the user.
         * If it was the "Ok" button, the fields are already updated (see the
         * actionPerformed method below). If it was the "Cancel" button, then
         * the reference has been set to null.
         */
        if (selectedVariables == null) {
            throw new DatabaseVetoException();
        }
        return selectedVariables;
    }

    /**
     * Invoked when a new coverage is about to be added. This method set the fields value to
     * the values declared in the given {@code reference} argument, and shows the window.
     * <p>
     * This method needs to be invoked in a thread different than the <cite>Swing</cite> thread.
     *
     * @throws DatabaseVetoException If the user clicked on the "Cancel" button.
     */
    @Override
    public synchronized void coverageAdding(final CoverageDatabaseEvent event,
            final NewGridCoverageReference newReference) throws DatabaseVetoException
    {
        assert !EventQueue.isDispatchThread();
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
        final File file = newReference.getFile();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                reference         = newReference; // Must be set first.
                defaultFormatName = newReference.format;
                selectedFormat    = null; // Must be before the change of format choices.
                try {
                    final String[] alternatives = newReference.getAlternativeFormats();
                    final DefaultComboBoxModel<CharSequence> model = new DefaultComboBoxModel<CharSequence>(alternatives);
                    if (!ArraysExt.contains(alternatives, newReference.format)) {
                        /*
                         * InternationalString is used as a sentinal value meaning "New Format",
                         * to be replaced by NewGridCoverageDetails.this.defaultFormatName when
                         * needed.
                         */
                        model.insertElementAt(newFormat, 0);
                    }
                    format.setModel(model);
                } catch (CoverageStoreException e) {
                    Logging.unexpectedException(NewGridCoverageReference.class, "getAlternativeFormats", e);
                    // Keep the current combo box content unchanged.
                }
                filename.setText(file.getName());
                format.setSelectedItem(newReference.format);
                setSelectedCode(horizontalCRS, newReference.horizontalSRID);
                setSelectedCode(verticalCRS,   newReference.verticalSRID);
                owner.setSelectionPanel(CoverageList.CONTROLLER);
                owner.properties.setImageLater(file);
            }
        });
        try {
            wait(); // Weakup at the end of actionPerformed(boolean) below.
        } catch (InterruptedException e) {
            // This happen if the CoverageList frame has been closed
            // by CoverageList.Listeners.ancestorRemoved(AncestorEvent).
            throw new DatabaseVetoException(e);
        }
        /*
         * At this point, we have been weakup by a button pressed by the user.
         * If it was the "Ok" button, the fields are already updated (see the
         * actionPerformed method below). If it was the "Cancel" button, then
         * the reference has been set to null.
         */
        if (reference == null) {
            throw new DatabaseVetoException();
        }
    }

    /**
     * Invoked when the user pressed the "Cancel" button or closed the window. When pressing
     * the "Cancel" button, this method is invoked by the {@link #actionPerformed(ActionEvent)}
     * method. When closing the window, this method is invoked by {@link ComponentDisposer}.
     */
    @Override
    public synchronized void dispose() {
        reference         = null;
        defaultFormatName = null;
        selectedVariables = null;
        notifyAll(); // Weakup the sleeping 'coverageAdding' method.
    }

    /**
     * Invoked when the user selected a new variable. This is relevant only for
     * files containing different images for different variables.
     */
    private synchronized void variableSelectionChanged() {
        selectedVariables = owner.getSelectedVariables();
        notifyAll(); // Weakup the sleeping 'coverageAdding' method.
    }

    /**
     * Invoked when the user pressed the "Ok" button.
     */
    private synchronized void confirm() {
        final NewGridCoverageReference reference = this.reference;
        try {
            reference.format         = getSelectedFormat();
            reference.horizontalSRID = getSelectedCode(horizontalCRS);
            reference.verticalSRID   = getSelectedCode(verticalCRS);
            reference.sampleDimensions.clear();
            sampleDimensionEditor.commitEdit();
            final List<GridSampleDimension> bands = sampleDimensionEditor.getSampleDimensions();
            if (bands != null) {
                final boolean isGeophysics = this.isGeophysics.isSelected();
                for (int i=bands.size(); --i>=0;) {
                    bands.set(i, bands.get(i).geophysics(isGeophysics));
                }
                reference.sampleDimensions.addAll(bands);
            }
        } catch (NumberFormatException | ParseException e) {
            // Do not weakup the sleeping thread.
            // User will need to make an other selection.
            return;
        }
        /*
         * Perform some validity checks on user arguments.
         */
        if (reference.horizontalSRID == 0) {
            incompleteForm(0);
            // Do no weakup the sleeping thread.
            return;
        }
        if (reference.verticalSRID == 0 && reference.verticalValues != null) {
            incompleteForm(1);
            // Do no weakup the sleeping thread.
            return;
        }
        notifyAll(); // Weakup the sleeping 'coverageAdding' method.
    }

    /**
     * Invoked when the widget can not process because of missing information in the form.
     *
     * @param crsType 0 for horizontal CRS, or 1 for vertical CRS.
     */
    private void incompleteForm(final int crsType) {
        final Widgets resources = Widgets.getResources(getLocale());
        final JXLabel label = new JXLabel(resources.getString(Widgets.Keys.CRS_REQUIRED_1, crsType));
        final String  title = resources.getString(Widgets.Keys.INCOMPLETE_FORM);
        label.setLineWrap(true);
        getWindowHandler().showError(this, label, title);
    }

    /**
     * Parses the selected authority code from the given combo box,
     * or {@code 0} if there is no selection.
     */
    private static int getSelectedCode(final AuthorityCodesComboBox choices) throws NumberFormatException {
        String code = choices.getSelectedCode();
        if (code != null && !(code = code.trim()).isEmpty()) {
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
