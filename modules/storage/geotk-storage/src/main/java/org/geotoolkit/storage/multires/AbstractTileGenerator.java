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
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.CoverageStoreException;
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
    public void generate(Pyramid pyramid, Envelope env, NumberRange resolutions,
            ProcessListener listener) throws DataStoreException, InterruptedException {

        if (env != null) {
            try {
                env = Envelopes.transform(env, pyramid.getCoordinateReferenceSystem());
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        final long total = countTiles(pyramid, env, resolutions);
        final AtomicLong al = new AtomicLong();

        //generate mosaic in resolution order
        //this order allows the pyramid to be used at high scales until she is not completed.
        final List<Mosaic> mosaics = new ArrayList<>(pyramid.getMosaics());
        mosaics.sort((Mosaic o1, Mosaic o2) -> Double.compare(o2.getScale(), o1.getScale()));
        for (final Mosaic mosaic : mosaics) {
            if (resolutions == null || resolutions.contains(mosaic.getScale())) {
                final Rectangle rect = Pyramids.getTilesInEnvelope(mosaic, env);

                final long nbTile = ((long)rect.width) * ((long)rect.height);
                final long eventstep = Math.min(1000, Math.max(1, nbTile/100l));
                LongStream.range(0, nbTile).parallel().forEach(new LongConsumer() {
                    @Override
                    public void accept(long value) {
                        final long x = rect.x + (value % rect.width);
                        final long y = rect.y + (value / rect.width);

                        try {
                            //do not regenerate existing tiles
                            //if (!mosaic.isMissing((int)x, (int)y)) return;

                            final Point coord = new Point((int)x, (int)y);
                            try {
                                final Tile data = generateTile(pyramid, mosaic, coord);
                                if (!skipEmptyTiles || (skipEmptyTiles && !isEmpty(data))) {
                                    mosaic.writeTiles(Stream.of(data), null);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } finally {
                            long v = al.incrementAndGet();
                            if (listener != null & (v % eventstep == 0))  {
                                listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" mosaic="+mosaic.getIdentifier()+" scale="+mosaic.getScale(), (float) (( ((double)v)/((double)total) )*100.0)  ));
                            }
                        }
                    }
                });

                long v = al.get();
                if (listener != null) {
                    listener.progressing(new ProcessEvent(DUMMY, v+"/"+total+" mosaic="+mosaic.getIdentifier()+" scale="+mosaic.getScale(), (float) (( ((double)v)/((double)total) )*100.0)  ));
                }
            }
        }

    }

    protected long countTiles(Pyramid pyramid, Envelope env, NumberRange resolutions) throws CoverageStoreException {

        long count = 0;
        for (Mosaic mosaic : pyramid.getMosaics()) {
            final Mosaic m = mosaic;
            if (resolutions == null || resolutions.contains(mosaic.getScale())) {
                if (env == null) {
                    count += ((long)m.getGridSize().width) * ((long)m.getGridSize().height);
                } else {
                    final Rectangle rect = Pyramids.getTilesInEnvelope(m, env);
                    count += ((long)rect.width) * ((long)rect.height);
                }
            }
        }
        return count;
    }

    protected abstract boolean isEmpty(Tile tile) throws DataStoreException;

}
