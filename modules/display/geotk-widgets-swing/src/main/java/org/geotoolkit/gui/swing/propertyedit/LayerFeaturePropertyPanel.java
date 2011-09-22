/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Johann Sorel
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.geotoolkit.map.MapItem;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.gui.swing.misc.LoadingLockableUI;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.propertyedit.model.FeatureCollectionModel;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.LayerListener;
import org.geotoolkit.util.collection.CollectionChangeEvent;

import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.DatePickerCellEditor;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;

/**
 * layer feature panel
 * 
 * @author  Johann Sorel
 * @module pending
 */
public class LayerFeaturePropertyPanel extends javax.swing.JPanel implements PropertyPane, LayerListener {



    private final ListSelectionListener selectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent arg0) {
            updateTableSelection();
        }
    };

    private final TableModelListener tableListener = new TableModelListener() {

        @Override
        public void tableChanged(TableModelEvent e) {
            checkChanges();
        }
    };


    private final List<JComponent> actions = new ArrayList<JComponent>();
    private final LockableUI lockableUI = new LoadingLockableUI();

    private FeatureMapLayer layer = null;
    private boolean editable = false;
    private final LayerListener.Weak weakListener = new LayerListener.Weak(this);
    
    private final JXTable tab_data = new JXTable();
    private final JFeatureCollectionOutline outline = new JFeatureCollectionOutline();
    

    /** Creates new form DefaultMapLayerTablePanel */
    public LayerFeaturePropertyPanel() {
        initComponents();

        tab_data.setEditable(false);
        tab_data.setColumnControlVisible(true);
        tab_data.setHorizontalScrollEnabled(true);
        tab_data.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);
        tab_data.getSelectionModel().addListSelectionListener(selectionListener);

        tab_data.setHighlighters(new Highlighter[]{HighlighterFactory.createAlternateStriping(Color.white, HighlighterFactory.QUICKSILVER, 5)});
        tab_data.setShowGrid(true, true);
        tab_data.setGridColor(Color.GRAY.brighter());

        tab_data.setDefaultEditor(Date.class, new DatePickerCellEditor(DateFormat.getDateTimeInstance()));
        tab_data.setDefaultEditor(java.sql.Date.class, new DatePickerCellEditor(DateFormat.getDateTimeInstance()));
        tab_data.setDefaultEditor(Time.class, new DatePickerCellEditor(DateFormat.getDateTimeInstance()));
        tab_data.setDefaultEditor(Timestamp.class, new DatePickerCellEditor(DateFormat.getDateTimeInstance()));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tab_data.packAll();
            }
        });


        final JPopupMenu menu = new DynamicMenu();

        jbu_action.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if(event.getButton() == MouseEvent.BUTTON1){
                    menu.show(jbu_action, event.getX()-10, event.getY()-10);
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

        checkChanges();

    }

    /**
     * Check if there are changes in the current session and
     * activate commit/rollback buttons.
     */
    private void checkChanges(){
        boolean changes = false;

        if(layer != null){
            final FeatureCollection col = layer.getCollection();
            final Session session = col.getSession();
            if(session != null){
                changes = session.hasPendingChanges();
            }
        }


        guiCommit.setEnabled(changes);
        guiRollback.setEnabled(changes);
    }

    private void updateLayerSelection(){
        tab_data.getSelectionModel().removeListSelectionListener(selectionListener);
        weakListener.unregisterSource(layer);

        FeatureCollectionModel model = (FeatureCollectionModel) tab_data.getModel();

        Id selection = layer.getSelectionFilter();
        String selected = "0";
        tab_data.getSelectionModel().clearSelection();
        if(selection != null){
            for(int i=0,n=tab_data.getRowCount();i<n;i++){
                Feature f = model.getFeatureAt(i);
                if(selection.evaluate(f)){
                    int viewIndex = tab_data.convertRowIndexToView(i);
                    tab_data.getSelectionModel().addSelectionInterval(viewIndex, viewIndex);
                }
            }
            selected = String.valueOf(selection.getIDs().size());
        }

        guiCount.setText("Selection : "+ selected +" / "+tab_data.getModel().getRowCount());

        weakListener.registerSource(layer);
        tab_data.getSelectionModel().addListSelectionListener(selectionListener);
    }

    private void updateTableSelection(){
        tab_data.getSelectionModel().removeListSelectionListener(selectionListener);
        weakListener.unregisterSource(layer);

        final int[] rows = tab_data.getSelectedRows();

        FeatureCollectionModel model = (FeatureCollectionModel) tab_data.getModel();

        for(int i=0; i<rows.length; i++){
            rows[i] = tab_data.convertRowIndexToModel(rows[i]);
        }

        final HashSet<Identifier> ids = new HashSet<Identifier>();

        for(int i : rows){
            ids.add(new DefaultFeatureId((String)model.getValueAt(i, 0)));
        }

        if(ids.isEmpty()){
            layer.setSelectionFilter(null);
        }else{
            FilterFactory ff = FactoryFinder.getFilterFactory(null);
            layer.setSelectionFilter(ff.id(ids));
        }

        weakListener.registerSource(layer);
        tab_data.getSelectionModel().addListSelectionListener(selectionListener);
    }

    public List<JComponent> actions(){
        return actions;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JPanel();
        jcb_edit = new JCheckBox();
        guiCount = new JLabel();
        guiCommit = new JButton();
        guiRollback = new JButton();
        jbu_action = new JButton();
        guiShowId = new JCheckBox();
        panCenter = new JPanel();

        setLayout(new BorderLayout());

        jPanel1.setOpaque(false);

        jcb_edit.setText(MessageBundle.getString("property_edit")); // NOI18N
        jcb_edit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jcb_edit.setEnabled(false);
        jcb_edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionEditer(evt);
            }
        });

        guiCount.setHorizontalAlignment(SwingConstants.CENTER);
        guiCount.setText(" ");


        guiCommit.setText(MessageBundle.getString("commit")); // NOI18N
        guiCommit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiCommitActionPerformed(evt);
            }
        });

        guiRollback.setText(MessageBundle.getString("rollback")); // NOI18N
        guiRollback.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiRollbackActionPerformed(evt);
            }
        });

        jbu_action.setText(MessageBundle.getString("property_action")); // NOI18N
        guiShowId.setText(MessageBundle.getString("show_id")); // NOI18N
        guiShowId.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiShowIdActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jcb_edit)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiShowId)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiCount, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiCommit)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiRollback)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jbu_action, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                .addComponent(jbu_action, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                .addComponent(guiCount)
                .addComponent(jcb_edit)
                .addComponent(guiRollback)
                .addComponent(guiCommit)
                .addComponent(guiShowId))
        );

        add(jPanel1, BorderLayout.SOUTH);

        panCenter.setLayout(new BorderLayout());
        add(panCenter, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void actionEditer(final ActionEvent evt) {//GEN-FIRST:event_actionEditer
        tab_data.setEditable(((JCheckBox) evt.getSource()).isSelected());
    }//GEN-LAST:event_actionEditer

    private void guiCommitActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_guiCommitActionPerformed

        if(layer != null){
            final FeatureMapLayer candidate = layer;
            new Thread(){
                @Override
                public void run() {
                    lockableUI.setLocked(true);
                    guiCommit.setEnabled(false);
                    guiRollback.setEnabled(false);
                    try {
                        candidate.getCollection().getSession().commit();
                    } catch (DataStoreException ex) {
                        JXErrorPane.showDialog(ex);
                    }finally {
                        lockableUI.setLocked(false);
                        checkChanges();
                    }
                }
            }.start();
        }
    }//GEN-LAST:event_guiCommitActionPerformed

    private void guiRollbackActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_guiRollbackActionPerformed

        if(layer != null){
            final FeatureMapLayer candidate = layer;
            new Thread(){
                @Override
                public void run() {
                    lockableUI.setLocked(true);
                    guiCommit.setEnabled(false);
                    guiRollback.setEnabled(false);
                    candidate.getCollection().getSession().rollback();
                    lockableUI.setLocked(false);
                    reset();
                }

            }.start();
        }
    }//GEN-LAST:event_guiRollbackActionPerformed

    private void guiShowIdActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_guiShowIdActionPerformed
        setTarget(layer);
    }//GEN-LAST:event_guiShowIdActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton guiCommit;
    private JLabel guiCount;
    private JButton guiRollback;
    private JCheckBox guiShowId;
    private JPanel jPanel1;
    private JButton jbu_action;
    private JCheckBox jcb_edit;
    private JPanel panCenter;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void setTarget(final Object target) {

        if(layer != null){
            weakListener.unregisterSource(layer);
            tab_data.getModel().removeTableModelListener(tableListener);
        }

        panCenter.removeAll();

        if (target instanceof FeatureMapLayer) {
            layer = (FeatureMapLayer) target;
            final FeatureCollection<SimpleFeature> source =
                    (FeatureCollection<SimpleFeature>) layer.getCollection();
            editable = source.isWritable();
            
            jcb_edit.setEnabled(editable);

            final FeatureType type = source.getFeatureType();

            if(type instanceof SimpleFeatureType){
                //use table view
                final FeatureCollectionModel m = new FeatureCollectionModel(tab_data, layer, guiShowId.isSelected());
                tab_data.setModel(m);
                tab_data.packAll();
                tab_data.getModel().addTableModelListener(tableListener);
                updateLayerSelection();
                panCenter.add(BorderLayout.CENTER, new JScrollPane(tab_data));
            }else{
                //use outline view
                outline.setTarget(source);
                panCenter.add(BorderLayout.CENTER, outline);
            }

            weakListener.registerSource(layer);
        }

        panCenter.revalidate();

        revalidate();
        repaint();
        checkChanges();
    }
    
    public void updateEditPane(){
        
    }
    

    public FeatureMapLayer getTarget(){
        return layer;
    }

    @Override
    public boolean canHandle(Object target) {
        return target instanceof FeatureMapLayer;
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
        return IconBundle.getIcon("16_feature_table");
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
        setTarget(getTarget());
    }

    private class DynamicMenu extends JPopupMenu{

        public DynamicMenu() {
        }

        @Override
        public void setVisible(final boolean visible) {
            DynamicMenu.this.removeAll();
            if(visible){
                for(final JComponent item : actions){
                    if(item instanceof JFeaturePanelAction){
                        ((JFeaturePanelAction)item).setFeaturePanel(LayerFeaturePropertyPanel.this);
                    }
                    DynamicMenu.this.add(item);
                }
            }
            super.setVisible(visible);
        }

    }


    ////////////////////////////////////////////////////////////////////////////
    //Layer listener ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if(MapLayer.SELECTION_FILTER_PROPERTY.equals(event.getPropertyName())){
            updateLayerSelection();
        }
    }
    @Override
    public void styleChange(final MapLayer source, final EventObject event) {
    }

    @Override
    public void itemChange(final CollectionChangeEvent<MapItem> event) {
    }

}
