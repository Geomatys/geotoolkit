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

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.TreeSet;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.render.SymbolStyle;
import org.jdesktop.swingx.JXTable;
import org.openide.util.Exceptions;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JS52SymbolPane extends JPanel{

    private final S52Context context;

    public JS52SymbolPane(final S52Context context) {
        setLayout(new BorderLayout());
        this.context = context;

        final TreeSet<String> names = new TreeSet<>();
        names.addAll(context.getAvailableStyles());

        final StyleTableModel paletteModel = new StyleTableModel(names.toArray(new String[0]));

        final JXTable table = new JXTable(paletteModel);
        table.getColumn(1).setCellRenderer(new StyleCellRenderer());
        table.setRowHeight(60);

        add(BorderLayout.CENTER, new JScrollPane(table));
        setPreferredSize(new Dimension(400, 400));
    }


    private class StyleTableModel extends AbstractTableModel{

        private final String[] styleNames;

        public StyleTableModel(String[] syleNames) {
            this.styleNames = syleNames;
        }

        @Override
        public String getColumnName(int column) {
            if(column==0){
                return "code";
            }else if(column==1){
                return "preview";
            }else{
                return "desc";
            }
        }

        @Override
        public int getRowCount() {
            return styleNames.length;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            final String styleCode = styleNames[rowIndex];
            if(columnIndex==0){
                return styleCode;
            }else if(columnIndex==1){
                return context.getSyle(styleCode);
            }else{
                return context.getSyle(styleCode).explication.EXPT;
            }
        }

    }

    private class StyleCellRenderer extends DefaultTableCellRenderer{

        private final JPanel pane = new JPanel(new GridBagLayout());

        public StyleCellRenderer() {
            pane.setBackground(Color.WHITE);
            pane.removeAll();
            pane.add(this);
        }



        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setOpaque(true);

            if(value instanceof SymbolStyle){
                lbl.setBackground(Color.WHITE);
                lbl.setVerticalTextPosition(JLabel.BOTTOM);
                lbl.setHorizontalTextPosition(JLabel.CENTER);
                final SymbolStyle ss = (SymbolStyle) value;

                try {
                    final BufferedImage image = ss.asImage(context,2);
                    lbl.setIcon(new ImageIcon(image));
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    lbl.setIcon(null);
                }

                lbl.setText("");
            }

            return pane;
        }

    }

}
