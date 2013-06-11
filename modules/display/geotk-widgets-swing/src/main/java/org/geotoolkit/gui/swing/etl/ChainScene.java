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
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.chain.model.Constant;
import org.geotoolkit.process.chain.model.DataLink;
import org.geotoolkit.process.chain.model.ElementCondition;
import org.geotoolkit.process.chain.model.ElementManual;
import org.geotoolkit.process.chain.model.ElementProcess;
import org.geotoolkit.process.chain.model.FlowLink;
import org.geotoolkit.process.chain.model.Parameter;
import org.geotoolkit.process.chain.model.event.ChainListener;
import org.geotoolkit.process.chain.model.event.EventChain;
import org.geotoolkit.util.logging.Logging;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public abstract class ChainScene extends GraphScene<ProcessDescriptor,String> implements ChainListener {

    protected static final Logger LOGGER = Logging.getLogger(ChainScene.class);
    protected static final Color IN_OUT_COLOR = new Color(120, 210, 150);

    protected static final Object IN = "IN";
    protected static final Object OUT = "OUT";

    protected final LayerWidget mainLayer = new LayerWidget(this);
    protected final LayerWidget actionLayer = new LayerWidget(this);
    protected final LayerWidget connectionLayer = new LayerWidget(this);
    
    protected static final WidgetAction HOVER_ACTION = ActionFactory.createHoverAction (new ProviderWidgetHover());
    protected final Map<Object,Widget> mapping = new IdentityHashMap<Object, Widget>();

    
    public abstract EventChain getChain();
    
    public LayerWidget getMainLayer() {
        return mainLayer;
    }

    public LayerWidget getActionLayer() {
        return actionLayer;
    }

    public LayerWidget getConnectionLayer() {
        return connectionLayer;
    }

    public Widget getWidgetFromElement(Object element) {
        return mapping.get(element);
    }

    public Map<Object, Widget> getMapping() {
        return mapping;
    }
    
    public final void addWigetToMainLayer(Widget widget) {
        widget.getActions().addAction(HOVER_ACTION);
        this.getMainLayer().addChild(widget);
    }
    /**
     * Clean scene and remove listener from chain.
     */
    public abstract void dispose();

    /**
     * Find a Widget using his linked object like Parameter, Constant, Element, ...
     *
     * @param obj object like Parameter, Constant, Element, ...
     * @return Widget
     */
    public abstract Widget getWidget(final Object obj);

    /**
     * Method called to build widgets of the scene using the given Chain.
     * @param chain Chain
     */
    protected abstract void setChain(final EventChain chain);

    /**
     * Create an chain input parameter offering widget.
     * @param param Parameter
     */
    protected abstract void createInParameter(final Parameter param);

    /**
     * Remove an chain input parameter offering widget.
     * @param param Parameter
     */
    protected abstract void removeInParameter(final Parameter param);


    /**
     * Create an chain ouptut parameter receiving widget.
     * @param param Parameter
     */
    protected abstract void createOutParameter(final Parameter param);

    /**
     * Remove an chain ouptut parameter receiving widget.
     * @param param Parameter
     */
    protected abstract void removeOutParameter(final Parameter param);


    /**
     * Create a new constant widget in scene.
     * @param cst Constant
     */
    protected abstract void createConstant(final Constant cst);

    /**
     * Remove a constant widget from scene.
     * @param cst Constant
     */
    protected abstract void removeConstant(final Constant cst);

    /**
     * Create a data link widget in scene.
     * @param link DataLink
     */
    protected abstract void createDataLink(final DataLink link);

    /**
     * Remove a data link widget from scene.
     * @param link DataLink
     */
    protected abstract void removeDataLink(final DataLink link);

    /**
     * Create an flow link between to WChainElement in scene.
     * @param link FlowLink
     */
    protected abstract void createFlowLink(final FlowLink link);

    /**
     * Remove an flow link between to WChainElement from scene.
     * @param link FlowLink
     */
    protected abstract void removeFlowLink(final FlowLink link);

    /**
     * Create a WChainElement widget in scene.
     * @param desc ElementProcess
     */
    protected abstract void createElementProcess(final ElementProcess desc);

    /**
     * Remove a WChainElement widget from scene.
     * @param desc ElementProcess
     */
    protected abstract void removeElementProcess(final ElementProcess desc);
    
    /**
     * Create an manual intervention element in scene.
     * @param mi ElementManual
     */
    protected abstract void createElementManual(final ElementManual mi);

    /**
     * Remove an manual intervention element in scene.
     * @param mi ElementManual
     */
    protected abstract void removeElementManual(final ElementManual mi);
    
    
    /**
     * Create an conditional element in scene.
     * @param ce ElementCondition
     */
    protected abstract void createElementCondition(final ElementCondition ce);

    /**
     * Remove an conditional element in scene.
     * @param ce ElementCondition
     */
    protected abstract void removeElementCondition(final ElementCondition ce);
}
