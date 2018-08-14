/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.storage.coverage;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImagingOpException;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageReader;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.image.io.XImageIO;
import org.opengis.util.GenericName;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessListener;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.WritableAggregate;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.internal.ImageUtilities;
import org.geotoolkit.image.io.large.AbstractLargeRenderedImage;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.grid.GridGeometryIterator;
import org.geotoolkit.process.Monitor;
import org.opengis.metadata.spatial.PixelOrientation;

/**
 * <p>Resampling, re-project, tile cut and insert in given datastore, image from
 * {@link GridCoverage} or {@link GridCoverageReader}.<br/><br/>
 *
 * Use example : <br/><br/>
 * We own a {@link GridCoverage} or {@link GridCoverageReader} and a {@link CoverageStore}.<br/>
 * {@code final GridCoverage myGridCoverage;}<br/>
 * {@code final CoverageStore myCoverageStore;}<br/><br/>
 * We want project our {@link GridCoverage} in 2 others {@link CoordinateReferenceSystem}
 * and insert results in our database with differents scale levels.<br/><br/>
 *
 * In first time we build {@link PGCoverageBuilder} object and define interpolation properties use during resampling.<br/>
 *
 * {@code final PGCoverageBuilder pgcb = new PGCoverageBuilder(new Dimension(200, 200), InterpolationCase.BICUBIC, 2);}.<br/><br/>
 * See {@link PGCoverageBuilder#PGCoverageBuilder(java.awt.Dimension, org.geotoolkit.image.interpolation.InterpolationCase, int) }<br/><br/>
 *
 * We choose {@link CoordinateReferenceSystem}.<br/>
 * {@code final CoordinateReferenceSystem crs4326 = CRS.forCode("EPSG:4326");}<br/>
 * {@code final CoordinateReferenceSystem crs2163 = CRS.forCode("EPSG:2163");}<br/>
 * See <a href="http://www.geotoolkit.org/modules/referencing/supported-codes.html">List of authority codes</a>
 * for more {@link CoordinateReferenceSystem}.<br/><br/>
 *
 * We define work area with appropriate {@link Envelope}.<br/>
 * {@code final GeneralEnvelope envCRS4326 = new GeneralEnvelope(crs4326);}<br/>
 * {@code envCRS4326.setEnvelope(xmin, ymin,} &#133; {@code , xmax, ymax);}<br/>
 * {@code final GeneralEnvelope envCRS2163 = new GeneralEnvelope(crs2163);}<br/>
 * {@code envCRS2163.setEnvelope(xmin, ymin,} &#133; {@code , xmax, ymax);}<br/>
 * See {@link CoordinateReferenceSystem#getDomainOfValidity()} for appropriate ordinates values.<br/><br/>
 *
 * We define appropriate scale level from envelope size.<br/>
 * {@code final double[] scaleLvlCRS4329 = new double[]{val0, val1, ... , valn};}<br/>
 * {@code final double[] scaleLvlCRS2163 = new double[]{val0, val1, ... , valn};}<br/>
 * Note : output stored image size : <br/>
 * output image width  = {@link Envelope#getSpan(0) } / {val0, &#133; , valn}.<br/>
 * output image height = {@link Envelope#getSpan(1) } / {val0, &#133; , valn}.<br/><br/>
 *
 * We associate work area ({@link Envelope}) with theirs scale levels.<br/>
 * {@code final HashMap&lt;Envelope, double[]&gt; resolution_Per_Envelope  = new HashMap&lt;Envelope, double[]&gt;();}<br/>
 * {@code resolution_Per_Envelope.put(envCRS4326, scaleLvlCRS4329);}<br/>
 * {@code resolution_Per_Envelope.put(envCRS2163, scaleLvlCRS2163);}<br/><br/>
 *
 * We define pyramid name.<br/>
 * {@code final Name name = new DefaultName("myName");}<br/><br/>
 * and table in case when pixel transformation is out of source image boundary.<br/>
 * {@code final double[] fillValue = new double[]{1, 2, 3}};//for example.<br/><br/>
 *
 * In last time, call create method.<br/>
 * {@code pgcb.create(myGridCoverage, myCoverageStore, name, resolution_Per_Envelope, fillValue);}</p>
 *
 * @author Rémi Marechal (Geomatys).
 * @author Quentin Boileau (Geomatys).
 * @author Johann Sorel (Geomatys).
 */
public class PyramidCoverageBuilder {

    /**
     * The default tile size in pixels.
     */
    private static final int DEFAULT_TILE_SIZE = 256;

    /**
     * Tile width.
     */
    private int tileWidth = DEFAULT_TILE_SIZE;

    /**
     * Tile height.
     */
    private int tileHeight = DEFAULT_TILE_SIZE;

    /**
     * Interpolation properties.
     */
    private InterpolationCase interpolationCase = InterpolationCase.NEIGHBOR;
    private int lanczosWindow = 1;

    /**
     * Flag to re-use mosaic tiles if already exist in output pyramid.
     * Set at false by default.
     */
    private boolean reuseTiles = false;

    /**
     * Global number of tiles which will be generate.
     * @see PyramidCoverageBuilder#initListener(java.util.Map, org.geotoolkit.process.ProcessListener)
     */
    private int globalTileNumber;

    /**
     * The current nth tile.
     */
    private int niemeTile;

    private ProcessListener processListener;
    private Monitor monitor;
    private double[] fillValue;
    private Map<Envelope, double[]> resolutionPerEnvelope;

    //source and target description
    private GridCoverageResource sourceResource;
    private GridCoverage sourceCoverage;
    private GridCoverageReader sourceReader;
    private CoverageResource targetResource;
    private CoverageStore targetStore;
    private GenericName targetName;

    /**
     * Used for events.
     */
    private final org.geotoolkit.process.Process fakeProcess = new org.geotoolkit.process.Process() {
        @Override
        public ProcessDescriptor getDescriptor() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public ParameterValueGroup getInput() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public ParameterValueGroup call() throws ProcessException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public ProcessStep getMetadata() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public void addListener(ProcessListener listener) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public void removeListener(ProcessListener listener) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        @Override
        public ProcessListener[] getListeners() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    };

