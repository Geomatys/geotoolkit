
package org.geotoolkit.coverage;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.concurrent.CancellationException;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageWriteParam;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.SphericalCS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 */
public class PyramidalModelWriter extends GridCoverageWriter {
    private final CoverageReference reference;

    /**
     * Build a writer on an existing {@linkplain CoverageReference coverage reference}.
     *
     * @param reference A valid {@linkplain CoverageReference coverage reference}.
     *                  Should not be {@code null} and an instance of {@link PyramidalModel}.
     * @throws IllegalArgumentException if the given {@linkplain CoverageReference coverage reference}
     *                                  is not an instance of {@link PyramidalModel}.
     */
    public PyramidalModelWriter(final CoverageReference reference) {
        if (!(reference instanceof PyramidalModel)) {
            throw new IllegalArgumentException("Given coverage reference should be an instance of PyramidalModel!");
        }
        this.reference = reference;
    }

    /**
     * Write a {@linkplain GridCoverage grid coverage} into the coverage store pointed by the
     * {@link CoverageReference coverage reference}.
     *
     * @param coverage {@link GridCoverage} to write.
     * @param param Writing parameters for the given coverage. Should not be {@code null}, and
     *              should contain a valid envelope.
     * @throws CoverageStoreException if something goes wrong during writing.
     * @throws CancellationException never thrown in this implementation.
     */
    @Override
    public void write(GridCoverage coverage, final GridCoverageWriteParam param) throws CoverageStoreException, CancellationException {
        // todo : algorithm in multi-dimensional
        ArgumentChecks.ensureNonNull("param", param);
        if (coverage == null) {
            return;
        }
        //geographic area where pixel values changes.
        Envelope requestedEnvelope = param.getEnvelope();
        ArgumentChecks.ensureNonNull("requestedEnvelope", requestedEnvelope);
        final CoordinateReferenceSystem crsCoverage = coverage.getCoordinateReferenceSystem();

        final CoordinateReferenceSystem envelopeCrs = requestedEnvelope.getCoordinateReferenceSystem();
        if (!CRS.equalsIgnoreMetadata(crsCoverage, envelopeCrs)) {
            try {
                requestedEnvelope = CRS.transform(requestedEnvelope, crsCoverage);
            } catch (TransformException ex) {
                throw new CoverageStoreException(ex);
            }
        }

        //source image CRS to grid
        final MathTransform srcCRSToGrid;
        RenderedImage image = null;
        try {
            if (coverage instanceof GridCoverage2D) {
                image = ((GridCoverage2D)coverage).getRenderedImage();
                srcCRSToGrid = ((GridCoverage2D)coverage).getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER).inverse();
            } else {
                final GridCoverageBuilder gcb = new GridCoverageBuilder();
                gcb.setGridCoverage(coverage);
                gcb.setPixelAnchor(PixelInCell.CELL_CENTER);
                image = gcb.getRenderedImage();
                srcCRSToGrid = gcb.getGridToCRS().inverse();
            }
        } catch (NoninvertibleTransformException ex) {
            throw new CoverageStoreException(ex);
        }

