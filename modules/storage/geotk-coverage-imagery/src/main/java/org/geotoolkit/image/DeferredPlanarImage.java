/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.*;
import java.util.Map;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

import javax.media.jai.PlanarImage;
import javax.media.jai.ImageLayout;
import javax.media.jai.TileRequest;
import javax.media.jai.TileScheduler;
import javax.media.jai.TileComputationListener;

import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Disposable;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.Classes;
import org.apache.sis.util.collection.WeakValueHashMap;
import org.geotoolkit.image.color.ColorUtilities;
import org.geotoolkit.resources.Loggings;

import static java.awt.image.DataBuffer.*;


/**
 * A tiled image to be used by renderer when the actual image may take a while to compute. This
 * image wraps an arbitrary {@linkplain RenderedImage rendered image}, which may (or may not) be
 * some image expensive to compute. When a tile is requested (through a call to {@link #getTile}
 * but the tile is not available in the wrapped image, then this class returns some default
 * (usually black) tile and start the real tile computation in a background thread. When the
 * actual tile is available, this class fire a {@link TileObserver#tileUpdate tileUpdate} event,
 * thus given a chance to a renderer to repaint again this image with the new tiles.
 * <p>
 * Example of use:
 *
 * {@preformat java
 *     public class Renderer extends JPanel implements TileObserver {
 *         private DeferredPlanarImage image;
 *
 *         public Renderer(RenderedImage toPaint) {
 *             image = new DeferredPlanarImage(toPaint);
 *             image.addTileObserver(this);
 *         }
 *
 *         public void tileUpdate(WritableRenderedImage source,
 *                 int tileX, int tileY, boolean willBeWritable)
 *         {
 *             repaint();
 *         }
 *
 *         public void paint(Graphics gr) {
 *             ((Graphics2D) gr).drawRenderedImage(image);
 *         }
 *     }
 * }
 *
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 * @module
 */
