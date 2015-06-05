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

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.imageio.ImageReader;
import javax.swing.ProgressMonitor;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.LanczosInterpolation;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessListener;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.combineIterator.GridCombineIterator;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.internal.ImageUtilities;
import org.geotoolkit.util.ImageIOUtilities;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

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
 * {@code final CoordinateReferenceSystem crs4326 = CRS.decode("EPSG:4326");}<br/>
 * {@code final CoordinateReferenceSystem crs2163 = CRS.decode(";EPSG:2163");}<br/>
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
 */
public class PyramidCoverageBuilder {

    /**
     * The default tile size in pixels.
     */
    private static final int DEFAULT_TILE_SIZE = 256;

    /**
     * Minimum tile size.
     */
    private static final int MIN_TILE_SIZE = 64;

    /**
     * Tile width.
     */
    private final int tileWidth;

    /**
     * Tile height.
     */
    private final int tileHeight;

    /**
     * Interpolation properties.
     */
    private final InterpolationCase interpolationCase;
    private final int lanczosWindow;

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

    /**
     * <p>Define tile size and interpolation properties use during resampling operation.<br/><br/>
     *
     * Note : if lanczos interpolation doesn't choose lanczosWindow parameter has no impact.</p>
     *
     * @param tileSize size of tile from mosaic if null a default tile size of 256 x 256 is chosen. Minimum tile size is 64 x 64.
     * @param interpolation pixel operation use during resampling operation.
     * @param lanczosWindow only use about Lanczos interpolation.
     * @see Resample#fillImage()
     * @see LanczosInterpolation#LanczosInterpolation(org.geotoolkit.image.iterator.PixelIterator, int)
     * @see InterpolationCase
     */
    public PyramidCoverageBuilder(Dimension tileSize, InterpolationCase interpolation, int lanczosWindow) {
        this(tileSize, interpolation, lanczosWindow, false);
    }

    /**
     * <p>Define tile size and interpolation properties use during resampling operation.<br/><br/>
     *
     * Note : if lanczos interpolation doesn't choose lanczosWindow parameter has no impact.</p>
     *
     * @param tileSize size of tile from mosaic if null a default tile size of 256 x 256 is chosen. Minimum tile size is 64 x 64.
     * @param interpolation pixel operation use during resampling operation.
     * @param lanczosWindow only use about Lanczos interpolation.
     * @param reuseTiles flag to re-use mosaic tiles if already exist in output pyramid.
     * @see Resample#fillImage()
     * @see LanczosInterpolation#LanczosInterpolation(org.geotoolkit.image.iterator.PixelIterator, int)
     * @see InterpolationCase
     */
    public PyramidCoverageBuilder(Dimension tileSize, InterpolationCase interpolation, int lanczosWindow, boolean reuseTiles) {
        ArgumentChecks.ensureNonNull("interpolation", interpolation);
        ArgumentChecks.ensureStrictlyPositive("lanczosWindow", lanczosWindow);
        if (tileSize == null) {
            tileWidth = tileHeight = DEFAULT_TILE_SIZE;
        } else {
            tileWidth  = Math.min(DEFAULT_TILE_SIZE, Math.max(tileSize.width, MIN_TILE_SIZE));
            tileHeight = Math.min(DEFAULT_TILE_SIZE, Math.max(tileSize.height, MIN_TILE_SIZE));
        }
        this.interpolationCase = interpolation;
        this.lanczosWindow     = lanczosWindow;
        this.reuseTiles        = reuseTiles;
    }

    /**
     * <p>Effectuate resampling, re-projection, tile cutting and insertion in datastore on {@link GridCoverage}.<br/><br/>
     *
     * Note : <br/>
     * {@link GridGeometry} from {@link GridCoverage} must be instance of {@link GridGeometry2D}
     * else a {@link IllegalArgumentException} will be thrown.<br/>
     * fillValue parameter must have same lenght than pixel size from image within coverage.<br/>
     * If fill value is {@code null} a table of zero value with appropriate lenght is use.
     * </p>
     * @param gridCoverage {@link GridCoverage} which will be stored.
     * @param coverageStore {@link CoverageStore} where operation on {@link GridCoverage} are stored.
     * @param coverageName name given to the set of operations results, performed on the coverage in the datastore.
     * @param resolution_Per_Envelope reprojection and resampling attibuts.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @throws DataStoreException if tile writing throw exception.
     * @throws TransformException if problems during resampling operation.
     * @throws FactoryException if impossible to find {@code MathTransform} between two {@link CoordinateReferenceSystem}.
     */
    public void create(GridCoverage gridCoverage, CoverageStore coverageStore, Name coverageName,
            Map<Envelope, double[]> resolution_Per_Envelope, double[] fillValue)
            throws DataStoreException, TransformException, FactoryException, IOException {
        create(gridCoverage, coverageStore, coverageName, resolution_Per_Envelope, fillValue, null, null);
    }
    
