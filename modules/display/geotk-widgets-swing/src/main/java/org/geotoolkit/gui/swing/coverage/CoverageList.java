/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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

import java.util.Set;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.io.File;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXTitledPanel;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;

import org.geotoolkit.coverage.sql.Layer;
import org.geotoolkit.coverage.sql.CoverageTableModel;
import org.geotoolkit.coverage.sql.CoverageEnvelope;
import org.geotoolkit.coverage.sql.CoverageDatabase;
import org.geotoolkit.coverage.sql.GridCoverageReference;
import org.geotoolkit.coverage.sql.DatabaseVetoException;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.gui.swing.image.ImageFileProperties;
import org.geotoolkit.gui.swing.image.ImageFileChooser;
import org.geotoolkit.gui.swing.IconFactory;
import org.geotoolkit.internal.swing.ToolBar;
import org.geotoolkit.internal.swing.ExceptionMonitor;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.internal.swing.SwingUtilities;
import org.geotoolkit.resources.Widgets;
import org.jdesktop.swingx.JXBusyLabel;


/**
 * A list displaying the {@linkplain Layer#getCoverageReferences(CoverageEnvelope) set of
 * coverages} available in a given layer. This widget displays also the properties of the
 * selected file on the right side.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @see CoverageTableModel
 * @see <a href="{@docRoot}/../modules/display/geotk-wizards-swing/AddCoverages.html">Adding layers and images to the Coverage-SQL database</a>
 *
 * @since 3.11 (derived from Seagis)
 * @module
 */
@SuppressWarnings("serial")
public class CoverageList extends JComponent {
    /**
     * Action commands.
     */
    private static final String ADD="ADD", REMOVE="REMOVE", REFRESH="REFRESH";

    /**
     * Layout parameters for the components put in {@link #selectionPanel}.
     */
    static final String TABLE="TABLE", FILES="FILES", VARIABLES="VARIABLES", CONTROLLER="CONTROLLER", BUZY="BUZY";

    /**
     * The name of the panel currently selected in {@link #selectionPanel}.
     */
    private String selectionPanelName;

    /**
     * The panel where to select a coverage, either from the table of coverages
     * or from a file chooser. This panel uses a {@link CardLayout}.
     */
    private final JPanel selectionPanel;

    /**
     * The file chooser, created only when the user click the "add" button for the first time.
     */
    private ImageFileChooser fileChooser;

    /**
     * The variable chooser, created only when the user select a file for the first time.
     *
     * @since 3.15
     */
    private JList<String> variableChooser;

    /**
     * The table which list all coverages.
     */
    private final JTable table;

    /**
     * The list of coverages for the selected layer.
     */
    private final CoverageTableModel coverages;

    /**
     * The layer shown by this widget.
     */
    private Layer layer;

    /**
     * The spatio-temporal envelope to query, or {@code null} for the full coverage.
     */
    private CoverageEnvelope envelope;

    /**
     * The properties of the selected image.
     */
    final ImageFileProperties properties;

    /**
     * The label to display when the widget is busy loading the properties of an image.
     * This happen before the {@link #addController} is initialized with the new values.
     */
    private JXBusyLabel busyLabel;

    /**
     * The panel for adding new files. Will be created only when first needed.
     */
    private NewGridCoverageDetails addController;

    /**
     * The toolbar, to be enabled or disabled depending on the view currently active
     * in {@link #selectionPanel}.
     */
    private final ToolBar toolbar;

    /**
     * The button for removing entries. To be enabled only when at least
     * one entry is selected.
     */
    private final JButton removeButton;

    /**
     * Listeners used for various actions.
     */
    private final Listeners listeners;

    /**
     * Creates a new list with a default, initially empty, {@code CoverageTableModel}.
     */
    public CoverageList() {
        this(new CoverageTableModel((Locale) null));
    }

