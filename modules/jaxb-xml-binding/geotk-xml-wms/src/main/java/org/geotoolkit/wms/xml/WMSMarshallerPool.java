/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.wms.xml;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.geotoolkit.xml.MarshallerPool;

/**
 *
 * @author guilhem
 */
public class WMSMarshallerPool {

    private static MarshallerPool instance;
    static {
        try {
            instance = new MarshallerPool("org.geotoolkit.ogc.xml.exception:" +
                                          "org.geotoolkit.wms.xml.v111:" +
                                          "org.geotoolkit.wms.xml.v130:" +
                                          "org.geotoolkit.sld.xml.v110:" +
                                          "org.geotoolkit.inspire.xml.vs:" +
                                          "org.geotoolkit.internal.jaxb.geometry");
        } catch (JAXBException ex) {
            Logger.getLogger(WMSMarshallerPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private WMSMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return instance;
    }
}