    /**
     * <p>Effectuate resampling, re-projection, tile cutting and insertion in datastore on {@link GridCoverage}.<br/><br/>
     *
     * Note : <br/>
     * {@link GridGeometry} from {@link GridCoverage} must be instance of {@link GridGeometry2D}
     * else a {@link IllegalArgumentException} will be thrown.<br/>
     * fillValue parameter must have same lenght than pixel size from image within coverage.<br/>
     * If fill value is {@code null} a table of zero value with appropriate lenght is use.
     * </p>
     * @param gridCoverageRef {@link GridCoverage} which will be stored.
     * @param coverageStore {@link CoverageStore} where operation on {@link GridCoverage} are stored.
     * @param coverageName name given to the set of operations results, performed on the coverage in the datastore.
     * @param resolution_Per_Envelope reprojection and resampling attibuts.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @param processListener {@link ProcessListener} to send state informations (should be null).
     * @param monitor A progress monitor used for detecting a cancel request during the process. Can be {@code null}.
     * @throws DataStoreException if tile writing throw exception.
     * @throws TransformException if problems during resampling operation.
     * @throws FactoryException if impossible to find {@code MathTransform} between two {@link CoordinateReferenceSystem}.
     */
    public void create(CoverageReference gridCoverageRef, CoverageStore coverageStore, Name coverageName,
            Map<Envelope, double[]> resolution_Per_Envelope, double[] fillValue,
            ProcessListener processListener, ProgressMonitor monitor)
            throws DataStoreException, TransformException, FactoryException, IOException {
        ArgumentChecks.ensureNonNull("CoverageReference"      , gridCoverageRef);
        ArgumentChecks.ensureNonNull("output CoverageStore", coverageStore);
        ArgumentChecks.ensureNonNull("coverageName"           , coverageName);
        ArgumentChecks.ensureNonNull("resolution_Per_Envelope", resolution_Per_Envelope);

        //one coverageReference for each reader.
        final CoverageReference cv = getOrCreateCRef(coverageStore,coverageName);

        if (!(cv instanceof PyramidalCoverageReference)) {
            final IllegalArgumentException ex = new IllegalArgumentException("CoverageStore parameter should be instance of PyramidalModel."+coverageStore.toString());
            if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
            throw ex;
        }
        final PyramidalCoverageReference pm = (PyramidalCoverageReference) cv;

        //----------------------- sample dimensions-----------------------------
        final GridCoverageReader gridReader = gridCoverageRef.acquireReader();
        final int imageIndex                = gridCoverageRef.getImageIndex();
        List<GridSampleDimension> coverageSampleDims = gridReader.getSampleDimensions(imageIndex);
        if (!isDimensionsCompatible(pm, coverageSampleDims))
            throw new DataStoreException("Incompatible GridSampleDimensions. " +
                    "Input coverage should have compatible GridSampleDimension with output Pyramid.");

        pm.setSampleDimensions(coverageSampleDims);
        //----------------------------------------------------------------------
        
        final GridGeometry currentgridGeometry = gridReader.getGridGeometry(imageIndex);
        
        //-- init listener ---
        if (processListener != null) initListener(resolution_Per_Envelope, currentgridGeometry, processListener);
        
        for (Envelope outEnv : resolution_Per_Envelope.keySet()) {
            final CoordinateReferenceSystem crs = outEnv.getCoordinateReferenceSystem();
            final int minOrdi0 = CRSUtilities.firstHorizontalAxis(crs);
            final int minOrdi1 = minOrdi0 + 1;
            final int outDim   = crs.getCoordinateSystem().getDimension();
            final DirectPosition upperLeft = new GeneralDirectPosition(crs);
            upperLeft.setOrdinate(minOrdi0, outEnv.getMinimum(minOrdi0));
            upperLeft.setOrdinate(minOrdi1, outEnv.getMaximum(minOrdi1));

            //one pyramid for each CoordinateReferenceSystem.
            final Pyramid pyram         = getOrCreatePyramid(pm, crs);
//            final CombineIterator itEnv = new CombineIterator(new GeneralEnvelope(outEnv));
            final GridCombineIterator gitenv = new GridCombineIterator(new GeneralGridGeometry(currentgridGeometry));
            while (gitenv.hasNext()) {
                final Envelope gcEnv = gitenv.next();
                //-- temporary reprojection
                //-- try later to concatene gridtocrs + findmathtransform srcCrs -> outCrs
                final GeneralEnvelope envDest = GeneralEnvelope.castOrCopy(Envelopes.transform(gcEnv, crs));
                
                //set upperLeft ordinate
                for (int d = 0; d < outDim; d++) {
                    if (d != minOrdi0 && d != minOrdi1) {
                        upperLeft.setOrdinate(d, envDest.getMedian(d));
                    } else {
                        //-- set horizontal crs part coordinates from out envelope into destination envelope  
                        envDest.setRange(d, outEnv.getMinimum(d), outEnv.getMaximum(d));
                    }
                }
                final GridCoverageReadParam rp = new GridCoverageReadParam();
                rp.setEnvelope(envDest);
                rp.setDeferred(true);
                
                resample(pm, pyram.getId(), gridReader, imageIndex, rp, resolution_Per_Envelope.get(outEnv), upperLeft, envDest, minOrdi0, minOrdi1, fillValue, processListener);
            }
        }
        gridCoverageRef.recycle(gridReader);
        if (processListener != null)  processListener.completed(new ProcessEvent(fakeProcess, "Pyramid coverage builder successfully submitted.", 100));
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
     * with lenght equal to source coverage image band number is created.
     *
     * @throws NoninvertibleTransformException
     * @throws FactoryException
     * @throws TransformException
     * @throws DataStoreException
     */
    private void resample (PyramidalCoverageReference pm, String pyramidID, GridCoverageReader coverageReader, 
            int imageIndex, GridCoverageReadParam readParam, double[] scaleLevel, DirectPosition upperLeft, 
            Envelope envDest, int widthAxis, int heightAxis, double[] fillValue, ProcessListener processListener)
            throws NoninvertibleTransformException, FactoryException, TransformException, DataStoreException, IOException {

        final GeneralGridGeometry ggg = coverageReader.getGridGeometry(imageIndex);
        if (!(ggg instanceof GridGeometry2D)) {
            final IllegalArgumentException ex = new IllegalArgumentException("GridGeometry should be instance of GridGeometry2D");
            if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
            throw ex;
        }
        
        final GridGeometry2D gg2d   = (GridGeometry2D) ggg;
        final Envelope covEnv       = gg2d.getEnvelope2D();
        
        // work on pixels coordinates.
        final double envWidth       = envDest.getSpan(widthAxis);
        final double envHeight      = envDest.getSpan(heightAxis);
        final double min0           = envDest.getMinimum(widthAxis);
        final double max1           = envDest.getMaximum(heightAxis);
        
        //MathTransform2D
        CoordinateReferenceSystem envDestCRS2D = CRSUtilities.getCRS2D(envDest.getCoordinateReferenceSystem());
        GeneralEnvelope envDest2D = GeneralEnvelope.castOrCopy(CRS.transform(envDest, envDestCRS2D));
        final MathTransform destCrs_to_coverageCRS = CRS.findMathTransform(envDestCRS2D, gg2d.getCoordinateReferenceSystem2D(), true);
        
        final Dimension tileSize              = new Dimension(tileWidth, tileHeight);
        
        final GeneralEnvelope covEnvInDestCRS = CRS.transform(destCrs_to_coverageCRS.inverse(), covEnv);
        final GeneralEnvelope clipEnv   = ReferencingUtilities.intersectEnvelopes(covEnvInDestCRS, envDest2D);
        
        //------------------- param resolution configuration -------------------
        final int paramDim;
        final CoordinateReferenceSystem paramCrs = readParam.getCoordinateReferenceSystem();
        if (paramCrs != null) {
            paramDim = paramCrs.getCoordinateSystem().getDimension();
        } else if (readParam.getEnvelope() != null) {
            paramDim = readParam.getEnvelope().getDimension();
        } else {
            //-- assume param in the same crs than coverage
            readParam.setCoordinateReferenceSystem(ggg.getCoordinateReferenceSystem());
            paramDim = ggg.getDimension();
        }
        final double[] res = new double[paramDim];
        Arrays.fill(res, 1.0);
        //----------------------------------------------------------------------
        
        //-- one mosaic for each level scale
        for (double pixelScal : scaleLevel) {
            res[widthAxis] = res[heightAxis] = pixelScal;
            //-- output image size
            readParam.setResolution(res);
            assert CRS.equalsIgnoreMetadata(readParam.getCoordinateReferenceSystem(), ggg.getCoordinateReferenceSystem()) 
                    : "PyramidCoverageBuilder : requested CRS into GridCoverageReadParam must be same than Coverage";
            
            final GridCoverage2D gridCoverage2D = (GridCoverage2D) coverageReader.read(imageIndex, readParam);//-- normaly with a gridGeometry2D --> gridCoverage2D
            final RenderedImage baseImg = gridCoverage2D.getRenderedImage();
            final MathTransform2D coverageCRS_to_grid = gridCoverage2D.getGridGeometry().getGridToCRS2D(PixelOrientation.CENTER).inverse();
            final MathTransform destCrs_to_covGrid     = MathTransforms.concatenate(destCrs_to_coverageCRS, coverageCRS_to_grid).inverse();

            final double[] fill    = getFillValue(gridCoverage2D, fillValue);
            
                final double imgWidth  = envWidth  / pixelScal;
                final double imgHeight = envHeight / pixelScal;
            final double sx        = envWidth  / imgWidth;
            final double sy        = envHeight / imgHeight;
            final MathTransform2D globalGridDest_to_crs = new AffineTransform2D(sx, 0, 0, -sy, min0, max1);
                
                //-- mosaic size
                final int nbrTileX  = (int) Math.ceil(imgWidth  / tileWidth);
                final int nbrTileY  = (int) Math.ceil(imgHeight / tileHeight);
                
            //-- coverage extent on mosaic space
            final GeneralEnvelope coverageExtent = Envelopes.transform(globalGridDest_to_crs.inverse(), clipEnv);

            //-- coverage intersection tile index
            final int startTileX = (int) coverageExtent.getMinimum(widthAxis)   / tileWidth;
            final int startTileY = (int) coverageExtent.getMinimum(heightAxis)  / tileHeight;
            final int endTileX   = (int) (coverageExtent.getMaximum(widthAxis)  + tileWidth - 1)  / tileWidth;
            final int endTileY   = (int) (coverageExtent.getMaximum(heightAxis) + tileHeight - 1) / tileHeight;
            
            final GridMosaic mosaic = getOrCreateMosaic(pm, pyramidID, new Dimension(nbrTileX, nbrTileY), tileSize, upperLeft, pixelScal);
            final String mosaicId   = mosaic.getId();

            //final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(baseImg), interpolationCase, lanczosWindow);

            for (int cTY = startTileY; cTY < endTileY; cTY++) {
                for (int cTX = startTileX; cTX < endTileX; cTX++) {
                    final int destMinX = cTX * tileWidth;
                    final int destMinY = cTY * tileHeight;
                    boolean noFill = false;

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
                        processListener.progressing(new ProcessEvent(fakeProcess, (++niemeTile) + "/" + globalTileNumber, (niemeTile * 100 / globalTileNumber)));
                    }

                    //-- dest grid --> dest envelope coordinate --> base envelope --> base grid
                    //-- concatene : dest grid_to_crs, dest_crs_to_coverageCRS, coverageCRS_to_grid coverage
                    final MathTransform2D gridDest_to_crs = new AffineTransform2D(sx, 0, 0, -sy, min0 + sx * (destMinX + 0.5), max1 - sy * (destMinY + 0.5)).inverse();
                    final MathTransform mt = MathTransforms.concatenate(destCrs_to_covGrid, gridDest_to_crs);

                    final Resample resample = new Resample(mt.inverse(), destImg, baseImg, interpolationCase, lanczosWindow,
                            ResampleBorderComportement.FILL_VALUE, (noFill ? null : fill));
                    resample.fillImage();
                    pm.writeTile(pyramidID, mosaicId, cTX, cTY, destImg);
                }
            }
        }
    }

