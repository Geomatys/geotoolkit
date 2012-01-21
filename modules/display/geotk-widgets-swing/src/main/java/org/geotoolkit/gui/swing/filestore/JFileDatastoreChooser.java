/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2011, Johann Sorel
 *    (C) 2011, Geomatys
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
package org.geotoolkit.gui.swing.filestore;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import org.geotoolkit.data.AbstractFileDataStoreFactory;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FileDataStoreFactory;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.io.DefaultFileFilter;
import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 *
 * @author Johann Sorel (Geoamatys)
 */
public class JFileDatastoreChooser extends javax.swing.JSplitPane {

    private static final Logger LOGGER = Logging.getLogger(JFileDatastoreChooser.class);
    private static volatile File lastPath = null;
    private static volatile FileFilter lastFilter = null;
    private final Map<FileFilter, FileDataStoreFactory> filterMap = new HashMap<FileFilter, FileDataStoreFactory>();
    private final JFileChooser guiChooser = new JFileChooser();
    private final JFeatureOutLine guiConfig = new JFeatureOutLine();

    public JFileDatastoreChooser() {
        this(null);
    }

    /**
     * Creates new form panel, similar to a JFileChooser panel.
     * @param openPath : default path to open
     */
    public JFileDatastoreChooser(final File openPath) {
        setBorder(null);
        setDividerSize(3);

        guiChooser.setControlButtonsAreShown(false);
        guiChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        setLeftComponent(guiChooser);
        setRightComponent(null);

        // get all datastore factories ---------------------------------------------
        final List<FileFilter> filters = new ArrayList<FileFilter>();

        final Iterator<FileDataStoreFactory> ite = DataStoreFinder.getAvailableDataStores(FileDataStoreFactory.class);
        while(ite.hasNext()){
            final FileDataStoreFactory fact = ite.next();
            final String name = fact.getDescription();
            final String[] exts = fact.getFileExtensions();            
            final FileFilter filter = new DefaultFileFilter("*"+exts[0], name){
                @Override
                public boolean accept(File file) {
                    return super.accept(file) || file.isDirectory();
                }
            };
            filterMap.put(filter, fact);
            filters.add(filter);
        }
        

        Collections.sort(filters, new Comparator<FileFilter>() {
            @Override
            public int compare(FileFilter o1, FileFilter o2) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
        });

        for (FileFilter ff : filters) {
            guiChooser.addChoosableFileFilter(ff);
        }

        guiChooser.setMultiSelectionEnabled(true);
        guiChooser.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(JFileChooser.FILE_FILTER_CHANGED_PROPERTY)) {
                    //update the additional configuration sheet accordingly
                    final FileFilter ff = (FileFilter) evt.getNewValue();
                    final FileDataStoreFactory factory = filterMap.get(ff);
                    if(factory == null){
                        return;
                    }
                    final ParameterDescriptorGroup desc = factory.getParametersDescriptor();
                    ComplexType type = FeatureTypeUtilities.toPropertyType(desc);
                    
                    //remove the URL parameter
                    final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
                    ftb.copy(type);
                    for(Object pd : ftb.getProperties().toArray()){
                        if(AbstractFileDataStoreFactory.URLP.getName().getCode().equals(
                                ((PropertyDescriptor)pd).getName().getLocalPart())){
                            ftb.getProperties().remove(pd);
                        }
                    }
                    type = ftb.buildType();
                    guiConfig.setEdited(FeatureUtilities.defaultProperty(type));                    
                    setRightComponent(guiConfig);
                }
            }
        });

        //restore last path and file chooser -----------------------------------
        final FileFilter lastf = lastFilter;
        if (lastf != null) {
            guiChooser.setFileFilter(lastf);
        }

        if (openPath != null) {
            guiChooser.setCurrentDirectory(openPath);
            lastPath = openPath;
        } else {
            final File lastp = lastPath;
            if (lastp != null) {
                guiChooser.setCurrentDirectory(lastp);
            }
        }

    }

    /**
     * Go to the given directory.
     */
    public void setDirectory(final File directory) {
        guiChooser.setCurrentDirectory(directory);
    }

    /**
     * Get the current directory.
     */
    public File getDirectory() {
        return guiChooser.getCurrentDirectory();
    }

    /**
     * Returns a list of created datastores
     */
    public List<DataStore> getSources() {
        final List<DataStore> stores = new ArrayList<DataStore>();
        final File[] files = guiChooser.getSelectedFiles();

        //store current path and filter for next time.
        lastFilter = guiChooser.getFileFilter();
        lastPath = guiChooser.getCurrentDirectory();
        final FileDataStoreFactory currentService = filterMap.get(lastFilter);

        file_loop:
        for (final File file : files) {
            if(file.isDirectory()){
                continue;
            }
            
            if (currentService != null) {
                //specific filter has been choosen, use the related service.
                try {
                    final DataStore store = currentService.createDataStore(file.toURI().toURL());
                    stores.add(store);
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
                
            } else {
                //no filter choosen, find a service that can handle this file
                for (Entry<FileFilter, FileDataStoreFactory> entry : filterMap.entrySet()) {
                    if (entry.getKey().accept(file)) {
                        try {
                            final DataStore store = currentService.createDataStore(file.toURI().toURL());
                            stores.add(store);
                        } catch (Exception ex) {
                            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                        }
                        break;
                    }
                }
            }
        }

        return stores;
    }
    
    public static List<DataStore> showDialog(){
        final JFileDatastoreChooser chooser = new JFileDatastoreChooser();
        final JDialog dialog = new JDialog();
        
        final JToolBar bar = new JToolBar();
        bar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bar.setFloatable(false);
        bar.add(new AbstractAction(MessageBundle.getString("open")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(BorderLayout.CENTER,chooser);        
        panel.add(BorderLayout.SOUTH, bar);
        dialog.setModal(true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return chooser.getSources();
    }
    
}
