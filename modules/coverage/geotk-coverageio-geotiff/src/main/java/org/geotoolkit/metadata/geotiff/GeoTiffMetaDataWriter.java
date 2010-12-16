/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import org.geotoolkit.image.io.ImageMetadataException;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.Georectified;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import org.w3c.dom.Node;

import static org.geotoolkit.metadata.geotiff.GeoTiffConstants.*;

/**
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GeoTiffMetaDataWriter {

    public GeoTiffMetaDataWriter(){}

    /**
     * Complete the TIFF metadata tree with geotiff informations.
     */
    public void fillMetadata(Node tiffTree, SpatialMetadata spatialMD) throws ImageMetadataException, IOException, FactoryException{

        //container for informations which will be written
        final GeoTiffMetaDataStack stack = new GeoTiffMetaDataStack(tiffTree);

        //fill geotiff crs information
        final CoordinateReferenceSystem coverageCRS = spatialMD.getInstanceForType(CoordinateReferenceSystem.class);
        final GeoTiffCRSWriter crsWriter = new GeoTiffCRSWriter();
        crsWriter.fillCRSMetaDatas(stack, coverageCRS);

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

        fillTransform(stack, gridToCrs, domain.getExtent());

        //write in the metadata tree
        stack.flush();
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
        // Setting raster type to pixel centre since the ogc specifications
        // require so.
        // /////////////////////////////////////////////////////////////////////
        stack.addShort(GTRasterTypeGeoKey, RasterPixelIsPoint);

        // /////////////////////////////////////////////////////////////////////
        // AXES DIRECTION
        // we need to understand how the axes of this gridcoverage are
        // specified, trying to understand the direction of the first axis in
        // order to correctly use transformations.
        //
        // Note that here wew assume that in case of a Flip the flip is on the Y
        // axis.
        // /////////////////////////////////////////////////////////////////////
        final boolean lonFirst = XAffineTransform.getSwapXY(modifiedRasterToModel) != -1;

        // /////////////////////////////////////////////////////////////////////
        // ROTATION
        // If fthere is not rotation or shearing or flipping we have a simple
        // scale and translate hence we can simply set the tie points.
        // /////////////////////////////////////////////////////////////////////
        final double rotation = XAffineTransform.getRotation(modifiedRasterToModel);

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
             * XAffineTransform.getScaleX0(coordToGrid); final double
             * scaleModelToRasterLatitude = 1 /
             * XAffineTransform.getScaleY0(coordToGrid);
             */
        } else {
            stack.setModelTransformation(modifiedRasterToModel);
        }
    }

}