    /**
     * Creates a list for the specified collection of coverages.
     *
     * @param coverages The table model which contain the coverage entries to list.
     */
    public CoverageList(final CoverageTableModel coverages) {
        setLayout(new BorderLayout()); // Required for the toolbar.
        this.coverages = coverages;
        final Locale locale = getLocale();
        final Vocabulary resources = Vocabulary.getResources(locale);
        listeners = new Listeners();

        final JTable table = new JTable(coverages);
        final TableCellRenderer renderer = new CoverageTableModel.CellRenderer();
        table.setDefaultRenderer(String.class, renderer);
        table.setDefaultRenderer(Date.class,   renderer);
        table.getSelectionModel().addListSelectionListener(listeners);
        this.table = table;

        final Dimension minimumSize = new Dimension(120, 100);
        selectionPanel = new JPanel(new CardLayout());
        selectionPanel.add(new JScrollPane(table), TABLE);
        selectionPanel.setMinimumSize(minimumSize);
        selectionPanelName = TABLE;

        properties = new CoverageFileProperties();
        properties.setMinimumSize(minimumSize);
        properties.setPreferredSize(new Dimension(440, 400));
        final JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, selectionPanel, properties);
        pane.setOneTouchExpandable(true);
        pane.setContinuousLayout(true);
        pane.setBorder(BorderFactory.createEmptyBorder(9, 3, 9, 9));
        /*
         * The buttons bar.
         */
        toolbar = new ToolBar(resources.getString(Vocabulary.Keys.EDIT), ToolBar.VERTICAL);
        toolbar.setRollover(true);
        button(resources, Vocabulary.Keys.ADD, "toolbarButtonGraphics/table/RowInsertBefore24.gif", ADD);
        removeButton = button(resources, Vocabulary.Keys.REMOVE, "toolbarButtonGraphics/table/RowDelete24.gif", REMOVE);
        removeButton.setEnabled(false);
        button(resources, Vocabulary.Keys.REFRESH, "toolbarButtonGraphics/general/Refresh24.gif", REFRESH);
        /*
         * Put the components in this panel.
         */
        add(BorderLayout.CENTER, pane);
        add(toolbar, BorderLayout.WEST);
    }

    /**
     * Creates a new button and adds it to the toolbar.
     */
    private JButton button(final Vocabulary resources, final short key, final String image, final String action) {
        final String text = resources.getString(key);
        final JButton button = IconFactory.DEFAULT.getButton(image, text, text);
        button.setActionCommand(action);
        button.addActionListener(listeners);
        toolbar.add(button);
        return button;
    }

    /**
     * Implement all listeners used by the {@link LayerList} class.
     */
    private final class Listeners implements ListSelectionListener, ActionListener {
        /**
         * The last selected entry. Used in order to detect if the selection changed,
         * in order to avoid unnecessary fetching of image properties.
         */
        private GridCoverageReference last;

        /**
         * Invoked when a coverage has been selected. This method enable the "remove"
         * button if at least one entry is selected, then read the properties of the
         * selected image in a background thread.
         */
        @Override
        public void valueChanged(final ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            final ListSelectionModel model = (ListSelectionModel) event.getSource();
            final boolean isEmpty = model.isSelectionEmpty();
            removeButton.setEnabled(!isEmpty);
            GridCoverageReference reference = null;
            if (!isEmpty) {
                final int coverageIndex = model.getAnchorSelectionIndex();
                reference = coverages.getCoverageReferenceAt(coverageIndex);
                if (reference == last) {
                    return;
                }
            }
            last = reference;
            setImageProperties(reference);
        }

        /**
         * Invoked when one of the buttons ("Remove", "Add", etc.) has been pressed.
         * This method delegates to the appropriate method in the enclosing class.
         */
        @Override
        public void actionPerformed(final ActionEvent event) {
            final String action = event.getActionCommand();
            switch (action) {
                case REFRESH: {
                    refresh();
                    break;
                }
                case REMOVE: {
                    removeCoverage();
                    break;
                }
                case ADD: {
                    showFileChooser();
                    break;
                }
                case ImageFileChooser.CANCEL_SELECTION: {
                    setSelectionPanel(TABLE);
                    break;
                }
                case ImageFileChooser.APPROVE_SELECTION: {
                    // Must check if the file selection panel is visible, because pressing the 'Enter'
                    // key in the format JComboBox of the NewGridCoverageDetails widget seems to also
                    // fire the event associated with "Ok" button of the JFileChooser.
                    if (FILES.equals(selectionPanelName)) {
                        addNewCoverage();
                    }
                    break;
                }
            }
        }
    }

    /**
     * Returns the layer for which this widget is listing the coverages.
     * If the layer is unknown, then this method returns {@code null}.
     *
     * @return The current layer, or {@code null} if unknown.
     */
    public Layer getLayer() {
        return layer;
    }

    /**
     * Sets the content of this widget to the list of coverages in the given layer.
     * This method will fetch the list of coverage entries in a background thread.
     *
     * @param layer The layer for which to get the coverage entries, or {@code null} if none.
     */
    public void setLayer(final Layer layer) {
        if (!Objects.equals(layer, this.layer)) {
            setData(layer, envelope);
        }
    }

    /**
     * Returns the envelope of the listed coverage entries, or {@code null} if there
     * is no restriction. If non-null, then this widget list only the coverage entries
     * which intersect the returned envelope.
     *
     * @return The envelope of the listed coverage entries, or {@code null} if unbounded.
     */
    public CoverageEnvelope getEnvelope() {
        return (envelope != null) ? envelope.clone() : null;
    }

    /**
     * Sets the envelope of coverage entries to list. IF the given envelope is non-null, then
     * this widget will list only the coverage entries which intersect the given envelope.
     * <p>
     * If a {@linkplain #setLayer(Layer) layer has been set}, then this method will refresh
     * the list of coverage entries in a background thread.
     *
     * @param envelope The envelope of the coverage entries to list, or {@code null} if unbounded.
     */
    public void setEnvelope(CoverageEnvelope envelope) {
        if (!Objects.equals(envelope, this.envelope)) {
            setData(layer, envelope);
        }
    }

    /**
     * Sets the content of this widget to the list of coverages in the given layer which
     * intersect the given envelope. This method combines {@link #setLayer(Layer)} and
     * {@link #setEnvelope(CoverageEnvelope)} in a single method call.
     * <p>
     * This method will fetch the list of coverage references in a background thread.
     *
     * @param layer The layer for which to get the coverage entries, or {@code null} if none.
     * @param envelope The envelope of the coverage entries to list, or {@code null} if unbounded.
     */
    final void setData(final Layer layer, final CoverageEnvelope envelope) {
        final Layer oldLayer = this.layer;
        final CoverageEnvelope oldEnvelope = this.envelope;
        this.layer = layer;
        this.envelope = envelope;
        if (layer == null) {
            coverages.setCoverageReferences(Collections.<GridCoverageReference>emptyList());
        } else {
            final SwingWorker<Set<GridCoverageReference>,Object> worker = new SwingWorker<Set<GridCoverageReference>,Object>() {
                /**
                 * Invoked in a background thread for fetching the list of layers.
                 */
                @Override
                protected Set<GridCoverageReference> doInBackground() throws CoverageStoreException {
                    return layer.getCoverageReferences(envelope);
                }

                /**
                 * Invoked in the Swing thread for settings the table content.
                 */
                @Override
                protected void done() {
                    Exception cause;
                    try {
                        coverages.setCoverageReferences(get());
                        return;
                    } catch (InterruptedException ex) {
                        cause = ex;
                    } catch (ExecutionException ex) {
                        final Throwable c = ex.getCause();
                        cause = (c instanceof Exception) ? (Exception) c : ex;
                    }
                    exceptionOccured(cause);
                }
            };
            worker.execute();
        }
        firePropertyChange("layer", layer, oldLayer);
        firePropertyChange("envelope", envelope, oldEnvelope);
    }

    /**
     * Reloads the list of coverages in the {@linkplain #getLayer() current layer} which
     * intersect the {@linkplain #getEnvelope() current envelope}.
     *
     * @since 3.12
     */
    public void refresh() {
        setData(layer, envelope);
    }

    /**
     * Shows the properties of the given coverage reference. The properties are shown
     * in the right side of the split pane.
     *
     * @param reference The reference to a grid coverage, or {@code null} if none.
     */
    private void setImageProperties(final GridCoverageReference reference) {
        properties.setImageLater(reference);
    }

    /**
     * Sets the selection panel (the component on the left side of the split panel)
     * to the table or to the file chooser. The component to be shown is controlled
     * by a {@link CardLayout}.
     *
     * @param name The name of the components to show, either {@link #TABLE}, {@link #FILES}
     *             or {@link #CONTROLLER}.
     */
    final void setSelectionPanel(final String name) {
        ((CardLayout) selectionPanel.getLayout()).show(selectionPanel, name);
        selectionPanelName = name;
        toolbar.setButtonsEnabled(TABLE.equals(name));
        switch (name) {
            case TABLE: {
                final ListSelectionModel model = table.getSelectionModel();
                setImageProperties(model.isSelectionEmpty() ? null :
                        coverages.getCoverageReferenceAt(model.getAnchorSelectionIndex()));
                break;
            }
            case FILES: {
                properties.setImageLater(fileChooser.getSelectedFile());
                break;
            }
        }
    }

    /**
     * Ensures that the file chooser is ready to be shown. If the user confirm
     * his selection, then {@link #selectVariables()} will be invoked later.
     */
    private void showFileChooser() {
        final Layer layer = getLayer();
        if (layer != null) {
            if (fileChooser == null) {
                final SortedSet<String> formats;
                final SortedSet<File> directories;
                try {
                    formats = layer.getImageFormats();
                    directories = layer.getImageDirectories();
                } catch (CoverageStoreException e) {
                    ExceptionMonitor.show(this, e);
                    return;
                }
                fileChooser = new ImageFileChooser(formats.isEmpty() ? "png" : formats.first(), true);
                fileChooser.setDialogType(ImageFileChooser.OPEN_DIALOG);
                fileChooser.setPropertiesPane(properties);
                fileChooser.addActionListener(listeners);
                for (final File directory : directories) {
                    if (directory.isDirectory()) {
                        fileChooser.setCurrentDirectory(directory);
                        break;
                    }
                }
                selectionPanel.add(new JXTitledPanel(Widgets.getResources(getLocale())
                        .getString(Widgets.Keys.SELECT_FILE), fileChooser), FILES);
                /*
                 * Creates the busy panel.
                 */
                busyLabel = new JXBusyLabel(new Dimension(60, 60));
                busyLabel.setHorizontalAlignment(JXBusyLabel.CENTER);
                selectionPanel.add(busyLabel, BUZY);
            }
            setSelectionPanel(FILES);
        }
    }

    /**
     * Potentially invoked after {@link #addNewCoverage()} started its work. This method shows
     * a list of available images in the file.
     *
     * @since 3.15
     */
    final void showVariableChooser(final String[] images, final boolean multiSelectionAllowed) {
        if (variableChooser == null) {
            variableChooser = new JList<>();
            final Vocabulary resources = Vocabulary.getResources(getLocale());
            final JButton ok     = new JButton(resources.getString(Vocabulary.Keys.OK));
            final JButton cancel = new JButton(resources.getString(Vocabulary.Keys.CANCEL));
            final JPanel buttons = new JPanel(new GridLayout(1, 2, 6, 0));
            ok    .addActionListener(addController);
            cancel.addActionListener(addController);
            ok    .setActionCommand(NewGridCoverageDetails.SELECT_VARIABLES);
            cancel.setActionCommand(NewGridCoverageDetails.CANCEL);
            buttons.add(ok);
            buttons.add(cancel);
            buttons.setOpaque(false);
            buttons.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            final Box buttonBar = Box.createHorizontalBox();
            buttonBar.add(Box.createHorizontalGlue());
            buttonBar.add(buttons);
            buttonBar.add(Box.createHorizontalGlue());
            buttonBar.setOpaque(false);
            final JPanel panel = new JPanel(new BorderLayout());
            panel.add(variableChooser, BorderLayout.CENTER);
            panel.add(buttonBar, BorderLayout.AFTER_LAST_LINE);
            selectionPanel.add(new JXTitledPanel(Widgets.getResources(getLocale())
                    .getString(Widgets.Keys.SELECT_VARIABLES), panel), VARIABLES);
        }
        variableChooser.setListData(images);
        variableChooser.setSelectionMode(multiSelectionAllowed ?
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION :
                ListSelectionModel.SINGLE_SELECTION);
        setSelectionPanel(VARIABLES);
    }

    /**
     * Invoked after {@link #showVariableChooser(String[], boolean)} in order to get the
     * variables selected by the user. A side effect of this method is to show again the
     * busy panel, since this method is invoked when {@link #addNewCoverage()} is about
     * to continue its work.
     *
     * @since 3.15
     */
    final List<String> getSelectedVariables() {
        setSelectionPanel(BUZY);
        return variableChooser.getSelectedValuesList();
    }

    /**
     * Invoked when the user confirmed his selection in the file chooser. If the user selected
     * a file, then the {@link NewGridCoverageDetails} window will be shown for each file to be
     * added in the database.
     */
    private void addNewCoverage() {
        setSelectionPanel(BUZY);
        busyLabel.setBusy(true);
        /*
         * If the user confirmed his selection, create the controller (if not already
         * done), then starts the image addition process in a background thread.
         */
        final File[] files = fileChooser.getSelectedFiles();
        if (files != null && files.length != 0) {
            if (addController == null) {
                CRSAuthorityFactory factory = null;
                final CoverageDatabase database = layer.getCoverageDatabase();
                if (database != null) try {
                    factory = database.getCRSAuthorityFactory();
                } catch (FactoryException e) {
                    exceptionOccured(e);
                }
                if (factory == null) {
                    factory = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", null);
                }
                addController = new NewGridCoverageDetails(this, factory);
                selectionPanel.add(addController, CONTROLLER);
            }
            /*
             * Runs a worker in a background thread for adding the new coverages.
             * We don't use the SwingWorker because NewGridCoverageDetails will
             * block waiting for user input from the event thread, and it seems
             * to prevent other SwingWorkers to work.
             *
             * Note: don't use Threads.executeWork(...) neither, since it is designed
             * for short-lived task (running this task could block other tasks if the
             * thread pool is full).
             */
            new Thread(SwingUtilities.THREAD_GROUP, new Runnable() {
                @Override public void run() {
                    try {
                        layer.addCoverageReferences(Arrays.asList(files), addController);
                    } catch (DatabaseVetoException e) {
                        // User cancelled the operation or closed the frame.
                        // Do not report the exception since it is intentional.
                    } catch (Exception e) {
                        exceptionOccured(e);
                    } finally {
                        EventQueue.invokeLater(new Runnable() {
                            @Override public void run() {
                                busyLabel.setBusy(false);
                                setSelectionPanel(TABLE);
                                refresh();
                            }
                        });
                    }
                }
            }, "CoverageList").start();
        }
    }

    /**
     * Invoked when the user pressed the "Remove" button.
     *
     * @todo Current implementation remove only the row from the JTable.
     *       It does not yet update the database.
     */
    private void removeCoverage() {
        coverages.remove(table.getSelectedRows());
    }

    /**
     * Invoked when an exception occurred while querying the {@linkplain Layer layer}.
     * The default implementation reports the error in an {@link ExceptionMonitor}.
     * Subclasses can override this method in order to report the error in a different way.
     *
     * @param ex The exception which occurred.
     */
    protected void exceptionOccured(final Exception ex) {
        ExceptionMonitor.show(this, ex);
    }
}