    public PyramidCoverageBuilder() {
    }

    /**
     * @param tileSize size of tile from mosaic.
     */
    public void setTileSize(Dimension tileSize) {
        ArgumentChecks.ensureStrictlyPositive("tileWidth", tileSize.width);
        ArgumentChecks.ensureStrictlyPositive("tileHeight", tileSize.height);
        this.tileWidth = tileSize.width;
        this.tileHeight = tileSize.height;
    }

    /**
     * @return current tile sizes
     */
    public Dimension getTileSize() {
        return new Dimension(tileWidth, tileHeight);
    }

    /**
     * Set interpolation method.
     * Default is set to NEAREST_NEIGHBOR
     *
     * @param interpolation pixel interpolation use during resampling operation.
     */
    public void setInterpolation(InterpolationCase interpolation) {
        ArgumentChecks.ensureNonNull("interpolation", interpolation);
        this.interpolationCase = interpolation;
    }

    /**
     *
     * @return interpolation method.
     */
    public InterpolationCase getInterpolation() {
        return interpolationCase;
    }

    /**
     * @param lanczosWindow only used with Lanczos interpolation.
     */
    public void setLanczosWindow(int lanczosWindow) {
        ArgumentChecks.ensureStrictlyPositive("lanczosWindow", lanczosWindow);
        this.lanczosWindow = lanczosWindow;
    }

    /**
     * @return Lanczos window size, used only when interpolation is Lanczos
     */
    public int getLanczosWindow() {
        return lanczosWindow;
    }

    /**
     * @param reuseTiles flag to re-use mosaic tiles if already exist in output pyramid.
     */
    public void setReuseTiles(boolean reuseTiles) {
        this.reuseTiles = reuseTiles;
    }

    /**
     * @return true if tiles already existing are preserved
     */
    public boolean isReuseTiles() {
        return reuseTiles;
    }

    /**
     * Set process listener.
     * The listener will be notified of the pyramid creation progress and errors.
     *
     * @param processListener {@link ProcessListener} to send state informations.
     */
    public void setListener(ProcessListener processListener) {
        this.processListener = processListener;
    }

    /**
     * @return current attached listener
     */
    public ProcessListener getListener() {
        return processListener;
    }

    /**
     * Set control monitor.
     * <p>
     * The monitor allows the user to stop the process.
     * Stopping a pyramid creation process may take some time until the process
     * reachs a safe state to abort.
     * </p>
     *
     * @param monitor A progress monitor used for detecting a cancel request during the process. Can be {@code null}.
     */
    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    /**
     * @return current attached monitor
     */
    public Monitor getMonitor() {
        return monitor;
    }

    /**
     * If fill value is not set, default fille values are computed from the
     * sample dimensions descriptions.
     *
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     */
    public void setFillValues(double[] fillValue) {
        this.fillValue = fillValue;
    }

    /**
     * @return user defined fill values, can be null.
     */
    public double[] getFillValues() {
        return fillValue;
    }

    /**
     * @param resolution_Per_Envelope reprojection and resampling attibuts.
     */
    public void setResolutionPerEnvelope(Map<Envelope, double[]> resolution_Per_Envelope) {
        this.resolutionPerEnvelope = resolution_Per_Envelope;
    }

    /**
     * @return map of envelope and scales to pyramid
     */
    public Map<Envelope, double[]> getResolutionPerEnvelope() {
        return resolutionPerEnvelope;
    }

    /**
     * This method unset any previous source defined and replace it
     * by given source.
     *
     * @param sourceCoverage {@link GridCoverage} which will be stored.
     */
    public void setSourceCoverage(GridCoverage sourceCoverage) {
        this.sourceCoverage = sourceCoverage;
        this.sourceReader = null;
        this.sourceResource = null;
    }

    /**
     * This method unset any previous source defined and replace it
     * by given source.
     *
     * @param sourceReader {@link GridCoverageReader} which will be stored.
     */
    public void setSourceReader(GridCoverageReader sourceReader) {
        this.sourceCoverage = null;
        this.sourceReader = sourceReader;
        this.sourceResource = null;
    }

    /**
     * This method unset any previous source defined and replace it
     * by given source.
     *
     * @param sourceResource {@link GridCoverageResource} which will be stored.
     */
    public void setSourceResource(GridCoverageResource sourceResource) {
        this.sourceCoverage = null;
        this.sourceReader = null;
        this.sourceResource = sourceResource;
    }

    /**
     * @see #setSourceCoverage(org.opengis.coverage.grid.GridCoverage)
     * @return {@link GridCoverage} , can be null
     */
    public GridCoverage getSourceCoverage() {
        return sourceCoverage;
    }

    /**
     * @see #setSourceReader(org.geotoolkit.coverage.io.GridCoverageReader)
     * @return {@link GridCoverageReader} , can be null
     */
    public GridCoverageReader getSourceReader() {
        return sourceReader;
    }

    /**
     * @see #setSourceResource(org.geotoolkit.storage.coverage.GridCoverageResource) e)
     * @return {@link GridCoverageResource} , can be null
     */
    public GridCoverageResource getSourceResource() {
        return sourceResource;
    }

    /**
     * @param targetResource resource where the pyramid will be created
     */
    public void setTargetResource(CoverageResource targetResource) {
        this.targetResource = targetResource;
        this.targetStore = null;
        this.targetName = null;
    }

    /**
     * @param targetStore {@link CoverageStore} where the pyramid will be created.
     * @param targetName name given to the set of operations results, performed on the coverage in the datastore.
     */
    public void setTargetStore(CoverageStore targetStore, GenericName targetName) {
        this.targetResource = null;
        this.targetStore = targetStore;
        this.targetName = targetName;
    }

    /**
     * @see #setTargetResource(org.geotoolkit.storage.coverage.CoverageResource)
     * @return target resource where to write pyramid
     */
    public CoverageResource getTargetResource() {
        return targetResource;
    }

    /**
     * @see #setTargetStore(org.geotoolkit.storage.coverage.CoverageStore, org.opengis.util.GenericName)
     * @return target store where to write pyramid
     */
    public CoverageStore getTargetStore() {
        return targetStore;
    }

