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
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.lookuptable.LookupRecord;
import org.geotoolkit.s52.lookuptable.LookupTable;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JS52LookupTablePane extends JPanel{

    private final S52Context context;
    private final LookupTable lk;

    public JS52LookupTablePane(final S52Context context, final LookupTable lk) {
        setLayout(new BorderLayout());
        this.context = context;
        this.lk = lk;

        final LookupTableModel paletteModel = new LookupTableModel();

        final JXTable table = new JXTable(paletteModel);

        add(BorderLayout.CENTER, new JScrollPane(table));
        setPreferredSize(new Dimension(400, 400));
    }


    private class LookupTableModel extends AbstractTableModel{

        public LookupTableModel() {
        }

        @Override
        public String getColumnName(int column) {
            switch(column){
                case 0 : return "class";
                case 1 : return "priority";
                case 2 : return "radar";
                case 3 : return "category";
                case 4 : return "view group";
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
        public Object getValueAt(int rowIndex, int columnIndex) {

            final LookupRecord rec = lk.getRecords().get(rowIndex);
            switch(columnIndex){
                case 0 : return rec.getObjectClass();
                case 1 : return rec.getPriority();
                case 2 : return rec.getRadar();
                case 3 : return rec.getDisplayCaegory();
                case 4 : return rec.getViewingGroup();
            }
            return "";
        }

    }

}
