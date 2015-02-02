/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2014, Geomatys
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
package org.geotoolkit.coverage;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.media.jai.PlanarImage;

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;

import org.geotoolkit.coverage.finder.CoverageFinder;
import org.geotoolkit.coverage.finder.DefaultCoverageFinder;
import org.geotoolkit.coverage.finder.StrictlyCoverageFinder;
import org.geotoolkit.coverage.grid.*;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.math.XMath;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.geotoolkit.util.BufferedImageUtilities;
import org.geotoolkit.util.Cancellable;
import org.geotoolkit.util.ImageIOUtilities;

import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;
import org.opengis.util.NameFactory;
import org.opengis.util.NameSpace;

/**
 * GridCoverage reader on top of a Pyramidal object.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PyramidalModelReader extends GridCoverageReader{

    private CoverageReference ref;
    private final CoverageFinder coverageFinder;

    @Deprecated
    public PyramidalModelReader() {
        this.coverageFinder = new DefaultCoverageFinder();
    }

    public PyramidalModelReader(CoverageFinder coverageFinder) {
        this.coverageFinder = coverageFinder;
    }

    @Override
    public CoverageReference getInput() {
        return ref;
    }

    private PyramidalCoverageReference getPyramidalModel(){
        return (PyramidalCoverageReference)ref;
    }

    @Override
    public void setInput(Object input) throws CoverageStoreException {
        if(!(input instanceof CoverageReference) || !(input instanceof PyramidalCoverageReference)){
            throw new CoverageStoreException("Unsupported input type, can only be CoverageReference implementing PyramidalModel.");
        }
        this.ref = (CoverageReference) input;
        super.setInput(input);
    }

    @Override
    public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
        final NameFactory dnf = FactoryFinder.getNameFactory(null);
        final String nameSpace = getInput().getName().getNamespaceURI() != null ? getInput().getName().getNamespaceURI() : "http://geotoolkit.org" ;
        final NameSpace ns = dnf.createNameSpace(dnf.createGenericName(null, nameSpace), null);
        final GenericName gn = dnf.createLocalName(ns, getInput().getName().getLocalPart());
        return Collections.singletonList(gn);
    }

    @Override
    public GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException, CancellationException {
        final PyramidSet set;
        try {
            set = getPyramidalModel().getPyramidSet();
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex);
        }

        final GeneralGridGeometry gridGeom;
        if (!set.getPyramids().isEmpty()) {
            //we use the first pyramid as default
            final Pyramid pyramid = set.getPyramids().iterator().next();

            final List<GridMosaic> mosaics = new ArrayList<>(pyramid.getMosaics());
            Collections.sort(mosaics, CoverageFinder.SCALE_COMPARATOR);

            if (mosaics.isEmpty()) {
                //no mosaics
                gridGeom = new GeneralGridGeometry(null, null, set.getEnvelope());
            } else {
                final CoordinateReferenceSystem crs = pyramid.getCoordinateReferenceSystem();
                final CoordinateSystem cs = crs.getCoordinateSystem();
                final int nbdim = cs.getDimension();

                //use the first mosaic informations, most accurate
                final GridMosaic mosaic = mosaics.get(0);
                final Dimension gridSize = mosaic.getGridSize();
                final Dimension tileSize = mosaic.getTileSize();

                //we expect no rotation
                MathTransform gridToCRS = AbstractGridMosaic.getTileGridToCRS2D(mosaic, new Point(0, 0));
                gridToCRS = PixelTranslation.translate(gridToCRS, PixelInCell.CELL_CORNER,PixelInCell.CELL_CENTER);

                /* Check for additional axis. If we've got a discrete CRS, we will be able to get the axis envelope by
                 * just analysing the axis from one mosaic. Otherwise, we'll have to get upper-left points of each mosaic
                 * to get all possible values.
                 */
                final List<double[]> axisValues = new ArrayList<>();
                final ArrayList<Integer> nonDiscreteIndices = new ArrayList<>();
                for (int i = 2; i < nbdim; i++) {
                    final CoordinateSystemAxis axis = cs.getAxis(i);
                    if (axis instanceof DiscreteCoordinateSystemAxis) {
                        final DiscreteCoordinateSystemAxis dca = (DiscreteCoordinateSystemAxis) axis;
                        final double[] values = new double[dca.length()];
                        for (int k = 0; k < values.length; k++) {
                            final Comparable c = dca.getOrdinateAt(k);
                            if (c instanceof Number) {
                                values[k] = ((Number) c).doubleValue();
                            } else if (c instanceof Date) {
                                values[k] = ((Date) c).getTime();
                            }
                        }
                        axisValues.add(values);
                        if (values.length < 1) {
                            nonDiscreteIndices.add(i);
                        }
                    } else {
                        axisValues.add(new double[0]);
                        nonDiscreteIndices.add(i);
                    }
                }

                /* Not all additional axis are discrete, so we will browse mosaic upper-left corners to define the
                 * "slices" of the pyramid.
                 */
                if (!nonDiscreteIndices.isEmpty()) {
                    HashMap<Integer, TreeSet<Double> > remainingAxis = new HashMap<>(nonDiscreteIndices.size());
                    // initialize with current mosaic information
                    for (Integer indice : nonDiscreteIndices) {
                        // use a tree set to get naturally sorted data without any doublon.
                        final TreeSet<Double> sortedSet = new TreeSet<>();
                        sortedSet.add(mosaic.getUpperLeftCorner().getOrdinate(indice));
                        remainingAxis.put(indice, sortedSet);
                    }
                    // browse other mosaics to get all the slices.
                    TreeSet<Double> sortedSet;
                    DirectPosition upperLeft;
                    for (int mCount = 1 ; mCount < mosaics.size() ; mCount++) {
                        upperLeft = mosaics.get(mCount).getUpperLeftCorner();
                        for (Integer indice : nonDiscreteIndices) {
                            sortedSet = remainingAxis.get(indice);
                            sortedSet.add(upperLeft.getOrdinate(indice));
                        }
                    }

                    for (Map.Entry<Integer, TreeSet<Double> > entry : remainingAxis.entrySet()) {
                        sortedSet = entry.getValue();
                        final double[] values = new double[sortedSet.size()];
                        int i = 0;
                        for (Double d : sortedSet) {
                            values[i++] = d;
                        }
                        // minus 2 because axis value list does not contain values of the two first axis (which are grid axis).
                        axisValues.set(entry.getKey()-2, values);
                    }
                }

                final double[][] discretValues = axisValues.toArray(new double[0][0]);
                final MathTransform gridToCRSds = ReferencingUtilities.toTransform(gridToCRS, discretValues);

                final int[] low = new int[nbdim];
                final int[] high = new int[nbdim];
                low[0] = 0;
                low[1] = 0;
                high[0] = gridSize.width*tileSize.width;
                high[1] = gridSize.height*tileSize.height;

                for(int i=2;i<nbdim;i++){
                    low[i] = 0;
                    high[i] = discretValues[i-2].length;
                }
                final GeneralGridEnvelope ge = new GeneralGridEnvelope(low,high,false);

                gridGeom = new GeneralGridGeometry(ge, PixelInCell.CELL_CENTER, gridToCRSds, crs);
            }

        }else{
            //empty pyramid set
            gridGeom = new GeneralGridGeometry(null, null, set.getEnvelope());
        }

        return gridGeom;
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException, CancellationException {
        try {
            return getPyramidalModel().getSampleDimensions();
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public GridCoverage read(int index, GridCoverageReadParam param) throws CoverageStoreException, CancellationException {
        if(index != 0){
            throw new CoverageStoreException("Invalid Image index.");
        }

        if(param == null){
            param = new GridCoverageReadParam();
        }

        final int[] desBands = param.getDestinationBands();
        final int[] sourceBands = param.getSourceBands();
        if(desBands != null || sourceBands != null){
            throw new CoverageStoreException("Source or destination bands can not be used on pyramidal coverages.");
        }

        CoordinateReferenceSystem crs = param.getCoordinateReferenceSystem();
        Envelope paramEnv = param.getEnvelope();
        double[] resolution = param.getResolution();

        // Build proper envelope and CRS from parameters. If null, they're set from the queried coverage information.
        if (paramEnv == null) {
            paramEnv = getGridGeometry(index).getEnvelope();
        }

        if (crs == null) {
            crs = paramEnv.getCoordinateReferenceSystem();
        } else {
            try {
                paramEnv = CRS.transform(paramEnv, crs);
            } catch (TransformException ex) {
                throw new CoverageStoreException("Could not transform coverage envelope to given crs.", ex);
            }
        }

        if (crs == null) {
            throw new CoverageStoreException("CRS not defined in parameters or input envelope.");
        }

        //estimate resolution if not given
        if(resolution == null){
            //set resolution to infinite, will select the last mosaic level
            resolution = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
        }

        final PyramidalCoverageReference covref = getPyramidalModel();
        
        final PyramidSet pyramidSet;
        try {
            pyramidSet = covref.getPyramidSet();
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex);
        }

        Pyramid pyramid;
        try {
//            pyramid = CoverageUtilities.findPyramid(pyramidSet, crs);
             pyramid = coverageFinder.findPyramid(pyramidSet, crs);
        } catch (FactoryException ex) {
            throw new CoverageStoreException(ex);
        }

        if(pyramid == null){
            //no reliable pyramid
            throw new CoverageStoreException("No pyramid defined.");
        }

        /*
         * We will transform the input envelope to found pyramid CRS.
         */
        final CoordinateReferenceSystem pyramidCRS = pyramid.getCoordinateReferenceSystem();
        final Envelope maxExt = CRS.getEnvelope(pyramidCRS);
        GeneralEnvelope wantedEnv;
        try {
            wantedEnv = new GeneralEnvelope(ReferencingUtilities.transform(paramEnv, pyramidCRS));
            
        } catch (TransformException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }

        //the wanted image resolution
        double wantedResolution = resolution[0];
        final double tolerance = 0.1d;
        
        //-- transform resolution into pyramid crs
        if (!(crs.equals(pyramidCRS))) {
            final int displayBoundX = (int) ((paramEnv.getSpan(0) + wantedResolution - 1) / wantedResolution); //-- to force minimum mosaic resolution
            wantedResolution = wantedEnv.getSpan(0) / displayBoundX;
        }
        //----------------------------------------

        List<GridMosaic> mosaics;
        try {
            mosaics = coverageFinder.findMosaics(pyramid, wantedResolution, tolerance, wantedEnv, 100);
        } catch (FactoryException ex) {
            throw new CoverageStoreException(ex.getMessage(),ex);
        }

        if(mosaics == null ||mosaics.isEmpty()){
            try {
                //no reliable mosaic
                mosaics = coverageFinder.findMosaics(pyramid, wantedResolution, tolerance, wantedEnv, 100);
            } catch (FactoryException ex) {
                Logger.getLogger(PyramidalModelReader.class.getName()).log(Level.SEVERE, null, ex);
            }
            throw new CoverageStoreException("No mosaic can be found with given parameters.");
        }

        //we definitely do not want some NaN values
        for (int i = 0 ; i < wantedEnv.getDimension(); i++) {
            if(Double.isNaN(wantedEnv.getMinimum(i))){ wantedEnv.setRange(i, Double.NEGATIVE_INFINITY, wantedEnv.getMaximum(i));  }
            if(Double.isNaN(wantedEnv.getMaximum(i))){ wantedEnv.setRange(i, wantedEnv.getMinimum(i), Double.POSITIVE_INFINITY);  }
        }

        int xAxis = CoverageUtilities.getMinOrdinate(pyramidCRS);
        // Well... If the pyramid is not defined on an horizontal CRS, all we can do for now is supposing that the first two axis are the grid axis.
        if (xAxis < 0) {
            xAxis = 0;
        }
        int yAxis = xAxis +1;

        //read the data
        final boolean deferred = param.isDeferred();
        if(mosaics.size()==1){
            //read a single slice
            return readSlice(mosaics.get(0), wantedEnv, deferred);
        }else{
            //read a data cube of multiple slices
            return readCube(mosaics, wantedEnv, deferred);
        }
        
    }

    /**
     * Build a coverage from a Grid mosaic definition.
     * 
     * @param mosaic original data grid mosaic
     * @param wantedEnv area to read : must be in mosaic CRS, of a subset of it
     * @param deferred true to delay tile reading, set to true to use a LargeRenderedImage
     * @return GridCoverage
     * @throws CoverageStoreException 
     */
    private GridCoverage readSlice(GridMosaic mosaic, Envelope wantedEnv, boolean deferred) throws CoverageStoreException{
        
        final CoordinateReferenceSystem wantedCRS = wantedEnv.getCoordinateReferenceSystem();
        final Envelope mosEnvelope                = mosaic.getEnvelope();
        
        //-- check CRS conformity
        if (!(CRS.equalsIgnoreMetadata(wantedCRS, mosEnvelope.getCoordinateReferenceSystem())))
            throw new IllegalArgumentException("the wantedEnvelope is not define in same CRS than mosaic. Expected : "
                                                +mosEnvelope.getCoordinateReferenceSystem()+". Found : "+wantedCRS);
        
        int xAxis = CoverageUtilities.getMinOrdinate(wantedCRS);
        // Well... If the pyramid is not defined on an horizontal CRS, all we can do for now is supposing that the first two axis are the grid axis.
        if (xAxis < 0) {
            xAxis = 0;
        }
        int yAxis = xAxis +1;
        
        
        final DirectPosition ul = mosaic.getUpperLeftCorner();
        final double tileMatrixMinX = ul.getOrdinate(xAxis);
        final double tileMatrixMaxY = ul.getOrdinate(yAxis);
        final Dimension gridSize = mosaic.getGridSize();
        final Dimension tileSize = mosaic.getTileSize();
        
        final GeneralEnvelope envelopOfInterest = new GeneralEnvelope(wantedEnv);
        envelopOfInterest.intersect(mosaic.getEnvelope());
        
        final double scale     = mosaic.getScale();
        final double tileSpanX = scale * tileSize.width;
        final double tileSpanY = scale * tileSize.height;
        final int gridWidth    = gridSize.width;
        final int gridHeight   = gridSize.height;

        //find all the tiles we need --------------------------------------
        final double epsilon = 1e-6;
        final double bBoxMinX = envelopOfInterest.getMinimum(xAxis);
        final double bBoxMaxX = envelopOfInterest.getMaximum(xAxis);
        final double bBoxMinY = envelopOfInterest.getMinimum(yAxis);
        final double bBoxMaxY = envelopOfInterest.getMaximum(yAxis);
//        final double epsilon = 1e-6;
//        final double bBoxMinX = wantedEnv.getMinimum(xAxis);
//        final double bBoxMaxX = wantedEnv.getMaximum(xAxis);
//        final double bBoxMinY = wantedEnv.getMinimum(yAxis);
//        final double bBoxMaxY = wantedEnv.getMaximum(yAxis);
//        double tileMinCol = Math.floor( (bBoxMinX - tileMatrixMinX) / tileSpanX + epsilon);
//        double tileMaxCol = Math.floor( (bBoxMaxX - tileMatrixMinX) / tileSpanX - epsilon)+1;
//        double tileMinRow = Math.floor( (tileMatrixMaxY - bBoxMaxY) / tileSpanY + epsilon);
//        double tileMaxRow = Math.floor( (tileMatrixMaxY - bBoxMinY) / tileSpanY - epsilon)+1;
        int tileMinCol = (int) ((bBoxMinX - tileMatrixMinX) / tileSpanX);
//        int tileMaxCol = (int) ((bBoxMaxX - tileMatrixMinX + tileSpanX - 1) / tileSpanX);
        int tileMaxCol = (int) StrictMath.ceil((bBoxMaxX - tileMatrixMinX) / tileSpanX);
        int tileMinRow = (int) ((tileMatrixMaxY - bBoxMaxY) / tileSpanY);
        int tileMaxRow = (int) StrictMath.ceil((tileMatrixMaxY - bBoxMinY) / tileSpanY);
        
        double tileMinCol2 = Math.floor( (bBoxMinX - tileMatrixMinX) / tileSpanX + epsilon);
        double tileMaxCol2 = Math.floor( (bBoxMaxX - tileMatrixMinX) / tileSpanX - epsilon)+1;
        double tileMinRow2 = Math.floor( (tileMatrixMaxY - bBoxMaxY) / tileSpanY + epsilon);
        double tileMaxRow2 = Math.floor( (tileMatrixMaxY - bBoxMinY) / tileSpanY - epsilon)+1;
        System.out.println("before tileMinCol : "+tileMinCol2+" next : "+tileMinCol);
        System.out.println("before tileMaxCol : "+tileMaxCol2+" next : "+tileMaxCol);
        System.out.println("before tileMinRow : "+tileMinRow2+" next : "+tileMinRow);
        System.out.println("before tileMaxRow : "+tileMaxRow2+" next : "+tileMaxRow);
//
//        //ensure we dont go out of the grid
//        tileMinCol = XMath.clamp(tileMinCol, 0, gridWidth);
//        tileMaxCol = XMath.clamp(tileMaxCol, 0, gridWidth);
//        tileMinRow = XMath.clamp(tileMinRow, 0, gridHeight);
//        tileMaxRow = XMath.clamp(tileMaxRow, 0, gridHeight);

        RenderedImage image = null;
        if(deferred){
            //delay reading tiles
            image = new GridMosaicRenderedImage(mosaic, new Rectangle(
                    (int)tileMinCol, (int)tileMinRow, (int)(tileMaxCol-tileMinCol), (int)(tileMaxRow-tileMinRow)));
        }else{
            //tiles to render, coordinate in grid -> image offset
            final Collection<Point> candidates = new ArrayList<>();

            for(int tileCol=(int)tileMinCol; tileCol<tileMaxCol; tileCol++){
                for(int tileRow=(int)tileMinRow; tileRow<tileMaxRow; tileRow++){
                    if(mosaic.isMissing(tileCol, tileRow)){
                        //tile not available
                        continue;
                    }candidates.add(new Point(tileCol, tileRow));
                }
            }

            if(candidates.isEmpty()){
                //no tiles intersect
                throw new DisjointCoverageDomainException("Requested envelope do not intersect tiles.");
            }

            //aggregation ----------------------------------------------------------
            final Map hints = Collections.EMPTY_MAP;


            final BlockingQueue<Object> queue;
            try {
                queue = mosaic.getTiles(candidates, hints);
            } catch (DataStoreException ex) {
                throw new CoverageStoreException(ex.getMessage(),ex);
            }

            int n = 0;
            
            while(true){
                Object obj = null;
                try {
                    obj = queue.poll(100, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ex) {
                    //not important
                }

                if(abortRequested){
                    if(queue instanceof Cancellable){
                        ((Cancellable)queue).cancel();
                    }
                    break;
                }

                if(obj == GridMosaic.END_OF_QUEUE){
                    break;
                }

                if(obj instanceof TileReference){
                    final TileReference tile = (TileReference)obj;
                    final Point position = tile.getPosition();
                    final Point offset = new Point(
                            (int)(position.x-tileMinCol)*tileSize.width,
                            (int)(position.y-tileMinRow)*tileSize.height);

                    final Object input = tile.getInput();
                    RenderedImage tileImage = null;
                    if(input instanceof RenderedImage){
                        tileImage = (RenderedImage) input;
                    }else{
                        ImageReader reader = null;
                        try {
                            reader = tile.getImageReader();
                            tileImage = reader.read(tile.getImageIndex());
                        } catch (IOException ex) {
                            throw new CoverageStoreException(ex.getMessage(),ex);
                        }finally{
                            ImageIOUtilities.releaseReader(reader);
                        }
                    }

//                    if (tileImage != null)
//                    try {
//                        ImageIO.write(tileImage, "geotiff", new File("/home/rmarechal/Documents/image/test"+(n++)+".tiff"));
//                    } catch (IOException ex) {
//                        Logger.getLogger(PyramidalModelReader.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                    if(image == null){
                        image = BufferedImageUtilities.createImage(
                                (int)(tileMaxCol-tileMinCol)*tileSize.width, 
                                (int)(tileMaxRow-tileMinRow)*tileSize.height, tileImage);
                    }

                    ((BufferedImage)image).getRaster().setDataElements(offset.x, offset.y, tileImage.getData());
                    //we consider all images have the same data model
                    //g2d.drawRenderedImage(tileImage, new AffineTransform(1, 0, 0, 1, offset.x, offset.y));
                }
            }

            if(image == null){
                image = new BufferedImage(
                    (int)(tileMaxCol-tileMinCol)*tileSize.width,
                    (int)(tileMaxRow-tileMinRow)*tileSize.height,
                    BufferedImage.TYPE_INT_ARGB);
            }
        }

        //build the coverage ---------------------------------------------------
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName(ref.getName().getLocalPart());
        final List<GridSampleDimension> dimensions = getSampleDimensions(ref.getImageIndex());
        if (dimensions != null) {
            gcb.setSampleDimensions(dimensions.toArray(new GridSampleDimension[dimensions.size()]));
        }

        final GridEnvelope ge = new GeneralGridEnvelope(image, wantedCRS.getCoordinateSystem().getDimension());
        final MathTransform gtc = AbstractGridMosaic.getTileGridToCRSND(mosaic,
                new Point((int)tileMinCol,(int)tileMinRow),wantedCRS.getCoordinateSystem().getDimension());
        final GridGeometry2D gridgeo = new GridGeometry2D(ge, PixelOrientation.UPPER_LEFT, gtc, wantedCRS, null);
        gcb.setGridGeometry(gridgeo);
        gcb.setRenderedImage(image);

        return gcb.build();
    }

    private GridCoverage readCube(List<GridMosaic> mosaics, Envelope wantedEnv, boolean deferred) throws CoverageStoreException{
        //regroup mosaic by hierarchy cubes
        final TreeMap groups = new TreeMap();
        for(GridMosaic mosaic : mosaics){
            appendSlice(groups, mosaic);
        }

        int dim = wantedEnv.getDimension();
        //rebuild coverage
        return rebuildCoverage(groups, wantedEnv, deferred, dim-1);
    }
    
    /**
     * Organize mosaics in groups which are on the same dimension slice.
     * 
     * @param rootGroup
     * @param mosaic
     * @throws CoverageStoreException 
     */
    private void appendSlice(final TreeMap<Double,Object> rootGroup, GridMosaic mosaic) throws CoverageStoreException{
        final DirectPosition upperLeft = mosaic.getUpperLeftCorner();
        TreeMap<Double,Object> groups = rootGroup;
        
        //regroup them by inverse axis order so we can rebuild stacks always adding dimensions at the end
        for(int i=upperLeft.getDimension()-1; i>=2; i--){
            final double d = upperLeft.getOrdinate(i);
            final Object obj = groups.get(d);
            if(obj==null){
                groups.put(d, mosaic);
                break;
            }else if(obj instanceof GridMosaic){
                //already another mosaic for the dimension slice
                //replace the coverage by a map and re-add them.
                groups.put(d, new TreeMap());
                appendSlice(rootGroup, (GridMosaic)obj);
                appendSlice(rootGroup, mosaic);
                break;
            }else if(obj instanceof TreeMap){
                groups = (TreeMap) obj;
            }else{
                throw new CoverageStoreException("Found an object which is not a Coverage or a Map group, should not happen : "+obj);
            }
        }
    }

    /**
     *
     * @param groups
     * @param wantedEnv
     * @param deferred
     * @param axisIndex
     * @return GridCoverage
     * @throws CoverageStoreException
     */
    private GridCoverage rebuildCoverage(TreeMap<Double,Object> groups, Envelope wantedEnv, boolean deferred, int axisIndex)
            throws CoverageStoreException {

        final CoordinateReferenceSystem crs = wantedEnv.getCoordinateReferenceSystem();
        final CoordinateSystem cs = crs.getCoordinateSystem();
        int nbDim = cs.getDimension();

        final List<GridCoverageStack.Element> elements = new ArrayList<>();
        final List<Entry<Double,Object>> entries = new ArrayList<>(groups.entrySet());

        for(int k=0,kn=entries.size();k<kn;k++){
            final Entry<Double,Object> entry = entries.get(k);

            final Double z = entry.getKey();
            final Object obj = entry.getValue();

            final org.geotoolkit.geometry.GeneralEnvelope sliceEnvelop = new org.geotoolkit.geometry.GeneralEnvelope(crs);
            for (int i = 0; i < nbDim; i++) {
                if (i == axisIndex) {
                    sliceEnvelop.setRange(i, z, z);
                } else {
                    sliceEnvelop.setRange(i, wantedEnv.getMinimum(i), wantedEnv.getMaximum(i));
                }
            }

            final GridCoverage subCoverage;
            if(obj instanceof GridMosaic){
                subCoverage = readSlice((GridMosaic)obj, sliceEnvelop, deferred);
            }else if(obj instanceof TreeMap){
                subCoverage = rebuildCoverage((TreeMap)obj, sliceEnvelop, deferred, axisIndex-1);
            }else{
                throw new CoverageStoreException("Found an object which is not a Coverage or a Map group, should not happen : "+obj);
            }

            //calculate the range
            double min;
            double max;
            if(k==0){
                if(kn==1){
                    //a single element, use a range of 1
                    min = z - 0.5;
                    max = z + 0.5;
                }else{
                    final double nextD = entries.get(k+1).getKey();
                    final double diff = (nextD - z) / 2.0;
                    min = z-diff;
                    max = z+diff;
                }
            }else if(k==kn-1){
                final double previousD = entries.get(k-1).getKey();
                final double diff = (z - previousD) / 2.0;
                min = z-diff;
                max = z+diff;
            }else{
                final double prevD = entries.get(k-1).getKey();
                final double nextD = entries.get(k+1).getKey();
                min = z - (z - prevD) / 2.0;
                max = z + (nextD - z) / 2.0;
            }

            elements.add(new CoverageStack.Adapter(subCoverage, NumberRange.create(min, true, max, false), z));
        }

        try {
            return new GridCoverageStack("HyperCube"+ nbDim +"D", crs, elements, axisIndex);
        } catch (IOException ex) {
            throw new CoverageStoreException(ex);
        } catch (TransformException ex) {
            throw new CoverageStoreException(ex);
        } catch (FactoryException ex) {
            throw new CoverageStoreException(ex);
        }
    }
    
    
}
