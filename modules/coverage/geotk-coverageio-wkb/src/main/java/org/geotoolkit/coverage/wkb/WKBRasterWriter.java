/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.wkb;

import java.awt.geom.AffineTransform;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.io.LEDataOutputStream;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.util.FactoryException;

/**
 * WKB Raster Writer, used in postGIS 2 but can be used elsewhere.
 *
 * @author Johann Sorel (Geomatys)
 */
public class WKBRasterWriter {

    public WKBRasterWriter() {
    }

    /**
     * Reset values before new write call.
     */
    public void reset(){
    }

    /**
     * Encode given coverage in Postgis WKB.
     *
     * @param coverage : grid coverage 2d , not null
     * @return byte[] encoded image
     * @throws IOException
     */
    public byte[] write(final GridCoverage2D coverage) throws IOException, FactoryException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(coverage, out);
        return out.toByteArray();
    }
    
    /**
     * Encode given coverage in Postgis WKB.
     *
     * @param coverage : grid coverage 2d , not null
     * @param stream : output stream to write in
     * @throws IOException
     */
    public void write(final GridCoverage2D coverage, final OutputStream stream) throws IOException, FactoryException {
        write(coverage, stream, true);
    }
    
    /**
     * Encode given coverage in Postgis WKB.
     *
     * @param coverage : grid coverage 2d , not null
     * @param stream : output stream to write in
     * @param littleEndian : wanted value encoding
     * @throws IOException
     */
    public void write(final GridCoverage2D coverage, final OutputStream stream, final boolean littleEndian) 
            throws IOException, FactoryException {
        final CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem2D();
        final Integer srid = IdentifiedObjects.lookupEpsgCode(crs, true);
        if(srid == null){
            throw new IOException("CoordinateReferenceSystem does not have an EPSG code.");
        }
        final MathTransform2D gridToCRS = coverage.getGridGeometry().getGridToCRS2D();
        if(!(gridToCRS instanceof AffineTransform)){
            throw new IOException("Coverage GridToCRS transform is not affine.");
        }
        final RenderedImage image = coverage.getRenderedImage();
        
        write(image, (AffineTransform)gridToCRS, srid, stream);
    }
    
    /**
     * Encode given image in Postgis WKB.
     *
     * @param image : image , not null
     * @param gridToCRS : image grid to crs, can be null
     * @param srid : image srid
     * @return byte[] encoded image
     * @throws IOException
     */
    public byte[] write(final RenderedImage image, final AffineTransform gridToCRS,
            final int srid) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(image, gridToCRS, srid, out);
        return out.toByteArray();
    }

    /**
     * Encode given image in Postgis WKB.
     *
     * @param image : image , not null
     * @param gridToCRS : image grid to crs, can be null
     * @param srid : image srid
     * @param stream : output stream to write in
     * @throws IOException
     */
    public void write(final RenderedImage image, AffineTransform gridToCRS,
            final int srid, final OutputStream stream) throws IOException {
        write(image, gridToCRS, srid, stream, true);
    }

    /**
     * Encode given image in Postgis WKB.
     *
     * @param image : image , not null
     * @param gridToCRS : image grid to crs, can be null
     * @param srid : image srid
     * @param stream : output stream to write in
     * @param littleEndian : wanted value encoding
     * @throws IOException
     */
    public void write(final RenderedImage image, AffineTransform gridToCRS,
            final int srid, final OutputStream stream, final boolean littleEndian) throws IOException {
        if(gridToCRS == null){
            gridToCRS = new AffineTransform();
        }

        final DataOutput ds;
        if(littleEndian){
            ds = new LEDataOutputStream(stream);
        }else{
            ds = new DataOutputStream(stream);
        }

        final SampleModel sm = image.getSampleModel();
        final Raster raster = image.getData();
        final int nbBand = sm.getNumBands();
        final int width = image.getWidth();
        final int height = image.getHeight();
        int databufferType = sm.getDataType();
        if(databufferType == DataBuffer.TYPE_INT){
            //special case, most image compression bands on single int when it would be byte
            if(sm.getSampleSize()[0] <= 8){
                databufferType = DataBuffer.TYPE_BYTE;
            }
        }

        final int pixelType = WKBRasterConstants.getPixelType(databufferType);
        final int bytePerpixel = WKBRasterConstants.getNbBytePerPixel(pixelType);

        //endianess
        ds.write( littleEndian ? 1 : 0 );
        //version, 0 for now
        ds.writeShort(0);
        //number of bands
        ds.writeShort(nbBand);
        //grid to crs
        ds.writeDouble(gridToCRS.getScaleX());
        ds.writeDouble(gridToCRS.getScaleY());
        ds.writeDouble(gridToCRS.getTranslateX());
        ds.writeDouble(gridToCRS.getTranslateY());
        ds.writeDouble(gridToCRS.getShearX());
        ds.writeDouble(gridToCRS.getShearY());
        //write srid
        ds.writeInt(srid);
        //width and height
        ds.writeShort(width);
        ds.writeShort(height);

        //write each band
        for(int b=0;b<nbBand;b++){

            // band description
            final byte flags = (byte) pixelType;
            // OffDatabase = false
            // TODO HasNodata : we don't have informations for no data
            //      this would requiere a SampleDimension object
            // IsNodata = false
            // Reserved = false
            ds.write(flags);

            // TODO no data value
            ds.write(new byte[bytePerpixel]);

            //write values
            for(int y=raster.getMinY(),maxy=raster.getMinY()+height;y<maxy;y++){
                for(int x=raster.getMinX(),maxx=raster.getMinX()+width;x<maxx;x++){
                    switch(databufferType){
                        case DataBuffer.TYPE_BYTE :     ds.writeByte( (byte)raster.getSample(x, y, b)); break;
                        case DataBuffer.TYPE_SHORT :    ds.writeShort( (short)raster.getSample(x, y, b)); break;
                        case DataBuffer.TYPE_USHORT :   ds.writeShort( (short)raster.getSample(x, y, b)); break;
                        case DataBuffer.TYPE_INT :      ds.writeInt( raster.getSample(x, y, b)); break;
                        case DataBuffer.TYPE_FLOAT :    ds.writeFloat( raster.getSampleFloat(x, y, b)); break;
                        case DataBuffer.TYPE_DOUBLE :   ds.writeDouble( raster.getSampleDouble(x, y, b)); break;
                    }
                }
            }

        }

    }

}
