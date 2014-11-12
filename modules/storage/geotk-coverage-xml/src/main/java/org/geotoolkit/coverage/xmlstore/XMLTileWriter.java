/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.coverage.xmlstore;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.io.XImageIO;

import javax.imageio.ImageWriter;
import java.awt.image.RenderedImage;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

/**
 * A thread which will write given tiles into the specified pyramid set.
 * /!\ It won't stop until you provide it a poisonous object {@link org.geotoolkit.coverage.xmlstore.XMLTileWriter.MissingInfo}.
 *
 * @author Alexis Manin (Geomatys)
 */
public class XMLTileWriter implements Runnable {

    protected XMLCoverageReference targetRef;
    protected BlockingQueue<XMLTileInfo> tileQueue;

    /**
     * A new runnable which role is to write tiles into a specific pyramid set.
     * @param tileQueue The queue containing the tiles to write as all needed information to do so.
     * @param targetRef The reference to the destination pyramid set.
     */
    XMLTileWriter(BlockingQueue<XMLTileInfo> tileQueue, XMLCoverageReference targetRef) {
        ArgumentChecks.ensureNonNull("Tile queue", tileQueue);
        ArgumentChecks.ensureNonNull("Pyramid set reference", targetRef);
        this.tileQueue = tileQueue;
        this.targetRef = targetRef;
    }

    @Override
    public void run() {
        final Thread currentThread = Thread.currentThread();
        ImageWriter writer = null;
        try {
            writer = XImageIO.getWriterByFormatName(targetRef.getPyramidSet().getFormatName(), null, null);
            takeMethod(writer, currentThread);
        } catch (InterruptedException e) {
            currentThread.interrupt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) writer.dispose();
        }
    }

    /**
     * Core method for tile writing. This function uses the "draining algorithm", which will peek tiles by set.
     *
     * @param writer        The {@link javax.imageio.ImageWriter} to use to write tiles in the mosaics.
     * @param currentThread The current thread in which the runnable is running. Used to get interruption status.
     * @throws DataStoreException If tile creation fails.
     */
    private void drainMethod(final ImageWriter writer, final Thread currentThread) throws DataStoreException {
        final LinkedList<XMLTileInfo> tiles = new LinkedList<>();
        XMLTileInfo info;
        try {
            while (!currentThread.isInterrupted()) {
                tileQueue.drainTo(tiles);
                while ((info = tiles.poll()) != null) {
                    if (info instanceof MissingInfo || currentThread.isInterrupted()) {
                        return;
                    }
                    info.mosaic.createTile(info.tX, info.tY, info.data, writer);
                }
            }
        } finally {
            targetRef.save();
        }
    }

    /**
     * Core method for tile writing. This function uses a simple algorithm which will peek tiles one by one.
     *
     * @param writer        The {@link javax.imageio.ImageWriter} to use to write tiles in the mosaics.
     * @param currentThread The current thread in which the runnable is running. Used to get interruption status.
     * @throws DataStoreException   If tile creation fails.
     * @throws InterruptedException If the thread in which we run has been intercepted while we were waiting for a tile from the queue.
     */
    private void takeMethod(final ImageWriter writer, final Thread currentThread) throws DataStoreException, InterruptedException {
        XMLTileInfo info;
        try {
            while (!currentThread.isInterrupted()) {
                info = tileQueue.take();
                if (info == null || info instanceof MissingInfo) {
                    break;
                }
                info.mosaic.createTile(info.tX, info.tY, info.data, writer);
            }
        } finally {
            targetRef.save();
        }
    }

    /**
     * An object which aim is to contain a tile to write, as all the information about its position in the target pyramid set.
     */
    public static class XMLTileInfo {
        int tX;
        int tY;
        final XMLMosaic mosaic;
        RenderedImage data;

        public XMLTileInfo(int tX, int tY, RenderedImage data, XMLMosaic mosaic) {
            this.tX = tX;
            this.tY = tY;
            this.data = data;
            this.mosaic = mosaic;
        }
    }

    /**
     * A poisonous object to specify to the thread that we don't have any more data to write.
     */
    public static class MissingInfo extends XMLTileInfo {

        public MissingInfo() {
            super(-1, -1, null, null);
        }
    }
}

