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

/**
 * PostGIS WKB raster constants.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class WKBRasterConstants {
    
    /** 1-bit boolean            */
    public static final int PT_1BB=0;     
    /** 2-bit unsigned integer   */
    public static final int PT_2BUI=1;    
    /** 4-bit unsigned integer   */
    public static final int PT_4BUI=2;    
    /** 8-bit signed integer     */
    public static final int PT_8BSI=3;  
    /** 8-bit unsigned integer   */
    public static final int PT_8BUI=4;    
    /** 16-bit signed integer    */
    public static final int PT_16BSI=5;   
    /** 16-bit unsigned integer  */
    public static final int PT_16BUI=6;   
    /** 32-bit signed integer    */
    public static final int PT_32BSI=7;   
    /** 32-bit unsigned integer  */
    public static final int PT_32BUI=8;   
    /** 32-bit float             */
    public static final int PT_32BF=10;   
    /** 64-bit float             */
    public static final int PT_64BF=11;   
    public static final int PT_END=13;
    
    public static final int BANDTYPE_FLAGS_MASK = 0xF0;
    public static final int BANDTYPE_PIXTYPE_MASK = 0x0F;
    public static final int BANDTYPE_FLAG_OFFDB = 1<<7;
    public static final int BANDTYPE_FLAG_HASNODATA = 1<<6;
    public static final int BANDTYPE_FLAG_ISNODATA = 1<<5;
    public static final int BANDTYPE_FLAG_RESERVED3 =1<<4;
    
    private WKBRasterConstants(){}
            
}
