/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.resources;

import java.lang.reflect.Field;


/**
 * Adapter from <abbr>SIS</abbr> internal <abbr>API</abbr> to Geotk internal <abbr>API</abbr>.
 * This temporary class will not work after Geotk modularisation and will need to be deleted.
 */
public abstract class KeyConstants extends org.apache.sis.util.resources.KeyConstants {
    protected KeyConstants() {
    }

    /**
     * Returns the value of a field declared in this class.
     * This default implementation assumes that the <abbr>JAR</abbr> is <strong>not</strong> modularized
     * (this method — actually the full class — will need to be removed in a future version).
     */
    @Override
    protected Object getStaticValue(final Field field) throws IllegalAccessException {
        return field.get(null);
    }
}
