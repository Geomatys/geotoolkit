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

package org.geotoolkit.wcs.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public final class WCSMarshallerPool {

    private static final MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool(JAXBContext.newInstance(
                      "org.geotoolkit.wcs.xml.v100:"
                    + "org.geotoolkit.wcs.xml.v111:"
                    + "org.geotoolkit.wcs.xml.v200:"
                    + "org.geotoolkit.wcs.xml.v200.crs:"
                    + "org.apache.sis.xml.bind.metadata.geometry:"
                    + "org.geotoolkit.gmlcov.geotiff.xml.v100:"
                    + "org.geotoolkit.ogc.xml.exception"), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private WCSMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
