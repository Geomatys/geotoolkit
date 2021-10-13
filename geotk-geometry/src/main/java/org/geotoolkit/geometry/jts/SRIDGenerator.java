/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.geometry.jts;

import java.util.Set;

import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;

import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Utility class that can generate a SRID from a Coordinate Reference System.
 * this ability is needed since JTS geometry only handle a integer id for crs.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class SRIDGenerator {

    public enum Version{

        V1( (byte)1 );

        final byte code;

        Version(final byte code){
            this.code = code;
        }

    };

    private static final String AUTHORITY_CRS = "CRS";
    private static final String AUTHORITY_EPSG = "EPSG";

    private SRIDGenerator() {
    }

    /**
     * Generate a unique int number for a knowned coordinate reference system.
     * The first 4 bits of the result integer are holding the authority code.
     * The remaining bits hold the code number of the crs within the authority.
     *
     * exemple : version = COMPACT_V1
     * first 4 bits = 0 > EPSG
     * first 4 bits = 1 > CRS
     *
     * return int , 0 if no srid could be generated because the crs has no identifiers.
     */
    public static int toSRID(final CoordinateReferenceSystem crs, final Version version){
        final Set<Identifier> ids = crs.getIdentifiers();

        if(ids.isEmpty()){
            return 0;
//            throw new IllegalArgumentException("CoordinateReferenceSystem has not identifier, impossible to compact it.");
        }

        final Identifier id = ids.iterator().next();
        final String authority = id.getCodeSpace();
        final int code = Integer.valueOf(id.getCode());
        final int authorityCode;

        if(version == Version.V1){
            if(AUTHORITY_EPSG.equalsIgnoreCase(authority)){
                authorityCode = 0;
            }else if(AUTHORITY_CRS.equalsIgnoreCase(authority)){
                authorityCode = 1;
            }else{
                throw new IllegalArgumentException("unknowed authority : " + authority);
            }
            int compact = (authorityCode << 28) | code ;
            return compact;
        }

        throw new IllegalArgumentException("unknowed compact version : " + version);
    }

    /**
     * Generate a unique int number for a knowed coordinate reference system.
     * @param  srs : String presenting the crs code.
     * exemple : EPSG:4326 or CRS:84
     */
    public static int toSRID(final String srs, final Version version){
        final int index = srs.lastIndexOf(':');
        if(index <= 0){
            throw new IllegalArgumentException("CRS code doesnt match pattern Authority:Code : " + srs );
        }

        final String authority = srs.substring(0, index);
        int code = Integer.parseInt(srs.substring(index+1, srs.length()));
        final int authorityCode;

        if(version == Version.V1){
            if(AUTHORITY_EPSG.equalsIgnoreCase(authority)){
                authorityCode = 0;
            }else if(AUTHORITY_CRS.equalsIgnoreCase(authority)){
                authorityCode = 1;
            }else{
                try {
                    CoordinateReferenceSystem crs = CRS.forCode(srs);
                    Integer epsgCode = IdentifiedObjects.lookupEPSG(crs);
                    if (epsgCode != null) {
                        authorityCode = 0;
                        code = epsgCode;
                    } else {
                        throw new IllegalArgumentException("unknowed authority : " + authority);
                    }
                } catch (NoSuchAuthorityCodeException ex) {
                   throw new IllegalArgumentException("unknowed authority : " + srs);
                } catch (FactoryException ex) {
                    throw new IllegalArgumentException("unknowed authority : " + srs);
                }
            }
            int compact = (authorityCode << 28) | code ;
            return compact;
        }

        throw new IllegalArgumentException("unknowed compact version : " + version);

    }

    /**
     * Read a byte array and extract the srid.
     * buffer must have a size of 5.
     * buffer[offset+0] hold the compact version number
     * buffer[offset+1] to buffer[offset+4] hold the SRID number
     */
    public static int toSRID(final byte[] buffer, final int offset){
        if(buffer[offset] == Version.V1.code){
            return toInt(buffer, offset+1);
        }

        throw new IllegalArgumentException("unknowed compact version : " + buffer[offset]);
    }

    /**
     * Read a byte array and extract an integer value from
     * the first 4 bytes, buffer[offset+0] to buffer[offset+3].
     */
    public static int toInt(final byte[] buffer, final int offset){
        int compact = ((int)(buffer[offset]) & 0xFF) << 24;
        compact |= ((int)(buffer[offset+1]) & 0xFF) << 16;
        compact |= ((int)(buffer[offset+2]) & 0xFF) << 8;
        compact |= ((int)(buffer[offset+3]) & 0xFF);
        return compact;
    }

    /**
     * Read an integer SRID, and knowing it's compact version
     * extract the real SRS value.
     */
    public static String toSRS(final int srid, final Version version){
        if(version == Version.V1){
            final int authorityCode = srid >>> 28;
            final String authority;
            if(authorityCode == 0){
                authority = AUTHORITY_EPSG;
            }else if(authorityCode == 1){
                authority = AUTHORITY_CRS;
            }else{
                throw new IllegalArgumentException("unknown authority : " + authorityCode);
            }

            final int code = srid & 0xFFFFFFF;

            return new StringBuilder(authority).append(':').append(code).toString();
        }

        throw new IllegalArgumentException("unknowed compact version : " + version);

    }

    /**
     * Read a byte array and extract the srs.
     * buffer must have a size of 5.
     * buffer[offset+0] hold the compact version number
     * buffer[offset+1] to buffer[offset+4] hold the SRID number
     */
    public static String toSRS(final byte[] buffer, final int offset){
        if(buffer[offset] == Version.V1.code){
            final int srid = toInt(buffer, offset+1);
            return toSRS(srid, Version.V1);
        }

        throw new IllegalArgumentException("unknowed compact version : " + buffer[0]);
    }

    /**
     * Write a CRS in a byte[] of length 5.
     * the result byte array holds the compact version and the srid.
     */
    public static byte[] toBytes(final CoordinateReferenceSystem crs, final Version version){
        final int srid = toSRID(crs, version);
        return toBytes(srid, version);
    }

    /**
     * Write an srid in a byte[] of length 5.
     * the result byte array holds the compact version and the srid.
     */
    public static byte[] toBytes(final int srid, final Version version){
        final byte[] buffer = new byte[5];
        buffer[0] = version.code;
        toBytes(srid, buffer, 1);
        return buffer;
    }

    /**
     * Write an int in a byte array.
     * 4 bytes will be used, from buffer[offset+0] to buffer[offset+3].
     */
    public static byte[] toBytes(final int compact, final byte[] buffer, final int offset){
        buffer[offset  ] = (byte)(compact >>> 24);
        buffer[offset+1] = (byte)(compact >>> 16);
        buffer[offset+2] = (byte)(compact >>> 8);
        buffer[offset+3] = (byte) compact;
        return buffer;
    }


}