        // interpolation neighbourg to find pixel values changed.
        final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(image), InterpolationCase.NEIGHBOR, 2);
        //to fill value table : see resample.
        final int nbBand = image.getSampleModel().getNumBands();
        final PyramidalModel pm = (PyramidalModel)reference;
        final PyramidSet pyramidSet;
        try {
            pyramidSet = pm.getPyramidSet();
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex);
        }

        for (Pyramid pyramid : pyramidSet.getPyramids()) {
            //define CRS and mathTransform from current pyramid to source coverage.
            final CoordinateReferenceSystem destCrs;
            final MathTransform crsDestToCrsCoverage;
            final Envelope pyramidEnvelope;
            try {
                destCrs = CRSUtilities.getCRS2D(pyramid.getCoordinateReferenceSystem());
                crsDestToCrsCoverage = CRS.findMathTransform(destCrs, CRSUtilities.getCRS2D(crsCoverage));
                //geographic
                pyramidEnvelope = CRS.transform(requestedEnvelope, destCrs);
            } catch (Exception ex) {
                throw new CoverageStoreException(ex);
            }

            final MathTransform crsDestToSrcGrid = MathTransforms.concatenate(crsDestToCrsCoverage, srcCRSToGrid);

            for (GridMosaic mosaic : pyramid.getMosaics()) {

                final double res = mosaic.getScale();
                final DirectPosition moUpperLeft = mosaic.getUpperLeftCorner();
                // define geographic intersection
                final Envelope mosEnv = mosaic.getEnvelope();
                final GeneralEnvelope mosEnvelope = new GeneralEnvelope(destCrs);
                final int minOrdinate = getMinOrdinate(mosEnv.getCoordinateReferenceSystem());
                mosEnvelope.setEnvelope(mosEnv.getMinimum(minOrdinate), mosEnv.getMinimum(minOrdinate+1), mosEnv.getMaximum(minOrdinate), mosEnv.getMaximum(minOrdinate+1));
                if (!mosEnvelope.intersects(pyramidEnvelope, false)) {
                    throw new CoverageStoreException("envelope from GridCoverageWriteParam object doesn't intersect mosaic envelope");
                }

                final GeneralEnvelope intersection = new GeneralEnvelope(pyramidEnvelope);
                intersection.intersect(mosEnvelope);
                //define pixel work area of current mosaic.
                final int mosAreaX      = (int)Math.round(Math.abs((moUpperLeft.getOrdinate(0)-intersection.getMinimum(0))) /res);
                final int mosAreaY      = (int)Math.round(Math.abs((moUpperLeft.getOrdinate(1)-intersection.getMaximum(1))) /res);
                final int mosAreaWidth  = (int)(intersection.getSpan(0)/res);
                final int mosAreaHeight = (int)(intersection.getSpan(1)/res);

                // mosaic tiles properties.
                final Dimension moSize   = mosaic.getGridSize();
                final Dimension tileSize = mosaic.getTileSize();
                final int tileWidth      = tileSize.width;
                final int tileHeight     = tileSize.height;

                // define intersection
                final int iminx = Math.max(0, mosAreaX);
                final int iminy = Math.max(0, mosAreaY);
                final int imaxx = Math.min(moSize.width  * tileWidth,  mosAreaX + mosAreaWidth);
                final int imaxy = Math.min(moSize.height * tileHeight, mosAreaY + mosAreaHeight);

                // define tiles indexes from current mosaic which will be changed.
                final int idminx = iminx / tileWidth;
                final int idminy = iminy / tileHeight;
                final int idmaxx = (imaxx + tileWidth-1) / tileWidth;
                final int idmaxy = (imaxy + tileHeight - 1) / tileHeight;

                //define destination grid to CRS.
                final MathTransform gridToCrsDest = new AffineTransform2D(res, 0, 0, -res, moUpperLeft.getOrdinate(0) + 0.5, moUpperLeft.getOrdinate(1) - 0.5);
                final MathTransform gridToCrsCoverage = MathTransforms.concatenate(gridToCrsDest, crsDestToSrcGrid);

                // browse selected tile.
                for (int idy = idminy; idy < idmaxy; idy++) {
                    for (int idx = idminx; idx < idmaxx; idx++) {
                        final BufferedImage currentlyTile;
                        try {
                            currentlyTile = mosaic.getTile(idx, idy, null).getImageReader().read(0);
                        } catch (Exception ex) {
                            throw new CoverageStoreException(ex);
                        }

                        // define tile translation from bufferedImage min pixel position to mosaic pixel position.
                        final int minidx = idx * tileWidth;
                        final int minidy = idy * tileHeight;
                        final MathTransform destImgToGrid        = new AffineTransform2D(1, 0, 0, 1, minidx, minidy);
                        final MathTransform destImgToCrsCoverage = MathTransforms.concatenate(destImgToGrid, gridToCrsCoverage);

                        // define currently tile work area.
                        final int tminx = Math.max(iminx, minidx);
                        final int tminy = Math.max(iminy, minidy);
                        final int tmaxx = Math.min(imaxx, minidx + tileWidth);
                        final int tmaxy = Math.min(imaxy, minidy + tileHeight);
                        final Rectangle tileAreaWork = new Rectangle(tminx-minidx, tminy-minidy, tmaxx-tminx, tmaxy-tminy);

                        try {
                            final Resample resample = new Resample(destImgToCrsCoverage.inverse(), currentlyTile, tileAreaWork, interpolation, new double[nbBand]);
                            resample.fillImage();
                            pm.writeTile(pyramid.getId(), mosaic.getId(), idx, idy, currentlyTile);
                        } catch (Exception ex) {
                            throw new CoverageStoreException(ex);
                        }
                    }
                }
            }
        }
    }

    /**
     * Return min geographic index ordinate from {@link CoordinateReferenceSystem} 2d part.
     *
     * @param crs
     * @return
     */
    private int getMinOrdinate(final CoordinateReferenceSystem crs) {
        int tempOrdinate = 0;
        for(CoordinateReferenceSystem ccrrss : ReferencingUtilities.decompose(crs)) {
            final CoordinateSystem cs = ccrrss.getCoordinateSystem();
            if((cs instanceof CartesianCS)
            || (cs instanceof SphericalCS)
            || (cs instanceof EllipsoidalCS)) return tempOrdinate;
            tempOrdinate += cs.getDimension();
        }
        throw new IllegalArgumentException("crs doesn't have any geoghaphic crs");
    }
}
