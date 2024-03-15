/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.internal;

import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.sis.util.ResourceInternationalString;

/**
 * Subclass of ResourceInternationalString with a configurable ClassLoader.
 *
 * @author Johann Sorel (Geomatys)
 *
 * @deprecated This class will not work anymore in a JPMS context.
 */
@Deprecated
public class ClassLoaderInternationalString extends ResourceInternationalString{

    private final String resources;
    private final ClassLoader classLoader;

    public ClassLoaderInternationalString(Class clazz, String resources, String key) {
        this(clazz.getClassLoader(), resources, key);
    }

    public ClassLoaderInternationalString(ClassLoader classloder, String resources, String key) {
        super(key);
        this.resources = resources;
        this.classLoader = classloder;
    }

    @Override
    protected ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(resources, locale, classLoader);
    }

}
