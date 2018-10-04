/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.outputs.complex;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.TypeRegistration;
import org.geotoolkit.wps.xml.v200.Data;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JAXBToComplexConverter extends AbstractComplexOutputConverter<Object> {

    public static final JAXBToComplexConverter INSTANCE = new JAXBToComplexConverter();

    @Override
    public Class<Object> getSourceClass() {
        return Object.class;
    }

    @Override
    public Data convert(Object source, Map<String, Object> params) throws UnconvertibleObjectException {
        return new Data(source);
    }

    @Override
    public boolean canConvert(Class source, Class target) {
        //check if object is known by the JAXB Context
        try {
            Constructor constructor = source.getDeclaredConstructor();
            constructor.setAccessible(true);
            if (TypeRegistration.getSharedContext().createJAXBIntrospector().isElement(constructor.newInstance())) {
                return true;
            }
        }catch(JAXBException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            //ignore exception
        }

        return false;
    }

}
