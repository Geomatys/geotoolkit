/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing;

import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeEvent;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.SwingUtilities;


/**
 * A panel displaying logging messages. The windows displaying Geotoolkit's logging messages
 * can be constructed with the following code:
 *
 * {@preformat java
 *     new LoggingPanel("org.geotoolkit").show(null);
 * }
 *
 * This panel is initially set to listen to messages of level {@link Level#CONFIG} or higher.
 * This level can be changed with a call to <code>{@linkplain #getHandler}.setLevel(aLevel)</code>.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.0
 * @module
 */
@SuppressWarnings("serial")
public class LoggingPanel extends JPanel {
    /**
     * Enumeration class for columns to be shown in a {@link LoggingPanel}.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.0
     *
     * @see LoggingPanel#setColumnVisible
     *
     * @since 2.0
     * @module
     */
    public static enum Column {
        /*
         * NOTE: Orinal values MUST match index in the LoggingTableModel.COLUMN_NAMES array.
         */
        /** The column displaying the logger name.        */  LOGGER,
        /** The column displaying the originating class.  */  CLASS,
        /** The column displaying the originating method. */  METHOD,
        /** The column displaying the log record time.    */  TIME_OF_DAY,
        /** The column displaying the log record level.   */  LEVEL,
        /** The column displaying the message.            */  MESSAGE;
    }

    /**
     * The background color for the columns prior to the logging message.
     */
    private static final Color INFO_BACKGROUND = new Color(240, 240, 240);

    /**
     * The model for this component.
     */
    private final LoggingTableModel model = new LoggingTableModel();

    /**
     * The table for displaying logging messages.
     */
    private final JTable table = new JTable(model);

    /**
     * The levels for colors enumerated in {@code levelColors}. This array <strong>must</strong>
     * be in increasing order. Logging messages of level {@code levelValues[i]} or higher will
     * be displayed with foreground color {@code levelColors[i*2]} and background color
     * {@code levelColors[i*2+1]}.
     *
     * @see Level#intValue
     * @see #getForeground(LogRecord)
     * @see #getBackground(LogRecord)
     */
    private int[] levelValues = new int[0];

    /**
     * Pairs of foreground and background colors to use for displaying logging messages. Logging
     * messages of level {@code levelValues[i]} or higher will be displayed with foreground color
     * {@code levelColors[i*2]} and background color {@code levelColors[i*2+1]}.
     *
     * @see #getForeground(LogRecord)
     * @see #getBackground(LogRecord)
     */
    private final List<Color> levelColors = new ArrayList<Color>();

    /**
     * The logger specified at construction time, or {@code null} if none.
     */
    private Logger logger;

    /**
     * Constructs a new logging panel. This panel is not registered to any logger.
     * Registration can be done with the following code:
     *
     * {@preformat java
     *     logger.addHandler(getHandler());
     * }
     */
    public LoggingPanel() {
        super(new BorderLayout());
        table.setShowGrid(false);
        table.setCellSelectionEnabled(false);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDefaultRenderer(Object.class, new CellRenderer());
        if (true) {
            int width = 300;
            final TableColumnModel columns = table.getColumnModel();
            for (int i=model.getColumnCount(); --i>=0;) {
                columns.getColumn(i).setPreferredWidth(width);
                width = 80;
            }
        }
        final JScrollPane scroll = new JScrollPane(table);
        new AutoScroll(scroll.getVerticalScrollBar().getModel());
        add(scroll, BorderLayout.CENTER);

        setLevelColor(Level.ALL,     Color.GRAY,       null);
        setLevelColor(Level.CONFIG,  null,             null);
        setLevelColor(Level.WARNING, Color.RED,        null);
        setLevelColor(Level.SEVERE,  Color.WHITE, Color.RED);
    }

    /**
     * Constructs a new logging panel and register it to the specified logger.
     *
     * @param logger The logger to listen to, or {@code null} for the root logger.
     */
    public LoggingPanel(Logger logger) {
        this();
        if (logger == null) {
            logger = Logging.getLogger("");
        }
        logger.addHandler(getHandler());
        this.logger = logger;
    }

    /**
     * Constructs a logging panel and register it to the specified logger.
     *
     * @param logger The logger name to listen to, or {@code null} for the root logger.
     */
    public LoggingPanel(final String logger) {
        this(Logging.getLogger(logger!=null ? logger : ""));
    }

    /**
     * Returns the logging handler. This is the handler to register to loggers in order to get
     * logging message to appear in the widget. This registration had been done automatically
     * if this widget has been created with any constructor except the no-argument one.
     *
     * @return The logging handler.
     */
    public Handler getHandler() {
        return model;
    }

    /**
     * Returns {@code true} if the given column is visible.
     *
     * @param column The column to show or hide.
     * @return {@code true} if the given column is visible.
     */
    public boolean isColumnVisible(final Column column) {
        return model.isColumnVisible(column.ordinal());
    }

