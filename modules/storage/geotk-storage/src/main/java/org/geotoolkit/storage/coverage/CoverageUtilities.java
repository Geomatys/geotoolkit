/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Geomatys
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
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.SampleDimensionType;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.coverage.finder.CoverageFinder;
import org.geotoolkit.storage.coverage.finder.StrictlyCoverageFinder;
import org.geotoolkit.storage.multires.DefiningMosaic;
import org.geotoolkit.storage.multires.DefiningPyramid;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Pyramids;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.SphericalCS;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * Utility functions for coverage and mosaic.
 *
 * @author Johann Sorel  (Geomatys)
 * @author Rémi Marechal (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public final class CoverageUtilities {

    private CoverageUtilities() {}

    /**
     * Find the most appropriate pyramid in given pyramid set and given crs.
     * Returned Pyramid may not have the given crs.
     *
     * @param set pyramid set to search in
     * @param crs searched crs
     * @return Pyramid, never null except if the pyramid set is empty
     * TODO: Is it really OK ? If we search a Lambert pyramid, and we've only got polar ones...
     * @deprecated use {@link org.geotoolkit.coverage.finder.StrictlyCoverageFinder#findPyramid(PyramidSet, org.opengis.referencing.crs.CoordinateReferenceSystem)}
     */
    public static Pyramid findPyramid(final MultiResolutionResource set, final CoordinateReferenceSystem crs) throws FactoryException, DataStoreException {
        CoverageFinder finder = new StrictlyCoverageFinder();
        return finder.findPyramid(set, crs);
    }

    /**
     * Find the most appropriate mosaic in the pyramid with the given information.
     *
     * @deprecated use {@link org.geotoolkit.coverage.finder.StrictlyCoverageFinder#findMosaic(Pyramid, double, double, org.opengis.geometry.Envelope, Integer)}
     */
    public static Mosaic findMosaic(final Pyramid pyramid, final double resolution,
            final double tolerance, final Envelope env, int maxTileNumber) throws FactoryException
    {
        CoverageFinder finder = new StrictlyCoverageFinder();
        return finder.findMosaic(pyramid, resolution, tolerance, env, maxTileNumber);
    }

    /**
     * Search in the given pyramid all of the mosaics which fit the given parameters. 2 modes
     * are possible :
     * - Contains only : Suitable mosaics must be CONTAINED (or equal) to given spatial filter.
     * - Intersection  : Suitable mosaics must INTERSECT given filter.
     *
     * TODO port this method in CoverageFinder
     *
     * @param toSearchIn The pyramid to get mosaics from.
     * @param filter The {@link Envelope} to use to  specify spatial position of wanted mosaics.
     * @param containOnly True if you want 'Contains only' mode, false if you want 'Intersection' mode.
     * @return A list containing all the mosaics which fit the given envelope. Never null, but can be empty.
     * @throws TransformException If input filter {@link CoordinateReferenceSystem} is not compatible with
     * input mosaics one.
     */
    public static List<Mosaic> findMosaics(final Pyramid toSearchIn, Envelope filter, boolean containOnly) throws TransformException {
        final ArrayList<Mosaic> result = new ArrayList<Mosaic>();

        // Rebuild filter envelope from pyramid CRS
        final GeneralEnvelope tmpFilter = new GeneralEnvelope(
                ReferencingUtilities.transform(filter, toSearchIn.getCoordinateReferenceSystem()));

        for (Mosaic source : toSearchIn.getMosaics()) {
            final Envelope sourceEnv = source.getEnvelope();
            if ((containOnly && tmpFilter.contains(sourceEnv, true))
                    || (!containOnly && tmpFilter.intersects(sourceEnv, true))) {
                result.add(source);
            }
        }
        return result;
    }

    /**
     * Compute ratio on each ordinate, not within 2D part of {@link CoordinateReferenceSystem},
     * which represent recovery from each ordinate of searchEnvelope on gridEnvelope.
     *
     * @param searchEnvelope user coverage area search.
     * @param gridEnvelope mosaic envelope.
     * @return computed ratio.
     */
    public static double getRatioND(Envelope searchEnvelope, Envelope gridEnvelope) {
        ArgumentChecks.ensureNonNull("gridEnvelope", gridEnvelope);
        ArgumentChecks.ensureNonNull("findEnvelope", searchEnvelope);
        final CoordinateReferenceSystem crs = gridEnvelope.getCoordinateReferenceSystem();
        //find index ordinate of crs2D part of this crs.
        int minOrdinate2D = 0;
        boolean find = false;
        for (CoordinateReferenceSystem ccrrss : CRS.getSingleComponents(crs)) {
            final CoordinateSystem cs = ccrrss.getCoordinateSystem();
            if ((cs instanceof CartesianCS) || (cs instanceof SphericalCS) || (cs instanceof EllipsoidalCS)) {
                find = true;
                break;
            }
            minOrdinate2D += cs.getDimension();
        }
        final int maxOrdinate2D = minOrdinate2D + 1;
        // compute distance
        if (!find) throw new IllegalArgumentException("CRS 2D part, not find");
        final GeneralEnvelope intersection = new GeneralEnvelope(searchEnvelope);
        intersection.intersect(gridEnvelope);
        double sumRatio = 0;
        final int dimension = crs.getCoordinateSystem().getDimension();
        for (int d = 0; d < dimension; d++) {
            if (d != minOrdinate2D && d != maxOrdinate2D) {
                final double ges = gridEnvelope.getSpan(d);
                // if intersect a slice part of gridEnvelope.
                // avoid divide by zero
                if (Math.abs(ges) <= 1E-12) continue;
                sumRatio += intersection.getSpan(d) / ges;
            }
        }
        return sumRatio;
    }

    /**
     * Retrieve index of the first axis of the geographic component in the input {@link CoordinateReferenceSystem}.
     *
     * @param crs {@link CoordinateReferenceSystem} which is analysed.
     * @return Index of the first horizontal axis in this CRS
     * @throws java.lang.IllegalArgumentException if input CRS has no horizontal component.
     *
     * @deprecated moved to {@link org.geotoolkit.internal.referencing.CRSUtilities#firstHorizontalAxis(org.opengis.referencing.crs.CoordinateReferenceSystem)}
     */
    public static int getMinOrdinate(final CoordinateReferenceSystem crs) {
        int tempOrdinate = 0;
        for (CoordinateReferenceSystem ccrrss : CRS.getSingleComponents(crs)) {
            final CoordinateSystem cs = ccrrss.getCoordinateSystem();
            if((cs instanceof CartesianCS) || (cs instanceof SphericalCS) || (cs instanceof EllipsoidalCS)) {
                return tempOrdinate;
            }
            tempOrdinate += cs.getDimension();
        }
        throw new IllegalArgumentException("crs doesn't have any horizontal crs");
    }

    /**
     * Copy a set of pyramid pointed by source coverage reference into destination
     * reference.
     * @param sourceRef The {@link PyramidalCoverageResource} to copy data from.
     * @param targetRef The {@link PyramidalCoverageResource} to copy data to.
     * @throws DataStoreException If a problem occurs at pyramid access.
     * @throws IOException If a problem occurs at image reading/writing.
     */
    public static void copyPyramidReference(MultiResolutionResource sourceRef, MultiResolutionResource targetRef) throws DataStoreException, IOException {
        final Collection<? extends Pyramid> pyramids = Pyramids.getPyramids(sourceRef);

        // Create pyramids
        for (Pyramid sP : pyramids) {
            final Pyramid tP = (Pyramid) targetRef.createModel(new DefiningPyramid(sP.getCoordinateReferenceSystem()));

            //create mosaics
            for (Mosaic sM : sP.getMosaics()) {
                final Mosaic tM = tP.createMosaic(sM);
                final int height = sM.getGridSize().height;
                final int width = sM.getGridSize().width;

                // Write tiles
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        if (!sM.isMissing(x, y)) {
                            final ImageTile sT = (ImageTile) sM.getTile(x, y);
                            final RenderedImage sourceImg = sT.getImage();
                            tM.writeTiles(Stream.of(new DefaultImageTile(sourceImg, new Point(x, y))), null);
                        }
                    }
                }
            }
        }
    }

    public static int getDataType(final GridCoverage coverage) {
        GridGeometry grid = coverage.getGridGeometry();
        GridGeometry query = grid.derive().sliceByRatio(0, 0, 1).build();
        RenderedImage image = coverage.render(query.getExtent());
        return image.getSampleModel().getDataType();
    }

    @Deprecated
    public static int getDataType(SampleDimensionType sdt) {
        if (SampleDimensionType.REAL_32BITS.equals(sdt)) {
            return DataBuffer.TYPE_FLOAT;
        } else if (SampleDimensionType.REAL_64BITS.equals(sdt)) {
            return DataBuffer.TYPE_DOUBLE;
        } else if (SampleDimensionType.SIGNED_8BITS.equals(sdt)) {
            return DataBuffer.TYPE_BYTE;
        } else if (SampleDimensionType.SIGNED_16BITS.equals(sdt)) {
            return DataBuffer.TYPE_SHORT;
        } else if (SampleDimensionType.SIGNED_32BITS.equals(sdt)) {
            return DataBuffer.TYPE_INT;
        } else if (SampleDimensionType.UNSIGNED_1BIT.equals(sdt)) {
            return DataBuffer.TYPE_BYTE;
        } else if (SampleDimensionType.UNSIGNED_2BITS.equals(sdt)) {
            return DataBuffer.TYPE_BYTE;
        } else if (SampleDimensionType.UNSIGNED_4BITS.equals(sdt)) {
            return DataBuffer.TYPE_BYTE;
        } else if (SampleDimensionType.UNSIGNED_8BITS.equals(sdt)) {
            return DataBuffer.TYPE_BYTE;
        } else if (SampleDimensionType.UNSIGNED_16BITS.equals(sdt)) {
            return DataBuffer.TYPE_USHORT;
        } else if (SampleDimensionType.UNSIGNED_32BITS.equals(sdt)) {
            return DataBuffer.TYPE_INT;
        }else {
            throw new IllegalArgumentException("Unexprected data type : "+sdt);
        }
    }

    /**
     * Get or create a pyramid and it's mosaic for the given envelope and scales.
     */
    public static Pyramid getOrCreatePyramid(MultiResolutionResource container,
            Envelope envelope, Dimension tileSize, double[] scales) throws DataStoreException
    {
        // Find if we already have a pyramid in the given CRS
        Pyramid pyramid = null;
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
        for (Pyramid candidate : Pyramids.getPyramids(container)) {
            if (Utilities.equalsApproximately(crs, candidate.getCoordinateReferenceSystem())) {
                pyramid = candidate;
                break;
            }
        }
        if (pyramid == null) {
            // We didn't find a pyramid, create one
            pyramid = (Pyramid) container.createModel(new DefiningPyramid(crs));
        }

        // Those parameters can change if another mosaic already exist
        final DirectPosition newUpperleft = new GeneralDirectPosition(crs);
        // We found the second horizontale axis dimension.
        final int maxHorizOrdinate = CRSUtilities.firstHorizontalAxis(crs) + 1;
        for (int d = 0; d < crs.getCoordinateSystem().getDimension(); d++) {
            final double v = (d == maxHorizOrdinate) ? envelope.getMaximum(d) : envelope.getMinimum(d);
            newUpperleft.setOrdinate(d, v);
        }

        // Generate each mosaic
        for (final double scale : scales) {
            final double gridWidth  = envelope.getSpan(0) / (scale*tileSize.width);
            final double gridHeight = envelope.getSpan(1) / (scale*tileSize.height);

            final int dataPixelWidth  = (int) (envelope.getSpan(0) / scale);        // Fully filled area
            final int dataPixelHeight = (int) (envelope.getSpan(1) / scale);

            Dimension tileDim = tileSize;
            Dimension gridSize = new Dimension( (int)(Math.ceil(gridWidth)), (int)(Math.ceil(gridHeight)));

            // Check if we already have a mosaic at this scale
            boolean mosaicFound = false;
            int index = 0;
            for (Mosaic m : pyramid.getMosaics()) {
                if (m.getScale() == scale) {
                    mosaicFound = true;
                    break;
                }
            }
            if (!mosaicFound) {
                // Create a new mosaic
                final DefiningMosaic dm = new DefiningMosaic(
                        null,
                        newUpperleft,
                        scale,
                        tileSize,
                        gridSize,
                        new GridExtent(dataPixelWidth, dataPixelHeight));
                pyramid.createMosaic(dm);
            }
        }
        return pyramid;
    }

    /**
     * Compute Pyramid envelope.
     *
     * @param pyramid shouldn't be null
     * @return pyramid Envelope or null if no mosaic found.
     */
    public static GeneralEnvelope getPyramidEnvelope(org.geotoolkit.storage.multires.Pyramid pyramid) {
        ArgumentChecks.ensureNonNull("pyramid", pyramid);
        GeneralEnvelope pyramidEnv = null;
        for (Mosaic mosaic : pyramid.getMosaics()) {
            if (pyramidEnv == null) {
                pyramidEnv = new GeneralEnvelope(mosaic.getEnvelope());
            } else {
                pyramidEnv.add(mosaic.getEnvelope());
            }
        }
        return pyramidEnv;
    }
}
