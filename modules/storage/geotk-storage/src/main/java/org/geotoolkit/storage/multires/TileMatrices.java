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
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.PixelTranslation;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Static;
import org.apache.sis.util.Utilities;
import org.geotoolkit.internal.referencing.CRSUtilities;
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

    /**
     * Additional hint : to specify the mime type.
     */
    public static final String HINT_FORMAT = "format";

    private TileMatrices(){}

    public static Envelope getEnvelope(MultiResolutionResource resource) throws DataStoreException {
        for(TileMatrixSet pyramid : getTileMatrixSets(resource)){
            //we consider the first pyramid to be in the main data crs
            return pyramid.getEnvelope();
        }
        return null;
    }

    public static TileMatrixSet getTileMatrixSet(MultiResolutionResource resource, String pyramidId) throws DataStoreException {
        for (MultiResolutionModel p : resource.getModels()) {
            if (p instanceof TileMatrixSet && p.getIdentifier().equals(pyramidId)) {
                return (TileMatrixSet) p;
            }
        }
        return null;
    }

    public static TileMatrix getTileMatrix(MultiResolutionResource model, String pyramidId, String tileMatrixId) throws DataStoreException {
        final TileMatrixSet p = getTileMatrixSet(model, pyramidId);
        if (p == null) {
            return null;
        }

        for (TileMatrix m : p.getTileMatrices()) {
            if (m.getIdentifier().equals(tileMatrixId)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Redefine mosaic changing tile size.
     *
     * @param tileMatrix
     * @param tileSize
     * @return
     */
    public static DefiningTileMatrix resizeTile(TileMatrix tileMatrix, Dimension tileSize) {
        double scale = tileMatrix.getScale();
        scale *= tileMatrix.getTileSize().width;
        scale /= tileSize.width;
        return new DefiningTileMatrix(tileMatrix.getIdentifier(), tileMatrix.getUpperLeftCorner(), scale, tileSize, tileMatrix.getGridSize());
    }

    /**
     * Delete all tiles in the pyramid intersecting given envelope and resolution range.
     *
     * @param pyramid Pyramid to clean
     * @param env restricted envelope, can be null
     * @param resolutions restricted resolution range
     * @throws DataStoreException
     */
    public static void clear(TileMatrixSet pyramid, Envelope env, NumberRange resolutions) throws DataStoreException {

        if (env != null) {
            try {
                env = Envelopes.transform(env, pyramid.getCoordinateReferenceSystem());
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        for (TileMatrix mosaic : pyramid.getTileMatrices()) {
            if (resolutions == null || resolutions.contains(mosaic.getScale())) {
                final Rectangle rect;
                if (env == null) {
                    rect = new Rectangle(mosaic.getGridSize());
                } else {
                    rect = TileMatrices.getTilesInEnvelope(mosaic, env);
                }
                for (int x=rect.x, xn=rect.x+rect.width; x<xn; x++) {
                    for (int y=rect.y, yn=rect.y+rect.height; y<yn; y++) {
                        mosaic.deleteTile(x, y);
                    }
                }
            }
        }
    }

    /**
     * Copy the mosaic structures from base pyramid to target pyramid.
     *
     * @param base not null
     * @param target not null
     */
    public static void copyStructure(TileMatrixSet base, TileMatrixSet target) throws DataStoreException {
        ArgumentChecks.ensureNonNull("base", base);
        ArgumentChecks.ensureNonNull("target", target);
        for (TileMatrix m : base.getTileMatrices()) {
            target.createTileMatrix(m);
        }
    }

    public static List<TileMatrixSet> getTileMatrixSets(MultiResolutionResource resource) throws DataStoreException {
        final List<TileMatrixSet> pyramids = new ArrayList<>();
        for (MultiResolutionModel model : resource.getModels()) {
            if (model instanceof TileMatrixSet) pyramids.add((TileMatrixSet) model);
        }
        return pyramids;
    }

    /**
     * Grid to CRS N dimension. CORNER transform
     *
     * @param tileMatrix not null
     * @param location not null
     * @param orientation pixel orientation
     * @return MathTransform never null
     */
    public static LinearTransform getTileGridToCRS(TileMatrix tileMatrix, Point location, PixelInCell orientation){
        final DirectPosition upperleft = tileMatrix.getUpperLeftCorner();
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
    public static LinearTransform getTileGridToCRSND(TileMatrix tileMatrix, Point location, int nbDim, PixelInCell orientation){

        final AffineTransform2D trs2d = getTileGridToCRS2D(tileMatrix, location, orientation);
        final DirectPosition upperleft = tileMatrix.getUpperLeftCorner();

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
    public static AffineTransform2D getTileGridToCRS2D(TileMatrix tileMatrix, Point location, PixelInCell orientation){

        final Dimension tileSize = tileMatrix.getTileSize();
        final DirectPosition upperleft = tileMatrix.getUpperLeftCorner();
        final double scale = tileMatrix.getScale();

        final double offsetX  = upperleft.getOrdinate(0) + location.x * (scale * tileSize.width) ;
        final double offsetY = upperleft.getOrdinate(1) - location.y * (scale * tileSize.height);
        AffineTransform2D transform2D = new AffineTransform2D(scale, 0, 0, -scale, offsetX, offsetY);
        if (orientation.equals(PixelInCell.CELL_CENTER)) {
            return (AffineTransform2D) PixelTranslation.translate(transform2D, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);
        }
        return transform2D;
    }

    /**
     * Compute tile envelope from mosaic and position.
     *
     * @param tileMatrix
     * @param col
     * @param row
     * @return
     */
    public static Envelope computeTileEnvelope(TileMatrix tileMatrix, final long col, final long row) {
        final Dimension tileSize = tileMatrix.getTileSize();
        final double scale = tileMatrix.getScale();
        final GeneralDirectPosition ul = new GeneralDirectPosition(tileMatrix.getUpperLeftCorner());
        final int xAxis = Math.max(CRSUtilities.firstHorizontalAxis(ul.getCoordinateReferenceSystem()), 0);
        final int yAxis = xAxis + 1;
        final double minX = ul.getOrdinate(xAxis);
        final double maxY = ul.getOrdinate(yAxis);
        final double spanX = tileSize.width * scale;
        final double spanY = tileSize.height * scale;

        final GeneralEnvelope envelope = new GeneralEnvelope(ul.getCoordinateReferenceSystem());
        envelope.setRange(xAxis, minX + col*spanX, minX + (col+1)*spanX);
        envelope.setRange(yAxis, maxY - (row+1)*spanY, maxY - row*spanY);

        return envelope;
    }

    /**
     * Compute mosaic envelope from it's corner, scale and size.
     *
     * @param tileMatrix
     * @return
     */
    public static GeneralEnvelope computeMosaicEnvelope(TileMatrix tileMatrix) {
        final Dimension tileSize = tileMatrix.getTileSize();
        final Dimension gridSize = tileMatrix.getGridSize();
        final double scale = tileMatrix.getScale();
        final GeneralDirectPosition ul = new GeneralDirectPosition(tileMatrix.getUpperLeftCorner());
        final int xAxis = Math.max(CRSUtilities.firstHorizontalAxis(ul.getCoordinateReferenceSystem()), 0);
        final int yAxis = xAxis + 1;
        final double minX = ul.getOrdinate(xAxis);
        final double maxY = ul.getOrdinate(yAxis);
        final double spanX = scale * tileSize.width * gridSize.width;
        final double spanY = scale * tileSize.height * gridSize.height;

        final GeneralEnvelope envelope = new GeneralEnvelope(ul,ul);
        envelope.setRange(xAxis, minX, minX + spanX);
        envelope.setRange(yAxis, maxY - spanY, maxY );

        return envelope;
    }

    /**
     * Returns a rectangle of the tiles intersecting given envelope.
     * If envelope is null, all tiles will be included.
     *
     * @param tileMatrix searched mosaic
     * @param wantedEnv searched envelope in mosaic {@link CoordinateReferenceSystem}
     * @return
     */
    public static Rectangle getTilesInEnvelope(TileMatrix tileMatrix, Envelope wantedEnv) throws DataStoreException {
        if (wantedEnv == null) {
            return new Rectangle(tileMatrix.getGridSize());
        }

        final CoordinateReferenceSystem wantedCRS = wantedEnv.getCoordinateReferenceSystem();
        final Envelope mosEnvelope                = tileMatrix.getEnvelope();

        //-- check CRS conformity
        if (!(Utilities.equalsIgnoreMetadata(wantedCRS, mosEnvelope.getCoordinateReferenceSystem())))
            throw new IllegalArgumentException("the wantedEnvelope is not define in same CRS than mosaic. Expected : "
                                                +mosEnvelope.getCoordinateReferenceSystem()+". Found : "+wantedCRS);

        final DirectPosition upperLeft = tileMatrix.getUpperLeftCorner();
        assert Utilities.equalsIgnoreMetadata(upperLeft.getCoordinateReferenceSystem(), wantedCRS);

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
            throw new DataStoreException(ex);
        }

        //-- define appropriate gridToCRS
//        final Dimension gridSize = mosaic.getGridSize();
        final Dimension tileSize = tileMatrix.getTileSize();
//        final double sx          = mosEnv2D.getSpan(0) / (gridSize.width  * tileSize.width);
//        final double sy          = mosEnv2D.getSpan(1) / (gridSize.height * tileSize.height);
//        final double offsetX     = upperLeft.getOrdinate(xAxis);
//        final double offsetY     = upperLeft.getOrdinate(yAxis);

        final MathTransform gridToCrs2D = TileMatrices.getTileGridToCRS2D(tileMatrix, new Point(0, 0), PixelInCell.CELL_CENTER);

        final GeneralEnvelope envelopOfInterest2D = new GeneralEnvelope(wantedEnv2D);
        envelopOfInterest2D.intersect(mosEnv2D);

        final Envelope gridOfInterest;
        try {
            gridOfInterest = Envelopes.transform(gridToCrs2D.inverse(), envelopOfInterest2D);
        } catch (Exception ex) {
            throw new DataStoreException(ex);
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

        return new Rectangle(tileMinCol, tileMinRow, (tileMaxCol-tileMinCol), (tileMaxRow-tileMinRow));
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

        final List<TileMatrix> mosaics = new ArrayList<>();

        int level = 0;
        final DefiningTileMatrix level0 = new DefiningTileMatrix(""+level, corner, 360.0/512.0, tileSize, new Dimension(2, 1));
        mosaics.add(level0);

        level++;
        DefiningTileMatrix parent = level0;
        for (;level<=maxDepth;level++) {
            final DefiningTileMatrix levelN = new DefiningTileMatrix(""+level, corner,
                    parent.getScale() / 2.0, tileSize,
                    new Dimension(parent.getGridSize().width*2, parent.getGridSize().height*2));
            mosaics.add(levelN);
            parent = levelN;
        }

        return new DefiningTileMatrixSet("wgs84", "", crs, mosaics);
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

    public static GridGeometry getTileGridGeometry2D(TileMatrix tileMatrix, Point location) {
        final SingleCRS crs2d = CRS.getHorizontalComponent(tileMatrix.getUpperLeftCorner().getCoordinateReferenceSystem());
        final Dimension tileSize = tileMatrix.getTileSize();
        final AffineTransform2D tileGridToCrs = getTileGridToCRS2D(tileMatrix, location, PixelInCell.CELL_CENTER);
        final GridExtent tileExtent = new GridExtent(tileSize.width, tileSize.height);
        return new GridGeometry(tileExtent, PixelInCell.CELL_CENTER, tileGridToCrs, crs2d);
    }

    public static GridGeometry getTileGridGeometry2D(TileMatrix tileMatrix, Rectangle rectangle) {
        final SingleCRS crs2d = CRS.getHorizontalComponent(tileMatrix.getUpperLeftCorner().getCoordinateReferenceSystem());
        final Dimension tileSize = tileMatrix.getTileSize();
        final AffineTransform2D tileGridToCrs = getTileGridToCRS2D(tileMatrix, rectangle.getLocation(), PixelInCell.CELL_CENTER);
        final GridExtent tileExtent = new GridExtent((long) tileSize.width * rectangle.width, (long) tileSize.height * rectangle.height);
        return new GridGeometry(tileExtent, PixelInCell.CELL_CENTER, tileGridToCrs, crs2d);
    }

    /**
     * @deprecated use getTileGridGeometry2D method without crs parameter.
     */
    @Deprecated
    public static GridGeometry getTileGridGeometry2D(TileMatrix tileMatrix, Point location, CoordinateReferenceSystem crs) {
        final Dimension tileSize = tileMatrix.getTileSize();
        final AffineTransform2D tileGridToCrs = getTileGridToCRS2D(tileMatrix, location, PixelInCell.CELL_CENTER);
        final GridExtent tileExtent = new GridExtent(tileSize.width, tileSize.height);
        return new GridGeometry(tileExtent, PixelInCell.CELL_CENTER, tileGridToCrs, crs);
    }

    /**
     * @deprecated use getTileGridGeometry2D method without crs parameter.
     */
    @Deprecated
    public static GridGeometry getTileGridGeometry2D(TileMatrix tileMatrix, Rectangle rectangle, CoordinateReferenceSystem crs) {
        final Dimension tileSize = tileMatrix.getTileSize();
        final AffineTransform2D tileGridToCrs = getTileGridToCRS2D(tileMatrix, rectangle.getLocation(), PixelInCell.CELL_CENTER);
        final GridExtent tileExtent = new GridExtent((long) tileSize.width * rectangle.width, (long) tileSize.height * rectangle.height);
        return new GridGeometry(tileExtent, PixelInCell.CELL_CENTER, tileGridToCrs, crs);
    }

}
