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
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.gui.swing.misc.EmptyCellRenderer;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.*;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.geotoolkit.util.converter.Classes;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.netbeans.swing.outline.*;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;

/**
 * Property editor, can edit Feature/Complex attribut or single properties.
 * Additionaly Parameter can be edited since their model is close.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JFeatureOutLine extends Outline{

    private static final ImageIcon ICON_ADD = IconBundle.getIcon("16_smallgray");
    private static final ImageIcon ICON_REMOVE = IconBundle.getIcon("16_smallgreen");
    private static final ImageIcon ICON_OCC_ADD = IconBundle.getIcon("16_occurence_add");
    private static final ImageIcon ICON_OCC_REMOVE = IconBundle.getIcon("16_occurence_remove");

    private final List<PropertyValueEditor> editors = new CopyOnWriteArrayList<PropertyValueEditor>();
    private final JFeatureOutLine.PropertyRowModel rowModel = new JFeatureOutLine.PropertyRowModel();
    private FeatureTreeModel treeModel = null;
    private Property edited = null;

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
    public void setEdited(final Property property){
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
            } catch (InterruptedException ex) {
                Logger.getLogger(JFeatureOutLine.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(JFeatureOutLine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void updateModel(final FeatureTreeModel treeModel,final Property prop){
        setModel(DefaultOutlineModel.createOutlineModel(treeModel, rowModel));
        setRootVisible(!(prop instanceof ComplexAttribute));
        getColumnModel().getColumn(0).setHeaderValue(" ");
        getColumnModel().getColumn(0).setResizable(true);
        getColumnModel().getColumn(1).setResizable(true);
        getColumnModel().getColumn(2).setMinWidth(26);
        getColumnModel().getColumn(2).setMaxWidth(26);
        getColumnModel().getColumn(2).setResizable(false);
    }

    /**
     * Get the property displayed in this component.
     */
    public Property getEdited(){
        return edited;
    }

    /**
     * Set the property to display in this component. Parameters are not the
     * natural model expected, but since Parameters are close to Features. An
     * automatic translation is done.
     */
    public void setEdited(final ParameterValueGroup parameter) {
        setEdited(FeatureUtilities.toFeature(parameter));
    }

    /**
     * Return the edited property as a Parameter
     *
     * @param desc parameter descriptor
     * @return ParameterValueGroup
     */
    public ParameterValueGroup getEditedAsParameter(final ParameterDescriptorGroup desc) {
        return FeatureUtilities.toParameter((ComplexAttribute) edited, desc);
    }

    /**
     * @return live list of property editors.
     */
    public List<PropertyValueEditor> getEditors() {
        return editors;
    }

    @Override
    public TableCellEditor getCellEditor(final int row, final int column) {
        if(column == 2){
            return new JFeatureOutLine.ActionCellEditor();
        }

        final MutableTreeNode node = (MutableTreeNode) getValueAt(row, 0);
        PropertyType type = null;
        final Object obj = node.getUserObject();
        if(obj instanceof Property){
            type = ((Property)obj).getType();
        }

        if(column == 1){
            final PropertyValueEditor edit = JAttributeEditor.getEditor(editors,type);
            if(edit != null){
                return new TableCellEditorRenderer.Editor(edit);
            }
        }

        //fallback on default java editor.
        final Class c = type.getBinding();
        return getDefaultEditor(c);
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
        if(column == 2){
            return new JFeatureOutLine.ActionCellRenderer();
        }

        final MutableTreeNode node = (MutableTreeNode) getValueAt(row, 0);
        PropertyType type = null;
        final Object obj = node.getUserObject();
        if(obj instanceof Property){
            type = ((Property)obj).getType();
        }

        if(column == 1 && type != null){
            final PropertyValueEditor edit = JAttributeEditor.getEditor(editors,type);
            if(edit != null){
                return new TableCellEditorRenderer.Renderer(edit);
            }
        }

        //fallback on default java editor.
        final Object value = getValueAt(row, column);
        if(value instanceof Geometry || value instanceof org.opengis.geometry.Geometry){
            return new JFeatureOutLine.GeometryCellRenderer();
        }

        if(column >= 1){
            return new EmptyCellRenderer();
        }else{
            return super.getCellRenderer(row, column);
        }

    }

    private class PropertyRowModel implements RowModel{

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueFor(final Object o, final int i) {
            final MutableTreeNode node = (MutableTreeNode) o;
            final Object candidate = node.getUserObject();

            if(i==0){
                return node;
//                //first column, property value
//                if(candidate instanceof Property && !(candidate instanceof ComplexAttribute)){
//                    return ((Property)candidate).getValue();
//                }else{
//                    return null;
//                }
            }else{
                //second column, actions
                return node;
            }

        }

        @Override
        public Class getColumnClass(final int i) {
            return Object.class;
        }

        @Override
        public boolean isCellEditable(final Object o, final int i) {
            if(i==1) return true;

            //property value, check it's editable
            final MutableTreeNode node = (MutableTreeNode) o;

            if(node.getUserObject() instanceof Property){
                final Property prop = (Property) node.getUserObject();
                final Class type = prop.getType().getBinding();
                return !(prop instanceof ComplexAttribute)
                      && JAttributeEditor.getEditor(editors,prop.getType()) != null
                      || getDefaultEditor(type) != getDefaultEditor(Object.class);
            }else{
                return false;
            }
        }

        @Override
        public void setValueFor(final Object o, final int i, final Object value) {
            if(i==1)return; //action column

            final MutableTreeNode node = (MutableTreeNode) o;
            final Property prop = (Property) node.getUserObject();
            prop.setValue(value);
        }

        @Override
        public String getColumnName(final int i) {
            return "";
        }

    }

    private static class PropertyDataProvider implements RenderDataProvider {

        @Override
        public java.awt.Color getBackground(final Object o) {
            return null;
        }

        @Override
        public String getDisplayName(final Object o) {
            final MutableTreeNode node = (MutableTreeNode) o;
            final Object candidate = node.getUserObject();



            final Name name;
            if(candidate instanceof Property){

                Property prop = ((Property)candidate);
                final PropertyDescriptor desc = prop.getDescriptor();

                if(desc != null){
                    final Object origin = desc.getType().getUserData().get("origin");
                    if(origin instanceof GeneralParameterDescriptor){
                        final GeneralParameterDescriptor pd = (GeneralParameterDescriptor) origin;
                        if(pd.getAlias() != null && !pd.getAlias().isEmpty()){
                            final GenericName gn = pd.getAlias().iterator().next();
                            name = new DefaultName(null, gn.toInternationalString().toString());
                        }else{
                            name = prop.getName();
                        }
                    }else{
                        name = prop.getName();
                    }
                }else{
                    name = prop.getName();
                }


            }else if(candidate instanceof PropertyDescriptor){

                final PropertyDescriptor desc = ((PropertyDescriptor)candidate);

                if(desc != null){
                    final Object origin = desc.getType().getUserData().get("origin");
                    if(origin instanceof GeneralParameterDescriptor){
                        final GeneralParameterDescriptor pd = (GeneralParameterDescriptor) origin;
                        if(pd.getAlias() != null && !pd.getAlias().isEmpty()){
                            final GenericName gn = pd.getAlias().iterator().next();
                            name = new DefaultName(null, gn.toInternationalString().toString());
                        }else{
                            name = desc.getName();
                        }
                    }else{
                        name = desc.getName();
                    }
                }else{
                    name = desc.getName();
                }

            }else{
                name = null;
            }

            String text;
            if(name != null){
                text = name.getLocalPart();
            }else{
                text = "";
            }

            final StringBuilder sb = new StringBuilder();

            if(candidate instanceof Property){
                PropertyDescriptor desc = ((Property)candidate).getDescriptor();
                if(desc != null &&desc.getMaxOccurs() > 1){
                    //we have to find this property index
                    final int index = node.getParent().getIndex(node);
                    text = "["+index+"] "+text;
                }
            }

            if(candidate instanceof ComplexAttribute){
                sb.append("<b>");
                sb.append(text);
                sb.append("</b>");
            }else if(candidate instanceof PropertyDescriptor){
                final PropertyDescriptor desc = (PropertyDescriptor) candidate;

                sb.append("<i>");
                if(desc.getMaxOccurs() > 1){
                    sb.append("[~] ");
                }
                sb.append(text);
                sb.append("</i>");
            }else{
                sb.append(text);
            }

            return sb.toString();
        }

        @Override
        public java.awt.Color getForeground(final Object o) {
            final MutableTreeNode node = (MutableTreeNode) o;
            final Object candidate = node.getUserObject();

            if(candidate instanceof PropertyDescriptor){
                final PropertyDescriptor desc = (PropertyDescriptor) candidate;
                final int nb = node.getChildCount();

                if(nb == 0){
                    return Color.LIGHT_GRAY;
                }

            }

            return null;
        }

        @Override
        public javax.swing.Icon getIcon(final Object o) {
            final MutableTreeNode node = (MutableTreeNode) o;
            final Object prop = node.getUserObject();
            if(prop instanceof ComplexAttribute){
                return IconBundle.EMPTY_ICON;
            }else{
                return IconBundle.EMPTY_ICON;
            }
        }

        @Override
        public String getTooltipText(final Object o) {
            final MutableTreeNode node = (MutableTreeNode) o;
            final Object userObject = node.getUserObject();

            if(userObject instanceof Property){
                final InternationalString i18n = ((Property) node.getUserObject()).getDescriptor().getType().getDescription();
                String tooltip = String.valueOf(i18n);
                tooltip += " ("+Classes.getShortName(((Property) node.getUserObject()).getType().getBinding()) +")";
                return tooltip;
            }else{
                return null;
            }

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

        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        if(node == null){
            return new JLabel();
        }

        final TreePath path = new TreePath(node.getPath());
        final Object obj = node.getUserObject();

        if(obj instanceof PropertyDescriptor){
            final PropertyDescriptor desc = (PropertyDescriptor) obj;
            final int nbProp = node.getChildCount();

            if(desc.getMaxOccurs() > nbProp){
                final int max = desc.getMaxOccurs();
                final ImageIcon icon = (max>1) ? ICON_OCC_ADD : ICON_ADD;
                final JButton butAdd = new JButton(icon);
                butAdd.setBorderPainted(false);
                butAdd.setContentAreaFilled(false);
                butAdd.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        treeModel.createProperty(path);
                    }
                });
                return butAdd;
            }

        }else if(obj instanceof Property){
            final Property prop = (Property) obj;
            final PropertyDescriptor desc = prop.getDescriptor();

            if(desc != null && desc.getMinOccurs() == 0){
                final int max = desc.getMaxOccurs();
                final ImageIcon icon = (max>1) ? ICON_OCC_REMOVE : ICON_REMOVE;
                final JButton butRemove = new JButton(icon);
                butRemove.setBorderPainted(false);
                butRemove.setContentAreaFilled(false);
                butRemove.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        treeModel.removeProperty(path);
                    }
                });
                return butRemove;
            }
        }

        return new JLabel();
    }

    public static void show(final Property candidate){
        show(candidate, false);
    }

    public static void show(final Property candidate, boolean modal){
        final JDialog dialog = new JDialog();
        final JFeatureOutLine outline = new JFeatureOutLine();
        outline.setEdited(candidate);
        dialog.setContentPane(new JScrollPane(outline));
        dialog.setSize(600, 600);
        dialog.setModal(modal);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

}
