/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.worldfile;

import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageReaderSpi;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.AxisDirections;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.geotoolkit.coverage.TypeMap;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * Reference to a coverage stored in a single file.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class FileCoverageResource extends AbstractGridResource implements WritableGridCoverageResource {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.coverage.filestore");

    private final FileCoverageStore store;
    private final GenericName name;
    private final Path file;
    private ImageReaderSpi spi;
    private List<SampleDimension> cachedSampleDimensions;
    private GridGeometry cachedGridGeometry;

    FileCoverageResource(FileCoverageStore store, GenericName name, Path file) {
        super(null);
        this.store = store;
        this.name = name;
        this.file = file;
        this.spi = store.spi;
    }

    public boolean isWritable() throws DataStoreException {
        try {
            final ImageWriter writer = ((FileCoverageStore)store).createWriter(file);
            writer.dispose();
            return true;
        } catch (IOException ex) {
            //no writer found
            LOGGER.log(Level.FINER, "No writer found for file : "+file.toAbsolutePath().toString());
        }
        return false;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(name);
    }

    @Override
    public synchronized GridGeometry getGridGeometry() throws DataStoreException {
        if (cachedGridGeometry == null) {
            final ImageCoverageReader reader = acquireReader();
            try {
                cachedGridGeometry = reader.getGridGeometry();
            } finally {
                try {
                    reader.dispose();
                } catch (DataStoreException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
        return cachedGridGeometry;
    }

    @Override
    public synchronized List<SampleDimension> getSampleDimensions() throws DataStoreException {
        if (cachedSampleDimensions == null) {
            final ImageCoverageReader reader = acquireReader();
            try {
                //Reader are allowed to return null when dimensions are unknown
                List<SampleDimension> sd = reader.getSampleDimensions();
                if (sd == null) {
                    final GridCoverageReadParam param = new GridCoverageReadParam();
                    GeneralEnvelope envelope = new GeneralEnvelope(reader.getGridGeometry().getEnvelope());
                    for (int i=0,n=envelope.getDimension();i<n;i++) {
                        double min = envelope.getMinimum(i);
                        double span = envelope.getSpan(i);
                        envelope.setRange(i, min, min + span/100.0);
                    }
                    param.setEnvelope(reader.getGridGeometry().getEnvelope());
                    param.setDeferred(true);
                    final GridCoverage coverage = reader.read(param);
                    final RenderedImage img = coverage.render(null);
                    final SampleModel sampleModel = img.getSampleModel();
                    final int numBands = sampleModel.getNumBands();
                    final NumberRange<?> range = TypeMap.getRange(TypeMap.getSampleDimensionType(sampleModel, 0));
                    sd = new ArrayList<>();
                    for (int i=0;i<numBands;i++) {
                        sd.add(new SampleDimension.Builder()
                                .setName(i)
                                .addQuantitative("data", range, (MathTransform1D) MathTransforms.linear(1.0, 0.0), Units.UNITY)
                                .build());
                    }
                }
                cachedSampleDimensions = Collections.unmodifiableList(sd);
            } finally {
                try {
                    reader.dispose();
                } catch (DataStoreException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
        return cachedSampleDimensions;
    }

    public ImageReaderSpi getSpi() {
        return spi;
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        final ImageCoverageReader reader = acquireReader();
        try {
            final GridCoverageReadParam param = new GridCoverageReadParam();
            if (range != null && range.length > 0) {
                param.setSourceBands(range);
                param.setDestinationBands(IntStream.range(0, range.length).toArray());
            }

            if (domain != null && domain.isDefined(org.apache.sis.coverage.grid.GridGeometry.ENVELOPE)) {
                /*
                 * Modify envelope: when we encounter a slice, use the median value instead of the slice width
                 * to avoid multiple coverage occurence of coverages at envelope border intersections.
                 */
                Envelope envelope = domain.getEnvelope();
                int startDim = 0;
                if (!domain.isDefined(org.apache.sis.coverage.grid.GridGeometry.EXTENT)) {
                    domain = new GridGeometry(PixelInCell.CELL_CENTER, getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER), envelope, GridRoundingMode.ENCLOSING);
                }
                GeneralEnvelope modified = null;
                final GridExtent extent = domain.getExtent();
                for (final SingleCRS part : CRS.getSingleComponents(envelope.getCoordinateReferenceSystem())) {
                    final int crsDim = part.getCoordinateSystem().getDimension();
                    if (crsDim == 1 && extent.getSize(startDim) == 1) {
                        if (modified == null) {
                            envelope = modified = new GeneralEnvelope(envelope);
                        }
                        double m = modified.getMedian(startDim);
                        modified.setRange(startDim, m, m);
                    } else if (crsDim == 3) {
                        //might be 3d geographic/projected crs, see if we can split a vertical axis
                        VerticalCRS vcrs = CRS.getVerticalComponent(part, true);
                        if (vcrs != null) {
                            int idx = AxisDirections.indexOfColinear(part.getCoordinateSystem(), vcrs.getCoordinateSystem());
                            if (idx >= 0) {
                                if (modified == null) {
                                    envelope = modified = new GeneralEnvelope(envelope);
                                }
                                try {
                                    final int vidx = startDim + idx;
                                    final MathTransform gridToCRS = domain.getGridToCRS(PixelInCell.CELL_CENTER);
                                    final TransformSeparator ts = new TransformSeparator(gridToCRS);
                                    ts.addTargetDimensions(vidx);
                                    final MathTransform vtrs = ts.separate();
                                    final double[] vcoord = new double[]{extent.getLow(vidx)};
                                    vtrs.transform(vcoord, 0, vcoord, 0, 1);
                                    final double m = vcoord[0];
                                    modified.setRange(startDim+idx, m, m);
                                } catch (TransformException | FactoryException ex) {
                                    //we have try, no luck
                                }
                            }
                        }
                    }
                    startDim += crsDim;
                }

                param.setEnvelope(envelope);
                final double[] resolution = domain.getResolution(true);
                param.setResolution(resolution);
            }

            return reader.read(param);
        } finally {
            reader.dispose();
        }
    }

    @Override
    public synchronized void write(GridCoverage coverage, Option... options) throws DataStoreException {
        final ImageCoverageWriter writer = acquireWriter();
        try {
            writer.write(coverage, null);
        } finally {
            cachedSampleDimensions = null;
            cachedGridGeometry = null;
            writer.dispose();
        }
    }

    private ImageCoverageReader acquireReader() throws DataStoreException {
        final ImageCoverageReader reader = new ImageCoverageReader();
        try {
            final ImageReader ioreader = ((FileCoverageStore) store).createReader(file, spi);
            if (spi == null) {
                //format was on AUTO. keep the spi for futur reuse.
                spi = ioreader.getOriginatingProvider();
            }
            reader.setInput(ioreader);
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(),ex);
        }
        return reader;
    }

    private ImageCoverageWriter acquireWriter() throws DataStoreException {
        final ImageCoverageWriter writer = new ImageCoverageWriter();
        try {
            writer.setOutput( ((FileCoverageStore)store).createWriter(file) );
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(),ex);
        }
        return writer;
    }
}
