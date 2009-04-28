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

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import org.jdesktop.swingx.JXTitledPanel;

import org.geotoolkit.math.XMath;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.coverage.grid.ImageGeometry;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.internal.swing.FileField;
import org.geotoolkit.internal.swing.SizeFields;
import org.geotoolkit.internal.swing.LabeledTableCellRenderer;


/**
 * Configures a {@link MosaicBuilder} according the input provided by a user.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@SuppressWarnings("serial")
public class MosaicBuilderEditor extends JPanel {
    /**
     * The mosaic builder to configure.
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
     * The output directory.
     */
    private final FileField directory;

    /**
     * A view of the mosaic to be created.
     */
    private final MosaicPanel mosaic;

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
        subsamplings.add(new Dimension(1,1));
        final Vocabulary resources = Vocabulary.getResources(locale);
        /*
         * The table where to specifies subsampling.
         */
        final JTable subsamplingTable = new JTable(new Subsamplings(subsamplings, resources));
        subsamplingTable.setDefaultRenderer(Integer.class, new LabeledTableCellRenderer.Numeric(locale, true));
        final JPanel subsamplingPane = new JXTitledPanel(
                resources.getString(Vocabulary.Keys.SUBSAMPLING), new JScrollPane(subsamplingTable));
        /*
         * The panel where to select the tile size and the output directory.
         */
        Dimension size = builder.getTileSize();
        if (size == null) {
            size = new Dimension(256, 256); // Default tile size.
        }
        tileSize = new SizeFields(locale, size);
        directory = new FileField(locale, null, true);
        directory.setBorder(BorderFactory.createTitledBorder(resources.getString(Vocabulary.Keys.OUTPUT_DIRECTORY)));
        final JLabel explain = new JLabel(); // No purpose other than fill space at this time.
        /*
         * Assembles the control panel which is on the right side of the subsampling table.
         */
        final JPanel controlPane = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.insets.bottom=9;
        c.weightx=1; c.fill=GridBagConstraints.HORIZONTAL;
        c.gridx=0; c.anchor = GridBagConstraints.LINE_START;
        c.gridy=0; controlPane.add(tileSize,  c);
        c.gridy++; controlPane.add(directory, c); c.weighty=1; c.fill=GridBagConstraints.BOTH;
        c.gridy++; controlPane.add(explain, c);
        /*
         * Layout all the above components.
         */
        mosaic = new MosaicPanel();
        final JPanel panel = new JPanel(new GridLayout(1, 2, 15, 9));
        panel.add(subsamplingPane);
        panel.add(controlPane);
        final JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, panel, mosaic);
        sp.setDividerLocation(MosaicPanel.LEFT_PANEL_SIZE);
        sp.setBorder(null);
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);
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
    public void setDefaultValues(final TileManager... managers) throws IOException {
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
        /*
         * If a region was found, discard the values previously set and give the new region
         * to the TileBuilder. Then asks for the default values proposed by the builder
         */
        if (bounds != null) {
            builder.setTileSize(null);
            builder.setUntiledImageBounds(bounds);
        }
        Dimension size = builder.getTileSize();
        if (size == null) {
            size = new Dimension(256, 256); // Default tile size.
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
    private static final class Subsamplings extends AbstractTableModel implements Comparator<Dimension> {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 4366921097769025343L;

        /**
         * The subsampling selection.
         */
        private final List<Dimension> subsamplings;

        /**
         * Localized column titles.
         */
        private final String[] titles;

        /**
         * Creates a default set of subsampling values.
         */
        Subsamplings(final List<Dimension> subsamplings, final Vocabulary resources) {
            titles = new String[] {
                resources.getString(Vocabulary.Keys.LEVEL),
                resources.getString(Vocabulary.Keys.AXIS_$1, "x"),
                resources.getString(Vocabulary.Keys.AXIS_$1, "y")
            };
            this.subsamplings = subsamplings;
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
            return subsamplings.size() + 1;
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
            if (rowIndex < subsamplings.size()) {
                switch (columnIndex) {
                    case 1:  return subsamplings.get(rowIndex).width;
                    case 2:  return subsamplings.get(rowIndex).height;
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
                if (rowIndex < subsamplings.size()) {
                    s = subsamplings.get(rowIndex);
                    switch (columnIndex) {
                        case 1: s.width  = n; break;
                        case 2: s.height = n; break;
                    }
                } else {
                    s = new Dimension(n, n);
                    subsamplings.add(s);
                }
                /*
                 * Sorts the subsamplings in increasing order and fires a change event
                 * only if the position of the new subsampling changed as a result of
                 * this operation. We test only the new subsampling because the other
                 * ones are already sorted, so they should not move if the edit record
                 * did not moved.
                 */
                Collections.sort(subsamplings, this);
                if (subsamplings.get(rowIndex) != s) {
                    fireTableRowsUpdated(0, subsamplings.size());
                }
            }
        }
    }
}
