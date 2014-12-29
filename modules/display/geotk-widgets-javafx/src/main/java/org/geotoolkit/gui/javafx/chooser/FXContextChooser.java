/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.javafx.chooser;

import java.io.File;
import java.util.prefs.Preferences;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.owc.xml.OwcXmlIO;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXContextChooser extends BorderPane {
    
    private FXContextChooser(){
    }
    
    public static MapContext showOpenChooser(Window owner) throws JAXBException, FactoryException, DataStoreException{
        final FileChooser chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("OWC Context", "xml"));        
        
        final String prevPath = getPreviousPath();
        if (prevPath != null) {
            final File f = new File(prevPath);
            if(f.exists() && f.isDirectory()){
                chooser.setInitialDirectory(f);
            }
        }
        
        
        final File file = chooser.showOpenDialog(owner);
        if(file!=null){
            setPreviousPath(file.getParentFile().getAbsolutePath());
            return OwcXmlIO.read(file);
        }
        
        return null;
    }
    
    public static File showSaveChooser(Window owner,MapContext context) throws JAXBException, PropertyException, FactoryException{
        final FileChooser chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("OWC Context", "xml"));        
        
        final String prevPath = getPreviousPath();
        if (prevPath != null) {
            final File f = new File(prevPath);
            if(f.exists() && f.isDirectory()){
                chooser.setInitialDirectory(f);
            }
        }
        
        final File file = chooser.showSaveDialog(null);
        
        if(file!=null){
            setPreviousPath(file.getParentFile().getAbsolutePath());
            OwcXmlIO.write(file,context);
        }
        
        return file;
    }
    
    public static String getPreviousPath() {
        final Preferences prefs = Preferences.userNodeForPackage(FXContextChooser.class);
        return prefs.get("path", null);
    }

    public static void setPreviousPath(final String path) {
        final Preferences prefs = Preferences.userNodeForPackage(FXContextChooser.class);
        prefs.put("path", path);
    }
    
}
