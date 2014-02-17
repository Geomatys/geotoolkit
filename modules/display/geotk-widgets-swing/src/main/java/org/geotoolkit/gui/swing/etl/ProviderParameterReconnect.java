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
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ProviderParameterReconnect implements ReconnectProvider{

    @Override
    public boolean isSourceReconnectable(ConnectionWidget cw) {
        return false;
    }

    @Override
    public boolean isTargetReconnectable(ConnectionWidget cw) {
        return true;
    }

    @Override
    public void reconnectingStarted(ConnectionWidget cw, boolean bln) {
    }

    @Override
    public void reconnectingFinished(ConnectionWidget cw, boolean bln) {
    }

    @Override
    public ConnectorState isReplacementWidget(final ConnectionWidget cw,
            final Widget replacementWidget, final boolean reconnectingSource) {

        final WDataLink cnx = (WDataLink) cw;
        final ChainScene scene = (ChainScene)cw.getScene();
        final DataLink link = cnx.getLink();

        if(replacementWidget instanceof WReceivingParameter){
            final WReceivingParameter receiver = (WReceivingParameter)replacementWidget;

            //check the link is valid
            final DataLink lk = new DataLink(link);
            lk.setTargetCode(receiver.getCode());
            lk.setTargetId(receiver.getId());

            if(scene.getChain().isValidLink(lk)){
                return ConnectorState.ACCEPT;
            }else{
                return ConnectorState.REJECT;
            }
        }

        return ConnectorState.REJECT_AND_STOP;
    }

    @Override
    public boolean hasCustomReplacementWidgetResolver(Scene scene) {
        return false;
    }

    @Override
    public Widget resolveReplacementWidget(Scene scene, Point point) {
        return null;
    }

    @Override
    public void reconnect(ConnectionWidget cw, Widget widget, boolean bln) {

        final WDataLink cnx = (WDataLink) cw;
        final ChainScene scene = (ChainScene)cw.getScene();
        final DataLink link = cnx.getLink();

        if(widget instanceof WReceivingParameter){
            final WReceivingParameter receiver = (WReceivingParameter) widget;

            //check the link is valid
            final DataLink lk = new DataLink(link);
            lk.setTargetCode(receiver.getCode());
            lk.setTargetId(receiver.getId());

            if(scene.getChain().isValidLink(lk)){
                //remove old link, add new one
                //leave the events do the job
                scene.getChain().getDataLinks().remove(link);
                scene.getChain().getDataLinks().add(lk);
            }

        }else if(widget == null){
            //disconect widget == remove link
            //leave the events do the job
            scene.getChain().getDataLinks().remove(link);
        }
    }


}
