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

import static org.geotoolkit.gui.swing.etl.ChainEditorConstants.*;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.chain.model.ElementProcess;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;
import org.opengis.metadata.Identifier;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WElementProcess extends Widget implements WPositionable {

    private final WName execWidget;

    private final ElementProcess descriptor;
    private final Widget w_receivings;
    private final Widget w_offerings;
    private boolean descriptorMissing;
    private Color bgColor;
    private ProcessDescriptor processDescriptor;

    public WElementProcess(final ChainScene scene, final ElementProcess pdesc, final boolean showParameters, final boolean editable) {
        super(scene);
        this.descriptor = pdesc;

        this.bgColor = DEFAULT_CHAIN_ELEMENT_COLOR;
        setOpaque(true);

        getActions().addAction(ActionFactory.createMoveAction());
        if (editable) {
            getActions().addAction(ActionFactory.createPopupMenuAction(new ProcessPopup()));
            getActions().addAction(ActionFactory.createEditAction(new ProcessEditor()));
        }

        final Border border = BorderFactory.createCompositeBorder(
                BorderFactory.createLineBorder(2, Color.BLACK),
                BorderFactory.createEmptyBorder(6));
        setBorder(border);


        try {
            processDescriptor = ProcessFinder.getProcessDescriptor(
                scene.getChain().getFactories().iterator(), pdesc.getAuthority(), pdesc.getCode());
            descriptorMissing = false;
        } catch(NoSuchIdentifierException ex) {

            //process is missing, create a fake one with no parameters
            descriptorMissing = true;
            final DefaultServiceIdentification identification = new DefaultServiceIdentification();
            final Identifier id = new DefaultIdentifier(pdesc.getAuthority());
            final DefaultCitation citation = new DefaultCitation(pdesc.getAuthority());
            citation.setIdentifiers(Collections.singleton(id));
            identification.setCitation(citation);
            processDescriptor = new AbstractProcessDescriptor(
                    pdesc.getCode(),
                    identification,
                    new SimpleInternationalString("missing"),
                    new DefaultParameterDescriptorGroup("input"),
                    new DefaultParameterDescriptorGroup("output")) {
                @Override
                public Process createProcess(ParameterValueGroup input) {
                    throw new UnsupportedOperationException("Not supported.");
                }
            };
        }

        final String name = processDescriptor.getDisplayName() != null ?
                processDescriptor.getDisplayName().toString() :
                processDescriptor.getIdentifier().getCode();

        execWidget = new WName(scene, name, editable, true, true, this);
        execWidget.setFont(CHAIN_ELEMENT_EXECUTION_TITLE_FONT);
        if (descriptorMissing) {
            execWidget.setForeground(CHAIN_TITLE_MISSING_COLOR);
        }
        //underline ChainElement title only if we display parameters.
        if (showParameters) {
            execWidget.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, CHAIN_TITLE_UNDERLINE_COLOR));
        }

        if(processDescriptor.getProcedureDescription() != null){
            execWidget.setToolTipText(processDescriptor.getProcedureDescription().toString());
        }
        setLayout(LayoutFactory.createVerticalFlowLayout());
        addChild(execWidget);

        w_receivings = new Widget(scene);
        w_offerings = new Widget(scene);

        final Widget w_params = new Widget(scene);
        if (showParameters) {
            addChild(w_params);
        }

        final Widget w_separator = new LabelWidget(scene, "");
        w_separator.setBorder(BorderFactory.createLineBorder(0, 1, 0, 0, CHAIN_TITLE_UNDERLINE_COLOR));


        w_params.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.CENTER,5));

        w_params.addChild(w_receivings);
        w_params.addChild(w_separator);
        w_params.addChild(w_offerings);


        final ParameterDescriptorGroup input = processDescriptor.getInputDescriptor();
        w_receivings.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, 4));
        w_receivings.setBorder(BorderFactory.createEmptyBorder(2,0,2,8));
        for(GeneralParameterDescriptor id : input.descriptors()){
            final WReceivingParameter widget = new WReceivingParameter(scene, pdesc, id);
            w_receivings.addChild(0,widget);
        }

        final ParameterDescriptorGroup output = processDescriptor.getOutputDescriptor();
        w_offerings.setLayout(LayoutFactory.createVerticalFlowLayout());
        w_offerings.setBorder(BorderFactory.createEmptyBorder(2,8,2,0));
        for(GeneralParameterDescriptor od : output.descriptors()){
            final WOfferingParameter widget = new WOfferingParameter(scene, pdesc, od, editable);
            w_offerings.addChild(0,widget);
        }
        getScene().validate();
    }

    @Override
    public ElementProcess getObject() {
        return descriptor;
    }

    public Color getBgColor() {
        return bgColor;
    }
    
    public WName getExecutionWidget(){
        return execWidget;
    }

    public boolean isDescriptorMissing() {
        return descriptorMissing;
    }

    public ProcessDescriptor getProcessDescriptor () {
        return processDescriptor;
    }

    /**
     * Search for a specific input parameter using his code.
     * @param code of the parameters
     * @return WReceivingParameter or null if not found.
     */
    public WReceivingParameter getInputWidget(final String code){
        for(Widget w : w_receivings.getChildren()){
            if( ((WReceivingParameter)w).getCode().equals(code)){
                return (WReceivingParameter) w;
            }
        }
        return null;
    }

     /**
     * Get all input element parameters.
     * @return a list of WReceivingParameter. Can't be null.
     */
    public List<WReceivingParameter> getInputsParameters () {
        final List<WReceivingParameter> inputs = new ArrayList<WReceivingParameter>();
        for(Widget w : w_receivings.getChildren()){
            inputs.add((WReceivingParameter)w);
        }
        return inputs;
    }

    /**
     * Search for a specific output parameter using his code.
     * @param code of the parameters
     * @return WOfferingParameter or null if not found.
     */
    public WOfferingParameter getOutputWidget(final String code){
        for(Widget w : w_offerings.getChildren()){
            if( ((WOfferingParameter)w).getCode().equals(code)){
                return (WOfferingParameter) w;
            }
        }
        return null;
    }

    /**
     * Get all output element parameters.
     * @return a list of WOfferingParameter. Can't be null.
     */
    public List<WOfferingParameter> getOutputsParameters () {
        final List<WOfferingParameter> outputs = new ArrayList<WOfferingParameter>();
        for(Widget w : w_offerings.getChildren()){
            outputs.add((WOfferingParameter)w);
        }
        return outputs;
    }

    private class ProcessPopup implements PopupMenuProvider{

        @Override
        public JPopupMenu getPopupMenu(final Widget widget, final Point point) {
            final JPopupMenu menu = new JPopupMenu();
            menu.add(new JMenuItem(
                 new AbstractAction("Configure") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final ChainScene scene = (ChainScene)getScene();
                            final WElementProcess elem = (WElementProcess) widget;

                            final JProcessConfigurePanel pane = new JProcessConfigurePanel(scene, elem);
                            pane.showDialog();
                        }
                    }
                ));
            menu.add(new JMenuItem(
                 new AbstractAction("Delete") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ((ChainScene)getScene()).getChain().getElements().remove(descriptor);
                        }
                    }
                ));

            return menu;
        }

    }

    public Integer getId() {
        if (descriptor != null) {
            return descriptor.getId();
        }
        return -1;
    }

    @Override
    protected void paintWidget() {
        super.paintWidget();
        descriptor.setX(getLocation().x);
        descriptor.setY(getLocation().y);
    }
    
//    private final void showEditor(final AbstractChainScene scene, final WChainElement element) {
//        if (element.getDto().getAuthority().equals(DcnsConstants.GROOVY_REGISTRY_NAME)) {
//        
//        } else {
//            final JProcessConfigurePanel pane = new JProcessConfigurePanel(scene, element);
//            pane.showDialog();
//        }
//    }

    private final class ProcessEditor implements EditProvider {

        @Override
        public void edit(Widget widget) {
            final ChainScene scene = (ChainScene)getScene();
            final WElementProcess elem = (WElementProcess) widget;
            final JProcessConfigurePanel pane = new JProcessConfigurePanel(scene, elem);
            pane.showDialog();
        }
    }
}
