/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.metadata.bil;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.lang.Static;
import org.geotoolkit.util.logging.Logging;

/**
 * Utility class to read HDR file.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class HDRAccessor extends Static {

    public static final String BYTEORDER    = "BYTEORDER";
    public static final String LAYOUT       = "LAYOUT";
    public static final String NROWS        = "NROWS";
    public static final String NCOLS        = "NCOLS";
    public static final String NBANDS       = "NBANDS";
    public static final String NBITS        = "NBITS";
    public static final String BANDROWBYTES = "BANDROWBYTES";
    public static final String TOTALROWBYTES= "TOTALROWBYTES";
    public static final String PIXELTYPE    = "PIXELTYPE";
    public static final String ULXMAP       = "ULXMAP";
    public static final String ULYMAP       = "ULYMAP";
    public static final String XDIM         = "XDIM";
    public static final String YDIM         = "YDIM";
    public static final String NODATA       = "NODATA";
    
    private static final Logger LOGGER = Logging.getLogger(HDRAccessor.class);

    private HDRAccessor() {
    }

    public static Map<String,String> read(final Object file) throws IOException{
        final Map<String,String> parameters = new HashMap<String, String>();        
        final LineNumberReader reader = IOUtilities.openLatin(file);
        
        String line = null;
        while ((line = reader.readLine()) != null) {
            final int index = line.indexOf(' ');
            final String key = line.substring(0,index).trim();
            final String value = line.substring(index+1).trim();
            parameters.put(key, value);
        }
        
        return parameters;
    }
    
}
