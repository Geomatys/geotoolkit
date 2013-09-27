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
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JS52PalettePane extends JPanel{

    private final S52Context context;

    public JS52PalettePane(final S52Context context) {
        setLayout(new BorderLayout());
        this.context = context;

        final SortedSet<String> colorCodes = new TreeSet<>();
        final List<String> paletteNames = context.getAvailablePalettes();
        for(String name : paletteNames){
            final S52Palette palette = context.getPalette(name);
            colorCodes.addAll(palette.getColorNames());
        }
        Collections.sort(paletteNames);

        final PaletteTableModel paletteModel = new PaletteTableModel(
                colorCodes.toArray(new String[0]), paletteNames.toArray(new String[0]));

        final JXTable table = new JXTable(paletteModel);
        for(int i=0;i<paletteNames.size();i++){
            table.getColumn(i+1).setCellRenderer(new org.geotoolkit.util.ColorCellRenderer());
        }

        add(BorderLayout.CENTER, new JScrollPane(table));
    }


    private class PaletteTableModel extends AbstractTableModel{

        private final String[] colorCodes;
        private final String[] paletteNames;

        public PaletteTableModel(String[] colorCodes, String[] paletteNames) {
            this.colorCodes = colorCodes;
            this.paletteNames = paletteNames;
        }

        @Override
        public String getColumnName(int column) {
            if(column==0){
                return "code";
            }else if(column== (1+paletteNames.length)){
                return "desc";
            }else{
                return paletteNames[column-1];
            }
        }

        @Override
        public int getRowCount() {
            return colorCodes.length;
        }

        @Override
        public int getColumnCount() {
            return 2 + paletteNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            final String colorCode = colorCodes[rowIndex];
            if(columnIndex==0){
                return colorCode;
            }else if(columnIndex== (1+paletteNames.length)){
                return context.getPalette(paletteNames[0]).getColorDef(colorCode).CUSE;
            }else{
                return context.getPalette(paletteNames[columnIndex-1]).getColor(colorCode);
            }
        }

    }

    private static class ColorCellRenderer extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setOpaque(true);

            if(value instanceof Color){
                lbl.setBackground(((Color)value));
                lbl.setText("   ");
            }

            return lbl;
        }

    }

}
