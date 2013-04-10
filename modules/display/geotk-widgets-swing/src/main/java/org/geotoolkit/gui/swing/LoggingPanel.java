/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing;

import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.ScrollPaneConstants;

import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Disposable;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.resources.Vocabulary;


/**
 * A panel displaying logging messages. The windows displaying Geotk's logging messages
 * can be constructed with the following code:
 *
 * {@preformat java
 *     new LoggingPanel("org.geotoolkit").show(null);
 * }
 *
 * This panel initially listens to all messages ({@link Level#ALL}). However the messages will
 * still be filtered according the {@linkplain Logger#getLevel() logger level}. If all levels
 * are really aimed to be reported, then a call to {@code Logger.setLevel(Level.ALL)} may be
 * needed.
 * <p>
 * Note that a different level can be set specifically to this {@code LoggingPanel} with a call
 * to <code>{@linkplain #getHandler}.setLevel(aLevel)</code>. However this is only for restricting
 * the logger messages to a higher level than the logger level.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @since 2.0
 * @module
 */
@SuppressWarnings("serial")
public class LoggingPanel extends JComponent implements Disposable {
    /**
     * Enumeration class for columns to be shown in a {@link LoggingPanel}.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
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
        /** The column displaying the logger name.        */  LOGGER    (160),
        /** The column displaying the originating class.  */  CLASS     (120),
        /** The column displaying the originating method. */  METHOD     (80),
        /** The column displaying the log record time.    */  TIME_OF_DAY(80),
        /** The column displaying the log record level.   */  LEVEL      (80),
        /** The column displaying the message.            */  MESSAGE   (300);

        /** The preferred width. */
        final int width;