public final class DeferredPlanarImage extends PlanarImage
        implements WritableRenderedImage, TileObserver, TileComputationListener, Disposable
{
    /**
     * The logger for information messages.
     */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.image");

    /**
     * The thickness (in pixels) of the box to draw around deferred tiles, or 0 for disabling
     * this feature. Current implementation draw a box only for {@link DataBufferByte} with
     * only one band.
     */
    private static final int BOX_THICKNESS = 2;

    /**
     * An entry in the {@link #buffers} map. Contains a sample model and the sample value
     * used for filling the empty {@link DataBuffer} (usually 0, unless the color model had
     * a transparent pixel different from 0).
     */
    private static final class Entry {
        /** The sample model. */ public final SampleModel model;
        /** The fill value.   */ public final int fill;
        /** The box value.    */ public final int box;

        /** Constructs a new entry. */
        public Entry(final SampleModel model, final int fill, final int box) {
            this.model = model;
            this.fill  = fill;
            this.box   = box;
        }

        /** Returns a hash code value for this entry. */
        @Override
        public int hashCode() {
            return model.hashCode();
        }

        /** Compares this entry with the specified object. */
        @Override
        public boolean equals(final Object object) {
            if (object instanceof Entry) {
                final Entry that = (Entry) object;
                return model.equals(that.model) && fill == that.fill && box == that.box;
            }
            return false;
        }
    }

    /**
     * Empty {@link DataBuffer} for a set of {@link SampleModel}.
     * Will be created only when first needed.
     */
    private static Map<Entry,DataBuffer> buffers;

    /**
     * The maximum delay (in milliseconds) to wait for a tile with one million pixels (e.g.
     * a 1000&times;1000 tile). The actual {@link #delay} will be shorter if the tiles are
     * smaller; for example the delay is four time smaller for a 500&times;500 tile.  When
     * a requested tile is not yet available, the {@link #getTile} method will wait for a
     * maximum of {@code DELAY} milliseconds in case the tile computation would be very
     * fast. Set the delay to 0 in order to disable this feature.
     *
     * {@note Must be of type <code>long</code> even if <code>int</code> would be sufficient,
     *        if order to force widening conversions.}
     */
    private static final long DELAY = 500;

    /**
     * The delay (in milliseconds) to wait for a tile. When a requested tile is not yet available,
     * the {@link #getTile} method will wait for a maximum of {@code delay} milliseconds in
     * case the tile computation would be very fast. Set the delay to 0 in order to disable this
     * feature.
     */
    private final int delay;

    /**
     * The source image.
     */
    private final PlanarImage image;

    /**
     * The tile observers, or {@code null} if none.
     */
    private TileObserver[] observers;

    /**
     * The {@link TileRequest}s for a given tile.
     * Tile index are computed by {@link #getTileIndex}.
     * This array will be constructed only when first needed.
     */
    private transient TileRequest[] requests;

    /**
     * Tells if a tile is request is waiting.
     * Tile index are computed by {@link #getTileIndex}.
     * This array will be constructed only when first needed.
     */
    private transient boolean[] waitings;

    /**
     * Tells if a tile is in process of being computed.
     * Tile index are computed by {@link #getTileIndex}.
     * This array will be constructed only when first needed.
     */
    private transient Raster[] pendings;

    /**
     * Constructs a new instance of {@code DeferredPlanarImage}.
     *
     * @param source The source image.
     */
    public DeferredPlanarImage(final RenderedImage source) {
        super(new ImageLayout(source), toVector(source), null);
        image = getSourceImage(0);
        image.addTileComputationListener(this);
        if (image instanceof WritableRenderedImage) {
            ((WritableRenderedImage) image).addTileObserver(this);
        }
        delay = (int) Math.min(DELAY * tileWidth * tileHeight / 1000000, DELAY);
    }

    /**
     * Wraps the specified image in a vector.
     *
     * @todo Should be inlined in the constructor if only Sun was to fix RFE #4093999
     *       ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Vector<RenderedImage> toVector(final RenderedImage image) {
        final Vector<RenderedImage> vector = new Vector<>(1);
        vector.add(image);
        return vector;
    }

    /**
     * Returns the source images.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Vector<RenderedImage> getSources() {
        return super.getSources();
    }

    /**
     * Returns the index in {@link #requests} and {@link #pendings} array for the given tile.
     * The {@code x} index varies fastest.
     */
    private int getTileIndice(final int tileX, final int tileY) {
        assert tileX >= getMinTileX() && tileX <= getMaxTileX() : tileX;
        assert tileY >= getMinTileY() && tileY <= getMaxTileY() : tileY;
        return (tileY - getMinTileY()) * getNumXTiles() +
               (tileX - getMinTileX());
    }

    /**
     * Returns the specified tile, or a default one if the requested tile is not yet available.
     * If the requested tile is not immediately available, then an empty tile is returned and
     * a notification will be sent later through {@link TileObserver} when the real tile will
     * be available.
     *
     * @param  tileX Tile X index.
     * @param  tileY Tile Y index.
     * @return The requested tile.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public synchronized Raster getTile(final int tileX, final int tileY) {
        if (requests == null) {
            requests = new TileRequest[getNumXTiles() * getNumYTiles()];
        }
        final int tileIndex = getTileIndice(tileX, tileY);
        TileRequest request = requests[tileIndex];
        if (request == null) {
            request = image.queueTiles(new Point[]{new Point(tileX, tileY)});
            requests[tileIndex] = request;
        }
        switch (request.getTileStatus(tileX, tileY)) {
            default: {
                LOGGER.warning("Unknow tile status");
                // Fall through
            }
            case TileRequest.TILE_STATUS_CANCELLED:  // Fall through
            case TileRequest.TILE_STATUS_FAILED:     // Fall through
            case TileRequest.TILE_STATUS_COMPUTED:   return image.getTile(tileX, tileY);
            case TileRequest.TILE_STATUS_PENDING:    // Fall through
            case TileRequest.TILE_STATUS_PROCESSING: break;
        }
        /*
         * The tile is not yet available. A background thread should be computing it right
         * now. Wait a little bit in case the tile computation is very fast. If we can get
         * the tile in a very short time, it would be more efficient than invoking some
         * 'repaint()' method later.
         */
        if (pendings != null) {
            if (pendings[tileIndex] != null) {
                return pendings[tileIndex];
            }
        }
        if (delay != 0) {
            if (waitings == null) {
                waitings = new boolean[requests.length];
            }
            waitings[tileIndex] = true;
            try {
                wait(delay);
            } catch (InterruptedException exception) {
                // Somebody doesn't want to lets us sleep. Go back to work.
            }
            waitings[tileIndex] = false;
            switch (request.getTileStatus(tileX, tileY)) {
                default: return image.getTile(tileX, tileY);
                case TileRequest.TILE_STATUS_PENDING:    // Fall through
                case TileRequest.TILE_STATUS_PROCESSING: break;
            }
        }
        /*
         * The tile is not yet available and seems to take a long time to compute.
         * Flag that this tile will need to be repainted later and returns an empty tile.
         */
        if (LOGGER.isLoggable(Level.FINER)) {
            final LogRecord record = Loggings.format(Level.FINER,
                    Loggings.Keys.DeferredTilePainting_2, tileX, tileY);
            record.setSourceClassName(DeferredPlanarImage.class.getName());
            record.setSourceMethodName("getTile");
            record.setLoggerName(LOGGER.getName());
            LOGGER.log(record);
        }
        if (pendings == null) {
            pendings = new Raster[requests.length];
        }
        final Point      origin = new Point(tileXToX(tileX), tileYToY(tileY));
        final DataBuffer buffer = getDefaultDataBuffer(sampleModel, colorModel);
        final Raster     raster = Raster.createRaster(sampleModel, buffer, origin);
        pendings[tileIndex] = raster;
        fireTileUpdate(tileX, tileY, true);
        return raster;
    }

    /**
     * Returns a databuffer for the specified sample model. If the image uses an
     * {@link IndexColorModel} and a {@linkplain IndexColorModel#getTransparentPixel
     * transparent pixel} is defined, then raster sample values are initialized to
     * the transparent pixel.
     */
    private static synchronized DataBuffer getDefaultDataBuffer(
            final SampleModel sampleModel, final ColorModel colorModel)
    {
        int fill = 0;
        int box  = 0;
        if (colorModel instanceof IndexColorModel) {
            final IndexColorModel colors = (IndexColorModel) colorModel;
            fill = ColorUtilities.getTransparentPixel(colors);
            if (BOX_THICKNESS > 0 && Math.min(sampleModel.getWidth(), sampleModel.getHeight()) >= 64) {
                box = ColorUtilities.getColorIndex(colors, Color.DARK_GRAY, fill);
            } else {
                // Avoid drawing the box if tiles are too small.
                box = fill;
            }
        }
        final Entry entry = new Entry(sampleModel, fill, box);
        if (buffers == null) {
            buffers = new WeakValueHashMap<>(Entry.class);
        }
        DataBuffer buffer = buffers.get(entry);
        if (buffer != null) {
            return buffer;
        }
        /*
         * No suitable data buffer existed prior to this call. Create a new one and fill it
         * with the transparent color. Note that no filling is needed if the transparent value
         * is 0, since the data buffer is already initialized to 0.
         */
        buffer = sampleModel.createDataBuffer();
        if (fill > 0) {
            for (int bank=buffer.getNumBanks(); --bank>=0;) {
                fill(buffer, bank, fill);
            }
        }
        /*
         * Draw a box around the tile. This is just a visual clue about tile location.
         * Current implementation draw a box only for type byte with a single band.
         */
        if (BOX_THICKNESS > 0 && box != fill) {
            if (sampleModel.getNumBands() == 1) {
                final int width = sampleModel.getWidth();
                int thickness = BOX_THICKNESS;
                int offset = (width + 1) * thickness;
                switch (buffer.getDataType()) {
                    case TYPE_BYTE: {
                        final byte[] array = ((DataBufferByte) buffer).getData(0);
                        Arrays.fill(array, 0, offset, (byte) box);
                        Arrays.fill(array, array.length-offset, array.length, (byte) box);
                        thickness *= 2;
                        while ((offset += width) < array.length) {
                            Arrays.fill(array, offset-thickness, offset, (byte) box);
                        }
                        break;
                    }
                }
            }
        }
        buffers.put(entry, buffer);
        return buffer;
    }

    /**
     * Sets all values in the specified bank to the specified value.
     *
     * @param buffer The databuffer in which to set all sample values.
     * @param bank   Index of the bank to set.
     * @param value  The value.
     */
    private static void fill(final DataBuffer buffer, final int bank, final int value) {
        switch (buffer.getDataType()) {
            case TYPE_BYTE :  Arrays.fill(((DataBufferByte)   buffer).getData(bank),   (byte) value); break;
            case TYPE_SHORT:  Arrays.fill(((DataBufferShort)  buffer).getData(bank),  (short) value); break;
            case TYPE_USHORT: Arrays.fill(((DataBufferUShort) buffer).getData(bank),  (short) value); break;
            case TYPE_INT:    Arrays.fill(((DataBufferInt)    buffer).getData(bank),          value); break;
            case TYPE_FLOAT:  Arrays.fill(((DataBufferFloat)  buffer).getData(bank),  (float) value); break;
            case TYPE_DOUBLE: Arrays.fill(((DataBufferDouble) buffer).getData(bank), (double) value); break;
            default: throw new RasterFormatException(String.valueOf(buffer));
        }
    }

    /**
     * A tile is about to be updated (it is either about to be grabbed for writing,
     * or it is being released from writing).
     */
    private void fireTileUpdate(final int tileX, final int tileY, final boolean willBeWritable) {
        final TileObserver[] observers = this.observers; // Avoid the need for synchronization.
        if (observers != null) {
            final int length = observers.length;
            for (int i=0; i<length; i++) {
                try {
                    observers[i].tileUpdate(this, tileX, tileY, willBeWritable);
                } catch (RuntimeException cause) {
                    /*
                     * An exception occurred in the user code. Unfortunately, we are probably not in
                     * the mean user thread (e.g. the Swing thread).  This method is often invoked
                     * from some JAI's worker thread, which we don't want to corrupt. Log a warning
                     * for the user and lets the JAI's worker thread continue its work.
                     */
                    String message = cause.getLocalizedMessage();
                    if (message == null) {
                        message = Classes.getShortClassName(cause);
                    }
                    final LogRecord record = new LogRecord(Level.WARNING, message);
                    record.setSourceClassName(observers[i].getClass().getCanonicalName());
                    record.setSourceMethodName("tileUpdate");
                    record.setThrown(cause);
                    record.setLoggerName(LOGGER.getName());
                    LOGGER.log(record);
                }
            }
        }
    }

    /**
     * Invoked when a tile has been computed.
     *
     * @param eventSource The caller of this method.
     * @param requests    The relevant tile computation requests as returned by the method used to queue the tile.
     * @param image       The image for which tiles are being computed as specified to the {@link TileScheduler}.
     * @param tileX       The X index of the tile in the tile array.
     * @param tileY       The Y index of the tile in the tile array.
     * @param tile        The computed tile.
     */
    @Override
    public void tileComputed(final Object eventSource, final TileRequest[] requests,
            final PlanarImage image, final int tileX, final int tileY, final Raster tile)
    {
        synchronized (this) {
            final int tileIndice = getTileIndice(tileX, tileY);
            if (waitings != null && waitings[tileIndice]) {
                /*
                 * Notify the 'getTile(...)' method in only ONE thread that a tile is available.
                 * If tiles computation occurs in two or more background thread, then there is no
                 * guarantee that the notified thread is really the one waiting for this particular
                 * tile. However, this is not a damageable problem; the delay hint may just not be
                 * accuratly respected (the actual delay may be shorter for wrongly notified tile).
                 */
                notify();
            }
            if (pendings == null || pendings[tileIndice] == null) {
                return;
            }
            pendings[tileIndice] = null;
        }
        fireTileUpdate(tileX, tileY, false);
    }

    /**
     * Invoked when a tile computation has been canceled. The default implementation does nothing.
     *
     * @param eventSource The caller of this method.
     * @param requests    The relevant tile computation requests as returned by the method used to queue the tile.
     * @param image       The image for which tiles are being computed as specified to the {@link TileScheduler}.
     * @param tileX       The X index of the tile in the tile array.
     * @param tileY       The Y index of the tile in the tile array.
     */
    @Override
    public void tileCancelled(final Object eventSource, final TileRequest[] requests,
            final PlanarImage image, final int tileX, final int tileY)
    {
    }

    /**
     * Invoked when a tile computation failed. Default implementation log a warning and lets the
     * program continue as usual. We are not throwing an exception since this failure will alter
     * the visual rendering, but will not otherwise harm the system.
     *
     * @param eventSource The caller of this method.
     * @param requests    The relevant tile computation requests as returned by the method used to queue the tile.
     * @param image       The image for which tiles are being computed as specified to the {@link TileScheduler}.
     * @param tileX       The X index of the tile in the tile array.
     * @param tileY       The Y index of the tile in the tile array.
     * @param cause       The cause of the failure.
     */
    @Override
    public void tileComputationFailure(final Object eventSource, final TileRequest[] requests,
            final PlanarImage image, final int tileX, final int tileY, final Throwable cause)
    {
        final LogRecord record = new LogRecord(Level.WARNING, cause.getLocalizedMessage());
        record.setSourceClassName(DeferredPlanarImage.class.getName());
        record.setSourceMethodName("getTile");
        record.setThrown(cause);
        record.setLoggerName(LOGGER.getName());
        LOGGER.log(record);
    }

    /**
     * Invoked if the underlying image is writable and one of its tile changed.
     * This method forward the call to every registered listener.
     */
    @Override
    public void tileUpdate(final WritableRenderedImage source,
            final int tileX, final int tileY, final boolean willBeWritable)
    {
        fireTileUpdate(tileX, tileY, willBeWritable);
    }

    /**
     * Adds an observer. This observer will be notified everytime a tile initially empty become
     * available. If the observer is already present, it will receive multiple notifications.
     *
     * @param observer The observer to add.
     */
    @Override
    public synchronized void addTileObserver(final TileObserver observer) {
        if (observer != null) {
            if (observers == null) {
                observers = new TileObserver[] {observer};
            } else {
                observers = ArraysExt.append(observers, observer);
            }
        }
    }

    /**
     * Removes an observer. If the observer was not registered, nothing happens.
     * If the observer was registered for multiple notifications, it will now be
     * registered for one fewer.
     *
     * @param observer The observer to remove.
     */
    @Override
    public synchronized void removeTileObserver(final TileObserver observer) {
        if (observers != null) {
            for (int i=observers.length; --i>=0;) {
                if (observers[i] == observer) {
                    observers = ArraysExt.remove(observers, i, 1);
                    break;
                }
            }
        }
    }

    /**
     * Checks out a tile for writing. Since {@code DeferredPlanarImage} are not really
     * writable, this method throws an {@link UnsupportedOperationException}.
     */
    @Override
    public WritableRaster getWritableTile(final int tileX, final int tileY) {
        throw new UnsupportedOperationException();
    }

    /**
     * Relinquishes the right to write to a tile. Since {@code DeferredPlanarImage} are
     * not really writable, this method throws an {@link IllegalStateException} (the state is
     * really illegal since {@link #getWritableTile} should never have succeeded).
     */
    @Override
    public void releaseWritableTile(final int tileX, final int tileY) {
        throw new IllegalStateException();
    }

    /**
     * Returns whether any tile is checked out for writing.
     */
    @Override
    public synchronized boolean hasTileWriters() {
        final Raster[] pendings = this.pendings;
        if (pendings != null) {
            final int length = pendings.length;
            for (int i=0; i<length; i++) {
                if (pendings[i] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether a tile is currently checked out for writing.
     */
    @Override
    public synchronized boolean isTileWritable(final int tileX, final int tileY) {
        final Raster[] pendings = this.pendings;
        return pendings != null && pendings[getTileIndice(tileX, tileY)] != null;
    }

    /**
     * Returns an array of {@link Point} objects indicating which tiles are
     * checked out for writing. Returns null if none are checked out.
     */
    @Override
    public synchronized Point[] getWritableTileIndices() {
        final Raster[] pendings = this.pendings;
        Point[] indices = null;
        if (pendings != null) {
            int count = 0;
            final int minX = getMinTileX();
            final int minY = getMinTileY();
            final int numX = getNumXTiles();
            final int length = pendings.length;
            for (int i=0; i<length; i++) {
                if (pendings[i] != null) {
                    if (indices == null) {
                        indices = new Point[length - i];
                    }
                    final int x = i % numX + minX;
                    final int y = i / numX + minY;
                    assert getTileIndice(x,y) == i : i;
                    indices[count++] = new Point(x,y);
                }
            }
            if (indices != null) {
                indices = ArraysExt.resize(indices, count);
            }
        }
        return indices;
    }

    /**
     * Sets a rectangle of the image to the contents of the raster. Since
     * {@code DeferredPlanarImage} are not really writable, this method
     * throws an {@link UnsupportedOperationException}.
     */
    @Override
    public void setData(Raster r) {
        throw new UnsupportedOperationException();
    }

    /**
     * Provides a hint that this image will no longer be accessed from a reference in user space.
     * <strong>NOTE: this method dispose the image given to the constructor as well.</strong>
     * This is because {@code DeferredPlanarImage} is used as a "view" of an other
     * image, and the user shouldn't know that he is not using directly the other image.
     */
    @Override
    public synchronized void dispose() {
        if (image instanceof WritableRenderedImage) {
            ((WritableRenderedImage) image).removeTileObserver(this);
        }
        image.removeTileComputationListener(this);
        requests = null;
        waitings = null;
        pendings = null;
        super.dispose();
        image.dispose();
    }
}
