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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.s57.TypeBanks;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.internal.swing.table.BooleanRenderer;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.lookuptable.IMODisplayCategory;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RenderDataProvider;
import org.netbeans.swing.outline.RowModel;
import org.opengis.feature.type.FeatureType;
import org.opengis.util.InternationalString;
import org.openide.util.Exceptions;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JS52ViewingGroupPane extends JPanel{

    private static final ImageIcon ICON_VISIBLE = IconBuilder.createIcon(FontAwesomeIcons.ICON_EYE_OPEN, 16, Color.BLACK);
    private static final ImageIcon ICON_UNVISIBLE = IconBuilder.createIcon(FontAwesomeIcons.ICON_EYE_CLOSE, 16, Color.LIGHT_GRAY);

    private final S52Context context;
    private final JRadioButton chartBase        = new JRadioButton(MessageBundle.getString("s52.base"));
    private final JRadioButton chartStandard    = new JRadioButton(MessageBundle.getString("s52.standard"));
    private final JRadioButton chartOther       = new JRadioButton(MessageBundle.getString("s52.other"));
    private final JRadioButton marinerBase      = new JRadioButton(MessageBundle.getString("s52.base"));
    private final JRadioButton marinerStandard  = new JRadioButton(MessageBundle.getString("s52.standard"));
    private final JRadioButton marinerOther     = new JRadioButton(MessageBundle.getString("s52.other"));
    private final Map<String,List<TypeRecord>> groups = new HashMap<>();

    public JS52ViewingGroupPane(S52Context context) {
        super(new BorderLayout());
        this.context = context;

        final JPanel panel          = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(3, 3, 3, 3));
        final JLabel lblChart       = new JLabel(MessageBundle.getString("s52.chart"));
        final JLabel lblMariner     = new JLabel(MessageBundle.getString("s52.mariner"));


        chartBase.setSelected(IMODisplayCategory.DISPLAYBASE.equals(context.getDisplayChartCategory())
                           || IMODisplayCategory.NULL.equals(context.getDisplayChartCategory()));
        chartStandard.setSelected(IMODisplayCategory.STANDARD.equals(context.getDisplayChartCategory()));
        chartOther.setSelected(IMODisplayCategory.OTHER.equals(context.getDisplayChartCategory()));
        marinerBase.setSelected(IMODisplayCategory.MARINERS_DISPLAYBASE.equals(context.getDisplayMarinerCategory()));
        marinerStandard.setSelected(IMODisplayCategory.MARINERS_STANDARD.equals(context.getDisplayMarinerCategory()));
        marinerOther.setSelected(IMODisplayCategory.MARINERS_OTHER.equals(context.getDisplayMarinerCategory()));


        final ButtonGroup chartGroup = new ButtonGroup();
        chartGroup.add(chartBase);
        chartGroup.add(chartStandard);
        chartGroup.add(chartOther);
        final ButtonGroup marinerGroup = new ButtonGroup();
        marinerGroup.add(marinerBase);
        marinerGroup.add(marinerStandard);
        marinerGroup.add(marinerOther);

        panel.add(lblChart,         new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        panel.add(chartBase,        new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(chartStandard,    new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(chartOther,       new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(lblMariner,       new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        panel.add(marinerBase,      new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(marinerStandard,  new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(marinerOther,     new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(BorderLayout.NORTH,panel);

        try {
            final Map<Integer,String> types = TypeBanks.getAllFeatureTypes();

            for(Entry<Integer,String> entry : types.entrySet()){
                final FeatureType ft = TypeBanks.getFeatureType(entry.getKey(), DefaultGeographicCRS.WGS84);
                final String spec = TypeBanks.getFeatureTypeSpecification(entry.getValue());
                final TypeRecord rec = new TypeRecord(entry.getKey(),entry.getValue(),true,ft.getDescription());
                rec.visible = !context.getHiddenClasses().contains(rec.name);

                List<TypeRecord> groupRecords = groups.get(spec);
                if(groupRecords == null){
                    groupRecords = new ArrayList<>();
                    groups.put(spec, groupRecords);
                }
                groupRecords.add(rec);
            }

            final DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
            for(Entry<String,List<TypeRecord>> entry : groups.entrySet()){
                final DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(entry.getKey());
                root.add(groupNode);

                for(TypeRecord rec : entry.getValue()){
                    final DefaultMutableTreeNode typeNode = new DefaultMutableTreeNode(rec);
                    groupNode.add(typeNode);
                }
            }

            final TreeModel tm = new DefaultTreeModel(root);
            final RowModel rm = new TypeRowModel();
            final OutlineModel om = DefaultOutlineModel.createOutlineModel(tm, rm);

            final Outline outline = new Outline(om);
            outline.setRootVisible(false);
            outline.setBackground(Color.WHITE);
            outline.setOpaque(true);
            outline.setGridColor(new Color(0, 0, 0, 0));
            outline.setRowHeight(24);
            outline.setShowVerticalLines(false);
            outline.setShowHorizontalLines(false);
            outline.setIntercellSpacing(new Dimension(0, 0));
            outline.getColumnModel().getColumn(0).setPreferredWidth(160);
            outline.getColumnModel().getColumn(0).setMaxWidth(160);
            outline.getColumnModel().getColumn(1).setPreferredWidth(60);
            outline.getColumnModel().getColumn(1).setMaxWidth(60);

            final BooleanRenderer renderer = new BooleanRenderer();
            renderer.setPressedIcon(         ICON_UNVISIBLE);
            renderer.setRolloverIcon(        ICON_UNVISIBLE);
            renderer.setRolloverSelectedIcon(ICON_VISIBLE);
            renderer.setIcon(                ICON_UNVISIBLE);
            renderer.setSelectedIcon(        ICON_VISIBLE);
            renderer.setDisabledIcon(        ICON_UNVISIBLE);
            renderer.setDisabledSelectedIcon(ICON_VISIBLE);
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
            outline.getColumnModel().getColumn(1).setCellRenderer(renderer);

            outline.setRenderDataProvider(new RenderDataProvider() {

                @Override
                public String getDisplayName(Object o) {
                    if(o instanceof DefaultMutableTreeNode) o = ((DefaultMutableTreeNode)o).getUserObject();
                    if(o instanceof String){
                        return o.toString();
                    }else if(o instanceof TypeRecord){
                        return ((TypeRecord)o).name +" ("+((TypeRecord)o).code+")";
                    }
                    return "";
                }

                @Override
                public boolean isHtmlDisplayName(Object o) {
                    return false;
                }

                @Override
                public Color getBackground(Object o) {
                    return null;
                }

                @Override
                public Color getForeground(Object o) {
                    return null;
                }

                @Override
                public String getTooltipText(Object o) {
                    return null;
                }

                @Override
                public Icon getIcon(Object o) {
                    return null;
                }
            });

            final JScrollPane scroll = new JScrollPane(outline, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            add(BorderLayout.CENTER,scroll);

        } catch (DataStoreException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public void apply(){
        //set display categories
        if(chartBase.isSelected()){
            context.setDisplayChartCategory(IMODisplayCategory.DISPLAYBASE);
        }else if(chartStandard.isSelected()){
            context.setDisplayChartCategory(IMODisplayCategory.STANDARD);
        }else{
            context.setDisplayChartCategory(IMODisplayCategory.OTHER);
        }

        if(marinerBase.isSelected()){
            context.setDisplayMarinerCategory(IMODisplayCategory.MARINERS_DISPLAYBASE);
        }else if(marinerStandard.isSelected()){
            context.setDisplayMarinerCategory(IMODisplayCategory.MARINERS_STANDARD);
        }else{
            context.setDisplayMarinerCategory(IMODisplayCategory.MARINERS_OTHER);
        }

        //update hidden classes
        context.getHiddenClasses().clear();
        for(Entry<String,List<TypeRecord>> entry : groups.entrySet()){
            for(TypeRecord rec : entry.getValue()){
                if(!rec.visible) context.getHiddenClasses().add(rec.name);
            }
        }

    }

    private static class TypeRecord{
        public int code;
        public String name;
        public boolean visible;
        public InternationalString description;

        public TypeRecord(int code, String name, boolean visible, InternationalString description) {
            this.code = code;
            this.name = name;
            this.visible = visible;
            this.description = description;
        }

    }

    private static class TypeRowModel implements RowModel{

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueFor(Object o, int columnIndex) {
            if(o instanceof DefaultMutableTreeNode) o = ((DefaultMutableTreeNode)o).getUserObject();
            if(o instanceof String){
                switch(columnIndex){
                    case 0 : return true;
                    case 1 : return "";
                }
                return "";
            }else{
                final TypeRecord rec = (TypeRecord) o;
                switch(columnIndex){
                    case 0 : return rec.visible;
                    case 1 : return rec.description == null ? "" : rec.description.toString();
                }
                return "";
            }
        }

        @Override
        public Class getColumnClass(int col) {
            return (col==0) ? Boolean.class : String.class;
        }

        @Override
        public boolean isCellEditable(Object o, int i) {
            return i == 0;
        }

        @Override
        public void setValueFor(Object o, int i, Object o1) {
            if(o instanceof DefaultMutableTreeNode) o = ((DefaultMutableTreeNode)o).getUserObject();
            if(o instanceof String){
                //TODO modify all sub types
            }else{
                final TypeRecord rec = (TypeRecord) o;
                rec.visible = (Boolean)o1;
            }

        }

        @Override
        public String getColumnName(int column) {
            switch(column){
                case 0 : return MessageBundle.getString("s52.visible");
                case 1 : return MessageBundle.getString("s52.description");
            }
            return "";
        }

    }

}
