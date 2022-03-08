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
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;
import java.util.logging.Logger;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.ImageProcessor;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.grid.GridCoverageStack;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.coverage.finder.CoverageFinder;
import org.geotoolkit.storage.coverage.finder.DefaultCoverageFinder;
import org.geotoolkit.storage.multires.GeneralProgressiveResource;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.TiledResource;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
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
 * @module
 */
public class TileMatrixSetCoverageReader <T extends TiledResource & org.apache.sis.storage.GridCoverageResource> {

    private final T ref;

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.storage.coverage");

    public TileMatrixSetCoverageReader(T ref) {
        this.ref = ref;
    }

    public GridGeometry getGridGeometry() throws DataStoreException, CancellationException {
        final Collection<? extends TileMatrixSet> models = ref.getTileMatrixSets();

        //search for a pyramid
        //-- we use the first pyramid as default
        org.geotoolkit.storage.multires.TileMatrixSet pyramid = null;
        for (TileMatrixSet model : models) {
            pyramid = model;
            break;
        }

        return getGridGeometry(pyramid);
    }

    private static GridGeometry getGridGeometry(TileMatrixSet pyramid) {

        if (pyramid == null) {
            //-- empty pyramid set
            return GridGeometry.UNDEFINED;
        }

        final List<TileMatrix> mosaics = new ArrayList<>(pyramid.getTileMatrices());
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
        final TileMatrix mosaic  = mosaics.get(0);

        //-- we expect no rotation
        final MathTransform gridToCRS = TileMatrices.getTileGridToCRS2D(mosaic, new Point(0, 0), PixelInCell.CELL_CORNER);

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
                for (final TileMatrix gridMos : mosaics) {
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
        final Dimension gridSize = mosaic.getGridSize();
        final Dimension tileSize = mosaic.getTileSize();
        final GridExtent dataSize = new GridExtent(
                    ((long) gridSize.width) * tileSize.width,
                    ((long) gridSize.height) * tileSize.height);

        final long[] low  = new long[nbdim];
        final long[] high  = new long[nbdim];

        for (int i = 0; i < cs.getDimension(); i++) {
            if (i == minordi) {
                low[i] = dataSize.getLow(0); //-- X horizontal 2D part
                high[i] = dataSize.getHigh(0) + 1; //-- X horizontal 2D part, +1 for exclusive
            } else if (i == minordi + 1) {
                low[i] = dataSize.getLow(1); //-- Y horizontal 2D part
                high[i] = dataSize.getHigh(1) + 1; //-- Y horizontal 2D part, +1 for exclusive
            } else if (i != minordi && i != minordi + 1) {
                low[i] = 0;
                high[i] = multiAxisValues.get(i).length; //-- other dimension grid high value = discret axis values number.
            } else {
                //-- should never append
                throw new IllegalStateException("PyramidalModelReader.getGridGeometry() : problem during grid creation.");
            }
        }

        final GridExtent ge = new GridExtent(null, low, high, false);

        // TODO : we should do the transform like this but the is an issue further with derivate transforms
        //convert to center
        //gridToCRS = PixelTranslation.translate(gridToCRS, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);
        //final MathTransform gridToCRSds = ReferencingUtilities.toTransform(minordi, gridToCRS, multiAxisValues, cs.getDimension());
        //gridGeom = new GridGeometry(ge, PixelInCell.CELL_CENTER, gridToCRSds, crs);

        final MathTransform gridToCRSds = ReferencingUtilities.toTransform(minordi, gridToCRS, multiAxisValues, cs.getDimension());
        gridGeom = new GridGeometry(ge, PixelInCell.CELL_CORNER, gridToCRSds, crs);

        return gridGeom;
    }

    public List<SampleDimension> getSampleDimensions() throws DataStoreException, CancellationException {
        return ref.getSampleDimensions();
    }

    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {

        //choose the most appropriate pyramid based on requested CRS
        if (domain == null) {
            domain = getGridGeometry();
        }

        if (range != null && range.length == 0) range = null;

        final Entry<Envelope, List<TileMatrix>> intersect = intersect(ref, domain);
        final List<TileMatrix> mosaics = intersect.getValue();
        final Envelope wantedEnv = intersect.getKey();

        //-- features the data
        if (mosaics.size() == 1) {
            //-- features a single slice
            return readSlice(mosaics.get(0), wantedEnv, range);

        } else {
            //-- features a data cube of multiple slices
            return readCube(mosaics, wantedEnv, range);
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
    private GridCoverage readSlice(TileMatrix mosaic, Envelope wantedEnv, int... range) throws DataStoreException {

        final CoordinateReferenceSystem wantedCRS = wantedEnv.getCoordinateReferenceSystem();
        List<SampleDimension> sampleDimensions = ref.getSampleDimensions();
        if (range != null) {
            List<SampleDimension> subSamples = new ArrayList<>();
            for (int i : range) {
                subSamples.add(sampleDimensions.get(i));
            }
            sampleDimensions = subSamples;
        }

        final Dimension tileSize = mosaic.getTileSize();

        final Rectangle tilesInEnvelope = TileMatrices.getTilesInEnvelope(mosaic, wantedEnv);
        final int tileMinCol = tilesInEnvelope.x;
        final int tileMinRow = tilesInEnvelope.y;

        if (mosaic instanceof GeneralProgressiveResource.ProgressiveTileMatrix) {

            RenderedImage image = ((GeneralProgressiveResource.ProgressiveTileMatrix) mosaic).asImage();
            if (range != null) {
                image = new ImageProcessor().selectBands(image, range);
            }

            final long[] high = new long[wantedCRS.getCoordinateSystem().getDimension()];
            Arrays.fill(high, 1);
            high[0] = image.getWidth();
            high[1] = image.getHeight();

            final GridExtent ge = new GridExtent(null, null, high, false);
            final MathTransform gtc = TileMatrices.getTileGridToCRSND(mosaic,
                    new Point(0,0),wantedCRS.getCoordinateSystem().getDimension(),
                    PixelInCell.CELL_CENTER);
            final GridGeometry gridgeo = new GridGeometry(ge, PixelInCell.CELL_CENTER, gtc, wantedCRS);

            final GridCoverage2D coverage = new GridCoverage2D(gridgeo, sampleDimensions, image);

            { //reduce area
                final long[] l = new long[high.length];
                final long[] h = new long[high.length];
                l[0] = tilesInEnvelope.x * tileSize.width;
                l[1] = tilesInEnvelope.y * tileSize.height;
                h[0] = (tilesInEnvelope.x+tilesInEnvelope.width) * tileSize.width;
                h[1] = (tilesInEnvelope.y+tilesInEnvelope.height) * tileSize.height;
                GridExtent crop = new GridExtent(null, l, h, false);
                RenderedImage subImage = coverage.render(crop);
                l[0] = 0;
                l[1] = 0;
                h[0] = (tilesInEnvelope.width) * tileSize.width;
                h[1] = (tilesInEnvelope.height) * tileSize.height;
                crop = new GridExtent(null, l, h, false);
                final MathTransform gtcr = TileMatrices.getTileGridToCRSND(mosaic,
                        new Point(tilesInEnvelope.x,tilesInEnvelope.y),wantedCRS.getCoordinateSystem().getDimension(),
                        PixelInCell.CELL_CENTER);
                final GridGeometry cropped = new GridGeometry(crop, PixelInCell.CELL_CENTER, gtcr, wantedCRS);

                return new GridCoverage2D(cropped, sampleDimensions, subImage);
            }
        }


        RenderedImage image =  TileMatrixImage.create(mosaic, tilesInEnvelope, sampleDimensions);
        if (range != null) {
            image = new ImageProcessor().selectBands(image, range);
        }

        final long[] high = new long[wantedCRS.getCoordinateSystem().getDimension()];
        Arrays.fill(high, 1);
        high[0] = image.getWidth();
        high[1] = image.getHeight();

        final GridExtent ge = new GridExtent(null, null, high, false);
        final MathTransform gtc = TileMatrices.getTileGridToCRSND(mosaic,
                new Point(tileMinCol,tileMinRow),wantedCRS.getCoordinateSystem().getDimension(),
                PixelInCell.CELL_CENTER);
        final GridGeometry gridgeo = new GridGeometry(ge, PixelInCell.CELL_CENTER, gtc, wantedCRS);
        return new GridCoverage2D(gridgeo, sampleDimensions, image);
    }

    private GridCoverage readCube(List<TileMatrix> mosaics, Envelope wantedEnv, int... range) throws DataStoreException {
        //regroup mosaic by hierarchy cubes
        final TreeMap groups = new TreeMap();
        for (TileMatrix mosaic : mosaics) {
            appendSlice(groups, mosaic);
        }

        int dim = wantedEnv.getDimension();
        //rebuild coverage
        return rebuildCoverage(groups, wantedEnv, dim-1, range);
    }

    /**
     * Organize mosaics in groups which are on the same dimension slice.
     *
     * @param rootGroup
     * @param mosaic
     * @throws CoverageStoreException
     */
    private void appendSlice(final TreeMap<Double,Object> rootGroup, TileMatrix mosaic) throws DataStoreException {
        final DirectPosition upperLeft = mosaic.getUpperLeftCorner();
        TreeMap<Double,Object> groups = rootGroup;

        //regroup them by inverse axis order so we can rebuild stacks always adding dimensions at the end
        for (int i=upperLeft.getDimension()-1; i>=2; i--) {
            final double d = upperLeft.getOrdinate(i);
            final Object obj = groups.get(d);
            if (obj == null) {
                groups.put(d, mosaic);
                break;
            } else if (obj instanceof TileMatrix) {
                //already another mosaic for the dimension slice
                //replace the coverage by a map and re-add them.
                groups.put(d, new TreeMap());
                appendSlice(rootGroup, (TileMatrix) obj);
                appendSlice(rootGroup, mosaic);
                break;
            } else if (obj instanceof TreeMap) {
                groups = (TreeMap) obj;
            } else {
                throw new DataStoreException("Found an object which is not a Coverage or a Map group, should not happen : "+obj);
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
    private GridCoverage rebuildCoverage(TreeMap<Double,Object> groups, Envelope wantedEnv, int axisIndex, int... range)
            throws DataStoreException {

        final CoordinateReferenceSystem crs = wantedEnv.getCoordinateReferenceSystem();
        final CoordinateSystem cs = crs.getCoordinateSystem();
        int nbDim = cs.getDimension();

        final List<GridCoverageStack.Element> elements = new ArrayList<>();
        final List<Entry<Double,Object>> entries = new ArrayList<>(groups.entrySet());

        for (int k=0,kn=entries.size();k<kn;k++) {
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
            if (obj instanceof TileMatrix) {
                subCoverage = readSlice((TileMatrix)obj, sliceEnvelop, range);
            } else if (obj instanceof TreeMap) {
                subCoverage = rebuildCoverage((TreeMap)obj, sliceEnvelop, axisIndex-1, range);
            } else {
                throw new DataStoreException("Found an object which is not a Coverage or a Map group, should not happen : "+obj);
            }

            //calculate the range
            double min;
            double max;
            if (k == 0) {
                if (kn == 1) {
                    //a single element, use a range of 1
                    min = z - 0.5;
                    max = z + 0.5;
                } else {
                    final double nextD = entries.get(k+1).getKey();
                    final double diff = (nextD - z) / 2.0;
                    min = z-diff;
                    max = z+diff;
                }
            } else if (k == kn-1) {
                final double previousD = entries.get(k-1).getKey();
                final double diff = (z - previousD) / 2.0;
                min = z-diff;
                max = z+diff;
            } else {
                final double prevD = entries.get(k-1).getKey();
                final double nextD = entries.get(k+1).getKey();
                min = z - (z - prevD) / 2.0;
                max = z + (nextD - z) / 2.0;
            }

            elements.add(new GridCoverageStack.Adapter(subCoverage, NumberRange.create(min, true, max, false), z));
        }

        try {
            return new GridCoverageStack("HyperCube"+ nbDim +"D", crs, elements, axisIndex);
        } catch (IOException | TransformException | FactoryException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * Intersect given resource with domain and return the most appropriate mosaics.
     *
     * @param mrr, not null
     * @param domain, not null
     */
    public static Entry<Envelope,List<TileMatrix>> intersect(TiledResource mrr, GridGeometry domain) throws DataStoreException {
        ArgumentChecks.ensureNonNull("mrr", mrr);
        ArgumentChecks.ensureNonNull("domain", domain);

        final DefaultCoverageFinder coverageFinder = new DefaultCoverageFinder();

        CoordinateReferenceSystem crs = domain.getCoordinateReferenceSystem();
        TileMatrixSet pyramid;
        try {
             pyramid = coverageFinder.findPyramid(mrr, crs);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex);
        }
        if (pyramid == null) {
            throw new NoSuchDataException("No data pyramids available in this resource.");
        }

        crs = pyramid.getCoordinateReferenceSystem();

        GridGeometry canvas = getGridGeometry(pyramid);
        try {
            canvas = canvas.derive().rounding(GridRoundingMode.ENCLOSING).subgrid(domain).build();
        } catch (IllegalArgumentException ex) {
            throw new NoSuchDataException(ex.getMessage(), ex);
        }

        Envelope paramEnv = canvas.getEnvelope();
        double[] resolution = canvas.getResolution(true);

        //-- estimate resolution if not given
        if (resolution == null) {
            //-- set resolution to infinite, will select the last mosaic level
            resolution = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
        }

        //-- no reliable pyramid
        if (pyramid == null) {
            throw new DataStoreException("No pyramid defined.");
        }

        /*
         * We will transform the input envelope to found pyramid CRS.
         */
        final CoordinateReferenceSystem pyramidCRS = pyramid.getCoordinateReferenceSystem();
        GeneralEnvelope wantedEnv;
        try {
            wantedEnv = new GeneralEnvelope(ReferencingUtilities.transform(paramEnv, pyramidCRS));
        } catch (TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
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

        List<TileMatrix> mosaics;
        try {
            mosaics = coverageFinder.findMosaics(pyramid, wantedResolution, tolerance, wantedEnv);
        } catch (MismatchedDimensionException ex) {
            throw new DataStoreException(ex.getMessage(),ex);
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

        return new AbstractMap.SimpleImmutableEntry<>(wantedEnv, mosaics);
    }
}
