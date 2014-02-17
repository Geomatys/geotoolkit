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

import java.util.List;
import org.geotoolkit.process.chain.model.Parameter;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WChainParametersFull extends WChainParametersBasic {


    private final Widget w_receivings;
    private final Widget w_offerings;

    public WChainParametersFull(final ChainScene scene, final String name, final int id, final boolean editable) {
        super(scene, name, id, editable);
        
        final Widget w_params = new Widget(scene);
        addChild(w_params);

        w_params.setLayout(LayoutFactory.createHorizontalFlowLayout());
        w_receivings = new Widget(scene);
        w_offerings = new Widget(scene);
        w_params.addChild(w_receivings);
        w_params.addChild(w_offerings);


        w_receivings.setLayout(LayoutFactory.createVerticalFlowLayout());
        w_receivings.setBorder(BorderFactory.createEmptyBorder(2,0,2,8));
        w_offerings.setLayout(LayoutFactory.createVerticalFlowLayout());
        w_offerings.setBorder(BorderFactory.createEmptyBorder(2,8,2,0));
        getScene().validate();
    }

    private int getInsertIndex(final Parameter param, final Widget parent){
        // warning : vertical flowbuffer has a reverse order. 0 is at the bottom

        final List<Widget> widgets = parent.getChildren();


        for(int i=0;i<widgets.size();i++){
            Widget c = widgets.get(i);

            final Parameter parameter;
            if(c instanceof WReceivingParameter){
                parameter = ((WReceivingParameter)c).getParameter();
            }else{
                parameter = ((WOfferingParameter)c).getParameter();
            }

            if(param.getCode().compareTo(parameter.getCode()) < 0){
                return i;
            }

        }

        return widgets.size();
    }

    public WOfferingParameter addOfferingParameter(final Parameter param){
        final WOfferingParameter widget = new WOfferingParameter((ChainScene) scene, param, editable);
        w_offerings.addChild(getInsertIndex(param, w_offerings),widget);
        getScene().validate();
        return widget;
    }

    public WReceivingParameter addReceivingParameter(final Parameter param) {
        final WReceivingParameter widget = new WReceivingParameter((ChainScene) scene, param);
        w_receivings.addChild(getInsertIndex(param, w_receivings),widget);
        getScene().validate();
        return widget;
    }

    public WOfferingParameter removeOfferingParameter(final Parameter param){

        for(Widget w : w_offerings.getChildren()){
            if(param.equals( ((WOfferingParameter)w).getParameter())){
                WOfferingParameter off = (WOfferingParameter) w;
                w_offerings.removeChild(off);
                getScene().validate();
                return off;
            }
        }

        return null;
    }

    public WReceivingParameter removeReceivingParameter(final Parameter param){

        for(Widget w : w_receivings.getChildren()){
            if(param.equals( ((WReceivingParameter)w).getParameter())){
                WReceivingParameter off = (WReceivingParameter) w;
                w_receivings.removeChild(off);
                getScene().validate();
                return off;
            }
        }

        return null;
    }

    public WOfferingParameter getInputWidget(String code){
        for(Widget w : w_receivings.getChildren()){
            if( ((WOfferingParameter)w).getLabel().equals(code)){
                return (WOfferingParameter) w;
            }
        }
        return null;
    }

    public WReceivingParameter getOutputWidget(String code){
        for(Widget w : w_offerings.getChildren()){
            if( ((WReceivingParameter)w).getLabel().equals(code)){
                return (WReceivingParameter) w;
            }
        }
        return null;
    }

}
