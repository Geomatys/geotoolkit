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
import java.awt.Font;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WChainParametersBasic extends Widget{

    protected final WName execWidget;

    protected final int inChainId;
    protected final ChainScene scene;
    protected final String name;
    protected final boolean editable;

    public WChainParametersBasic(final ChainScene scene, final String name, final int id, final boolean editable) {
        super(scene);
        this.inChainId = id;
        this.scene = scene;
        this.name = name;
        this.editable = editable;
        setOpaque(false);

        getActions().addAction(ActionFactory.createMoveAction());
        getActions().addAction(ActionFactory.createEditAction(new ParametersEditorProvider()));

        setBorder(BorderFactory.createRoundedBorder(20, 20, 8, 8, Color.WHITE, Color.DARK_GRAY));


        execWidget = new WName(scene, name, (editable && id != Integer.MAX_VALUE), true, true, this);
        execWidget.setFont(new Font("monospaced", Font.BOLD, 12));
        execWidget.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        setLayout(LayoutFactory.createVerticalFlowLayout());
        addChild(execWidget);
        getScene().validate();
    }

    /**
     * In chain Id : Integer.MIN_VALUE or Integer.MAX_VALUE
     */
    public int getId(){
        return inChainId;
    }

    public WName getExecutionWidget(){
        return execWidget;
    }

    private class ParametersEditorProvider implements EditProvider {

        @Override
        public void edit(Widget widget) {
            final ChainScene scene = (ChainScene)getScene();
            final JChainParametersPanel pane = new JChainParametersPanel(scene.getChain(), editable, editable);
            pane.showDialog();
        }
    }

}
