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
public class ClassToStringConverter extends SimpleConverter<Class, String>{

    @Override
    public Class<Class> getSourceClass() {
        return Class.class;
    }

    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }

    @Override
    public String apply(final Class s) throws UnconvertibleObjectException {
        if (s != null) {
            return s.getName();
        }
        return null;
    }
}