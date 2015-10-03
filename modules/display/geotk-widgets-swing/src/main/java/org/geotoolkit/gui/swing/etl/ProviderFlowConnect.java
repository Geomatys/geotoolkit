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
import org.geotoolkit.processing.chain.model.FlowLink;
import org.apache.sis.util.logging.Logging;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ProviderFlowConnect implements ConnectProvider{

    private final ChainScene scene;

    public ProviderFlowConnect(final ChainScene scene) {
        this.scene = scene;
    }

    @Override
    public boolean isSourceWidget(final Widget widget) {
        return widget instanceof WName && ((WName)widget).isSource();
    }

    @Override
    public ConnectorState isTargetWidget(final Widget source, final Widget target) {

        if (source == target) {
            return ConnectorState.REJECT;
        }
        if (target instanceof WName) {
            if (!((WName)target).isTarget()) {
                return ConnectorState.REJECT;
            }

            final FlowLink link = toLink(source, target);

            if (scene.getChain().isValidFlowLink(link)) {
                return ConnectorState.ACCEPT;
            } else {
                return ConnectorState.REJECT;
            }
        }
        return ConnectorState.REJECT;
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
        final FlowLink link = toLink(source, target);
        scene.getChain().getFlowLinks().add(link);
    }

    private FlowLink toLink(final Widget source, final Widget target){
        final FlowLink link = new FlowLink(-1, -1);

        final WName sourceN = (WName) source;
        final Widget parentSource = sourceN.getOriginWidget();

        if (parentSource instanceof WChainParametersBasic){
            link.setSourceId( ((WChainParametersBasic)parentSource).getId());
        } else if (parentSource instanceof WElementProcess) {
            link.setSourceId( ((WElementProcess)parentSource).getId());
        } else if (parentSource instanceof WElementManuel) {
            link.setSourceId( ((WElementManuel)parentSource).getId());
        } else if (parentSource instanceof WElementCondition) {
            final WElementCondition condition = (WElementCondition) parentSource;
            if (sourceN.equals(condition.getWExecutionTitle())) {
                link.setSourceId(-1);
            } else {
                link.setSourceId(condition.getId());
            }

        } else {
            Logging.getLogger("org.geotoolkit.gui.swing.etl").warning("unexpected source type:" + parentSource.getClass());
        }

        final WName targetN = (WName) target;
        final Widget parentTarget = targetN.getOriginWidget();

        if(parentTarget instanceof WChainParametersBasic){
            link.setTargetId( ((WChainParametersBasic)parentTarget).getId() );
        } else if (parentTarget instanceof WElementProcess) {
            link.setTargetId( ((WElementProcess)parentTarget).getId());
        } else if (parentTarget instanceof WElementManuel) {
            link.setTargetId( ((WElementManuel)parentTarget).getId());
        } else if (parentTarget instanceof WElementCondition) {
            final WElementCondition condition = (WElementCondition) parentTarget;
            if (sourceN.equals(condition.getWExecutionFailed()) || sourceN.equals(condition.getWExecutionSuccess())) {
                link.setTargetId(-1);
            } else {
                link.setTargetId(condition.getId());
            }
        } else {
            Logging.getLogger("org.geotoolkit.gui.swing.etl").warning("unexpected target type:" + parentTarget.getClass());
        }

        return link;
    }

}
