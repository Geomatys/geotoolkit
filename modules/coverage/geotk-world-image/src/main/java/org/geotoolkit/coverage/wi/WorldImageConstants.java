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
package org.geotoolkit.coverage.wi;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Constants for world image.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class WorldImageConstants {

    /** 
     * {@link Set} of supported extensions for png world files. 
     */
    private static final Set<String> PNG_WFILE_EXT;
    
    /** 
     * {@link Set} of supported extensions for tiff world files. 
     */
    private static final Set<String> TIFF_WFILE_EXT;
    
    /** 
     * {@link Set} of supported extensions for jpeg world files. 
     */
    private static final Set<String> JPG_WFILE_EXT;
    
    /** 
     * {@link Set} of supported extensions for gif world files. 
     */
    private static final Set<String> GIF_WFILE_EXT;
    
    /** 
     * {@link Set} of supported extensions for bmp world files. 
     */
    private static final Set<String> BMP_WFILE_EXT;
    
    static {
        Set<String> tempSet = new HashSet<String>(2);
        tempSet.add(".pgw");
        tempSet.add(".pngw");
        PNG_WFILE_EXT = Collections.unmodifiableSet(tempSet);

        tempSet = new HashSet<String>(4);
        tempSet.add(".jpw");
        tempSet.add(".jgw");
        tempSet.add(".jpgw");
        tempSet.add(".jpegw");
        JPG_WFILE_EXT = Collections.unmodifiableSet(tempSet);

        tempSet = new HashSet<String>(2);
        tempSet.add(".gifw");
        tempSet.add(".gfw");
        GIF_WFILE_EXT = Collections.unmodifiableSet(tempSet);

        tempSet = new HashSet<String>(2);
        tempSet.add(".tfw");
        tempSet.add(".tiffw");
        TIFF_WFILE_EXT = Collections.unmodifiableSet(tempSet);

        tempSet = new HashSet<String>(2);
        tempSet.add(".bmw");
        tempSet.add(".bmpw");
        BMP_WFILE_EXT = Collections.unmodifiableSet(tempSet);
    }
    
    /**
     * Takes an image file name including extension and
     * returns it's corresponding world file extension (such as .gfw).
     * 
     * @param fileExtension an image file name
     * @return a corresponding {@link Set} of world file extensions, including the '.'
     * or empty set if no correspondance found.
     */
    public static final Set<String> getWorldExtension(String fileExtension) {
        
        if (fileExtension == null) return Collections.emptySet();
        fileExtension = fileExtension.toLowerCase();
        if (fileExtension.endsWith("png"))                                   return PNG_WFILE_EXT;
        if (fileExtension.endsWith("gif"))                                   return GIF_WFILE_EXT;
        if (fileExtension.endsWith("jpg") || fileExtension.endsWith("jpeg")) return JPG_WFILE_EXT;
        if (fileExtension.endsWith("tif") || fileExtension.endsWith("tiff")) return TIFF_WFILE_EXT;
        if (fileExtension.endsWith("bmp"))                                   return BMP_WFILE_EXT;
        
        //no extensions found return empty list
        return Collections.emptySet();
    }

}
