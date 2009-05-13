/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.prefs.Preferences;
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

import org.geotoolkit.math.XMath;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.coverage.grid.ImageGeometry;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.gui.swing.Dialog;
import org.geotoolkit.gui.swing.ListTableModel;
import org.geotoolkit.internal.SwingUtilities;
import org.geotoolkit.internal.swing.FileField;
import org.geotoolkit.internal.swing.SizeFields;
import org.geotoolkit.internal.swing.LabeledTableCellRenderer;

import static org.geotoolkit.gui.swing.image.MosaicChooser.OUTPUT_FORMAT;
import static org.geotoolkit.gui.swing.image.MosaicChooser.OUTPUT_DIRECTORY;


/**
 * Configures a {@link MosaicBuilder} according the input provided by a user. The caller can
 * invoke one of the {@code initializeForXXX} methods (optional but recommanded) in order to
 * initialize the widgets with a set of default values. After the widget has been displayed,
 * the caller can invoke {@link #getMosaicBuilder()} in order to get the user's choices in
 * an object ready for use.
 * <p>
 * <b>Example:</b>
 *
 * {@preformat java
 *     MosaicBuilderEditor editor = new MosaicBuilderEditor();
 *     editor.initializeForBounds(boundsOfTheWholeMosaic);
 *     if (editor.showDialog(null, "Define pyramid tiling")) {
 *         MosaicBuilder builder = editor.getMosaicBuilder();
 *         // Process here.
 *     }
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@SuppressWarnings("serial")
public class MosaicBuilderEditor extends JPanel implements Dialog {
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
    protected final MosaicBuilder builder;

    /**
     * The subsampling selection.
     */
    private final List<Dimension> subsamplings = new ArrayList<Dimension>();

    /**
     * The size of output tiles.
     */
    private final SizeFields tileSize;

    /**
     * The target file format for writing tiles.
     */
    private final JComboBox format;

    /**
     * The output directory.
     */
    private final FileField directory;

    /**
     * A plot of the estimated cost of loading tiles at given resolution.
     */
    private final MosaicPerformanceGraph plot;

    /**
     * The progress in the calculation of the performance graph.
     */
    private final JProgressBar progress;

    /**
     * Creates a new panel for configuring a default mosaic builder.
     */
    public MosaicBuilderEditor() {
        this(new MosaicBuilder());
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
         * Determines the default values.
         */
        final Preferences prefs = Preferences.userNodeForPackage(MosaicBuilderEditor.class);
        File initialDirectory = builder.getTileDirectory();
        if (initialDirectory == null) {
            initialDirectory = new File(prefs.get(OUTPUT_DIRECTORY, System.getProperty("user.home", ".")));
        }
        ImageReaderSpi reader = builder.getTileReaderSpi();
        String preferredFormat;
        if (reader != null) {
            // FormatNames can not be a null or empty array according the method contract.
            preferredFormat = reader.getFormatNames()[0];
        } else {
            preferredFormat = prefs.get(OUTPUT_FORMAT, "png");
        }
        /*
         * The table where to specifies subsampling, together with a "Remove" botton for
         * removing rows. There is no "add" button given that subsampling can be added on
         * the last row.
         */
        final List<Dimension> subsamplings = this.subsamplings;
        final Subsamplings subsamplingModel = new Subsamplings(subsamplings, resources);
        final JTable subsamplingTable = new JTable(subsamplingModel);
        subsamplingTable.setDefaultRenderer(Integer.class, new LabeledTableCellRenderer.Numeric(locale, true));
        final JButton removeButton = new JButton(resources.getString(Vocabulary.Keys.REMOVE));
        removeButton.setEnabled(false);
        final class RemoveSubsampling implements ActionListener, ListSelectionListener {
            @Override public void actionPerformed(final ActionEvent event) {
                subsamplingModel.remove(subsamplingTable.getSelectedRows());
            }

            @Override public void valueChanged(final ListSelectionEvent event) {
                final int min = ((ListSelectionModel) event.getSource()).getMinSelectionIndex();
                removeButton.setEnabled(min >= 0 && min < subsamplings.size());
            }
        }
        final RemoveSubsampling removeAction = new RemoveSubsampling();
        removeButton.addActionListener(removeAction);
        subsamplingTable.getSelectionModel().addListSelectionListener(removeAction);
        JPanel subsamplingPane = new JPanel(new BorderLayout());
        subsamplingPane.add(new JScrollPane(subsamplingTable), BorderLayout.CENTER);
        subsamplingPane.add(removeButton, BorderLayout.SOUTH);
        subsamplingPane = new JXTitledPanel(resources.getString(Vocabulary.Keys.SUBSAMPLING), subsamplingPane);
        /*
         * The panel where to select the tile size, file format and the output directory.
         */
        tileSize = new SizeFields(locale, DEFAULT_TILE_SIZE);
        {
            final Set<ImageFormatEntry> preferredEntries = new LinkedHashSet<ImageFormatEntry>();
            format = new JComboBox(ImageFormatEntry.list(preferredFormat, preferredEntries));
            final Iterator<ImageFormatEntry> it = preferredEntries.iterator();
            if (it.hasNext()) {
                format.setSelectedItem(it.next());
            }
        }
        format.setBorder(BorderFactory.createTitledBorder(resources.getString(Vocabulary.Keys.FORMAT)));
        directory = new FileField(locale, null, true);
        directory.setFile(initialDirectory);
        directory.setBorder(BorderFactory.createTitledBorder(resources.getString(Vocabulary.Keys.OUTPUT_DIRECTORY)));
        final JLabel explain = new JLabel(); // No purpose other than fill space at this time.
        /*
         * Assembles the control panel which is on the right side of the subsampling table.
         */
        final JPanel controlPane = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.insets.bottom=9;
        c.weightx=1; c.fill=GridBagConstraints.HORIZONTAL;
        c.gridx=0; c.anchor=GridBagConstraints.LINE_START;
        c.gridy=0; controlPane.add(tileSize,  c);
        c.gridy++; controlPane.add(format,    c);
        c.gridy++; controlPane.add(directory, c); c.weighty=1; c.fill=GridBagConstraints.BOTH;
        c.gridy++; controlPane.add(explain, c);
        /*
         * Creates the panel which will contains the plot of estimated performance.
         */
        plot = new MosaicPerformanceGraph();
        plot.setMargin(new Insets(15, 45, 45, 15));
        progress = new JProgressBar();
        progress.setEnabled(false);
        plot.setProgressBar(progress);
        final JPanel plotPanel = new JPanel(new BorderLayout());
        plotPanel.add(plot, BorderLayout.CENTER);
        plotPanel.add(progress, BorderLayout.SOUTH);
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
        initializeForBounds(null); // Sets default values inferred from the MosaicBuilder.
        /*
         * Adds listeners to be notified when a property that may affect the MosaicBuilder
         * changed. They will trig a repaint of the graph of estimated tiles loading cost.
         */
        final class Listener implements TableModelListener, ChangeListener {
            @Override public void tableChanged(final TableModelEvent event) {
                stateChanged(null);
            }

            @Override public void stateChanged(final ChangeEvent event) {
                plot.plotLater(null, getMosaicBuilder(), DELAY);
            }
        }
        final Listener listener = new Listener();
        subsamplingModel.addTableModelListener(listener);
        tileSize.addChangeListener(listener);
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
        /*
         * Searchs for a rectangle that encompass every tiles.
         */
        Rectangle bounds = null;
        for (final TileManager manager : managers) {
            final ImageGeometry geom = manager.getGridGeometry();
            if (geom != null) {
                final Rectangle candidate = geom.getGridRange();
                if (bounds == null) {
                    bounds = candidate;
                } else {
                    bounds.add(candidate);
                }
            }
        }
        initializeForBounds(bounds);
    }

    /**
     * Proposes default values suitable for tiles in a mosaic of the given size.
     *
     * @param bounds The bounds of the whole mosaic.
     */
    public void initializeForBounds(final Rectangle bounds) {
        synchronized (getTreeLock()) {
            Dimension size;
            final Dimension[] sub;
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
                sub  = builder.getSubsamplings();
                size = builder.getTileSize();
            }
            if (size == null) {
                size = DEFAULT_TILE_SIZE;
            }
            tileSize.setSizeValue(size);
            subsamplings.clear();
            if (sub != null) {
                subsamplings.addAll(Arrays.asList(sub));
            } else {
                subsamplings.add(new Dimension(1,1));
            }
        }
    }

    /**
     * The table model for the subsamplings table.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.0
     *
     * @since 3.0
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
        Subsamplings(final List<Dimension> subsamplings, final Vocabulary resources) {
            super(Dimension.class, subsamplings);
            titles = new String[] {
                resources.getString(Vocabulary.Keys.LEVEL),
                resources.getString(Vocabulary.Keys.AXIS_$1, "x"),
                resources.getString(Vocabulary.Keys.AXIS_$1, "y")
            };
            Collections.sort(subsamplings, this);
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
            return XMath.sgn(areaSquared(s1) - areaSquared(s2));
        }

        /**
         * Returns the number of row, including the insertion row.
         */
        @Override
        public int getRowCount() {
            return elements.size() + 1;
        }

        /**
         * Returns the number of columns, which is 3 including the title column.
         */
        @Override
        public int getColumnCount() {
            return 3;
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
                        case 1: s.width  = n; break;
                        case 2: s.height = n; break;
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
     * Configures the mosaic {@linkplain #builder} with the informations provided by the user
     * and returns it.
     *
     * @return The configured mosaic builder.
     */
    public MosaicBuilder getMosaicBuilder() {
        final File directory;
        final Dimension tileSize;
        final Dimension[] sub;
        final ImageFormatEntry tileFormat;
        synchronized (getTreeLock()) {
            directory  = this.directory.getFile();
            tileFormat = (ImageFormatEntry) format.getSelectedItem();
            tileSize   = this.tileSize.getSizeValue();
            sub        = subsamplings.toArray(new Dimension[subsamplings.size()]);
        }
        final Preferences prefs = Preferences.userNodeForPackage(MosaicBuilderEditor.class);
        prefs.put(OUTPUT_FORMAT, tileFormat.getFormat());
        prefs.put(OUTPUT_DIRECTORY, directory.getPath());
        final MosaicBuilder builder = this.builder;
        synchronized (builder) {
            builder.setTileDirectory(directory);
            builder.setTileSize(tileSize);
            builder.setSubsamplings(sub);
            builder.setTileReaderSpi(tileFormat.getReader());
        }
        return builder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean showDialog(final Component owner, final String title) {
        return SwingUtilities.showOptionDialog(owner, this, title);
    }
}
