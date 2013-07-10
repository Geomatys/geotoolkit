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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import java.util.Date;
import java.text.DateFormat;

import java.awt.EventQueue;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.EventListenerList;
import javax.swing.BoundedRangeModel;

import org.apache.sis.util.CharSequences;
import org.geotoolkit.resources.Vocabulary;


/**
 * A logging {@link Handler} storing {@link LogRecords} as a {@link TableModel}.
 * This model is used by {@link LoggingPanel} for displaying logging messages in
 * a {@link javax.swing.JTable}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.02
 *
 * @since 2.0
 * @module
 */
final class LoggingTableModel extends Handler implements TableModel {
    /**
     * Resource keys for default column names. <STRONG>NOTE: Order is significant.</STRONG>
     * If the order is changed, then the constants in {@link #getValueAt(int,int)} and
     * {@link LoggingPanel} must be updated.
     */
    private static final int[] COLUMN_NAMES = new int[] {
        Vocabulary.Keys.LOGGER,
        Vocabulary.Keys.CLASS,
        Vocabulary.Keys.METHOD,
        Vocabulary.Keys.TIME_OF_DAY,
        Vocabulary.Keys.LEVEL,
        Vocabulary.Keys.MESSAGE
    };

    /**
     * Holds a log record. If the message is multi-lines, then a {@code Record} instance
     * will be repeated for each line with a reference to the same {@link LogRecord} but
     * a different line of the message.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.01
     *
     * @since 3.01
     * @module
     */
    private static final class Record {
        /**
         * Special value for {@link #time} meaning that this entry is for additional
         * lines after the first one.
         */
        private static final String ADDITIONAL_LINES = "ADDITIONAL_LINES";

        /**
         * The log record.
         */
        final LogRecord log;

        /**
         * The message. If there is log message has more than one line, then this
         * will contains only one line of that message.
         */
        private final String line;

        /**
         * The time. Will be created when first needed. The {@value #ADDITIONAL_LINES}
         * string is used as a special value meaning that nothing should be formatted.
         */
        private String time;

        /**
         * Creates a new record for the given log.
         */
        private Record(final LogRecord log, final String line) {
            this.log  = log;
            this.line = line;
        }

        /**
         * Creates the records for the given log and its message.
         */
        static Record[] create(final LogRecord log, final String message) {
            final CharSequence[] lines = CharSequences.splitOnEOL(message);
            final Record[] records = new Record[lines.length];
            for (int i=0; i<lines.length; i++) {
                records[i] = new Record(log, lines[i].toString());
            }
            for (int i=1; i<lines.length; i++) {
                records[i].time = ADDITIONAL_LINES;
            }
            return records;
        }

        /**
         * Returns the value at the given column.
         *
         * @param  owner The model which is invoking this method.
         * @param  columnIndex The column for which to get the value.
         * @return The value at the given column.
         */
        public Object getValueAt(final LoggingTableModel owner, final int columnIndex) {
            if (time == ADDITIONAL_LINES) { // NOSONAR (Intentional identity comparisons, not String.equals)
                switch (columnIndex) {
                    // The Level as Integer will be handled specially by
                    // LoggingPanel.Highlighter.isHighlighted(...).
                    case 4:  return Integer.valueOf(log.getLevel().intValue());
                    case 5:  return line;
                    default: return null;
                }
            }
            /*
             * General case (the logging message holds on a single line,
             * or we are asking for the first line of a multi-lines log).
             */
            switch (columnIndex) {
                default: throw new AssertionError(columnIndex);
                case 0:  return log.getLoggerName();
                case 1:  return getShortClassName(log.getSourceClassName());
                case 2:  return log.getSourceMethodName();
                case 4:  return log.getLevel();
                case 5:  return line;
                case 3: {
                    if (time == null) {
                        time = owner.dateFormat.format(new Date(log.getMillis()));
                    }
                    return time;
                }
            }
        }
    }

    /**
     * The last {@link Record}s stored. This array will grows as needed up to
     * {@link #capacity}. Once the maximal capacity is reached, early records
     * are discarded.
     */
    private Record[] records = new Record[16];

    /**
     * The maximum amount of records that can be stored in this logging panel.
     * If more than {@link #capacity} messages are logged, early messages will
     * be discarded.
     */
    private int capacity = 500;

    /**
     * The total number of logging messages published by this panel. This number may be
     * greater than the amount of {@link Record} actually memorized, since early records
     * may have been discarded. The slot in {@code records} where to write the next
     * message is defined by {@code recordCount % capacity}.
     */
    private int recordCount;

