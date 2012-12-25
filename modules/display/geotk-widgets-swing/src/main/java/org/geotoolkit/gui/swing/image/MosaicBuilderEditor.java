/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.gui.swing.image;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.prefs.Preferences;
import java.text.ParseException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.spi.ImageReaderSpi;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingx.JXTitledPanel;

import org.apache.sis.math.MathFunctions;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.coverage.grid.ImageGeometry;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.ImageReaderAdapter;
import org.geotoolkit.gui.swing.Dialog;
import org.geotoolkit.gui.swing.ListTableModel;
import org.geotoolkit.internal.swing.SwingUtilities;
import org.geotoolkit.internal.swing.FileField;
import org.geotoolkit.internal.swing.SizeFields;
import org.geotoolkit.internal.swing.table.LabeledRenderer;

import static org.geotoolkit.gui.swing.image.MosaicChooser.OUTPUT_FORMAT;
import static org.geotoolkit.gui.swing.image.MosaicChooser.OUTPUT_DIRECTORY;


/**
 * Configures a {@link MosaicBuilder} according the input provided by a user. The caller can
 * invoke one of the one-argument constructors (optional but recommended) in order to initialize
 * the widgets with a set of default values. After the widget has been displayed, the caller can
 * invoke {@link #getTileManager()} in order to get the user's choices in an object ready for use.
 * <p>
 * <b>Example:</b>
 *
 * {@preformat java
 *     MosaicBuilderEditor editor = new MosaicBuilderEditor(boundsOfTheWholeMosaic);
 *     if (editor.showDialog(null, "Define pyramid tiling")) {
 *         TileManager mosaic = editor.getTileManager();
 *         // Process here.
 *     }
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.00
 * @module
 */
@SuppressWarnings("serial")
public class MosaicBuilderEditor extends JComponent implements MosaicPerformanceGraph.Delayed, Dialog {
    /**
     * The default tile size. If {@link MosaicBuilder} can not suggest a tile size,
     * we will use the size specified by the WMTS (<cite>Web Map Tile Service</cite>)
     * specification.
     */
    private static final Dimension DEFAULT_TILE_SIZE = new Dimension(256, 256);

    /**
     * The delay before to plot the graph, in milliseconds.
     */
    private static final int DELAY = 1000;

    /**
     * The mosaic builder to configure. This is the instance given to the constructor.
     * This builder may not be synchronized with the content of this widget - the
     * synchronization happen only when {@link #getMosaicBuilder()} is invoked.
     */
    private final MosaicBuilder builder;

    /**
     * The table model for the subsampling selection.
     */
    private final Subsamplings subsamplingTable;

    /**
     * The size of output tiles.
     */
    private final SizeFields sizeFields;

    /**
     * The target file format for writing tiles.
     */
    private final JComboBox<ImageFormatEntry> formatChoices;

    /**
     * The output directory.
     */
    private final FileField directoryField;

    /**
     * A plot of the estimated cost of loading tiles at given resolution.
     */
    private final MosaicPerformanceGraph plot;

    /**
     * The progress during the calculation of the performance graph.
     */
    private final JProgressBar progressBar;

    /**
     * Creates a new panel for configuring a default mosaic builder.
     */
    public MosaicBuilderEditor() {
        this(new MosaicBuilder());
    }