    /**
     * @see #setTargetStore(org.geotoolkit.storage.coverage.CoverageStore, org.opengis.util.GenericName)
     * @return target store resource name where to write pyramid
     */
    public GenericName getTargetName() {
        return targetName;
    }

    /**
     * Effectuate resampling, re-projection, tile cutting and insertion in datastore on {@link GridCoverage}.
     *
     * <p>
     * Note : <br/>
     * {@link GridGeometry} from {@link GridCoverage} must be instance of {@link GridGeometry2D}
     * else a {@link IllegalArgumentException} will be thrown.<br/>
     * fillValue parameter must have same length than pixel size from image within coverage.<br/>
     * If fill value is {@code null} a table of zero value with appropriate length is use.
     * </p>
     *
     * @throws DataStoreException if tile writing throw exception.
     * @throws TransformException if problems during resampling operation.
     * @throws FactoryException if impossible to find {@code MathTransform} between two {@link CoordinateReferenceSystem}.
     */
    public void execute() throws DataStoreException, TransformException, FactoryException, IOException {

        // get or create target pyramid

        final PyramidalCoverageResource pm;
        if (targetResource != null) {
            if (!(targetResource instanceof PyramidalCoverageResource)) {
                final IllegalArgumentException ex = new IllegalArgumentException("CoverageStore parameter must be instance of Pyramid resource."+targetResource.toString());
                if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
                throw ex;
            }
            pm = (PyramidalCoverageResource) targetResource;
        } else {
            ArgumentChecks.ensureNonNull("output CoverageStore", targetStore);
            ArgumentChecks.ensureNonNull("coverageName", targetName);
            final GridCoverageResource cv = getOrCreateCRef(targetStore,targetName);
            if (!(cv instanceof PyramidalCoverageResource)) {
                final IllegalArgumentException ex = new IllegalArgumentException("Target resource must be an instance of Pyramid."+targetStore.toString());
                if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
                throw ex;
            }
            pm = (PyramidalCoverageResource) cv;
        }


        //get or create sub resolutions TODO
        Map<Envelope, double[]> resPerEnv = resolutionPerEnvelope;
        if (resolutionPerEnvelope == null) {
            //compute default resolutions

            //get grid geometry
            final GeneralGridGeometry gg;
            if (sourceCoverage != null) {
                gg = (GeneralGridGeometry) sourceCoverage.getGridGeometry();
            } else if (sourceReader != null) {
                gg = sourceReader.getGridGeometry(0);
            } else if (sourceResource != null) {
                final GridCoverageReader reader = sourceResource.acquireReader();
                try {
                    gg = reader.getGridGeometry(sourceResource.getImageIndex());
                } finally {
                    sourceResource.recycle(reader);
                }
            } else {
                final IllegalArgumentException ex = new IllegalArgumentException("Source parameter is not defined.");
                if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
                throw ex;
            }
            final Envelope dataEnv = gg.getEnvelope();

            resPerEnv = new HashMap<>();
            final double geospanX = dataEnv.getSpan(0);
            final double baseScale = geospanX / gg.getExtent().getSpan(0);
            final int tileSize = tileWidth;
            double scale = geospanX / tileSize;
            final GeneralDirectPosition ul = new GeneralDirectPosition(dataEnv.getCoordinateReferenceSystem());
            ul.setOrdinate(0, dataEnv.getMinimum(0));
            ul.setOrdinate(1, dataEnv.getMaximum(1));
            final List<Double> scalesList = new ArrayList<>();
            while (true) {
                if (scale <= baseScale) {
                    //fit to exact match to preserve base quality.
                    scale = baseScale;
                }
                scalesList.add(scale);
                if (scale <= baseScale) {
                    break;
                }
                scale = scale / 2;
            }
            final double[] scales = new double[scalesList.size()];
            for (int i = 0; i < scales.length; i++) scales[i] = scalesList.get(i);
            resPerEnv.put(dataEnv, scales);
        }

        //check canceled
        if (monitor != null && monitor.isCanceled()) {
            final CancellationException ex = new CancellationException();
            if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
            throw ex;
        }

        if (sourceCoverage != null) {

            final GridGeometry currentGridGeometry = sourceCoverage.getGridGeometry();

            if (!(currentGridGeometry instanceof GridGeometry2D)) {
                final IllegalArgumentException ex = new IllegalArgumentException("GridGeometry not instance of GridGeometry2D");
                if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
                throw ex;
            }

            if (processListener != null) {
                initListener(resPerEnv, currentGridGeometry, processListener);
            }

            //------------------------ add sampleDimension -------------------------
            final int nbSampleDimension = sourceCoverage.getNumSampleDimensions();
            final List<GridSampleDimension> sampleList = new ArrayList<>();
            for (int nbs = 0; nbs < nbSampleDimension; nbs++) {
                final SampleDimension s = sourceCoverage.getSampleDimension(nbs);
                if (s instanceof GridSampleDimension) {
                    sampleList.add((GridSampleDimension) s);
                } else {
                    //-- should never append for the moment the only sampleDimension type is GridSampleDimension.
                    throw new IllegalStateException("The only supported SampleDimension type is GridSampleDimension. Found : "+s.getClass());
                }
            }
            if (!isDimensionsCompatible(pm, sampleList)) {
                throw new DataStoreException("Incompatible GridSampleDimensions. " +
                        "Input coverage should have compatible GridSampleDimension with output Pyramid.");
            }

            pm.setSampleDimensions(sampleList);
            //----------------------------------------------------------------------

            //Image
            fillValue = getFillValue(sourceCoverage, fillValue);

            for (Envelope envDest : resPerEnv.keySet()) {
                if (monitor != null && monitor.isCanceled()) {
                    final CancellationException ex = new CancellationException();
                    if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
                    throw ex;
                }
                final CoordinateReferenceSystem crs = envDest.getCoordinateReferenceSystem();
                final int minOrdi0                  = CRSUtilities.firstHorizontalAxis(crs);
                final int minOrdi1                  = minOrdi0 + 1;
                final DirectPosition upperLeft      = new GeneralDirectPosition(crs);
                upperLeft.setOrdinate(minOrdi0, envDest.getMinimum(minOrdi0));
                upperLeft.setOrdinate(minOrdi1, envDest.getMaximum(minOrdi1));
                //one pyramid for each CoordinateReferenceSystem.
                final Pyramid pyram                 = getOrCreatePyramid(pm, crs);
                resample(pm, pyram.getId(), sourceCoverage, resPerEnv.get(envDest), upperLeft, envDest, minOrdi0, minOrdi1);
            }

        } else if (sourceResource != null || sourceReader != null) {

            final GridCoverageReader reader;
            final int imageIndex;

            if (sourceResource != null) {
                reader = sourceResource.acquireReader();
                imageIndex = sourceResource.getImageIndex();
            } else {
                reader = sourceReader;
                imageIndex = 0;
            }

            //----------------------- sample dimensions-----------------------------
            List<GridSampleDimension> coverageSampleDims = reader.getSampleDimensions(imageIndex);
            if (!isDimensionsCompatible(pm, coverageSampleDims)) {
                throw new DataStoreException("Incompatible GridSampleDimensions. " +
                        "Input coverage should have compatible GridSampleDimension with output Pyramid.");
            }
            pm.setSampleDimensions(coverageSampleDims);
            //----------------------------------------------------------------------

            final GridGeometry currentGridGeometry = reader.getGridGeometry(imageIndex);

            //-- init listener ---
            if (processListener != null) initListener(resPerEnv, currentGridGeometry, processListener);

            for (Envelope outEnv : resPerEnv.keySet()) {
                final CoordinateReferenceSystem crs = outEnv.getCoordinateReferenceSystem();
                final int minOrdi0 = CRSUtilities.firstHorizontalAxis(crs);
                final int minOrdi1 = minOrdi0 + 1;
                final int outDim   = crs.getCoordinateSystem().getDimension();
                final DirectPosition upperLeft = new GeneralDirectPosition(crs);
                upperLeft.setOrdinate(minOrdi0, outEnv.getMinimum(minOrdi0));
                upperLeft.setOrdinate(minOrdi1, outEnv.getMaximum(minOrdi1));

                //one pyramid for each CoordinateReferenceSystem.
                final Pyramid pyram = getOrCreatePyramid(pm, crs);
                final GridGeometryIterator gitenv = new GridGeometryIterator(new GeneralGridGeometry(currentGridGeometry));
                while (gitenv.hasNext()) {
                    final GeneralEnvelope envDest = new GeneralEnvelope(Envelopes.transform(gitenv.next().getEnvelope(), crs));
                    final GeneralEnvelope clipped = new GeneralEnvelope(envDest);

                    for (int d = 0; d < outDim; d++) {
                        if (d != minOrdi0 && d != minOrdi1) {
                            //set upperLeft ordinate
                            upperLeft.setOrdinate(d, envDest.getMedian(d));
                        } else {
                            //-- set horizontal crs part coordinates from out envelope into destination envelope
                            envDest.setRange(d, outEnv.getMinimum(d), outEnv.getMaximum(d));
                            // clip envelope 2D part
                            clipped.setRange(d, Math.max(clipped.getMinimum(d),outEnv.getMinimum(d)), Math.min(clipped.getMaximum(d),outEnv.getMaximum(d)));
                        }
                    }

                    final GridCoverageReadParam rp = new GridCoverageReadParam();
                    rp.setEnvelope(clipped);
                    rp.setDeferred(true);

                    final GridCoverage gridCoverage = reader.read(imageIndex, rp);
                    final GridGeometry gg = gridCoverage.getGridGeometry();
                    if (!(gg instanceof GridGeometry2D)) {
                        final IllegalArgumentException ex = new IllegalArgumentException("GridGeometry should be instance of GridGeometry2D");
                        if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
                        throw ex;
                    }

                    final GridCoverage gridCoverage2D = gridCoverage;
                    resample(pm, pyram.getId(), gridCoverage2D, resPerEnv.get(outEnv), upperLeft, envDest, minOrdi0, minOrdi1);
                }
            }

            if (sourceResource != null) {
                sourceResource.recycle(reader);
            }

        } else {
            final IllegalArgumentException ex = new IllegalArgumentException("Source parameter is not defined.");
            if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
            throw ex;
        }

        //finished event
        if (processListener != null)  {
            processListener.completed(new ProcessEvent(fakeProcess, "Pyramid coverage builder successfully submitted.", 100));
        }
    }

