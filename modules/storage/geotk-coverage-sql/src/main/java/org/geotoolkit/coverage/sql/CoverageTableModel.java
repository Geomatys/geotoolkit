/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Locale;
import java.util.Date;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.concurrent.CancellationException;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;

import org.opengis.coverage.Coverage;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.DateRange;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.Threads;
import org.geotoolkit.image.io.IIOListeners;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.coverage.io.CoverageStoreException;

import static org.apache.sis.util.collection.Containers.hashMapCapacity;


/**
 * A <cite>Swing</cite> {@linkplain TableModel Table Model} listing coverages entries. An instance
 * of {@code CoverageTableModel} typically contains the coverages available in a particular layer,
 * but this is not required.
 * <p>
 * This class also provides support for <cite>undo</cite>/<cite>redo</cite> operations,
 * and can render the table cells in different colors according whatever the coverage file exists
 * (the name of missing files are written in red) and if the coverage has been previously visited.
 * <p>
 * The code below shows how to use this table model in a <cite>Swing</cite> application:
 *
 * {@preformat java
 *     Layer layer = coverageDatabase.getLayer("MyLayer");
 *     CoverageTableModel model = new CoverageTableModel(layer.getCoverageReferences(null), null);
 *     JTable table = new JTable(model);
 *
 *     // Enable cell coloring (optional).
 *     TableCellRenderer renderer = new CoverageTableModel.CellRenderer();
 *     view.setDefaultRenderer(String.class, renderer);
 *     view.setDefaultRenderer(Date.class,   renderer);
 *
 *     // Enable undo/redo manager (optional).
 *     UndoManager undoManager = new UndoManager();
 *     model.addUndoableEditListener(undoManager);
 *
 *     // Show the table in a frame.
 *     JFrame frame = new JFrame(layer.getName());
 *     frame.add(table);
 *     frame.pack();
 *     frame.setVisible(true);
 * }
 *
 * If a undo manager has been setup, the application can invoke the {@link UndoManager#undo()}
 * and {@link UndoManager#redo()} methods.
 *
 * {@section Multi-threading}
 * Like most <cite>Swing</cite> class, this class shall not be assumed thread-safe. This class
 * is designed for usage from the <cite>event dispatcher</cite> thread, unless otherwise specified.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.11
 *
 * @see org.geotoolkit.gui.swing.coverage.CoverageList
 *
 * @since 3.11 (derived from Seagis)
 * @module
 */
public class CoverageTableModel extends AbstractTableModel {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 6723633134014245147L;

    /**
     * {@code true} for displaying most recent records first.
     */
    private static final boolean REVERSE_ORDER = true;

    /** Column number of file names. */ private static final int NAME     = 0;
    /** Column number of file dates. */ private static final int DATE     = 1;
    /** Column number of duration.   */ private static final int DURATION = 2;

    /**
     * Column types. The index shall be the {@link #NAME}, {@link #DATE} or
     * {@link #DURATION} constants.
     */
    private static final Class<?>[] CLASSES = new Class<?>[3];
    static {
        CLASSES[NAME]     = String.class;
        CLASSES[DATE]     = String.class; // We format this entry ourself.
        CLASSES[DURATION] = String.class;
    };

    /**
     * Column titles. The index shall be the {@link #NAME}, {@link #DATE} or
     * {@link #DURATION} constants.
     */
    private final String[] titles;

    /**
     * The list of entries shown by this table model. The length of this array is the
     * number of rows in the table model. The elements in this array will be replaced
     * by {@code CoverageProxy} instances when first visited, on a element-by-element
     * basis.
     */
    private GridCoverageReference[] entries;

    /**
     * The locale to use for formatting the cell content.
     * Locale category is {@code Locale.Category.DISPLAY}.
     */
    private final Locale locale;

    /**
     * The formatter to use for dates.
     */
    private final DateFormat dateFormat;

    /**
     * The formatter to use for duration.
     */
    private final DateFormat timeFormat;

