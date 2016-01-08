/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

package org.geotoolkit.tms.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.tms.xml.v100.ObjectFactory;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class TMSMarshallerPool {

    private static final MarshallerPool INSTANCE;
    static {
        try {
            final JAXBContext ctx = JAXBContext.newInstance(ObjectFactory.class);
            INSTANCE = new MarshallerPool(ctx,null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private TMSMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return INSTANCE;
    }

}