    /**
     * Create and insert pyramid from {@code GridCoverage2D} source,
     * in specified {@code PyramidalModel} at specified pyramidID.
     *
     * @param pm {@code PyramidalModel} in which insert pyramid tiles.
     * @param pyramidID ID in which pyramid is inserted.
     * @param gridCoverage2D source data of pyramid which will be inserted.
     * @param scaleLevel scale values table for each pyramid level. Table length represent pyramid level number.
     * @param upperLeft geographic upper left corner of pyramid will be inserted.
     * @param envDest envelope which represent multi-dimensional slice of origin coverage envelope.
     * @param widthAxis index of X direction from multi-dimensional coverage envelope.
     * @param heightAxis index of Y direction from multi-dimensional coverage envelope.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.(should be {@code null}).
     * Can be {@code null}. If {@code null} a default table value filled by zero value,
     * with length equal to source coverage image band number is created.
     */
//    private void resample (final PyramidalCoverageResource pm, String pyramidID, GridCoverageReader coverageReader,
//            int imageIndex, GridCoverageReadParam readParam, double[] scaleLevel, DirectPosition upperLeft,
//            Envelope envDest, int widthAxis, int heightAxis)
//            throws NoninvertibleTransformException, FactoryException, TransformException, DataStoreException, IOException {
//
//        final GeneralGridGeometry ggg = coverageReader.getGridGeometry(imageIndex);
//        if (!(ggg instanceof GridGeometry2D)) {
//            final IllegalArgumentException ex = new IllegalArgumentException("GridGeometry should be instance of GridGeometry2D");
//            if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
//            throw ex;
//        }
//
//        final GridGeometry2D gg2d   = (GridGeometry2D) ggg;
//        final Envelope covEnv       = gg2d.getEnvelope2D();
//
//        // work on pixels coordinates.
//        final double envWidth       = envDest.getSpan(widthAxis);
//        final double envHeight      = envDest.getSpan(heightAxis);
//        final double min0           = envDest.getMinimum(widthAxis);
//        final double max1           = envDest.getMaximum(heightAxis);
//
//        //MathTransform2D
//        CoordinateReferenceSystem envDestCRS2D = CRSUtilities.getCRS2D(envDest.getCoordinateReferenceSystem());
//        GeneralEnvelope envDest2D = GeneralEnvelope.castOrCopy(Envelopes.transform(envDest, envDestCRS2D));
//        final MathTransform destCrs_to_coverageCRS = CRS.findOperation(envDestCRS2D, gg2d.getCoordinateReferenceSystem2D(), null).getMathTransform();
//
//        final Dimension tileSize              = new Dimension(tileWidth, tileHeight);
//
//        final GeneralEnvelope covEnvInDestCRS = Envelopes.transform(destCrs_to_coverageCRS.inverse(), covEnv);
//        final GeneralEnvelope clipEnv   = ReferencingUtilities.intersectEnvelopes(covEnvInDestCRS, envDest2D);
//
//        //------------------- param resolution configuration -------------------
//        final int paramDim;
//        final CoordinateReferenceSystem paramCrs = readParam.getCoordinateReferenceSystem();
//        if (paramCrs != null) {
//            paramDim = paramCrs.getCoordinateSystem().getDimension();
//        } else if (readParam.getEnvelope() != null) {
//            paramDim = readParam.getEnvelope().getDimension();
//        } else {
//            //-- assume param in the same crs than coverage
//            readParam.setCoordinateReferenceSystem(ggg.getCoordinateReferenceSystem());
//            paramDim = ggg.getDimension();
//        }
//        final double[] res = new double[paramDim];
//        Arrays.fill(res, 1.0);
//        //----------------------------------------------------------------------
//
//        //-- one mosaic for each level scale
//        for (double pixelScal : scaleLevel) {
//            res[widthAxis] = res[heightAxis] = pixelScal;
//            //-- output image size
//            readParam.setResolution(res);
//            assert Utilities.equalsIgnoreMetadata(readParam.getCoordinateReferenceSystem(), ggg.getCoordinateReferenceSystem())
//                    : "PyramidCoverageBuilder : requested CRS into GridCoverageReadParam must be same than Coverage";
//
//            final GridCoverage2D gridCoverage2D = (GridCoverage2D) coverageReader.read(imageIndex, readParam);//-- normaly with a gridGeometry2D --> gridCoverage2D
//            final RenderedImage baseImg = gridCoverage2D.getRenderedImage();
//            final MathTransform2D coverageCRS_to_grid = gridCoverage2D.getGridGeometry().getGridToCRS2D(PixelOrientation.CENTER).inverse();
//            final MathTransform destCrs_to_covGrid     = MathTransforms.concatenate(destCrs_to_coverageCRS, coverageCRS_to_grid).inverse();
//
//            final double[] fill    = getFillValue(gridCoverage2D, fillValue);
//
//            final double imgWidth  = envWidth  / pixelScal;
//            final double imgHeight = envHeight / pixelScal;
//            final double sx        = envWidth  / imgWidth;
//            final double sy        = envHeight / imgHeight;
//            final MathTransform2D globalGridDest_to_crs = new AffineTransform2D(sx, 0, 0, -sy, min0, max1);
//
//            //-- mosaic size
//            final int nbrTileX  = (int) Math.ceil(imgWidth  / tileWidth);
//            final int nbrTileY  = (int) Math.ceil(imgHeight / tileHeight);
//
//            //-- coverage extent on mosaic space
//            final GeneralEnvelope coverageExtent = Envelopes.transform(globalGridDest_to_crs.inverse(), clipEnv);
//
//            //-- coverage intersection tile index
//            final int startTileX = (int) coverageExtent.getMinimum(widthAxis)   / tileWidth;
//            final int startTileY = (int) coverageExtent.getMinimum(heightAxis)  / tileHeight;
//            final int endTileX   = (int) (coverageExtent.getMaximum(widthAxis)  + tileWidth - 1)  / tileWidth;
//            final int endTileY   = (int) (coverageExtent.getMaximum(heightAxis) + tileHeight - 1) / tileHeight;
//
//            final GridMosaic mosaic = getOrCreateMosaic(pm, pyramidID, new Dimension(nbrTileX, nbrTileY), tileSize, upperLeft, pixelScal);
//            final String mosaicId   = mosaic.getId();
//
//            final AtomicInteger inc = new AtomicInteger();
//            final RenderedImage img = new BuildImage(
//                    startTileX*tileWidth,
//                    startTileY*tileHeight,
//                    (endTileX-startTileX)*tileWidth,
//                    (endTileY-startTileY)*tileHeight,
//                    tileSize, baseImg, mosaic,
//                    processListener,
//                    inc,fill,destCrs_to_covGrid,
//                    sx,sy,min0,max1
//                    );
//
//            try {
//                pm.writeTiles(pyramidID, mosaicId, img, false, null);
//            } catch(ImagingOpException ex) {
//                if (processListener!=null) {
//                    float prc = (float)niemeTile / globalTileNumber;
//                    processListener.failed(new ProcessEvent(fakeProcess, "writing tiles", prc, ex));
//                }
//                throw new DataStoreException(ex.getMessage(), ex);
//            }
//
//        }
//    }

