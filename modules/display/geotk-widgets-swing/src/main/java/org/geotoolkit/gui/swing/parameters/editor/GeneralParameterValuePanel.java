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
package  org.geotoolkit.gui.swing.parameters.editor;

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
public abstract class GeneralParameterValuePanel extends JPanel implements PropertyChangeListener, MouseListener, Comparable<GeneralParameterValuePanel> {

    /*
     * Common parameters properties.
     */
    protected final String code;

    protected boolean selected = false;
    protected GeneralParameterDescriptor paramDesc;
    protected String validationError = null;
    /*
     * Parent parameter group.
     */
    private JParameterValueGroupPanel parent;

    protected GeneralParameterValuePanel(final GeneralParameterDescriptor paramDesc, final JParameterValueGroupPanel parent) {
        this.parent = parent;
        this.paramDesc = paramDesc;
        this.code = this.paramDesc.getName().getCode();
    }

    /**
     * Get parameter <code>code</code>
     * @return
     */
    public String getCode() {
        return code;
    }

    public String getValidationError() {
        return validationError;
    }

    /**
     * Set if a GeneralParameterValuePanel is selected.
     * This method also firePropertyChange event {@link JParameterValuesEditor#PARAMETER_SELECTED_EVENT}
     * and change panel background color.
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            firePropertyChange(JParameterValuesEditor.PARAMETER_SELECTED_EVENT, null, this);
            this.setBackgroundColor(UIManager.getColor("List.selectionBackground"));
        } else {
            this.setBackgroundColor(UIManager.getColor("Label.background"));
        }
    }

    /**
     * Return parent JParameterValueGroupPanel of current GeneralParameterValuePanel.
     * Can return null in case of current parameter is the root JParameterValueGroupPanel.
     *
     * @return JParameterValueGroupPanel parent or null if root.
     */
    public JParameterValueGroupPanel getParentPanel(){
        return parent;
    }

    /**
     * Compute and return current panel ParameterDescriptor.
     *
     * @return GeneralParameterDescriptor
     */
    public GeneralParameterDescriptor getDescriptor() {
        return paramDesc;
    }

    /**
     * Method called to update parameter component panel.
     */
    public abstract void updateContent();

    /**
     * Set GeneralParameterValuePanel background color.
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
     * Use to sort Parameter in a JParameterValueGroupPanel.
     * This sort parameters in alphabetical order using there code attribute.
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(final GeneralParameterValuePanel o) {
        return code.compareTo(o.code);
    }
}