    /**
     * <p>Effectuate resampling, re-projection, tile cutting and insertion in datastore on {@link GridCoverage}.<br/><br/>
     *
     * Note : <br/>
     * {@link GridGeometry} from {@link GridCoverage} must be instance of {@link GridGeometry2D}
     * else a {@link IllegalArgumentException} will be thrown.<br/>
     * fillValue parameter must have same lenght than pixel size from image within coverage.<br/>
     * If fill value is {@code null} a table of zero value with appropriate lenght is use.
     * </p>
     * @param gridCoverage {@link GridCoverage} which will be stored.
     * @param coverageStore {@link CoverageStore} where operation on {@link GridCoverage} are stored.
     * @param coverageName name given to the set of operations results, performed on the coverage in the datastore.
     * @param resolution_Per_Envelope reprojection and resampling attibuts.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @param processListener {@link ProcessListener} to send state informations (should be null).
     * @param monitor A progress monitor used for detecting a cancel request during the process. Can be {@code null}.
     * @throws DataStoreException if tile writing throw exception.
     * @throws TransformException if problems during resampling operation.
     * @throws FactoryException if impossible to find {@code MathTransform} between two {@link CoordinateReferenceSystem}.
     */
    public void create(GridCoverage gridCoverage, CoverageStore coverageStore, Name coverageName,
            Map<Envelope, double[]> resolution_Per_Envelope, double[] fillValue,
            ProcessListener processListener, ProgressMonitor monitor)
            throws DataStoreException, TransformException, FactoryException, IOException {
        ArgumentChecks.ensureNonNull("GridCoverage"           , gridCoverage);
        ArgumentChecks.ensureNonNull("output CoverageStore"   , coverageStore);
        ArgumentChecks.ensureNonNull("coverageName"           , coverageName);
        ArgumentChecks.ensureNonNull("resolution_Per_Envelope", resolution_Per_Envelope);

        final GridGeometry gg = gridCoverage.getGridGeometry();
        
        if (!(gg instanceof GridGeometry2D)) {
            final IllegalArgumentException ex = new IllegalArgumentException("GridGeometry not instance of GridGeometry2D");
            if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
            throw ex;
        }

        final CoverageReference cv  = getOrCreateCRef(coverageStore,coverageName);
        if (!(cv instanceof PyramidalCoverageReference)) {
            final IllegalArgumentException ex = new IllegalArgumentException("CoverageReference not instance of PyramidalCoverageReference");
            if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
            throw ex;
        }
        if (monitor != null && monitor.isCanceled()) {
            final CancellationException ex = new CancellationException();
            if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
            throw ex;
        }
        
        if (processListener != null) initListener(resolution_Per_Envelope, gg, processListener);
        
        final PyramidalCoverageReference pm     = (PyramidalCoverageReference) cv;
        
        //------------------------ add sampleDimension -------------------------
        final int nbSampleDimension                     = gridCoverage.getNumSampleDimensions();
        final ArrayList<GridSampleDimension> sampleList = new ArrayList<GridSampleDimension>();
        for (int nbs = 0; nbs < nbSampleDimension; nbs++) {
            final SampleDimension s = gridCoverage.getSampleDimension(nbs);
            if (s instanceof GridSampleDimension) {
                sampleList.add((GridSampleDimension) s);
            } else {
                //-- should never append for the moment the only sampleDimension type is GridSampleDimension.
                throw new IllegalStateException("The only supported SampleDimension type is GridSampleDimension. Found : "+s.getClass());
            }
        }
        if (!isDimensionsCompatible(pm, sampleList))
            throw new DataStoreException("Incompatible GridSampleDimensions. " +
                    "Input coverage should have compatible GridSampleDimension with output Pyramid.");

        pm.setSampleDimensions(sampleList);
        //----------------------------------------------------------------------
        
        
        //Image
        fillValue = getFillValue((GridCoverage2D)gridCoverage, fillValue);

        for (Envelope envDest : resolution_Per_Envelope.keySet()) {
            if (monitor != null && monitor.isCanceled()) {
                final CancellationException ex = new CancellationException();
                if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
                throw ex;
            }
            final CoordinateReferenceSystem crs = envDest.getCoordinateReferenceSystem();
            final int minOrdi0                  = CoverageUtilities.getMinOrdinate(crs);
            final int minOrdi1                  = minOrdi0 + 1;
            final DirectPosition upperLeft      = new GeneralDirectPosition(crs);
            upperLeft.setOrdinate(minOrdi0, envDest.getMinimum(minOrdi0));
            upperLeft.setOrdinate(minOrdi1, envDest.getMaximum(minOrdi1));
            //one pyramid for each CoordinateReferenceSystem.
            final Pyramid pyram                 = getOrCreatePyramid(pm, crs);
            resample(pm, pyram.getId(), ((GridCoverage2D)gridCoverage), resolution_Per_Envelope.get(envDest), upperLeft, envDest, minOrdi0, minOrdi1, fillValue, processListener);
        }
        if (processListener != null)  processListener.completed(new ProcessEvent(fakeProcess, "Pyramid coverage builder successfully submitted.", 100));
    }

