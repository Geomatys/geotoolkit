/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultAbstractTimePrimitive extends DefaultAbstractObject implements AbstractTimePrimitive {

    /**
     * 
     */
    protected DefaultAbstractTimePrimitive() {
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractTimePrimitiveSimpleExtensions
     * @param abstractTimePrimitiveObjectExtensions
     */
    protected DefaultAbstractTimePrimitive(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleTypeContainer> abstractTimePrimitiveSimpleExtensions,
            List<Object> abstractTimePrimitiveObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        if (abstractTimePrimitiveSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.TIME_PRIMITIVE).addAll(abstractTimePrimitiveSimpleExtensions);
        }
        if (abstractTimePrimitiveObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.TIME_PRIMITIVE).addAll(abstractTimePrimitiveObjectExtensions);
        }
    }
}
