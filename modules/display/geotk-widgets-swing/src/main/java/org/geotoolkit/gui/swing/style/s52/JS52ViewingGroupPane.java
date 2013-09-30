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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.s57.TypeBanks;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.lookuptable.IMODisplayCategory;
import org.geotoolkit.s52.lookuptable.LookupRecord;
import org.geotoolkit.s52.lookuptable.LookupTable;
import org.jdesktop.swingx.JXTable;
import org.opengis.feature.type.FeatureType;
import org.opengis.util.InternationalString;
import org.openide.util.Exceptions;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JS52ViewingGroupPane extends JPanel{

    private final S52Context context;
    private int nbChartBase = 0;
    private int nbChartStandard = 0;
    private int nbChartOther = 0;
    private int nbMarinerBase = 0;
    private int nbMarinerStandard = 0;
    private int nbMarinerOther = 0;

    public JS52ViewingGroupPane(S52Context context) {
        super(new BorderLayout());
        this.context = context;

        //find all groups defined in this style
        for(String name : context.getAvailablePointTables()){
            explore(context.getLookupTable(name));
        }
        for(String name : context.getAvailableLineTables()){
            explore(context.getLookupTable(name));
        }
        for(String name : context.getAvailableAreaTables()){
            explore(context.getLookupTable(name));
        }

        final JPanel panel          = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(3, 3, 3, 3));
        final JLabel lblChart       = new JLabel(MessageBundle.getString("s52.chart"));
        final JLabel lblMariner     = new JLabel(MessageBundle.getString("s52.mariner"));
        final JRadioButton chartBase        = new JRadioButton(MessageBundle.getString("s52.base"));
        final JRadioButton chartStandard    = new JRadioButton(MessageBundle.getString("s52.standard"));
        final JRadioButton chartOther       = new JRadioButton(MessageBundle.getString("s52.other"));
        final JRadioButton marinerBase      = new JRadioButton(MessageBundle.getString("s52.base"));
        final JRadioButton marinerStandard  = new JRadioButton(MessageBundle.getString("s52.standard"));
        final JRadioButton marinerOther     = new JRadioButton(MessageBundle.getString("s52.other"));

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
            final JXTable table = new JXTable();

            final List<TypeRecord> records = new ArrayList<>();
            for(Entry<Integer,String> entry : types.entrySet()){
                final FeatureType ft = TypeBanks.getFeatureType(entry.getKey(), DefaultGeographicCRS.WGS84);
                records.add(new TypeRecord(entry.getKey(),entry.getValue(),true,ft.getDescription() ));
            }

            table.setModel(new TypeModel(records));
            table.getColumn(0).setPreferredWidth(50);
            table.getColumn(0).setMaxWidth(50);
            table.getColumn(1).setPreferredWidth(50);
            table.getColumn(1).setMaxWidth(50);
            table.getColumn(2).setPreferredWidth(100);
            table.getColumn(2).setMaxWidth(100);
            add(BorderLayout.CENTER,new JScrollPane(table));


        } catch (DataStoreException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public void apply(){

    }

    private void explore(LookupTable table){
        for(LookupRecord rec : table.getRecords()){
            if(rec.getDisplayCategory() != null){
                if(rec.getDisplayCategory().equals(IMODisplayCategory.DISPLAYBASE)){
                    nbChartBase++;
                }else if(rec.getDisplayCategory().equals(IMODisplayCategory.STANDARD)){
                    nbChartStandard++;
                }else if(rec.getDisplayCategory().equals(IMODisplayCategory.OTHER)){
                    nbChartOther++;
                }else if(rec.getDisplayCategory().equals(IMODisplayCategory.MARINERS_DISPLAYBASE)){
                    nbMarinerBase++;
                }else if(rec.getDisplayCategory().equals(IMODisplayCategory.MARINERS_STANDARD)){
                    nbMarinerStandard++;
                }else if(rec.getDisplayCategory().equals(IMODisplayCategory.MARINERS_OTHER)){
                    nbMarinerOther++;
                }else if(rec.getDisplayCategory().equals(IMODisplayCategory.NULL)){
                    // what to do with those ?
                }
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

    private static class TypeModel extends AbstractTableModel{

        private final List<TypeRecord> types;

        public TypeModel(List<TypeRecord> types) {
            this.types = types;
        }

        @Override
        public String getColumnName(int column) {
            switch(column){
                case 0 : return MessageBundle.getString("s52.visible");
                case 1 : return MessageBundle.getString("s52.code");
                case 2 : return MessageBundle.getString("s52.class");
                case 3 : return MessageBundle.getString("s52.description");
            }
            return "";
        }

        @Override
        public int getRowCount() {
            return types.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {

            final TypeRecord rec = types.get(rowIndex);
            switch(columnIndex){
                case 0 : return rec.visible;
                case 1 : return rec.code;
                case 2 : return rec.name;
                case 3 : return rec.description == null ? "" : rec.description.toString();
            }
            return "";
        }

    }

}
