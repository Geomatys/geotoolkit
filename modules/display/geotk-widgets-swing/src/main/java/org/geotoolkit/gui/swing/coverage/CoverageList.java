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

import java.util.Date;
import java.util.Locale;
import java.io.IOException;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import org.geotoolkit.coverage.sql.Layer;
import org.geotoolkit.coverage.sql.CoverageTableModel;
import org.geotoolkit.coverage.sql.CoverageEnvelope;
import org.geotoolkit.coverage.sql.GridCoverageReference;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.gui.swing.ExceptionMonitor;


/**
 * A list displaying the {@linkplain Layer#getCoverageReferences(CoverageEnvelope) set of
 * coverages} available in a given layer.
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
     * The spatio-temporal envelope to query, or {@code null} for the full coverage.
     */
    private CoverageEnvelope envelope;

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

        add(BorderLayout.CENTER, new JScrollPane(table));
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
            final GridCoverage2D coverage;
            try {
                coverage = reference.getCoverage(null);
            } catch (IOException ex) {
                exceptionOccured(ex);
                return;
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
        return coverages.getLayer();
    }

    /**
     * Sets the content of this widget to the list of coverages in the given layer.
     * This method will fetch the list of coverage refererences in a background thread.
     *
     * @param layer The layer for which to get the coverage references.
     */
    public void setLayer(final Layer layer) {
        final SwingWorker<Object,Object> worker = new SwingWorker<Object,Object>() {
            @Override protected Object doInBackground() {
                try {
                    coverages.setLayer(layer, envelope);
                } catch (CoverageStoreException ex) {
                    exceptionOccured(ex);
                }
                return null;
            }
        };
        worker.execute();
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
