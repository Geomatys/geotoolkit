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

import java.awt.Image;
import java.awt.image.BufferedImage;
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
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.DataSet;
import org.geotoolkit.storage.Resource;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.spatial.PixelOrientation;
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
public abstract class AbstractCollectionCoverageResource extends AbstractCoverageResource implements DataSet,CollectionCoverageReference {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.storage.coverage");

    protected final List<Resource> resources = new CopyOnWriteArrayList<Resource>();

    private GeneralGridGeometry gridGeom;
    private List<GridSampleDimension> sampleDimensions;

    public AbstractCollectionCoverageResource(CoverageStore store, GenericName name) {
        super(store,name);
    }

    @Override
    public Metadata getMatadata() throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public Collection<Resource> getResources() {
        return Collections.unmodifiableList(resources);
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
        return new CollectionCoverageReader();
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        throw new CoverageStoreException("Not supported.");
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return null;
    }

    /**
     * Calculate a global grid geometry for all coverages.
     * This function combines the various envelopes and fine the finest resolution.
     *
     * @return
     * @throws CoverageStoreException
     */
    private synchronized GeneralGridGeometry getGridGeometry() throws CoverageStoreException {
        if (gridGeom != null) return gridGeom;

        try {
            //find global envelope and finest resolution
            GeneralEnvelope env = null;
            double[] resolution = null;
            for (CoverageResource ref : getCoverages(null)) {
                final GridCoverageReader reader = ref.acquireReader();
                final GeneralGridGeometry gg = reader.getGridGeometry(ref.getImageIndex());
                ref.recycle(reader);

                Envelope envelope = gg.getEnvelope();
                if (env==null) {
                    //we use the first coverage crs as default
                    env = new GeneralEnvelope(envelope);
                    resolution = gg.getResolution().clone();
                } else {
                    env.add(Envelopes.transform(envelope, env.getCoordinateReferenceSystem()));
                    //convert resolution to global crs
                    double[] res = ReferencingUtilities.convertResolution(envelope, gg.getResolution(), env.getCoordinateReferenceSystem(), new double[2]);
                    if(res[0]<resolution[0]) resolution[0] = res[0];
                    if(res[1]<resolution[1]) resolution[1] = res[1];
                }
            }

            //compute final grid geometry
            //calculate the output grid geometry and image size
            final int sizeX = (int)(env.getSpan(0) / resolution[0]);
            final int sizeY = (int)(env.getSpan(1) / resolution[1]);
            gridGeom = new GridGeometry2D(new GridEnvelope2D(0, 0, sizeX, sizeY), env);

            return gridGeom;
        } catch (TransformException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
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
    private synchronized List<GridSampleDimension> getSampleDimensions() throws CoverageStoreException {
        if (sampleDimensions != null) return sampleDimensions;

        final Collection<CoverageResource> references = getCoverages(null);
        if (!references.isEmpty()) {
            final CoverageResource ref = references.iterator().next();
            GridCoverageReader reader = ref.acquireReader();
            sampleDimensions = reader.getSampleDimensions(ref.getImageIndex());
            ref.recycle(reader);
        }

        return sampleDimensions;
    }

    private class CollectionCoverageReader extends GeoReferencedGridCoverageReader {

        private CollectionCoverageReader(){
            super(AbstractCollectionCoverageResource.this);
        }

        @Override
        public GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException, CancellationException {
            return AbstractCollectionCoverageResource.this.getGridGeometry();
        }

        @Override
        public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException, CancellationException {
            return AbstractCollectionCoverageResource.this.getSampleDimensions();
        }

        @Override
        protected GridCoverage readInNativeCRS(GridCoverageReadParam param) throws CoverageStoreException, CancellationException {

            final Collection<CoverageResource> references = getCoverages(param);
            final List<GridCoverage2D> coverages = new ArrayList<>();
            for (CoverageResource ref : references) {
                final GridCoverageReader reader = ref.acquireReader();
                try {
                    coverages.add((GridCoverage2D) reader.read(ref.getImageIndex(), param));
                } catch(CoverageStoreException ex) {
                    LOGGER.log(Level.FINE, ex.getMessage(), ex);
                }
                ref.recycle(reader);
            }

            if (coverages.isEmpty()) {
                throw new DisjointCoverageDomainException("No coverage match parameters");
            }

            try {
                //concatenate envelopes
                final GeneralEnvelope env = new GeneralEnvelope(coverages.get(0).getEnvelope());
                double resolution = coverages.get(0).getGridGeometry().getResolution()[0];
                for (int i=0,n=coverages.size();i<n;i++) {
                    Envelope cenv = coverages.get(i).getEnvelope();
                    env.add(Envelopes.transform(cenv, env.getCoordinateReferenceSystem()));
                }

                //compute final grid geometry
                //calculate the output grid geometry and image size
                final int sizeX = (int)(env.getSpan(0) / resolution);
                final int sizeY = (int) (env.getSpan(1) / resolution);

                if (sizeX <= 0 || sizeY <= 0) {
                    throw new DisjointCoverageDomainException("No coverage match parameters");
                }

                final GridGeometry2D gridGeom = new GridGeometry2D(new GridEnvelope2D(0, 0, sizeX, sizeY), env);

                final BufferedImage targetImage = BufferedImages.createImage(sizeX, sizeY, coverages.get(0).getRenderedImage());

                final MathTransformFactory mtFactory = FactoryFinder.getMathTransformFactory(null);
                final MathTransform targetGridToCrs = gridGeom.getGridToCRS(PixelOrientation.CENTER);

                //resample all coverages in target image
                for (GridCoverage2D coverage : coverages) {
                    final MathTransform targetCrsToSourceCrs = CRS.findOperation(
                            gridGeom.getCoordinateReferenceSystem(),
                            coverage.getCoordinateReferenceSystem(), null).getMathTransform();
                    final MathTransform sourceGridToCrs = coverage.getGridGeometry().getGridToCRS(PixelOrientation.CENTER).inverse();

                    final MathTransform targetToSource = mtFactory.createConcatenatedTransform(
                               mtFactory.createConcatenatedTransform(targetGridToCrs, targetCrsToSourceCrs), sourceGridToCrs);

                    final RenderedImage sourceImage = coverage.getRenderedImage();
                    final double[] fillValues = new double[sourceImage.getSampleModel().getNumBands()];
                    Arrays.fill(fillValues, Double.NaN);

                    final Resample resample = new Resample(targetToSource, targetImage, sourceImage,
                            InterpolationCase.NEIGHBOR, ResampleBorderComportement.FILL_VALUE, null);
                    resample.fillImage();
                }

                final GridCoverageBuilder builder = new GridCoverageBuilder();
                builder.setName(ref.getName().tip().toString());
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
