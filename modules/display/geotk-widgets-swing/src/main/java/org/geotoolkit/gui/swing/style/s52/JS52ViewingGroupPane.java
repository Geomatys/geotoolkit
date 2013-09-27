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
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.lookuptable.IMODisplayCategory;
import org.geotoolkit.s52.lookuptable.LookupRecord;
import org.geotoolkit.s52.lookuptable.LookupTable;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RowModel;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JS52ViewingGroupPane extends JPanel{

    private final S52Context context;
    private final Outline outline;

    public JS52ViewingGroupPane(S52Context context) {
        super(new BorderLayout());
        this.context = context;

        //find all groups defined in this style
        final SortedSet<Integer> groups = new TreeSet<>();
        for(String name : context.getAvailablePointTables()){
            explore(context.getLookupTable(name), groups);
        }
        for(String name : context.getAvailableLineTables()){
            explore(context.getLookupTable(name), groups);
        }
        for(String name : context.getAvailableAreaTables()){
            explore(context.getLookupTable(name), groups);
        }

        //makes groups
        //BASE
        // chart : 10000 - 19999
        // mariner : 40000 - 49999
        final List<Integer> baseChart = subset(groups, 10000, 19999);
        final List<Integer> baseMariner = subset(groups, 40000, 49999);
        //Standard
        // chart : 20000 - 29999
        // mariner : 50000 - 59999
        final List<Integer> standardChart = subset(groups, 20000, 29999);
        final List<Integer> standardMariner = subset(groups, 50000, 59999);
        //Other
        // chart : 30000 - 39999
        // mariner : 60000 - 69999
        final List<Integer> otherChart = subset(groups, 30000, 39999);
        final List<Integer> otherMariner = subset(groups, 60000, 69999);
        //might remain some unknowns
        final List<Integer> unknow = new ArrayList<>(groups);


        //recreate the tree
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        final DefaultMutableTreeNode nbase = new DefaultMutableTreeNode("Base");
        final DefaultMutableTreeNode nbaseChart = new DefaultMutableTreeNode("Chart (10000 - 19999)");
        final DefaultMutableTreeNode nbaseMariner = new DefaultMutableTreeNode("Mariner (40000 - 49999)");
        final DefaultMutableTreeNode nstandard = new DefaultMutableTreeNode("Standard");
        final DefaultMutableTreeNode nstandardChart = new DefaultMutableTreeNode("Chart (20000 - 29999)");
        final DefaultMutableTreeNode nstandardMariner = new DefaultMutableTreeNode("Mariner (50000 - 59999)");
        final DefaultMutableTreeNode nother = new DefaultMutableTreeNode("Other");
        final DefaultMutableTreeNode notherChart = new DefaultMutableTreeNode("Chart (30000 - 39999)");
        final DefaultMutableTreeNode notherMariner = new DefaultMutableTreeNode("Mariner (60000 - 69999)");
        final DefaultMutableTreeNode nunknown = new DefaultMutableTreeNode("Unknowned");

        root.add(nbase);
            nbase.add(nbaseChart);
                fillNode(nbaseChart, baseChart);
            nbase.add(nbaseMariner);
                fillNode(nbaseMariner, baseMariner);
        root.add(nstandard);
            nstandard.add(nstandardChart);
                fillNode(nstandardChart, standardChart);
            nstandard.add(nstandardMariner);
                fillNode(nstandardMariner, standardMariner);
        root.add(nother);
            nother.add(notherChart);
                fillNode(notherChart, otherChart);
            nother.add(notherMariner);
                fillNode(notherMariner, otherMariner);
        root.add(nunknown);
            fillNode(nunknown, unknow);


        final TreeModel tm = new DefaultTreeModel(root);
        final RowModel rm = new GroupRowModel();
        final OutlineModel model = DefaultOutlineModel.createOutlineModel(tm, rm);
        outline = new Outline(model);
        outline.setRootVisible(false);
        outline.setBackground(Color.WHITE);
        outline.setOpaque(true);
        outline.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0, 0)));
        outline.setGridColor(new Color(0, 0, 0, 0));
        outline.setTableHeader(null);

        add(BorderLayout.CENTER, new JScrollPane(outline));

    }

    private void explore(LookupTable table, SortedSet<Integer> groups){
        for(LookupRecord rec : table.getRecords()){
            if(rec.getViewingGroup()!=null){
                groups.add(rec.getViewingGroup());
            }else if(rec.getDisplayCaegory() != null){
                if(rec.getDisplayCaegory().equals(IMODisplayCategory.DISPLAYBASE)){
                    groups.add(10000);
                }else if(rec.getDisplayCaegory().equals(IMODisplayCategory.STANDARD)){
                    groups.add(20000);
                }else if(rec.getDisplayCaegory().equals(IMODisplayCategory.OTHER)){
                    groups.add(30000);
                }else if(rec.getDisplayCaegory().equals(IMODisplayCategory.MARINERS_DISPLAYBASE)){
                    groups.add(40000);
                }else if(rec.getDisplayCaegory().equals(IMODisplayCategory.MARINERS_STANDARD)){
                    groups.add(50000);
                }else if(rec.getDisplayCaegory().equals(IMODisplayCategory.MARINERS_OTHER)){
                    groups.add(60000);
                }else if(rec.getDisplayCaegory().equals(IMODisplayCategory.NULL)){
                    groups.add(99999);
                }
            }
        }
    }

    /**
     *
     * @param min inclusive
     * @param max inclusive
     * @return
     */
    private List<Integer> subset(SortedSet<Integer> groups, int min, int max){
        final List<Integer> candidates = new ArrayList<>();
        for(Integer i : groups){
            if(i>=min && i<=max) candidates.add(i);
        }
        groups.removeAll(candidates);
        return candidates;
    }

    private void fillNode(DefaultMutableTreeNode node, List<Integer> values){
        for(Integer i : values){
            node.add(new DefaultMutableTreeNode(i));
        }
    }

    private class GroupRowModel implements RowModel{

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public Object getValueFor(Object o, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Class getColumnClass(int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean isCellEditable(Object o, int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setValueFor(Object o, int i, Object o1) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getColumnName(int i) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }


}
