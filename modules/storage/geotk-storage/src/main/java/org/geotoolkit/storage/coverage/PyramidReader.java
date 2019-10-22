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
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.storage.coverage.finder.CoverageFinder;
import org.geotoolkit.storage.coverage.finder.DefaultCoverageFinder;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridCoverageStack;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.MultiResolutionModel;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Pyramids;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
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

/**
 * GridCoverage reader on top of a Pyramidal object.
 *
 * @author Johann Sorel (Geomatys)
 * @param <T>
 * @param <GridCoverageResource>
 * @module
 */
public class PyramidReader <T extends MultiResolutionResource & org.apache.sis.storage.GridCoverageResource> {

    private final T ref;
    private final CoverageFinder coverageFinder = new DefaultCoverageFinder();

    protected static final Logger LOGGER = Logging.getLogger("org.geotoolkit.storage.coverage");

    public PyramidReader(T ref) {
        this.ref = ref;
    }

    public GridGeometry getGridGeometry() throws CoverageStoreException, CancellationException {
        final Collection<? extends MultiResolutionModel> models;

        try {
            models = ref.getModels();
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex);
        }

        //search for a pyramid
        //-- we use the first pyramid as default
        org.geotoolkit.storage.multires.Pyramid pyramid = null;
        for (MultiResolutionModel model : models) {
            if (model instanceof org.geotoolkit.storage.multires.Pyramid) {
                pyramid = (org.geotoolkit.storage.multires.Pyramid) model;
                break;
            }
        }

        if (pyramid == null) {
            //-- empty pyramid set
            return GridGeometry.UNDEFINED;
        }

        final List<Mosaic> mosaics = new ArrayList<>(pyramid.getMosaics());
        if (mosaics.isEmpty()) {
            //no mosaics
            return GridGeometry.UNDEFINED;
        }

        Collections.sort(mosaics, CoverageFinder.SCALE_COMPARATOR);
        final GridGeometry gridGeom;

        final CoordinateReferenceSystem crs = pyramid.getCoordinateReferenceSystem();
        final CoordinateSystem cs           = crs.getCoordinateSystem();
        final int nbdim                     = cs.getDimension();

        //-- use the first mosaic informations, most accurate
        final Mosaic mosaic  = mosaics.get(0);

        //-- we expect no rotation
        final MathTransform gridToCRS = Pyramids.getTileGridToCRS2D(mosaic, new Point(0, 0));

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
                for (final Mosaic gridMos : mosaics) {
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

        //-- size of internal pixel data recovered
        final GridExtent dataSize = mosaic.getDataExtent();

        final long[] high  = new long[nbdim];

        for (int i = 0; i < cs.getDimension(); i++) {
            if (i == minordi) {
                high[i] = dataSize.getSize(0); //-- X horizontal 2D part
            } else if (i == minordi + 1) {
                high[i] = dataSize.getSize(1); //-- Y horizontal 2D part
            } else if (i != minordi && i != minordi + 1) {
                high[i] = multiAxisValues.get(i).length; //-- other dimension grid high value = discret axis values number.
            } else {
                //-- should never append
                throw new IllegalStateException("PyramidalModelReader.getGridGeometry() : problem during grid creation.");
            }
        }

        final GridExtent ge = new GridExtent(null, null, high, false);

        // TODO : we should do the transform like this but the is an issue further with derivate transforms
        //convert to center
        //gridToCRS = PixelTranslation.translate(gridToCRS, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);
        //final MathTransform gridToCRSds = ReferencingUtilities.toTransform(minordi, gridToCRS, multiAxisValues, cs.getDimension());
        //gridGeom = new GridGeometry(ge, PixelInCell.CELL_CENTER, gridToCRSds, crs);

        final MathTransform gridToCRSds = ReferencingUtilities.toTransform(minordi, gridToCRS, multiAxisValues, cs.getDimension());
        gridGeom = new GridGeometry(ge, PixelInCell.CELL_CORNER, gridToCRSds, crs);

        return gridGeom;
    }

    public List<SampleDimension> getSampleDimensions() throws CoverageStoreException, CancellationException {
        try {
            return ref.getSampleDimensions();
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }
    }

    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        GridGeometry canvas = getGridGeometry();
        if (domain != null) {
            canvas = canvas.derive().subgrid(domain).build();
        }

        if (range != null)
            LOGGER.log(Level.FINE, "Source or destination bands can not be used on pyramidal coverages."
                                    + " Continue Coverage reading without sources and destinations bands interpretations.");

