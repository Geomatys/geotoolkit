package org.geotoolkit.pending.demo.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.*;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.gui.swing.parameters.editor.JParameterValuesEditor;
import org.geotoolkit.gui.swing.propertyedit.JAttributeEditor;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class ParameterEditionDemo {

    public static void main(String[] args) {

        final ParameterDescriptorGroup subGroupDesc = new ParameterBuilder().addName("subgroup").createGroup(2, 5,
                new ParameterBuilder().addName("dateParam").setRequired(true).create(Date.class, null));

        final GeneralParameterDescriptor[] params = new GeneralParameterDescriptor[5];
        params[0] = new ParameterBuilder().addName("strParam").setRequired(true).create(String.class, "Test");
        params[1] = new ParameterBuilder().addName("boolParam").setRequired(true).create(Boolean.class, true);
        params[2] = new ParameterBuilder().addName("doubleParam").setRequired(false).create(Double.class, 10.5);
        params[3] = subGroupDesc;


        final ParameterDescriptorGroup descGroup = new ParameterBuilder().addName("group").createGroup(params);
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
