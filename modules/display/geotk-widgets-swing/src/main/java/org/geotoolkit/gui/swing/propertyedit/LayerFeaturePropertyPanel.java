/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.swing.propertyedit;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import java.util.HashSet;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.map.MapLayer;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.propertyedit.model.FeatureSourceModel;
import org.geotoolkit.map.FeatureMapLayer;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.LayerListener;
import org.opengis.feature.Feature;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;

/**
 * layer feature panel
 * 
 * @author  Johann Sorel
 */
public class LayerFeaturePropertyPanel extends javax.swing.JPanel implements PropertyPane {

    private final LayerListener layerListener = new LayerListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if(MapLayer.SELECTION_FILTER_PROPERTY.equals(event.getPropertyName())){
                updateLayerSelection();
            }
        }
        @Override
        public void styleChange(MapLayer source, EventObject event) {
        }
    };

    private final ListSelectionListener selectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent arg0) {
            updateTableSelection();
        }
    };

    private FeatureMapLayer layer = null;
    private boolean editable = false;

    /** Creates new form DefaultMapLayerTablePanel */
    public LayerFeaturePropertyPanel() {

        //netbeans JPanel components init
        initComponents();

        tab_data.setEditable(false);
        tab_data.setColumnControlVisible(true);
        tab_data.setHorizontalScrollEnabled(true);
        tab_data.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        tab_data.getSelectionModel().addListSelectionListener(selectionListener);

        tab_data.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping(Color.white, HighlighterFactory.QUICKSILVER, 5)});
        tab_data.setShowGrid(true, true);
        tab_data.setGridColor(Color.GRAY.brighter());

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                tab_data.packAll();
            }
        });


        final JPopupMenu menu = new JPopupMenu();

        JMenuItem mi = new JMenuItem("Clear selection");
        menu.add(mi);
        mi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(layer != null){
                    layer.setSelectionFilter(null);
                }
            }
        });

        jbu_action.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if(event.getButton() == MouseEvent.BUTTON1){
                    menu.show(jbu_action, event.getX(), event.getY());
                }
            }
            @Override
            public void mousePressed(MouseEvent arg0) {}
            @Override
            public void mouseReleased(MouseEvent arg0) {}
            @Override
            public void mouseEntered(MouseEvent arg0) {}
            @Override
            public void mouseExited(MouseEvent arg0) {}
        });


    }

    private void updateLayerSelection(){
        tab_data.getSelectionModel().removeListSelectionListener(selectionListener);
        layer.removeLayerListener(layerListener);

        FeatureSourceModel model = (FeatureSourceModel) tab_data.getModel();

        Id selection = layer.getSelectionFilter();
        String selected = "0";
        tab_data.getSelectionModel().clearSelection();
        if(selection != null){
            for(int i=0,n=tab_data.getRowCount();i<n;i++){
                Feature f = model.getFeatureAt(i);
                if(selection.evaluate(f)){
                    tab_data.getSelectionModel().addSelectionInterval(i, i);
                }
            }
            selected = String.valueOf(selection.getIDs().size());
        }

        guiCount.setText("Selection : "+ selected +" / "+String.valueOf(tab_data.getModel().getRowCount()));

        layer.addLayerListener(layerListener);
        tab_data.getSelectionModel().addListSelectionListener(selectionListener);
    }

    private void updateTableSelection(){
        tab_data.getSelectionModel().removeListSelectionListener(selectionListener);
        layer.removeLayerListener(layerListener);

        int[] rows = tab_data.getSelectedRows();

        HashSet<Identifier> ids = new HashSet<Identifier>();

        FeatureSourceModel model = (FeatureSourceModel) tab_data.getModel();

        for(int i : rows){
            ids.add(model.getFeatureAt(i).getIdentifier());
        }

        if(ids.isEmpty()){
            layer.setSelectionFilter(null);
        }else{
            FilterFactory ff = FactoryFinder.getFilterFactory(null);
            layer.setSelectionFilter(ff.id(ids));
        }
        
        layer.addLayerListener(layerListener);
        tab_data.getSelectionModel().addListSelectionListener(selectionListener);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new JScrollPane();
        tab_data = new JXTable();
        jcb_edit = new JCheckBox();
        jbu_action = new JButton();
        guiCount = new JLabel();

        tab_data.setModel(new DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tab_data);

        jcb_edit.setText(MessageBundle.getString("property_edit")); // NOI18N
        jcb_edit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jcb_edit.setEnabled(false);
        jcb_edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionEditer(evt);
            }
        });

        jbu_action.setText(MessageBundle.getString("property_action")); // NOI18N
        jbu_action.setEnabled(false);

        guiCount.setHorizontalAlignment(SwingConstants.CENTER);
        guiCount.setText(" ");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcb_edit)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiCount, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jbu_action))
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jbu_action)
                    .addComponent(guiCount)
                    .addComponent(jcb_edit)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void actionEditer(ActionEvent evt) {//GEN-FIRST:event_actionEditer
        tab_data.setEditable(((JCheckBox) evt.getSource()).isSelected());
    }//GEN-LAST:event_actionEditer

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel guiCount;
    private JScrollPane jScrollPane1;
    private JButton jbu_action;
    private JCheckBox jcb_edit;
    private JXTable tab_data;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void setTarget(Object target) {

        if(layer != null){
            layer.removeLayerListener(layerListener);
        }

        if (target instanceof FeatureMapLayer) {
            layer = (FeatureMapLayer) target;
            FeatureSource<SimpleFeatureType, SimpleFeature> source =
                    (FeatureSource<SimpleFeatureType, SimpleFeature>) layer.getFeatureSource();

            editable = (source instanceof FeatureStore);
            jcb_edit.setEnabled(editable);
            jbu_action.setEnabled(editable);

            FeatureSourceModel m = new FeatureSourceModel(tab_data, layer);
            tab_data.setModel(m);
            updateLayerSelection();
            
            layer.addLayerListener(layerListener);
        }

    }

    @Override
    public void apply() {
    }

    @Override
    public String getTitle() {
        return MessageBundle.getString("property_feature_table");
    }

    @Override
    public ImageIcon getIcon() {
        return IconBundle.getInstance().getIcon("16_feature_table");
    }

    @Override
    public String getToolTip() {
        return "";
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void reset() {
    }
}
