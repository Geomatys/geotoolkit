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

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.AbstractTableModel;
import javax.imageio.spi.ImageReaderSpi;

import org.opengis.util.ProgressListener;

import org.geotoolkit.util.XArrays;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.TileManagerFactory;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.internal.SwingUtilities;


/**
 * A table model for a list of {@linkplain Tile tiles}. The default implementation provides one
 * row for each tile {@linkplain #add(Collection) added} to this model and one column for each
 * tile property listed below. This table model does not allows cell edition but allows row
 * insertion and removal, assuming that the {@linkplain #tiles list of tiles} is modifiable.
 * The default columns are:
 * <p>
 * <ul>
 *   <li>The {@linkplain Tile#getInputName input name}</li>
 *   <li>The {@linkplain Tile#getImageIndex image index}</li>
 *   <li>The {@linkplain Tile#getSize tile size} (two columns for width and height)</li>
 *   <li>The {@linkplain Tile#getLocation tile location} (two columns for <var>x</var> and
 *       <var>y</var>)</li>
 *   <li>The {@linkplain Tile#getSubsampling subsampling} (two columns for <var>x</var> and
 *       <var>y</var> axis)</li>
 * </ul>
 *
 * {@section External list of tiles}
 * A list of tiles is given to the constructor and retained by direct reference - the list is
 * <strong>not</strong> cloned, because it may contains millions of tiles, sometime through a
 * custom {@link List} implementation fetching the information on-the-fly from a database.
 * Consequently if the content of the list is modified externally, then a {@code fireXXX}
 * method (inherited from {@link AbstractTableModel} must be invoked explicitly. Note that
 * the {@code fireXXX} methods don't need to be invoked if the list is modified through the
 * methods provided in this class.
 *
 * {@section Multi-threading}
 * Unless otherwise specified, every methods in this class must be invoked from the Swing
 * thread. The main exceptions are the methods listed below, which should actually be invoked
 * from a background thread rather than the Swing thread:
 * <p>
 * <ul>
 *   <li>{@link #add(ImageReaderSpi, File[], ProgressListener)}</li>
 *   <li>{@link #getTileManager()}</li>
 * </ul>
 *
 * {@section Serialization}
 * This model is serialiable if the underlying list of tiles is serializable.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @see MosaicChooser
 *
 * @since 3.0
 * @module
 */
public class MosaicTableModel extends AbstractTableModel {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 1863722624530354663L;

    /**
     * Dummy dimension to be used when we have determined that no dimension are available.
     */
    private static final Dimension NO_DIMENSION = new Dimension();

    /**
     * Dummy location to be used when we have determined that no location are available.
     */
    private static final Point NO_LOCATION = new Point();

    /**
     * The list of tiles. This is a direct reference to the list given by the user at
     * construction time, not a clone. If this list is modified externaly, then the
     * appropriate {@code fireXXX} method must be invoked explicitly.
     */
    protected final List<Tile> tiles;

    /**
     * An optional locale for column headers and error messages, or {@code null}
     * for the default.
     */
    Locale locale;

    /**
     * The last tile queried by {@link #getValueAt}. If non-null, then the values of
     * {@link #size}, {@link #location} and {@link #subsampling} are about that tile.
     * Those values are saved for avoiding querying the same object many time while
     * rendering the same row.
     */
    private transient Tile last;

    /**
     * The value of {@link Tile#getSize} for the {@linkplain #last} tile.
     */
    private transient Dimension size;

    /**
     * The value of {@link Tile#getLocation} for the {@linkplain #last} tile.
     */
    private transient Point location;

    /**
     * The value of {@link Tile#getSubsampling} for the {@linkplain #last} tile.
     */
    private transient Dimension subsampling;

    /**
     * Creates a new table model backed by an {@link ArrayList} of tiles.
     */
    public MosaicTableModel() {
        tiles = new ArrayList<Tile>();
    }

