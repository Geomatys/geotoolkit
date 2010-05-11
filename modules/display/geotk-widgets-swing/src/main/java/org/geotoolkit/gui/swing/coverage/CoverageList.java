/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
import java.util.Locale;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.coverage.sql.Layer;
import org.geotoolkit.coverage.sql.CoverageTableModel;
import org.geotoolkit.coverage.sql.CoverageEnvelope;
import org.geotoolkit.coverage.sql.CoverageDatabase;
import org.geotoolkit.coverage.sql.GridCoverageReference;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.gui.swing.image.ImageFileProperties;
import org.geotoolkit.gui.swing.image.ImageFileChooser;
import org.geotoolkit.gui.swing.ExceptionMonitor;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.factory.AuthorityFactoryFinder;


/**
 * A list displaying the {@linkplain Layer#getCoverageReferences(CoverageEnvelope) set of
 * coverages} available in a given layer. This widget displays also the properties of the
 * selected file on the right side.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @see CoverageTableModel
 *
 * @since 3.11 (derived from Seagis)
 * @module
 */
@SuppressWarnings("serial")
public class CoverageList extends JComponent {
    /**
     * Action commands.
     */
    private static final String ADD="ADD", REMOVE="REMOVE";

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
    private final ImageFileProperties properties;

    /**
     * The panel for adding new files. Will be created only when first needed.
     */
    private NewGridCoverageDetails addController;

    /**
     * The button for removing entries. To be enabled only when at least
     * one entry is selected.
     */
    private final JButton removeButton;

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
        setLayout(new BorderLayout());
        this.coverages = coverages;
        final Locale locale = getLocale();
        final Vocabulary resources = Vocabulary.getResources(locale);
        final Listeners listeners = new Listeners();

        final JTable table = new JTable(coverages);
        final TableCellRenderer renderer = new CoverageTableModel.CellRenderer();
        table.setDefaultRenderer(String.class, renderer);
        table.setDefaultRenderer(Date.class,   renderer);
        table.getSelectionModel().addListSelectionListener(listeners);

        properties = new ImageFileProperties();
        properties.setPreferredSize(new Dimension(440, 400));
        final JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), properties);
        pane.setOneTouchExpandable(true);
        pane.setContinuousLayout(true);
        pane.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));
        /*
         * The buttons bar.
         */
        final JButton addButton;
        addButton    = new JButton(resources.getString(Vocabulary.Keys.ADD));
        removeButton = new JButton(resources.getString(Vocabulary.Keys.REMOVE));

        addButton.setActionCommand(ADD);
        addButton.addActionListener(listeners);
        removeButton.setActionCommand(REMOVE);
        removeButton.addActionListener(listeners);
        removeButton.setEnabled(false);

        final JPanel buttonBar = new JPanel(new GridLayout(1, 2));
        buttonBar.setOpaque(false);
        buttonBar.add(addButton);
        buttonBar.add(removeButton);
        final Box b = Box.createHorizontalBox();
        b.add(Box.createHorizontalGlue());
        b.add(buttonBar);
        b.add(Box.createHorizontalGlue());
        b.setBorder(BorderFactory.createEmptyBorder(9, 15, 9, 15));
        /*
         * Put the components in this panel.
         */
        add(BorderLayout.CENTER, pane);
        add(b, BorderLayout.AFTER_LAST_LINE);
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
            if (isEmpty) {
                properties.setImage((RenderedImage) null);
                last = null;
                return;
            }
            final int coverageIndex = model.getAnchorSelectionIndex();
            final GridCoverageReference reference = coverages.getCoverageReferenceAt(coverageIndex);
            if (reference == last) {
                return;
            }
            last = reference;
            final Object input;
            try {
                final File file = reference.getFile(File.class);
                if (file.exists()) {
                    input = file;
                } else if (!file.isAbsolute()) {
                    input = reference.getFile(URL.class);
                } else {
                    properties.setImage((RenderedImage) null);
                    return;
                }
            } catch (IOException e) {
                exceptionOccured(e);
                return;
            }
            /*
             * Read the image-properties in a background thread. Ignore the IOException
             * if any - the default ImageFileProperties will paint it in its widget area.
             */
            final SwingWorker<Object,Object> worker = new SwingWorker<Object,Object>() {
                @Override protected Object doInBackground() throws IOException {
                    properties.setImageInput(input);
                    return null;
                }
            };
            worker.execute();
        }

        /**
         * Invoked when one of the buttons ("Remove", "Add", etc.) has been pressed.
         * This method delegates to the appropriate method in the encloding class.
         */
        @Override
        public void actionPerformed(final ActionEvent event) {
            final String action = event.getActionCommand();
            if (ADD.equals(action)) {
                addNewCoverage();
            } else if (REMOVE.equals(action)) {
                removeCoverage();
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
        if (!Utilities.equals(layer, this.layer)) {
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
        if (!Utilities.equals(envelope, this.envelope)) {
            setData(layer, envelope);
        }
    }

    /**
     * Sets the content of this widget to the list of coverages in the given layer wich
     * insersect the given envelope. This method combines {@link #setLayer(Layer)} and
     * {@link #setEnvelope(CoverageEnvelope)} in a single method call.
     * <p>
     * This method will fetch the list of coverage refererences in a background thread.
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
     * Invoked when the user pressed the "Add" button. This method shows a file chooser.
     * If the user confirms, then the {@link NewGridCoverageDetails} window will be show
     * for each file to append to the database.
     */
    final void addNewCoverage() {
        final Layer layer = getLayer();
        if (layer != null) try {
            final SortedSet<String> formats = layer.getImageFormats();
            final ImageFileChooser chooser = new ImageFileChooser(formats.isEmpty() ? "png" : formats.first(), true);
            if (chooser.showOpenDialog(this) == ImageFileChooser.APPROVE_OPTION) {
                final File[] files = chooser.getSelectedFiles();
                if (files != null && files.length != 0) {
                    if (addController == null) {
                        CRSAuthorityFactory factory = null;
                        final CoverageDatabase database = layer.getCoverageDatabase();
                        if (database != null) try {
                            factory = database.getCRSAuthorityFactory();
                        } catch (FactoryException e) {
                            ExceptionMonitor.show(this, e);
                        }
                        if (factory == null) {
                            factory = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", null);
                        }
                        addController = new NewGridCoverageDetails(this, factory);
                    }
                    layer.addCoverageReferences(Arrays.asList(files), addController);
                }
            }
        } catch (CoverageStoreException e) {
            ExceptionMonitor.show(this, e);
        }
    }

    /**
     * Invoked when the user pressed the "Remove" button.
     */
    final void removeCoverage() {
        // TODO
    }

    /**
     * Invoked when an exception occured while querying the {@linkplain Layer layer}.
     * The default implementation reports the error in an {@link ExceptionMonitor}.
     * Subclasses can override this method in order to report the error in a different way.
     *
     * @param ex The exception which occured.
     */
    protected void exceptionOccured(final Exception ex) {
        ExceptionMonitor.show(this, ex);
    }
}
