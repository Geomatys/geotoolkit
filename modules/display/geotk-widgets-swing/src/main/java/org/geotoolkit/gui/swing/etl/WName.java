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

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WName extends LabelWidget {

    private final boolean isSource;
    private final boolean isTarget;
    private final Widget originWidget;
    
    public WName(final ChainScene scene, final String name, final boolean editable, 
            final boolean isSource, final boolean isTarget, final Widget originWidget) {
        super(scene, name);
        this.isSource = isSource;
        this.isTarget = isTarget;
        this.originWidget = originWidget;
        
        if (editable) {
            getActions().addAction(ActionFactory.createConnectAction(scene.getActionLayer(), new ProviderFlowConnect(scene)));
        }
    }

    public boolean isSource() {
        return isSource;
    }
    
    public boolean isTarget() {
        return isTarget;
    }
    
    public Widget getOriginWidget() {
        return originWidget;
    }
}
