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
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.image.RenderedImage;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.coverage.sql.Layer;
import org.geotoolkit.coverage.sql.CoverageTableModel;
import org.geotoolkit.coverage.sql.CoverageEnvelope;
import org.geotoolkit.coverage.sql.GridCoverageReference;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.gui.swing.image.ImageFileProperties;
import org.geotoolkit.gui.swing.ExceptionMonitor;


/**
 * A list displaying the {@linkplain Layer#getCoverageReferences(CoverageEnvelope) set of
 * coverages} available in a given layer. This widget displays also the properties of the
 * selected file in the right side.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @see CoverageTableModel
 *
 * @since 3.11 (derived from Seagis)
 * @module
 */
@SuppressWarnings("serial")
public class CoverageList extends JPanel {
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
        super(new BorderLayout());
        this.coverages = coverages;
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
        add(BorderLayout.CENTER, pane);
    }

    /**
     * Implement all listeners used by the {@link LayerList} class.
     */
    private final class Listeners implements ListSelectionListener {
        /**
         * Invoked when a coverage has been selected.
         */
        @Override
        public void valueChanged(final ListSelectionEvent event) {
            final int coverageIndex = event.getFirstIndex();
            if (coverageIndex < 0) {
                return;
            }
            final GridCoverageReference reference = coverages.getCoverageReferenceAt(coverageIndex);
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
