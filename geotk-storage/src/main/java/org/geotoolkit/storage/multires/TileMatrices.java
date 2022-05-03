/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.multires;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.coverage.grid.PixelTranslation;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.tiling.TileMatrix;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Static;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class TileMatrices extends Static {

    public static Comparator<TileMatrix> SCALE_COMPARATOR = new Comparator<TileMatrix>() {
        @Override
        public int compare(TileMatrix tm1, TileMatrix tm2) {
            final double[] res1 = tm1.getTilingScheme().getResolution(true);
            final double[] res2 = tm2.getTilingScheme().getResolution(true);
            for (int i = 0; i < res1.length; i++) {
                int cmp = Double.compare(res1[i], res2[i]);
                if (cmp != 0) return cmp;
            }
            return 0;
        }
    };

    /**
     * Additional hint : to specify the mime type.
     */
    public static final String HINT_FORMAT = "format";

    private TileMatrices(){}

    /**
     * TODO
     * until all calls to getScale are remove, preserve the old bahavior : return the longitude pixel scale.
     */
    @Deprecated
    public static double getScale(TileMatrix tileMatrix) {
        return tileMatrix.getResolution()[0];
    }

    /**
     * Returns the upper left corner of the TileMatrix.
     * The corner is in PixelInCell.CELL_CORNER, so it contains a translate of a half
     * pixel compared to a GridToCrs transform of a coverage.
     *
     * @return upper left corner of the TileMatrix, expressed in pyramid CRS.
     */
    @Deprecated
    public static DirectPosition getUpperLeftCorner(TileMatrix tileMatrix) {
        final GeneralEnvelope envelope = new GeneralEnvelope(tileMatrix.getTilingScheme().getEnvelope());
        final GeneralDirectPosition upperLeft = new GeneralDirectPosition(envelope.getCoordinateReferenceSystem());
        upperLeft.setOrdinate(0, envelope.getMinimum(0));
        upperLeft.setOrdinate(1, envelope.getMaximum(1));
        for (int i = 2, n = envelope.getDimension(); i < n; i++) {
            upperLeft.setOrdinate(i, envelope.getMedian(i));
        }
        return upperLeft;
    }

    /**
     * @return size of the grid in number of columns/rows.
     */
    @Deprecated
    public static Dimension getGridSize(TileMatrix tileMatrix) {
        final GridExtent extent = tileMatrix.getTilingScheme().getExtent();
        return new Dimension(Math.toIntExact(extent.getSize(0)), Math.toIntExact(extent.getSize(1)));
    }

    /**
     * Create TileMatrix GridGeometry from corner, size and scale informations.
     * This method expect the grid size to match dimensions 0,1 of the corner CRS.
     * The upper left corner may have more then two dimensions, extract dimension will generate a 1 unit slice
     * in the GridGeometry.
     * Created GridGeometry will have the orientation GridOrientation.REFLECTION_Y.
     *
     * @param upperleft top left corner of the tile matrix
     * @param gridSize tile matrix grid size
     * @param scale pixel resolution
     * @param tileSize tile size in pixels
     * @return TileMatrix GridGeometry.
     */
    public static GridGeometry toTilingScheme(DirectPosition upperleft, Dimension gridSize, double scale, Dimension tileSize) {
        final CoordinateReferenceSystem crs = upperleft.getCoordinateReferenceSystem();

        final int dimension = crs.getCoordinateSystem().getDimension();
        final long[] low = new long[dimension];
        final long[] high = new long[dimension];
        high[0] = gridSize.width-1;
        high[1] = gridSize.height-1;
        final GridExtent extent = new GridExtent(null, low, high, true);

        final double offsetX  = upperleft.getOrdinate(0);
        final double offsetY = upperleft.getOrdinate(1);
        MatrixSIS matrix = Matrices.createDiagonal(dimension+1, dimension+1);
        matrix.setElement(0, 0, scale * tileSize.width);
        matrix.setElement(1, 1, -scale * tileSize.height);
        matrix.setElement(0, dimension, offsetX);
        matrix.setElement(1, dimension, offsetY);
        for (int i = 2; i < dimension; i++) {
            // this shoud normaly be zero, but it causes exception a many places, 0.0 is not handle well yet in the library
            double span = 1.0;
            matrix.setElement(i, i, span);
            matrix.setElement(i, dimension, upperleft.getOrdinate(i) - span/2.0);
        }
        final MathTransform gridToCrs = MathTransforms.linear(matrix);
        return new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCrs, crs);
    }

    /**
     * @return the different scales available in the pyramid.
     * The scale value is expressed in CRS unit by image cell (pixel usually)
     * Scales are sorted in natural order, from smallest to highest.
     */
    public static double[] getScales(TileMatrixSet tileMatrixSet) {
        return tileMatrixSet.getTileMatrices().values().stream()
                .mapToDouble(TileMatrices::getScale)
                .sorted()
                .toArray();
    }

    /**
     * @param scale the wanted scale, must match an available scale of the scales table.
     * @return Collection<TileMatrix> available TileMatrix at this scale.
     * Waring : in multidimensional pyramids, multiple TileMatrix at the same scale
     * may exist.
     */
    public static Collection<? extends org.geotoolkit.storage.multires.TileMatrix> getTileMatrices(TileMatrixSet tileMatrixSet, double scale) {
        final List<org.geotoolkit.storage.multires.TileMatrix> candidates = new ArrayList<>();
        for (org.geotoolkit.storage.multires.TileMatrix tileMatrix : tileMatrixSet.getTileMatrices().values()) {
            if (TileMatrices.getScale(tileMatrix) == scale) {
                candidates.add(tileMatrix);
            }
        }
        return candidates;
    }

    public static TileMatrixSet getTileMatrixSet(TiledResource resource, String tilematrixsetId) throws DataStoreException {
        for (TileMatrixSet p : resource.getTileMatrixSets()) {
            if (p.getIdentifier().equals(tilematrixsetId)) {
                return p;
            }
        }
        return null;
    }

    public static org.geotoolkit.storage.multires.TileMatrix getTileMatrix(TiledResource model, String pyramidId, String tileMatrixId) throws DataStoreException {
        final TileMatrixSet p = getTileMatrixSet(model, pyramidId);
        if (p == null) {
            return null;
        }

        for (org.geotoolkit.storage.multires.TileMatrix m : p.getTileMatrices().values()) {
            if (m.getIdentifier().equals(tileMatrixId)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Search in the given TileMatrixSet all of the TileMatrix which fit the given parameters. 2 modes
     * are possible :
     * - Contains only : Suitable TileMatrix must be CONTAINED (or equal) to given spatial filter.
     * - Intersection  : Suitable TileMatrix must INTERSECT given filter.
     *
     * @param toSearchIn The TileMatrixSet to get TileMatrix from.
     * @param filter The {@link Envelope} to use to  specify spatial position of wanted TileMatrix.
     * @param containOnly True if you want 'Contains only' mode, false if you want 'Intersection' mode.
     * @return A list containing all the TileMatrix which fit the given envelope. Never null, but can be empty.
     * @throws TransformException If input filter {@link CoordinateReferenceSystem} is not compatible with
     * input TileMatrix one.
     */
    public static List<org.geotoolkit.storage.multires.TileMatrix> findTileMatrix(final TileMatrixSet toSearchIn, Envelope filter, boolean containOnly) throws TransformException {
        final ArrayList<org.geotoolkit.storage.multires.TileMatrix> result = new ArrayList<>();

        // Rebuild filter envelope from pyramid CRS
        final GeneralEnvelope tmpFilter = new GeneralEnvelope(
                ReferencingUtilities.transform(filter, toSearchIn.getCoordinateReferenceSystem()));

        for (org.geotoolkit.storage.multires.TileMatrix tileMatrix : toSearchIn.getTileMatrices().values()) {
            final Envelope sourceEnv = tileMatrix.getTilingScheme().getEnvelope();
            if ((containOnly && tmpFilter.contains(sourceEnv, true))
                    || (!containOnly && tmpFilter.intersects(sourceEnv, true))) {
                result.add(tileMatrix);
            }
        }
        return result;
    }

    /**
     * Redefine mosaic changing tile size.
     *
     * @param tileMatrix
     * @param tileSize
     * @return
     */
    public static DefiningTileMatrix resizeTile(org.geotoolkit.storage.multires.TileMatrix tileMatrix, Dimension tileSize) {
        double scale = tileMatrix.getResolution()[0];
        scale *= tileMatrix.getTileSize().width;
        scale /= tileSize.width;
        return new DefiningTileMatrix(tileMatrix.getIdentifier(),
                toTilingScheme(getUpperLeftCorner(tileMatrix), getGridSize(tileMatrix), scale, tileSize), tileSize);
    }

    /**
     * Delete all tiles in the pyramid intersecting given envelope and resolution range.
     *
     * @param tileMatrixSet Pyramid to clean
     * @param env restricted envelope, can be null
     * @param resolutions restricted resolution range
     * @throws DataStoreException
     */
    public static void clear(WritableTileMatrixSet tileMatrixSet, Envelope env, NumberRange resolutions) throws DataStoreException {

        if (env != null) {
            try {
                env = Envelopes.transform(env, tileMatrixSet.getCoordinateReferenceSystem());
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        for (WritableTileMatrix tileMatrix : tileMatrixSet.getTileMatrices().values()) {
            if (resolutions == null || resolutions.contains(tileMatrix.getResolution()[0])) {
                final GridExtent extent;
                try {
                    extent = TileMatrices.getTilesInEnvelope(tileMatrix, env);
                } catch (NoSuchDataException ex) {
                    continue;
                }
                tileMatrix.deleteTiles(extent);
            }
        }
    }

    /**
     * Copy the mosaic structures from base pyramid to target pyramid.
     *
     * @param base not null
     * @param target not null
     */
    public static void copyStructure(org.apache.sis.storage.tiling.TileMatrixSet base, WritableTileMatrixSet target) throws DataStoreException {
        ArgumentChecks.ensureNonNull("base", base);
        ArgumentChecks.ensureNonNull("target", target);
        for (TileMatrix m : base.getTileMatrices().values()) {
            target.createTileMatrix(m);
        }
    }

    /**
     * Grid to CRS N dimension. CORNER transform
     *
     * @param tileMatrix not null
     * @param location not null
     * @param orientation pixel orientation
     * @return MathTransform never null
     */
    public static LinearTransform getTileGridToCRS(org.geotoolkit.storage.multires.TileMatrix tileMatrix, long[] location, PixelInCell orientation){
        final DirectPosition upperleft = getUpperLeftCorner(tileMatrix);
        return getTileGridToCRSND(tileMatrix, location, upperleft.getDimension(), orientation);
    }

    /**
     * Grid to CRS N dimension.
     * This allows to create a transform ignoring last axis transform.
     *
     * @param tileMatrix not null
     * @param location not null
     * @param nbDim : number of dimension wanted. value must be in range [2...crsNbDim]
     * @param orientation pixel orientation
     * @return MathTransform never null
     */
    public static LinearTransform getTileGridToCRSND(org.geotoolkit.storage.multires.TileMatrix tileMatrix, long[] location, int nbDim, PixelInCell orientation){

        final AffineTransform2D trs2d = getTileGridToCRS2D(tileMatrix, location, orientation);
        final DirectPosition upperleft = getUpperLeftCorner(tileMatrix);

        if(upperleft.getDimension()==2){
            return trs2d;
        }else{
            final int dim = nbDim+1;
            final Matrix gm = Matrices.createIdentity(dim);
            gm.setElement(0, 0, trs2d.getScaleX());
            gm.setElement(1, 1, trs2d.getScaleY());
            gm.setElement(0, dim-1, trs2d.getTranslateX());
            gm.setElement(1, dim-1, trs2d.getTranslateY());
            for(int i=2;i<dim-1;i++){
                gm.setElement(i, i, 1);
                gm.setElement(i, dim-1, upperleft.getOrdinate(i));
            }
            return MathTransforms.linear(gm);
        }
    }

    /**
     * Grid to CRS 2D part.
     *
     * @param tileMatrix not null
     * @param location not null
     * @param orientation pixel orientation
     * @return AffineTransform2D never null.
     */
    public static AffineTransform2D getTileGridToCRS2D(org.geotoolkit.storage.multires.TileMatrix tileMatrix, long[] location, PixelInCell orientation){

        final Dimension tileSize = tileMatrix.getTileSize();
        final DirectPosition upperleft = getUpperLeftCorner(tileMatrix);
        final double scale = tileMatrix.getResolution()[0];

        final double offsetX  = upperleft.getOrdinate(0) + location[0] * (scale * tileSize.width) ;
        final double offsetY = upperleft.getOrdinate(1) - location[1] * (scale * tileSize.height);
        AffineTransform2D transform2D = new AffineTransform2D(scale, 0, 0, -scale, offsetX, offsetY);
        if (orientation.equals(PixelInCell.CELL_CENTER)) {
            return (AffineTransform2D) PixelTranslation.translate(transform2D, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);
        }
        return transform2D;
    }

    /**
     * Compute tile envelope from matrix and position.
     */
    public static Envelope computeTileEnvelope(org.geotoolkit.storage.multires.TileMatrix tileMatrix, final long[] location) {
        return tileMatrix.getTilingScheme().derive()
                .subgrid(new GridExtent(null, location, location, true))
                .build()
                .getEnvelope();
    }

    /**
     * Returns a rectangle of the tiles intersecting given envelope.
     * If envelope is null, all tiles will be included.
     *
     * @param tileMatrix searched mosaic
     * @param wantedEnv searched envelope in mosaic {@link CoordinateReferenceSystem}
     * @return never null
     * @throws org.apache.sis.storage.DataStoreException if the grid geometry of the tile matrix isn't well defined
     * @throws org.apache.sis.storage.NoSuchDataException if envelope do not intersect tile matrix
     */
    public static GridExtent getTilesInEnvelope(org.geotoolkit.storage.multires.TileMatrix tileMatrix, Envelope wantedEnv) throws DataStoreException {
        if (wantedEnv == null) {
            return tileMatrix.getTilingScheme().getExtent();
        }

        return tileMatrix.getTilingScheme().derive().rounding(GridRoundingMode.ENCLOSING).subgrid(wantedEnv).getIntersection();
    }

    /**
     * Create a common WGS84 (CRS:84) 2D pyramid model with a fixed depth.
     * <p>
     * The pyramid covers the world in CRS:84 coordinate reference system.
     * Each mosaic is half the resolution of it's parent.
     * </p>
     * <ul>
     *   <li>The first level (0) has two tiles of bbox : (-180,-90,0,90), (0,-90,180,90)</li>
     *   <li>The second level (1) has 8 tiles.</li>
     *   <li>and so on ...</li>
     * </ul>
     * <p>
     * This pyramid model is often used by TMS service.
     * </p>
     *
     * @param maxDepth, maximum level inclusive
     * @return
     */
    public static DefiningTileMatrixSet createWorldWGS84Template(int maxDepth) {
        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final Dimension tileSize = new Dimension(256, 256);
        final GeneralDirectPosition corner = new GeneralDirectPosition(crs);
        corner.setOrdinate(0, -180.0);
        corner.setOrdinate(1, 90.0);

        final List<org.geotoolkit.storage.multires.TileMatrix> mosaics = new ArrayList<>();

        Dimension gridSize = new Dimension(2, 1);
        double scale = 360.0/512.0;
        int level = 0;
        final DefiningTileMatrix level0 = new DefiningTileMatrix(
                Names.createLocalName(null, null, ""+level),
                toTilingScheme(corner, gridSize, scale, tileSize),
                tileSize);
        mosaics.add(level0);

        level++;
        for (;level<=maxDepth;level++) {
            gridSize.width *= 2.0;
            gridSize.height *= 2.0;
            scale /= 2.0;
            final DefiningTileMatrix levelN = new DefiningTileMatrix(
                    Names.createLocalName(null, null, ""+level),
                    toTilingScheme(corner, gridSize, scale, tileSize),
                    tileSize);
            mosaics.add(levelN);
        }

        return new DefiningTileMatrixSet(Names.createLocalName(null, null, "wgs84"), crs, mosaics);
    }

    /**
     * Create Google Pseudo-Mercator (EPSG:3857) World pyramid.
     *
     * @param maxDepth
     * @return
     */
    public static DefiningTileMatrixSet createPseudoMercatorTemplate(int maxDepth) throws FactoryException {
        final CoordinateReferenceSystem pseudoMercator = CRS.forCode("EPSG:3857");

        //X goes from 0 (left edge is 180 °W) to 2^zoom -1 (right edge is 180 °E)
        //Y goes from 0 (top edge is 85.0511 °N) to 2^zoom -1 (bottom edge is 85.0511 °S) in a Mercator projection
        GeneralEnvelope MERCATOR_EXTEND = new GeneralEnvelope(pseudoMercator);
        MERCATOR_EXTEND.setRange(0, -20037508.342789244d, 20037508.342789244d);
        MERCATOR_EXTEND.setRange(1, -20037508.342789244d, 20037508.342789244d);

        final double[] scales = new double[maxDepth+1];
        scales[0] = MERCATOR_EXTEND.getSpan(0) / 256.0;
        for (int i=1;i<scales.length;i++) {
            scales[i] = scales[i-1] / 2.0;
        }

        return createTemplate(MERCATOR_EXTEND, new Dimension(256, 256), scales);
    }

    /**
     * Create Mercator (EPSG:33950 )World pyramid.
     *
     * @param maxDepth
     * @return
     */
    public static DefiningTileMatrixSet createMercatorTemplate(int maxDepth) throws FactoryException {
        final CoordinateReferenceSystem pseudoMercator = CRS.forCode("EPSG:3395");

        //X goes from 0 (left edge is 180 °W) to 2^zoom -1 (right edge is 180 °E)
        //Y goes from 0 (top edge is 85.0511 °N) to 2^zoom -1 (bottom edge is 85.0511 °S) in a Mercator projection
        GeneralEnvelope MERCATOR_EXTEND = new GeneralEnvelope(pseudoMercator);
        MERCATOR_EXTEND.setRange(0, -20037508.342789244d, 20037508.342789244d);
        MERCATOR_EXTEND.setRange(1, -20037508.342789244d, 20037508.342789244d);

        final double[] scales = new double[maxDepth+1];
        scales[0] = MERCATOR_EXTEND.getSpan(0) / 256.0;
        for (int i=1;i<scales.length;i++) {
            scales[i] = scales[i-1] / 2.0;
        }

        return createTemplate(MERCATOR_EXTEND, new Dimension(256, 256), scales);
    }

    /**
     * Create arbitrary template.
     *
     * @param dataEnv
     * @param minResolution
     * @return
     * @deprecated use TileMatrixSetBuilder instead
     */
    @Deprecated
    public static DefiningTileMatrixSet createTemplate(Envelope dataEnv, double minResolution) {
        return new TileMatrixSetBuilder()
                .setDomain(dataEnv, minResolution)
                .setNbTileThreshold(1)
                .setTileSize(new Dimension(256, 256))
                .build();
    }

    /**
     * Create multi dimension template from grid geometry.
     *
     * @param gridGeom reference grid geometry
     * @return
     * @deprecated use TileMatrixSetBuilder instead
     */
    @Deprecated
    public static DefiningTileMatrixSet createTemplate(GridGeometry gridGeom, Dimension tileSize) {
        return new TileMatrixSetBuilder()
                .setDomain(gridGeom)
                .setNbTileThreshold(1)
                .setTileSize(tileSize)
                .build();
    }

    /**
     * Create multi dimension template from grid geometry.
     *
     * @param gridGeom reference grid geometry
     * @param crs wanted template crs, if null, grid geometry crs is used
     * @param tileSize wanted template tile size
     * @return
     * @throws org.apache.sis.storage.DataStoreException if template could not be
     *         created because grid geometry doesn't have enough information.
     * @deprecated use TileMatrixSetBuilder instead
     */
    @Deprecated
    public static DefiningTileMatrixSet createTemplate(GridGeometry gridGeom, CoordinateReferenceSystem crs, Dimension tileSize) throws DataStoreException {
        try {
            return new TileMatrixSetBuilder()
                    .setDomain(gridGeom, crs)
                    .setNbTileThreshold(1)
                    .setTileSize(tileSize)
                    .build();
        } catch (FactoryException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * @deprecated use TileMatrixSetBuilder instead
     */
    @Deprecated
    public static DefiningTileMatrixSet createTemplate(Envelope envelope, Dimension tileSize, double[] scales) {
            return new TileMatrixSetBuilder()
                    .setDomain(envelope, 1)
                    .setScales(scales)
                    .setNbTileThreshold(1)
                    .setTileSize(tileSize)
                    .build();
    }

    /**
     * Find the matching pyramid depth for given resolution.
     *
     * @param resolution in WGS:84 units
     * @return
     */
    public static int computeWorldWGS84DepthForResolution(double resolution) {
        int depth = 0;
        double scale = 180.0 / 256.0;
        while (scale > resolution) {
            depth++;
            scale = scale / 2.0;
        }
        return depth;
    }

    /**
     * Find the matching pyramid depth for given resolution.
     *
     * @param resolution in PseudoMercator units
     * @return
     * @throws org.opengis.util.FactoryException
     */
    public static int computePseudoMercatorDepthForResolution(double resolution) throws FactoryException {
        final CoordinateReferenceSystem pseudoMercator = CRS.forCode("EPSG:3857");

        //X goes from 0 (left edge is 180 °W) to 2^zoom -1 (right edge is 180 °E)
        //Y goes from 0 (top edge is 85.0511 °N) to 2^zoom -1 (bottom edge is 85.0511 °S) in a Mercator projection
        GeneralEnvelope MERCATOR_EXTEND = new GeneralEnvelope(pseudoMercator);
        MERCATOR_EXTEND.setRange(0, -20037508.342789244d, 20037508.342789244d);
        MERCATOR_EXTEND.setRange(1, -20037508.342789244d, 20037508.342789244d);

        int depth = 0;
        double scale = MERCATOR_EXTEND.getSpan(0) / 256.0;
        while (scale > resolution) {
            depth++;
            scale = scale / 2.0;
        }
        return depth;
    }

    /**
     * Create tile grid geometry, with it's extent relative to the tile matrix corner.
     *
     * @param tileMatrix
     * @param location
     * @return
     */
    public static GridGeometry getAbsoluteTileGridGeometry2D(org.geotoolkit.storage.multires.TileMatrix tileMatrix, long[] location) {
        final SingleCRS crs2d = CRS.getHorizontalComponent(tileMatrix.getTilingScheme().getCoordinateReferenceSystem());
        final Dimension tileSize = tileMatrix.getTileSize();
        final AffineTransform2D matrixGridToCrs = getTileGridToCRS2D(tileMatrix, new long[]{0,0}, PixelInCell.CELL_CENTER);
        final long[] low = new long[]{
            location[0] * tileSize.width,
            location[1] * tileSize.height,
        };
        final long[] high = new long[]{
            low[0] + tileSize.width,
            low[1] + tileSize.height
        };
        final GridExtent tileExtent = new GridExtent(null, low, high, false);
        return new GridGeometry(tileExtent, PixelInCell.CELL_CENTER, matrixGridToCrs, crs2d);
    }

    public static GridGeometry getTileGridGeometry2D(org.geotoolkit.storage.multires.TileMatrix tileMatrix, long[] location) {
        final SingleCRS crs2d = CRS.getHorizontalComponent(tileMatrix.getTilingScheme().getCoordinateReferenceSystem());
        final Dimension tileSize = tileMatrix.getTileSize();
        final AffineTransform2D tileGridToCrs = getTileGridToCRS2D(tileMatrix, location, PixelInCell.CELL_CENTER);
        final GridExtent tileExtent = new GridExtent(tileSize.width, tileSize.height);
        return new GridGeometry(tileExtent, PixelInCell.CELL_CENTER, tileGridToCrs, crs2d);
    }

    public static GridGeometry getTileGridGeometry2D(org.geotoolkit.storage.multires.TileMatrix tileMatrix, Rectangle rectangle) {
        final SingleCRS crs2d = CRS.getHorizontalComponent(tileMatrix.getTilingScheme().getCoordinateReferenceSystem());
        final Dimension tileSize = tileMatrix.getTileSize();
        final AffineTransform2D tileGridToCrs = getTileGridToCRS2D(tileMatrix, new long[]{rectangle.x, rectangle.y}, PixelInCell.CELL_CENTER);
        final GridExtent tileExtent = new GridExtent((long) tileSize.width * rectangle.width, (long) tileSize.height * rectangle.height);
        return new GridGeometry(tileExtent, PixelInCell.CELL_CENTER, tileGridToCrs, crs2d);
    }

    /**
     * @deprecated use getTileGridGeometry2D method without crs parameter.
     */
    @Deprecated
    public static GridGeometry getTileGridGeometry2D(org.geotoolkit.storage.multires.TileMatrix tileMatrix, long[] location, CoordinateReferenceSystem crs) {
        final Dimension tileSize = tileMatrix.getTileSize();
        final AffineTransform2D tileGridToCrs = getTileGridToCRS2D(tileMatrix, location, PixelInCell.CELL_CENTER);
        final GridExtent tileExtent = new GridExtent(tileSize.width, tileSize.height);
        return new GridGeometry(tileExtent, PixelInCell.CELL_CENTER, tileGridToCrs, crs);
    }

    /**
     * @deprecated use getTileGridGeometry2D method without crs parameter.
     */
    @Deprecated
    public static GridGeometry getTileGridGeometry2D(org.geotoolkit.storage.multires.TileMatrix tileMatrix, Rectangle rectangle, CoordinateReferenceSystem crs) {
        final Dimension tileSize = tileMatrix.getTileSize();
        final AffineTransform2D tileGridToCrs = getTileGridToCRS2D(tileMatrix, new long[]{rectangle.x, rectangle.y}, PixelInCell.CELL_CENTER);
        final GridExtent tileExtent = new GridExtent((long) tileSize.width * rectangle.width, (long) tileSize.height * rectangle.height);
        return new GridGeometry(tileExtent, PixelInCell.CELL_CENTER, tileGridToCrs, crs);
    }

    /**
     * Count the number of tiles in TileMatrixSet.
     *
     * @param tileMatrixSet
     * @param env searched envelope, can be null
     * @param resolutions, searched resolutions, can be null
     * @return number of tiles.
     * @throws DataStoreException
     */
    public static long countTiles(TileMatrixSet tileMatrixSet, Envelope env, NumberRange resolutions) throws DataStoreException {

        long count = 0;
        for (org.geotoolkit.storage.multires.TileMatrix tileMatrix : tileMatrixSet.getTileMatrices().values()) {
            if (resolutions == null || resolutions.containsAny(tileMatrix.getResolution()[0])) {
                GridExtent ext;
                try {
                    ext = TileMatrices.getTilesInEnvelope(tileMatrix, env);
                } catch (NoSuchDataException ex) {
                    continue;
                }
                count += countCells(ext);
            }
        }
        return count;
    }

    public static long countCells(GridExtent extent) {
        long nb = 1;
        for (int i = 0; i < extent.getDimension(); i++) {
            nb *= extent.getSize(i);
        }
        return nb;
    }

    /**
     * Test if indices are within GridExtent range.
     * TODO : remove when GridExtent.contains is available in SIS.
     */
    public static boolean contains(GridExtent extent, long... indices) {
        for (int i = 0; i < indices.length; i++) {
            if (indices[i] < extent.getLow(i) || indices[i] > extent.getHigh(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Test if indices are on the border of the GridExtent.
     */
    public static boolean onBorder(GridExtent extent, long... indices) {
        for (int i = 0, n = extent.getDimension(); i < n; i++) {
            if (indices[i] == extent.getLow(i) || indices[i] == extent.getHigh(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a stream of point in the GridExtent.
     *
     * TODO : make a more efficient implementation.
     */
    public static Stream<long[]> pointStream(GridExtent extent) {
        final int dimension = extent.getDimension();
        final long[] low = extent.getLow().getCoordinateValues();
        final long[] high = extent.getHigh().getCoordinateValues();

        Stream<long[]> stream = LongStream.range(low[0], high[0]+1)
                .mapToObj((long value) -> {
                    final long[] array = new long[dimension];
                    array[0] = value;
                    return array;
        });
        for (int i = 1; i <dimension; i++) {
            final int idx = i;
            stream = stream.flatMap((long[] t) -> LongStream.range(low[idx], high[idx]+1)
                    .mapToObj((long value) -> {
                        final long[] array = t.clone();
                        array[idx] = value;
                        return array;
                    }));
        }
        return stream;
    }
}
