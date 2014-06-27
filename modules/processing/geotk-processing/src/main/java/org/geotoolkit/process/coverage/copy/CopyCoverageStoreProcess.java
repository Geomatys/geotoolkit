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
package org.geotoolkit.process.coverage.copy;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidSet;
import org.geotoolkit.coverage.PyramidalCoverageReference;
import org.geotoolkit.coverage.TileReference;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.image.coverage.CombineIterator;
import org.geotoolkit.parameter.Parameters;
import static org.geotoolkit.parameter.Parameters.*;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.process.coverage.copy.CopyCoverageStoreDescriptor.*;
import org.geotoolkit.process.coverage.reducetodomain.ReduceToDomainDescriptor;
import org.geotoolkit.process.coverage.straighten.StraightenDescriptor;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.geotoolkit.util.ImageIOUtilities;
import org.geotoolkit.feature.type.Name;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ImageCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.TransformException;

/**
 * Copy a {@linkplain CoverageStore coverage store} into another one, that supports
 * a {@linkplain PyramidalModel pyramid model}.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CopyCoverageStoreProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public CopyCoverageStoreProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws ProcessException {
        final CoverageStore inStore  = value(STORE_IN,  inputParameters);
        final CoverageStore outStore = value(STORE_OUT, inputParameters);
        final Boolean       erase    = value(ERASE,     inputParameters);
        final Boolean       reduce   = value(REDUCE_TO_DOMAIN,     inputParameters);

        try {
            final float size = inStore.getNames().size();
            int inc = 0;
            for(Name n : inStore.getNames()){

                fireProgressing("Copying "+n+".", (int)((inc*100f)/size), false);
                final CoverageReference inRef = inStore.getCoverageReference(n);
                final Name name = inRef.getName();
                if (erase) {
                    outStore.delete(name);
                }
                final CoverageReference outRef = outStore.create(name);

                if(inRef instanceof PyramidalCoverageReference && outRef instanceof PyramidalCoverageReference){
                    savePMtoPM((PyramidalCoverageReference)inRef, (PyramidalCoverageReference)outRef);
                }else if(outRef instanceof PyramidalCoverageReference){
                    savePlainToPM(inRef, (PyramidalCoverageReference)outRef, reduce);
                }else{
                    throw new DataStoreException("The given coverage reference is not a pyramidal model, "
                    + "this process only work with this kind of model.");
                }
                inc++;
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
    private void savePMtoPM(final PyramidalCoverageReference inPM, final PyramidalCoverageReference outPM) throws DataStoreException{
        final PyramidSet inPS = inPM.getPyramidSet();

        final List<GridSampleDimension> sampleDimensions = inPM.getSampleDimensions();
        if(sampleDimensions != null){
            outPM.setSampleDimensions(sampleDimensions);
        }

        //count total number of tiles
        long nb = 0;
        for(final Pyramid inPY : inPS.getPyramids()){
            for(final GridMosaic inGM : inPY.getMosaics()){
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
            for(final Pyramid inPY : inPS.getPyramids()){
                final Pyramid outPY = outPM.createPyramid(inPY.getCoordinateReferenceSystem());
                //copy mosaics
                for(final GridMosaic inGM : inPY.getMosaics()){
                    final Dimension gridDimension = inGM.getGridSize();
                    final GridMosaic outGM = outPM.createMosaic(outPY.getId(),
                            gridDimension, inGM.getTileSize(),
                            inGM.getUpperLeftCorner(), inGM.getScale());


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
                            Logger.getLogger(CopyCoverageStoreProcess.class.getName()).log(Level.SEVERE, null, ex);
                            continue;
                        }
                        if(obj == GridMosaic.END_OF_QUEUE){
                            break;
                        }else if(obj == null){
                            continue;
                        }
                        final TileReference inTR = (TileReference) obj;
                        final int x = inTR.getPosition().x;
                        final int y = inTR.getPosition().y;
                        ImageReader inReader = null;
                        try{
                            Object input = inTR.getInput();
                            RenderedImage image;
                            if(input instanceof RenderedImage){
                                image = (RenderedImage) input;
                                if(image.getColorModel() instanceof IndexColorModel){
                                    final BufferedImage bg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                                    bg.createGraphics().drawRenderedImage(image, new AffineTransform());
                                    image = bg;
                                }
                            }else{
                                inReader = inTR.getImageReader();
                                image = inReader.read(inTR.getImageIndex());
                            }
                            final RenderedImage img = image;
                            es.submit(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        outPM.writeTile(outPY.getId(), outGM.getId(), x, y, img);
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
                            ImageIOUtilities.releaseReader(inReader);
                        }
                    }

                }
            }
        }finally{
            es.shutdown();
        }
    }

    /**
     * Save a {@linkplain CoverageReference coverage} into a {@linkplain CoverageStore coverage store}.
     *
     * @param outStore Coverage store in which to copy values.
     * @param inRef Coverage to store.
     * @param erase {@code True} if the data must be erased.
     *
     * @throws DataStoreException
     * @throws TransformException
     */
    private void savePlainToPM(final CoverageReference inRef, final PyramidalCoverageReference outPM, Boolean reduce)
            throws DataStoreException, TransformException, ProcessException {

        if(reduce == null) reduce = Boolean.TRUE;

        final GridCoverageReader reader = inRef.acquireReader();
        final int imageIndex = inRef.getImageIndex();
        final GeneralGridGeometry globalGeom = reader.getGridGeometry(imageIndex);
        final CoordinateReferenceSystem crs = globalGeom.getCoordinateReferenceSystem();
        final CoordinateSystem cs = crs.getCoordinateSystem();

        final Name name = inRef.getName();
        if(crs instanceof ImageCRS){
            //image is not georeferenced, we can't store it.
            fireWarningOccurred("Image "+name+" does not have a CoordinateReferenceSystem, insertion is skipped.", 0, null);
            inRef.recycle(reader);
            return;
        }

        //create sampleDimensions bands
        //TODO remove analyse when CoverageImageReader getSampleDimensions will be fix with min/max values.
        final Map<String, Object> analyse = StatisticOp.analyze(reader, imageIndex);
        final List<GridSampleDimension> sampleDimensions = reader.getSampleDimensions(imageIndex);
        outPM.setSampleDimensions(sampleDimensions);

        final Pyramid pyramid = outPM.createPyramid(crs);

        // Stores additional coordinate system axes, to know how many pyramids should be created
        final List<List<Comparable>> possibilities = new ArrayList<>();

        final int nbdim = cs.getDimension();
        for (int i = 2; i < nbdim; i++) {
            final CoordinateSystemAxis axis = cs.getAxis(i);
            if (axis instanceof DiscreteCoordinateSystemAxis) {
                final DiscreteCoordinateSystemAxis daxis = (DiscreteCoordinateSystemAxis) axis;
                final List<Comparable> values = new ArrayList<>();
                possibilities.add(values);
                final int nbval = daxis.length();
                for (int k = 0; k < nbval; k++) {
                    final Comparable c = daxis.getOrdinateAt(k);
                    values.add(c);
                }
            }
        }

        if (possibilities.isEmpty()) {
            //only a single image to insert
            saveMosaic(outPM, pyramid, reader, imageIndex, null,reduce);

        } else {
            //multiple dimensions to insert
            final GeneralEnvelope env = new GeneralEnvelope(globalGeom.getEnvelope());
            final CombineIterator ite = new CombineIterator(possibilities, env);
            Envelope ce = ite.next();
            do {
                saveMosaic(outPM, pyramid, reader, imageIndex, ce,reduce);
                ce = ite.next();
            } while (ce != null);
        }
        inRef.recycle(reader);

    }

    /**
     * Save a {@linkplain Pyramid mosaic} via its {@linkplain PyramidalModel model}.
     *
     * @param pm The {@linkplain PyramidalModel model} of this pyramid. Must not be {@code null}.
     * @param pyramid {@link Pyramid} to store. Must not be {@code null}.
     * @param reader {@linplain GridCoverageReader reader} of the input coverage. Must not be {@code null}.
     * @param imageIndex Index of the image to read in the reader.
     * @param env {@link Envelope} of the pyramid.
     * @throws DataStoreException
     * @throws TransformException
     */
    private void saveMosaic(final PyramidalCoverageReference pm, final Pyramid pyramid, final GridCoverageReader reader,
            final int imageIndex, Envelope env, boolean reduce) throws DataStoreException, TransformException, ProcessException {
        final GridCoverageReadParam params = new GridCoverageReadParam();
        if (env != null) {
            params.setEnvelope(env);
        }else{
            env = reader.getGridGeometry(imageIndex).getEnvelope();
        }

        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        GridCoverage2D coverage = (GridCoverage2D) reader.read(imageIndex, params);

        //straighten coverage
        final ParameterValueGroup subParams = StraightenDescriptor.INPUT_DESC.createValue();
        Parameters.getOrCreate(StraightenDescriptor.COVERAGE_IN, subParams).setValue(coverage);
        final Process subprocess = StraightenDescriptor.INSTANCE.createProcess(subParams);
        ParameterValueGroup result;
        try{
            result = subprocess.call();
        }catch(ProcessException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        coverage = (GridCoverage2D) Parameters.getOrCreate(StraightenDescriptor.COVERAGE_OUT, result).getValue();

        //reduce to valid domain
        if(reduce){
            final ParameterValueGroup redParams = ReduceToDomainDescriptor.INPUT_DESC.createValue();
            Parameters.getOrCreate(ReduceToDomainDescriptor.COVERAGE_IN, redParams).setValue(coverage);
            final Process redprocess = ReduceToDomainDescriptor.INSTANCE.createProcess(redParams);
            try{
                result = redprocess.call();
                coverage = (GridCoverage2D) Parameters.getOrCreate(StraightenDescriptor.COVERAGE_OUT, result).getValue();
            }catch(ProcessException ex){
                throw new ProcessException(ex.getMessage(), this, ex);
            }

        }

        final GridGeometry2D gridgeom = coverage.getGridGeometry();
        //we know it's an affine transform since we straighten the coverage
        final AffineTransform2D gridToCRS = (AffineTransform2D) gridgeom.getGridToCRS2D(PixelOrientation.UPPER_LEFT);
        final double scale = gridToCRS.getScaleX();

        final RenderedImage img = ((GridCoverage2D) coverage).getRenderedImage();
        final Dimension gridSize = new Dimension(1, 1);
        final Dimension TileSize = new Dimension(img.getWidth(), img.getHeight());
        final Envelope covEnv = coverage.getEnvelope();
        final GeneralDirectPosition upperleft = new GeneralDirectPosition(crs);
        upperleft.setOrdinate(0, covEnv.getMinimum(0));
        upperleft.setOrdinate(1, covEnv.getMaximum(1));
        //envelope seems to lost its additional 2D+ values
        for (int i = 2, n = env.getDimension(); i < n; i++) {
            upperleft.setOrdinate(i, env.getMedian(i));
        }
        final GridMosaic mosaic = pm.createMosaic(pyramid.getId(), gridSize, TileSize, upperleft, scale);
        pm.writeTile(pyramid.getId(), mosaic.getId(), 0, 0, img);
    }
}