    /**
     * Creates a new panel suitable for tiles in a mosaic of the given size.
     *
     * @param bounds The bounds of the whole mosaic.
     */
    public MosaicBuilderEditor(final Rectangle bounds) {
        this(create(bounds));
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static MosaicBuilder create(final Rectangle bounds) {
        final MosaicBuilder builder = new MosaicBuilder();
        builder.setUntiledImageBounds(bounds);
        return builder;
    }

    /**
     * Creates a new panel suitable for the given tiles, specified as {@code TileManager} objects.
     * Only one tile manager is usually provided. However more managers can be provided if, for
     * example, {@link org.geotoolkit.image.io.mosaic.TileManagerFactory} failed to create only
     * one instance from a set of tiles.
     *
     * @param  managers The tiles for which to setup default values.
     * @throws IOException If an I/O operation was necessary and failed.
     */
    public MosaicBuilderEditor(final TileManager... managers) throws IOException {
        this(bounds(managers));
    }

    /**
     * Creates a new panel for configuring the given mosaic builder.
     *
     * @param builder The mosaic builder to be configured by this panel.
     */
    public MosaicBuilderEditor(final MosaicBuilder builder) {
        this.builder = builder;
        final Locale locale = getLocale();
        final Vocabulary resources = Vocabulary.getResources(locale);
        /*
         * Determines the default values. We fetch the values from the MosaicBuilder
         * if they are defined, or from the user's preferences otherwise.
         */
        File directory;
        Dimension tileSize, minSize;
        final String preferredFormat;
        final Dimension[] subsamplings;
        /* Block for reducing variable scope */ {
            final ImageReaderSpi reader;
            final Preferences prefs = Preferences.userNodeForPackage(MosaicBuilderEditor.class);
            synchronized (builder) {
                reader       = builder.getTileReaderSpi();
                directory    = builder.getTileDirectory();
                subsamplings = builder.getSubsamplings();
                tileSize     = builder.getTileSize();
            }
            if (reader != null) {
                // FormatNames can not be a null or empty array according the method contract.
                preferredFormat = reader.getFormatNames()[0];
            } else {
                preferredFormat = prefs.get(OUTPUT_FORMAT, "png");
            }
            if (directory == null) {
                directory = new File(prefs.get(OUTPUT_DIRECTORY, System.getProperty("user.home", ".")));
            }
            if (tileSize == null) {
                tileSize = DEFAULT_TILE_SIZE;
            }
            /*
             * A minimal size is essential, because too small size will cause too many tiles
             * to be created, which cause a OutOfMemoryError.
             */
            minSize = new Dimension(256, 256);
        }
        /*
         * The table where to specifies subsampling, together with a "Remove" botton for
         * removing rows. There is no "add" button given that subsampling can be added on
         * the last row.
         */
        subsamplingTable = new Subsamplings(resources);
        subsamplingTable.setElements(subsamplings);
        final JTable subsamplingTable = new JTable(this.subsamplingTable);
        subsamplingTable.setDefaultRenderer(Integer.class, new LabeledRenderer.Numeric(locale, true));
        final JButton removeButton = new JButton(resources.getString(Vocabulary.Keys.REMOVE));
        removeButton.setEnabled(false);
        JPanel subsamplingPane = new JPanel(new BorderLayout());
        subsamplingPane.add(new JScrollPane(subsamplingTable), BorderLayout.CENTER);
        subsamplingPane.add(removeButton, BorderLayout.SOUTH);
        subsamplingPane = new JXTitledPanel(resources.getString(Vocabulary.Keys.SUBSAMPLING), subsamplingPane);
        /*
         * The panel where to select the tile size, file format and the output directory.
         */
        sizeFields = new SizeFields(locale, DEFAULT_TILE_SIZE, minSize);
        sizeFields.setSizeValue(tileSize);
        formatChoices = ImageFormatEntry.comboBox(preferredFormat);
        final JPanel formatPanel = new JPanel(new BorderLayout());
        formatPanel.add(formatChoices, BorderLayout.CENTER);
        formatPanel.setBorder(BorderFactory.createTitledBorder(resources.getString(Vocabulary.Keys.FORMAT)));
        directoryField = new FileField(locale, null, true);
        directoryField.setFile(directory);
        directoryField.setBorder(BorderFactory.createTitledBorder(resources.getString(Vocabulary.Keys.OUTPUT_DIRECTORY)));
        final JLabel explain = new JLabel(); // No purpose other than fill space at this time.
        /*
         * Assembles the control panel which is on the right side of the subsampling table.
         */
        final JPanel controlPane = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.insets.bottom=9;
        c.weightx=1; c.fill=GridBagConstraints.HORIZONTAL;
        c.gridx=0; c.anchor=GridBagConstraints.LINE_START;
        c.gridy=0; controlPane.add(sizeFields, c);
        c.gridy++; controlPane.add(formatPanel, c);
        c.gridy++; controlPane.add(directoryField, c); c.weighty=1; c.fill=GridBagConstraints.BOTH;
        c.gridy++; controlPane.add(explain, c);
        /*
         * Creates the panel which will contains the plot of estimated performance.
         */
        plot = new MosaicPerformanceGraph();
        plot.setMargin(new Insets(15, 50, 45, 15));
        progressBar = new JProgressBar();
        progressBar.setEnabled(false);
        plot.setProgressBar(progressBar);
        final JPanel plotPanel = new JPanel(new BorderLayout());
        plotPanel.add(plot, BorderLayout.CENTER);
        plotPanel.add(progressBar, BorderLayout.SOUTH);
        /*
         * Layout all the above components. The divider location has been determined
         * empirically for allowing the subsamplings columns to be fully visible.
         */
        final JPanel panel = new JPanel(new GridLayout(1, 2, 15, 9));
        panel.add(subsamplingPane);
        panel.add(controlPane);
        final JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, panel, plotPanel);
        sp.setDividerLocation(400);
        sp.setBorder(null);
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
        setPreferredSize(new Dimension(800, 300));
        /*
         * Adds listeners to be notified when a property that may affect the MosaicBuilder
         * changed. They will trig a repaint of the graph of estimated tiles loading efficiency.
         * Defines also listeners for controlling the removal of rows in the subsampling table.
         */
        final class Listener implements TableModelListener, ChangeListener, ActionListener, ListSelectionListener {
            /** Invoked when a subsampling value in the table has been edited. */
            @Override public void tableChanged(final TableModelEvent event) {
                plotEfficiency(DELAY);
            }

            /** Invoked when a tile size (width or height) value changed. */
            @Override public void stateChanged(final ChangeEvent event) {
                plotEfficiency(DELAY);
            }

            /** Invoked when the "Remove" button is pressed. */
            @Override public void actionPerformed(final ActionEvent event) {
                // The insertion row is not a "real" row and must be omitted.
                final int insertionRow = subsamplingTable.getModel().getRowCount() - 1;
                int[] selected = subsamplingTable.getSelectedRows();
                int count = 0;
                for (int i=0; i<selected.length; i++) {
                    final int row = selected[i];
                    if (row < insertionRow) {
                        selected[count++] = row;
                    }
                }
                selected = XArrays.resize(selected, count);
                ((Subsamplings) subsamplingTable.getModel()).remove(selected);
            }

            /** Invoked when the row selection in the subsampling table changed. */
            @Override public void valueChanged(final ListSelectionEvent event) {
                final int min = ((ListSelectionModel) event.getSource()).getMinSelectionIndex();
                removeButton.setEnabled(min >= 0 && min < subsamplingTable.getModel().getRowCount() - 1);
            }
        }
        final Listener listener = new Listener();
        this.subsamplingTable.addTableModelListener(listener);
        this.sizeFields.addChangeListener(listener);
        removeButton.addActionListener(listener);
        subsamplingTable.getSelectionModel().addListSelectionListener(listener);
        plotEfficiency(0);
    }

