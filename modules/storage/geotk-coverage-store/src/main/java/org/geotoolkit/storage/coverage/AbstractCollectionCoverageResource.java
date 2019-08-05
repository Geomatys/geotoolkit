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
package org.geotoolkit.storage.coverage;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.internal.ImageUtilities;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * Combines a collection of coverage references as a single coverage.<br>
 * The resulting coverage enveloper contains all child coverages and it's resolution
 * matches the most accurate coverage.<br>
 * <br>
 * All coverages are expected to have identical sample models and sample dimensions.<br>
 * But coverage extant and grid to crs transformes may by different.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractCollectionCoverageResource extends AbstractCoverageResource implements Aggregate,CollectionCoverageResource {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.storage.coverage");

    protected final List<Resource> resources = new CopyOnWriteArrayList<Resource>();

    private GridGeometry gridGeom;
    private List<SampleDimension> sampleDimensions;

    public AbstractCollectionCoverageResource(DataStore store, GenericName name) {
        super(store,name);
    }

    @Override
    public Collection<Resource> components() {
        return Collections.unmodifiableList(resources);
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        return new CollectionCoverageReader();
    }

    /**
     * Calculate a global grid geometry for all coverages.
     * This function combines the various envelopes and fine the finest resolution.
     *
     * @return
     * @throws CoverageStoreException
     */
    @Override
    public GridGeometry getGridGeometry() throws CoverageStoreException {
        if (gridGeom != null) return gridGeom;

        try {
            //find global envelope and finest resolution
            GeneralEnvelope env = null;
            double[] resolution = null;
            for (GridCoverageResource ref : getCoverages(null)) {
                final GridGeometry gg = ref.getGridGeometry();

                if (gg instanceof GridGeometry2D) {
                    final GridGeometry2D gg2d = (GridGeometry2D) gg;
                    Envelope envelope = gg2d.getEnvelope2D();
                    if (env == null) {
                        //we use the first coverage crs as default
                        env = new GeneralEnvelope(envelope);
                        resolution = gg.getResolution(true).clone();
                    } else {
                        env.add(Envelopes.transform(envelope, env.getCoordinateReferenceSystem()));
                        //convert resolution to global crs
                        double[] res = ReferencingUtilities.convertResolution(envelope, gg.getResolution(true), env.getCoordinateReferenceSystem(), new double[2]);
                        if(res[0]<resolution[0]) resolution[0] = res[0];
                        if(res[1]<resolution[1]) resolution[1] = res[1];
                    }
                } else {
                    Envelope envelope = gg.getEnvelope();
                    if (env==null) {
                        //we use the first coverage crs as default
                        env = new GeneralEnvelope(envelope);
                        resolution = gg.getResolution(true).clone();
                    } else {
                        env.add(Envelopes.transform(envelope, env.getCoordinateReferenceSystem()));
                        //convert resolution to global crs
                        double[] res = ReferencingUtilities.convertResolution(envelope, gg.getResolution(true), env.getCoordinateReferenceSystem(), new double[2]);
                        if(res[0]<resolution[0]) resolution[0] = res[0];
                        if(res[1]<resolution[1]) resolution[1] = res[1];
                    }
                }
            }

            //compute final grid geometry
            //calculate the output grid geometry and image size
            final int sizeX = (int)(env.getSpan(0) / resolution[0]);
            final int sizeY = (int)(env.getSpan(1) / resolution[1]);
            gridGeom = new GridGeometry2D(new GridExtent(sizeX, sizeY), env);

            return gridGeom;
        } catch (TransformException | DataStoreException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }

    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        final GridCoverageReader reader = acquireReader();
        try {
            return reader.getSampleDimensions();
        } finally {
            recycle(reader);
        }
    }

    /**
     * Cache and return the sample dimensions.<br>
     * All coverages are expected to have the same model.<br>
     * The sample dimensions are extracted from the first coverage.<br>
     *
     * @return
     * @throws CoverageStoreException
     */
    private synchronized List<SampleDimension> getSampleDimensionsInternal() throws DataStoreException {
        if (sampleDimensions != null) return sampleDimensions;

        final Collection<GridCoverageResource> references = getCoverages(null);
        if (!references.isEmpty()) {
            final GridCoverageResource ref = references.iterator().next();
            sampleDimensions = ref.getSampleDimensions();
        }

        return sampleDimensions;
    }

    private class CollectionCoverageReader extends GeoReferencedGridCoverageReader {

        private CollectionCoverageReader(){
            super(AbstractCollectionCoverageResource.this);
        }

        @Override
        public GridGeometry getGridGeometry() throws DataStoreException, CancellationException {
            return AbstractCollectionCoverageResource.this.getGridGeometry();
        }

        @Override
        public List<SampleDimension> getSampleDimensions() throws DataStoreException, CancellationException {
            return AbstractCollectionCoverageResource.this.getSampleDimensionsInternal();
        }

        @Override
        protected GridCoverage readGridSlice(int[] areaLower, int[] areaUpper, int[] subsampling, GridCoverageReadParam param)
                throws DataStoreException, TransformException, CancellationException {

            final Collection<GridCoverageResource> references = getCoverages(param);
            final List<GridCoverage2D> coverages = new ArrayList<>();
            for (GridCoverageResource ref : references) {
                final GridCoverageReader reader = ref.acquireReader();
                try {
                    GridCoverageReadParam subParam = new GridCoverageReadParam();
                    subParam.setDeferred(true);
                    subParam.setEnvelope(param.getEnvelope());
                    coverages.add((GridCoverage2D) reader.read(subParam));
                } catch(DisjointCoverageDomainException ex) {
                    //continue, no log, normal it can happen
                } catch(CoverageStoreException ex) {
                    LOGGER.log(Level.FINE, ex.getMessage(), ex);
                }
                ref.recycle(reader);
            }

            if (coverages.isEmpty()) {
                throw new DisjointCoverageDomainException("No coverage match parameters");
            }

            final GridGeometry globalGridGeom = getGridGeometry();
            final GridGeometry gridGeom = GeoReferencedGridCoverageReader.getGridGeometry(globalGridGeom, areaLower, areaUpper, subsampling);
            final GridExtent extent = gridGeom.getExtent();
            final long sizeX = extent.getSize(0);
            final long sizeY = extent.getSize(1);

            try {
                final BufferedImage targetImage = BufferedImages.createImage((int) sizeX, (int) sizeY, coverages.get(0).getRenderedImage());

                //intermediate image for float and double images
                BufferedImage tempImage = null;
                final int dataType = targetImage.getRaster().getDataBuffer().getDataType();
                final boolean isNanable = (DataBuffer.TYPE_FLOAT == dataType || DataBuffer.TYPE_DOUBLE == dataType)
                                       && (targetImage.getRaster().getNumBands() == 1);
                if (isNanable) {
                    ImageUtilities.fill(targetImage, Double.NaN);
                    tempImage = BufferedImages.createImage((int) sizeX, (int) sizeY, coverages.get(0).getRenderedImage());
                }

                final MathTransformFactory mtFactory = DefaultFactories.forBuildin(MathTransformFactory.class);
                final MathTransform targetGridToCrs = gridGeom.getGridToCRS(PixelInCell.CELL_CENTER);

                //resample all coverages in target image
                for (GridCoverage2D coverage : coverages) {
                    final MathTransform targetCrsToSourceCrs = CRS.findOperation(
                            gridGeom.getCoordinateReferenceSystem(),
                            coverage.getCoordinateReferenceSystem2D(), null).getMathTransform();
                    final MathTransform sourceGridToCrs = coverage.getGridGeometry().getGridToCRS2D(PixelOrientation.CENTER).inverse();

                    final MathTransform targetToSource = mtFactory.createConcatenatedTransform(
                               mtFactory.createConcatenatedTransform(targetGridToCrs, targetCrsToSourceCrs), sourceGridToCrs);

                    final RenderedImage sourceImage = coverage.getRenderedImage();
                    final double[] fillValues = new double[sourceImage.getSampleModel().getNumBands()];
                    Arrays.fill(fillValues, Double.NaN);

                    //NOTE : do not use BILINEAR, BICUBIC,BICUBIC2, errors in resampling

                    //fill image with NaN
                    if (isNanable) {
                        ImageUtilities.fill(tempImage, Double.NaN);
                        final Resample resample = new Resample(targetToSource, tempImage, sourceImage,
                            InterpolationCase.NEIGHBOR, ResampleBorderComportement.FILL_VALUE, null);
                        resample.fillImage();

                        //merge not NaN values in target
                        final PixelIterator reader = PixelIterator.create(tempImage);
                        final WritablePixelIterator writer = WritablePixelIterator.create(targetImage);
                        double[] pixel = new double[1];
                        while (reader.next()) {
                            reader.getPixel(pixel);
                            if (!Double.isNaN(pixel[0])) {
                                Point pt = reader.getPosition();
                                writer.moveTo(pt.x, pt.y);
                                writer.setPixel(pixel);
                            }
                        }
                    } else {
                        final Resample resample = new Resample(targetToSource, targetImage, sourceImage,
                            InterpolationCase.NEIGHBOR, ResampleBorderComportement.FILL_VALUE, null);
                        resample.fillImage();
                    }
                }

                final GridCoverageBuilder builder = new GridCoverageBuilder();
                ref.getIdentifier().ifPresent((n) -> builder.setName(n.tip().toString()));
                builder.setRenderedImage(targetImage);
                builder.setGridGeometry(gridGeom);
                builder.setSampleDimensions(coverages.get(0).getSampleDimensions());
                return builder.build();
            } catch (TransformException | FactoryException ex) {
                throw new CoverageStoreException(ex.getMessage(), ex);
            }

        }

    }

}
