/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.gui.swing.etl;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.Widget;
import static org.geotoolkit.gui.swing.etl.ChainEditorConstants.*;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.processing.chain.model.ElementManual;
import org.netbeans.api.visual.action.EditProvider;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WElementManuel extends Widget implements WPositionable {

    private final WName execWidget;

    private final ElementManual manualIntervention;

    public WElementManuel(final ChainScene scene, final ElementManual mi, final boolean editable) {
        super(scene);
        this.manualIntervention = mi;

        setOpaque(true);
        setBackground(MANUAL_INTERVENTION_COLOR);

        getActions().addAction(ActionFactory.createMoveAction());
        if (editable) {
            getActions().addAction(ActionFactory.createPopupMenuAction(new ProcessPopup()));
            getActions().addAction(ActionFactory.createEditAction(new ManualInterventionEditor()));
        }

        setBorder(BorderFactory.createRoundedBorder(20, 20, 8, 8, Color.WHITE, Color.DARK_GRAY));

        final String name = MessageBundle.format("manualInt");

        execWidget = new WName(scene, name, editable, true, true, this);
        execWidget.setFont(CHAIN_ELEMENT_EXECUTION_TITLE_FONT);

        //underline ChainElement title only if we display parameters.
        //execWidget.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, MANUAL_INTERVENTION_BORDER_COLOR));
        setLayout(LayoutFactory.createVerticalFlowLayout());
        addChild(execWidget);

        getScene().validate();
    }

    @Override
    public ElementManual getObject() {
        return manualIntervention;
    }

    public WName getExecutionWidget(){
        return execWidget;
    }

    private class ProcessPopup implements PopupMenuProvider{

        @Override
        public JPopupMenu getPopupMenu(final Widget widget, final Point point) {
            final JPopupMenu menu = new JPopupMenu();
            menu.add(new JMenuItem(new AbstractAction("Configure") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final ChainScene scene = (ChainScene) getScene();
                            final WElementManuel elem = (WElementManuel) widget;
                            final ElementManual manual = elem.getObject();
                            final String msg = MessageBundle.format("manualDesc");
                            final String title = MessageBundle.format("manualInt");
                            final String desc = (String) JOptionPane.showInputDialog(null, msg, title, JOptionPane.PLAIN_MESSAGE, null, null, manual.getDescription());
                            if (desc != null) {
                                manual.setDescription(desc);
                            }
                        }
                    }));
            menu.add(new JMenuItem(new AbstractAction("Delete") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ((ChainScene) getScene()).getChain().getElements().remove(manualIntervention);
                        }
                    }));

            return menu;
        }

    }

    public Integer getId() {
        if (manualIntervention != null) {
            return manualIntervention.getId();
        }
        return -1;
    }

    @Override
    protected void paintWidget() {
        super.paintWidget();
        manualIntervention.setX(getLocation().x);
        manualIntervention.setY(getLocation().y);
    }

    private class ManualInterventionEditor implements EditProvider {

        @Override
        public void edit(Widget widget) {
           final WElementManuel elem = (WElementManuel) widget;
            final ElementManual manuel = elem.getObject();
            final String msg = MessageBundle.format("manualDesc");
            final String title = MessageBundle.format("manualInt");
            final String desc = (String) JOptionPane.showInputDialog(null, msg, title, JOptionPane.PLAIN_MESSAGE, null, null, manuel.getDescription());
            if (desc != null) {
                manuel.setDescription(desc);
            }
        }
    }
}
