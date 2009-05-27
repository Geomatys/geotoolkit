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
package org.geotoolkit.gui.swing.report;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.report.JRMappingUtils;
import org.geotoolkit.report.JRMapper;
import org.geotoolkit.report.JRMapperFactory;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RenderDataProvider;
import org.netbeans.swing.outline.RowModel;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JRConfigTree<C> extends Outline{

    private static String STR_PARAMETERS    = "Parameters";
    private static String STR_FIELDS        = "Fields";
    private static ImageIcon ICON_FIELD     = IconBundle.getInstance().getIcon("16_jasper_field");
    private static ImageIcon ICON_PARAMETER = IconBundle.getInstance().getIcon("16_jasper_parameter");


    private JRModel treeModel = null;
    private JRRowModel rowModel = null;

    public JRConfigTree(){

        setShowGrid(false);
        setTableHeader(null);
        setRootVisible(false);
        setOpaque(true);

        setFillsViewportHeight(true);

        setRenderDataProvider(new RenderDataProvider() {

            @Override
            public String getDisplayName(Object candidate) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) candidate;
                Object userObj = node.getUserObject();

                if(userObj instanceof String){
                    String name = (String) userObj;
                    return "<html><b>"+name+"</b></html>";
                }else if(userObj instanceof JRField){
                    JRField field = (JRField) userObj;
                    return field.getName();
                }else if(userObj instanceof JRParameter){
                    JRParameter param = (JRParameter) userObj;
                    return param.getName();
                }else{
                    return candidate.toString();
                }

            }

            @Override
            public boolean isHtmlDisplayName(Object candidate) {
                return true;
            }

            @Override
            public Color getBackground(Object candidate) {
                return Color.WHITE;
            }

            @Override
            public Color getForeground(Object candidate) {
                return Color.BLACK;
            }

            @Override
            public String getTooltipText(Object candidate) {
                return null;
            }

            @Override
            public Icon getIcon(Object candidate) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) candidate;
                Object userObj = node.getUserObject();

                if(userObj instanceof String){
                    String str = (String) userObj;
                    if(str.equals(STR_FIELDS)){
                        return ICON_FIELD;
                    }else if(str.equals(STR_PARAMETERS)){
                        return ICON_PARAMETER;
                    }else{
                        return null;
                    }

                }else if(userObj instanceof JRField){
                    return ICON_FIELD;
                }else if(userObj instanceof JRParameter){
                    return ICON_PARAMETER;
                }else{
                    return null;
                }
            }
        });

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    public Map<Object,Object> getParameters(){
        Map<Object,Object> parameters = new HashMap<Object, Object>();

        for(JRParameter param : treeModel.design.getParameters()){
            Object val = rowModel.values.get(param);
            if(val != null && val instanceof JRMapper){
                JRMapper mapper = (JRMapper) val;
                parameters.put(param.getName(), mapper.getValue(Collections.emptyList()));
            }
        }

        return parameters;
    }

    public Map<String,JRMapper<?,? super C>> getMapping(){
        Map<String,JRMapper<?,? super C>> fields = new HashMap<String, JRMapper<?,? super C>>();

        for(JRField field : treeModel.design.getFields()){
            Object val = rowModel.values.get(field);
            if(val != null && val instanceof JRMapper){
                JRMapper mapper = (JRMapper) val;
                fields.put(field.getName(), mapper);
            }
        }

        return fields;
    }

    public void setDesign(JasperDesign design){
        treeModel = new JRModel(design);
        rowModel = new JRRowModel();

        OutlineModel outlineModel = DefaultOutlineModel.createOutlineModel(treeModel, rowModel);
        setModel(outlineModel);

        getColumnModel().getColumn(1).setCellRenderer(new JRCellRenderer());
        getColumnModel().getColumn(1).setCellEditor(new JRCellEditor());

    }

    public JasperDesign getDesign(JasperDesign design){
        return treeModel.design;
    }

    private class JRModel implements TreeModel{

        private final JasperDesign design;
        private final DefaultMutableTreeNode root;
        private final DefaultMutableTreeNode parameters;
        private final DefaultMutableTreeNode fields;

        JRModel(JasperDesign design){
            this.design = design;
            root = new DefaultMutableTreeNode("root");
            parameters = new DefaultMutableTreeNode(STR_PARAMETERS);
            fields = new DefaultMutableTreeNode(STR_FIELDS);

            root.add(parameters);
            root.add(fields);

            for(final JRParameter param : design.getParameters()){
                parameters.add(new DefaultMutableTreeNode(param));
            }

            for(final JRField field : design.getFields()){
                fields.add(new DefaultMutableTreeNode(field));
            }

        }


        @Override
        public Object getRoot() {
            return root;
        }

        @Override
        public Object getChild(Object candidate, int index) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) candidate;
            return node.getChildAt(index);
        }

        @Override
        public int getChildCount(Object candidate) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) candidate;
            return node.getChildCount();
        }

        @Override
        public boolean isLeaf(Object candidate) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) candidate;
            return node.isLeaf();
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent;
            return node.getIndex( (TreeNode)child);
        }

        @Override
        public void addTreeModelListener(TreeModelListener l) {
        }

        @Override
        public void removeTreeModelListener(TreeModelListener l) {
        }

    }

    private class JRRowModel implements RowModel{

        private Map<Object,Object> values = new HashMap<Object,Object>();


        private Object findBestMapper(JRField field){
            final String name = field.getName();
            final Class classe = field.getValueClass();

            final List<JRMapperFactory> factories = JRMappingUtils.getFactories((Class)classe);

            for(final JRMapperFactory factory : factories){
                final String[] favorites = factory.getFavoritesFieldName();
                for(final String favorite : favorites){
                    if(favorite.equals(name)){
                        return factory.createMapper();
                    }
                }
            }

            return classe;
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueFor(Object candidate, int index) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) candidate;
            Object userObj = node.getUserObject();

            if(userObj instanceof String){
                return null;
            }else if(userObj instanceof JRField){
                JRField field = (JRField) userObj;
                if(!values.containsKey(field)) values.put(field, findBestMapper(field));
                return values.get(field);
            }else if(userObj instanceof JRParameter){
                JRParameter param = (JRParameter)userObj;
                if(!values.containsKey(param)) values.put(param, param.getValueClass());
                return values.get(param);
            }else{
                return null;
            }
        }

        @Override
        public Class getColumnClass(int arg0) {
            return Object.class;
        }

        @Override
        public boolean isCellEditable(Object candidate, int index) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) candidate;
            Object userObj = node.getUserObject();

            if(userObj instanceof String){
                return false;
            }else if(userObj instanceof JRField){
                return true;
            }else if(userObj instanceof JRParameter){
                return true;
            }else{
                return false;
            }
        }

        @Override
        public void setValueFor(Object candidate, int index, Object newValue) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) candidate;
            Object userObj = node.getUserObject();

            if(userObj instanceof String){
            }else if(userObj instanceof JRField){
                JRField field = (JRField) userObj;
                values.put(field, newValue);
            }else if(userObj instanceof JRParameter){
                JRParameter param = (JRParameter)userObj;
                values.put(param, newValue);
            }
        }

        @Override
        public String getColumnName(int arg0) {
            return "editor";
        }

    }

    private class JRCellRenderer implements TableCellRenderer{

        private final JLabel label = new JLabel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        
            if(value == null){
                label.setText("");
                label.setIcon(null);
            }else if(value instanceof Class){
                label.setText("");
                label.setIcon(null);
            }else if(value instanceof JRMapper){
                JRMapper mapper = (JRMapper) value;
                label.setText(mapper.getFactory().getTitle().toString());
                Image img = mapper.getFactory().getIcon(0);
                final Icon icon;
                if(img == null){
                    icon = IconBundle.EMPTY_ICON;
                }else{
                    icon = new ImageIcon(img);
                }
                label.setIcon(icon);
            }else{
                label.setText("");
                label.setIcon(null);
            }
            
            return label;

        }

    }

    private class JRCellEditor extends AbstractCellEditor implements TableCellEditor{

        private final JComboBox jbox = new JComboBox();
        private JRMapper mapper = null;

        public JRCellEditor(){
            jbox.setRenderer(new ListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    if(value == null){
                        return new JLabel();
                    }else if( value instanceof JRMapperFactory){
                        JRMapperFactory factory = (JRMapperFactory) value;
                        Image img = factory.getIcon(0);
                        final Icon icon;
                        if(img == null){
                            icon = IconBundle.EMPTY_ICON;
                        }else{
                            icon = new ImageIcon(img);
                        }

                        return new JLabel(factory.getTitle().toString(),icon,SwingConstants.LEADING );
                    }else{
                        return new JLabel();
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

            mapper = null;
            
            if(value == null) return new JLabel();

            if(value instanceof Class){
                List<JRMapperFactory> factories = JRMappingUtils.getFactories((Class)value);
                jbox.setModel(new ListComboBoxModel(factories));
                return jbox;
            }else if(value instanceof JRMapper){
                mapper = (JRMapper) value;
                List<JRMapperFactory> factories = JRMappingUtils.getFactories(mapper.getFactory().getFieldClass());
                factories.add(null);
                jbox.setModel(new ListComboBoxModel(factories));
                jbox.setSelectedItem(mapper.getFactory());
                return jbox;
            }else{
                return new JLabel();
            }
        }

        @Override
        public Object getCellEditorValue() {
            Object obj = jbox.getSelectedItem();

            if(obj == null) return null;

            JRMapperFactory factory = (JRMapperFactory) obj;

            if(mapper == null){
                return factory.createMapper();
            }

            if(factory.equals(mapper.getFactory())){
                //same mapper no change
                return mapper;
            }else{
                //different factory, create a new mapper
                return factory.createMapper();
            }
        }

    }
}
