/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile.lock;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Enumerates the known types of files associated with a shapefile.
 * 
 * @author jesse
 * @author Johann Sorel (Geomatys)
 */
public enum ShpFileType {

    /**
     * The .shp file. It contains the geometries of the shapefile
     */
    SHP("shp"),
    /**
     * the .dbf file, it contains the attribute information of the shapefile
     */
    DBF("dbf"),
    /**
     * the .shx file, it contains index information of the existing features
     */
    SHX("shx"),
    /**
     * the .prj file, it contains the projection information of the shapefile
     */
    PRJ("prj"),
    /**
     * the .qix file, A quad tree spatial index of the shapefile. It is the same
     * format the mapservers shptree tool generates
     */
    QIX("qix"),
    /**
     * the .fix file, it contains all the Feature IDs for constant time lookup
     * by fid also so that the fids stay consistent across deletes and adds
     */
    FIX("fix"),
    /**
     * the .shp.xml file, it contains the metadata about the shapefile
     */
    SHP_XML("shp.xml"),
    /**
     * the .cpg file, it contains the dbf character encoding as a single string.
     */
    CPG("cpg");

    public final String extension;
    public final String extensionWithPeriod;
    public final Pattern pattern;

    ShpFileType(final String extension) {
        this.extension = extension.toLowerCase();
        this.extensionWithPeriod = "." + this.extension;
        this.pattern = Pattern.compile(".*"+extension+"$", Pattern.CASE_INSENSITIVE);
    }


    /**
     * Returns the base of the file or null if the file passed in is not of the
     * correct type (has the correct extension.)
     * <p>
     * For example if the file is c:\shapefiles\file1.dbf. The DBF type will
     * return c:\shapefiles\file1 but all other will return null.
     */
    public String toBase(final URI uri) {
        return toBase(uri.toString());
    }

    /**
     * Returns the base of the file or null if the file passed in is not of the
     * correct type (has the correct extension.)
     * <p>
     * For example if the file is c:\shapefiles\file1.dbf. The DBF type will
     * return c:\shapefiles\file1 but all other will return null.
     */
    public String toBase(final File file) {
        String path = file.getPath();
        return toBase(path);
    }

    /**
     * Returns the base of the file or null if the file passed in is not of the
     * correct type (has the correct extension.)
     * <p>
     * For example if the file is c:\shapefiles\file1.dbf. The DBF type will
     * return c:\shapefiles\file1 but all other will return null.
     */
    public String toBase(final String path) {
        if (!path.toLowerCase().endsWith(extensionWithPeriod)
                || path.equalsIgnoreCase(extensionWithPeriod)) {
            return null;
        }

        int indexOfExtension = path.toLowerCase().lastIndexOf(extensionWithPeriod);
        return path.substring(0, indexOfExtension);
    }

    /**
     * Returns the base of the file or null if the file passed in is not of the
     * correct type (has the correct extension.)
     * <p>
     * For example if the file is c:\shapefiles\file1.dbf. The DBF type will
     * return c:\shapefiles\file1 but all other will return null.
     */
    public String toBase(final URL url) {
        if(!org.geotoolkit.nio.IOUtilities.canProcessAsPath(url)){
            try {
                return toBase(java.net.URLDecoder.decode(url.toExternalForm(),"US-ASCII"));
            } catch (UnsupportedEncodingException e) {
                return toBase(url.toExternalForm());
            }
        }else{
            return toBase(url.toExternalForm());
        }
    }

    public boolean match(Path path) {
        final String fileName = path.getFileName().toString();
        return pattern.matcher(fileName).matches();
    }

    public String toBase(Path obj) {
        return toBase(obj.toString());
    }
}