    /**
     * The list of registered listeners.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * The format to use for formatting time.
     */
    private final DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);

    /**
     * Constructs the handler.
     */
    public LoggingTableModel() {
        setLevel(Level.ALL);
        setFormatter(new SimpleFormatter());
    }

    /**
     * Returns the capacity. This is the maximum number of {@link LogRecord}s this handler
     * can memorize. If more messages are logged, then the oldiest messages will be discarded.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the capacity. This is the maximum number of {@link LogRecord}s this handler can
     * memorize. If more messages are logged, then the oldiest messages will be discarded.
     */
    public synchronized void setCapacity(final int capacity) {
        if (recordCount != 0) {
            throw new IllegalStateException("Not yet implemented.");
        }
        this.capacity = capacity;
    }

    /**
     * Publishes a {@link LogRecord}. If the maximal capacity has been reached,
     * then the oldest record will be discarded.
     */
    @Override
    public synchronized void publish(final LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        /*
         * Wraps the LogRecord in exactly one Record instances (typicaly case), or more
         * Record instances if the LogRecord message spans more than one line.
         */
        final Record[] toAdd = Record.create(record, getFormatter().formatMessage(record));
        for (final Record item : toAdd) {
            final int nextSlot = recordCount % capacity;
            if (nextSlot >= records.length) {
                records = Arrays.copyOf(records, Math.min(records.length*2, capacity));
            }
            records[nextSlot] = item;
            recordCount++;
        }
        /*
         * Notify all listeners that one (or more) records have been added.
         */
        final int upper   = Math.min(recordCount, capacity);
        final int removed = Math.min(recordCount - capacity, toAdd.length);
        final TableModelEvent remove, insert;
        if (removed > 0) {
            remove = new TableModelEvent(this, 0, removed-1,
                    TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
        } else {
            remove = null;
        }
        insert = new TableModelEvent(this, upper - toAdd.length, upper - 1,
                TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                if (remove != null) {
                    fireTableChanged(remove);
                }
                fireTableChanged(insert);
            }
        });
    }

    /**
     * Controls the scrolling of {@link LoggingPanel}. This class scroll down if the view port
     * was already at the bottom of the scroll area and a new record is inserted, or scroll up
     * if the view port is <em>not</em> at the bottom of the scroll area and the first record
     * has been removed.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.02
     * @module
     */
    @SuppressWarnings("serial")
    static final class Scroll extends AutoScroll implements TableModelListener {
        /**
         * The table on which to control the scrolling.
         */
        private final JTable table;

        /**
         * Constructs a new {@code AutoScroll} for the specified model.
         */
        Scroll(final JTable table, final BoundedRangeModel model) {
            super(model);
            this.table = table;
        }

        /**
         * Invoked when a new record has been added. If the oldest record has been removed
         * and the viewport is not at the bottom of the scroll area, scroll up in order to
         * keep the viewport on the same records.
         */
        @Override
        public void tableChanged(final TableModelEvent event) {
            if (event.getType() == TableModelEvent.DELETE && !isViewBottom()) {
                final int n = event.getFirstRow() - (event.getLastRow() + 1); // Intentionally negative.
                conditionalScroll(n * table.getRowHeight());
            }
        }
    }

    /**
     * Returns the record for the specified row.
     *
     * @param row The row in the table. This is the visible row,
     *            not the record number from the first record.
     */
    private Record getLogRecord(int row) {
        // Above check is performed with an assertion instead than an API contract check
        // because this method is invoked indirectly only from the table inside LoggingPanel.
        // This is pretty far from a public access (while not inacessible).
        assert row < getRowCount();
        if (recordCount > capacity) {
            row += (recordCount % capacity);
            row %= capacity;
        }
        return records[row];
    }

    /**
     * Returns the number of columns in the model.
     */
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    /**
     * Returns the number of rows in the model.
     */
    @Override
    public synchronized int getRowCount() {
        return Math.min(recordCount, capacity);
    }

    /**
     * Returns the most specific superclass for all the cell values in the column.
     */
    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
            case 4:  return Level .class;
            default: return String.class;
        }
    }

    /**
     * Returns the name of the column at {@code columnIndex}.
     */
    @Override
    public String getColumnName(final int columnIndex) {
        return Vocabulary.format(COLUMN_NAMES[columnIndex]);
    }

    /**
     * Returns the value for the cell at {@code columnIndex} and {@code rowIndex}.
     */
    @Override
    public synchronized Object getValueAt(final int rowIndex, final int columnIndex) {
        return getLogRecord(rowIndex).getValueAt(this, columnIndex);
    }

    /**
     * Returns the class name in a shorter form (without package).
     */
    private static String getShortClassName(String name) {
        if (name != null) {
            final int dot = name.lastIndexOf('.');
            if (dot >= 0) {
                name = name.substring(dot+1);
            }
            name = name.replace('$','.');
        }
        return name;
    }

    /**
     * Does nothing since cells are not editable.
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    /**
     * Returns {@code false} since cells are not editable.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Adds a listener that is notified each time a change to the data model occurs.
     */
    @Override
    public void addTableModelListener(final TableModelListener listener) {
        listenerList.add(TableModelListener.class, listener);
    }

    /**
     * Removes a listener from the list that is notified each time a change occurs.
     */
    @Override
    public void removeTableModelListener(final TableModelListener listener) {
        listenerList.remove(TableModelListener.class, listener);
    }

    /**
     * Forwards the given notification event to all {@link TableModelListeners}.
     * Listeners are notified in registration order. This order is necessary,
     * because the last listener is the {@link Scroll} object and we want it
     * to be notified last.
     */
    private void fireTableChanged(final TableModelEvent event) {
        final Object[] listeners = listenerList.getListenerList();
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i] == TableModelListener.class) {
                ((TableModelListener) listeners[i+1]).tableChanged(event);
            }
        }
    }

    /**
     * Flushes any buffered output.
     */
    @Override
    public void flush() {
    }

    /**
     * Closes the {@code Handler} and free all associated resources.
     */
    @Override
    public void close() {
    }
}
