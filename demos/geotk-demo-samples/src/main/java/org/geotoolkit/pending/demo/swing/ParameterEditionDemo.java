package org.geotoolkit.pending.demo.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import org.geotoolkit.gui.swing.parameters.editor.JParameterValuesEditor;
import org.geotoolkit.gui.swing.propertyedit.JAttributeEditor;
import org.geotoolkit.parameter.ParametersExt;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class ParameterEditionDemo {
    
    public static void main(String[] args) {
        
        List<GeneralParameterDescriptor> params = new ArrayList<>();
        params.add(ParametersExt.createParameterDescriptor("strParam", null, String.class, null, "Test", null, null, null, true));
        params.add(ParametersExt.createParameterDescriptor("boolParam", null, Boolean.class, null, true, null, null, null, true));
        params.add(ParametersExt.createParameterDescriptor("doubleParam", null, Double.class, null, 10.5, null, null, null, false));
        
        List<GeneralParameterDescriptor> subParams = new ArrayList<>();
        subParams.add(ParametersExt.createParameterDescriptor("dateParam", null, Date.class, null, null, null, null, null, true));
        params.add(ParametersExt.createParameterDescriptorGroup("subgroup", null, 2, 5, subParams));
        
        final ParameterDescriptorGroup descGroup = ParametersExt.createParameterDescriptorGroup("group", null, 1, 1, params);
        final JAttributeEditor editors = new JAttributeEditor();


        System.out.println("########################## Input ParameterDescriptorGroup");
        System.out.println(descGroup);

        final JParameterValuesEditor editor = new JParameterValuesEditor(descGroup, editors.getEditors(), null);

        final JDialog optionPaneDialog = new JDialog();
        final JOptionPane optPane = new JOptionPane(editor, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        optPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("value")) {
                    switch ((Integer) e.getNewValue()) {
                        case JOptionPane.OK_OPTION:
                            System.out.println("########################## New ParameterValueGroup");
                            System.out.println(editor.getParameterValue());
                            optionPaneDialog.dispose();
                            break;
                        case JOptionPane.CANCEL_OPTION:
                            optionPaneDialog.dispose();
                            break;
                    }
                }
            }
        });

        optionPaneDialog.setTitle("");
        optionPaneDialog.setContentPane(optPane);
        optionPaneDialog.pack();
        optionPaneDialog.setResizable(true);
        optionPaneDialog.setLocationRelativeTo(null);
        optionPaneDialog.setModal(true);
        optionPaneDialog.setVisible(true);
    }
}
