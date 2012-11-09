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
package org.geotoolkit.coverage.postgresql.io;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterFactory;
import static org.geotoolkit.coverage.postgresql.io.WKBRasterConstants.*;
import org.geotoolkit.io.LEDataInputStream;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;

/**
 * WKB PostGIS Raster Reader.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class WKBRasterReader {
    
    private AffineTransform2D gridToCRS = null;
    private int srid = 0;
    
    public WKBRasterReader(){
    }
    
    /**
     * Reset values before new read call.
     */
    public void reset(){
        gridToCRS = null;
        srid = 0;
    }
    
    /**
     * Get the Grid to CRS transform, can be called after read only.
     * @return AffineTransform2D
     */
    public AffineTransform2D getGridToCRS() {
        return gridToCRS;
    }
    
    /**
     * Get the postgis srid, can be called after read only.
     * @return int, postgid srid
     */
    public int getSRID(){
        return srid;
    }
    
    /**
     * Parse given byte[] and rebuild RenderedImage.
     * 
     * @param data
     * @return
     * @throws IOException 
     */
    public BufferedImage read(byte[] data) throws IOException{
        final InputStream stream = new ByteArrayInputStream(data);
        return read(stream);
    }
    
    /**
     * Parse given InputStream and rebuild RenderedImage.
     * 
     * @param stream
     * @return
     * @throws IOException 
     */
    public BufferedImage read(final InputStream stream) throws IOException{
                
        final DataInput ds;
        final int endian = stream.read();
        if(endian == 0){
            //big endian
            ds = new DataInputStream(stream);
        }else{
            //little endian
            ds = new LEDataInputStream(stream);
        }
        
        final int version = ds.readUnsignedShort();
        final int nbBand = ds.readUnsignedShort();
        //grid to crs
        final double scaleX = ds.readDouble();
        final double scaleY = ds.readDouble();
        final double ipX = ds.readDouble();
        final double ipY = ds.readDouble();
        final double skewX = ds.readDouble();
        final double skewY = ds.readDouble();
        gridToCRS = new AffineTransform2D(scaleX, skewY, skewX, scaleY, ipX, ipY);
        
        
        srid = ds.readInt();
        final int width = ds.readUnsignedShort();
        final int height = ds.readUnsignedShort();
        
        if(nbBand == 0){
            //possible for empty raster
            return null;
        }
        
        final WKBRasterBand[] bands = new WKBRasterBand[nbBand];
        
        for(int i=0;i<nbBand;i++){
            final WKBRasterBand band = new WKBRasterBand();
            
            final byte b = ds.readByte();
            band.setPixelType(b & BANDTYPE_PIXTYPE_MASK);
            band.setOffDatabase( (b & BANDTYPE_FLAG_OFFDB) != 0);
            band.setHasNodata( (b & BANDTYPE_FLAG_HASNODATA) != 0);
            band.setIsNodata( (b & BANDTYPE_FLAG_ISNODATA) != 0);
            band.setReserved( (b & BANDTYPE_FLAG_RESERVED3) != 0);
            
            /* read nodata value */
            switch (band.getPixelType()) {
                case PT_1BB:
                case PT_2BUI:
                case PT_4BUI:
                case PT_8BUI:
                    band.setNoDataValue(ds.readUnsignedByte());
                    break;
                case PT_8BSI:
                    band.setNoDataValue(ds.readByte());
                    break;
                case PT_16BSI:
                    band.setNoDataValue(ds.readShort());
                    break;
                case PT_16BUI:
                    band.setNoDataValue(ds.readUnsignedShort());
                    break;
                case PT_32BSI:
                    band.setNoDataValue(ds.readInt());
                    break;
                case PT_32BUI:
                    band.setNoDataValue(ds.readInt() & 0x00000000ffffffffL);
                    break;
                case PT_32BF:
                    band.setNoDataValue(ds.readFloat());
                    break;
                case PT_64BF:
                    band.setNoDataValue(ds.readDouble());
                    break;
                default:
                    throw new IOException("unknowned pixel type : "+band.getPixelType());
            }
            
            if(band.isOffDatabase()){
                throw new IOException("can not access data which are off database");
            }else{
                //read values
                final byte[] datas = new byte[width*height*band.getNbBytePerPixel()];
                ds.readFully(datas);
                band.setDatas(datas);
            }
            
            bands[i] = band;
        }
        
        //check all band have the same sample model and rebuild data buffer
        Integer dataType = null;
        final byte[][] dataArray = new byte[nbBand][0];
        final int[] bankIndices = new int[nbBand];
        final int[] bankOffsets = new int[nbBand];
        for(int i=0;i<bands.length;i++){
            final WKBRasterBand band = bands[i];
            if(dataType == null){
                dataType = band.getDataBufferType();
            }else if(dataType != band.getDataBufferType()){
                throw new IOException("Band type differ, can not be mapped to java image.");
            }
            dataArray[i] = band.getDatas();
            bankIndices[i] = i;
            bankOffsets[i] = 0;
        }
        
        //rebuild image
        final DataBuffer db = new DataBufferByte(dataArray, dataArray[0].length);
        final int scanlineStride = width;
        final WritableRaster raster = RasterFactory.createBandedRaster(
                db, width, height, scanlineStride, bankIndices, bankOffsets, new Point(0,0));     
        final SampleModel sm = raster.getSampleModel();
                
        final ColorModel cm = PlanarImage.getDefaultColorModel(sm.getDataType(), raster.getNumBands());        
        return new BufferedImage(cm, raster, false, null);
    }
    
    
}