    /**
     * Proposes default values suitable for the given tiles, specified as {@code TileManager}
     * objects. Only one tile manager is usually provided. However more managers can be provided
     * if, for example, {@link org.geotoolkit.image.io.mosaic.TileManagerFactory} failed to create
     * only one instance from a set of tiles.
     *
     * @param managers The tiles for which to setup default values.
     * @throws IOException If an I/O operation was necessary and failed.
     */
    public void initializeForTiles(final TileManager... managers) throws IOException {
        initializeForBounds(bounds(managers));
    }

    /**
     * Searches for a rectangle that encompass every tiles.
     */
    private static Rectangle bounds(final TileManager... managers) throws IOException {
        Rectangle bounds = null;
        for (final TileManager manager : managers) {
            final ImageGeometry geom = manager.getGridGeometry();
            if (geom != null) {
                final Rectangle candidate = geom.getExtent();
                if (bounds == null) {
                    bounds = candidate;
                } else {
                    bounds.add(candidate);
                }
            }
        }
        return bounds;
    }

    /**
     * Proposes default values suitable for tiles in a mosaic of the given size.
     *
     * @param bounds The bounds of the whole mosaic.
     */
    public void initializeForBounds(final Rectangle bounds) {
        synchronized (getTreeLock()) {
            Dimension tileSize;
            final Dimension[] subsamplings;
            final MosaicBuilder builder = this.builder;
            synchronized (builder) {
                /*
                 * If a region was found, discard the values previously set and give the new region
                 * to the TileBuilder. Then asks for the default values proposed by the builder
                 */
                if (bounds != null) {
                    builder.setTileSize(null);
                    builder.setSubsamplings((Dimension[]) null);
                    builder.setUntiledImageBounds(bounds);
                }
                subsamplings = builder.getSubsamplings();
                tileSize = builder.getTileSize();
            }
            if (tileSize == null) {
                tileSize = DEFAULT_TILE_SIZE;
            }
            sizeFields.setSizeValue(tileSize);
            subsamplingTable.setElements(subsamplings);
        }
    }