    /**
     * <p>Effectuate resampling, re-projection, tile cutting and insertion in datastore on {@link GridCoverage} from {@link GridCoverageReader}.<br/><br/>
     *
     * Note : <br/>
     * {@link GridGeometry} from {@link GridCoverage} must be instance of {@link GridGeometry2D}
     * else a {@link IllegalArgumentException} will be thrown.<br/>
     * fillValue parameter must have same lenght than pixel size from image within coverage.<br/>
     * If fill value is {@code null} a table of zero value with appropriate lenght is use.
     * </p>
     *
     * @param reader {@link GridCoverageReader} which contain {@link GridCoverage} will be stored.
     * @param coverageStore {@link CoverageStore} where operation on {@link GridCoverage} are stored.
     * @param coverageName name given to the set of operations results, performed on the coverage in the datastore.
     * @param resolution_Per_Envelope reprojection and resampling attibuts.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.(should be {@code null}).
     * @throws DataStoreException if tile writing throw exception.
     * @throws TransformException if problems during resampling operation.
     * @throws FactoryException if impossible to find {@code MathTransform} between two {@link CoordinateReferenceSystem}.
     */
    public void create(GridCoverageReader reader, CoverageStore coverageStore, Name coverageName,
            Map<Envelope, double[]> resolution_Per_Envelope, double[] fillValue)
            throws DataStoreException, TransformException, FactoryException, IOException {
        create(reader, coverageStore, coverageName, resolution_Per_Envelope, fillValue, null);
    }

