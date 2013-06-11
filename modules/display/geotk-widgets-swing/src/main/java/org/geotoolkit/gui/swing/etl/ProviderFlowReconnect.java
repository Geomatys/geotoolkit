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
import org.geotoolkit.process.chain.model.FlowLink;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ProviderFlowReconnect implements ReconnectProvider{

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
            Widget replacementWidget, final boolean reconnectingSource) {

        final WFlowLink cnx = (WFlowLink) cw;
        final ChainScene scene = (ChainScene)cw.getScene();
        final FlowLink link = cnx.getLink();

        if(replacementWidget instanceof WName){
            if (!((WName)replacementWidget).isTarget()) {
                return ConnectorState.REJECT;
            }
            replacementWidget = ((WName)replacementWidget).getOriginWidget();
        }

        int newTargetId = -1;
        if (replacementWidget instanceof WChainParametersFull) {
            final WChainParametersFull receiver = (WChainParametersFull) replacementWidget;
            newTargetId = receiver.getId();
            
        } else if (replacementWidget instanceof WElementProcess) {
            final WElementProcess receiver = (WElementProcess) replacementWidget;
            newTargetId = receiver.getId();

        } else if (replacementWidget instanceof WElementManuel) {
            final WElementManuel receiver = (WElementManuel) replacementWidget;
            newTargetId = receiver.getId();
            
        } else if (replacementWidget instanceof WElementCondition) {
            final WElementCondition receiver = (WElementCondition) replacementWidget;
            newTargetId = receiver.getId();
        } 
        
        if (newTargetId != -1) {
            //check the link is valid
            final FlowLink lk = new FlowLink(link);
            lk.setTargetId(newTargetId);

            if (scene.getChain().isValidFlowLink(lk)) {
                return ConnectorState.ACCEPT;
            } else {
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

        final WFlowLink cnx = (WFlowLink) cw;
        final ChainScene scene = (ChainScene)cw.getScene();
        final FlowLink link = cnx.getLink();

        if (widget instanceof WName) {
            if (!((WName)widget).isTarget()) {
                return;
            }
            widget = ((WName) widget).getOriginWidget();
        }

        int newTargetId = -1;
        if (widget instanceof WChainParametersFull) {
            final WChainParametersFull receiver = (WChainParametersFull) widget;
            newTargetId = receiver.getId();
            
        } else if (widget instanceof WElementProcess) {
            final WElementProcess receiver = (WElementProcess) widget;
            newTargetId = receiver.getId();

        } else if (widget instanceof WElementManuel) {
            final WElementManuel receiver = (WElementManuel) widget;
            newTargetId = receiver.getId();
            
        } else if (widget instanceof WElementCondition) {
            final WElementCondition receiver = (WElementCondition) widget;
            newTargetId = receiver.getId();
        } else if(widget == null){
            //disconect widget == remove link
            //leave the events do the job
            scene.getChain().getFlowLinks().remove(link);
        }
        
        if (newTargetId != -1) {
            //check the link is valid
            final FlowLink lk = new FlowLink(link);
            lk.setTargetId(newTargetId);

            if(scene.getChain().isValidFlowLink(lk)){
                //remove old link, add new one
                //leave the events do the job
                scene.getChain().getFlowLinks().remove(link);
                scene.getChain().getFlowLinks().add(lk);
            }
        }
    }

}
