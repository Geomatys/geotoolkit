/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.io;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Simple file filter.
 * Stores additional information to indicate if folders can be selected.
 *
 * @author Johann Sorel (Geomatys)
 */
public class SimpleFileFilter extends FileFilter {

    private final String[] ends;
    private final String desc;
    private final boolean allowFolder;

    public SimpleFileFilter(final String name, boolean allowFolder, String[] ends) {
        this.ends = ends;
        this.allowFolder = allowFolder;

        final StringBuilder buff = new StringBuilder();
        buff.append(name);
        buff.append(" (");
        buff.append("*.").append(ends[0]);
        for (int i = 1; i < ends.length; i++) {
            buff.append(",*.").append(ends[i]);
        }
        buff.append(')');
        desc = buff.toString();
    }

    @Override
    public String getDescription() {
        return desc;
    }

    /**
     * Inform that folder file can be selected.
     * @return true if folders are selectable.
     */
    public boolean allowFolderSelection(){
        return allowFolder;
    }

    @Override
    public boolean accept(final File pathname) {
        final String nom = pathname.getName();

        if (pathname.isDirectory()) {
            return true;
        }

        for (int i = 0, n = ends.length; i < n; i++) {
            if (nom.toLowerCase().endsWith(ends[i])) {
                return true;
            }
        }

        return false;
    }
}
