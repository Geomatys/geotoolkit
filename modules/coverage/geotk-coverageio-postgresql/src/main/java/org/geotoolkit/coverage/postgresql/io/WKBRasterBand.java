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

import java.awt.image.DataBuffer;
import static org.geotoolkit.coverage.postgresql.io.WKBRasterConstants.*;

/**
 * PostGIS Raster band.
 * 
 * @author Johann Sorel (Geomatys)
 */
class WKBRasterBand {
    
    private int pixelType;
    private boolean offdatabase;
    private boolean hasnodata;
    private boolean isnodata;
    private boolean reserved;
    private Number noDataValue;
    private byte[] datas;

    public WKBRasterBand() {
    }

    public int getPixelType() {
        return pixelType;
    }

    public void setPixelType(int pixelType) {
        this.pixelType = pixelType;
    }

    public boolean isOffDatabase() {
        return offdatabase;
    }

    public void setOffDatabase(boolean offdatabase) {
        this.offdatabase = offdatabase;
    }

    public boolean hasNodata() {
        return hasnodata;
    }

    public void setHasNodata(boolean hasnodata) {
        this.hasnodata = hasnodata;
    }

    public boolean isNodata() {
        return isnodata;
    }

    public void setIsNodata(boolean isnodata) {
        this.isnodata = isnodata;
    }

    public boolean getReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public void setNoDataValue(Number noDataValue) {
        this.noDataValue = noDataValue;
    }

    public Number getNoDataValue() {
        return noDataValue;
    }

    public void setDatas(byte[] datas) {
        this.datas = datas;
    }

    public byte[] getDatas() {
        return datas;
    }
    
    public int getNbBytePerPixel() {
        switch (pixelType) {
            case PT_1BB:
            case PT_2BUI:
            case PT_4BUI:
            case PT_8BUI:
            case PT_8BSI:
                return 1;
            case PT_16BSI:
            case PT_16BUI:
                return 2;
            case PT_32BSI:
            case PT_32BUI:
            case PT_32BF:
                return 4;
            case PT_64BF:
                return 8;
            default:
                throw new IllegalArgumentException("unknowned pixel type : " + pixelType);
        }
    }
    
    public int getDataBufferType(){
        switch (pixelType) {
            case PT_1BB:
            case PT_2BUI:
            case PT_4BUI:
            case PT_8BUI:
            case PT_8BSI:
                return DataBuffer.TYPE_BYTE;
            case PT_16BSI:
                return DataBuffer.TYPE_USHORT;
            case PT_16BUI:
                return DataBuffer.TYPE_SHORT;
            case PT_32BSI:
                return DataBuffer.TYPE_INT;
            case PT_32BUI:
                return DataBuffer.TYPE_INT;
            case PT_32BF:
                return DataBuffer.TYPE_FLOAT;
            case PT_64BF:
                return DataBuffer.TYPE_DOUBLE;
            default:
                throw new IllegalArgumentException("unknowned pixel type : " + pixelType);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WKB Raster Band :");
        sb.append("\n- pixel type : ").append(pixelType);
        sb.append("\n- offdatabase : ").append(offdatabase);
        sb.append("\n- hasnodata : ").append(hasnodata);
        sb.append("\n- is no data : ").append(isnodata);
        sb.append("\n- reserved : ").append(reserved);
        sb.append("\n- no data value : ").append(noDataValue);
        return sb.toString();
    }

    
}
