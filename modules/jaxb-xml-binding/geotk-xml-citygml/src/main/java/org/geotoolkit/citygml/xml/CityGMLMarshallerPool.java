/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Geomatys
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

package org.geotoolkit.citygml.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public final class CityGMLMarshallerPool {

    private static final MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool(JAXBContext.newInstance(
                    "org.apache.sis.internal.jaxb.geometry:" +
                    "org.geotoolkit.gml.xml.v311:" +
                    "org.geotoolkit.citygml.xml.v100:" +
                    "org.geotoolkit.citygml.xml.v100.appearance:" +
                    "org.geotoolkit.citygml.xml.v100.building:" +
                    "org.geotoolkit.citygml.xml.v100.cityfurniture:" +
                    "org.geotoolkit.citygml.xml.v100.landuse:" +
                    "org.geotoolkit.citygml.xml.v100.transportation"), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private CityGMLMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
