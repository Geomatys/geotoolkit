/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013 Geomatys
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
package  org.geotoolkit.gui.swing.parameters.creator;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.opengis.parameter.GeneralParameterDescriptor;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public abstract class GeneralParameterDescriptorPanel extends JPanel implements PropertyChangeListener, MouseListener, Comparable<GeneralParameterDescriptorPanel> {
    
    /*
     * Common parameters properties.
     */
    protected String code;
    protected String remarks;
    
    protected boolean selected = false;
    
    /*
     * Parent parameter group.
     */
    private JParameterDescriptorGroupPanel parent;
    
    protected GeneralParameterDescriptorPanel(final GeneralParameterDescriptor desc, final JParameterDescriptorGroupPanel parent) {
        this.code = desc.getName().getCode();
        this.remarks = (desc.getRemarks() != null) ? desc.getRemarks().toString() : null;
        this.parent = parent;
    }

    /**
     * Get parameter <code>code</code>
     * @return 
     */
    public String getCode() {
        return code;
    }

    /**
     * Set parameter <code>code</code>
     * @param code 
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Get parameter <code>remarks</code>
     * @return 
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Set parameter <code>remarks</code>
     * @param remarks 
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Set if a GeneralParameterDescriptorPanel is selected.
     * This method also firePropertyChange event {@link JParameterDescriptorsEditor.PARAMETER_SELECTED_EVENT} 
     * and change panel background color.
     * 
     * @param selected 
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            firePropertyChange(JParameterDescriptorsEditor.PARAMETER_SELECTED_EVENT, null, this);
            this.setBackgroundColor(UIManager.getColor("List.selectionBackground"));
        } else {
            this.setBackgroundColor(UIManager.getColor("Label.background"));
        }
    }
    
    /**
     * Return parent JParameterDescriptorGroupPanel of current GeneralParameterDescriptorPanel.
     * Can return null in case of current parameter is the root JParameterDescriptorGroupPanel.
     * 
     * @return JParameterDescriptorGroupPanel parent or null if root.
     */
    public JParameterDescriptorGroupPanel getParentPanel(){
        return parent;
    }
    
    /**
     * Compute and return current panel ParameterDescriptor.
     * 
     * @return GeneralParameterDescriptor
     */
    public abstract GeneralParameterDescriptor getDescriptor();
    
    /**
     * Return if a parameter is editable or not.
     * 
     * @return boolean
     */
    public abstract boolean isEditable();
    
    /**
     * Method called to update parameter component panel.
     */
    public abstract void updateContent();
    
    /**
     * Set GeneralParameterDescriptorPanel background color.
     * 
     * @param color 
     */
    public abstract void setBackgroundColor(final Color color);
    
    
    /**
     * Mouse events when a parameter is clicked.
     */
    
    @Override
    public void mouseClicked(MouseEvent e) {
        setSelected(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Use to sort Parameter in a JParameterDescriptorGroupPanel.
     * This sort parameters in alphabetical order using there code attribute.
     * 
     * @param o
     * @return 
     */
    @Override
    public int compareTo(final GeneralParameterDescriptorPanel o) {
        return code.compareTo(o.code);
    }
}
