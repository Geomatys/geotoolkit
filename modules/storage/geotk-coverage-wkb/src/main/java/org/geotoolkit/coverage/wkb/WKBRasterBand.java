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

/**
 * WKB Raster band, used in postGIS 2 but can be used elsewhere.
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
        return WKBRasterConstants.getNbBytePerPixel(pixelType);
    }

    public int getDataBufferType(){
        return WKBRasterConstants.getDataBufferType(pixelType);
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
