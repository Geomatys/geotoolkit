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
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.geotoolkit.util.Converters;

import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.netbeans.swing.outline.DefaultOutlineCellRenderer;

import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.RenderDataProvider;
import org.netbeans.swing.outline.RowModel;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyType;

/**
 * Property editor, can edit Feature/Complex attribut or single properties.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JFeatureOutLine extends Outline{

    private Property edited = null;
    
    private final PropertyRowModel rowModel = new PropertyRowModel();
    
    public JFeatureOutLine(){
        setRenderDataProvider(new PropertyDataProvider());
        setShowHorizontalLines(false);
        setColumnSelectionAllowed(false);
        setFillsViewportHeight(true);
        setBackground(Color.WHITE);
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    /**
     * Set the property to display in this component.
     */
    public void setEdited(Property property){
        this.edited = property;
        final TreeModel model = new DefaultTreeModel(toNode(property));
        setModel(DefaultOutlineModel.createOutlineModel(model, rowModel));
        setRootVisible(!(property instanceof ComplexAttribute));
        getColumnModel().getColumn(0).setMinWidth(100);
    }
    
    /**
     * Get the property displayed in this component.
     */
    public Property getEdited(){
        return edited;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        final MutableTreeNode node = (MutableTreeNode) getValueAt(row, 0);
        final Property prop = (Property) node.getUserObject();
        final PropertyType type = prop.getType();
        final Class c = type.getBinding();
        return getDefaultEditor(c);
    }

    @Override
    public TableCellEditor getDefaultEditor(Class<?> columnClass) {
        if(Date.class.isAssignableFrom(columnClass)){
            return new DatePickerCellEditor();
        }
        return super.getDefaultEditor(columnClass);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        final Object value = getValueAt(row, column);
        if(value instanceof Geometry || value instanceof org.opengis.geometry.Geometry){
            return new GeometryCellRenderer();
        }
        return super.getCellRenderer(row, column);
    }
      
    private static MutableTreeNode toNode(Property property){
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(property);
        if(property instanceof ComplexAttribute){
            final ComplexAttribute att = (ComplexAttribute) property;
            for(Property prop : att.getProperties()){
                node.add(toNode(prop));
            }
        }
        return node;
    }    
        
    private class PropertyRowModel implements RowModel{
        
        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueFor(Object o, int i) {
            MutableTreeNode node = (MutableTreeNode) o;
            final Property prop = (Property) node.getUserObject();
            
            if(prop instanceof ComplexAttribute){
                return null;
            }else{
                return prop.getValue();
            }
            
        }

        @Override
        public Class getColumnClass(int i) {
            return Object.class;
        }

        @Override
        public boolean isCellEditable(Object o, int i) {
            final MutableTreeNode node = (MutableTreeNode) o;
            final Property prop = (Property) node.getUserObject();
            final Class type = prop.getType().getBinding();
            return !(prop instanceof ComplexAttribute) 
                  && (type == String.class || getDefaultEditor(type) != getDefaultEditor(Object.class));
        }

        @Override
        public void setValueFor(Object o, int i, Object value) {
            final MutableTreeNode node = (MutableTreeNode) o;
            final Property prop = (Property) node.getUserObject();
            prop.setValue(Converters.convert(value, prop.getType().getBinding()));
        }

        @Override
        public String getColumnName(int i) {
            return "";
        }
        
    }
    
    private static class PropertyDataProvider implements RenderDataProvider {

        @Override
        public java.awt.Color getBackground(Object o) {
            return null;
        }

        @Override
        public String getDisplayName(Object o) {
            final MutableTreeNode node = (MutableTreeNode) o;
            final Property prop = (Property) node.getUserObject();
            final StringBuilder sb = new StringBuilder();

            final String text;
            final Name name = prop.getName();
            if(name != null){
                text = name.getLocalPart();
            }else{
                text = "";
            }

            if(prop instanceof ComplexAttribute){
                sb.append("<b>");
                sb.append(text);
                sb.append("</b>");
            }else{
                sb.append(text);
            }
            
            return sb.toString();
        }

        @Override
        public java.awt.Color getForeground(Object o) {
            return null;
        }

        @Override
        public javax.swing.Icon getIcon(Object o) {
            final MutableTreeNode node = (MutableTreeNode) o;
            final Property prop = (Property) node.getUserObject();
            if(prop instanceof ComplexAttribute){
                return IconBundle.getInstance().getIcon("16_attach");
            }else{
                return IconBundle.EMPTY_ICON;
            }            
        }

        @Override
        public String getTooltipText(Object o) {
            final MutableTreeNode node = (MutableTreeNode) o;
            return DefaultName.toJCRExtendedForm( ((Property) node.getUserObject()).getName());
        }

        @Override
        public boolean isHtmlDisplayName(Object o) {
            return true;
        }
    }

    private final class GeometryCellRenderer extends DefaultOutlineCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final JLabel lbl = (JLabel) super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
            if(value instanceof Geometry || value instanceof org.opengis.geometry.Geometry){
                lbl.setText("~");
            }
            return lbl;
        }

    }

}
