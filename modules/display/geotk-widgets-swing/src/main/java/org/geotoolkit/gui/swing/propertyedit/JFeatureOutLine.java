/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.gui.swing.util.EmptyCellRenderer;
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.*;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.apache.sis.util.Classes;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.netbeans.swing.outline.*;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.apache.sis.util.logging.Logging;
import org.opengis.feature.Feature;

/**
 * Property editor, can edit Feature/Complex attribut or single properties.
 * Additionaly Parameter can be edited since their model is close.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
public class JFeatureOutLine extends Outline{

    private static final ImageIcon ICON_ADD = IconBuilder.createIcon(FontAwesomeIcons.ICON_CIRCLE_O, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_REMOVE = IconBuilder.createIcon(FontAwesomeIcons.ICON_CIRCLE, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_OCC_ADD = IconBuilder.createIcon(FontAwesomeIcons.ICON_PLUS_SQUARE, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final ImageIcon ICON_OCC_REMOVE = IconBuilder.createIcon(FontAwesomeIcons.ICON_MINUS_SQUARE, 16, FontAwesomeIcons.DEFAULT_COLOR);

    private final List<PropertyValueEditor> editors = new CopyOnWriteArrayList<PropertyValueEditor>();
    private final JFeatureOutLine.PropertyRowModel rowModel = new JFeatureOutLine.PropertyRowModel();
    private FeatureTreeModel treeModel = null;
    private Feature edited = null;

    public JFeatureOutLine(){
        setRenderDataProvider(new JFeatureOutLine.PropertyDataProvider());
        setShowHorizontalLines(false);
        //setColumnSelectionAllowed(false);
        setFillsViewportHeight(true);
        setBackground(Color.WHITE);
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        editors.addAll(JAttributeEditor.createDefaultEditorList());
    }

    /**
     * Set the property to display in this component.
     */
    public void setEdited(final Feature property){
        this.edited = property;
        treeModel = new FeatureTreeModel(property);

        if(SwingUtilities.isEventDispatchThread()){
            updateModel(treeModel, property);
        }else{
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        updateModel(treeModel, property);
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Logging.getLogger("org.geotoolkit.gui.swing.propertyedit").log(Level.SEVERE, null, ex);
            }
        }

    }

    private void updateModel(final FeatureTreeModel treeModel,final Feature prop){
        setModel(DefaultOutlineModel.createOutlineModel(treeModel, rowModel));
        getColumnModel().getColumn(0).setHeaderValue(" ");
        getColumnModel().getColumn(0).setResizable(true);
        getColumnModel().getColumn(1).setResizable(true);
        getColumnModel().getColumn(2).setMinWidth(36);
        getColumnModel().getColumn(2).setMaxWidth(36);
        getColumnModel().getColumn(2).setResizable(false);
    }

    /**
     * Get the property displayed in this component.
     */
    public Feature getEdited(){
        return edited;
    }

    /**
     * Set the property to display in this component. Parameters are not the
     * natural model expected, but since Parameters are close to Features. An
     * automatic translation is done.
     */
    public void setEdited(final ParameterValueGroup parameter) {
        setEdited(FeatureExt.toFeature(parameter));
    }

    /**
     * Return the edited property as a Parameter
     *
     * @param desc parameter descriptor
     * @return ParameterValueGroup
     */
    public ParameterValueGroup getEditedAsParameter(final ParameterDescriptorGroup desc) {
        return FeatureExt.toParameter(edited, desc);
    }

    /**
     * @return live list of property editors.
     */
    public List<PropertyValueEditor> getEditors() {
        return editors;
    }

    @Override
    public TableCellEditor getCellEditor(final int row, final int column) {
        //TODO editor obsolete with new feature api
        throw new RuntimeException("Editor do not support new feature API yet.");
    }

    @Override
    public TableCellEditor getDefaultEditor(final Class<?> columnClass) {
        if(columnClass != null && Date.class.isAssignableFrom(columnClass)){
            return new DatePickerCellEditor();
        }
        return super.getDefaultEditor(columnClass);
    }

    @Override
    public TableCellRenderer getCellRenderer(final int row, final int column) {
        //TODO editor obsolete with new feature api
        throw new RuntimeException("Editor do not support new feature API yet.");
    }

    private class PropertyRowModel implements RowModel{

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueFor(final Object o, final int i) {
            return (MutableTreeNode) o;
        }

        @Override
        public Class getColumnClass(final int i) {
            return Object.class;
        }

        @Override
        public boolean isCellEditable(final Object o, final int i) {
        //TODO editor obsolete with new feature api
        throw new RuntimeException("Editor do not support new feature API yet.");
        }

        @Override
        public void setValueFor(final Object o, final int i, final Object value) {
        //TODO editor obsolete with new feature api
        throw new RuntimeException("Editor do not support new feature API yet.");
        }

        @Override
        public String getColumnName(final int i) {
            return "";
        }

    }

    public static class PropertyDataProvider implements RenderDataProvider {

        @Override
        public java.awt.Color getBackground(final Object o) {
            return null;
        }

        @Override
        public String getDisplayName(final Object o) {
            //TODO editor obsolete with new feature api
            throw new RuntimeException("Editor do not support new feature API yet.");
        }

        @Override
        public java.awt.Color getForeground(final Object o) {
            //TODO editor obsolete with new feature api
            throw new RuntimeException("Editor do not support new feature API yet.");
        }

        @Override
        public javax.swing.Icon getIcon(final Object o) {
            //TODO editor obsolete with new feature api
            throw new RuntimeException("Editor do not support new feature API yet.");
        }

        @Override
        public String getTooltipText(final Object o) {
            //TODO editor obsolete with new feature api
            throw new RuntimeException("Editor do not support new feature API yet.");
        }

        @Override
        public boolean isHtmlDisplayName(final Object o) {
            return true;
        }
    }

    private final class GeometryCellRenderer extends DefaultOutlineCellRenderer{

        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            final JLabel lbl = (JLabel) super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
            if(value instanceof Geometry || value instanceof org.opengis.geometry.Geometry){
                lbl.setText("~");
            }
            return lbl;
        }

    }

    private final class ActionCellRenderer extends DefaultOutlineCellRenderer {
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            return getActionComponent((JFeatureOutLine)table,value);
        }
    }

    private final class ActionCellEditor extends AbstractCellEditor implements TableCellEditor{
        @Override
        public Component getTableCellEditorComponent(final JTable table, final Object value,
                final boolean isSelected, final int row, final int column) {
            Component comp = getActionComponent((JFeatureOutLine)table, value);
            if(comp instanceof JButton){
                ((JButton)comp).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        fireEditingStopped();
                    }
                });
            }

            return comp;
        }
        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    public Component getActionComponent(final JFeatureOutLine outline, final Object value) {
        //TODO editor obsolete with new feature api
        throw new RuntimeException("Editor do not support new feature API yet.");
    }

    public static void show(Component parent, final Feature candidate){
        show(parent, candidate, false);
    }

    public static void show(Component parent, final Feature candidate, boolean modal){
        final JFeatureOutLine outline = new JFeatureOutLine();
        outline.setEdited(candidate);
        JOptionDialog.show(parent, outline, JOptionPane.OK_OPTION);
    }

}
