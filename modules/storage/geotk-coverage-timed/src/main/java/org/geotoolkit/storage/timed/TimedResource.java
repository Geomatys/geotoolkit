/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.storage.timed;

import java.awt.Image;
import java.awt.Rectangle;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.operation.matrix.Matrix3;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.storage.coverage.AbstractCoverageResource;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;

/**
 * The single resource for {@link TimedCoverageStore}. It will analyze (and survey
 * over time) the content of a folder, indexing coverages whose file name match the
 * pattern given in {@link TimedCoverageStore} parameters.
 * We extract a single date from each file, and use it as temporal component of
 * the coverage.
 * The temporal dimension is always expressed using {@link CommonCRS.Temporal#JAVA}.
 *
 * @author Alexis Manin (Geomatys)
 */
public class TimedResource extends AbstractCoverageResource implements Closeable {

    /**
     * Name of the directory in which spatio-temporal index is located.
     */
    protected static final String INDEX_DIR = "index";

    final Index index;
    final Harvester harvester;

    /**
     * The grid geometry of the total image set. Cached for faster accesses.
     * Note : the grid geometry is reset each time a new data is registered.
     */
    GeneralGridGeometry grid;

    TimedResource(TimedCoverageStore store, Path directory, long delay) throws DataStoreException {
        super(store, Names.createLocalName(null, ":", directory.getFileName().toString()));

        final Path indexDir = directory.resolve(INDEX_DIR);
        try {
            index = new Index(indexDir, directory, store.fileNameParser);
        } catch (IOException | StoreIndexException e) {
            throw new DataStoreException("Cannot initialize spatio-temporal index", e);
        }

        try {
            harvester = new Harvester(directory, this::register, TimeUnit.MILLISECONDS, delay, indexDir);
        } catch (IOException ex) {
            try {
                index.close();
            } catch (Exception e) {
                ex.addSuppressed(e);
            }
            throw new DataStoreException("Cannot initialize directory scan", ex);
        }
    }

    @Override
    public int getImageIndex() {
        return 0;
    }

    @Override
    public boolean isWritable() throws DataStoreException {
        return false;
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        return new TimedReader(this);
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Image getLegend() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    GeneralGridGeometry getGridGeometry() throws CoverageStoreException {
        final GeneralEnvelope treeEnv;
        try {
            treeEnv = index.getEnvelope()
                    .map(GeneralEnvelope::castOrCopy)
                    .orElse(new GeneralEnvelope(new DefaultCompoundCRS(
                            Collections.singletonMap("name", "Timed"),
                            CommonCRS.defaultGeographic(),
                            CommonCRS.Temporal.JAVA.crs()
                    )));
        } catch (StoreIndexException ex) {
            throw new CoverageStoreException("Cannot get envelope", ex);
        }

        if (treeEnv.isEmpty() || treeEnv.isAllNaN() || index.isEmpty()) {
            return new GeneralGridGeometry(new GeneralGridEnvelope(new Rectangle(), treeEnv.getDimension()), treeEnv);
        }

        if (grid != null && treeEnv.equals(grid.getEnvelope())) {
            return grid;
        }

        // Get grid dimension from a random image from the registered ones.
        // TODO : make more accurate by finding the image with biggest resolution.
        final Path input;
        final double[] times;
        try {
            input = index.getObject(1).orElseThrow(() -> new IllegalStateException("Cannot retrieve index data"));
        } catch (IOException ex) {
            throw new CoverageStoreException("Cannot get grid geometry", ex);
        }

        try {
            times = index.getTimes();
        } catch (IOException ex) {
            throw new CoverageStoreException("Cannot extract times from index", ex);
        }

        GridEnvelope extent;
        try (TimedUtils.CloseableCoverageReader reader = new TimedUtils.CloseableCoverageReader()) {
            reader.setInput(input.toFile());
            extent = reader.getGridGeometry(0).getExtent2D();
        }

        // Adapt image dimension to contain time
        final int dimension = treeEnv.getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
        final int[] low = new int[dimension];
        final int[] high = new int[dimension];
        low[index.xIndex] = extent.getLow(0);
        low[index.xIndex + 1] = extent.getLow(1);
        low[index.timeIndex] = 0;

        high[index.xIndex] = extent.getHigh(0);
        high[index.xIndex + 1] = extent.getHigh(1);
        high[index.timeIndex] = times.length - 1; // inclusive upper corner

        extent = new GeneralGridEnvelope(low, high, true);

        final double scaleX = treeEnv.getSpan(index.xIndex) / extent.getSpan(0);
        final double scaleY = -(treeEnv.getSpan(index.xIndex + 1) / extent.getSpan(1));
        //final double scaleT = treeEnv.getSpan(index.timeIndex) / nbImages;

        final double translationX = treeEnv.getMinimum(0);
        final double translationY = treeEnv.getMaximum(1);
        //final double translationT = treeEnv.getMinimum(2);

        final Matrix3 matrix = new Matrix3(
                scaleX, 0, translationX,
                0, scaleY, translationY,
                0, 0, 1
        );

        final MathTransform geoTransform = PixelTranslation.translate(
                MathTransforms.linear(matrix),
                PixelInCell.CELL_CORNER,
                PixelInCell.CELL_CENTER
        );
        final MathTransform1D timeTransform = MathTransforms.interpolate(null, times);
        final MathTransform gridToCRS = MathTransforms.compound(geoTransform, timeTransform);

        grid = new GeneralGridGeometry(extent, PixelInCell.CELL_CENTER, gridToCRS, treeEnv.getCoordinateReferenceSystem());

        return grid;
    }

    private synchronized void invalidateGrid() {
        grid = null;
        fireDataUpdated();
    }

    /**
     * Try to integrate the coverage pointed by given file set.
     * @param fileSet The data to index.
     * @return True if we were able to index the image behing input file set. False
     * if something wrong happens.
     */
    boolean register(final FileSet fileSet) {
        final Iterator<Path> pathIterator = Spliterators.iterator(fileSet.spliterator());
        boolean integrated = false;
        while (!integrated && pathIterator.hasNext()) {
            integrated = index.tryIndex(pathIterator.next());
        }

        if (integrated) {
            invalidateGrid();
        }

        return integrated;
    }

    @Override
    public void close() throws IOException {
        try (final Index tmpIndex = index;
                final Harvester tmpHarvester = harvester) {
            TimedUtils.LOGGER.log(Level.FINE, "Closing timed resource.");
        }
    }
}
