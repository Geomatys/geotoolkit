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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.geotoolkit.cql.JCQLTextPane;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.Selector;
import org.geotoolkit.data.query.Source;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.gui.swing.util.LoadingLockableUI;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.PropertyValueEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.TableCellEditorRenderer;
import org.geotoolkit.gui.swing.propertyedit.model.FeatureCollectionModel;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.LayerListener;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.style.RandomStyleBuilder;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionHistory;
import org.geotoolkit.version.Versioned;
import org.geotoolkit.version.VersioningException;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;
import org.openide.util.Exceptions;

/**
 * layer feature panel
 *
 * @author  Johann Sorel
 * @module
 */
public class LayerFeaturePropertyPanel extends AbstractPropertyPane implements LayerListener {

    private static final ImageIcon ICON_TABLE = IconBuilder.createIcon(FontAwesomeIcons.ICON_LIST, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_VERSIONED = IconBuilder.createIcon(FontAwesomeIcons.ICON_CLOCK_O, 16, FontAwesomeIcons.DEFAULT_COLOR);

    public static final String ACTION_REF = "LayerFeaturePropertyPanel";

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


    private final List<Action> actions = new ArrayList<Action>();
    private final LockableUI lockableUI = new LoadingLockableUI();
    private final JCQLTextPane guiCQL = new JCQLTextPane();

    private FeatureMapLayer layer = null;
    private boolean editable = false;
    private final LayerListener.Weak weakListener = new LayerListener.Weak(this);
    private final List<PropertyValueEditor> editors = new CopyOnWriteArrayList<>();

    private final JXTable tab_data = new JXTable(){
        @Override
        public TableCellEditor getCellEditor(final int row, final int column) {

            final FeatureCollectionModel model = (FeatureCollectionModel) tab_data.getModel();
            final int modelindex = tab_data.getColumnModel().getColumn(column).getModelIndex();

            final PropertyType desc = model.getColumnDesc(modelindex);
            if(desc != null){
                final PropertyValueEditor edit = JAttributeEditor.getEditor(editors, (AttributeType) desc);
                if(edit != null){
                    return new TableCellEditorRenderer.Editor(edit);
                }
            }

            return super.getCellEditor(row, column);
        }
    };
    private final JFeatureCollectionOutline outline = new JFeatureCollectionOutline();


    /** Creates new form DefaultMapLayerTablePanel */
    public LayerFeaturePropertyPanel() {
        super(MessageBundle.format("property_feature_table"),ICON_TABLE,null,MessageBundle.format("crs"));
        initComponents();

        JLabel label = new JLabel(MessageBundle.format("cql_text_help"));
        guiPanFilter.add(BorderLayout.NORTH, label);
        guiPanFilter.add(BorderLayout.CENTER,guiCQL);

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
        tab_data.setDefaultEditor(Versioned.class, new VersionEditor());
        tab_data.setDefaultRenderer(Versioned.class, new VersionEditor());
        editors.addAll(JAttributeEditor.createDefaultEditorList());

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
     * @return live list of property editors.
     */
    public List<PropertyValueEditor> getEditors() {
        return editors;
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
            ids.add(new DefaultFeatureId((String)model.getValueAt(i, 1)));
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

    public List<Action> actions(){
        return actions;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new JPanel();
        guiQueryButton = new JButton();
        guiPanFilter = new JPanel();
        jPanel4 = new JPanel();
        jPanel3 = new JPanel();
        jLabel1 = new JLabel();
        guiVersions = new JComboBox();
        jPanel1 = new JPanel();
        jcb_edit = new JCheckBox();
        guiCount = new JLabel();
        guiCommit = new JButton();
        guiRollback = new JButton();
        jbu_action = new JButton();
        guiShowId = new JCheckBox();
        panCenter = new JPanel();

        setLayout(new BorderLayout());

        jPanel2.setLayout(new BorderLayout());

        guiQueryButton.setText(MessageBundle.format("ok")); // NOI18N
        guiQueryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiQueryButtonActionPerformed(evt);
            }
        });
        jPanel2.add(guiQueryButton, BorderLayout.EAST);

        guiPanFilter.setPreferredSize(new Dimension(10, 0));
        guiPanFilter.setLayout(new BorderLayout());
        jPanel2.add(guiPanFilter, BorderLayout.CENTER);

        jPanel4.setLayout(new GridLayout(2, 1));

        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText(MessageBundle.format("version")); // NOI18N

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel3);
        jPanel4.add(guiVersions);

        jPanel2.add(jPanel4, BorderLayout.WEST);

        add(jPanel2, BorderLayout.NORTH);

        jPanel1.setOpaque(false);

