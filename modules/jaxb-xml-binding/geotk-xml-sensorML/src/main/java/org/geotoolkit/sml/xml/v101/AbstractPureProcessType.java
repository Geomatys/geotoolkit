/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sml.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractPureProcess;


/**
 * Complex Type for all soft-typed processes
 * 
 * <p>Java class for AbstractPureProcessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractPureProcessType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0.1}AbstractRestrictedProcessType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}inputs" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}outputs" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}parameters" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractPureProcessType", propOrder = {
    "inputs",
    "outputs",
    "parameters"
})
@XmlSeeAlso({
    ProcessChainType.class,
    ProcessModelType.class
})
public abstract class AbstractPureProcessType extends AbstractRestrictedProcessType implements AbstractPureProcess {

    private Inputs inputs;
    private Outputs outputs;
    private Parameters parameters;

    public AbstractPureProcessType() {

    }

    public AbstractPureProcessType(final AbstractPureProcess pp) {
        super(pp);
        if (pp != null) {
            if (pp.getInputs() != null) {
                this.inputs = new Inputs(pp.getInputs());
            }
            if (pp.getOutputs() != null) {
                this.outputs = new Outputs(pp.getOutputs());
            }
            if (pp.getParameters() != null) {
                this.parameters = new Parameters(pp.getParameters());
            }
        }
    }

    /**
     * Gets the value of the inputs property.
     * 
     */
    public Inputs getInputs() {
        return inputs;
    }

    /**
     * Sets the value of the inputs property.
     * 
     */
    public void setInputs(final Inputs value) {
        this.inputs = value;
    }

    /**
     * Gets the value of the outputs property.
     * 
     */
    public Outputs getOutputs() {
        return outputs;
    }

    /**
     * Sets the value of the outputs property.
     * 
     */
    public void setOutputs(final Outputs value) {
        this.outputs = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     */
    public Parameters getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     */
    public void setParameters(final Parameters value) {
        this.parameters = value;
    }

}