    /**
     * The formatter to use for numbers.
     */
    private final NumberFormat numberFormat;

    /**
     * Temporary object for cell formatting.
     */
    private transient FieldPosition fieldPosition;

    /**
     * Temporary buffer for cell formatting.
     */
    private transient StringBuffer buffer;

    /**
     * The word "day" in the user locale.
     */
    private final String DAY;

    /**
     * The word "days" in the user locale.
     */
    private final String DAYS;

    /**
     * Creates a new, initially empty, table model. The initial timezone for the dates column
     * is the {@linkplain TimeZone#getDefault() local timezone}, but this can be changed by
     * a call to {@link #setTimeZone(TimeZone)} after construction.
     *
     * @param locale The locale for column titles and cell formatting, or
     *        {@code null} for the {@linkplain Locale#getDefault() default locale}.
     */
    public CoverageTableModel(Locale locale) {
        Locale fmtLoc = locale;
        if (locale == null) {
            locale = Locale.getDefault(Locale.Category.DISPLAY);
            fmtLoc = Locale.getDefault(Locale.Category.FORMAT);
        }
        this.locale  = locale;
        entries      = new GridCoverageReference[0];
        numberFormat = NumberFormat.getNumberInstance(fmtLoc);
        dateFormat   = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, fmtLoc);
        timeFormat   = new SimpleDateFormat("HH:mm", fmtLoc);
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Vocabulary resources = Vocabulary.getResources(locale);
        DAY  = resources.getString(Vocabulary.Keys.DAY);
        DAYS = resources.getString(Vocabulary.Keys.DAYS);
        titles = new String[CLASSES.length];
        titles[NAME]     = resources.getString(Vocabulary.Keys.NAME);
        titles[DATE]     = resources.getString(Vocabulary.Keys.END_TIME);
        titles[DURATION] = resources.getString(Vocabulary.Keys.DURATION);
    }

    /**
     * Creates a new table model initialized to the content of the given collection. The initial
     * timezone for the date column is the {@linkplain TimeZone#getDefault() local timezone},
     * but this can be changed by a call to {@link #setTimeZone(TimeZone)} after construction.
     *
     * @param  refs References to the coverages to put in the list, or {@code null} if none.
     * @param  locale The locale for column titles and cell formatting, or
     *         {@code null} for the {@linkplain Locale#getDefault() default locale}.
     */
    public CoverageTableModel(final Collection<? extends GridCoverageReference> refs, final Locale locale) {
        this(locale);
        if (refs != null) {
            entries = refs.toArray(new GridCoverageReference[refs.size()]);
            rewrapEntries();
            if (REVERSE_ORDER) {
                ArraysExt.reverse(entries);
            }
        }
    }

    /**
     * Creates a new table initialized to the content of the given model.
     * This is a copy constructor, except for the listener lists which are not copied.
     *
     * @param table The table model to copy.
     */
    public CoverageTableModel(final CoverageTableModel table) {
        titles       =                table.titles; // No need to clone this one.
        DAY          =                table.DAY;
        DAYS         =                table.DAYS;
        locale       =                table.locale;
        numberFormat = (NumberFormat) table.numberFormat.clone();
        dateFormat   =   (DateFormat) table.  dateFormat.clone();
        timeFormat   =   (DateFormat) table.  timeFormat.clone();
        entries      =                table.     entries.clone();
        rewrapEntries();
    }

    /**
     * If there is any instance of {@link CoverageProxy} from a foreigner {@code CoverageTableModel}
     * in the given array, replace them by new instance for this {@code CoverageTableModel}. This
     * method is used by constructors only.
     */
    private void rewrapEntries() {
        final GridCoverageReference[] entries = this.entries;
        for (int i=0; i<entries.length; i++) {
            if (entries[i] instanceof CoverageProxy) {
                final CoverageProxy oldProxy = (CoverageProxy) entries[i];
                final CoverageProxy newProxy = new CoverageProxy(unwrap(oldProxy.reference));
                newProxy.flags = oldProxy.flags;
                entries[i] = newProxy;
            }
        }
    }

    /**
     * Sets the content of this table model to the given collection of coverage references.
     * Any coverage references previously listed will be removed from this table model.
     *
     * @param references The new collection of coverage references.
     */
    public void setCoverageReferences(final Collection<? extends GridCoverageReference> references) {
        final GridCoverageReference[] newEntries = references.toArray(new GridCoverageReference[references.size()]);
        if (REVERSE_ORDER) {
            ArraysExt.reverse(newEntries);
        }
        final GridCoverageReference[] oldEntries = entries;
        /*
         * Get the list of CoverageProxy instances that existed before this method call.
         * We will try to recycle existing instances in order to preserve the information
         * about whatever the file exists, etc.
         */
        Map<GridCoverageReference,CoverageProxy> proxies = null;
        for (GridCoverageReference entry : oldEntries) {
            if (entry instanceof CoverageProxy) {
                if (proxies == null) {
                    proxies = new HashMap<>();
                }
                final CoverageProxy proxy = (CoverageProxy) entry;
                final CoverageProxy old = proxies.put(proxy.reference, proxy);
                assert old == null || old == proxy : proxy;
            }
        }
        if (proxies != null) {
            for (int i=0; i<newEntries.length; i++) {
                final CoverageProxy proxy = proxies.get(newEntries[i]);
                if (proxy != null) {
                    newEntries[i] = proxy;
                }
            }
        }
        entries = newEntries;
        fireTableDataChanged();
        commitEdit(oldEntries, newEntries, Vocabulary.Keys.DEFINE);
    }

    /**
     * If the given {@code entry} is of kind {@link CoverageProxy},
     * returns the original reference.
     */
    private static GridCoverageReference unwrap(GridCoverageReference entry) {
        while (entry instanceof CoverageProxy) {
            entry = ((CoverageProxy) entry).reference;
        }
        return entry;
    }

    /**
     * Returns all coverage references listed by this table model.
     * <p>
     * <b>Note:</b> If a call to {@link GridCoverageReference#read GridCoverageReference.read}
     * is planed, consider using the reference returned by {@link #getCoverageReferenceAt(int)}
     * instead.
     *
     * @return The coverage references listed by this table model, or an empty array if none
     *         (never {@code null}).
     */
    public GridCoverageReference[] getCoverageReferences() {
        final GridCoverageReference[] entries = this.entries;
        final GridCoverageReference[] out = new GridCoverageReference[entries.length];
        for (int i=0; i<out.length; i++) {
            out[i] = unwrap(entries[i]);
        }
        return out;
    }

    /**
     * Returns the coverage reference at the given row index. This method returns a special
     * reference which will take trace of whatever a call to {@link GridCoverageReference#read
     * GridCoverageReference.read} succeed or not, and will apply a color on the corresponding
     * table cell accordingly.
     *
     * @param  row The index of the desired coverage reference.
     * @return The coverage reference at the given row.
     */
    public GridCoverageReference getCoverageReferenceAt(final int row) {
        GridCoverageReference entry = entries[row];
        if (!(entry instanceof CoverageProxy)) {
            entries[row] = entry = new CoverageProxy(entry);
        }
        return entry;
    }

    /**
     * Returns the name of all coverages in this table model. The names are computed by
     * {@link #getCoverageName(GridCoverageReference)} and are usually unique for a given
     * layer.
     *
     * @return The coverage names listed by this table model, or an empty array if none
     *         (never {@code null}).
     */
    public String[] getCoverageNames() {
        final GridCoverageReference[] entries = this.entries;
        final String[] names = new String[entries.length];
        for (int i=0; i<names.length; i++) {
            names[i] = getCoverageName(entries[i]);
        }
        return names;
    }

    /**
     * Returns the name of coverages at the given row indices in this table model. The names
     * are computed by {@link #getCoverageName(GridCoverageReference)} and are usually unique
     * for a given layer.
     *
     * @param  rows The rows for which to get the coverage names.
     * @return The coverage names at the given indices.
     *         The length of this array is equals to {@code rows.length}.
     */
    public String[] getCoverageNames(final int... rows) {
        final GridCoverageReference[] entries = this.entries;
        final String[] names = new String[rows.length];
        for (int i=0; i<names.length; i++) {
            names[i] = getCoverageName(entries[rows[i]]);
        }
        return names;
    }

    /**
     * Returns the row indices of coverage references having the given name. The given names shall
     * be built in the same way than what the {@link #getCoverageName(GridCoverageReference)}
     * method do. This method is the converse of {@link #getCoverageNames(int[])}.
     * <p>
     * If no image is found for a given name, the indices of the corresponding element in the
     * returned array is set to -1.
     *
     * @param  names The coverage reference names.
     * @return Row indices of named coverages. The length of this array is equals to
     *         {@code names.length}. The array may contains -1 values for image not found.
     */
    public int[] indexOf(final String... names) {
        /*
         * Get the indices (in the names array) of each name. A name
         * can have more than one index if it appears many time.
         */
        final Map<String,int[]> map = new HashMap<>(hashMapCapacity(names.length));
        for (int i=0; i<names.length; i++) {
            int[] index = map.put(names[i], new int[]{i});
            if (index != null) {
                // In case the same name is requested more than once.
                final int length = index.length;
                index = ArraysExt.resize(index, length+1);
                index[length] = i;
                map.put(names[i], index);
            }
        }
        /*
         * Scan the entries in this table. For each entry having a name matching one of the
         * requested names, set all corresponding elements in the 'rows' array to the row
         * indice.
         */
        final int[] rows = new int[names.length];
        Arrays.fill(rows, -1);
        final GridCoverageReference[] entries = this.entries;
        for (int i=0; i<entries.length; i++) {
            final String name = getCoverageName(entries[i]);
            int[] index = map.remove(name);
            if (index != null) {
                for (int j=0; j<index.length; j++) {
                    rows[index[j]] = i;
                }
                // If the same name has been requested more than once, then the next occurrences
                // of this name will leave the index of the previous occurrence unchanged, and
                // update the indices of the other occurrences.
                if (index.length > 1) {
                    map.put(name, ArraysExt.remove(index, 0, 1));
                }
            }
        }
        return rows;
    }

    /**
     * Removes one or many rows from this table.
     *
     * @param rows The rows to remove.
     */
    public void remove(final int... rows) {
        final Set<GridCoverageReference> toRemoveSet;
        toRemoveSet = new HashSet<>(hashMapCapacity(rows.length));
        for (int i=0; i<rows.length; i++) {
            toRemoveSet.add(unwrap(entries[rows[i]]));
        }
        remove(toRemoveSet);
    }

    /**
     * Removes one or many coverage references from this table. If a given coverage
     * reference is not found in this table, then that reference is ignored.
     *
     * @param toRemove The coverage references to remove.
     */
    public void remove(final GridCoverageReference... toRemove) {
        final Set<GridCoverageReference> toRemoveSet;
        toRemoveSet = new HashSet<>(hashMapCapacity(toRemove.length));
        for (int i=0; i<toRemove.length; i++) {
            toRemoveSet.add(unwrap(toRemove[i]));
        }
        remove(toRemoveSet);
    }

    /**
     * Removes one or many coverage references from this table. If a given coverage
     * reference is not found in this table, then that reference is ignored.
     *
     * @param toRemove The coverage references to remove.
     */
    private void remove(final Set<GridCoverageReference> toRemove) {
        final GridCoverageReference[] oldEntries = entries;
        GridCoverageReference[] entries = oldEntries;
        int entriesLength = entries.length;
        int upper = entriesLength;
        for (int i=upper; --i>=-1;) {
            if (i<0 || !toRemove.contains(unwrap(entries[i]))) {
                final int lower = i+1;
                if (upper != lower) {
                    if (entries == oldEntries) {
                        // Create a copy, so we don't modify the original array.
                        entries = ArraysExt.remove(entries, lower, upper-lower);
                    } else {
                        // Work directly on the array only if we known that it is a copy.
                        System.arraycopy(entries, upper, entries, lower, entriesLength-upper);
                    }
                    entriesLength -= upper - lower;
                    fireTableRowsDeleted(lower, upper-1);
                }
                upper = i;
            }
        }
        this.entries = ArraysExt.resize(entries, entriesLength);
        commitEdit(oldEntries, this.entries, Vocabulary.Keys.DELETE);
    }

    /**
     * Returns a transferable which can be set to the clipboard. A <cite>Copy</cite>
     * action in a <cite>Swing</cite> application could be implemented as below:
     *
     * {@preformat java
     *     Transferable tr = model.copy(rows);
     *     Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
     *     cb.setContents(tr, owner);
     * }
     *
     * @param  rows The indices of the rows to copy in the transferable object.
     * @return A copy of the specified rows as a transferable object.
     */
    public Transferable copy(final int[] rows) {
        if (fieldPosition == null) {
            fieldPosition = new FieldPosition(0);
        }
        /*
         * Don't use the cell buffer.
         *
         * Note: Use the '\n' line separator, not System.getProperty("line.separator", "\n"),
         *       because the later produces strange result when pasted in Excel: an empty line
         *       is inserted between every rows.
         */
        final StringBuffer buffer = new StringBuffer(256);
        final short[] keys = {
            Vocabulary.Keys.NAME,
            Vocabulary.Keys.START_TIME,
            Vocabulary.Keys.END_TIME
        };
        final Vocabulary resources = Vocabulary.getResources(locale);
        for (int i=0; i<keys.length;) {
            buffer.append(resources.getString(keys[i++]));
            buffer.append((i != keys.length) ? '\t' : '\n');
        }
        for (int i=0; i<rows.length; i++) {
            Date date;
            final GridCoverageReference entry = unwrap(entries[rows[i]]);
            final DateRange timeRange = entry.getTimeRange();
            buffer.append(getCoverageName(entry)).append('\t');
            if ((date = timeRange.getMinValue()) != null) {
                dateFormat.format(date, buffer, fieldPosition);
            }
            buffer.append('\t');
            if ((date = timeRange.getMaxValue()) != null) {
                dateFormat.format(date, buffer, fieldPosition);
            }
        }
        return new StringSelection(buffer.append('\n').toString());
    }

    /**
     * Returns the number of rows in the model.
     */
    @Override
    public int getRowCount() {
        return entries.length;
    }

    /**
     * Returns the number of columns in the model.
     */
    @Override
    public int getColumnCount() {
        return titles.length;
    }

    /**
     * Returns the column name at the given index.
     */
    @Override
    public String getColumnName(final int column) {
        return titles[column];
    }

    /**
     * Returns the type of cell values in the column at the given index.
     *
     * @param column The column index.
     */
    @Override
    public Class<?> getColumnClass(final int column) {
        return CLASSES[column];
    }

    /**
     * Returns the cell value at the given row and column.
     *
     * @param  row    The 0-based row index.
     * @param  column The 0-based column index.
     * @return The cell value at the given location.
     */
    @Override
    public Object getValueAt(final int row, final int column) {
        GridCoverageReference entry = entries[row];
        if (!(entry instanceof CoverageProxy)) {
            entries[row] = entry = new CoverageProxy(entry);
        }
        switch (column) {
            case NAME: return getCoverageName(entry);
            case DATE: return format(entry.getTimeRange().getMaxValue());
            case DURATION: {
                final DateRange range = entry.getTimeRange();
                final Date      time  = range.getMaxValue();
                final Date      start = range.getMinValue();
                if (time != null && start != null) {
                    final long millis = time.getTime() - start.getTime();
                    final long days   = millis / (24L*60*60*1000);
                    time.setTime(millis);
                    final StringBuffer buffer = getBuffer();
                    if (days != 0) {
                        numberFormat.format(days, buffer, fieldPosition)
                                .append(' ').append((days > 1) ? DAYS : DAY).append(' ');
                    }
                    return timeFormat.format(time, buffer, fieldPosition).toString();
                }
                break;
            }
        }
        return null;
    }

    /**
     * Returns the coverage name to write in the table cell. The default implementation returns
     * {@link GridCoverageReference#getName()}. Subclasses can override this method in order to
     * build the name differently.
     *
     * @param  entry The coverage reference for which to get the name.
     * @return The coverage name to write in the table cell.
     */
    protected String getCoverageName(final GridCoverageReference entry) {
        return entry.getName();
    }

    /**
     * Returns the string buffer to use for formatting purpose.
     */
    private StringBuffer getBuffer() {
        if (buffer == null) {
            buffer = new StringBuffer();
        }
        if (fieldPosition == null) {
            fieldPosition = new FieldPosition(0);
        }
        buffer.setLength(0);
        return buffer;
    }

    /**
     * Formats the given name.
     */
    private String format(final Date date) {
        if (date == null) {
            return null;
        }
        return dateFormat.format(date, getBuffer(), fieldPosition).toString();
    }

    /**
     * Returns the current timezone used for formatting dates.
     *
     * @return The timezone used for formatting dates.
     */
    public TimeZone getTimeZone() {
        return dateFormat.getTimeZone();
    }

    /**
     * Sets the timezone to use for formatting dates.
     *
     * @param timezone The new timezone to use.
     */
    public void setTimeZone(final TimeZone timezone) {
        dateFormat.setTimeZone(timezone);
        if (entries.length != 0) {
            fireTableChanged(new TableModelEvent(this, 0, entries.length-1, DATE));
        }
    }

    /**
     * Adds a new object to inform every time a undoable action has been performed.
     *
     * @param listener The listener to add.
     */
    public void addUndoableEditListener(final UndoableEditListener listener) {
        listenerList.add(UndoableEditListener.class, listener);
    }

    /**
     * Removes an object from the list of listeners to inform about undoable actions.
     *
     * @param listener The listener to remove.
     */
    public void removeUndoableEditListener(final UndoableEditListener listener) {
        listenerList.remove(UndoableEditListener.class, listener);
    }

    /**
     * Invoked when an undoable action has been performed.
     */
    private void commitEdit(final GridCoverageReference[] oldEntries,
                            final GridCoverageReference[] newEntries,
                            final short key)
    {
        final String name = Vocabulary.getResources(locale).getString(key).toLowerCase();

        @SuppressWarnings("serial")
        final class EditEvent extends AbstractUndoableEdit {
            /** Undo the edit. */
            @Override public void undo() throws CannotUndoException {
                super.undo();
                entries = oldEntries;
                fireTableDataChanged();
            }

            /** Redo the edit. */
            @Override public void redo() throws CannotRedoException {
                super.redo();
                entries = newEntries;
                fireTableDataChanged();
            }

            /** Returns a name describing the edit. */
            @Override public String getPresentationName() {
                return name;
            }
        }

        if (oldEntries != newEntries) {
            final Object[] listeners = listenerList.getListenerList();
            if (listeners.length != 0) {
                UndoableEditEvent event = null;
                for (int i=listeners.length; (i-=2) >= 0;) {
                    if (listeners[i] == UndoableEditListener.class) {
                        if (event == null) {
                            event = new UndoableEditEvent(this, new EditEvent());
                        }
                        ((UndoableEditListener) listeners[i+1]).undoableEditHappened(event);
                    }
                }
            }
        }
    }

    /**
     * Invoked when the row for the given reference has been updated.
     */
    final void fireTableRowsUpdated(GridCoverageReference entry) {
        entry = unwrap(entry);
        final GridCoverageReference[] entries = this.entries;
        for (int i=entries.length; --i>=0;) {
            if (entry.equals(unwrap(entries[i]))) {
                fireTableRowsUpdated(i, i);
            }
        }
    }

    /**
     * A {@link GridCoverageReference} which intercept the read methods in order to track
     * whatever the reading succeed or failed.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.16
     *
     * @since 3.11 (derived from Seagis)
     * @module
     */
    private final class CoverageProxy extends GridCoverageDecorator {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 8398851451224196337L;

        /** Bit-flag for an entry visited. */ public static final byte VIEWED      = 1;
        /** Bit-flag for a missing file.   */ public static final byte MISSING     = 2;
        /** Bit-flag for a corrupted file. */ public static final byte CORRUPTED   = 4;
        /** Bit-flag for a RMI failure.    */ public static final byte RMI_FAILURE = 8;

        /**
         * The status of this entry as a bit mask.
         */
        byte flags;

        /**
         * Creates a new wrapper for the given entry.
         */
        CoverageProxy(final GridCoverageReference entry) {
            super(entry);
            FileChecker.add(this);
        }

        /**
         * Reads the given image. If the read operation succeed, then the {@link #VIEWED}
         * flag is set. If the read operation fails, then the {@link #CORRUPTED} flag is set.
         */
        @Override
        public Coverage getCoverage(final IIOListeners listeners) throws IOException {
            try {
                final Coverage image = reference.getCoverage(listeners);
                setFlag((byte) (MISSING|CORRUPTED|RMI_FAILURE), false);
                setFlag(VIEWED, image != null);
                return image;
            } catch (RemoteException exception) {
                setFlag(RMI_FAILURE, true);
                throw exception;
            } catch (FileNotFoundException exception) {
                setFlag(MISSING, true);
                throw exception;
            } catch (IOException exception) {
                setFlag(CORRUPTED, true);
                throw exception;
            }
        }

        /**
         * Reads the given image. If the read operation succeed, then the {@link #VIEWED}
         * flag is set. If the read operation fails, then the {@link #CORRUPTED} flag is set.
         */
        @Override
        public GridCoverage2D read(final CoverageEnvelope envelope, final IIOListeners listeners)
                throws CoverageStoreException, CancellationException
        {
            try {
                final GridCoverage2D image = reference.read(envelope, listeners);
                setFlag((byte) (MISSING|CORRUPTED|RMI_FAILURE), false);
                setFlag(VIEWED, image != null);
                return image;
            } catch (CoverageStoreException exception) {
                final Throwable cause = exception.getCause();
                if (cause instanceof RemoteException) {
                    setFlag(RMI_FAILURE, true);
                } else if (cause instanceof FileNotFoundException) {
                    setFlag(MISSING, true);
                } else if (cause instanceof IOException) {
                    setFlag(CORRUPTED, true);
                }
                throw exception;
            }
        }

        /**
         * Sets or clear the given flags. If this method changed the flag state,
         * then {@link #fireTableRowsUpdated} is invoked.
         *
         * {@section Multi-threading}
         * This method may be invoked from any thread: either the Swing thread or the background
         * thread created by {@link FileChecker}. Consequently this method must be thread-safe.
         */
        final synchronized void setFlag(byte f, final boolean set) {
            if (set) f |= flags;
            else f  = (byte) (flags & ~f);
            if (flags != f) {
                flags = f;
                if (EventQueue.isDispatchThread()) {
                    fireTableRowsUpdated(reference);
                } else EventQueue.invokeLater(new Runnable() {
                    @Override public void run() {
                        fireTableRowsUpdated(reference);
                    }
                });
            }
        }
    }

    /**
     * Background task checking for file existence.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.11
     *
     * @since 3.11 (derived from Seagis)
     * @module
     */
    private static final class FileChecker implements Runnable {
        /**
         * The task currently under execution.
         */
        private static FileChecker running;

        /**
         * List of coverage references to check for existence.
         */
        private final LinkedList<CoverageProxy> list = new LinkedList<>();

        /**
         * Only {@link FileChecker} is allowed to instantiate this class.
         */
        private FileChecker() {
        }

        /**
         * Adds an entry to the list of entries pending validity check.
         */
        public static synchronized void add(final CoverageProxy entry) {
            if (entry != null) {
                if (running == null) {
                    running = new FileChecker();
                    Threads.executeWork(running);
                }
                running.list.add(entry);
            }
        }

        /**
         * Returns the next image to check from the given list, or {@code null}
         * if the list is empty. In the later case, the thread will terminate.
         */
        private static synchronized CoverageProxy next(final LinkedList<CoverageProxy> list) {
            if (list.isEmpty()) {
                running = null;
                return null;
            }
            return list.removeFirst();
        }

        /**
         * Checks if the file for the given entry exists. If a file is not found, then the
         * {@link CoverageProxy#MISSING} flag for the corresponding entry will be set.
         */
        @Override
        public void run() {
            CoverageProxy entry;
            while ((entry = next(list)) != null) {
                try {
                    final File file = entry.getFile(File.class);
                    if (file.isAbsolute()) {
                        entry.setFlag(CoverageProxy.MISSING, !file.isFile());
                    }
                } catch (IOException e) {
                    entry.setFlag(CoverageProxy.CORRUPTED, true);
                    Logging.recoverableException(GridCoverageReference.class, "getFile", e);
                }
            }
        }
    }

    /**
     * A {@linkplain TableCellRenderer Table Cell Renderer} for coloring the cells in a table
     * using the {@linkplain CoverageTableModel Coverage Table Model}. Normal rows are rendered
     * with the default color (usually black). The rows corresponding to coverages which have been
     * {@linkplain GridCoverageReference#read read} are written in blue, and the rows corresponding
     * to files not found are written in red.
     *
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.11
     *
     * @since 3.11 (derived from Seagis)
     * @module
     */
    @SuppressWarnings("serial")
    public static class CellRenderer extends DefaultTableCellRenderer {
        /**
         * The default foreground color.
         */
        private Color foreground;

        /**
         * The default background color.
         */
        private Color background;

        /**
         * Creates a new {@code CellRenderer}.
         */
        public CellRenderer() {
            super();
            foreground = super.getForeground();
            background = super.getBackground();
        }

        /**
         * Sets the default foreground color.
         *
         * @param foreground The new foreground color.
         */
        @Override
        public void setForeground(final Color foreground) {
            super.setForeground(this.foreground = foreground);
        }

        /**
         * Sets the default background color.
         *
         * @param background The new background color.
         */
        @Override
        public void setBackground(final Color background) {
            super.setBackground(this.background = background);
        }

        /**
         * Returns the AWT component to use for painting the cell at the given location.
         */
        @Override
        public Component getTableCellRendererComponent(final JTable table, Object value,
                final boolean isSelected, final boolean hasFocus, final int row, final int column)
        {
            Color foreground = this.foreground;
            Color background = this.background;
            if (row >= 0) {
                final TableModel model = table.getModel();
                if (model instanceof CoverageTableModel) {
                    final GridCoverageReference entry;
                    final CoverageTableModel imageTable = (CoverageTableModel) model;
                    if (value instanceof Date) {
                        value = imageTable.format((Date) value);
                    }
                    entry = imageTable.entries[row];
                    if (entry instanceof CoverageProxy) {
                        final byte flags = ((CoverageProxy) entry).flags;
                        if ((flags & CoverageProxy.VIEWED     ) != 0) {foreground=Color.BLUE;}
                        if ((flags & CoverageProxy.MISSING    ) != 0) {foreground=Color.RED;}
                        if ((flags & CoverageProxy.CORRUPTED  ) != 0) {foreground=Color.WHITE; background=Color.RED;}
                        if ((flags & CoverageProxy.RMI_FAILURE) != 0) {foreground=Color.BLACK; background=Color.YELLOW;}
                    }
                }
            }
            super.setBackground(background);
            super.setForeground(foreground);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
