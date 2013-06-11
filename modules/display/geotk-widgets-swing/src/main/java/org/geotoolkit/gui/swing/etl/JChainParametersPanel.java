/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.gui.swing.etl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.gui.swing.misc.ActionCell;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.process.chain.model.ChainDataTypes;
import org.geotoolkit.process.chain.model.Parameter;
import org.geotoolkit.process.chain.model.Parameterized;
import org.geotoolkit.process.chain.model.event.ChainListener;
import org.geotoolkit.process.chain.model.event.EventChain;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.jdesktop.swingx.JXTable;

/**
 * Edition panel for Chain input and output parameters.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JChainParametersPanel extends JPanel{

    private static final ImageIcon ICON_EDIT = IconBundle.getIcon("16_edit_geom");
    private static final ImageIcon ICON_DELETE = IconBundle.getIcon("16_remove");

    private final boolean editableInput;
    private final boolean editableOutput;
    private final JDialog optionPaneDialog = new JDialog();

    /**
     * Currently edited chain.
     */
    private final Parameterized process;

    public JChainParametersPanel(final Parameterized process, final boolean editableInput, final boolean editableOutput) {
        super(new GridLayout(2,1));
        this.process = process;
        this.editableInput = editableInput;
        this.editableOutput = editableOutput;

        add(createPanel(true));
        add(createPanel(false));

        setPreferredSize(new Dimension(300, 300));
    }

    private JPanel createPanel(final boolean in){

        final boolean editable = in ? editableInput : editableOutput;
        final JPanel guiTopPanel = new JPanel(new FlowLayout());
        final JPanel guiPanel = new JPanel(new BorderLayout());

        final JLabel guiLabel = new JLabel(
                MessageBundle.getString(in ? "chainInputs" : "chainOutputs"));
        guiLabel.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        guiTopPanel.add(guiLabel);


        final ParametersTableModel model;

        if(process instanceof EventChain) {
            final EventChain chain = (EventChain) process;
            model = new ChainParametersTableModel(chain, in, editable);
        } else {
            model = new ParametersTableModel(process, in, editable);
        }

        if (editable) {
            final JButton guiAdd = new JButton(IconBundle.getIcon("16_add"));
            guiAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(in){
                        process.getInputs().add(new Parameter(nextInCode(),
                                ChainDataTypes.VALID_TYPES.get(0), "", 1, 1));
                    }else{
                        process.getOutputs().add(new Parameter(nextOutCode(),
                                ChainDataTypes.VALID_TYPES.get(0), "", 1, 1));
                    }
                    model.fireTableDataChanged();
                }
            });
            guiTopPanel.add(guiAdd);
        }

        final ActionCell.Renderer cellRendererEdit = new ActionCell.Renderer(ICON_EDIT){

            @Override
            public Icon getIcon(Object value) {
                if(value instanceof DefaultMutableTreeNode){
                    value = ((DefaultMutableTreeNode)value).getUserObject();
                }
                if(value instanceof Parameter){
                    final Parameter parameter = (Parameter) value;
                    return super.getIcon(value);
                }
                return null;
            }

        };
        final ActionCell.Renderer cellRendererRemove = new ActionCell.Renderer(ICON_DELETE){

            @Override
            public Icon getIcon(Object value) {
                if(value instanceof DefaultMutableTreeNode){
                    value = ((DefaultMutableTreeNode)value).getUserObject();
                }
                if(value instanceof Parameter){
                    final Parameter parameter = (Parameter) value;
                    return super.getIcon(value);
                }
                return null;
            }

        };
        final ActionCell.Editor cellEditEdit = new ActionCell.Editor(ICON_EDIT){
            @Override
            public void actionPerformed(ActionEvent e, Object value) {
                if(value instanceof DefaultMutableTreeNode){
                    value = ((DefaultMutableTreeNode)value).getUserObject();
                }
                final Parameter parameter;
                if(value instanceof Parameter){
                    parameter = (Parameter) value;
                }else{
                    return;
                }

                //show edit dialog
                JParameterPanel.showDialog(process,parameter, (in) ? Integer.MIN_VALUE : Integer.MAX_VALUE, in, editable);
                model.fireTableDataChanged();
            }

            @Override
            public Icon getIcon(Object value) {
                if(value instanceof DefaultMutableTreeNode){
                    value = ((DefaultMutableTreeNode)value).getUserObject();
                }
                if(value instanceof Parameter){
                    final Parameter parameter = (Parameter) value;
                    return super.getIcon(value);
                }
                return null;
            }

        };
        final ActionCell.Editor cellEditRemove = new ActionCell.Editor(ICON_DELETE){
            @Override
            public void actionPerformed(ActionEvent e, Object value) {
                if(value instanceof DefaultMutableTreeNode){
                    value = ((DefaultMutableTreeNode)value).getUserObject();
                }
                final Parameter parameter;
                if(value instanceof Parameter){
                    parameter = (Parameter) value;
                }else{
                    return;
                }

                //delete parameter
                if(in){
                    process.getInputs().remove(parameter);
                }else{
                    process.getOutputs().remove(parameter);
                }
                model.fireTableDataChanged();
            }

            @Override
            public Icon getIcon(Object value) {
                if(value instanceof DefaultMutableTreeNode){
                    value = ((DefaultMutableTreeNode)value).getUserObject();
                }
                if(value instanceof Parameter){
                    final Parameter parameter = (Parameter) value;
                    return super.getIcon(value);
                }
                return null;
            }

        };

        final JXTable guiTable = new JXTable(){

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if(column==1){
                    return cellEditEdit;
                }else if(column==2){
                    return cellEditRemove;
                }
                return super.getCellEditor(row, column);
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if(column==1){
                    return cellRendererEdit;
                }else if(column==2){
                    return cellRendererRemove;
                }
                return super.getCellRenderer(row, column);
            }

        };

        guiTable.setModel(model);
        guiTable.setTableHeader(null);
        guiTable.getColumnModel().getColumn(1).setPreferredWidth(40);
        guiTable.getColumnModel().getColumn(1).setMinWidth(40);
        guiTable.getColumnModel().getColumn(1).setWidth(40);
        guiTable.getColumnModel().getColumn(1).setMaxWidth(40);
        guiTable.getColumnModel().getColumn(1).setResizable(false);
        if (guiTable.getColumnModel().getColumnCount() > 2) {
            guiTable.getColumnModel().getColumn(2).setPreferredWidth(40);
            guiTable.getColumnModel().getColumn(2).setMinWidth(40);
            guiTable.getColumnModel().getColumn(2).setMaxWidth(40);
            guiTable.getColumnModel().getColumn(2).setWidth(40);
            guiTable.getColumnModel().getColumn(2).setResizable(false);
        }

        guiTable.setDefaultRenderer(Parameter.class, new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                lbl.setIcon(null);
                if(value instanceof Parameter){
                    final Parameter param = (Parameter) value;
                    lbl.setText(param.getCode());
                }

                return lbl;
            }

        });


        guiPanel.add(BorderLayout.NORTH, guiTopPanel);
        guiPanel.add(BorderLayout.CENTER, new JScrollPane(guiTable));

        return guiPanel;
    }

    /**
     * Generate next unused input parameter code.
     */
    private String nextInCode(){
        int i=1;
        incloop:
        for(;;i++){
            for(final Parameter param : process.getInputs()){
                if( ("in"+i).equalsIgnoreCase(param.getCode())){
                    continue incloop;
                }
            }
            break incloop;
        }
        return "in"+i;
    }

    /**
     * Generate next unused output parameter code.
     */
    private String nextOutCode(){
        int i=1;
        incloop:
        for(;;i++){
            for(final Parameter param : process.getOutputs()){
                if( ("out"+i).equalsIgnoreCase(param.getCode())){
                    continue incloop;
                }
            }
            break incloop;
        }
        return "out"+i;
    }

    private class ChainParametersTableModel extends ParametersTableModel implements ChainListener{

        public ChainParametersTableModel(final EventChain chain,final boolean in, final boolean editable) {
            super(chain, in, editable);
            chain.addListener(this);
        }

        private void updateModel(){
            fireTableDataChanged();
        }

        @Override
        public void constantChange(CollectionChangeEvent event) {
        }

        @Override
        public void descriptorChange(CollectionChangeEvent event) {
        }

        @Override
        public void linkChange(CollectionChangeEvent event) {
        }

        @Override
        public void executionLinkChange(CollectionChangeEvent event) {
        }

        @Override
        public void inputChange(CollectionChangeEvent event) {
            if(!in) {return;}
            updateModel();
        }

        @Override
        public void outputChange(CollectionChangeEvent event) {
            if(in) {return;}
            updateModel();
        }
    }
    
    /**
     * Open JDialog of editor.
     */
    public void showDialog() {

        final JOptionPane optPane = new JOptionPane(this,
                JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

        optPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("value")) {
                    optionPaneDialog.dispose();
                }
            }
        });
        optionPaneDialog.setTitle("");
        optionPaneDialog.setContentPane(optPane);
        optionPaneDialog.pack();
        optionPaneDialog.setResizable(true);
        optionPaneDialog.setLocationRelativeTo(null);
        optionPaneDialog.setModal(true);
        optionPaneDialog.setVisible(true);
    }
}