        jcb_edit.setText(MessageBundle.format("property_edit")); // NOI18N
        jcb_edit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jcb_edit.setEnabled(false);
        jcb_edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionEditer(evt);
            }
        });

        guiCount.setHorizontalAlignment(SwingConstants.CENTER);
        guiCount.setText(" ");

        guiCommit.setText(MessageBundle.format("commit")); // NOI18N
        guiCommit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiCommitActionPerformed(evt);
            }
        });

        guiRollback.setText(MessageBundle.format("rollback")); // NOI18N
        guiRollback.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiRollbackActionPerformed(evt);
            }
        });

        jbu_action.setText(MessageBundle.format("property_action")); // NOI18N

        guiShowId.setText(MessageBundle.format("show_id")); // NOI18N
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
                .addContainerGap()
                .addComponent(guiCount)
                .addPreferredGap(ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
                .addComponent(jcb_edit)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiShowId)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(guiCommit)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(guiRollback)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(jbu_action))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jcb_edit)
                        .addComponent(guiShowId)
                        .addComponent(guiCount))
                    .addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jbu_action, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                        .addComponent(guiRollback)
                        .addComponent(guiCommit)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {guiCommit, guiCount, guiRollback, guiShowId, jbu_action, jcb_edit});

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

    private void guiQueryButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiQueryButtonActionPerformed

        try {
            panCenter.removeAll();

            Filter f = guiCQL.getFilter();
            final QueryBuilder qb = new QueryBuilder(layer.getCollection().getFeatureType().getName().toString());
            qb.setFilter(f);
            if(guiVersions.getSelectedItem()!=null && !(guiVersions.getSelectedItem() instanceof String)){
                final Date d = ((Version)guiVersions.getSelectedItem()).getDate();
                qb.setVersionDate(d);
            }
            final FeatureCollection subcol = layer.getCollection().subCollection(qb.buildQuery());
            FeatureMapLayer layer = MapBuilder.createFeatureLayer(subcol, RandomStyleBuilder.createDefaultRasterStyle());
            final FeatureCollectionModel m = new FeatureCollectionModel(tab_data, layer, guiShowId.isSelected());
            tab_data.setModel(m);
            tab_data.packAll();
            tab_data.getModel().addTableModelListener(tableListener);
            updateLayerSelection();
            panCenter.add(BorderLayout.CENTER, new JScrollPane(tab_data));

            panCenter.revalidate();
            revalidate();
            repaint();
            checkChanges();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_guiQueryButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton guiCommit;
    private JLabel guiCount;
    private JPanel guiPanFilter;
    private JButton guiQueryButton;
    private JButton guiRollback;
    private JCheckBox guiShowId;
    private JComboBox guiVersions;
    private JLabel jLabel1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
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
            final FeatureCollection source = layer.getCollection();
            editable = source.isWritable();

            jcb_edit.setEnabled(editable);

            final FeatureType type = source.getFeatureType();

            if(type.isSimple()){
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

            //list versions
            final Source src = source.getSource();
            final List lst = new ArrayList();
            lst.add("-");
            if(src instanceof Selector){
                final Selector s = (Selector) src;
                final FeatureStore store = s.getSession().getFeatureStore();
                if(store.getQueryCapabilities().handleVersioning()){
                    try {
                        final VersionHistory history = store.getVersioning(source.getFeatureType().getName().toString());
                        lst.addAll(history.list());
                    } catch (VersioningException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            guiVersions.setModel(new ListComboBoxModel(lst));
            if (lst.size() < 2) {
                guiVersions.setVisible(false);
                jLabel1.setVisible(false);
            } else {
                guiVersions.setVisible(true);
            }

        }
        guiCommit.setVisible(false);
        guiRollback.setVisible(false);
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
                for(final Action item : actions){
                    item.putValue(ACTION_REF, LayerFeaturePropertyPanel.this);
                    final JFeaturePanelAction jfa = new JFeaturePanelAction(item);
                    jfa.setFeaturePanel(LayerFeaturePropertyPanel.this);
                    DynamicMenu.this.add(jfa);
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

    private class VersionEditor extends AbstractCellEditor implements TableCellRenderer,TableCellEditor{

        private final JLabel lbl = new JLabel(ICON_VERSIONED);

        public VersionEditor() {
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(value instanceof Versioned){
                return new VersionButton((Versioned)value);
            }else{
                return new JLabel("");
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if(value instanceof Versioned){
                return new VersionButton((Versioned)value);
            }else{
                return new JLabel("");
            }
        }
    }

    private class VersionButton extends JButton implements ActionListener{

        private Versioned versioned;

        public VersionButton(Versioned v) {
            super(ICON_VERSIONED);
            setMargin(new Insets(0, 0, 0, 0));
            this.versioned = v;
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            final JComboBox jcb = new JComboBox();
            try{
                ComboBoxModel m = new ListComboBoxModel(versioned.getHistory().list());
                jcb.setModel(m);
            }catch(Exception ex){
                ex.printStackTrace();
            }
            final JFeatureOutLine editor = new JFeatureOutLine();
            editor.setEnabled(false);

            jcb.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    final Version v = (Version) jcb.getSelectedItem();
                    try{
                        Feature o = (Feature) versioned.getForVersion(v);
                        editor.setEdited(o);
                    }catch(Exception ex){
                        ex.printStackTrace();
                        editor.setEdited((Feature)null);
                    }
                }
            });
            final Version v = (Version) jcb.getSelectedItem();
            if(v!=null){
                try{
                    Feature o = (Feature) versioned.getForVersion(v);
                    editor.setEdited(o);
                }catch(Exception ex){
                    ex.printStackTrace();
                    editor.setEdited((Feature)null);
                }
            }

            final JPanel p = new JPanel(new BorderLayout());
            p.add(BorderLayout.NORTH,jcb);
            p.add(BorderLayout.CENTER,new JScrollPane(editor));

            JOptionDialog.show(VersionButton.this, p, JOptionPane.OK_OPTION);
        }

    }

}
