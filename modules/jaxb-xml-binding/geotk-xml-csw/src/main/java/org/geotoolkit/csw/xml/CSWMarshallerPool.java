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

package org.geotoolkit.csw.xml;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBException;
import org.geotoolkit.xml.AnchoredMarshallerPool;
import org.apache.sis.xml.MarshallerPool;

import static org.geotoolkit.gml.xml.GMLMarshallerPool.createJAXBContext;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public final class CSWMarshallerPool {

    private static final MarshallerPool instance;
    static {
        try {
            instance = new AnchoredMarshallerPool(createJAXBContext(CSWClassesContext.getAllClasses()));
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private static final MarshallerPool instanceCswOnly;
    static {
        try {
            instanceCswOnly = new AnchoredMarshallerPool(createJAXBContext(CSWClassesContext.getCSWClasses()));
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private CSWMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
    
    public static MarshallerPool getInstanceCswOnly() {
        return instanceCswOnly;
    }

    public static Binder getBinderCswOnly() throws JAXBException {
        return createJAXBContext(CSWClassesContext.getCSWClasses()).createBinder();
    }

    public static Binder getBinder() throws JAXBException {
        return createJAXBContext(CSWClassesContext.getAllClasses()).createBinder();
    }
}
