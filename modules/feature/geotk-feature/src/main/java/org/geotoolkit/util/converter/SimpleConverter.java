/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.util.converter;

import java.util.Collections;
import java.util.Set;
import org.apache.sis.math.FunctionProperty;
import org.apache.sis.util.ObjectConverter;

public abstract class SimpleConverter<S,T> implements ObjectConverter<S,T> {
    @Override
    public Set<FunctionProperty> properties() {
        return Collections.emptySet();
    }

    @Override
    public ObjectConverter<T, S> inverse() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
