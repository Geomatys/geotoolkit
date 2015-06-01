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
import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.SwingUtilities;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.process.ProcessDescriptor;
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
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Exceptions;

/**
 * Basic scene for a chain. Only ChainElement and chain input/output both without parameters will be display.
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 */
public class ChainSceneBasic extends ChainScene {

    
    protected EventChain chain = null;
    protected final boolean editable;

    public ChainSceneBasic(final EventChain chain, final boolean editable) {
        ArgumentChecks.ensureNonNull("chain", chain);
        this.editable = editable;
        setBackground(Color.WHITE);
        
        addChild(getMainLayer());
        addChild(getActionLayer());
        addChild(getConnectionLayer());

        //default actions
        getActions().addAction(ActionFactory.createZoomAction());
        getActions().addAction(ActionFactory.createPanAction());
        getActions().addAction(ActionFactory.createAcceptAction(new DropHandler()));
        getActions().addAction(HOVER_ACTION);
        setChain(chain);
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

        final WChainParametersBasic inParameters = new WChainParametersBasic(this, MessageBundle.getString("inputs"),Integer.MIN_VALUE, editable);
        final WChainParametersBasic outParameters = new WChainParametersBasic(this, MessageBundle.getString("outputs"), Integer.MAX_VALUE, editable);
        getMapping().put(IN, inParameters);
        getMapping().put(OUT, outParameters);
        getMapping().put(ElementProcess.BEGIN, inParameters.getExecutionWidget());
        getMapping().put(ElementProcess.END, outParameters.getExecutionWidget());
        addWigetToMainLayer(inParameters);
        addWigetToMainLayer(outParameters);

        for(final Element ele : chain.getElements()){
            if(ele instanceof ElementProcess){
                createElementProcess((ElementProcess)ele);
            }else if(ele instanceof ElementManual){
                createElementManual((ElementManual)ele);
            }else if(ele instanceof ElementCondition){
                createElementCondition((ElementCondition)ele);
            }
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

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        this.chain.removeListener(this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public EventChain getChain() {
        return chain;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void createElementProcess(final ElementProcess desc){
        final WElementProcess widget = new WElementProcess(this,desc, false, editable);
        widget.setPreferredLocation(new Point(desc.getX(), desc.getY()));
        
        getMapping().put(desc, widget);
        addWigetToMainLayer(widget);
        validate();
        repaint();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void removeElementProcess(final ElementProcess desc){
        final Widget widget = getMapping().remove(desc);
        ArgumentChecks.ensureNonNull("ElementProcess", widget);
        getMainLayer().removeChild(widget);
        getScene().validate();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void createElementManual(final ElementManual mi){
        final WElementManuel widget = new WElementManuel(this, mi, editable);
        widget.setPreferredLocation(new Point(mi.getX(), mi.getY()));

        getMapping().put(mi, widget);
        getMainLayer().addChild(widget);
        validate();
        repaint();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void removeElementManual(final ElementManual mi){
        final Widget widget = getMapping().remove(mi);
        ArgumentChecks.ensureNonNull("ElementManual", widget);
        getMainLayer().removeChild(widget);
        getScene().validate();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void createElementCondition(ElementCondition ce) {
        final WElementCondition widget = new WElementCondition(ce, this, false, editable);
        widget.setPreferredLocation(new Point(ce.getX(), ce.getY()));

        getMapping().put(ce, widget);
        addWigetToMainLayer(widget);
        validate();
        repaint();
    }

     /**
     * {@inheritDoc }
     */
    @Override
    protected void removeElementCondition(ElementCondition ce) {
        final Widget widget = getMapping().remove(ce);
        ArgumentChecks.ensureNonNull("ElementCondition", widget);
        getMainLayer().removeChild(widget);
        getScene().validate();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    protected void createFlowLink(final FlowLink link){

        final EventChain sequence = getChain();
        final Object source = link.getSource(sequence);
        final Object target = link.getTarget(sequence);
        Widget w_source = getMapping().get(source);
        Widget w_target = getMapping().get(target);

        if(w_source instanceof WElementProcess){
            final WElementProcess pw = (WElementProcess) w_source;
            w_source = pw.getExecutionWidget();
        } else if (w_source instanceof WChainParametersFull) {
            final WChainParametersFull pw = (WChainParametersFull) w_source;
            w_source = pw.getExecutionWidget();
        } else if (w_source instanceof WElementManuel) {
            final WElementManuel pw = (WElementManuel) w_source;
            w_source = pw.getExecutionWidget();
        } else if (w_source instanceof WElementCondition) {
            final WElementCondition pw = (WElementCondition) w_source;
            
            //if link already exist, find if it's a success or failed output.
            if (pw.getSuccessLinks().contains(link)) {
                w_source = pw.getWExecutionSuccess();
            } else if (pw.getFailedLinks().contains(link)) {
                w_source = pw.getWExecutionFailed();
            } else {
                //new link : check type and add to Widgetlinks
                //TODO
//                if (DcnsConstants.EXEC_LINK_SUCCESS.equals(link.getType())) {
//                    w_source = pw.getWExecutionSuccess();
//                    pw.getSuccessLinks().add(link);
//                } else {
//                    w_source = pw.getWExecutionFailed();
//                    pw.getFailedLinks().add(link);
//                }
            }
        }

        if(w_target instanceof WElementProcess){
            final WElementProcess pw = (WElementProcess) w_target;
            w_target = pw.getExecutionWidget();
        } else if(w_target instanceof WChainParametersFull){
            final WChainParametersFull pw = (WChainParametersFull) w_target;
            w_target = pw.getExecutionWidget();
        } else if (w_target instanceof WElementManuel) {
            final WElementManuel pw = (WElementManuel) w_target;
            w_target = pw.getExecutionWidget();
        }  else if (w_target instanceof WElementCondition) {
            final WElementCondition pw = (WElementCondition) w_target;
            w_target = pw.getWExecutionTitle();
        }

        ArgumentChecks.ensureNonNull("link source", w_source);
        ArgumentChecks.ensureNonNull("link target", w_target);

        final ConnectionWidget connection = new WFlowLink(this,link);
        connection.setSourceAnchor(new SideAnchor(w_source, false));
        connection.setTargetAnchor(new SideAnchor(w_target, true));
        getMapping().put(link, connection);
        getConnectionLayer().addChild(connection);
        getScene().validate();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void removeFlowLink(final FlowLink link){
        final Widget widget = getMapping().remove(link);
        ArgumentChecks.ensureNonNull("Flow link widget", widget);
        getConnectionLayer().removeChild(widget);
        getScene().validate();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void createInParameter(Parameter param) {
        //Do nothing action not supported in basic view.
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void removeInParameter(Parameter param) {
        //Do nothing action not supported in basic view.
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void createOutParameter(Parameter param) {
        //Do nothing action not supported in basic view.
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void removeOutParameter(Parameter param) {
        //Do nothing action not supported in basic view.
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void createConstant(Constant cst) {
        //Do nothing action not supported in basic view.
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void removeConstant(Constant cst) {
        //Do nothing action not supported in basic view.
    }
/**
     * {@inheritDoc }
     */

    @Override
    protected void createDataLink(DataLink link) {
        //Do nothing action not supported in basic view.
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void removeDataLink(DataLink link) {
        //Do nothing action not supported in basic view.
    }

    /**
     * Return the Widget associate with the input object.
     * @param obj input object like Constant, Element, ...
     * @return widget or null if no found.
     */
    @Override
    public Widget getWidget(final Object obj) {
        return getMapping().get(obj);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected Widget attachNodeWidget(final ProcessDescriptor n) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected Widget attachEdgeWidget(String e) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void attachEdgeSourceAnchor(String e, ProcessDescriptor n, ProcessDescriptor n1) {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void attachEdgeTargetAnchor(String e, ProcessDescriptor n, ProcessDescriptor n1) {
    }

    private static Collection<Object> getDropCandidates(Transferable transferable){
        final DataFlavor[] flavors = transferable.getTransferDataFlavors();

        final Collection<Object> candidates = new ArrayList<Object>();

        for(DataFlavor flavor : flavors){
            Object obj;
            try {
                obj = transferable.getTransferData(flavor);
                if(obj instanceof Collection){
                    for(Object c : (Collection)obj){
                        if(c instanceof ProcessDescriptor){
                            candidates.add((ProcessDescriptor)c);
                        }else if(c instanceof Constant){
                            candidates.add(c);
                        }else if(c instanceof ElementManual){
                            candidates.add(c);
                        }else if(c instanceof ElementCondition){
                            candidates.add(c);
                        }
                    }
                }else if(obj instanceof ProcessDescriptor){
                    candidates.add(obj);
                }else if(obj instanceof Constant){
                    candidates.add(obj);
                }else if(obj instanceof ElementManual){
                    candidates.add(obj);
                }else if(obj instanceof ElementCondition){
                    candidates.add(obj);
                }
            } catch (UnsupportedFlavorException ex) {
            } catch (IOException ex) {}

        }

        return candidates;
    }


    ////////////////////////////////////////////////////////////////////////////
    // chain events /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public void descriptorChange(CollectionChangeEvent event) {
        if(event.getType() == CollectionChangeEvent.ITEM_ADDED){
            for(Object obj : event.getItems()){
                if(obj instanceof ElementProcess){
                    createElementProcess((ElementProcess)obj);
                }else if(obj instanceof ElementCondition){
                    createElementCondition((ElementCondition)obj);
                }else if(obj instanceof ElementManual){
                    createElementManual((ElementManual)obj);
                }
            }
        }else if(event.getType() == CollectionChangeEvent.ITEM_REMOVED){
            for(Object obj : event.getItems()){
                if(obj instanceof ElementProcess){
                    removeElementProcess((ElementProcess)obj);
                }else if(obj instanceof ElementCondition){
                    removeElementCondition((ElementCondition)obj);
                }else if(obj instanceof ElementManual){
                    removeElementManual((ElementManual)obj);
                }
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void executionLinkChange(CollectionChangeEvent event) {
        if(event.getType() == CollectionChangeEvent.ITEM_ADDED){
            for(Object obj : event.getItems()){
                createFlowLink((FlowLink)obj);
            }
        }else if(event.getType() == CollectionChangeEvent.ITEM_REMOVED){
            for(Object obj : event.getItems()){
                removeFlowLink((FlowLink)obj);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void constantChange(CollectionChangeEvent event) {
        //Do nothing action not supported in basic view.
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void linkChange(CollectionChangeEvent event) {
        //Do nothing action not supported in basic view.
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void inputChange(CollectionChangeEvent event) {
        //Do nothing action not supported in basic view.
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void outputChange(CollectionChangeEvent event) {
        //Do nothing action not supported in basic view.
    }

    private class DropHandler implements AcceptProvider {

        @Override
        public ConnectorState isAcceptable(final Widget widget, final Point point, final Transferable transferable) {

            final Collection<Object> candidates = getDropCandidates(transferable);
            if (candidates != null && !candidates.isEmpty()) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                return ConnectorState.ACCEPT;
            } else {
                return ConnectorState.REJECT_AND_STOP;
            }
        }

        @Override
        public void accept(final Widget widget, final Point point, final Transferable transferable) {

            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            final Collection<Object> candidates = getDropCandidates(transferable);
            final EventChain seq = getChain();
            for(Object candidate : candidates){
                if(candidate instanceof ProcessDescriptor){
                    final ProcessDescriptor desc = (ProcessDescriptor) candidate;
                    final ElementProcess pdesc = new ElementProcess(
                            seq.getNextId(),
                            desc.getIdentifier().getAuthority().getIdentifiers().iterator().next().getCode(),
                            desc.getIdentifier().getCode(),
                            point.x,
                            point.y);
                    seq.getElements().add(pdesc);
                }else if(candidate instanceof Constant){
                    final Constant ref = new Constant((Constant)candidate);
                    ref.setId(seq.getNextId());
                    ref.setX(point.x);
                    ref.setY(point.y);
                    seq.getConstants().add(ref);
                }else if(candidate instanceof ElementManual){
                    final ElementManual ref = new ElementManual((ElementManual)candidate);
                    ref.setId(seq.getNextId());
                    ref.setX(point.x);
                    ref.setY(point.y);
                    seq.getElements().add(ref);
                } else if(candidate instanceof ElementCondition){
                    final ElementCondition ref = new ElementCondition((ElementCondition)candidate);
                    ref.setId(seq.getNextId());
                    ref.setX(point.x);
                    ref.setY(point.y);
                    seq.getElements().add(ref);
                }
            }
        }
    }
    
}
