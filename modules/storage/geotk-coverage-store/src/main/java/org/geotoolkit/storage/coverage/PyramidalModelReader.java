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
package org.geotoolkit.storage.coverage;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import org.apache.sis.geometry.Envelopes;

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;

import org.geotoolkit.coverage.CoverageStack;
import org.geotoolkit.coverage.GridCoverageStack;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.finder.CoverageFinder;
import org.geotoolkit.coverage.finder.DefaultCoverageFinder;
import org.geotoolkit.coverage.grid.*;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.type.NamesExt;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.util.Cancellable;
import org.geotoolkit.util.ImageIOUtilities;

import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
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

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.storage.coverage");

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
        final String nss = NamesExt.getNamespace(getInput().getName());
        final String nameSpace = nss != null ? nss : "http://geotoolkit.org" ;
        final NameSpace ns = dnf.createNameSpace(dnf.createGenericName(null, nameSpace), null);
        final GenericName gn = dnf.createLocalName(ns, getInput().getName().tip().toString());
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

            //-- we use the first pyramid as default
            final Pyramid pyramid = set.getPyramids().iterator().next();

            final List<GridMosaic> mosaics = new ArrayList<>(pyramid.getMosaics());
            Collections.sort(mosaics, CoverageFinder.SCALE_COMPARATOR);

            if (mosaics.isEmpty()) {
                //no mosaics
                gridGeom = new GeneralGridGeometry(null, null, set.getEnvelope());
            } else {

                final CoordinateReferenceSystem crs = pyramid.getCoordinateReferenceSystem();
                final CoordinateSystem cs           = crs.getCoordinateSystem();
                final int nbdim                     = cs.getDimension();

                //-- use the first mosaic informations, most accurate
                final GridMosaic mosaic  = mosaics.get(0);
                final Dimension gridSize = mosaic.getGridSize();
                final Dimension tileSize = mosaic.getTileSize();

                //-- we expect no rotation
                final MathTransform gridToCRS = AbstractGridMosaic.getTileGridToCRS2D(mosaic, new Point(0, 0));

                //-- get all mosaics with same scale
                final double scal = mosaic.getScale();
                for (int m = mosaics.size() - 1; m >= 0; m--) {
                    if (mosaics.get(m).getScale() != scal) mosaics.remove(m);
                }

                final int minordi = CRSUtilities.firstHorizontalAxis(crs);

                final Map<Integer , double[]> multiAxisValues = new HashMap<>();
                for (int i = 0; i < cs.getDimension(); i++) {
                    if (i != minordi && i!= minordi + 1) {
                        //-- pass by TreeSet to avoid duplicate
                        final SortedSet<Double> axisValues = new TreeSet();
                        for (final GridMosaic gridMos : mosaics) {
                           axisValues.add(gridMos.getUpperLeftCorner().getOrdinate(i));
                        }
                        //-- convert Double[] -> double[]
                        final double[] axVals = new double[axisValues.size()];
                        int a = 0;
                        for (Double d : axisValues) axVals[a++] = d;
                        Arrays.sort(axVals, 0, axVals.length);
                        multiAxisValues.put(i, axVals);
                    }
                }

                final MathTransform gridToCRSds = ReferencingUtilities.toTransform(minordi, gridToCRS, multiAxisValues, cs.getDimension());

                final int[] low   = new int[nbdim];
                final int[] high  = new int[nbdim];

                for (int i = 0; i < cs.getDimension(); i++) {
                    low[i] = 0; //-- on each dimension low begin at 0
                    if (i == minordi) {
                        high[i] = gridSize.width  * tileSize.width; //-- X horizontal 2D part
                    } else if (i == minordi + 1) {
                        high[i] = gridSize.height * tileSize.height; //-- Y horizontal 2D part
                    } else if (i != minordi && i != minordi + 1) {
                        high[i] = multiAxisValues.get(i).length; //-- other dimension grid high value = discret axis values number.
                    } else {
                        //-- should never append
                        throw new IllegalStateException("PyramidalModelReader.getGridGeometry() : problem during grid creation.");
                    }
                }

                final GeneralGridEnvelope ge = new GeneralGridEnvelope(low, high, false);
                gridGeom = new GeneralGridGeometry(ge, PixelInCell.CELL_CORNER, gridToCRSds, crs);
            }
        } else {
            //-- empty pyramid set
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
        if (index != 0)
            throw new CoverageStoreException("Invalid Image index.");

        if (param == null)
            param = new GridCoverageReadParam();

        final int[] desBands    = param.getDestinationBands();
        final int[] sourceBands = param.getSourceBands();

        if (desBands != null || sourceBands != null)
            throw new CoverageStoreException("Source or destination bands can not be used on pyramidal coverages.");

        CoordinateReferenceSystem crs = param.getCoordinateReferenceSystem();
        Envelope paramEnv = param.getEnvelope();
        double[] resolution = param.getResolution();

        // Build proper envelope and CRS from parameters. If null, they're set from the queried coverage information.
        if (paramEnv == null)
            paramEnv = getGridGeometry(index).getEnvelope();

        if (crs == null) {
            crs = paramEnv.getCoordinateReferenceSystem();
        } else {
            try {
                paramEnv = CRS.transform(paramEnv, crs);
            } catch (TransformException ex) {
                throw new CoverageStoreException("Could not transform coverage envelope to given crs.", ex);
            }
        }

        if (crs == null)
            throw new CoverageStoreException("CRS not defined in parameters or input envelope.");

        //-- estimate resolution if not given
        if (resolution == null)
            //-- set resolution to infinite, will select the last mosaic level
            resolution = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};


        final PyramidalCoverageReference covref = getPyramidalModel();

        final PyramidSet pyramidSet;
        try {
            pyramidSet = covref.getPyramidSet();
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex);
        }

        Pyramid pyramid;
        try {
             pyramid = coverageFinder.findPyramid(pyramidSet, crs);
        } catch (FactoryException ex) {
            throw new CoverageStoreException(ex);
        }

        //-- no reliable pyramid
        if (pyramid == null)
            throw new CoverageStoreException("No pyramid defined.");

        /*
         * We will transform the input envelope to found pyramid CRS.
         */
        final CoordinateReferenceSystem pyramidCRS = pyramid.getCoordinateReferenceSystem();
        GeneralEnvelope wantedEnv;
        try {
            wantedEnv = new GeneralEnvelope(ReferencingUtilities.transform(paramEnv, pyramidCRS));
        } catch (TransformException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }

        //the wanted image resolution
        double wantedResolution = resolution[0];
        final double tolerance  = 0.1d;

        //-- transform resolution into pyramid crs
        if (!(crs.equals(pyramidCRS))) {
            final int displayBoundX = (int) ((paramEnv.getSpan(0) + wantedResolution - 1) / wantedResolution); //-- to force minimum mosaic resolution
            wantedResolution = wantedEnv.getSpan(0) / displayBoundX;
        }
        //----------------------------------------

        List<GridMosaic> mosaics;
        try {
            mosaics = coverageFinder.findMosaics(pyramid, wantedResolution, tolerance, wantedEnv);
        } catch(MismatchedDimensionException ex) {
            throw new CoverageStoreException(ex.getMessage(),ex);
        }

        if (mosaics.isEmpty())
            throw new IllegalStateException("Unexpected comportement an error should be precedently occurs.");

        //-- we definitely do not want some NaN values
        for (int i = 0 ; i < wantedEnv.getDimension(); i++) {

            if (Double.isNaN(wantedEnv.getMinimum(i)))
                wantedEnv.setRange(i, Double.NEGATIVE_INFINITY, wantedEnv.getMaximum(i));

            if (Double.isNaN(wantedEnv.getMaximum(i)))
                wantedEnv.setRange(i, wantedEnv.getMinimum(i),  Double.POSITIVE_INFINITY);
        }

        //-- read the data
        final boolean deferred = param.isDeferred();
         try {
            if (mosaics.size() == 1) {

                //-- read a single slice
                return readSlice(mosaics.get(0), wantedEnv, deferred);

            } else {
                //-- read a data cube of multiple slices
                return readCube(mosaics, wantedEnv, deferred);
            }
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex); //-- to be in accordance with reader method interface signature
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
    private GridCoverage readSlice(GridMosaic mosaic, Envelope wantedEnv, boolean deferred) throws CoverageStoreException, DataStoreException {

        final CoordinateReferenceSystem wantedCRS = wantedEnv.getCoordinateReferenceSystem();
        final Envelope mosEnvelope                = mosaic.getEnvelope();

        //-- check CRS conformity
        if (!(CRS.equalsIgnoreMetadata(wantedCRS, mosEnvelope.getCoordinateReferenceSystem())))
            throw new IllegalArgumentException("the wantedEnvelope is not define in same CRS than mosaic. Expected : "
                                                +mosEnvelope.getCoordinateReferenceSystem()+". Found : "+wantedCRS);

        final DirectPosition upperLeft = mosaic.getUpperLeftCorner();
        assert CRS.equalsIgnoreMetadata(upperLeft.getCoordinateReferenceSystem(), wantedCRS);

        final ViewType currentViewType = getPyramidalModel().getPackMode();

        final int xAxis = CRSUtilities.firstHorizontalAxis(wantedCRS);
        final int yAxis = xAxis +1;

        //-- convert working into 2D space
        final CoordinateReferenceSystem mosCRS2D;
        final GeneralEnvelope wantedEnv2D, mosEnv2D;
        try {
            mosCRS2D = CRSUtilities.getCRS2D(wantedCRS);
            wantedEnv2D = GeneralEnvelope.castOrCopy(Envelopes.transform(wantedEnv,   mosCRS2D));
            mosEnv2D    = GeneralEnvelope.castOrCopy(Envelopes.transform(mosEnvelope, mosCRS2D));
        } catch(Exception ex) {
            throw new CoverageStoreException(ex);
        }

        //-- define appropriate gridToCRS
        final Dimension gridSize = mosaic.getGridSize();
        final Dimension tileSize = mosaic.getTileSize();
        final double sx          = mosEnv2D.getSpan(0) / (gridSize.width  * tileSize.width);
        final double sy          = mosEnv2D.getSpan(1) / (gridSize.height * tileSize.height);
        final double offsetX     = upperLeft.getOrdinate(xAxis);
        final double offsetY     = upperLeft.getOrdinate(yAxis);

        final AffineTransform2D gridToCrs2D = new AffineTransform2D(sx, 0, 0, -sy, offsetX, offsetY);

        final GeneralEnvelope envelopOfInterest2D = new GeneralEnvelope(wantedEnv2D);
        envelopOfInterest2D.intersect(mosEnv2D);

        final Envelope gridOfInterest;
        try {
            gridOfInterest = Envelopes.transform(gridToCrs2D.inverse(), envelopOfInterest2D);
        } catch (Exception ex) {
            throw new CoverageStoreException(ex);
        }

        final long bBoxMinX = StrictMath.round(gridOfInterest.getMinimum(0));
        final long bBoxMaxX = StrictMath.round(gridOfInterest.getMaximum(0));
        final long bBoxMinY = StrictMath.round(gridOfInterest.getMinimum(1));
        final long bBoxMaxY = StrictMath.round(gridOfInterest.getMaximum(1));

        final int tileMinCol = (int) (bBoxMinX / tileSize.width);
        final int tileMaxCol = (int) StrictMath.ceil(bBoxMaxX / (double) tileSize.width);
        assert tileMaxCol == ((int)((bBoxMaxX + tileSize.width - 1) / (double) tileSize.width)) : "readSlice() : unexpected comportement maximum column index.";

        final int tileMinRow = (int) (bBoxMinY / tileSize.height);
        final int tileMaxRow = (int) StrictMath.ceil(bBoxMaxY / (double) tileSize.height);
        assert tileMaxRow == ((int)((bBoxMaxY + tileSize.height - 1) / (double) tileSize.height)) : "readSlice() : unexpected comportement maximum row index.";

        //-- debug helper
        {
//        System.out.println("index X mosaic : 0 -> "+gridSize.width);
//        System.out.println("index Y mosaic : 0 -> "+gridSize.height);
//        System.out.println("mosaic grid = (0, 0) --> ("+(gridSize.width*tileSize.width)+", "+(gridSize.height * tileSize.height)+")");
//
//        System.out.println("requested index X : "+tileMinCol+" -> "+tileMaxCol);
//        System.out.println("requested index Y : "+tileMinRow+" -> "+tileMaxRow);
//        System.out.println("gridOfInterest = "+gridOfInterest.toString());
        }

        RenderedImage image = null;
        if (deferred) {
            //delay reading tiles
            image = new GridMosaicRenderedImage(mosaic, new Rectangle(
                    (int)tileMinCol, (int)tileMinRow, (int)(tileMaxCol-tileMinCol), (int)(tileMaxRow-tileMinRow)));
        } else {
            //tiles to render, coordinate in grid -> image offset
            final Collection<Point> candidates = new ArrayList<>();

            for (int tileCol = (int) tileMinCol; tileCol < tileMaxCol; tileCol++) {

                for(int tileRow = (int) tileMinRow; tileRow < tileMaxRow; tileRow++) {

                    if (mosaic.isMissing(tileCol, tileRow)) continue;//--tile not available

                    candidates.add(new Point(tileCol, tileRow));
                }
            }

            if (candidates.isEmpty()) {
                //no tiles intersect
                LOGGER.log(Level.FINE, "Following Requested envelope : "
                        +wantedEnv
                        + "\n do not intersect data define by following data Envelope."
                        +mosaic.getEnvelope());
                return null;
            }

            //--debug helper
            {
//            System.out.println("retained mosaics : ");
//            for (Point candidate : candidates) {
//                System.out.println("mosaic : ("+candidate.x+", "+candidate.y+")");
//            }
            }

            //aggregation ----------------------------------------------------------
            final Map hints = Collections.EMPTY_MAP;


            final BlockingQueue<Object> queue;
            try {
                queue = mosaic.getTiles(candidates, hints);
            } catch (DataStoreException ex) {
                throw new CoverageStoreException(ex.getMessage(),ex);
            }
            int i = 0;
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
                            reader    = tile.getImageReader();
                            tileImage = reader.read(tile.getImageIndex());
                        } catch (IOException ex) {
                            throw new CoverageStoreException(ex.getMessage(),ex);
                        } finally {
                            ImageIOUtilities.releaseReader(reader);
                        }
                    }

                    //-- if photographic transform ARGB
                    if (ViewType.PHOTOGRAPHIC.equals(currentViewType)) {
                        //-- transform argb
                        tileImage = forceAlpha(tileImage);
                    }


                    if (image == null) {
                        ColorModel cm = null;
                        if (ref instanceof PyramidalCoverageReference) {
                            final PyramidalCoverageReference pyramRef = (PyramidalCoverageReference) ref;
                            cm = pyramRef.getColorModel();
                        }
                        if(cm==null) {
                            cm = tileImage.getColorModel();
                        }
                        image = new BufferedImage(cm,
                                cm.createCompatibleWritableRaster((int)(tileMaxCol-tileMinCol)*tileSize.width,
                                                                                       (int)(tileMaxRow-tileMinRow)*tileSize.height),
                                cm.isAlphaPremultiplied(), new Hashtable<>());
                    }
                    //-- write current read tile into destination image.
                    final Rectangle tileBound = new Rectangle(offset.x, offset.y, tileImage.getWidth(), tileImage.getHeight());
                    final PixelIterator destPix = PixelIteratorFactory.createDefaultWriteableIterator((BufferedImage)image, (BufferedImage)image, tileBound);
                    final PixelIterator tilePix = PixelIteratorFactory.createDefaultIterator(tileImage);
                    while(destPix.next()) {
                        tilePix.next();
                        destPix.setSampleDouble(tilePix.getSampleDouble());
                    }
                    assert !tilePix.next();
                }
            }

            if(image == null){
                image = new BufferedImage(
                    (int)(tileMaxCol-tileMinCol)*tileSize.width,
                    (int)(tileMaxRow-tileMinRow)*tileSize.height,
                    BufferedImage.TYPE_INT_ARGB);
            }
        }
