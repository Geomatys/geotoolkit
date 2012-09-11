/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.skos.xml;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.geotoolkit.xml.MarshallerPool;

/**
 *
 * @author guilhem
 */
public class SkosMarshallerPool {
 
    private static MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool("org.geotoolkit.skos.xml:org.geotoolkit.internal.jaxb.geometry");
        } catch (JAXBException ex) {
            Logger.getLogger(SkosMarshallerPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private SkosMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
