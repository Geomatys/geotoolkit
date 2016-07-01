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

import java.util.Locale;
import java.text.ParseException;

import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.IndexColorModel;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JColorChooser;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.colorchooser.ColorSelectionModel;

import org.geotoolkit.gui.swing.Dialog;
import org.geotoolkit.gui.swing.ListTableModel;
import org.geotoolkit.internal.swing.SwingUtilities;
import org.geotoolkit.internal.swing.table.LabeledRenderer;
import org.apache.sis.util.collection.IntegerList;
import org.geotoolkit.resources.Vocabulary;


/**
 * A chooser for an arbitrary amount of colors. The list of selected colors is displayed on the
 * left side and a {@linkplain JColorChooser color chooser} is displayed on the right side.
 * Only opaque colors can be selected in the current implementation.
 * <p>
 * This widget is typically used for creating an {@link IndexColorModel}, or (which is the same
 * in a more indirect way) the colors to be given to a {@link org.geotoolkit.coverage.Category}
 * object.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.00
 * @module
 */
@SuppressWarnings("serial")
public class MultiColorChooser extends JComponent implements Dialog {
    /**
     * The table of colors.
     */
    private final Colors colors;

    /**
     * Creates a new, initially empty, list of colors.
     */
    public MultiColorChooser() {
        setLayout(new BorderLayout());
        final Locale locale = getLocale();
        final Vocabulary resources = Vocabulary.getResources(locale);
        /*
         * Create the color chooser.
         */
        final JColorChooser chooser = new JColorChooser();
        /*
         * Create the list of selected colors.
         */
        final Colors colors = new Colors(resources);
        this.colors = colors;
        final JTable colorsTable = new JTable(colors);
        colorsTable.setDefaultRenderer(Color.class, new CellRenderer());
        colorsTable.setDefaultRenderer(Integer.class, new LabeledRenderer.Numeric(locale, true));
        colorsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        final JScrollPane scroll = new JScrollPane(colorsTable);
        final JLabel  title      = new JLabel (resources.getString(Vocabulary.Keys.SelectedColors), JLabel.CENTER);
        final JButton add        = new JButton(resources.getString(Vocabulary.Keys.Add));
        final JButton remove     = new JButton(resources.getString(Vocabulary.Keys.Remove));
        final JPanel  buttons    = new JPanel(new GridLayout(1,2));
        buttons.add(add);
        buttons.add(remove);
        final JPanel colorsPanel = new JPanel(new BorderLayout());
        scroll.setPreferredSize(new Dimension(160, 200));
        colorsPanel.add(title,   BorderLayout.NORTH);
        colorsPanel.add(scroll,  BorderLayout.CENTER);
        colorsPanel.add(buttons, BorderLayout.SOUTH);
        /*
         * Create the split pane which will contains the above pane.
         */
        final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, colorsPanel, chooser);
        split.setContinuousLayout(true);
        add(split, BorderLayout.CENTER);
        /*
         * Register the listeners.
         */
        final ColorSelectionModel select = chooser.getSelectionModel();
        final class Listeners implements ActionListener {
            @Override public void actionPerformed(final ActionEvent event) {
                if (event.getSource() == remove) {
                    colors.remove(colorsTable.getSelectedRows());
                } else {
                    colors.add(select.getSelectedColor());
                }
            }
        }
        final Listeners listeners = new Listeners();
        add.addActionListener(listeners);
        remove.addActionListener(listeners);
    }

    /**
     * Returns the selected colors, or an empty array if none.
     *
     * @return The selected colors.
     */
    public Color[] getSelectedColors() {
        return colors.getColors();
    }

    /**
     * The table model for the colors table. This table have two columns; the first one is the row
     * number and the second one is the RGB code in hexadecimal. The type of the second color is
     * {@link Color}, consequently the table needs the custom {@link CellRenderer} in order to
     * display the expected text in the cells.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    private static final class Colors extends ListTableModel<Integer> {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 9063526691205174844L;

        /**
         * Localized column titles.
         */
        private final String[] titles;

        /**
         * Creates a default set of subsampling values.
         */
        Colors(final Vocabulary resources) {
            super(Integer.class, new IntegerList(8, 0xFFFFFF));
            titles = new String[] {
                resources.getString(Vocabulary.Keys.Index),
                resources.getString(Vocabulary.Keys.Colors) + " (RGB)"
            };
        }

        /**
         * Adds the given color at the end of the current list.
         */
        final void add(final Color color) {
            final IntegerList elements = (IntegerList) this.elements;
            final int n = elements.size();
            elements.addInt(color.getRGB() & 0xFFFFFF);
            fireTableRowsInserted(n, n);
        }

        /**
         * Returns the colors in this table.
         */
        public Color[] getColors() {
            final IntegerList elements = (IntegerList) this.elements;
            final Color[] colors = new Color[elements.size()];
            for (int i=0; i<colors.length; i++) {
                colors[i] = new Color(elements.getInt(i));
            }
            return colors;
        }

        /**
         * Returns the number of row.
         */
        @Override
        public int getRowCount() {
            return elements.size();
        }

        /**
         * Returns the number of columns.
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
         * Returns the type of data in the given column.
         */
        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            return (columnIndex == 0) ? Integer.class : Color.class;
        }

        /**
         * Returns the value in the given cell.
         */
        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            if (columnIndex == 0) {
                return rowIndex + 1;
            }
            final IntegerList elements = (IntegerList) this.elements;
            return new Color(elements.getInt(rowIndex));
        }
    }

    /**
     * The renderer to use for columns of type {@link Color} in the the table of colors.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    private static final class CellRenderer extends DefaultTableCellRenderer {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -5368741385192560217L;

        /**
         * The font to use for the cells to be rendered.
         */
        private final Font font;

        /**
         * Creates a cell renderer for the color codes.
         */
        public CellRenderer() {
            setHorizontalAlignment(CENTER);
            font = Font.decode("Monospaced");
        }

        /**
         * Returns the renderer to use for rendering a cell.
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column)
        {
            final Color color = (Color) value;
            int ARGB = color.getRGB();
            final Color background, foreground;
            if (!isSelected && !hasFocus) {
                final int m = (ARGB & 0xFF) + ((ARGB >>> 8) & 0xFF) + ((ARGB >>> 16) & 0xFF);
                background = color;
                foreground = (m >= 0x180) ? Color.BLACK : Color.WHITE;
            } else {
                background = null;
                foreground = null;
            }
            setBackground(background);
            setForeground(foreground);
            /*
             * Formats the color as a hexadecimal string. At the difference of
             * Integer.toHexString(ARGB), we format exactly 6 digits no matter
             * the amount of leading zeros. We also insert spaces between R, G
             * and B components.
             */
            final StringBuilder buffer = new StringBuilder(9);
            for (int i=0; i<6; i++) {
                if (i == 2 || i == 4) {
                    buffer.insert(0, ' ');
                }
                char c = (char) (ARGB & 0xF);
                c += (c >= 10 ? ('A'-10) : '0');
                buffer.insert(0, c);
                ARGB >>>= 4;
            }
            value = buffer.toString();
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(font); // Must be invoked here because super.getTable... overwrites the font.
            return this;
        }
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
