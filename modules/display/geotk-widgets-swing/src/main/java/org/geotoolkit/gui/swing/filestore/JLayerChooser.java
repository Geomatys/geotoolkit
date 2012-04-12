/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.RandomStyleFactory;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.feature.type.Name;

/**
 * Panel allowing to choose layer among the available datas of the source.
 * Random styles are generated for feature layers.
 *
 * @author Johann Sorel (Geoamtys)
 * @module pending
 */
public class JLayerChooser extends javax.swing.JPanel {
    
    private Object source = null;
    
    public JLayerChooser() {
        initComponents();
        guiList.setCellRenderer(new NameCellRenderer());
    }
    
    public List<MapLayer> getLayers() throws DataStoreException{
                
        final MutableStyleFactory styleFactory = (MutableStyleFactory) FactoryFinder.getStyleFactory(
                            new Hints(Hints.STYLE_FACTORY, MutableStyleFactory.class));
        
        final Object[] values = guiList.getSelectedValues();
        final List<MapLayer> layers = new ArrayList<MapLayer>();
        
        if(values != null){
            for(Object value : values){
                final Name name = (Name) value;
                
                if(source instanceof DataStore){
                    final DataStore store = (DataStore) source;
                    final Session session = store.createSession(true);
                    final FeatureCollection collection = session.getFeatureCollection(QueryBuilder.all(name));
                    final MutableStyle style = RandomStyleFactory.createRandomVectorStyle(collection);
                    final FeatureMapLayer layer = MapBuilder.createFeatureLayer(collection, style);
                    layer.setDescription(styleFactory.description(name.getLocalPart(), name.toString()));
                    layers.add(layer);
                    
                }else if(source instanceof CoverageStore){
                    final CoverageStore store = (CoverageStore) source;                    
                    final CoverageReference ref = store.getCoverageReference(name);
                    final MutableStyle style = styleFactory.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER);
                    final CoverageMapLayer layer = MapBuilder.createCoverageLayer(ref, style, name.getLocalPart());
                    layer.setDescription(styleFactory.description(name.getLocalPart(), name.toString()));
                    layers.add(layer);
                }
            }
        }
        
        return layers;
    }

    public void setSource(Object source) throws DataStoreException {
        this.source = source;
        
        final List<Name> names = new ArrayList<Name>();
        
        if(source instanceof DataStore){
            final DataStore store = (DataStore) source;
            names.addAll(store.getNames());
        }
        
        if(source instanceof CoverageStore){
            final CoverageStore store = (CoverageStore) source;
            names.addAll(store.getNames());
        }
        
        guiList.setModel(new ListComboBoxModel(names));
    }

    public Object getSource() {
        return source;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        guiList = new javax.swing.JList();

        jScrollPane1.setViewportView(guiList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList guiList;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    
    /**
     * Display a modal dialog.
     * 
     * @param source
     * @return
     * @throws DataStoreException 
     */
    public static List<MapLayer> showDialog(Object source) throws DataStoreException{
        final JLayerChooser chooser = new JLayerChooser();
        chooser.setSource(source);
        final JDialog dialog = new JDialog();
        
        final AtomicBoolean openAction = new AtomicBoolean(false);
        final JToolBar bar = new JToolBar();
        bar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bar.setFloatable(false);
        bar.add(new AbstractAction(MessageBundle.getString("open")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAction.set(true);
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
        
        if(openAction.get()){
            return chooser.getLayers();
        }else{
            return Collections.EMPTY_LIST;
        }
    }
    
    
    private static class NameCellRenderer extends DefaultListCellRenderer{

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            final JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if(value instanceof Name){
                final Name name = (Name) value;
                lbl.setText(name.getLocalPart());
                lbl.setToolTipText(DefaultName.toJCRExtendedForm(name));
            }
            
            return lbl;
        }
        
    }

}