        /** Creates a new column with the given preferred width. */
        private Column(final int width) {
            this.width = width;
        }
    }

    /**
     * The model for this component.
     */
    private final LoggingTableModel model;

    /**
     * The table for displaying logging messages.
     */
    private final JXTable table;

    /**
     * Scroll down automatically when a new log record is added, provided that
     * the scroll is already at the bottom.
     */
    private final LoggingTableModel.Scroll scrollControl;

    /**
     * Foreground and background colors to use for displaying logging messages.
     *
     * @see #getForeground(Level)
     * @see #getBackground(Level)
     */
    private Highlighter[] levelColors = new Highlighter[0];

    /**
     * The logger specified at construction time, or {@code null} if none.
     */
    private Logger logger;

    /**
     * The font to use for messages. We use by default a monospaced font
     * because some messages are formatted for the console (e.g. as a table).
     */
    private Font messageFont;

    /**
     * Constructs a new logging panel. This panel is not registered to any logger.
     * Registration can be done with the following code:
     *
     * {@preformat java
     *     logger.addHandler(getHandler());
     * }
     */
    public LoggingPanel() {
        setLayout(new BorderLayout());
        messageFont = Font.decode("Monospaced");
        model = new LoggingTableModel();
        table = new JXTable(model);
        /*
         * Sets table properties, especially the row height which is set to the font size.
         * This is needed in order to preserve the formatting of boxes, trees, etc. printed
         * using the drawing character of monospaced font. The row height will be set again
         * in the setMessageFont(...) method.
         */
        table.setShowGrid(false);
        table.setRolloverEnabled(false);
        table.setColumnControlVisible(true);
        table.setCellSelectionEnabled(false);
        table.setRowHeight(messageFont.getSize());
        table.setRowMargin(0);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        final TableColumnModel columns = table.getColumnModel();
        for (final Column c : Column.values()) {
            final TableColumn column = columns.getColumn(c.ordinal());
            column.setPreferredWidth(c.width);
            column.setIdentifier(c);
        }
        table.setDefaultRenderer(Level.class, new DefaultTableRenderer(new StringValue() {
            @Override public String getString(final Object value) {
                return (value instanceof Level) ? ((Level) value).getLocalizedName() : null;
            }
        }));
        final JScrollPane scroll = new JScrollPane(table);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollControl = new LoggingTableModel.Scroll(table, scroll.getVerticalScrollBar().getModel());
        model.addTableModelListener(scrollControl);
        add(scroll, BorderLayout.CENTER);

        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setLevelColor(Level.ALL,     Color.GRAY,  null);
        setLevelColor(Level.CONFIG,  null,        null);
        setLevelColor(Level.WARNING, null,        Color.YELLOW);
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
        this(Logging.getLogger(logger != null ? logger : ""));
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
        return ((TableColumnExt) table.getColumn(column)).isVisible();
    }

    /**
     * Shows or hide the given column.
     *
     * @param column The column to show or hide.
     * @param visible The visible state for the specified column.
     */
    public void setColumnVisible(final Column column, final boolean visible) {
        ((TableColumnExt) table.getColumn(column)).setVisible(visible);
    }

    /**
     * Returns the maximum number of {@link java.util.logging.LogRecord}s the handler can
     * memorize. If more messages are logged, then the earliest messages will be discarded.
     *
     * @return The current maximum number of record.
     */
    public int getCapacity() {
        return model.getCapacity();
    }

    /**
     * Sets the maximum number of {@link java.util.logging.LogRecord}s the handler can memorize.
     * If more messages are logged, then the earliest messages will be discarded.
     *
     * @param capacity The new maximum number of record.
     */
    public void setCapacity(final int capacity) {
        model.setCapacity(capacity);
    }

    /**
     * Returns the font to use for displaying the messages (last table column).
     * The default is a monospaced font because some message are formatted for
     * the console (for example they may use the drawing unicode characters).
     *
     * @return The font to use for displaying messages.
     *
     * @since 3.01
     */
    public Font getMessageFont() {
        return messageFont;
    }

    /**
     * Sets the font to use for displaying the messages.
     *
     * @param font The new font for displaying messages.
     *
     * @since 3.01
     */
    public void setMessageFont(final Font font) {
        final Font old = messageFont;
        messageFont = font;
        table.setRowHeight(font.getSize());
        firePropertyChange("messageFont", old, font);
        repaint();
    }

    /**
     * Returns the foreground color for the given level.
     *
     * @param  level The level for which to get the foreground color.
     * @return The foreground color for the given level, or {@code null} for the default color.
     *
     * @since 3.01
     */
    public Color getForeground(final Level level) {
        final ColorHighlighter hlr = getHighlighter(level);
        return (hlr != null) ? hlr.getForeground() : null;
    }

    /**
     * Returns the background color for the specified log record. This method returns a color based
     * on the record level, using colors set with {@link #setLevelColor setLevelColor(...)}.
     *
     * @param  level The level for which to get the background color.
     * @return The background color for the given level, or {@code null} for the default color.
     *
     * @since 3.01
     */
    public Color getBackground(final Level level) {
        final ColorHighlighter hlr = getHighlighter(level);
        return (hlr != null) ? hlr.getBackground() : null;
    }

    /**
     * Returns the highlighter for the given level.
     *
     * @param  level The level for which to get the highlighter.
     * @return The highlighter for the given level, or {@code null} if none.
     */
    private ColorHighlighter getHighlighter(final Level level) {
        int i = Arrays.binarySearch(levelColors, level, COMPARATOR);
        if (i < 0) {
            /*
             * No exact match for the given level.
             * Looks for the level below the requested one.
             */
            i = ~i - 1; // "~" is the tild symbol, not minus.
            if (i < 0) {
                return null;
            }
        }
        return levelColors[i];
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
        final Highlighter hlr;
        Highlighter[] levelColors = this.levelColors;
        int i = Arrays.binarySearch(levelColors, level, COMPARATOR);
        if (i >= 0) {
            hlr = levelColors[i];
        } else {
            i = ~i;
            hlr = new Highlighter(level.intValue());
            this.levelColors = levelColors = ArraysExt.insert(levelColors, i, 1);
            levelColors[i] = hlr;
            if (i != 0) {
                levelColors[i-1].upper = hlr.lower;
            }
            if (++i < levelColors.length) {
                hlr.upper = levelColors[i].lower;
            }
            assert ArraysExt.isSorted(levelColors, COMPARATOR, true);
            table.setHighlighters(levelColors);
        }
        hlr.setBackground(background);
        hlr.setForeground(foreground);
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
     *   <li>If {@code owner} or one of its parent is a {@link javax.swing.JDesktopPane},
     *       then {@code panel} is added into a {@link javax.swing.JInternalFrame}.</li>
     *   <li>If {@code owner} or one of its parent is a {@link java.awt.Frame} or a
     *       {@link java.awt.Dialog}, then {@code panel} is added into a
     *       {@link javax.swing.JDialog}.</li>
     *   <li>Otherwise, {@code panel} is added into a {@link javax.swing.JFrame}.</li>
     * </ul>
     *
     * @param  owner The owner, or {@code null} to show
     *         this logging panel in a top-level window.
     * @return The frame. May be a {@link javax.swing.JInternalFrame},
     *         a {@link javax.swing.JDialog} or a {@link javax.swing.JFrame}.
     */
    public Component show(final Component owner) {
        final String title = Vocabulary.format(Vocabulary.Keys.EVENT_LOGGER);
        final Window frame = WindowCreator.Handler.DEFAULT.createWindow(owner, this, title);
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent event) {
                dispose();
            }
        });
        frame.setSize(750, 300);
        frame.setVisible(true);
        doLayout();
        return (Component) frame;
    }

    /**
     * Frees any resources used by this {@code LoggingPanel}. If a {@link Logger} was specified at
     * construction time, then this method unregister the {@code LoggingPanel}'s handler from the
     * specified logger. Next, {@link Handler#close} is invoked.
     * <p>
     * This method is invoked automatically when the user closes the windows created with
     * {@link #show(Component)}. If this {@code LoggingPanel} is displayed by some other ways
     * (for example if it has been added into a {@link JPanel}), then this {@code dispose()}
     * method must be invoked explicitly when the container is being discarded.
     */
    @Override
    public void dispose() {
        model.removeTableModelListener(scrollControl);
        scrollControl.dispose();
        final Handler handler = getHandler();
        while (logger != null) {
            logger.removeHandler(handler);
            logger = logger.getParent();
        }
        handler.close();
    }

    /**
     * Compares two levels for order. The level can be encapsulated in either
     * {@link Level} or {@link Highlighter} object.
     */
    private static final Comparator<Object> COMPARATOR = new Comparator<Object>() {
        @Override public int compare(final Object o1, final Object o2) {
            // Do not return (n1 - n2) - it doesn't work because of overflow.
            final int n1 = level(o1);
            final int n2 = level(o2);
            if (n1 > n2) return +1;
            if (n1 < n2) return -1;
            return 0;
        }
    };

    /**
     * Returns the numeric value of the given level. The level can be encapsulated in either
     * {@link Level} or {@link Highlighter} object.
     */
    static int level(final Object o) {
        if (o instanceof Level) {
            return ((Level) o).intValue();
        }
        return ((Highlighter) o).lower;
    }

    /**
     * Used for changing the cell's color according the log record level.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.01
     *
     * @since 3.01
     * @module
     */
    private final class Highlighter extends ColorHighlighter implements HighlightPredicate {
        /**
         * Uses this highlighter only for log level in the given range.
         * The lower value is inclusive while the upper value is exclusive.
         */
        final int lower;

        /**
         * The upper level value, exclusive. Must be updated by the caller
         * when other highlighter are added or removed.
         */
        int upper;

        /**
         * Creates a default highlighter.
         */
        Highlighter(final int level) {
            lower = level;
            upper = Integer.MAX_VALUE;
            setHighlightPredicate(this);
        }

        /**
         * Returns {@code true} if this highlighter should be applied to the current row.
         */
        @Override
        public boolean isHighlighted(final Component renderer, final ComponentAdapter adapter) {
            final Object value = adapter.getValue(Column.LEVEL.ordinal());
            final int level;
            if (value instanceof Level) {
                // The normal case.
                level = ((Level) value).intValue();
            } else if (value instanceof Integer) {
                // This is a special case generated by LoggingTableModel.Record.getValueAt(...)
                // when the log message span more than one line, and we are asking for any line
                // other than the first one.
                if (adapter.viewToModel(adapter.column) != Column.MESSAGE.ordinal()) {
                    return false;
                }
                level = ((Integer) value).intValue();
            } else {
                return false;
            }
            return level >= lower && level < upper;
        }

        /**
         * Applies the color, with a special processing for the message column.
         */
        @Override
        protected Component doHighlight(final Component renderer, final ComponentAdapter adapter) {
            if (adapter.viewToModel(adapter.column) == Column.MESSAGE.ordinal()) {
                renderer.setBackground(null);
                if (getBackground() == null) {
                    renderer.setForeground(getForeground());
                }
                renderer.setFont(messageFont);
                return renderer;
            }
            renderer.setFont(null);
            return super.doHighlight(renderer, adapter);
        }
    }
}
