/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.internal.jaxb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.geotoolkit.xml.MarshallerPool;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JTSWrapperMarshallerPool {
    private static MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool(ObjectFactory.class);
        } catch (JAXBException ex) {
            Logger.getLogger(JTSWrapperMarshallerPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private JTSWrapperMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