    /**
     * Shows or hide the given column.
     *
     * @param column The column to show or hide.
     * @param visible The visible state for the specified column.
     */
    public void setColumnVisible(final Column column, final boolean visible) {
        model.setColumnVisible(column.ordinal(), visible);
    }

    /**
     * Returns the maximum number of {@link LogRecord}s the handler can memorize. If more
     * messages are logged, then the earliest messages will be discarted.
     *
     * @return The current maximum number of record.
     */
    public int getCapacity() {
        return model.getCapacity();
    }

    /**
     * Sets the maximum number of {@link LogRecord}s the handler can memorize. If more
     * messages are logged, then the earliest messages will be discarted.
     *
     * @param capacity The new maximum number of record.
     */
    public void setCapacity(final int capacity) {
        model.setCapacity(capacity);
    }

    /**
     * Returns the foreground color for the specified log record. This method is invoked at
     * rendering time for every cell in the table's "message" column. The default implementation
     * returns a color based on the record's level, using colors set with {@link #setLevelColor
     * setLevelColor(...)}.
     *
     * @param  record The record to get the foreground color.
     * @return The foreground color for the specified record, or {@code null} for the default color.
     */
    public Color getForeground(final LogRecord record) {
        return getColor(record, 0);
    }

    /**
     * Returns the background color for the specified log record. This method is invoked at
     * rendering time for every cell in the table's "message" column. The default implementation
     * returns a color based on the record's level, using colors set with {@link #setLevelColor
     * setLevelColor(...)}.
     *
     * @param  record The record to get the background color.
     * @return The background color for the specified record,
     *         or {@code null} for the default color.
     */
    public Color getBackground(final LogRecord record) {
        return getColor(record, 1);
    }

    /**
     * Returns the foreground or background color for the specified record.
     *
     * @param  record The record to get the color.
     * @param  offset 0 for the foreground color, or 1 for the background color.
     * @return The color for the specified record, or {@code null} for the default color.
     */
    private Color getColor(final LogRecord record, final int offset) {
        int i = Arrays.binarySearch(levelValues, record.getLevel().intValue());
        if (i < 0) {
            i = ~i - 1; // "~" is the tild symbol, not minus.
            if (i < 0) {
                return null;
            }
        }
        return levelColors.get(i*2 + offset);
    }

    /**
     * Sets the foreground and background colors for messages of the specified level.
     * The specified colors will apply on any messages of level {@code level} or
     * higher, up to the next level set with an other call to {@code setLevelColor(...)}.
     *
     * @param level       The minimal level to set color for.
     * @param foreground  The foreground color, or {@code null} for the default color.
     * @param background  The background color, or {@code null} for the default color.
     */
    public void setLevelColor(final Level level, final Color foreground, final Color background) {
        final int value = level.intValue();
        int i = Arrays.binarySearch(levelValues, value);
        if (i >= 0) {
            i *= 2;
            levelColors.set(i+0, foreground);
            levelColors.set(i+1, background);
        } else {
            i = ~i;
            levelValues = XArrays.insert(levelValues, i, 1);
            levelValues[i] = value;
            i *= 2;
            levelColors.add(i+0, foreground);
            levelColors.add(i+1, background);
        }
        assert XArrays.isSorted(levelValues, true);
        assert levelValues.length*2 == levelColors.size();
    }

    /**
     * Layout this component. This method gives all the remaining space, if any,
     * to the last table's column. This column is usually the one with logging
     * messages.
     */
    @Override
    public void doLayout() {
        final TableColumnModel model = table.getColumnModel();
        final int      messageColumn = model.getColumnCount() - 1;
        Component parent = table.getParent();
        int delta = parent.getWidth();
        parent = parent.getParent();
        if (parent instanceof JScrollPane) {
            delta -= ((JScrollPane) parent).getVerticalScrollBar().getPreferredSize().width;
        }
        for (int i=0; i<messageColumn; i++) {
            delta -= model.getColumn(i).getWidth();
        }
        final TableColumn column = model.getColumn(messageColumn);
        if (delta > Math.max(column.getWidth(), column.getPreferredWidth())) {
            column.setPreferredWidth(delta);
        }
        super.doLayout();
    }

