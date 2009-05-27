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
package org.geotoolkit.gui.swing.contexttree.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.renderer.LabelProvider;
import org.jdesktop.swingx.renderer.TableCellContext;

/**
 * DefaultCellRenderer for JContextTree columns
 * 
 * @author Johann Sorel
 */
public class DefaultCellRenderer implements javax.swing.table.TableCellRenderer, java.io.Serializable {
    
    protected ComponentProvider componentController;
    private CellContext cellContext;
    private RenderAndEditComponent view;
    
        
    /**
     * Constructor
     * @param component
     */
    public DefaultCellRenderer(RenderAndEditComponent component){
        this(new Provider(component));
        this.view = component;        
    }
    
    
    private DefaultCellRenderer(ComponentProvider componentController) {
                
        if (componentController == null) {
            componentController = new LabelProvider();
        }
        this.componentController = componentController;
        this.cellContext = new TableCellContext();
    }
    
    
    /**
     *
     * Returns a configured component, appropriate to render the given
     * list cell.
     *
     * @param table the <code>JTable</code>
     * @param value the value to assign to the cell at <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
    public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) {
        // TODO cellContext.installContext(table, value, row, column, isSelected, hasFocus,true, true);
        return componentController.getRendererComponent(cellContext);
                
    }
    /**
     * @param background
     */
    public void setBackground(Color background) {
        componentController.getRendererComponent(cellContext).setBackground(background);        
    }
    /**
     * @param foreground
     */
    public void setForeground(Color foreground) {
        componentController.getRendererComponent(cellContext).setForeground(foreground);        
    }
    
    //----------------- RolloverRenderer
    
//    /**
//     * {@inheritDoc}
//     */
//    public void doClick() {
//        if (isEnabled()) {
//            ((RolloverRenderer) componentController).doClick();
//        }
//    }
//
//    /**
//     * {@inheritDoc}
//     * @return
//     */
//    public boolean isEnabled() {
//        return (componentController instanceof RolloverRenderer) && ((RolloverRenderer) componentController).isEnabled();
//
//    }
    
}


class Provider extends ComponentProvider<JComponent>{
    
    
    /** Creates a new instance of SymbolRendererProvider */
    Provider(RenderAndEditComponent view) {
        this.rendererComponent = view;        
    }

    @Override
    protected void format(CellContext cellContext) {   
        ((RenderAndEditComponent)rendererComponent).parse(cellContext.getValue());       
    }

    @Override
    protected void configureState(CellContext cellContext) {}

    
    @Override
    protected RenderAndEditComponent createRendererComponent() {
        return (RenderAndEditComponent) rendererComponent;
    }
    
    
    
    
}
