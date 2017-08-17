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

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.metadata.AxisDirections;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.index.tree.star.FileStarRTree;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ImageCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class Index implements Closeable {

    /**
     * Name of the file for the index.
     */
    private static final String TREE_NAME = "rtree";

    /**
     * Name of the folder containing mirror of indexed files.
     */
    private static final String HISTORY_NAME = ".history";

    private final Path indexDir;
    private final Path relativizer;

    private final Path history;
    private final Object historyLock;

    /**
     * A read/write lock to synchronize tree accesses.
     */
    private final Object treeLock;

    private final Function<Path, TemporalAccessor> timeReader;

    /**
     * A spatio-temporal tree to index images by date and location. It's private
     * so we can fully manage concurrent accesses to it.
     */
    private Tree<Path> tree;
    private CoordinateReferenceSystem treeCRS;
    protected int timeIndex;
    protected int xIndex;

    public Index(final Path indexDir, final Path relativizer, final Function<Path, TemporalAccessor> timeReader) throws IOException, StoreIndexException {
        ArgumentChecks.ensureNonNull("Index directory", indexDir);
        ArgumentChecks.ensureNonNull("Time reader", timeReader);
        if (!Files.isDirectory(indexDir)) {
            Files.createDirectory(indexDir);
        }

        history = indexDir.resolve(HISTORY_NAME);
        if (!Files.isDirectory(history)) {
            Files.createDirectory(history);
        }
        historyLock = TimedUtils.acquireLock(history);

        this.indexDir = indexDir;
        this.relativizer = relativizer;
        this.timeReader = timeReader;

        final Path treePath = indexDir.resolve(TREE_NAME);
        treeLock = TimedUtils.acquireLock(treePath);
        if (Files.exists(treePath)) {
            synchronized (treeLock) {
                tree = new FileStarRTree<>(treePath, new SimpleImageMapping(indexDir, relativizer, this::compute));
                treeCRS = tree.getCrs();
            }

            final TemporalCRS timeCRS = CRS.getTemporalComponent(treeCRS);
            if (timeCRS == null) {
                throw new StoreIndexException("The resource index has no temporal component, that's forbidden.");
            }
            xIndex = CRSUtilities.firstHorizontalAxis(treeCRS);
            timeIndex = AxisDirections.indexOfColinear(treeCRS.getCoordinateSystem(), timeCRS.getCoordinateSystem());
        }
    }

    public Optional<Envelope> getEnvelope() throws StoreIndexException {
        if (tree == null) {
            return Optional.empty();
        }

        final double[] extent;
        final CoordinateReferenceSystem crs;
        synchronized (treeLock) {
            extent = tree.getExtent();
        }

        final GeneralEnvelope result = new GeneralEnvelope(treeCRS);
        if (extent != null) {
            result.setEnvelope(extent);
        }

        return Optional.of(result);
    }

    /**
     * Try to put the given image in this index. If the given data is already part
     * of the index, nothing is done.
     *
     * Note that if the image file has been modified after insertion, we consider
     * it is not valid anymore and should be re-introduced.
     *
     * @param image The coverage file to index.
     * @return True if the file has been indexed or was already part of the index.
     * False otherwise.
     */
    boolean tryIndex(final Path image) {
        try {
            if (isIndexed(image)) {
                return true;
            }

            ensureTreeExists(image);
            synchronized (treeLock) {
                tree.insert(image);
            }

            markIndexed(image);

            return true;
        } catch (StoreIndexException|CoverageStoreException|IOException ex) {
            TimedUtils.LOGGER.log(Level.WARNING, ex, () -> String.format("Cannot read input image. It will be ignored.%nPath : %s", image));
        }

        return false;
    }

    private void ensureTreeExists(final Path imageFile) throws CoverageStoreException, IOException, StoreIndexException {
        if (tree == null) {
            final CoordinateReferenceSystem imageCRS;
            try (final TimedUtils.CloseableCoverageReader reader = new TimedUtils.CloseableCoverageReader()) {
                reader.setInput(imageFile);
                imageCRS = reader.getGridGeometry(0).getCoordinateReferenceSystem();
            }

            if (imageCRS instanceof ImageCRS) {
                throw new CoverageStoreException("Given image has no spatial information.");
            }

            synchronized (treeLock) {
                if (tree == null) {
                    treeCRS = new DefaultCompoundCRS(Collections.singletonMap("name", "Timed"), imageCRS, CommonCRS.Temporal.JAVA.crs());
                    tree = new FileStarRTree<>(indexDir.resolve(TREE_NAME), 7, treeCRS, new SimpleImageMapping(indexDir, relativizer, this::compute));
                }
            }

            xIndex = CRSUtilities.firstHorizontalAxis(treeCRS);
            timeIndex = AxisDirections.indexOfColinear(treeCRS.getCoordinateSystem(), CommonCRS.Temporal.JAVA.crs().getCoordinateSystem());
        }
    }

    private Envelope compute(final Path imageFile) {
        final TemporalAccessor time = timeReader.apply(imageFile);

        try (final TimedUtils.CloseableCoverageReader cvgReader = new TimedUtils.CloseableCoverageReader()) {
            cvgReader.setInput(imageFile.toFile());
            final Envelope tmpEnvelope = cvgReader.getGridGeometry(0).getEnvelope();
            if (tmpEnvelope.getCoordinateReferenceSystem() == null
                    || (tmpEnvelope.getCoordinateReferenceSystem() instanceof ImageCRS)) {
                throw new CoverageStoreException("No spatial information found in "+imageFile);
            }
            return toTreeCRS(tmpEnvelope, time);
        } catch (CoverageStoreException|TransformException e) {
            throw new RuntimeException(e);
        }
    }

    private Envelope toTreeCRS(final Envelope input, final TemporalAccessor time) throws TransformException {
        final GeneralEnvelope result = new GeneralEnvelope(toTreeCRS(input));
        final long timestamp = Instant.from(time).toEpochMilli();
        result.setRange(timeIndex, timestamp, timestamp + 1);// Avoid empty interval.
        return result;
    }

    /**
     * Convert input envelope in CRS used for internal RTree
     * @param input
     * @return
     * @throws TransformException
     */
    private Envelope toTreeCRS(final Envelope input) throws TransformException {
        if (input.getDimension() < treeCRS.getCoordinateSystem().getDimension()) {
            final GeneralEnvelope result = new GeneralEnvelope(treeCRS);
            result.setRange(timeIndex, Double.NaN, Double.NaN);
            return ReferencingUtilities.transposeEnvelope(new GeneralEnvelope(input), result);

        } else {
            return Envelopes.transform(input, treeCRS);
        }
    }

    /**
     * Find last indexed data intersecting given envelope.
     * @param e The search envelope.
     * @return The path of the queried data, or an empty shell if no entry has
     * been found.
     */
    Optional<Path> findMostRecent(Envelope e) throws StoreIndexException, TransformException, IOException {
        synchronized (treeLock) {
            if (tree == null) {
                return Optional.empty();
            }
        }

        final CoordinateReferenceSystem envCRS = e.getCoordinateReferenceSystem();
        if (envCRS == null || !Utilities.equalsApproximatively(treeCRS, envCRS)) {
            e = toTreeCRS(e);
        }

        final int[] ids;
        final TreeElementMapper<Path> eltMapper;
        synchronized (treeLock) {
            ids = tree.searchID(e);
            if (ids.length < 1) {
                return Optional.empty();
            }
            eltMapper = tree.getTreeElementMapper();
        }
        /**
         * Tree identifier is an auto-increment, so the most recent entry has
         * the highest id.
         */
        int max = ids[0];
        for (int i = 0; i < ids.length; i++) {
            max = Math.max(max, ids[i]);
        }

        return Optional.of(eltMapper.getObjectFromTreeIdentifier(max));
    }

    Optional<Path> getObject(final int identifier) throws IOException {
        synchronized (treeLock) {
            if (tree == null)
                return Optional.empty();
            return Optional.of(tree.getTreeElementMapper().getObjectFromTreeIdentifier(identifier));
        }
    }

    int size() {
        synchronized (treeLock) {
            return tree == null? 0 : tree.getElementsNumber();
        }
    }

    boolean isEmpty() {
        synchronized (treeLock) {
            return tree == null? true : tree.getElementsNumber() < 1;
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (treeLock) {
            if (tree != null)
                tree.close();
        }
    }

    private boolean isIndexed(final Path image) throws IOException {
        final Path imageHistory = getHistory(image);

        synchronized (historyLock) {
            return Files.exists(imageHistory) && Files.getLastModifiedTime(image).compareTo(Files.getLastModifiedTime(imageHistory)) >= 0;
        }
    }

    private void markIndexed(final Path image) throws IOException {
        final Path imageHistory = getHistory(image);
        synchronized (historyLock) {
            if (!Files.exists(imageHistory)) {
                Files.createFile(imageHistory);
            }

            try (final BufferedWriter writer = Files.newBufferedWriter(imageHistory, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
                writer.append("INDEXED ON "+OffsetDateTime.now(ZoneId.of("Z")));
                writer.newLine();
            }
        }
    }

    private Path getHistory(final Path image) {
        return history.resolve(relativizer.relativize(image));
    }
}
