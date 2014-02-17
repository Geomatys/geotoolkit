/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.lucene.index;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A file filter to retrieve all the index directory in a specified directory.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class IndexDirectoryFilter implements FilenameFilter {

    /**
     * The service ID.
     */
    private final String prefix;

    public IndexDirectoryFilter(final String id) {
        if (id != null) {
            prefix = id;
        } else {
            prefix = "";
        }
    }

    /**
     * Return true if the specified file is a directory and if its name start
     * with the serviceID + 'index-'.
     *
     * @param dir The current directory explored.
     * @param name The name of the file.
     * @return True if the specified file in the current directory match the
     * conditions.
     */
    @Override
    public boolean accept(final File dir, final String name) {
        File f = new File(dir, name);
        if ("all".equals(prefix)) {
            return (name.indexOf("index-") != -1 && f.isDirectory());
        } else {
            return (name.startsWith(prefix + "index-") && f.isDirectory());
        }
    }
}
