/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.skos.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author guilhem
 */
public final class SkosMarshallerPool {

    private static final MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool(JAXBContext.newInstance("org.geotoolkit.skos.xml:org.apache.sis.internal.jaxb.geometry"), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private SkosMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