    /**
     * <p>Effectuate resampling, re-projection, tile cutting and insertion in datastore on {@link GridCoverage} from {@link GridCoverageReader}.<br/><br/>
     *
     * Note : <br/>
     * {@link GridGeometry} from {@link GridCoverage} must be instance of {@link GridGeometry2D}
     * else a {@link IllegalArgumentException} will be thrown.<br/>
     * fillValue parameter must have same lenght than pixel size from image within coverage.<br/>
     * If fill value is {@code null} a table of zero value with appropriate lenght is use.
     * </p>
     *
     * @param reader {@link GridCoverageReader} which contain {@link GridCoverage} will be stored.
     * @param coverageStore {@link CoverageStore} where operation on {@link GridCoverage} are stored.
     * @param coverageName name given to the set of operations results, performed on the coverage in the datastore.
     * @param resolution_Per_Envelope reprojection and resampling attibuts.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @param processListener {@link ProcessListener} to send state informations (should be null).
     * @throws DataStoreException if tile writing throw exception.
     * @throws TransformException if problems during resampling operation.
     * @throws FactoryException if impossible to find {@code MathTransform} between two {@link CoordinateReferenceSystem}.
     */
    public void create(GridCoverageReader reader, CoverageStore coverageStore, Name coverageName,
            Map<Envelope, double[]> resolution_Per_Envelope, double[] fillValue, ProcessListener processListener)
            throws DataStoreException, TransformException, FactoryException, IOException {
        ArgumentChecks.ensureNonNull("GridCoverageReader"     , reader);
        ArgumentChecks.ensureNonNull("output CoverageStore"   , coverageStore);
        ArgumentChecks.ensureNonNull("coverageName"           , coverageName);
        ArgumentChecks.ensureNonNull("resolution_Per_Envelope", resolution_Per_Envelope);

        final GridGeometry currentGridGeometry = reader.getGridGeometry(0);
        if (processListener != null) initListener(resolution_Per_Envelope, currentGridGeometry, processListener);
        
        final GridCoverageReadParam rp = new GridCoverageReadParam();

        //one coverageReference for each reader.
        final CoverageReference cv = getOrCreateCRef(coverageStore,coverageName);

        if (!(cv instanceof PyramidalCoverageReference)) {
            final IllegalArgumentException ex = new IllegalArgumentException("CoverageStore parameter should be instance of PyramidalModel."+coverageStore.toString());
            if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
            throw ex;
        }
        final PyramidalCoverageReference pm = (PyramidalCoverageReference) cv;
        
        //----------------------- sample dimensions-----------------------------
        List<GridSampleDimension> coverageSampleDims = reader.getSampleDimensions(0);
        if (!isDimensionsCompatible(pm, coverageSampleDims))
            throw new DataStoreException("Incompatible GridSampleDimensions. " +
                    "Input coverage should have compatible GridSampleDimension with output Pyramid.");
        pm.setSampleDimensions(coverageSampleDims); //-- default image index
        //----------------------------------------------------------------------

        for (Envelope outEnv : resolution_Per_Envelope.keySet()) {
            final CoordinateReferenceSystem crs = outEnv.getCoordinateReferenceSystem();
            final int minOrdi0 = CoverageUtilities.getMinOrdinate(crs);
            final int minOrdi1 = minOrdi0 + 1;
            final int outDim   = crs.getCoordinateSystem().getDimension();
            final DirectPosition upperLeft = new GeneralDirectPosition(crs);
            upperLeft.setOrdinate(minOrdi0, outEnv.getMinimum(minOrdi0));
            upperLeft.setOrdinate(minOrdi1, outEnv.getMaximum(minOrdi1));

            //one pyramid for each CoordinateReferenceSystem.
            final Pyramid pyram         = getOrCreatePyramid(pm, crs);
//            final CombineIterator itEnv = new CombineIterator(new GeneralEnvelope(outEnv));
            final GridCombineIterator gitenv = new GridCombineIterator(new GeneralGridGeometry(currentGridGeometry));
            while (gitenv.hasNext()) {
                final GeneralEnvelope envDest = GeneralEnvelope.castOrCopy(Envelopes.transform(gitenv.next(), crs));
                
                for (int d = 0; d < outDim; d++) {
                    if (d != minOrdi0 && d != minOrdi1) {
                        //set upperLeft ordinate
                        upperLeft.setOrdinate(d, envDest.getMedian(d));
                    } else {
                        //-- set horizontal crs part coordinates from out envelope into destination envelope  
                        envDest.setRange(d, outEnv.getMinimum(d), outEnv.getMaximum(d));
                    }
                }
                rp.clear();
                rp.setEnvelope(envDest);
                rp.setDeferred(true);
                final GridCoverage gridCoverage = reader.read(0, rp);
                final GridGeometry gg           = gridCoverage.getGridGeometry();
                if (!(gg instanceof GridGeometry2D)) {
                    final IllegalArgumentException ex = new IllegalArgumentException("GridGeometry should be instance of GridGeometry2D");
                    if (processListener != null) processListener.failed(new ProcessEvent(fakeProcess, "", 0, ex));
                    throw ex;
                }

                final GridCoverage2D gridCoverage2D = (GridCoverage2D) gridCoverage;

                resample(pm, pyram.getId(), gridCoverage2D, resolution_Per_Envelope.get(outEnv), upperLeft, envDest, minOrdi0, minOrdi1, fillValue, processListener);
            }
        }
        if (processListener != null)  processListener.completed(new ProcessEvent(fakeProcess, "Pyramid coverage builder successfully submitted.", 100));
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
     * with lenght equal to source coverage image band number is created.
     *
     * @throws NoninvertibleTransformException
     * @throws FactoryException
     * @throws TransformException
     * @throws DataStoreException
     */
    private void resample (PyramidalCoverageReference pm, String pyramidID, GridCoverage2D gridCoverage2D, double[] scaleLevel,
            DirectPosition upperLeft, Envelope envDest, int widthAxis, int heightAxis, double[] fillValue, ProcessListener processListener)
            throws NoninvertibleTransformException, FactoryException, TransformException, DataStoreException, IOException {

        final GridGeometry2D gg2d       = gridCoverage2D.getGridGeometry();
        final Envelope covEnv           = gg2d.getEnvelope2D();

        final RenderedImage baseImg = gridCoverage2D.getRenderedImage();
        // work on pixels coordinates.
        final MathTransform2D coverageCRS_to_grid = gg2d.getGridToCRS2D(PixelOrientation.CENTER).inverse();
        final double envWidth       = envDest.getSpan(widthAxis);
        final double envHeight      = envDest.getSpan(heightAxis);
//        final int nbBand            = baseImg.getSampleModel().getNumBands();
        final double[] fill         = getFillValue(gridCoverage2D, fillValue);
//        final int dataType          = baseImg.getSampleModel().getDataType();
        final double min0           = envDest.getMinimum(widthAxis);
        final double max1           = envDest.getMaximum(heightAxis);
        
        //-- MathTransform2D
        CoordinateReferenceSystem envDestCRS2D = CRSUtilities.getCRS2D(envDest.getCoordinateReferenceSystem());
        GeneralEnvelope envDest2D = GeneralEnvelope.castOrCopy(CRS.transform(envDest, envDestCRS2D));
        final MathTransform destCrs_to_coverageCRS = CRS.findMathTransform(envDestCRS2D, gridCoverage2D.getCoordinateReferenceSystem2D(), true);

        final MathTransform destCrs_to_covGrid     = MathTransforms.concatenate(destCrs_to_coverageCRS, coverageCRS_to_grid).inverse();
        final Dimension tileSize                   = new Dimension(tileWidth, tileHeight);

        final GeneralEnvelope covEnvInDestCRS = CRS.transform(destCrs_to_coverageCRS.inverse(), covEnv);
        final GeneralEnvelope clipEnv   = ReferencingUtilities.intersectEnvelopes(covEnvInDestCRS, envDest2D);
        
        //one mosaic for each level scale
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

            //final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(baseImg), interpolationCase, lanczosWindow);

            for (int cTY = startTileY; cTY < endTileY; cTY++) {
                for (int cTX = startTileX; cTX < endTileX; cTX++) {
                    final int destMinX = cTX * tileWidth;
                    final int destMinY = cTY * tileHeight;
                    boolean noFill = false;

                    WritableRenderedImage destImg;
                    if (reuseTiles && !mosaic.isMissing(cTX, cTY)) {
                        TileReference tile = mosaic.getTile(cTX, cTY, null);
                        destImg = getImageFromTile(tile);
                        noFill = true;
                    } else {
                        destImg = BufferedImages.createImage(tileWidth, tileHeight, baseImg);
                        //ensure fill value is set.
                        ImageUtilities.fill(destImg, fill[0]);
                    }

                    if (processListener != null) {
                        processListener.progressing(new ProcessEvent(fakeProcess, (++niemeTile) + "/" + globalTileNumber, (niemeTile * 100 / globalTileNumber)));
                    }

                    //dest grid --> dest envelope coordinate --> base envelope --> base grid
                    //concatene : dest grid_to_crs, dest_crs_to_coverageCRS, coverageCRS_to_grid coverage
                    final MathTransform2D gridDest_to_crs = new AffineTransform2D(sx, 0, 0, -sy, min0 + sx * (destMinX + 0.5), max1 - sy * (destMinY + 0.5)).inverse();
                    final MathTransform mt = MathTransforms.concatenate(destCrs_to_covGrid, gridDest_to_crs);

                    final Resample resample = new Resample(mt.inverse(), destImg, baseImg, interpolationCase, lanczosWindow,
                            ResampleBorderComportement.FILL_VALUE, (noFill ? null : fill));
                    resample.fillImage();
                    pm.writeTile(pyramidID, mosaicId, cTX, cTY, destImg);
                }
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
                ImageIOUtilities.releaseReader(reader);
            }
        }
        return sourceImg;
    }

