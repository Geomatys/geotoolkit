package org.geotoolkit.pending.demo.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.*;
import org.geotoolkit.gui.swing.parameters.creator.JParameterDescriptorsEditor;
import org.geotoolkit.gui.swing.propertyedit.JAttributeEditor;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class ParameterCreationDemo {

    public static void main(String[] args) {

        final ParameterDescriptorGroup descGroup =
                ParametersExt.createParameterDescriptorGroup("group", null, 1, 1, new ArrayList<GeneralParameterDescriptor>());
        final JAttributeEditor editors = new JAttributeEditor();


        System.out.println("########################## Input ParameterDescriptorGroup");
        System.out.println(descGroup);

        final JParameterDescriptorsEditor editor = new JParameterDescriptorsEditor(descGroup, null, editors.getEditors());

        final JDialog optionPaneDialog = new JDialog();
        final JOptionPane optPane = new JOptionPane(editor, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        optPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("value")) {
                    switch ((Integer) e.getNewValue()) {
                        case JOptionPane.OK_OPTION:
                            System.out.println("########################## New ParameterDescriptorGroup");
                            System.out.println(editor.getDescriptor());
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
