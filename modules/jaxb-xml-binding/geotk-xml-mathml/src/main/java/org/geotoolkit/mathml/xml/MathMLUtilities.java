/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.mathml.xml;

import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class MathMLUtilities {

    private static MarshallerPool POLL;

    private MathMLUtilities() {
    }

    public static synchronized MarshallerPool getMarshallerPool() {
        if (POLL == null) {

            final java.util.List<Class> classes = new ArrayList<>();
            classes.add(Math.class);
            try {
                POLL = new MarshallerPool(JAXBContext.newInstance(classes.toArray(new Class[classes.size()])), null);
            } catch (JAXBException ex) {
                throw new RuntimeException("Could not load jaxbcontext for MathML.",ex);
            }
        }
        return POLL;
    }

}
