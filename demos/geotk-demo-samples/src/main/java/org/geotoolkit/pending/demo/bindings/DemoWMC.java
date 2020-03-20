/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.pending.demo.bindings;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.wmc.WMCUtilities;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class DemoWMC {

    public static void main(String[] args) {
        try {
            MapContext contextWMS = WMCUtilities.getMapContext(WMCUtilities.class.getResourceAsStream("testWMC_wms.xml"));
            MapContext contextWFS = WMCUtilities.getMapContext(WMCUtilities.class.getResourceAsStream("testWMC_wfs.xml"));
            //MapContext contextWFS1 = WMCUtilities.getMapContext(WMCUtilities.class.getResourceAsStream("testWMC_wfs_1.xml"));
//            FXMapFrame.show(contextWMS);
//            FXMapFrame.show(contextWFS);

        } catch (JAXBException ex) {
            Logger.getLogger("org.geotoolkit.pending.demo.bindings").log(Level.SEVERE, null, ex);
        }

    }
}