    /**
     * The table model for the subsamplings table.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    private static final class Subsamplings extends ListTableModel<Dimension> implements Comparator<Dimension> {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 4366921097769025343L;

        /**
         * Localized column titles.
         */
        private final String[] titles;

        /**
         * Creates a default set of subsampling values.
         */
        Subsamplings(final Vocabulary resources) {
            super(Dimension.class, new ArrayList<Dimension>());
            titles = new String[] {
                resources.getString(Vocabulary.Keys.LEVEL),
                resources.getString(Vocabulary.Keys.AXIS_$1, "x"),
                resources.getString(Vocabulary.Keys.AXIS_$1, "y")
            };
            Collections.sort(elements, this);
        }

        /**
         * Returns the square of the area of a rectangle of the given size.
         */
        private static long areaSquared(final Dimension size) {
            long s;
            return ((s = size.width) * s) + ((s = size.height) * s);
        }

        /**
         * Compares the given subsamplings for order. This is used for keeping the
         * subsampling list in increasing order.
         */
        @Override
        public int compare(final Dimension s1, final Dimension s2) {
            return MathFunctions.sgn(areaSquared(s1) - areaSquared(s2));
        }

        /**
         * Replaces all current values by the given ones.
         */
        @Override
        public void setElements(final Dimension... sub) {
            elements.clear();
            if (sub != null) {
                elements.addAll(Arrays.asList(sub));
            } else {
                elements.add(new Dimension(1,1));
            }
            fireTableDataChanged();
        }

        /**
         * Overrides the method inherited from the subclass in order to execute it from the
         * current thread rather than the Swing thread. This is required in order to avoid
         * deadlock. Should be okay since this method is invoked inside a block synchronized
         * on the AWT tree lock.
         */
        @Override
        public Dimension[] getElements() {
            return elements.toArray(new Dimension[elements.size()]);
        }

        /**
         * Returns the number of row, including the insertion row.
         */
        @Override
        public int getRowCount() {
            return elements.size() + 1;
        }

        /**
         * Returns the number of columns, which is 3 including the header column.
         */
        @Override
        public int getColumnCount() {
            return titles.length;
        }

        /**
         * Returns the name of the given column.
         */
        @Override
        public String getColumnName(final int column) {
            return titles[column];
        }

        /**
         * Returns {@code Integer.class} regardless of the column index.
         */
        @Override
        public Class<Integer> getColumnClass(final int columnIndex) {
            return Integer.class;
        }

        /**
         * Returns {@code true} for the columns that are not the header columns.
         */
        @Override
        public boolean isCellEditable(final int rowIndex, final int columnIndex) {
            return columnIndex != 0;
        }

