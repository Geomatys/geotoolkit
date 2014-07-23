/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.util.converter;

import java.io.File;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 *
 * @author guilhem
 */
public class FileToArrayConverter extends SimpleConverter<File, File[]> {

    @Override
    public Class<File> getSourceClass() {
        return File.class;
    }

    @Override
    public Class<File[]> getTargetClass() {
        return File[].class;
    }

    @Override
    public File[] apply(File object) throws UnconvertibleObjectException {
        return new File[]{object};
    }
    
}
