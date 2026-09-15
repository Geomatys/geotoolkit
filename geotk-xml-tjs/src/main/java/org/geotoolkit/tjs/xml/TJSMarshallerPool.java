/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.tjs.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 * TJS defines a single version (1.0), so this pool is a plain singleton, unlike
 * {@code WFSMarshallerPool} / {@code CSWMarshallerPool} which dispatch over several versions.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class TJSMarshallerPool {

    private static final MarshallerPool INSTANCE;
    static {
        try {
            INSTANCE = new MarshallerPool(JAXBContext.newInstance(
                      "org.geotoolkit.tjs.xml.v100:"
                    + "org.geotoolkit.ows.xml.v110"), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private TJSMarshallerPool() {
    }

    public static MarshallerPool getInstance() {
        return INSTANCE;
    }
}