    /**
     * Create and insert pyramid from {@code GridCoverage2D} source,
     * in specified {@code PyramidalModel} at specified pyramidID.
     *
     * @param pm {@code PyramidalModel} in which insert pyramid tiles.
     * @param pyramidID ID in which pyramid is inserted.
     * @param gridCoverage2D source data of pyramid which will be inserted.
     * @param scaleLevel scale values table for each pyramid level. Table length represent pyramid level number.
     * @param upperLeft geographic upper left corner of pyramid will be inserted.
     * @param envDest envelope which represent multi-dimensional slice of origin coverage envelope.
     * @param widthAxis index of X direction from multi-dimensional coverage envelope.
     * @param heightAxis index of Y direction from multi-dimensional coverage envelope.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.(should be {@code null}).
     * Can be {@code null}. If {@code null} a default table value filled by zero value,
     * with length equal to source coverage image band number is created.
     */
    private void resample (final PyramidalCoverageResource pm, String pyramidID, GridCoverage gridCoverage, double[] scaleLevel,
            DirectPosition upperLeft, Envelope envDest, int widthAxis, int heightAxis)
            throws NoninvertibleTransformException, FactoryException, TransformException, DataStoreException, IOException {

        final GridGeometry gg     = gridCoverage.getGridGeometry();
        final GridGeometry2D gg2d = GridGeometry2D.castOrCopy(gg);
        final Envelope covEnv     = gg2d.getEnvelope2D();

        final RenderedImage baseImg = gridCoverage.getRenderableImage(0, 1).createDefaultRendering();
        // work on pixels coordinates.
        final MathTransform coverageCRS_to_grid = gg2d.getGridToCRS2D(PixelOrientation.CENTER).inverse();
        final double envWidth       = envDest.getSpan(widthAxis);
        final double envHeight      = envDest.getSpan(heightAxis);
//        final int nbBand            = baseImg.getSampleModel().getNumBands();
        final double[] fill         = getFillValue(gridCoverage, fillValue);
//        final int dataType          = baseImg.getSampleModel().getDataType();
        final double min0           = envDest.getMinimum(widthAxis);
        final double max1           = envDest.getMaximum(heightAxis);

        //-- MathTransform2D
        CoordinateReferenceSystem envDestCRS2D = CRSUtilities.getCRS2D(envDest.getCoordinateReferenceSystem());
        GeneralEnvelope envDest2D = GeneralEnvelope.castOrCopy(Envelopes.transform(envDest, envDestCRS2D));
        final MathTransform destCrs_to_coverageCRS = CRS.findOperation(envDestCRS2D, gg2d.getCoordinateReferenceSystem2D(), null).getMathTransform();

        final MathTransform destCrs_to_covGrid     = MathTransforms.concatenate(destCrs_to_coverageCRS, coverageCRS_to_grid).inverse();
        final Dimension tileSize                   = new Dimension(tileWidth, tileHeight);

        final GeneralEnvelope covEnvInDestCRS = Envelopes.transform(destCrs_to_coverageCRS.inverse(), covEnv);
        final GeneralEnvelope clipEnv   = ReferencingUtilities.intersectEnvelopes(covEnvInDestCRS, envDest2D);

        //one mosaic for each level scale
        //Note,Todo : sort scale by finest, we will then be able to build each mosaic from the previous level
        scaleLevel = scaleLevel.clone();
        Arrays.sort(scaleLevel);
        for (double pixelScal : scaleLevel) {
            //output image size

            final double imgWidth  = envWidth / pixelScal;
            final double imgHeight = envHeight / pixelScal;
            final double sx     = envWidth  / imgWidth;
            final double sy     = envHeight / imgHeight;
            final MathTransform2D globalGridDest_to_crs = new AffineTransform2D(sx, 0, 0, -sy, min0, max1);

            //mosaic size
            final int nbrTileX  = (int)Math.ceil(imgWidth/tileWidth);
            final int nbrTileY  = (int)Math.ceil(imgHeight/tileHeight);

            //coverage extent on mosaic space
            final GeneralEnvelope coverageExtent = Envelopes.transform(globalGridDest_to_crs.inverse(), clipEnv);

            //coverage intersection tile index
            final int startTileX = (int)coverageExtent.getMinimum(widthAxis) / tileWidth;
            final int startTileY = (int)coverageExtent.getMinimum(heightAxis) / tileHeight;
            final int endTileX   = (int)(coverageExtent.getMaximum(widthAxis) + tileWidth - 1) / tileWidth;
            final int endTileY   = (int)(coverageExtent.getMaximum(heightAxis) + tileHeight - 1) / tileHeight;

            final GridMosaic mosaic = getOrCreateMosaic(pm, pyramidID, new Dimension(nbrTileX, nbrTileY), tileSize, upperLeft, pixelScal);
            final String mosaicId   = mosaic.getId();

            final AtomicInteger inc = new AtomicInteger();
            final AtomicReference<Exception> exps = new AtomicReference<>();

            final RenderedImage img = new BuildImage(
                    startTileX*tileWidth,
                    startTileY*tileHeight,
                    (endTileX-startTileX)*tileWidth,
                    (endTileY-startTileY)*tileHeight,
                    tileSize, baseImg, mosaic,
                    processListener,
                    inc,fill,destCrs_to_covGrid,
                    sx,sy,min0,max1
                    );

            try {
                pm.writeTiles(pyramidID, mosaicId, img, false, null);
            } catch(ImagingOpException ex) {
                if (processListener!=null) {
                    float prc = (float)niemeTile / globalTileNumber;
                    processListener.failed(new ProcessEvent(fakeProcess, "writing tiles", prc, ex));
                }
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Extract BufferedImage from TileReference.
     *
     * @param tile TileReference, should not be null.
     * @return tile BufferedImage.
     * @throws IOException if error on reading image from tile ImageReader.
     */
    private BufferedImage getImageFromTile(TileReference tile) throws IOException {
        ArgumentChecks.ensureNonNull("tile", tile);

        final BufferedImage sourceImg;
        ImageReader reader = null;
        try {
            if (tile.getInput() instanceof BufferedImage) {
                sourceImg = (BufferedImage) tile.getInput();
            } else {
                final int imgIdx = tile.getImageIndex();
                reader = tile.getImageReader();
                sourceImg = tile.getImageReader().read(imgIdx);
            }
        } finally {
            if (reader != null) {
                XImageIO.dispose(reader);
            }
        }
        return sourceImg;
    }

    private static double[] getFillValue(GridCoverage gridCoverage, double[] fillValue){
        //-- calculate fill values
        if (fillValue == null) {
            final int nbBand = gridCoverage.getNumSampleDimensions();
            fillValue = new double[nbBand];
            Arrays.fill(fillValue, Double.NaN);
            for(int i=0;i<nbBand;i++){
                final SampleDimension dimension = gridCoverage.getSampleDimension(i);
                final double[] nodata;
                if (dimension instanceof GridSampleDimension) {
                    nodata = ((GridSampleDimension)dimension).geophysics(true).getNoDataValues();
                } else {
                    nodata = dimension.getNoDataValues();
                }
                if (nodata != null && nodata.length > 0) {
                    fillValue[i] = nodata[0];
                }
            }
        }
        return fillValue;
    }

    /**
     * Initialize attribut to {@link ProcessListener} use.
     *
     * @param resolution_Per_Envelope reprojection and resampling attibuts.
     * @param processListener
     */
    private void initListener(final Map<Envelope, double[]> resolution_Per_Envelope, final GridGeometry currentGridGeom,
                              final ProcessListener processListener) throws TransformException {
        assert resolution_Per_Envelope != null : "resolution_Per_Envelope should not be null";
        assert processListener         != null : "processListener should not be null";
        globalTileNumber = 0;
        niemeTile = 0;
        for (Envelope outEnv : resolution_Per_Envelope.keySet()) {
                final CoordinateReferenceSystem crs = outEnv.getCoordinateReferenceSystem();
                final int minOrdi0 = CoverageUtilities.getMinOrdinate(crs);
                final int minOrdi1 = minOrdi0 + 1;
                final int outDim   = crs.getCoordinateSystem().getDimension();
                final GridGeometryIterator gitEnv = new GridGeometryIterator(new GeneralGridGeometry(currentGridGeom));
                while (gitEnv.hasNext()) {
                    final Envelope envGit = gitEnv.next().getEnvelope();
                    final GeneralEnvelope envDest = GeneralEnvelope.castOrCopy(Envelopes.transform(envGit, crs));
                    for (int d = 0; d < outDim; d++) {
                        if (d == minOrdi0 || d == minOrdi1) {
                            //-- set horizontal crs part coordinates from out envelope into destination envelope
                            envDest.setRange(d, outEnv.getMinimum(d), outEnv.getMaximum(d));
                        }
                    }
                    //-- set horizontal crs part coordinates from out envelope into destination envelope
                    for (double pixelScal : resolution_Per_Envelope.get(outEnv)) {
                        final int nbrtx   = (int) Math.ceil((envDest.getSpan(minOrdi0) / pixelScal) / tileWidth);
                        final int nbrty   = (int) Math.ceil((envDest.getSpan(minOrdi1) / pixelScal) / tileHeight);
                        globalTileNumber += nbrtx * nbrty;
                    }
                }
            }
            processListener.started(new ProcessEvent(fakeProcess, "0/"+globalTileNumber, 0));
    }

    /**
     * Search and return a {@link GridCoverageResource} in a {@link CoverageStore} from its {@link GenericName}.<br/>
     * If it doesn't exist a {@link GridCoverageResource} is created, added in {@link CoverageStore} parameter and returned.
     *
     * @param coverageStore
     * @param coverageName
     * @return a {@link GridCoverageResource} in a {@link CoverageStore} from its {@link GenericName}.
     * @throws DataStoreException
     */
    private GridCoverageResource getOrCreateCRef(CoverageStore coverageStore, GenericName coverageName) throws DataStoreException {
        GridCoverageResource cv = null;
        for (GenericName n : coverageStore.getNames()) {
            if (n.tip().toString().equals(coverageName.tip().toString())) {
                final Resource candidate = coverageStore.findResource(n.toString());
                if (!(candidate instanceof GridCoverageResource)) {
                    throw new DataStoreException("Resource "+coverageName+" is not a coverage.");
                }
                cv = (GridCoverageResource) candidate;
            }
        }
        if (cv == null) {
            if (coverageStore instanceof WritableAggregate) {
                final WritableAggregate agg = (WritableAggregate) coverageStore;
                cv = (GridCoverageResource) agg.add(new DefiningCoverageResource(coverageName, null));
            } else {
                throw new DataStoreException("Store do not support creation operation.");
            }
        }
        return cv;
    }

    /**
     * Search and return a {@link Pyramid} in a {@link PyramidalCoverageResource} from its {@link CoordinateReferenceSystem} properties.<br/>
     * If it doesn't exist a {@link Pyramid} is created, added in {@link PyramidalCoverageResource} parameter and returned.
     *
     * @param pm
     * @param crs
     * @return a {@link Pyramid} in a {@link PyramidalCoverageResource} from its {@link CoordinateReferenceSystem} properties.
     * @throws DataStoreException
     */
    private static synchronized Pyramid getOrCreatePyramid(final PyramidalCoverageResource pm, final CoordinateReferenceSystem crs) throws DataStoreException {
        Pyramid pyramid = null;
        for (Pyramid p : pm.getPyramidSet().getPyramids()) {
            if (Utilities.equalsIgnoreMetadata(p.getCoordinateReferenceSystem(), crs)) {
                pyramid = p;
                break;
            }
        }

        if (pyramid == null) {
            pyramid = pm.createPyramid(crs);
        }

        return pyramid;
    }

    private static synchronized GridMosaic getOrCreateMosaic(final PyramidalCoverageResource pm,
            String pyramidID, Dimension gridsize, Dimension tileSize, DirectPosition upperLeft, double pixelScal) throws DataStoreException {
        final Pyramid pyramid = pm.getPyramidSet().getPyramid(pyramidID);
        for(GridMosaic gm : pyramid.getMosaics()){
            if(gm.getScale() == pixelScal && Arrays.equals(upperLeft.getCoordinate(),gm.getUpperLeftCorner().getCoordinate())){
                return gm;
            }
        }
        return pm.createMosaic(pyramidID, gridsize, tileSize, upperLeft, pixelScal);
    }

    /**
     * Check if two GridSampleDimension list are compatible.
     * GridSampleDimension list are compatible if they have same number of GridSampleDimension
     * and GridSampleDimension are in same order and also compatible.
     *
     * Test is also considered valid if the pyramid GridSampleDimension list is null or empty
     * in case of the pyramid have just been created.
     *
     * @param pyramidRef pyramid reference
     * @param coverageSampleDims input coverage SampleDimension list
     * @return true if compatible, false otherwise
     * @throws DataStoreException if an error occurs during pyramid SampleDimension reading.
     */
    private boolean isDimensionsCompatible(PyramidalCoverageResource pyramidRef, List<GridSampleDimension> coverageSampleDims)
            throws DataStoreException {

        final List<GridSampleDimension> pyramidSampleDims = pyramidRef.getSampleDimensions();
        // pyramidSampleDims list is considered as valid in case of the pyramid have just been created.
        if (pyramidSampleDims == null || pyramidSampleDims.isEmpty()) {
            return true;
        }

        assert coverageSampleDims != null && !coverageSampleDims.isEmpty() : "coverageSampleDims should not be null or empty";

        //compare size
        if (pyramidSampleDims.size() != coverageSampleDims.size()) return false;

        final int nbDims = pyramidSampleDims.size();
        for (int i = 0; i < nbDims; i++) {
            final GridSampleDimension pyramidDim = pyramidSampleDims.get(i);
            final GridSampleDimension coverageDim = coverageSampleDims.get(i);
            if (!isDimensionCompatible(pyramidDim, coverageDim)) return false;
        }
        return true;
    }

    /**
     * Compare two GridSampleDimension.
     * Check noData list, ColorModel pixelSize and ColorSpace type.
     *
     * TODO compare categories.
     *
     * @param gsd1 first GridSampleDimension
     * @param gsd2 second GridSampleDimension
     * @return true if compatible, false otherwise
     */
    private boolean isDimensionCompatible(final GridSampleDimension gsd1, final GridSampleDimension gsd2) {
        ArgumentChecks.ensureNonNull("gsd1", gsd1);
        ArgumentChecks.ensureNonNull("gsd2", gsd2);

        NumberRange range1 = gsd1.getRange();
        NumberRange range2 = gsd2.getRange();

        if (range1 == null) return range2 == null;

        if (!range1.containsAny(range2)) return false;

        //compare noData
        double[] pNoData = gsd1.getNoDataValues();
        double[] cNoData = gsd2.getNoDataValues();
        if (!Arrays.equals(pNoData, cNoData)) return false;

        //compare pixelSize and ColorSpace type
        final ColorModel pCM = gsd1.getColorModel();
        final ColorModel cCM = gsd2.getColorModel();
        if (pCM != null && cCM != null) {
            if (pCM.getPixelSize() != cCM.getPixelSize() ||
                    pCM.getColorSpace().getType() != cCM.getColorSpace().getType()) {
                return false;
            }
        }

        //TODO compare categories
        return true;
    }

    /**
     * Inner class that extend {@link AbstractLargeRenderedImage#getTile(int, int)}
     * that resample on the fly mosaic tiles.
     */
    private class BuildImage extends AbstractLargeRenderedImage{

        private final ProcessListener processListener;
        private final AtomicInteger lastProc;
        private final RenderedImage baseImg;
        private final GridMosaic mosaic;
        private final double[] fill;
        private final MathTransform destCrs_to_covGrid;
        private final double[] affArgs;

        private BuildImage(int minX, int minY, int width, int height, Dimension tileSize,
                           RenderedImage baseImg, GridMosaic mosaic, ProcessListener processListener,
                           AtomicInteger lastProc, double[] fill, MathTransform destCrs_to_covGrid,
                           double... affArgs){
            super(minX,minY,width,height,tileSize,0,0, baseImg.getSampleModel(), baseImg.getColorModel());
            this.baseImg = baseImg;
            this.mosaic = mosaic;
            this.processListener = processListener;
            this.lastProc = lastProc;
            this.fill = fill;
            this.destCrs_to_covGrid = destCrs_to_covGrid;
            this.affArgs = affArgs;
        }

//        @Override
//        public SampleModel getSampleModel() {
//            return baseImg.getSampleModel();
//        }

        @Override
        public Raster getTile(int cTX, int cTY) {
            final int destMinX = cTX * getTileWidth();
            final int destMinY = cTY * getTileHeight();
            boolean noFill = false;

            try{
                WritableRenderedImage destImg;
                if (reuseTiles && !mosaic.isMissing(cTX, cTY)) {
                    TileReference tile = mosaic.getTile(cTX, cTY, null);
                    destImg = getImageFromTile(tile);
                    noFill = true;
                } else {
                    destImg = BufferedImages.createImage(tileWidth, tileHeight, baseImg);
                    //-- ensure fill value is set.
                    ImageUtilities.fill(destImg, fill[0]);
                }

                if (processListener != null) {
                    niemeTile++;
                    //do not send too much events, one every percent
                    int prc = (niemeTile * 100 / globalTileNumber);
                    if(prc!= lastProc.getAndSet(prc)){
                        processListener.progressing(new ProcessEvent(fakeProcess, (niemeTile) + "/" + globalTileNumber, prc));
                    }
                }

                //-- dest grid --> dest envelope coordinate --> base envelope --> base grid
                //-- concatene : dest grid_to_crs, dest_crs_to_coverageCRS, coverageCRS_to_grid coverage
                final MathTransform2D gridDest_to_crs = new AffineTransform2D(affArgs[0], 0, 0, -affArgs[1], affArgs[2] + affArgs[0] * (destMinX + 0.5), affArgs[3] - affArgs[1] * (destMinY + 0.5)).inverse();
                final MathTransform mt = MathTransforms.concatenate(destCrs_to_covGrid, gridDest_to_crs);


                final Resample resample = new Resample(mt.inverse(), destImg, baseImg, interpolationCase, lanczosWindow,
                        ResampleBorderComportement.FILL_VALUE, (noFill ? null : fill));
                resample.fillImage();
                return destImg.getTile(0, 0);
            }catch(Exception ex){
                final StringWriter writer = new StringWriter();
                final PrintWriter pw = new PrintWriter(writer);
                ex.printStackTrace(pw);
                pw.flush();
                writer.flush();
                throw new ImagingOpException(ex.getMessage()+"\n"+writer.toString());
            }
        }
    };


}
