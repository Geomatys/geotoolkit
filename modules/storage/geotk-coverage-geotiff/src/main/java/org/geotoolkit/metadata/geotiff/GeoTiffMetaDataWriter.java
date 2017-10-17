/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.metadata.geotiff;


import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.image.io.ImageMetadataException;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.Georectified;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.internal.image.io.DimensionAccessor;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.w3c.dom.Node;

import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.util.Utilities;
import org.opengis.referencing.crs.SingleCRS;

/**
 *
 * @author Johann Sorel  (Geomatys)
 * @author Marechal Remi (Geomatys)
 * @module
 */
public class GeoTiffMetaDataWriter {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.metadata.geotiff");

    public GeoTiffMetaDataWriter(){}

    /**
     * Complete the TIFF metadata tree with geotiff informations.
     */
    public void fillMetadata(Node tiffTree, final SpatialMetadata spatialMD) throws ImageMetadataException, IOException, FactoryException, TransformException{
        ArgumentChecks.ensureNonNull("tiffTree", tiffTree);
        ArgumentChecks.ensureNonNull("spatialMD", spatialMD);

        //container for informations which will be written
        final GeoTiffMetaDataStack stack = new GeoTiffMetaDataStack(tiffTree);

        //fill geotiff crs information
        final CoordinateReferenceSystem coverageCRS = spatialMD.getInstanceForType(CoordinateReferenceSystem.class);
        final GeoTiffCRSWriter crsWriter = new GeoTiffCRSWriter();
        crsWriter.fillCRSMetaDatas(stack, CRSUtilities.getCRS2D(coverageCRS));

        //fill the transformation information
        final RectifiedGrid domain = spatialMD.getInstanceForType(RectifiedGrid.class);
        AffineTransform gridToCrs = MetadataHelper.INSTANCE.getAffineTransform(domain, null);

        //readjust gridToCRS to be the pixel corner
        final Georectified georect = spatialMD.getInstanceForType(Georectified.class);
        final CellGeometry cell = georect.getCellGeometry();
        final PixelOrientation orientation = georect.getPointInPixel();

        /*
         * FAQ GEOTIFF :
         * Setting the GTRasterTypeGeoKey value to RasterPixelIsPoint or RasterPixelIsArea
         * alters how the raster coordinate space is to be interpreted.
         * This is defined in section 2.5.2.2 of the GeoTIFF specification.
         * => In the case of PixelIsArea (default) a pixel is treated as an area
         * and the raster coordinate (0,0) is the top left corner of the top left pixel.
         * => PixelIsPoint treats pixels as point samples with empty space between the "pixel" samples.
         * In this case raster (0,0) is the location of the top left raster pixel.
         *
         * Note : GeoTiff mix the concepts of CellGeometry and PixelOrientation.
         */

        if(CellGeometry.POINT.equals(cell)){
            stack.addShort(GTRasterTypeGeoKey, RasterPixelIsPoint);

            if(!orientation.equals(PixelOrientation.CENTER)){
                AffineTransform2D trs = new AffineTransform2D(gridToCrs);
                gridToCrs = (AffineTransform)PixelTranslation.translate(trs, orientation, PixelOrientation.CENTER,0,1);
            }

        }else{ //consider all other as Area
            stack.addShort(GTRasterTypeGeoKey, RasterPixelIsArea);

            if(!orientation.equals(PixelOrientation.UPPER_LEFT)){
                AffineTransform2D trs = new AffineTransform2D(gridToCrs);
                gridToCrs = (AffineTransform)PixelTranslation.translate(trs, orientation, PixelOrientation.UPPER_LEFT,0,1);
            }
        }

        //-- find a date from crs
        final int tempOrdinate = getTemporalOrdinate(coverageCRS);
        if (tempOrdinate >= 0) {
            //-- add temporal tag
            final GridDomainAccessor gda = new GridDomainAccessor(spatialMD);
            final double[] origin        = gda.getAttributeAsDoubles("origin", false);
            final double date            = origin[tempOrdinate];
            final Date dat               = DefaultTemporalCRS.castOrCopy(CommonCRS.Temporal.JAVA.crs()).toDate(date);
            stack.setDate(dat);
        }

        fillTransform(stack, gridToCrs, domain.getExtent());

        //fill NoData values
        fillSampleDimensionProperties(stack, spatialMD);

        //write in the metadata tree
        stack.flush();
    }

