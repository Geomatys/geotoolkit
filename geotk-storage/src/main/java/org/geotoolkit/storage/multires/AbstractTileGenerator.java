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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.IllegalGridGeometryException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessListener;
import org.geotoolkit.util.Streams;
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

    public AbstractTileGenerator() {
    }

    public void skipEmptyTiles(boolean b) {
        skipEmptyTiles = b;
    }

    @Override
    public void generate(TileMatrixSet pyramid, Envelope env, NumberRange resolutions,
            ProcessListener listener) throws DataStoreException, InterruptedException {

        if (env != null) {
            try {
                env = Envelopes.transform(env, pyramid.getCoordinateReferenceSystem());
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        final long total = TileMatrices.countTiles(pyramid, env, resolutions);
        final AtomicLong al = new AtomicLong();

        //generate mosaic in resolution order
        //this order allows the pyramid to be used at high scales until she is not completed.
        final List<TileMatrix> mosaics = new ArrayList<>(pyramid.getTileMatrices());
        mosaics.sort((TileMatrix o1, TileMatrix o2) -> Double.compare(o2.getScale(), o1.getScale()));
        for (final TileMatrix mosaic : mosaics) {
            if (resolutions == null || resolutions.containsAny(mosaic.getScale())) {

                final Rectangle rect;
                try {
                    rect = TileMatrices.getTilesInEnvelope(mosaic, env);
                } catch (NoSuchDataException ex) {
                    continue;
                }

                final long nbTile = ((long)rect.width) * ((long)rect.height);
                final long eventstep = Math.min(1000, Math.max(1, nbTile/100l));
                Stream<Tile> stream = LongStream.range(0, nbTile).parallel()
                        .mapToObj(new LongFunction<Tile>() {
                            @Override
                            public Tile apply(long value) {
                                final long x = rect.x + (value % rect.width);
                                final long y = rect.y + (value / rect.width);

                                Tile data = null;
                                try {
                                    //do not regenerate existing tiles
                                    //if (!mosaic.isMissing((int)x, (int)y)) return;

                                    final Point coord = new Point((int)x, (int)y);
                                    try {
                                        data = generateTile(pyramid, mosaic, coord);
                                    } catch (Exception ex) {
                                        data = TileInError.create(coord, null, ex);
                                    }
                                } finally {
                                    long v = al.incrementAndGet();
                                    if (listener != null & (v % eventstep == 0))  {
                                        listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" mosaic="+mosaic.getIdentifier()+" scale="+mosaic.getScale(), (float) (( ((double)v)/((double)total) )*100.0)  ));
                                    }
                                }
                                return data;
                            }
                        })
                        .filter(this::emptyFilter);

                batchWrite(stream, mosaic, listener == null ? null : err -> listener.progressing(new ProcessEvent(DUMMY, "Error while writing tile batch", (float) (( ((double)al.get())/((double)total) )*100.0))), 200);

                long v = al.get();
                if (listener != null) {
                    listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" mosaic="+mosaic.getIdentifier()+" scale="+mosaic.getScale(), (float) (( ((double)v)/((double)total) )*100.0)  ));
                }
            }
        }
    }

    protected void batchWrite(Stream<Tile> source, TileMatrix destination, Consumer<Exception> errorHandler, int batchSize) {
        Streams.batchExecute(source, (Collection<Tile> t) -> {
            try {
                destination.writeTiles(t.stream(), null);
            } catch (DataStoreException ex) {
                LOGGER.log(Level.WARNING, "Failed to write tile batch", ex);
                if (errorHandler != null) errorHandler.accept(ex);
            }
        }, batchSize);
    }

    // TODO: we should be able to simplify this if we transmit correctly empty tiles and tiles in error.
    protected final boolean emptyFilter(Tile data) {
        if (data == null) return false;
        if (data instanceof TileInError) {
            final TileInError error = (TileInError) data;
            final boolean errorDueToNoDataAvailable = (error.getCause() instanceof NoSuchDataException || error.getCause() instanceof IllegalGridGeometryException);
            LOGGER.log(errorDueToNoDataAvailable ? Level.FINER : Level.WARNING, error.getCause(), () -> String.format("Error on tile loading:%nTile coordinate: %s", error.getPosition()));
            if (!errorDueToNoDataAvailable) return false;
        }
        try {
            return !(skipEmptyTiles && (data instanceof EmptyTile || isEmpty(data)));
        } catch (DataStoreException ex) {
            throw new BackingStoreException(ex.getMessage(), ex);
        }
    }

//    private void Tile generateTile(Rectangle rect, long value) {
//        final long x = rect.x + (value % rect.width);
//        final long y = rect.y + (value / rect.width);
//
//        try {
//            //do not regenerate existing tiles
//            //if (!mosaic.isMissing((int)x, (int)y)) return;
//
//            final Point coord = new Point((int)x, (int)y);
//            try {
//                final Tile data = generateTile(pyramid, mosaic, coord);
//                if (!skipEmptyTiles || (skipEmptyTiles && !isEmpty(data))) {
//                    mosaic.writeTiles(Stream.of(data), null);
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        } finally {
//            long v = al.incrementAndGet();
//            if (listener != null & (v % eventstep == 0))  {
//                listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" mosaic="+mosaic.getIdentifier()+" scale="+mosaic.getScale(), (float) (( ((double)v)/((double)total) )*100.0)  ));
//            }
//        }
//    }

    protected abstract boolean isEmpty(Tile tile) throws DataStoreException;

}
