/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.pending.demo.bindings;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.geotoolkit.gui.swing.go2.JMap2DFrame;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.wmc.WMCUtilities;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class DemoWMC {
    
    public static void main(String[] args) {
        try {
            //MapContext contextWMS = WMCUtilities.getMapContext(WMCUtilities.class.getResourceAsStream("testWMC_wms.xml"));            
            MapContext contextWFS = WMCUtilities.getMapContext(WMCUtilities.class.getResourceAsStream("testWMC_wfs.xml"));
            
            //JMap2DFrame.show(contextWMS);
            JMap2DFrame.show(contextWFS);
            
        } catch (JAXBException ex) {
            Logger.getLogger(DemoWMC.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
