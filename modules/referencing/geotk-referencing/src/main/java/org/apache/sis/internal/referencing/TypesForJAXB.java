/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.apache.sis.internal.referencing;

import java.util.Collection;
import org.apache.sis.internal.jaxb.TypeRegistration;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;


/**
 * Additional types to be registered to the {@link org.apache.sis.xml.MarshallerPool} JAXB context.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.22
 *
 * @since 3.22
 * @module
 */
public class TypesForJAXB extends TypeRegistration {
    /**
     * Public constructor needed by Java reflection.
     */
    public TypesForJAXB() {
    }

    /**
     * Adds referencing types to the set of classes to use in the JAXB context.
     */
    @Override
    public void getTypes(final Collection<Class<?>> addTo) {
        addTo.add(DefaultVerticalCRS.class);
        /*
         * Temporarily remove the Apache SIS types. This hack will be removed
         * after we finished to port the Geotk referencing module to SIS.
         */
        addTo.remove(org.apache.sis.referencing.AbstractIdentifiedObject.class);
    }
}