        Envelope paramEnv = canvas.getEnvelope();
        double[] resolution = canvas.getResolution(true);

        CoordinateReferenceSystem crs = canvas.getCoordinateReferenceSystem();

        //-- estimate resolution if not given
        if (resolution == null)
            //-- set resolution to infinite, will select the last mosaic level
            resolution = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};


        org.geotoolkit.storage.multires.Pyramid pyramid;
        try {
             pyramid = coverageFinder.findPyramid(ref, crs);
        } catch (FactoryException | DataStoreException ex) {
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

        List<Mosaic> mosaics;
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

        //-- features the data
        final boolean deferred = false; //param.isDeferred();
         try {
            if (mosaics.size() == 1) {

                //-- features a single slice
                return readSlice(mosaics.get(0), wantedEnv, deferred);

            } else {
                //-- features a data cube of multiple slices
                return readCube(mosaics, wantedEnv, deferred);
            }
        } catch (CoverageStoreException ex) {
            throw ex;
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex); //-- to be in accordance with reader method interface signature
        }
    }

    /**
     * Build a coverage from a Grid mosaic definition.
     *
     * @param mosaic original data grid mosaic
     * @param wantedEnv area to features : must be in mosaic CRS, of a features of it
     * @param deferred true to delay tile reading, set to true to use a LargeRenderedImage
     * @return GridCoverage
     * @throws CoverageStoreException
     */
    private GridCoverage readSlice(Mosaic mosaic, Envelope wantedEnv, boolean deferred) throws CoverageStoreException, DataStoreException {

        final Rectangle tilesInEnvelope = Pyramids.getTilesInEnvelope(mosaic, wantedEnv);
        final int tileMinCol = tilesInEnvelope.x;
        final int tileMaxCol = tilesInEnvelope.x + tilesInEnvelope.width;
        final int tileMinRow = tilesInEnvelope.y;
        final int tileMaxRow = tilesInEnvelope.y + tilesInEnvelope.height;

        final CoordinateReferenceSystem wantedCRS = wantedEnv.getCoordinateReferenceSystem();

        //-- define appropriate gridToCRS
        final Dimension tileSize = mosaic.getTileSize();

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
            image = new GridMosaicRenderedImage(mosaic, tilesInEnvelope);
        } else {
            //tiles to render, coordinate in grid -> image offset
            final Collection<Point> candidates = new ArrayList<>();

            for (int tileCol = (int) tileMinCol; tileCol < tileMaxCol; tileCol++) {
                for(int tileRow = (int) tileMinRow; tileRow < tileMaxRow; tileRow++) {
                    //do not check missing tiles, the query pool will exclude them
                    //if (mosaic.isMissing(tileCol, tileRow)) continue;//--tile not available
                    candidates.add(new Point(tileCol, tileRow));
                }
            }

            if (candidates.isEmpty()) {
                //no tiles intersect
                throw new DisjointCoverageDomainException("Following Requested envelope : "
                        +wantedEnv
                        + "\n do not intersect any tiles data in mosaic Envelope : "
                        +mosaic.getEnvelope());
            }

            //--debug helper
            {
//            System.out.println("retained mosaics : ");
//            for (Point candidate : candidates) {
//                System.out.println("mosaic : ("+candidate.x+", "+candidate.y+")");
//            }
            }

            //aggregation ----------------------------------------------------------
            final Map hints = new HashMap();

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

                if(obj == Mosaic.END_OF_QUEUE){
                    break;
                }

                if(obj instanceof ImageTile){
                    final ImageTile tile = (ImageTile)obj;
                    final Point position = tile.getPosition();
                    final Point offset = new Point(
                            (int)(position.x-tileMinCol)*tileSize.width,
                            (int)(position.y-tileMinRow)*tileSize.height);

                    RenderedImage tileImage;
                    try {
                        tileImage = tile.getImage();
                    } catch (IOException ex) {
                        throw new CoverageStoreException(ex.getMessage(),ex);
                    }

                    if (image == null) {
                        ColorModel cm = null;
                        SampleModel sm = null;
                        if (cm == null) {
                            cm = tileImage.getColorModel();
                        }
                        if (sm == null) {
                            //if sample model is null, we need to have a coherent relation with
                            //the color model. we reuse the tile models.
                            cm = tileImage.getColorModel();
                            sm = tileImage.getSampleModel();
                        }
                        sm = sm.createCompatibleSampleModel((int)(tileMaxCol-tileMinCol)*tileSize.width,
                                                               (int)(tileMaxRow-tileMinRow)*tileSize.height);
                        final WritableRaster raster = WritableRaster.createWritableRaster(sm, null);
                        image = new BufferedImage(cm,raster,
                                cm.isAlphaPremultiplied(), new Hashtable<>());
                    }
                    //-- write current features tile into destination image.
                    final Rectangle tileBound = new Rectangle(offset.x, offset.y, tileImage.getWidth(), tileImage.getHeight());
                    final WritablePixelIterator destPix = new PixelIterator.Builder().setRegionOfInterest(tileBound).createWritable((BufferedImage)image);
                    final PixelIterator tilePix = new PixelIterator.Builder().create(tileImage);
                    double[] pixel = null;
                    while (destPix.next()) {
                        tilePix.next();
                        pixel = tilePix.getPixel(pixel);
                        destPix.setPixel(pixel);
                    }
                    assert !tilePix.next();
                }
            }

            if (image == null) {
                //no tiles intersect
                throw new DisjointCoverageDomainException("Following Requested envelope : "
                        +wantedEnv
                        + "\n do not intersect any tiles data in mosaic Envelope : "
                        +mosaic.getEnvelope());
            }
        }