    /**
     * Return the temporal ordinate from {@link CoordinateReferenceSystem} or -1 if temporal crs was not found.
     *
     * @return temporal ordinate if exist else return -1.
     */
    private int getTemporalOrdinate(final CoordinateReferenceSystem crs) {
        final List<SingleCRS> crss = CRS.getSingleComponents(crs);
        int o = 0;
        for (final SingleCRS c : crss) {
            if (Utilities.equalsIgnoreMetadata(c, CommonCRS.Temporal.JAVA.crs())) return o;
            o += c.getCoordinateSystem().getDimension();
        }
        return -1;
    }

    /**
     * Fill metadata tree with {@link GridSampleDimension} properties like noData
     * or minimum and maximum sample values.<br><br>
     *
     * Note : more informations at about tiff specification :<br>
     * http://www.awaresystems.be/imaging/tiff/tifftags/minsamplevalue.html<br>
     * http://www.awaresystems.be/imaging/tiff/tifftags/maxsamplevalue.html<br>
     * http://www.awaresystems.be/imaging/tiff/tifftags/gdal_nodata.html<br>
     *
     * @param stack
     * @param spatialMD metadata tree which will be filled.
     * @see GeoTiffMetaDataStack#setMinSampleValue(int...)
     * @see GeoTiffMetaDataStack#setMaxSampleValue(int...)
     * @see GeoTiffMetaDataStack#setNoData(java.lang.String)
     */
    private void fillSampleDimensionProperties(final GeoTiffMetaDataStack stack, final SpatialMetadata spatialMD) {
        ArgumentChecks.ensureNonNull("stack", stack);
        ArgumentChecks.ensureNonNull("spatialMD", spatialMD);
        final DimensionAccessor accessor                 = new DimensionAccessor(spatialMD);
        final List<GridSampleDimension> sampleDimensions = accessor.getGridSampleDimensions();

        if (sampleDimensions == null) {
            LOGGER.log(Level.FINE, "GeotiffMetadataWriter : no gridSampleDimension setted into spatialMetadata.");
            return;
        }

        final int sampleDimensionNumber = sampleDimensions.size();

        double[] noData   = null;
        final int[] minSV = new int[sampleDimensionNumber];
        final int[] maxSV = new int[sampleDimensionNumber];
        int minSVId       = 0;
        int maxSVId       = 0;

        if (sampleDimensions != null && !sampleDimensions.isEmpty()) {
            for (GridSampleDimension dimension : sampleDimensions) {

                //-- min samplevalue
                final double minSVd = dimension.getMinimumValue();
                if (checkDoubleToShort(minSVd)) minSV[minSVId++] = (short) minSVd;

                //-- maxSampleValue
                final double maxSVd = dimension.getMaximumValue();
                if (checkDoubleToShort(maxSVd)) maxSV[maxSVId++] = (short) maxSVd;

                final double[] dimNoData = dimension.getNoDataValues();
                if (noData == null) {
                    noData = dimNoData;
                } else {
                    if (!Arrays.equals(noData, dimNoData)) {
                        LOGGER.warning("Unable to fill Geotiff nodata tag cause : all bands must use the same nodata values."+
                        "expected : "+Arrays.toString(noData)+" found : "+Arrays.toString(dimNoData));
                        return;
                    }
                }
            }
        }

        //-- if all bands are stipulate
        if (minSVId == sampleDimensionNumber) stack.setMinSampleValue(minSV);


        //-- if all bands are stipulate
        if (maxSVId == sampleDimensionNumber) stack.setMaxSampleValue(maxSV);


        if (noData != null && noData.length > 0)
            for (double d : noData)
                stack.setNoData(String.valueOf(d));
     }

