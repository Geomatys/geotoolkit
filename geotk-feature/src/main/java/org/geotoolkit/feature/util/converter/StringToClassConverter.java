/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.feature.util.converter;

import org.apache.sis.util.UnconvertibleObjectException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class StringToClassConverter extends SimpleConverter<String, Class>{
    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Class> getTargetClass() {
        return Class.class;
    }

    @Override
    public Class apply(final String s) throws UnconvertibleObjectException {
        try {
            return Class.forName(s);
        } catch (ClassNotFoundException ex) {
            throw new UnconvertibleObjectException(ex);
        }
    }
}
