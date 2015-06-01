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

import org.geotoolkit.processing.chain.model.FlowLink;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;

/**
 * Execution connection widget. (links between start > process > ... > end )
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class WFlowLink extends ConnectionWidget {

    private final FlowLink link;

    public WFlowLink(final Scene scene, final FlowLink link) {
        super(scene);
        getScene().validate();
        this.link = link;
        getActions().addAction(ActionFactory.createReconnectAction(new ProviderFlowReconnect()));
        setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        setEndPointShape(PointShape.SQUARE_FILLED_BIG);
    }

    public FlowLink getLink() {
        return link;
    }

}