    /**
     * Returns {@code true} if {@code double} value may be cast to {@code short} and lost nothing.<br><br>
     *
     * Note : Tiff specification only accept short value to stipulate minimum or maximum sample value for each bands.<br>
     * For more explanations see : http://www.awaresystems.be/imaging/tiff/tifftags/minsamplevalue.html <br>
     * and http://www.awaresystems.be/imaging/tiff/tifftags/maxsamplevalue.html
     *
     * @param value double which will be cast.
     * @return {@code true} if {@code double} value may be cast to {@code short}
     * @see GeoTiffMetaDataStack#setMinSampleValue(int...)
     * @see GeoTiffMetaDataStack#setMaxSampleValue(int...)
     */
    private boolean checkDoubleToShort(final double value) {
        final short st = (short) value;
        return ((double) st) == value;
    }

    private void fillTransform(final GeoTiffMetaDataStack stack,
            final AffineTransform gridToCRS, final GridEnvelope range) {

        stack.setModelTransformation(gridToCRS);

        // /////////////////////////////////////////////////////////////////////
        // We have to set an affine transformation which is going to be 2D
        // since we support baseline GeoTiff.
        // /////////////////////////////////////////////////////////////////////
        final AffineTransform modifiedRasterToModel;
        final int minx = range.getLow(0);
        final int miny = range.getLow(1);
        if (minx != 0 || miny != 0) {
            // //
            // Preconcatenate a transform to have raster space beginning at (0,0)
            // //
            modifiedRasterToModel = new AffineTransform(gridToCRS);
            modifiedRasterToModel.concatenate(AffineTransform.getTranslateInstance(minx, miny));
        } else {
            modifiedRasterToModel = gridToCRS;
        }


        // /////////////////////////////////////////////////////////////////////
        // AXES DIRECTION
        // we need to understand how the axes of this gridcoverage are
        // specified, trying to understand the direction of the first axis in
        // order to correctly use transformations.
        //
        // Note that here wew assume that in case of a Flip the flip is on the Y
        // axis.
        // /////////////////////////////////////////////////////////////////////
        final boolean lonFirst = AffineTransforms2D.getSwapXY(modifiedRasterToModel) != -1;

        // /////////////////////////////////////////////////////////////////////
        // ROTATION
        // If fthere is not rotation or shearing or flipping we have a simple
        // scale and translate hence we can simply set the tie points.
        // /////////////////////////////////////////////////////////////////////
        final double rotation = AffineTransforms2D.getRotation(modifiedRasterToModel);

        // /////////////////////////////////////////////////////////////////////
        // Deciding how to save the georef with respect to the CRS.
        // /////////////////////////////////////////////////////////////////////
        // tie points
        if (!(Double.isInfinite(rotation) || Double.isNaN(rotation) || Math.abs(rotation) > 1E-6)) {
            final double tiePointLongitude = (lonFirst) ? modifiedRasterToModel.getTranslateX() : modifiedRasterToModel.getTranslateY();
            final double tiePointLatitude = (lonFirst) ? modifiedRasterToModel.getTranslateY() : modifiedRasterToModel.getTranslateX();
            stack.addModelTiePoint(new TiePoint(0, 0, 0, tiePointLongitude, tiePointLatitude, 0));
            // scale
            final double scaleModelToRasterLongitude = (lonFirst) ? Math.abs(modifiedRasterToModel.getScaleX()) : Math.abs(modifiedRasterToModel.getShearY());
            final double scaleModelToRasterLatitude = (lonFirst) ? Math.abs(modifiedRasterToModel.getScaleY()) : Math.abs(modifiedRasterToModel.getShearX());
            stack.setModelPixelScale(scaleModelToRasterLongitude, scaleModelToRasterLatitude, 0);
            // Alternative code, not yet enabled in order to avoid breaking
            // code.
            // The following code is insensitive to axis order and rotations in
            // the 'coord' space (not in the 'grid' space, otherwise we would
            // not take the inverse of the matrix).
            /*
             * final AffineTransform coordToGrid = gridToCoord.createInverse();
             * final double scaleModelToRasterLongitude = 1 /
             * AffineTransforms2D.getScaleX0(coordToGrid); final double
             * scaleModelToRasterLatitude = 1 /
             * AffineTransforms2D.getScaleY0(coordToGrid);
             */
        } else {
            stack.setModelTransformation(modifiedRasterToModel);
        }
    }
}
