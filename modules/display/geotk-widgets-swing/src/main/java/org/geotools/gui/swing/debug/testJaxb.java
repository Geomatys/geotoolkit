/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.gui.swing.debug;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.style.CollectionChangeEvent;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.xml.Specification.StyledLayerDescriptor;
import org.geotoolkit.style.xml.XMLUtilities;
import org.opengis.sld.NamedLayer;

/**
 *
 * @author sorel
 */
public class testJaxb {

    public static void main(String[] args){
//        MapContext context = ContextBuilder.buildRealCityContext();
//        context.addContextListener(new ContextListener() {
//
//            @Override
//            public void propertyChange(PropertyChangeEvent event) {
//                System.out.println("la");
//            }
//
//            @Override
//            public void layerChange(CollectionChangeEvent<MapLayer> event) {
//                System.out.println("ici");
//                System.out.println(event.getType());
//            }
//        });
//
//        context.layers().remove(0);
//        context.layers().clear();
        
        
        
//        XMLUtilities parser = new XMLUtilities();
//        File f = new File("/media/1/Projects/IESWeb/shape/sld/ind_pondere.sld");
//        File out = new File("new_ind_pondere.sld");
//
//        MutableStyledLayerDescriptor desc = null;
//        try {
//            desc = parser.readSLD(f, StyledLayerDescriptor.V_1_0_0);
//            parser.writeSLD(out, desc, StyledLayerDescriptor.V_1_1_0);
//        } catch (JAXBException ex) {
//            ex.printStackTrace();
//        }

    }
    
    
}