    /**
     * Convenience method showing this logging panel into a frame.
     * Different kinds of frame can be constructed according the {@code owner} class:
     * <p>
     * <ul>
     *   <li>If {@code owner} or one of its parent is a {@link JDesktopPane},
     *       then {@code panel} is added into a {@link JInternalFrame}.</li>
     *   <li>If {@code owner} or one of its parent is a {@link Frame} or a {@link Dialog},
     *       then {@code panel} is added into a {@link JDialog}.</li>
     *   <li>Otherwise, {@code panel} is added into a {@link JFrame}.</li>
     * </ul>
     *
     * @param  owner The owner, or {@code null} to show
     *         this logging panel in a top-level window.
     * @return The frame. May be a {@link JInternalFrame},
     *         a {@link JDialog} or a {@link JFrame}.
     */
    public Component show(final Component owner) {
        final String title = Vocabulary.format(Vocabulary.Keys.EVENT_LOGGER);
        final Component frame = SwingUtilities.toFrame(owner, this, title, new WindowAdapter() {
            @Override public void windowClosed(WindowEvent event) {
                dispose();
            }
        });
        frame.setSize(750, 300);
        frame.setVisible(true);
        doLayout();
        return frame;
    }

    /**
     * Frees any resources used by this {@code LoggingPanel}. If a {@link Logger} was specified at
     * construction time, then this method unregister the {@code LoggingPanel}'s handler from the
     * specified logger. Next, {@link Handler#close} is invoked.
     * <p>
     * This method is invoked automatically when the user closes the windows created with
     * {@link #show(Component)}. If this {@code LoggingPanel} is displayed by some other ways
     * (for example if it has been added into a {@link JPanel}), then this {@code dispose()}
     * method must be invoked explicitely when the container is being discarted.
     */
    public void dispose() {
        final Handler handler = getHandler();
        while (logger != null) {
            logger.removeHandler(handler);
            logger = logger.getParent();
        }
        handler.close();
    }

    /**
     * Display cell contents. This class is used for changing
     * the cell's color according the log record level.
     */
    @SuppressWarnings("serial")
    private final class CellRenderer extends DefaultTableCellRenderer implements TableColumnModelListener {
        /**
         * Default color for the foreground.
         */
        private Color foreground;

        /**
         * Default color for the background.
         */
        private Color background;

        /**
         * The index of messages column.
         */
        private int messageColumn;

        /**
         * The last row for which the side has been computed.
         */
        private int lastRow;

        /**
         * Construct a new cell renderer.
         */
        public CellRenderer() {
            foreground = super.getForeground();
            background = super.getBackground();
            table.getColumnModel().addColumnModelListener(this);
        }

        /**
         * Sets the foreground color.
         */
        @Override
        public void setForeground(final Color foreground) {
            super.setForeground(this.foreground=foreground);
        }

        /**
         * Sets the background colior
         */
        @Override
        public void setBackground(final Color background) {
            super.setBackground(this.background=background);
        }

        /**
         * Returns the component to use for painting the cell.
         */
        @Override
        public Component getTableCellRendererComponent(final JTable  table,
                                                       final Object  value,
                                                       final boolean isSelected,
                                                       final boolean hasFocus,
                                                       final int     rowIndex,
                                                       final int     columnIndex)
        {
            Color foreground = this.foreground;
            Color background = this.background;
            final boolean isMessage = (columnIndex == messageColumn);
            if (!isMessage) {
                background = INFO_BACKGROUND;
            }
            if (rowIndex >= 0) {
                final TableModel candidate = table.getModel();
                if (candidate instanceof LoggingTableModel) {
                    final LoggingTableModel model = (LoggingTableModel) candidate;
                    final LogRecord record = model.getLogRecord(rowIndex);
                    Color color;
                    color=LoggingPanel.this.getForeground(record); if (color!=null) foreground=color;
                    color=LoggingPanel.this.getBackground(record); if (color!=null) background=color;
                }
            }
            super.setBackground(background);
            super.setForeground(foreground);
            final Component component = super.getTableCellRendererComponent(table, value,
                                             isSelected, hasFocus, rowIndex, columnIndex);
            /*
             * If a new record is being painted and this new record is wider
             * than previous ones, then make the message column width larger.
             */
            if (isMessage) {
                if (rowIndex > lastRow) {
                    final int width = component.getPreferredSize().width + 15;
                    final TableColumn column = table.getColumnModel().getColumn(columnIndex);
                    if (width > column.getPreferredWidth()) {
                        column.setPreferredWidth(width);
                    }
                    if (rowIndex == lastRow+1) {
                        lastRow = rowIndex;
                    }
                }
            }
            return component;
        }

        /**
         * Invoked when the message column may have moved. This method update the
         * {@link #messageColumn} field, so that the message column will continue
         * to be paint with special colors.
         */
        private final void update() {
            messageColumn = table.convertColumnIndexToView(model.getColumnCount()-1);
        }

        @Override public void columnAdded        (TableColumnModelEvent e) {update();}
        @Override public void columnMarginChanged          (ChangeEvent e) {update();}
        @Override public void columnMoved        (TableColumnModelEvent e) {update();}
        @Override public void columnRemoved      (TableColumnModelEvent e) {update();}
        @Override public void columnSelectionChanged(ListSelectionEvent e) {update();}
    }
}
