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

import java.awt.Point;
import org.geotoolkit.process.chain.model.DataLink;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ProviderParameterConnect implements ConnectProvider{

    private final ChainScene scene;

    public ProviderParameterConnect(final ChainScene scene) {
        this.scene = scene;
    }

    @Override
    public boolean isSourceWidget(final Widget widget) {
        return widget instanceof WOfferingParameter;
    }

    @Override
    public ConnectorState isTargetWidget(final Widget source, final Widget target) {

        final DataLink link = toLink(source, target);

        if(scene.getChain().isValidLink(link)){
            return ConnectorState.ACCEPT;
        }else{
            return ConnectorState.REJECT;
        }
    }

    @Override
    public boolean hasCustomTargetWidgetResolver(final Scene scene) {
        return false;
    }

    @Override
    public Widget resolveTargetWidget(final Scene scene, final Point point) {
        throw new UnsupportedOperationException("Not supported ");
    }

    @Override
    public void createConnection(final Widget source, final Widget target) {
        final DataLink link = toLink(source, target);
        scene.getChain().getDataLinks().add(link);
    }

    private DataLink toLink(Widget source, final Widget target){
        final DataLink link = new DataLink(-1, (String)null, -1, (String)null);

        if(source instanceof WOfferingParameter){
            link.setSourceId( ((WOfferingParameter)source).getId() );
            link.setSourceCode( ((WOfferingParameter)source).getCode() );
        }

        if(target instanceof WReceivingParameter){
            link.setTargetId( ((WReceivingParameter)target).getId() );
            link.setTargetCode( ((WReceivingParameter)target).getCode() );
        }

        return link;
    }

}
