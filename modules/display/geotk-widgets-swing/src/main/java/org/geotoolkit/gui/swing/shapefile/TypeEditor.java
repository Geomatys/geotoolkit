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

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;


/**
 * Cell editor for ShapeFile creation field table.
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
class TypeEditor implements TableCellEditor{

    private final EventListenerList listeners = new EventListenerList();
    private final JComboBox box = new JComboBox();
    
    TypeEditor(){        
        box.addItem(FieldType.INTEGER);
        box.addItem(FieldType.LONG);
        box.addItem(FieldType.DOUBLE);
        box.addItem(FieldType.STRING);
        box.addItem(FieldType.DATE);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {        
        box.setSelectedItem(value);
        return box;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getCellEditorValue() {
        return box.getSelectedItem();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isCellEditable(final EventObject anEvent) {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean shouldSelectCell(final EventObject anEvent) {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean stopCellEditing() {
        final CellEditorListener[] lst = listeners.getListeners(CellEditorListener.class);
        
        for(final CellEditorListener l : lst){
            l.editingStopped(new ChangeEvent(this));
        }
        
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void cancelCellEditing() {
        final CellEditorListener[] lst = listeners.getListeners(CellEditorListener.class);
        
        for(final CellEditorListener l : lst){
            l.editingCanceled(new ChangeEvent(this));
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addCellEditorListener(final CellEditorListener l) {
        listeners.add(CellEditorListener.class, l);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeCellEditorListener(final CellEditorListener l) {
        listeners.remove(CellEditorListener.class, l);
    }

}
