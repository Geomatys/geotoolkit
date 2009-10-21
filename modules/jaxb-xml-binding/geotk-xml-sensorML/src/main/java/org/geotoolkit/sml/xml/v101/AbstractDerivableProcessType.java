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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractDerivableProcessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractDerivableProcessType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0.1}AbstractProcessType">
 *       &lt;sequence>
 *         &lt;element name="inputs" type="{http://www.opengis.net/sensorML/1.0.1}inputsPropertyType" minOccurs="0"/>
 *         &lt;element name="outputs" type="{http://www.opengis.net/sensorML/1.0.1}outputsPropertyType" minOccurs="0"/>
 *         &lt;element name="parameters" type="{http://www.opengis.net/sensorML/1.0.1}parametersPropertyType" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}method"/>
 *           &lt;sequence>
 *             &lt;element name="components" type="{http://www.opengis.net/sensorML/1.0.1}componentsPropertyType"/>
 *             &lt;element name="dataSources" type="{http://www.opengis.net/sensorML/1.0.1}dataSourcesPropertyType" minOccurs="0"/>
 *             &lt;element name="connections" type="{http://www.opengis.net/sensorML/1.0.1}connectionsPropertyType"/>
 *           &lt;/sequence>
 *         &lt;/choice>
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
@XmlType(name = "AbstractDerivableProcessType", propOrder = {
    "inputs",
    "outputs",
    "parameters",
    "method",
    "components",
    "dataSources",
    "connections"
})
public abstract class AbstractDerivableProcessType extends AbstractProcessType {

    private InputsPropertyType inputs;
    private OutputsPropertyType outputs;
    private ParametersPropertyType parameters;
    private MethodPropertyType method;
    private ComponentsPropertyType components;
    private DataSourcesPropertyType dataSources;
    private ConnectionsPropertyType connections;

    /**
     * Gets the value of the inputs property.
     * 
     */
    public InputsPropertyType getInputs() {
        return inputs;
    }

    /**
     * Sets the value of the inputs property.
     * 
    */
    public void setInputs(InputsPropertyType value) {
        this.inputs = value;
    }

    /**
     * Gets the value of the outputs property.
     * 
     */
    public OutputsPropertyType getOutputs() {
        return outputs;
    }

    /**
     * Sets the value of the outputs property.
     * 
     */
    public void setOutputs(OutputsPropertyType value) {
        this.outputs = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     */
    public ParametersPropertyType getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     */
    public void setParameters(ParametersPropertyType value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the method property.
     * 
     */
    public MethodPropertyType getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     */
    public void setMethod(MethodPropertyType value) {
        this.method = value;
    }

    /**
     * Gets the value of the components property.
     * 
    */
    public ComponentsPropertyType getComponents() {
        return components;
    }

    /**
     * Sets the value of the components property.
     */
    public void setComponents(ComponentsPropertyType value) {
        this.components = value;
    }

    /**
     * Gets the value of the dataSources property.
     * 
     */
    public DataSourcesPropertyType getDataSources() {
        return dataSources;
    }

    /**
     * Sets the value of the dataSources property.
     * 
     */
    public void setDataSources(DataSourcesPropertyType value) {
        this.dataSources = value;
    }

    /**
     * Gets the value of the connections property.
     * 
     */
    public ConnectionsPropertyType getConnections() {
        return connections;
    }

    /**
     * Sets the value of the connections property.
     * 
     */
    public void setConnections(ConnectionsPropertyType value) {
        this.connections = value;
    }

}