    /**
     * Creates a new table model for the given list of tiles. The given list is retained
     * by direct reference - it is not cloned. Consequently if the content of this list
     * is modified externaly, then one of the {@code fireXXX} method inherited from
     * {@link AbstractTableModel} must be invoked explicitly.
     *
     * @param tiles The list of tiles to display in a table.
     */
    public MosaicTableModel(final List<Tile> tiles) {
        this.tiles = tiles;
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return The number of rows.
     */
    @Override
    public int getRowCount() {
        return tiles.size();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return The number of columns.
     */
    @Override
    public int getColumnCount() {
        return 8;
    }

    /**
     * Returns the name of the given column.
     *
     * @param column The column.
     * @return The column name.
     */
    @Override
    public String getColumnName(final int column) {
        final int key;
        switch (column) {
            case 0: key = Vocabulary.Keys.FILE;   break;
            case 1: key = Vocabulary.Keys.INDEX;  break;
            case 2: key = Vocabulary.Keys.WIDTH;  break;
            case 3: key = Vocabulary.Keys.HEIGHT; break;
            case 4: return  "x";
            case 5: return  "y";
            case 6: return "sx";
            case 7: return "sy";
            default: throw new IndexOutOfBoundsException(String.valueOf(column));
        }
        return Vocabulary.getResources(locale).getString(key);
    }

    /**
     * Returns the class of values at the given column.
     *
     * @param column The column.
     * @return The classes of values.
     */
    @Override
    public Class<?> getColumnClass(final int column) {
        switch (column) {
            case 0:  return String.class;
            default: return Integer.class;
        }
    }

    /**
     * Returns the value at the given index. This method may return {@code null} if the
     * value at the given cell can not be obtained, for example because of an I/O error.
     *
     * @param row     The row, which is also the index of the tile.
     * @param column  The column.
     * @return The value at the given row and column, or {@code null}.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public Object getValueAt(final int row, final int column) {
        final int value;
        final Tile tile = tiles.get(row);
        if (tile != last) {
            last        = tile;
            size        = null;
            location    = null;
            subsampling = null;
        }
        boolean horizontal = false;
        switch (column) {
            case 0: return tile.getInputName();
            case 1: value = tile.getImageIndex(); break;
            case 2: horizontal = true; // Fall through
            case 3: {
                if (size == null) try {
                    size = tile.getSize();
                } catch (IOException e) {
                    // Can't get the size. For now leave the cell empty. In a future version we
                    // may render the row in a different color to make the error more obvious.
                    size = NO_DIMENSION;
                }
                if (size == NO_DIMENSION) return null;
                value = (horizontal ? size.width : size.height);
                break;
            }
            case 4: horizontal = true; // Fall through
            case 5: {
                if (location == null) try {
                    location = tile.getLocation();
                } catch (IllegalStateException e) {
                    // Same rational than the catch for IOException above.
                    location = NO_LOCATION;
                }
                if (location == NO_LOCATION) return null;
                value = (horizontal ? location.x : location.y);
                break;
            }
            case 6: horizontal = true; // Fall through
            case 7: {
                if (subsampling == null) try {
                    subsampling = tile.getSubsampling();
                } catch (IllegalStateException e) {
                    // Same rational than the catch for IOException above.
                    subsampling = NO_DIMENSION;
                }
                if (subsampling == NO_DIMENSION) return null;
                value = (horizontal ? subsampling.width : subsampling.height);
                break;
            }
            default: {
                throw new IndexOutOfBoundsException(String.valueOf(column));
            }
        }
        return value;
    }

    /**
     * For internal use by {@link MosaicTableModel#getTiles}:
     * asks for the tiles from the Swing thread.
     */
    private final class GetTiles implements Runnable {
        Tile[] array;

        @Override public void run() {
            array = tiles.toArray(new Tile[tiles.size()]);
        }
    }

    /**
     * Returns a snapshot of every tiles contained in this model. This method can be invoked
     * from any thread (not necessarly the Swing thread).
     *
     * @return A snapshot of every tiles in this model.
     */
    public Tile[] getTiles() {
        final GetTiles task = new GetTiles();
        SwingUtilities.invokeAndWait(task);
        return task.array;
    }

    /**
     * Adds tiles to be constructed from the given array of files. Every file in the given array
     * must exist, be a valid image and have a valid <cite>World File</cite>, i.e. a file of the
     * same name in the same directory with {@code ".tfw"} extension (for TIFF images) or
     * {@code ".jgw"} extension (for JPEG images).
     * <p>
     * This method loads the World Files and fetches the image sizes immediatly. Consequently
     * it may be slow and should be invoked in a background thread. After having built the
     * collection of tiles, this method invokes {@link #add(Collection)} in the Swing thread,
     * {@linkplain #removeDuplicates removes duplicates} and {@linkplain #sort sorts} the result.
     * <p>
     * On success, this method returns {@code null}. If this method failed to constructs some
     * tiles, then the sucessful tiles are added as explained above and the unused files are
     * returned in a new table model.
     *
     * @param  provider The image reader provider to use for reading image data, or {@code null}
     *         for attempting an automatic detection.
     * @param  files The files from which to creates tiles.
     * @param  progress An optional listener where to report progress, or {@code null} if none.
     * @throws UnsupportedOperationException if the underlying {@linkplain #tiles list of tiles}
     *         is not modifiable.
     * @return {@code null} on success, or a list of files that failed otherwise.
     */
    public TableModel add(final ImageReaderSpi provider, final File[] files, final ProgressListener progress)
            throws UnsupportedOperationException
    {
        if (progress != null) {
            progress.setTask(Vocabulary.formatInternational(Vocabulary.Keys.LOADING_HEADERS));
            progress.started();
        }
        final List<Tile> toAdd = new ArrayList<Tile>(files.length);
        DefaultTableModel failures = null;
        for (int i=0; i<files.length; i++) {
            final File file = files[i];
            final Tile tile;
            final Dimension size;
            try {
                tile = new Tile(provider, file, 0);
                size = tile.getSize();
            } catch (Exception e) {
                if (progress != null) {
                    String message = e.getLocalizedMessage();
                    if (message == null) {
                        message = Classes.getShortClassName(e);
                    }
                    progress.warningOccurred(file.getName(), null, message);
                }
                /*
                 * The error was reported in the ProgressWindow above, if it was non-null.
                 * In addition creates a table of failures to be returned to the user, to
                 * be used or ignored at user's choice.
                 */
                if (failures == null) {
                    failures = new DefaultTableModel();
                    final Vocabulary resources = Vocabulary.getResources(locale);
                    failures.setColumnIdentifiers(new String[] {
                        resources.getString(Vocabulary.Keys.FILE),
                        resources.getString(Vocabulary.Keys.ERROR),
                        resources.getString(Vocabulary.Keys.MESSAGE)
                    });
                }
                failures.addRow(new String[] {
                    file.getName(),
                    Classes.getShortClassName(e),
                    e.getLocalizedMessage()
                });
                continue;
            }
            /*
             * Adds the tile to the list of tile to be added.
             */
            if (size.width != 0 && size.height != 0) {
                toAdd.add(tile);
            }
            if (progress != null) {
                progress.progress(100f * i/files.length);
                if (progress.isCanceled()) {
                    break;
                }
            }
        }
        /*
         * At this point, we have a list of tiles to be added and we got the metadata
         * that we will need (TFW, image size). Process to the addition in Swing thread,
         * then remove duplicated and sort.
         */
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                add(toAdd);
                removeDuplicates();
                sort();
            }
        });
        if (progress != null) {
            progress.complete();
        }
        return failures;
    }

    /**
     * Adds all tiles from the given collection to the {@linkplain #tiles list of tiles} in this
     * table. The default implementation invokes {@link List#addAll(Collection)} and uses the
     * change of {@linkplain List#size list size} for computing the index to be given to the
     * {@link #fireTableRowsInserted fireTableRowsInserted(int,int)} method. Consequently the
     * list implementation doesn't need to accept every tiles. Note however that the list must
     * not change in a concurrent thread, otherwise the change event to be fired may have an
     * inacurate index range.
     *
     * @param  toAdd The tiles to add.
     * @throws UnsupportedOperationException if the underlying {@linkplain #tiles list of tiles}
     *         is not modifiable.
     */
    public void add(final Collection<? extends Tile> toAdd) throws UnsupportedOperationException {
        if (!toAdd.isEmpty()) {
            final int insertAt = tiles.size();
            if (tiles.addAll(toAdd)) {
                fireTableRowsInserted(insertAt, tiles.size() - 1);
            }
        }
    }

    /**
     * Inserts at the given position of the {@linkplain #tiles list of tiles} all tiles
     * from the given collection. The tiles in this table that are after the insertion
     * point are shifted to larger index.
     * <p>
     * The default implementation invokes {@link List#addAll(int,Collection)} and uses the
     * change of {@linkplain List#size list size} for computing the index to be given to the
     * {@link #fireTableRowsInserted fireTableRowsInserted(int,int)} method. Consequently the
     * list implementation doesn't need to accept every tiles. Note however that the list must
     * not change in a concurrent thread, otherwise the change event to be fired may have an
     * inacurate index range.
     *
     * @param  insertAt The insertion point. The first tile will be inserted at that position.
     * @param  toAdd The tiles to add.
     * @throws UnsupportedOperationException if the underlying {@linkplain #tiles list of tiles}
     *         is not modifiable.
     * @throws IndexOutOfBoundsException if the given index is out of range.
     */
    public void insert(final int insertAt, final Collection<? extends Tile> toAdd)
            throws UnsupportedOperationException, IndexOutOfBoundsException
    {
        if (!toAdd.isEmpty()) {
            int count = tiles.size();
            if (tiles.addAll(insertAt, toAdd)) {
                count = tiles.size() - count;
                fireTableRowsInserted(insertAt, insertAt + count - 1);
            }
        }
    }

    /**
     * Removes a range of rows (or tiles).
     *
     * @param  lower Index of the first row to remove, inclusive.
     * @param  upper Index of the last row to remove, <strong>inclusive</strong>.
     * @throws UnsupportedOperationException if the underlying {@linkplain #tiles list of tiles}
     *         is not modifiable.
     */
    public void remove(final int lower, final int upper) throws UnsupportedOperationException {
        if (lower == upper) {
            tiles.remove(lower);
        } else {
            tiles.subList(lower, upper+1).clear();
        }
        fireTableRowsDeleted(lower, upper);
    }

    /**
     * Removes the given elements from the {@linkplain #tiles list of tiles}.
     *
     * @param  indices The index of elements to remove.
     * @throws UnsupportedOperationException if the underlying {@linkplain #tiles list of tiles}
     *         is not modifiable.
     */
    public void remove(int[] indices) throws UnsupportedOperationException {
        // We must iterate in reverse order, because the
        // index after the removed elements will change.
        int i = indices.length;
        if (i != 0) {
            if (!XArrays.isSorted(indices, false)) {
                indices = indices.clone();
                Arrays.sort(indices);
            }
            int upper = indices[--i];
            int lower = upper;
            while (i != 0) {
                int previous = indices[--i];
                if (previous != lower - 1) {
                    remove(lower, upper);
                    upper = previous;
                }
                lower = previous;
            }
            remove(lower, upper);
        }
    }

    /**
     * Removes duplicated tiles. If two tiles are equal in the sense of {@link Tile#equals(Object)}
     * method, then the first one is removed from the {@linkplain #tiles list of tiles}. We keep the
     * last tile instead of the first one on the assumption that the last tile is the most recently
     * added.
     *
     * @return {@code true} if at least one tile has been removed as a result of this call.
     * @throws UnsupportedOperationException if the underlying {@linkplain #tiles list of tiles}
     *         is not modifiable.
     */
    public boolean removeDuplicates() throws UnsupportedOperationException {
        int count = 0;
        final int[] indices = new int[tiles.size()];
        final Map<Tile,Integer> previous = new HashMap<Tile,Integer>();
        final ListIterator<Tile> it = tiles.listIterator();
        while (it.hasNext()) {
            Tile tile = it.next();
            final Integer p = previous.put(tile, it.previousIndex());
            if (p != null) {
                indices[count++] = p;
            }
        }
        if (count == 0) {
            return false;
        }
        remove(XArrays.resize(indices, count));
        return true;
    }

    /**
     * Sorts the tiles by their input name first. The complete list of criterions is
     * documented in the {@link Tile#compareTo(Tile)} method.
     *
     * @return {@code true} if the row order changed as a result of this method call.
     * @throws UnsupportedOperationException if the underlying {@linkplain #tiles list of tiles}
     *         is not modifiable.
     */
    public boolean sort() throws UnsupportedOperationException {
        boolean changed = false;
        if (!tiles.isEmpty()) {
            final Tile[] array = tiles.toArray(new Tile[tiles.size()]);
            Arrays.sort(array);
            final ListIterator<Tile> it = tiles.listIterator();
            for (int i=0; i<array.length; i++) {
                final Tile t = array[i];
                if (it.next() != t) {
                    it.set(t);
                    changed = true;
                }
            }
            if (changed) {
                fireTableRowsUpdated(0, tiles.size() - 1);
            }
        }
        return changed;
    }

    /**
     * Returns a tile manager for the current {@linkplain #tiles list of tiles}. This method
     * should return an array of length 1, but a different length may be obtained if it was
     * not possible to create a single tile manager.
     * <p>
     * Consider invoking {@link #removeDuplicates} before this method, since duplicated
     * tiles are likely to produce erroneous tile manager. Consider also invoking this
     * {@code getTileManager()} method from a background thread, since it may be slow
     * (it is safe to invoke this particular method from non-Swing thread).
     *
     * @return The tile managers for the current list of tiles.
     * @throws IOException if an error occured while reading a tile.
     */
    public TileManager[] getTileManager() throws IOException {
        final TileManager[] managers = TileManagerFactory.DEFAULT.create(getTiles());
        /*
         * TileManagerFactory has computed some properties previously unavailable,
         * like the tile location computed from the "gridToCRS" affine transform.
         * Fire a "row updates" event so we can see the updated values in the table.
         */
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                fireTableRowsUpdated(0, tiles.size()-1);
            }
        });
        return managers;
    }
}
