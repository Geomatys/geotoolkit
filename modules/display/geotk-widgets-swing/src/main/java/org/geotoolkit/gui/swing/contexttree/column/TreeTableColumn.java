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
package org.geotoolkit.gui.swing.contexttree.column;

import org.jdesktop.swingx.table.TableColumnExt;

/**
 * Abstract class for columnmodel
 * 
 * @author Johann Sorel
 */
public abstract class TreeTableColumn extends TableColumnExt{

    private String title = "";
    private Object headervalue = null;
    protected boolean editOnMouseOver = false;
    
    @Override
    public void setTitle(String text) {
        title = text;
    }

    @Override
    public void setHeaderValue(Object value) {
        headervalue = value;
    }

    @Override
    public Object getHeaderValue() {
        return headervalue;
    }
    
    @Override
    public String getTitle(){
        return title;
    }
        
    
    /**
     * affect value object to target object, value come from editor
     * @param target
     * @param value
     */
    public abstract void setValue(Object target,Object value);
    
    /**
     * value of target object,will be used by renderer and editor
     * @param target
     * @return
     */
    public abstract Object getValue(Object target);
    
    /**
     * column class
     * @return
     */
    public abstract Class getColumnClass();
        
    /**
     * 
     * @param target
     * @return
     */
    public abstract boolean isCellEditable(Object target);
    
    /**
     * 
     * @return true if the cell must go in edit mode on mouseover,
     * more ressource consuming but make component highly interactive.
     */
    public boolean isEditableOnMouseOver(){
        return editOnMouseOver;
    }
    
    public void setEditableOnMouseOver(boolean edit){
        editOnMouseOver = edit;
    }
    
    
}