    private static double[] getFillValue(GridCoverage2D gridCoverage2D, double[] fillValue){
        //-- calculate fill values
        if (fillValue == null) {
            final GridSampleDimension[] dimensions = gridCoverage2D.getSampleDimensions();
            final int nbBand = dimensions.length;
            fillValue = new double[nbBand];
            Arrays.fill(fillValue, Double.NaN);
            for(int i=0;i<nbBand;i++){
                final double[] nodata = dimensions[i].geophysics(true).getNoDataValues();
                if (nodata != null && nodata.length > 0){
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
//                final CombineIterator itEnv = new CombineIterator(new GeneralEnvelope(outEnv));
                final GridCombineIterator gitEnv = new GridCombineIterator(new GeneralGridGeometry(currentGridGeom));
                while (gitEnv.hasNext()) {
                    final Envelope envGit = gitEnv.next();
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
     * Search and return a {@link CoverageReference} in a {@link CoverageStore} from its {@link Name}.<br/>
     * If it doesn't exist a {@link CoverageReference} is created, added in {@link CoverageStore} parameter and returned.
     *
     * @param coverageStore
     * @param coverageName
     * @return a {@link CoverageReference} in a {@link CoverageStore} from its {@link Name}.
     * @throws DataStoreException
     */
    private CoverageReference getOrCreateCRef(CoverageStore coverageStore, Name coverageName) throws DataStoreException {
        CoverageReference cv = null;
        for (Name n : coverageStore.getNames()) {
            if (n.tip().toString().equals(coverageName.tip().toString())) {
                cv = coverageStore.getCoverageReference(n);
            }
        }
        if (cv == null) {
            cv = coverageStore.create(coverageName);
        }
        return cv;
    }

    /**
     * Search and return a {@link Pyramid} in a {@link PyramidalCoverageReference} from its {@link CoordinateReferenceSystem} properties.<br/>
     * If it doesn't exist a {@link Pyramid} is created, added in {@link PyramidalCoverageReference} parameter and returned.
     *
     * @param pm
     * @param crs
     * @return a {@link Pyramid} in a {@link PyramidalCoverageReference} from its {@link CoordinateReferenceSystem} properties.
     * @throws DataStoreException
     */
    public static synchronized Pyramid getOrCreatePyramid(final PyramidalCoverageReference pm, final CoordinateReferenceSystem crs) throws DataStoreException {
        Pyramid pyramid = null;
        for (Pyramid p : pm.getPyramidSet().getPyramids()) {
            if (CRS.equalsIgnoreMetadata(p.getCoordinateReferenceSystem(), crs)) {
                pyramid = p;
                break;
            }
        }

        if (pyramid == null) {
            pyramid = pm.createPyramid(crs);
        }

        return pyramid;
    }

    public static synchronized GridMosaic getOrCreateMosaic(final PyramidalCoverageReference pm,
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
     * Returns a {@link GridMosaic} which already exist.<br><br>
     * <strong>
     * Note : an exception is thrown if tile does not exist.
     * </strong>
     * 
     * @param pm {@link PyramidalCoverageReference} where the requested {@link GridMosaic} is looking for.
     * @param pyramidID {@link Pyramid} identifier of the requested {@link GridMosaic}.
     * @param envDest requested {@link Envelope} in relation with needed {@link GridMosaic}.
     * @param pixelScal requested scale in relation with needed {@link GridMosaic}.
     * @return a {@link GridMosaic} which already exist.
     * @throws DataStoreException if pyramid does not match with the pyramid_ID or 
     * also if requested {@link GridMosaic} was not found. 
     */
    public static synchronized GridMosaic getMosaic(final PyramidalCoverageReference pm,
            String pyramidID, Envelope envDest, double pixelScal) throws DataStoreException {
        final Pyramid pyramid = pm.getPyramidSet().getPyramid(pyramidID);
        for (GridMosaic gm : pyramid.getMosaics()) {
            if (gm.getScale() == pixelScal) {
                final GeneralEnvelope mosGenEnv = new GeneralEnvelope(gm.getEnvelope());
                if (mosGenEnv.intersects(envDest, true)) { //-- return a mosaic only if they have more than just touch intersection
                    return gm;
                }
            }
        }
        throw new DataStoreException("getOrCreateMosaic : with reuse tile. No already built mosaic can contains new data.");
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
    private boolean isDimensionsCompatible(PyramidalCoverageReference pyramidRef, List<GridSampleDimension> coverageSampleDims)
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
}
