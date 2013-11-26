/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.gui.swing.style.s52;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import org.geotoolkit.gui.swing.misc.ActionCell;
import org.geotoolkit.gui.swing.misc.JOptionDialog;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.lookuptable.LookupRecord;
import org.geotoolkit.s52.lookuptable.LookupTable;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JS52LookupTablePane extends JPanel{

    private static final ImageIcon VIEW_ICON = IconBuilder.createIcon(FontAwesomeIcons.ICON_INFO_SIGN, 16, Color.BLACK);

    private final S52Context context;
    private final JXTable table = new JXTable();

    /**
     * Display a combo box on top of the panel to select lookup table.
     * @param context
     */
    public JS52LookupTablePane(final S52Context context) {
        setLayout(new BorderLayout());
        this.context = context;

        final List<LookupTable> tables = new ArrayList<>();
        for(String name : context.getAvailablePointTables()){
            tables.add(context.getLookupTable(name));
        }
        for(String name : context.getAvailableLineTables()){
            tables.add(context.getLookupTable(name));
        }
        for(String name : context.getAvailableAreaTables()){
            tables.add(context.getLookupTable(name));
        }
        final JComboBox cb = new JComboBox();
        cb.setModel(new ListComboBoxModel(tables));
        cb.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                final JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof LookupTable){
                    lbl.setText( ((LookupTable)value).getName());
                }
                return lbl;
            }
        });
        cb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                final LookupTable lk = (LookupTable) cb.getSelectedItem();
                setModel(lk);
            }
        });
        if(!tables.isEmpty()){
            cb.setSelectedIndex(0);
            setModel(tables.get(0));
        }

        add(BorderLayout.NORTH, cb);
        add(BorderLayout.CENTER, new JScrollPane(table));
        setPreferredSize(new Dimension(400, 400));
    }

    /**
     * Display a single lookup table.
     *
     * @param context
     * @param lk
     */
    public JS52LookupTablePane(final S52Context context, final LookupTable lk) {
        setLayout(new BorderLayout());
        this.context = context;
        table.setModel(new LookupTableModel(lk));

        add(BorderLayout.CENTER, new JScrollPane(table));
        setPreferredSize(new Dimension(400, 400));
    }

    private void setModel(LookupTable lk){
        if(lk==null){
            table.setModel(new DefaultTableModel());
            return;
        }

        table.setModel(new LookupTableModel(lk));
        table.getColumnExt(4).setCellRenderer(new ActionCell.Renderer(VIEW_ICON));
        table.getColumnExt(4).setCellEditor(new ActionCell.Editor(VIEW_ICON) {
            @Override
            public void actionPerformed(ActionEvent e, Object value) {
                final JLookupRecordPane pane = new JLookupRecordPane((LookupRecord) value);
                JOptionDialog.show(JS52LookupTablePane.this, pane, JOptionPane.OK_OPTION);
            }
        });
        table.getColumnExt(4).setMinWidth(20);
        table.getColumnExt(4).setMaxWidth(20);
        table.getColumnExt(4).setPreferredWidth(20);
    }


    private static class LookupTableModel extends AbstractTableModel{

        private final LookupTable lk;


        public LookupTableModel(LookupTable lk) {
            this.lk = lk;
        }

        @Override
        public String getColumnName(int column) {
            switch(column){
                case 0 : return MessageBundle.getString("s52.class");
                case 1 : return MessageBundle.getString("s52.priority");
                case 2 : return MessageBundle.getString("s52.radar");
                case 3 : return MessageBundle.getString("s52.category");
                case 4 : return "";
            }
            return "";
        }

        @Override
        public int getRowCount() {
            return lk.getRecords().size();
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            final LookupRecord rec = lk.getRecords().get(rowIndex);
            switch(columnIndex){
                case 0 : return rec.getObjectClass();
                case 1 : return rec.getPriority();
                case 2 : return rec.getRadar();
                case 3 : return rec.getDisplayCategory();
                case 4 : return rec;
            }
            return "";
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            //do nothing
        }

    }

}