////
////        //-- if DatabufferType of image is float or Double we must change the Color Space
////        //-- to bound sample value between 0 and 1 to avoid java 2d rendering problem
////        image = ImageUtils.replaceFloatingColorModel(image);

        //build the coverage ---------------------------------------------------
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        ref.getIdentifier().ifPresent((n) -> gcb.setName(n.tip().toString()));
        final List<SampleDimension> dimensions = getSampleDimensions();
        if (dimensions != null) {
            gcb.setSampleDimensions(dimensions.toArray(new SampleDimension[dimensions.size()]));
        }

        final long[] high = new long[wantedCRS.getCoordinateSystem().getDimension()];
        Arrays.fill(high, 1);
        high[0] = image.getWidth();
        high[1] = image.getHeight();

        final GridExtent ge = new GridExtent(null, null, high, false);
        final MathTransform gtc = Pyramids.getTileGridToCRSND(mosaic,
                new Point((int)tileMinCol,(int)tileMinRow),wantedCRS.getCoordinateSystem().getDimension());
        final GridGeometry2D gridgeo = new GridGeometry2D(ge, PixelOrientation.UPPER_LEFT, gtc, wantedCRS);
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

    private GridCoverage readCube(List<Mosaic> mosaics, Envelope wantedEnv, boolean deferred) throws CoverageStoreException, DataStoreException{
        //regroup mosaic by hierarchy cubes
        final TreeMap groups = new TreeMap();
        for (Mosaic mosaic : mosaics) {
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
    private void appendSlice(final TreeMap<Double,Object> rootGroup, Mosaic mosaic) throws CoverageStoreException{
        final DirectPosition upperLeft = mosaic.getUpperLeftCorner();
        TreeMap<Double,Object> groups = rootGroup;

        //regroup them by inverse axis order so we can rebuild stacks always adding dimensions at the end
        for(int i=upperLeft.getDimension()-1; i>=2; i--){
            final double d = upperLeft.getOrdinate(i);
            final Object obj = groups.get(d);
            if(obj==null){
                groups.put(d, mosaic);
                break;
            }else if(obj instanceof Mosaic){
                //already another mosaic for the dimension slice
                //replace the coverage by a map and re-add them.
                groups.put(d, new TreeMap());
                appendSlice(rootGroup, (Mosaic) obj);
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

        final List<GridCoverageStack.Element> elements = new ArrayList<>();
        final List<Entry<Double,Object>> entries = new ArrayList<>(groups.entrySet());

        for(int k=0,kn=entries.size();k<kn;k++){
            final Entry<Double,Object> entry = entries.get(k);

            final Double z = entry.getKey();
            final Object obj = entry.getValue();

            final GeneralEnvelope sliceEnvelop = new GeneralEnvelope(crs);
            for (int i = 0; i < nbDim; i++) {
                if (i == axisIndex) {
                    sliceEnvelop.setRange(i, z, z);
                } else {
                    sliceEnvelop.setRange(i, wantedEnv.getMinimum(i), wantedEnv.getMaximum(i));
                }
            }

            final GridCoverage subCoverage;
            if(obj instanceof Mosaic){
                subCoverage = readSlice((Mosaic)obj, sliceEnvelop, deferred);
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

            elements.add(new GridCoverageStack.Adapter(subCoverage, NumberRange.create(min, true, max, false), z));
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
