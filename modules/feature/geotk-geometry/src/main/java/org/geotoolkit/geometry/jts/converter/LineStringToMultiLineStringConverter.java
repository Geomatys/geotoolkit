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
package org.geotoolkit.geometry.jts.converter;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import java.util.Collections;
import java.util.Set;
import org.apache.sis.math.FunctionProperty;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 * Convert JTS LineString to MultiLineString.
 *
 * @author Johann Sorel (Geomatys)
 */
public class LineStringToMultiLineStringConverter implements ObjectConverter<LineString, MultiLineString>{

    @Override
    public Set<FunctionProperty> properties() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Class<LineString> getSourceClass() {
        return LineString.class;
    }

    @Override
    public Class<MultiLineString> getTargetClass() {
        return MultiLineString.class;
    }

    @Override
    public MultiLineString apply(LineString object) throws UnconvertibleObjectException {
        final MultiLineString geom = object.getFactory().createMultiLineString(new LineString[]{object});
        geom.setSRID(object.getSRID());
        geom.setUserData(object.getUserData());
        return geom;
    }

    @Override
    public ObjectConverter<MultiLineString, LineString> inverse() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

}
