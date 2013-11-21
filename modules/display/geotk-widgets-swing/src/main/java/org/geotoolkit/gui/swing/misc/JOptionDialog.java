/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.gui.swing.misc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.util.SwingUtilities;

/**
 * Component similar to JOptionPane but with a resizeable dialog.
 *
 * @author Fabien Retif (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JOptionDialog extends JDialog implements ActionListener {

    public static final Object DIALOG_CLOSE = "dialogClose";

    private static final String PROPERTY_CLOSE_ACTION = "closeAction";
    private final JPanel guiActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    private Object closeAction = DIALOG_CLOSE;

    /**
     * Creates new form JOptionDialog
     */
    private JOptionDialog(Window parent, boolean modal, Action[] actions) {
        super(parent);
        setModal(modal);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        add(BorderLayout.SOUTH,guiActions);

        for(Action act : actions){
            final JButton actButton = new JButton(act);
            actButton.addActionListener(this);
            guiActions.add(actButton);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JButton button = ((JButton)e.getSource());
        closeAction = button.getAction().getValue(PROPERTY_CLOSE_ACTION);
        dispose();
    }

    /**
     *
     * @param comp : component to display
     * @param modal : set the dialog modal or not.
     * @param optionType : JOptionpane.YES_NO_CANCEL_OPTION / OK_CANCEL_OPTION / YES_NO_OPTION / OK_OPTION
     * @return
     */
    public static int show(Component parent, Component comp, int optionType) {
        final Action[] actions;
        if(JOptionPane.OK_OPTION == optionType){
            actions = new Action[]{
                new NoEventAction(MessageBundle.getString("ok"), JOptionPane.OK_OPTION)
            };
        }else if(JOptionPane.OK_CANCEL_OPTION == optionType){
            actions = new Action[]{
                new NoEventAction(MessageBundle.getString("ok"), JOptionPane.OK_OPTION),
                new NoEventAction(MessageBundle.getString("cancel"), JOptionPane.CANCEL_OPTION)
            };
        }else if(JOptionPane.YES_NO_OPTION == optionType){
            actions = new Action[]{
                new NoEventAction(MessageBundle.getString("yes"), JOptionPane.YES_OPTION),
                new NoEventAction(MessageBundle.getString("no"), JOptionPane.NO_OPTION)
            };
        }else if(JOptionPane.YES_NO_CANCEL_OPTION == optionType){
            actions = new Action[]{
                new NoEventAction(MessageBundle.getString("yes"), JOptionPane.YES_OPTION),
                new NoEventAction(MessageBundle.getString("no"), JOptionPane.NO_OPTION),
                new NoEventAction(MessageBundle.getString("cancel"), JOptionPane.CANCEL_OPTION)
            };
        }else{
            throw new RuntimeException("Unexpected option type : " + optionType);
        }

        final Object res = show(parent,comp,actions);
        if(res == DIALOG_CLOSE){
            return JOptionPane.CANCEL_OPTION;
        }else{
            return (Integer)res;
        }
    }

    /**
     *
     * @param comp : component to display
     * @param modal : set the dialog modal or not.
     * @param actionCommands : ending actions to display
     * @return the selection end action or DIALOG_CLOSE
     */
    public static Object show(Component parent, Component comp, String[] actionCommands) {
        return show(parent, comp, null, actionCommands);
    }

    /**
     *
     * @param comp : component to display
     * @param modal : set the dialog modal or not.
     * @param title : A title to set the dialog. Can be null.
     * @param actionCommands : ending actions to display
     * @return the selection end action or DIALOG_CLOSE
     */
    public static Object show(Component parent, Component comp, String title, String[] actionCommands) {
        final Action[] actions = new Action[actionCommands.length];
        for(int i=0;i<actionCommands.length;i++){
            actions[i] = new NoEventAction(actionCommands[i], actionCommands[i]);
        }

        return show(parent, comp, title, actions);
    }

    private static Object show(Component parent, Component comp, Action[] actions) {
        return show(parent, comp, null, actions);
    }

    private static Object show(Component parent, Component comp, String title, Action[] actions) {
        final Window window = SwingUtilities.windowForComponent(parent);
        final JOptionDialog dialog = new JOptionDialog(window, true, actions);
        if (title != null) {
            dialog.setTitle(title);
        }
        dialog.add(BorderLayout.CENTER,comp);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        return dialog.closeAction;
    }

    private static final class NoEventAction extends AbstractAction{

        public NoEventAction(String name, Object closeAction) {
            this(name,closeAction,null);
        }

        public NoEventAction(String name, Object closeAction, Icon icon) {
            super(name,icon);
            putValue(PROPERTY_CLOSE_ACTION, closeAction);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }

}
