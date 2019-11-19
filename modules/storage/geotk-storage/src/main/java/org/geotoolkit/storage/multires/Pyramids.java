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
import java.util.UUID;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.IncompleteGridGeometryException;
import org.apache.sis.coverage.grid.PixelTranslation;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Static;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.grid.GridGeometryIterator;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Pyramids extends Static {

    /**
     * Additional hint : to specify the mime type.
     */
    public static final String HINT_FORMAT = "format";

    private Pyramids(){}

    public static Envelope getEnvelope(MultiResolutionResource resource) throws DataStoreException {
        for(Pyramid pyramid : getPyramids(resource)){
            //we consider the first pyramid to be in the main data crs
            return pyramid.getEnvelope();
        }
        return null;
    }

    public static Pyramid getPyramid(MultiResolutionResource resource, String pyramidId) throws DataStoreException {
        for (MultiResolutionModel p : resource.getModels()) {
            if (p instanceof Pyramid && p.getIdentifier().equals(pyramidId)) {
                return (Pyramid) p;
            }
        }
        return null;
    }

    public static Mosaic getMosaic(MultiResolutionResource model, String pyramidId, String mosaicId) throws DataStoreException {
        final Pyramid p = getPyramid(model, pyramidId);
        if (p == null) {
            return null;
        }

        for (Mosaic m : p.getMosaics()) {
            if (m.getIdentifier().equals(mosaicId)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Redefine mosaic changing tile size.
     *
     * @param mosaic
     * @param tileSize
     * @return
     */
    public static DefiningMosaic resizeTile(Mosaic mosaic, Dimension tileSize) {
        double scale = mosaic.getScale();
        scale *= mosaic.getTileSize().width;
        scale /= tileSize.width;
        return new DefiningMosaic(mosaic.getIdentifier(), mosaic.getUpperLeftCorner(), scale, tileSize, mosaic.getGridSize());
    }

    /**
     * Delete all tiles in the pyramid intersecting given envelope and resolution range.
     *
     * @param pyramid Pyramid to clean
     * @param env restricted envelope, can be null
     * @param resolutions restricted resolution range
     * @throws DataStoreException
     */
    public static void clear(Pyramid pyramid, Envelope env, NumberRange resolutions) throws DataStoreException {

        if (env != null) {
            try {
                env = Envelopes.transform(env, pyramid.getCoordinateReferenceSystem());
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        for (Mosaic mosaic : pyramid.getMosaics()) {
            if (resolutions == null || resolutions.contains(mosaic.getScale())) {
                final Rectangle rect;
                if (env == null) {
                    rect = new Rectangle(mosaic.getGridSize());
                } else {
                    rect = Pyramids.getTilesInEnvelope(mosaic, env);
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
    public static void copyStructure(Pyramid base, Pyramid target) throws DataStoreException {
        ArgumentChecks.ensureNonNull("base", base);
        ArgumentChecks.ensureNonNull("target", target);
        for (Mosaic m : base.getMosaics()) {
            target.createMosaic(m);
        }
    }

    public static List<Pyramid> getPyramids(MultiResolutionResource resource) throws DataStoreException {
        final List<Pyramid> pyramids = new ArrayList<>();
        for (MultiResolutionModel model : resource.getModels()) {
            if (model instanceof Pyramid) pyramids.add((Pyramid) model);
        }
        return pyramids;
    }

    /**
     * Grid to CRS N dimension. CORNER transform
     *
     * @param mosaic not null
     * @param location not null
     * @return MathTransform never null
     */
    public static LinearTransform getTileGridToCRS(Mosaic mosaic, Point location){
        return getTileGridToCRS(mosaic, location, PixelInCell.CELL_CORNER);
    }

    /**
     * Grid to CRS N dimension. CORNER transform
     *
     * @param mosaic not null
     * @param location not null
     * @param orientation pixel orientation
     * @return MathTransform never null
     */
    public static LinearTransform getTileGridToCRS(Mosaic mosaic, Point location, PixelInCell orientation){
        final DirectPosition upperleft = mosaic.getUpperLeftCorner();
        return getTileGridToCRSND(mosaic, location, upperleft.getDimension(), orientation);
    }

    /**
     * Grid to CRS N dimension. CORNER Transform.
     * This allows to create a transform ignoring last axis transform.
     *
     * @param mosaic not null
     * @param location not null
     * @param nbDim : number of dimension wanted. value must be in range [2...crsNbDim]
     * @return MathTransform never null
     */
    public static LinearTransform getTileGridToCRSND(Mosaic mosaic, Point location, int nbDim){
        return getTileGridToCRSND(mosaic, location, nbDim, PixelInCell.CELL_CORNER);
    }

    /**
     * Grid to CRS N dimension.
     * This allows to create a transform ignoring last axis transform.
     *
     * @param mosaic not null
     * @param location not null
     * @param nbDim : number of dimension wanted. value must be in range [2...crsNbDim]
     * @param orientation pixel orientation
     * @return MathTransform never null
     */
    public static LinearTransform getTileGridToCRSND(Mosaic mosaic, Point location, int nbDim, PixelInCell orientation){

        final AffineTransform2D trs2d = getTileGridToCRS2D(mosaic, location, orientation);
        final DirectPosition upperleft = mosaic.getUpperLeftCorner();

        if(upperleft.getDimension()==2){
            return trs2d;
        }else{
            final int dim = nbDim+1;
            final GeneralMatrix gm = new GeneralMatrix(dim);
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
     * Transform correspond to the CORNER.
     *
     * @param mosaic not null
     * @param location not null
     * @return AffineTransform2D never null.
     */
    public static AffineTransform2D getTileGridToCRS2D(Mosaic mosaic, Point location){
        return getTileGridToCRS2D(mosaic, location, PixelInCell.CELL_CORNER);
    }

    /**
     * Grid to CRS 2D part.
     *
     * @param mosaic not null
     * @param location not null
     * @param orientation pixel orientation
     * @return AffineTransform2D never null.
     */
    public static AffineTransform2D getTileGridToCRS2D(Mosaic mosaic, Point location, PixelInCell orientation){

        final Dimension tileSize = mosaic.getTileSize();
        final DirectPosition upperleft = mosaic.getUpperLeftCorner();
        final double scale = mosaic.getScale();

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
     * @param mosaic
     * @param col
     * @param row
     * @return
     */
    public static Envelope computeTileEnvelope(Mosaic mosaic, final long col, final long row) {
        final Dimension tileSize = mosaic.getTileSize();
        final double scale = mosaic.getScale();
        final GeneralDirectPosition ul = new GeneralDirectPosition(mosaic.getUpperLeftCorner());
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
     * @param mosaic
     * @return
     */
    public static GeneralEnvelope computeMosaicEnvelope(Mosaic mosaic) {
        final Dimension tileSize = mosaic.getTileSize();
        final Dimension gridSize = mosaic.getGridSize();
        final double scale = mosaic.getScale();
        final GeneralDirectPosition ul = new GeneralDirectPosition(mosaic.getUpperLeftCorner());
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
     * @param mosaic searched mosaic
     * @param wantedEnv searched envelope in mosaic {@link CoordinateReferenceSystem}
     * @return
     */
    public static Rectangle getTilesInEnvelope(Mosaic mosaic, Envelope wantedEnv) throws DataStoreException {
        if (wantedEnv == null) {
            return new Rectangle(mosaic.getGridSize());
        }

        final CoordinateReferenceSystem wantedCRS = wantedEnv.getCoordinateReferenceSystem();
        final Envelope mosEnvelope                = mosaic.getEnvelope();

        //-- check CRS conformity
        if (!(Utilities.equalsIgnoreMetadata(wantedCRS, mosEnvelope.getCoordinateReferenceSystem())))
            throw new IllegalArgumentException("the wantedEnvelope is not define in same CRS than mosaic. Expected : "
                                                +mosEnvelope.getCoordinateReferenceSystem()+". Found : "+wantedCRS);

        final DirectPosition upperLeft = mosaic.getUpperLeftCorner();
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
        final Dimension tileSize = mosaic.getTileSize();
//        final double sx          = mosEnv2D.getSpan(0) / (gridSize.width  * tileSize.width);
//        final double sy          = mosEnv2D.getSpan(1) / (gridSize.height * tileSize.height);
//        final double offsetX     = upperLeft.getOrdinate(xAxis);
//        final double offsetY     = upperLeft.getOrdinate(yAxis);

        final MathTransform gridToCrs2D = Pyramids.getTileGridToCRS2D(mosaic, new Point(0, 0), PixelInCell.CELL_CENTER);

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
     * Create a common WGS84 2D pyramid model with a fixed depth.
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
    public static DefiningPyramid createWorldWGS84Template(int maxDepth) {
        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final Dimension tileSize = new Dimension(256, 256);
        final GeneralDirectPosition corner = new GeneralDirectPosition(crs);
        corner.setOrdinate(0, -180.0);
        corner.setOrdinate(1, 90.0);

        final List<Mosaic> mosaics = new ArrayList<>();

        int level = 0;
        final DefiningMosaic level0 = new DefiningMosaic(""+level, corner, 360.0/512.0, tileSize, new Dimension(2, 1));
        mosaics.add(level0);

        level++;
        DefiningMosaic parent = level0;
        for (;level<=maxDepth;level++) {
            final DefiningMosaic levelN = new DefiningMosaic(""+level, corner,
                    parent.getScale() / 2.0, tileSize,
                    new Dimension(parent.getGridSize().width*2, parent.getGridSize().height*2));
            mosaics.add(levelN);
            parent = levelN;
        }

        return new DefiningPyramid("wgs84", "", crs, mosaics);
    }

    /**
     * Create Google Pseudo-Mercator World pyramid.
     *
     * @param maxDepth
     * @return
     */
    public static DefiningPyramid createPseudoMercatorTemplate(int maxDepth) throws FactoryException {
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
     * Create arbitrary template.
     *
     * @param dataEnv
     * @param minResolution
     * @return
     */
    public static DefiningPyramid createTemplate(Envelope dataEnv, double minResolution) {
        final double[] scales = computeScales(dataEnv, minResolution);
        return createTemplate(dataEnv, new Dimension(256, 256), scales);
    }

    /**
     * Create multi dimension template from grid geometry.
     *
     * @param gridGeom reference grid geometry
     * @return
     */
    public static DefiningPyramid createTemplate(GridGeometry gridGeom, Dimension tileSize) throws DataStoreException {
        ArgumentChecks.ensureNonNull("gridGeom", gridGeom);
        return createTemplate(gridGeom, gridGeom.getCoordinateReferenceSystem(), tileSize);
    }

    /**
     * Create multi dimension template from grid geometry.
     *
     * @param gridGeom reference grid geometry
     * @return
     */
    public static DefiningPyramid createTemplate(GridGeometry gridGeom, CoordinateReferenceSystem crs, Dimension tileSize) throws DataStoreException {
        ArgumentChecks.ensureNonNull("gridGeom", gridGeom);
        ArgumentChecks.ensureNonNull("crs", crs);

        final DefiningPyramid pyramid = new DefiningPyramid(crs);

        final GridGeometryIterator ite = new GridGeometryIterator(gridGeom);
        while (ite.hasNext()) {
            final GridGeometry slice = ite.next();
            final Envelope envelope = slice.getEnvelope();

            final DirectPosition upperLeft = new GeneralDirectPosition(crs);
            //-- We found the second horizontale axis dimension.
            final int horizontalOrdinate = CRSUtilities.firstHorizontalAxis(crs);
            for (int d = 0; d < crs.getCoordinateSystem().getDimension(); d++) {
                final double v = (d == horizontalOrdinate+1) ? envelope.getMaximum(d) : envelope.getMinimum(d);
                upperLeft.setOrdinate(d, v);
            }

            final double[] allRes;
            try {
                allRes = slice.getResolution(true);
            } catch (IncompleteGridGeometryException ex) {
                throw new DataStoreException("Mosaic resolution could not be computed");
            }
            if (Double.isNaN(allRes[horizontalOrdinate])) {
                throw new DataStoreException("Horizontal resolution is undefined on axis " + horizontalOrdinate);
            }
            if (Double.isNaN(allRes[horizontalOrdinate+1])) {
                throw new DataStoreException("Horizontal resolution is undefined on axis " + (horizontalOrdinate+1));
            }
            double resolution = Double.min(allRes[horizontalOrdinate], allRes[horizontalOrdinate+1]);

            //find number of tiles needed
            Dimension gridSize = new Dimension();
            final double spanX = envelope.getSpan(horizontalOrdinate);
            final double spanY = envelope.getSpan(horizontalOrdinate+1);
            double nbX = (spanX / resolution) / tileSize.width;
            double nbY = (spanY / resolution) / tileSize.height;
            gridSize.width = (int) Math.ceil(nbX);
            gridSize.height = (int) Math.ceil(nbY);

            final DefiningMosaic m = new DefiningMosaic(UUID.randomUUID().toString(), upperLeft, resolution, tileSize, new Dimension(gridSize));
            pyramid.createMosaic(m);

            //multiply resolution by 2 until we reach a 1x1 grid size
            while (gridSize.width > 1 || gridSize.height > 1) {
                resolution *= 2.0;
                nbX = (spanX / resolution) / tileSize.width;
                nbY = (spanY / resolution) / tileSize.height;
                gridSize.width = (int) Math.ceil(nbX);
                gridSize.height = (int) Math.ceil(nbY);

                final DefiningMosaic m2 = new DefiningMosaic(UUID.randomUUID().toString(), upperLeft, resolution, tileSize, new Dimension(gridSize));
                pyramid.createMosaic(m2);
            }
        }

        return pyramid;
    }

    private static double[] computeScales(Envelope dataEnv, double minResolution) {
        //calculate scales
        final double geospanX = dataEnv.getSpan(0);
        final int tileSize = 256;
        double scale = geospanX / tileSize;
        final GeneralDirectPosition ul = new GeneralDirectPosition(dataEnv.getCoordinateReferenceSystem());
        ul.setOrdinate(0, dataEnv.getMinimum(0));
        ul.setOrdinate(1, dataEnv.getMaximum(1));
        final List<Double> scalesList = new ArrayList<>();
        while (true) {
            scalesList.add(scale);
            if (scale <= minResolution) {
                break;
            }
            scale = scale / 2;
        }
        final double[] scales = new double[scalesList.size()];
        for (int i = 0; i < scales.length; i++) scales[i] = scalesList.get(i);
        return scales;
    }

    public static DefiningPyramid createTemplate(Envelope envelope, Dimension tileSize, double[] scales) {
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();

        final DirectPosition newUpperleft = new GeneralDirectPosition(crs);
        //-- We found the second horizontale axis dimension.
        final int maxHorizOrdinate = CRSUtilities.firstHorizontalAxis(crs) + 1;
        for (int d = 0; d < crs.getCoordinateSystem().getDimension(); d++) {
            final double v = (d == maxHorizOrdinate) ? envelope.getMaximum(d) : envelope.getMinimum(d);
            newUpperleft.setOrdinate(d, v);
        }

        //generate each mosaic
        final List<Mosaic> mosaics = new ArrayList<>();
        for (int i=0;i<scales.length;i++) {
            final double scale = scales[i];
            final double gridWidth  = envelope.getSpan(0) / (scale*tileSize.width);
            final double gridHeight = envelope.getSpan(1) / (scale*tileSize.height);
            final Dimension gridSize = new Dimension( (int)(Math.ceil(gridWidth)), (int)(Math.ceil(gridHeight)));
            final DefiningMosaic levelN = new DefiningMosaic(""+i, newUpperleft, scale, tileSize, gridSize);
            mosaics.add(levelN);
        }

        return new DefiningPyramid("Pyramid", "", crs, mosaics);
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
}
