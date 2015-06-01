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
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.processing.chain.model.Constant;
import org.geotoolkit.processing.chain.model.DataLink;
import org.geotoolkit.processing.chain.model.Element;
import org.geotoolkit.processing.chain.model.ElementCondition;
import org.geotoolkit.processing.chain.model.ElementManual;
import org.geotoolkit.processing.chain.model.ElementProcess;
import org.geotoolkit.processing.chain.model.FlowLink;
import org.geotoolkit.processing.chain.model.Parameter;
import org.geotoolkit.processing.chain.model.Positionable;
import org.geotoolkit.processing.chain.model.event.EventChain;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Exceptions;

/**
 * Advanced scene for a chain.
 * Display all. (ChainElements, parameters, constants, links)
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 */
public class ChainSceneAdvanced extends ChainSceneIntermediate {

    public ChainSceneAdvanced(final EventChain chain, final boolean editable) {
        super(chain, editable);
        if (editable) {
            getActions().addAction(ActionFactory.createPopupMenuAction(new ProcessPopup()));
        }
    }

    @Override
    protected void setChain(final EventChain chain) {
        getMapping().clear();
        if(this.chain != null){
            this.chain.removeListener(this);
        }

        this.chain = chain;
        if(this.chain != null){
            this.chain.addListener(this);
        }

        final WChainParametersFull inParameters = new WChainParametersFull(this, MessageBundle.getString("inputs"),Integer.MIN_VALUE, editable);
        final WChainParametersFull outParameters = new WChainParametersFull(this, MessageBundle.getString("outputs"), Integer.MAX_VALUE, editable);
        getMapping().put(IN, inParameters);
        getMapping().put(OUT, outParameters);
        getMapping().put(ElementProcess.BEGIN, inParameters.getExecutionWidget());
        getMapping().put(ElementProcess.END, outParameters.getExecutionWidget());
        addWigetToMainLayer(inParameters);
        addWigetToMainLayer(outParameters);


        for(final Parameter param : chain.getInputs()){
            createInParameter(param);
        }

        for(final Parameter param : chain.getOutputs()){
            createOutParameter(param);
        }

        for(final Element ele : chain.getElements()){
            if(ele instanceof ElementProcess){
                createElementProcess((ElementProcess)ele);
            }else if(ele instanceof ElementManual){
                createElementManual((ElementManual)ele);
            }else if(ele instanceof ElementCondition){
                createElementCondition((ElementCondition)ele);
            }
        }
        
        for(final Constant cst : chain.getConstants()){
            createConstant(cst);
        }

        for(final DataLink link : chain.getDataLinks()){
            createDataLink(link);
        }

        for(final FlowLink execlink : chain.getFlowLinks()){
            createFlowLink(execlink);
        }


        if(SwingUtilities.isEventDispatchThread()){
            replaceAll();
        }else{
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            replaceAll();
                        }
                    });
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        getScene().validate();
        repaint();
    }

    private void replaceAll(){

        int minX=0;
        int maxX=0;

        for(Widget widget : getMapping().values()){

            if(widget instanceof WPositionable){
                final WPositionable element = (WPositionable) widget;
                final Positionable positionable = element.getObject();
                widget.setPreferredLocation(new Point(positionable.getX(), positionable.getY()));

                if(positionable.getX()< minX) {minX = positionable.getX();}
                if(positionable.getX()> maxX) {maxX = positionable.getX();}
            }
        }

        final Widget win = getMapping().get(IN);
        final Widget wout = getMapping().get(OUT);
        win.setPreferredLocation(new Point(minX-150, 0));
        wout.setPreferredLocation(new Point(maxX+150, 0));

        //refresh
        validate();
    }

    @Override
    public void dispose() {
        this.chain.removeListener(this);
    }

    @Override
    protected void createConstant(final Constant cst){
        final WConstant widget = new WConstant(this, cst, editable);

        widget.setPreferredLocation(new Point(cst.getX(), cst.getY()));

        getMapping().put(cst, widget);
        addWigetToMainLayer(widget);
        validate();
    }

    @Override
    protected void removeConstant(final Constant cst){
        final Widget widget = getMapping().remove(cst);
        ArgumentChecks.ensureNonNull("constant widget", widget);
        getMainLayer().removeChild(widget);
        getScene().validate();
    }

    @Override
    protected void createDataLink(final DataLink link) {

        final EventChain sequence = getChain();
        final Object source = link.getSource(sequence);
        final Object target = link.getTarget(sequence);
        Widget w_source = getMapping().get(source);
        Widget w_target = getMapping().get(target);

        if (w_source instanceof WElementProcess) {
            final WElementProcess pw = (WElementProcess) w_source;
            w_source = pw.getOutputWidget(link.getSourceCode());
        } else if (w_source instanceof WConstant) {
            final WConstant pw = (WConstant) w_source;
            w_source = pw.getOffering();
        }

        if (w_target instanceof WElementProcess) {
            final WElementProcess pw = (WElementProcess) w_target;
            w_target = pw.getInputWidget(link.getTargetCode());
        } else if (w_target instanceof WConstant) {
            final WConstant pw = (WConstant) w_target;
            w_target = pw.getOffering();
        } else if (w_target instanceof WElementCondition) {
            final WElementCondition pw = (WElementCondition) w_target;
             w_target = pw.getInputWidget(link.getTargetCode());
        }

        ArgumentChecks.ensureNonNull("link source", w_source);
        ArgumentChecks.ensureNonNull("link target", w_target);

        final ConnectionWidget connection = new WDataLink(this, link);
        connection.setSourceAnchor(new SideAnchor(w_source, false));
        connection.setTargetAnchor(new SideAnchor(w_target, true));
        getMapping().put(link, connection);
        getConnectionLayer().addChild(connection);
        getScene().validate();
    }

    @Override
    protected void removeDataLink(final DataLink link) {

        final Widget widget = getMapping().remove(link);
        ArgumentChecks.ensureNonNull("link widget", widget);
        getConnectionLayer().removeChild(widget);
        getScene().validate();
    }

    ////////////////////////////////////////////////////////////////////////////
    // chain events /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void constantChange(CollectionChangeEvent event) {
        if(event.getType() == CollectionChangeEvent.ITEM_ADDED){
            for(Object obj : event.getItems()){
                createConstant((Constant)obj);
            }
        }else if(event.getType() == CollectionChangeEvent.ITEM_REMOVED){
            for(Object obj : event.getItems()){
                removeConstant((Constant)obj);
            }
        }
    }

    private class ProcessPopup implements PopupMenuProvider{

        @Override
        public JPopupMenu getPopupMenu(final Widget widget, final Point point) {

            final JPopupMenu menu = new JPopupMenu();
            menu.add(new JMenuItem(
                 new AbstractAction("New Constant") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final Constant cst = new Constant(getChain().getNextId(),  Integer.class, "",
                            point.x,
                            point.y);
                            getChain().getConstants().add(cst);
                        }
                    }
                ));
            return menu;
        }
    }
}
