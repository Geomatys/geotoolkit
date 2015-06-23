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
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;
import org.geotoolkit.processing.chain.ChainProcessDescriptor;
import org.geotoolkit.processing.chain.model.ClassFull;
import org.geotoolkit.processing.chain.model.ElementProcess;
import org.geotoolkit.processing.chain.model.Parameter;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;
import org.opengis.parameter.GeneralParameterDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WReceivingParameter extends LabelWidget{

    private final ElementProcess parentDescriptor;
    private final GeneralParameterDescriptor descriptor;
    private final Parameter parameter;
    private final ChainScene scene;

    public WReceivingParameter(ChainScene scene, Parameter parameter) {
        super(scene,"");
        this.scene = scene;
        this.parentDescriptor = null;
        this.descriptor = null;
        this.parameter = parameter;
        setLabel(parameter.getCode());
        setToolTipText(JClassCellRenderer.getShortSymbol(parameter.getType()) +" : "+ parameter.getRemarks());
        init();
        getScene().validate();
    }

    public WReceivingParameter(ChainScene scene,ElementProcess parentDescriptor, GeneralParameterDescriptor descriptor) {
        super(scene,"");
        this.scene = scene;
        this.parentDescriptor = parentDescriptor;
        this.descriptor = descriptor;
        this.parameter = null;
        setLabel(descriptor.getName().getCode());

        //change color upon mandatory/optional
        final int min = descriptor.getMinimumOccurs();
        if(min>0){
            //mandatory
            setForeground(Color.BLACK);
            setLabel(descriptor.getName().getCode()+" *");
        }else{
            //optional
            setForeground(Color.GRAY);
        }

        if(descriptor instanceof ExtendedParameterDescriptor){
            final ExtendedParameterDescriptor ed = (ExtendedParameterDescriptor) descriptor;
            final ClassFull full = (ClassFull) ed.getUserObject().get(ChainProcessDescriptor.KEY_DISTANT_CLASS);
            if(full != null){
                setToolTipText(JClassCellRenderer.getShortSymbol(full) +" : "+ descriptor.getRemarks());
            }else{
                setToolTipText(JClassCellRenderer.getShortSymbol(descriptor.getClass()) +" : "+ descriptor.getRemarks());
            }
        }else{
            setToolTipText(JClassCellRenderer.getShortSymbol(descriptor.getClass()) +" : "+ descriptor.getRemarks());
        }
        init();
        getScene().validate();
    }

    public Parameter getParameter() {
        return parameter;
    }

    private void init(){
        getActions().addAction(ActionFactory.createConnectAction(scene.getActionLayer(), new ProviderParameterConnect(scene)));
    }

    public GeneralParameterDescriptor getDescriptor() {
        return descriptor;
    }

    public ElementProcess getParentDescriptor() {
        return parentDescriptor;
    }

    public int getId(){
        if(parentDescriptor != null){
            return parentDescriptor.getId();
        }else{
            final Widget parentParent = getParentWidget() != null ? getParentWidget().getParentWidget() : null;
            if (parentParent != null && parentParent instanceof WElementCondition) {
                 final WElementCondition condition = (WElementCondition) parentParent;
                 return condition.getId();
            }
            //global output parameter
            return Integer.MAX_VALUE;
        }
    }

    public String getCode(){
        if(parentDescriptor != null){
            return descriptor.getName().getCode();
        }else{
            //global input parameter
            return parameter.getCode();
        }
    }

}