////
////        //-- if DatabufferType of image is float or Double we must change the Color Space
////        //-- to bound sample value between 0 and 1 to avoid java 2d rendering problem
////        image = ImageUtils.replaceFloatingColorModel(image);

        //build the coverage ---------------------------------------------------
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName(ref.getName().tip().toString());
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

     /**
     * Add an alpha band to the image and remove any black border if asked.
     *
     * TODO, this could be done more efficiently by adding an ImageLayout hints
     * when doing the coverage reprojection. but hints can not be passed currently.
     */
    private static RenderedImage forceAlpha(RenderedImage img) {
        if (!img.getColorModel().hasAlpha()) {
            //Add alpha channel
            final BufferedImage buffer = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            buffer.createGraphics().drawRenderedImage(img, new AffineTransform());
            img = buffer;
        }
        return img;
    }

    private GridCoverage readCube(List<GridMosaic> mosaics, Envelope wantedEnv, boolean deferred) throws CoverageStoreException, DataStoreException{
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
            throws CoverageStoreException, DataStoreException {

        final CoordinateReferenceSystem crs = wantedEnv.getCoordinateReferenceSystem();
        final CoordinateSystem cs = crs.getCoordinateSystem();
        int nbDim = cs.getDimension();

        final List<CoverageStack.Element> elements = new ArrayList<>();
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
