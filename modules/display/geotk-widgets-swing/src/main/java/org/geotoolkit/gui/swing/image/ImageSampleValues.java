/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.RenderedImage;
import java.text.NumberFormat;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JSpinner;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.swing.SwingUtilities;


/**
 * Displays the image sample values as a table. This panel includes a field for band selection
 * and scroll bars for the table.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.16
 *
 * @see ImageTableModel
 * @see ImageProperties
 *
 * @since 2.3
 * @module
 */
@SuppressWarnings("serial")
public class ImageSampleValues extends JComponent {
    /**
     * The table which contains sample values.
     */
    private final JTable table;

    /**
     * The cell renderer for sample values.
     */
    private final CellRenderer renderer;

    /**
     * The model for band selection.
     */
    private final SpinnerNumberModel band;

    /**
     * An empty component for now. May contains geographic coordinates in a future version.
     * This is the only component in the "status bar" with a variable width.
     */
    private final JLabel comments;

    /**
     * The color for the selected cell.
     */
    private final CurrentColor current;

    /**
     * The table for displaying row headers.
     */
    @SuppressWarnings("serial")
    private static final class RowHeaders extends JTable {
        /**
         * The table which contains sample values.
         */
        private final JTable table;

        /**
         * Creates new row headers for the specified table.
         *
         * @param model The table model to use for the data of this header.
         * @param owner The main table which will use this object for its row header.
         */
        public RowHeaders(final ImageTableModel model, final JTable owner) {
            super(model.new RowHeaders());
            this.table = owner;
            SwingUtilities.setupAsRowHeader(this);
        }

        /**
         * Returns the row height, making sure it is identical to the main table.
         */
        @Override
        public int getRowHeight(final int row) {
            return table.getRowHeight(row);
        }
    }

    /**
     * The cell renderer for pixel values.
     */
    @SuppressWarnings("serial")
    private static final class CellRenderer extends DefaultTableCellRenderer {
        /**
         * The formatter for sample values, to be updated for each new image.
         */
        NumberFormat formatter;

        /**
         * Constructs a cell renderer.
         */
        public CellRenderer() {
            setHorizontalAlignment(RIGHT);
        }

        /**
         * Formats a cell value.
         */
        @Override
        public void setValue(final Object value) {
            final String text;
            if (value == null) {
                text = "";
            } else if (formatter == null) {
                text = value.toString();
            } else {
                text = formatter.format(value);
            }
            setText(text);
        }
    }

    /**
     * The component responsible for painting the color for the selected cell.
     * Also a listener for various events of interest for the enclosing class.
     */
    @SuppressWarnings("serial")
    private final class CurrentColor extends JPanel implements ChangeListener, ListSelectionListener {
        /**
         * The color to be displayed by this component.
         */
        private Color color;

        /**
         * Fills this component with a color inferred from the currently selected cell.
         */
        @Override
        public void paintComponent(final Graphics graphics) {
            if (color == null) {
                super.paintComponent(graphics);
                return;
            }
            final Color oldColor = graphics.getColor();
            graphics.setColor(color);
            graphics.fillRect(0, 0, getWidth(), getHeight());
            graphics.setColor(oldColor);
        }

        /**
         * Invoked when a new cell is selected.
         */
        @Override
        public void valueChanged(final ListSelectionEvent event) {
            if (!event.getValueIsAdjusting()) {
                final ImageTableModel samples = (ImageTableModel) table.getModel();
                final Color c=samples.getColorAt(table.getSelectedRow(), table.getSelectedColumn());
                if (!Objects.equals(c, color)) {
                    color = c;
                    repaint();
                }
            }
        }

        /**
         * Invoked when the band changed.
         */
        @Override
        public void stateChanged(final ChangeEvent event) {
            final ImageTableModel samples = (ImageTableModel) table.getModel();
            samples.setBand(band.getNumber().intValue() - 1);
        }
    }

    /**
     * Creates a new table without initial image to display.
     */
    public ImageSampleValues() {
        setLayout(new GridBagLayout());

        // Prepares the component showing comments (not yet used).
        comments = new JLabel();

        // Prepares the component showing the selected color.
        current = new CurrentColor();
        current.setBorder(BorderFactory.createLoweredBevelBorder());
        Dimension size = new Dimension(current.getMinimumSize());
        size.width = 60;
        current.setMinimumSize(size);
        current.setPreferredSize(size);

        // Prepares the table of sample values.
        final ImageTableModel model = new ImageTableModel();
        renderer = new CellRenderer();
        table = new JTable(model);
        table.setDefaultRenderer(Float .class, null);     // Remove JTable default renderer.
        table.setDefaultRenderer(Double.class, null);     // Remove JTable default renderer.
        table.setDefaultRenderer(Number.class, renderer); // Use same renderer for all numbers.
        table.setCellSelectionEnabled(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getSelectionModel().addListSelectionListener(current);
        table.getColumnModel().getSelectionModel().addListSelectionListener(current);

        // Prepares the scroll pane for the previous table, including row headers.
        final JScrollPane scroll = new JScrollPane(table);
        scroll.setRowHeaderView(new RowHeaders(model, table));

        // Prepares the component for selecting the band.
        band = new SpinnerNumberModel(1, 1, 1, 1);
        final JSpinner spinner = new JSpinner(band);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setColumns(3);
        band.addChangeListener(current);

        // Places all components on the panel.
        final Vocabulary resources = Vocabulary.getResources(getDefaultLocale());
        final GridBagConstraints c = new GridBagConstraints();
        c.gridy=1; c.insets.bottom=6;
        c.gridx=0; c.insets.left =9; add(new JLabel(resources.getLabel(Vocabulary.Keys.Band)), c);
        c.gridx=1; c.insets.left =0; add(spinner, c); c.fill=GridBagConstraints.BOTH;
        c.gridx=3; c.insets.right=9; add(current, c);
        c.gridx=2; c.weightx=1;
        c.insets.left = c.insets.right = 15; add(comments, c);
        c.gridx=0; c.weighty=1; c.gridwidth=GridBagConstraints.REMAINDER;
        c.gridy=0; c.insets.left = c.insets.right = 0;
        add(scroll, c);
    }

    /**
     * Creates a new table that will display the sample values for the specified image.
     *
     * @param image The image for which to display sample values, or {@code null} if none.
     */
    public ImageSampleValues(final RenderedImage image) {
        this();
        setImage(image);
    }

    /**
     * Sets the rendered image to display.
     *
     * @param image The image for which to display sample values, or {@code null} if none.
     */
    public void setImage(final RenderedImage image) {
        final ImageTableModel samples = (ImageTableModel) table.getModel();
        final boolean isFirst = (samples.getRenderedImage() == null);
        samples.setRenderedImage(image);
        renderer.formatter = samples.getNumberFormat();
        if (image != null) {
            final SampleModel model = image.getSampleModel();
            final int      numBands = model.getNumBands();
            final Integer  maximum  = numBands;
            if (band.getNumber().intValue() >= numBands) {
                band.setValue(maximum);
            }
            band.setMaximum(maximum);
            /*
             * Once the spinner is updated, adjusts the columns width. However, we will perform this
             * task only for the first image to be displayed. For all others, we will preserve the
             * previous widths (which may have been adjusted by the user).
             */
            if (isFirst && model.getDataType() == DataBuffer.TYPE_BYTE) {
                final TableColumnModel columns = table.getColumnModel();
                for (int i=columns.getColumnCount(); --i>=0;) {
                    final TableColumn column = columns.getColumn(i);
                    column.setPreferredWidth(40);
                }
            }
        }
    }
}
