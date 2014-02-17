/*
 *    Puzzle GIS - Desktop GIS Platform
 *    http://puzzle-gis.codehaus.org
 *
 *    (C) 2007-2009, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.swing.shapefile;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 * TableModel for fields edition in the shapefile creation tool.
 * 
 * @author Johann Sorel
 */
class ShapeAttModel extends AbstractTableModel{

    private final List<Field> datas = new ArrayList<Field>();
    
    /**
     * {@inheritDoc }
     */
    @Override
    public int getRowCount() {
        return datas.size();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getColumnCount() {        
        return 2;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getColumnName(final int columnIndex) {
        if(columnIndex == 0){
            return MessageBundle.getString("shp_name");
        }else{
            return MessageBundle.getString("shp_type");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        if(columnIndex == 0){
            return String.class;
        }else{
            return FieldType.class;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return true;
    }

    Field getDataAt(final int rowIndex){
        return datas.get(rowIndex);
    }
    
    Field[] getDatas(){
        return datas.toArray(new Field[datas.size()]);
    }
    
    int indexOf(final Field data){
        return datas.indexOf(data);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if(columnIndex == 0){
            return datas.get(rowIndex).getName();
        }else{
            return datas.get(rowIndex).getType();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        if(columnIndex == 0){
            datas.get(rowIndex).setName((String) aValue);
        }else{
            datas.get(rowIndex).setType((FieldType) aValue);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
    
    void addAttribut(){
        final Field newData = new Field();
        datas.add(newData);
        fireTableRowsInserted(datas.indexOf(newData), datas.indexOf(newData));
    }
    
    void deleteAttribut(final Field data){
        final int index = datas.indexOf(data);
        datas.remove(data);
        fireTableRowsDeleted(index, index);
    }

    void moveUp(final Field data){
        final int index = datas.indexOf(data);
        if(index > 0){
            datas.remove(index);
            fireTableRowsDeleted(index, index);
            datas.add(index-1, data);
            fireTableRowsInserted(index-1, index-1);
        }
        
    }
    
    void moveDown(final Field data){
        final int index = datas.indexOf(data);
        if(index >= 0 && index < datas.size()-1 ){
            datas.remove(index);
            fireTableRowsDeleted(index, index);
            datas.add(index+1, data);
            fireTableRowsInserted(index+1, index+1);
        }
    }
    
    
}


