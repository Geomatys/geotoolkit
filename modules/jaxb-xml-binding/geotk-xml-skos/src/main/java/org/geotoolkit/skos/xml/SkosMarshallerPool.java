/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.skos.xml;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.xml.MarshallerPool;

/**
 *
 * @author guilhem
 */
public class SkosMarshallerPool {

    private static MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool(JAXBContext.newInstance("org.geotoolkit.skos.xml:org.apache.sis.internal.jaxb.geometry"), null);
        } catch (JAXBException ex) {
            Logger.getLogger(SkosMarshallerPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private SkosMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
