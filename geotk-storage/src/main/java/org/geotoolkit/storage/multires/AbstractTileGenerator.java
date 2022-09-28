/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.multires;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.IllegalGridGeometryException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.storage.tiling.WritableTileMatrix;
import org.apache.sis.storage.tiling.WritableTileMatrixSet;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessListener;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractTileGenerator implements TileGenerator {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.storage.multires");

    protected static final Process DUMMY = new Process() {
        @Override
        public ProcessDescriptor getDescriptor() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ParameterValueGroup getInput() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ParameterValueGroup call() throws ProcessException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ProcessStep getMetadata() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void addListener(ProcessListener listener) {
        }

        @Override
        public void removeListener(ProcessListener listener) {
        }

        @Override
        public ProcessListener[] getListeners() {
            throw new UnsupportedOperationException("Not supported.");
        }
    };

    protected boolean skipEmptyTiles = false;
    protected boolean skipExistingTiles = false;

    public AbstractTileGenerator() {
    }

    public void skipEmptyTiles(boolean b) {
        skipEmptyTiles = b;
    }


    /**
     * Indicate if existing tiles should be regenerated.
     *
     * @param skipExistingTiles
     */
    public void setSkipExistingTiles(boolean skipExistingTiles) {
        this.skipExistingTiles = skipExistingTiles;
    }

    /**
     * @return true if existing tiles are skipped
     */
    public boolean isSkipExistingTiles() {
        return skipExistingTiles;
    }

    @Override
    public void generate(WritableTileMatrixSet pyramid, Envelope env, NumberRange resolutions,
            ProcessListener listener) throws DataStoreException, InterruptedException {

        if (env != null) {
            try {
                env = Envelopes.transform(env, pyramid.getCoordinateReferenceSystem());
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        final long total = TileMatrices.countTiles(pyramid, env, resolutions);
        final double totalAsDouble = total;
        final AtomicLong al = new AtomicLong();
        final Supplier<Float> progress = () -> (float) (al.get() / totalAsDouble *100.0);

        //generate mosaic in resolution order
        //this order allows the pyramid to be used at high scales until she is not completed.
        final List<WritableTileMatrix> mosaics = new ArrayList<>(pyramid.getTileMatrices().values());
        mosaics.sort(TileMatrices.SCALE_COMPARATOR.reversed());
        for (final WritableTileMatrix tileMatrix : mosaics) {
            if (resolutions == null || resolutions.containsAny(TileMatrices.getScale(tileMatrix))) {

                final GridExtent rect;
                try {
                    rect = TileMatrices.getTilesInEnvelope(tileMatrix, env);
                } catch (NoSuchDataException ex) {
                    continue;
                }

                final long nbTile = TileMatrices.countCells(rect);
                final long eventstep = Math.min(1000, Math.max(1, nbTile/100l));
                Stream<Tile> stream = LongStream.range(0, nbTile).parallel()
                        .mapToObj(new LongFunction<Tile>() {
                            @Override
                            public Tile apply(long value) {
                                final long x = rect.getLow(0) + (value % rect.getSize(0));
                                final long y = rect.getLow(1) + (value / rect.getSize(0));
                                final long[] indices = new long[]{x, y};

                                Tile data = null;
                                try {
                                    if (skipExistingTiles) {
                                        try {
                                            if (TileStatus.EXISTS.equals(tileMatrix.getTileStatus(indices))) {
                                                //tile already exist
                                                return null;
                                            }
                                        } catch (DataStoreException ex) {
                                            //just log it, consider the tile do not exist
                                            LOGGER.warning(ex.getMessage());
                                            if (listener != null) {
                                                long v = al.incrementAndGet();
                                                listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" TileMatrix="+tileMatrix.getIdentifier(), progress.get(), ex));
                                            }
                                        }
                                    }

                                    try {
                                        data = generateTile(pyramid, tileMatrix, indices);
                                    } catch (Exception ex) {
                                        data = TileInError.create(indices, null, ex);
                                    }
                                } finally {
                                    long v = al.incrementAndGet();
                                    if (listener != null & (v % eventstep == 0))  {
                                        listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" TileMatrix="+tileMatrix.getIdentifier(), progress.get()));
                                    }
                                }
                                return data;
                            }
                        })
                        .filter(this::emptyFilter);

                streamWrite(stream, tileMatrix, listener == null ? null : err -> listener.progressing(new ProcessEvent(DUMMY, "Error while writing tile batch", progress.get())));

                long v = al.get();
                if (listener != null) {
                    listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" TileMatrix="+tileMatrix.getIdentifier(), progress.get()  ));
                }
            }
        }
    }

    protected void streamWrite(Stream<Tile> source, WritableTileMatrix destination, Consumer<Exception> errorHandler) {

        try {
            destination.writeTiles(source.parallel());
        } catch (DataStoreException ex) {
            LOGGER.log(Level.WARNING, "Failed to write tile batch", ex);
            if (errorHandler != null) errorHandler.accept(ex);
        }
    }

    // TODO: we should be able to simplify this if we transmit correctly empty tiles and tiles in error.
    protected final boolean emptyFilter(Tile data) {
        if (data == null) return false;
        if (data instanceof TileInError) {
            final TileInError error = (TileInError) data;
            final boolean errorDueToNoDataAvailable = (error.getCause() instanceof NoSuchDataException || error.getCause() instanceof IllegalGridGeometryException);
            LOGGER.log(errorDueToNoDataAvailable ? Level.FINER : Level.WARNING, error.getCause(), () -> String.format("Error on tile loading:%nTile coordinate: %s", Arrays.toString(error.getIndices())));
            if (!errorDueToNoDataAvailable) return false;
        }
        try {
            return !(skipEmptyTiles && (data instanceof EmptyTile || isEmpty(data)));
        } catch (DataStoreException ex) {
            throw new BackingStoreException(ex.getMessage(), ex);
        }
    }

    protected abstract boolean isEmpty(Tile tile) throws DataStoreException;

}