        /**
         * Returns the value in the given cell.
         */
        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            if (columnIndex == 0) {
                return rowIndex + 1;
            }
            if (rowIndex < elements.size()) {
                final Dimension size = elements.get(rowIndex);
                switch (columnIndex) {
                    case 1:  return size.width;
                    case 2:  return size.height;
                }
            }
            return null; // Insertion row.
        }

        /**
         * Sets the value in the given cell. If the value is added in the insertion row,
         * the same value is added for both <var>x</var> and <var>y</var> axes, which is
         * usually the desired behavior.
         */
        @Override
        public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
            if (value != null) {
                final Dimension s;
                final int n = (Integer) value;
                if (rowIndex < elements.size()) {
                    s = elements.get(rowIndex);
                    switch (columnIndex) {
                        case 1: if (s.width  == n) return; else s.width  = n; break;
                        case 2: if (s.height == n) return; else s.height = n; break;
                    }
                } else {
                    s = new Dimension(n, n);
                    elements.add(s);
                }
                /*
                 * Sorts the subsamplings in increasing order and fires a change event for the
                 * whole table only if the position of the new subsampling changed as a result
                 * of this operation. We test only the new subsampling because the other ones
                 * are already sorted, so they should not move if the edited record did not moved.
                 */
                Collections.sort(elements, this);
                if (elements.get(rowIndex) == s) {
                    fireTableCellUpdated(rowIndex, columnIndex);
                } else {
                    fireTableRowsUpdated(0, elements.size());
                }
            }
        }
    }

    /**
     * Refreshes the plot of estimated efficiency. This method is invoked automatically when
     * the values of some fields changed. The default implementation starts the calculation
     * in a background thread.
     *
     * @param delay How long to wait (in milliseconds) before to perform the calculation.
     */
    protected void plotEfficiency(final long delay) {
        plot.plotLater(null, this, delay);
    }

    /**
     * Configures the {@code MosaicBuilder} with the informations provided by the user
     * and return it.
     *
     * {@note Use this method when the widget state will not change anymore. If the user is still
     * editing the values in the widget, then invoking <code>getTileManager()</code> is preferable
     * than <code>getTileBuilder().getTileManager()</code> for synchronization reasons.}
     *
     * @return The configured mosaic builder.
     * @throws IOException if an I/O operation was required and failed.
     */
    public MosaicBuilder getMosaicBuilder() throws IOException {
        getTileManager(false);
        return builder;
    }

    /**
     * Configures the {@code MosaicBuilder} with the informations provided by the user
     * and create the mosaic. This method is automatically invoked when a graph is about to be
     * plot. It can also be invoked directly by the user, but may block if the builder is
     * currently in use by an other thread.
     *
     * @return The selected tiles as a {@code TileManager} object.
     * @throws IOException if an I/O operation was required and failed.
     */
    @Override
    public TileManager getTileManager() throws IOException {
        return getTileManager(true);
    }

    /**
     * Implementation of {@link #getTileManager} when the last step (the invocation of
     * {@link MosaicBuilder#createTileManager()}) is disabled if {@code run} is {@code false}.
     * This method exists because we want {@code builder.createTileManager()} to be invoked in
     * the same synchronization block than the one that configured the builder.
     */
    private TileManager getTileManager(final boolean run) throws IOException {
        final File directory;
        final Dimension tileSize;
        final Dimension[] subsamplings;
        final ImageFormatEntry tileFormat;
        synchronized (getTreeLock()) {
            directory    = directoryField.getFile();
            tileFormat   = (ImageFormatEntry) formatChoices.getSelectedItem();
            tileSize     = sizeFields.getSizeValue();
            subsamplings = subsamplingTable.getElements();
        }
        final Preferences prefs = Preferences.userNodeForPackage(MosaicBuilderEditor.class);
        prefs.put(OUTPUT_FORMAT, tileFormat.getFormat());
        prefs.put(OUTPUT_DIRECTORY, directory.getPath());
        final MosaicBuilder builder = this.builder;
        synchronized (builder) {
            builder.setTileDirectory(directory);
            builder.setTileSize(tileSize);
            builder.setSubsamplings(subsamplings);
            builder.setTileReaderSpi(ImageReaderAdapter.Spi.unwrap(tileFormat.getReader()));
            return run ? builder.createTileManager() : null;
        }
    }

    /**
     * Notifies that a {@link TileManager} has been created from the parameter edited in this widget.
     * This method is invoked automatically after the fields in this widget has been edited.
     * It can also be invoked directly by the user. Current implementation does nothing, but
     * subclasses can override this method for remembering the {@code TileManager}.
     *
     * @param mosaic The mosaic created from the information provided in this widget,
     *        or {@code null} if the {@code TileManager} creation has been canceled
     *        before completion.
     */
    @Override
    public void done(TileManager mosaic) {
    }

    /**
     * Notifies that the creation of a {@link TileManager} failed with the given exception. This
     * method is invoked instead than {@link #done(TileManager)} if an exception occurred during
     * the execution of {@link MosaicPerformanceGraph#plotEfficiency(String, TileManager)}.
     * <p>
     * The default implementation does nothing. Subclasses can override this method in order
     * to report the error in the way that best suite their application.
     *
     * @param exception The exception which occurred.
     */
    @Override
    public void failed(Throwable exception) {
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.12
     */
    @Override
    public void commitEdit() throws ParseException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showDialog(final Component owner, final String title) {
        return SwingUtilities.showDialog(owner, this, title);
    }
}
