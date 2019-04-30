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
package org.geotoolkit.processing.coverage.copy;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.imageio.ImageReader;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.WritableAggregate;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.GridGeometryIterator;
import org.geotoolkit.data.multires.DefiningMosaic;
import org.geotoolkit.data.multires.DefiningPyramid;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import static org.geotoolkit.processing.coverage.copy.CopyCoverageStoreDescriptor.ERASE;
import static org.geotoolkit.processing.coverage.copy.CopyCoverageStoreDescriptor.INSTANCE;
import static org.geotoolkit.processing.coverage.copy.CopyCoverageStoreDescriptor.REDUCE_TO_DOMAIN;
import static org.geotoolkit.processing.coverage.copy.CopyCoverageStoreDescriptor.STORE_IN;
import static org.geotoolkit.processing.coverage.copy.CopyCoverageStoreDescriptor.STORE_OUT;
import org.geotoolkit.processing.coverage.reducetodomain.ReduceToDomainDescriptor;
import org.geotoolkit.processing.coverage.straighten.StraightenDescriptor;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.storage.coverage.ImageTile;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ImageCRS;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * Copy a {@linkplain CoverageStore coverage store} into another one, that supports
 * a {@linkplain PyramidalModel pyramid model}.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CopyCoverageStoreProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public CopyCoverageStoreProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *
     * @param inStore input coverage store
     * @param outStore output coverage store
     * @param erase erase output data before insert
     * @param reduce reduce to data domain
     */
    public CopyCoverageStoreProcess(DataStore inStore,DataStore outStore,boolean erase, boolean reduce){
        super(INSTANCE, asParameters(inStore,outStore,erase,reduce));
    }

    private static ParameterValueGroup asParameters(DataStore inStore, DataStore outStore,boolean erase, boolean reduce){
        final Parameters params = Parameters.castOrWrap(CopyCoverageStoreDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(CopyCoverageStoreDescriptor.STORE_IN).setValue(inStore);
        params.getOrCreate(CopyCoverageStoreDescriptor.STORE_OUT).setValue(outStore);
        params.getOrCreate(CopyCoverageStoreDescriptor.ERASE).setValue(erase);
        params.getOrCreate(CopyCoverageStoreDescriptor.REDUCE_TO_DOMAIN).setValue(reduce);
        return params;
    }

    /**
     * Execute process now.
     *
     * @throws ProcessException
     */
    public void executeNow() throws ProcessException {
        execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws ProcessException {
        final DataStore     inStore  = inputParameters.getValue(STORE_IN);
        final DataStore     outStore = inputParameters.getValue(STORE_OUT);
        final Boolean       erase    = inputParameters.getValue(ERASE);
        final Boolean       reduce   = inputParameters.getValue(REDUCE_TO_DOMAIN);

        if (!(outStore instanceof WritableAggregate)) {
            throw new ProcessException("Outut store is not writable.",this);
        }
        final WritableAggregate outAggregate = (WritableAggregate) outStore;

        try {
            final Collection<GridCoverageResource> gcrs = DataStores.flatten(inStore, true, GridCoverageResource.class);
            final float size = gcrs.size();
            int inc = 0;
            for(GridCoverageResource gcr : gcrs){
                GenericName n = gcr.getIdentifier();
                fireProgressing("Copying "+n+".", (int)((inc*100f)/size), false);
                final Resource resource = inStore.findResource(n.toString());
                if (resource instanceof GridCoverageResource) {
                    final GridCoverageResource inRef = (GridCoverageResource) resource;
                    final GenericName name = inRef.getIdentifier();

                    //remove if exist
                    if (erase) {
                        try {
                            final Resource res = outStore.findResource(name.toString());
                            outAggregate.remove(res);
                        } catch (IllegalNameException e) {
                            //resource does not exist
                        }
                    }

                    final GridCoverageResource outRef = (GridCoverageResource) outAggregate.add(new DefiningCoverageResource(name, null));

                    if(inRef instanceof PyramidalCoverageResource && outRef instanceof PyramidalCoverageResource){
                        savePMtoPM((PyramidalCoverageResource)inRef, (PyramidalCoverageResource)outRef);
                    }else if(outRef instanceof PyramidalCoverageResource){
                        savePlainToPM(inRef, (PyramidalCoverageResource)outRef, reduce);
                    }else{
                        throw new DataStoreException("The given coverage reference is not a pyramidal model, "
                        + "this process only work with this kind of model.");
                    }
                    inc++;
                }
            }
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getLocalizedMessage(), this, ex);
        } catch (TransformException ex) {
            throw new ProcessException(ex.getLocalizedMessage(), this, ex);
        }
    }

    /**
     * If both source and target are pyramid model, we can copy each tiles.
     */
    private void savePMtoPM(final PyramidalCoverageResource inPM, final PyramidalCoverageResource outPM) throws DataStoreException{

        final List<SampleDimension> sampleDimensions = inPM.getSampleDimensions();
        if(sampleDimensions != null){
            outPM.setSampleDimensions(sampleDimensions);
        }

        //count total number of tiles
        long nb = 0;
        for(final Pyramid inPY : inPM.getModels()){
            for(final Mosaic inGM : inPY.getMosaics()){
                nb += inGM.getGridSize().height*inGM.getGridSize().width;
            }
        }
        final long total = nb;

        final int processors = Runtime.getRuntime().availableProcessors();
        final ExecutorService es = Executors.newFixedThreadPool(processors);
        try{
            final long before = System.currentTimeMillis();

            final AtomicLong count = new AtomicLong();
            //copy pyramids
            for(final Pyramid inPY : inPM.getModels()){
                final Pyramid outPY = (Pyramid) outPM.createModel(new DefiningPyramid(inPY.getCoordinateReferenceSystem()));
                //copy mosaics
                for(final Mosaic inGM : inPY.getMosaics()){
                    final Dimension gridDimension = inGM.getGridSize();
                    final Mosaic outGM = outPY.createMosaic(inGM);


                    //collection of all tile points
                    final Collection<Point> allPoints = new AbstractCollection<Point>() {
                        @Override
                        public Iterator<Point> iterator() {
                            return new Iterator<Point>() {
                                int x = 0;
                                int y = 0;
                                Point next = null;

                                @Override
                                public boolean hasNext() {
                                    findNext();
                                    return next != null;
                                }

                                @Override
                                public Point next() {
                                    findNext();
                                    Point cp = next;
                                    next = null;
                                    if(cp == null){
                                        throw new NoSuchElementException("no more element");
                                    }
                                    return cp;
                                }

                                private void findNext(){
                                    if(next != null) return;

                                    if(x>=gridDimension.width){
                                        x=0;
                                        y++;
                                    }
                                    if(y>=gridDimension.height){
                                        //reached the end
                                        return;
                                    }

                                    next = new Point(x, y);
                                    x++;
                                }

                                @Override
                                public void remove() {
                                }
                            };
                        }
                        @Override
                        public int size() {
                            return gridDimension.height*gridDimension.width;
                        }
                    };

                    final BlockingQueue<Object> queue = inGM.getTiles(allPoints, null);
                    while(true){
                        final Object obj;
                        try {
                            obj = queue.take();
                        } catch (InterruptedException ex) {
                            Logging.getLogger("org.geotoolkit.processing.coverage.copy").log(Level.SEVERE, null, ex);
                            continue;
                        }
                        if(obj == Mosaic.END_OF_QUEUE){
                            break;
                        }else if(obj == null){
                            continue;
                        }
                        final ImageTile inTR = (ImageTile) obj;
                        final int x = inTR.getPosition().x;
                        final int y = inTR.getPosition().y;
                        ImageReader inReader = null;
                        try{
                            RenderedImage image = inTR.getImage();
                            if (image.getColorModel() instanceof IndexColorModel) {
                                final BufferedImage bg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                                bg.createGraphics().drawRenderedImage(image, new AffineTransform());
                                image = bg;
                            }
                            final RenderedImage img = image;
                            es.submit(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        outGM.writeTiles(Stream.of(new DefaultImageTile(img, new Point(x, y))), null);
                                    } catch (DataStoreException ex) {
                                        CopyCoverageStoreProcess.this.fireWarningOccurred(ex.getMessage(), 0, ex);
                                        return;
                                    }
                                    final long inc = count.incrementAndGet();
                                    final long current = System.currentTimeMillis();
                                    final long oneTileTime = (current-before) / inc;
                                    final long remaining = (total-inc) * oneTileTime;
                                    final String remtext = TemporalUtilities.durationToString(remaining);
                                    fireProgressing(inc+" / "+total+ " ("+remtext+")", inc/total, false);
                                }
                            });

                        }catch(IOException ex){
                            throw new DataStoreException(ex);
                        }finally{
                            //dispose reader and substream
                            XImageIO.disposeSilently(inReader);
                        }
                    }

                }
            }
        }finally{
            es.shutdown();
        }
    }

    /**
     * Save a {@link GridCoverageResource coverage} into a {@linkplain CoverageStore coverage store}.
     *
     * @param outStore Coverage store in which to copy values.
     * @param inRef Coverage to store.
     * @param erase {@code True} if the data must be erased.
     *
     * @throws DataStoreException
     * @throws TransformException
     */
    private void savePlainToPM(final GridCoverageResource inRef, final PyramidalCoverageResource outPM, Boolean reduce)
            throws DataStoreException, TransformException, ProcessException {

        if(reduce == null) reduce = Boolean.TRUE;

        final GridGeometry globalGeom = inRef.getGridGeometry();
        final CoordinateReferenceSystem crs = globalGeom.getCoordinateReferenceSystem();

        final GenericName name = inRef.getIdentifier();
        if(crs instanceof ImageCRS){
            //image is not georeferenced, we can't store it.
            fireWarningOccurred("Image "+name+" does not have a CoordinateReferenceSystem, insertion is skipped.", 0, null);
            return;
        }

        //create sampleDimensions bands
        final List<SampleDimension> sampleDimensions = inRef.getSampleDimensions();
        outPM.setSampleDimensions(sampleDimensions);

        final Pyramid pyramid = (Pyramid) outPM.createModel(new DefiningPyramid(crs));

        // save all possible envelope slice combinations in a separate mosaic.
        final GridGeometryIterator gridCIte = new GridGeometryIterator(globalGeom);
        while (gridCIte.hasNext()) {
            GeneralEnvelope env = GeneralEnvelope.castOrCopy(gridCIte.next().getEnvelope());
            saveMosaic(outPM, pyramid, inRef, env, reduce);
        }

    }

    /**
     * Save a {@linkplain Pyramid mosaic} via its {@linkplain PyramidalModel model}.
     *
     * @param pm The {@linkplain PyramidalModel model} of this pyramid. Must not be {@code null}.
     * @param pyramid {@link Pyramid} to store. Must not be {@code null}.
     * @param reader {@linplain GridCoverageReader reader} of the input coverage. Must not be {@code null}.
     * @param imageIndex Index of the image to features in the reader.
     * @param env {@link Envelope} of the pyramid.
     * @throws DataStoreException
     * @throws TransformException
     */
    private void saveMosaic(final PyramidalCoverageResource pm, final Pyramid pyramid, final GridCoverageResource inRes,
            Envelope env, boolean reduce) throws DataStoreException, TransformException, ProcessException {

        GridCoverage coverage;
        if (env != null) {
            coverage = inRes.read(inRes.getGridGeometry().derive().subgrid(env).build());
        }else{
            env = inRes.getGridGeometry().getEnvelope();
            coverage = inRes.read(null);
        }
        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();

        //straighten coverage
        final Parameters subParams = Parameters.castOrWrap(StraightenDescriptor.INPUT_DESC.createValue());
        subParams.getOrCreate(StraightenDescriptor.COVERAGE_IN).setValue(coverage);
        final Process subprocess = StraightenDescriptor.INSTANCE.createProcess(subParams);
        Parameters result;
        try{
            result = Parameters.castOrWrap(subprocess.call());
        }catch(ProcessException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        coverage = result.getOrCreate(StraightenDescriptor.COVERAGE_OUT).getValue();

        //reduce to valid domain
        if(reduce){
            final Parameters redParams = Parameters.castOrWrap(ReduceToDomainDescriptor.INPUT_DESC.createValue());
            redParams.getOrCreate(ReduceToDomainDescriptor.COVERAGE_IN).setValue(coverage);
            final Process redprocess = ReduceToDomainDescriptor.INSTANCE.createProcess(redParams);
            try{
                result = Parameters.castOrWrap(redprocess.call());
                coverage = result.getOrCreate(StraightenDescriptor.COVERAGE_OUT).getValue();
            }catch(ProcessException ex){
                throw new ProcessException(ex.getMessage(), this, ex);
            }

        }

        final GridGeometry2D gridgeom = GridGeometry2D.castOrCopy(coverage.getGridGeometry());
        //we know it's an affine transform since we straighten the coverage
        final AffineTransform2D gridToCRS = (AffineTransform2D) gridgeom.getGridToCRS2D(PixelOrientation.UPPER_LEFT);
        final double scale = gridToCRS.getScaleX();

        final RenderedImage img = coverage.render(null);
        final Dimension gridSize = new Dimension(1, 1);
        final Dimension TileSize = new Dimension(img.getWidth(), img.getHeight());
        final Envelope covEnv = gridgeom.getEnvelope();
        final GeneralDirectPosition upperleft = new GeneralDirectPosition(crs);
        upperleft.setOrdinate(0, covEnv.getMinimum(0));
        upperleft.setOrdinate(1, covEnv.getMaximum(1));
        //envelope seems to lost its additional 2D+ values
        for (int i = 2, n = env.getDimension(); i < n; i++) {
            upperleft.setOrdinate(i, env.getMedian(i));
        }
        final Mosaic mosaic = pyramid.createMosaic(
                new DefiningMosaic(null, upperleft, scale, TileSize, gridSize));
        mosaic.writeTiles(Stream.of(new DefaultImageTile(img, new Point(0, 0))), null);
    }
}
