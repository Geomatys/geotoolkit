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
package org.geotoolkit.wps.xml;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WPSTypeRegistration extends TypeRegistration {

    @Override
    protected void getTypes(Collection<Class<?>> addTo) {
        addTo.addAll(Arrays.asList(
                org.geotoolkit.wps.xml.v100.ObjectFactory.class,
                org.geotoolkit.wps.xml.v200.ObjectFactory.class,
                org.geotoolkit.gml.xml.v321.ObjectFactory.class,
                org.geotoolkit.ows.xml.v200.ObjectFactory.class,
                org.apache.sis.internal.jaxb.geometry.ObjectFactory.class,
                org.geotoolkit.mathml.xml.ObjectFactory.class
        ));
    }

}
