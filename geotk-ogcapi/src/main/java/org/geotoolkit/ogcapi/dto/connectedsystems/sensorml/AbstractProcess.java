/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.dto.connectedsystems.sensorml;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;
import org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.AbstractDataComponent;

/**
 * Base of every SensorML process description (as opposed to {@link Deployment}, the other
 * concrete root of {@link DescribedObject}).
 *
 * <p>Abstract: not itself listed in {@link DescribedObject}'s {@code @JsonSubTypes} — only its
 * concrete leaves ({@link SimpleProcess}, {@link AggregateProcess}, {@link PhysicalComponent},
 * {@link PhysicalSystem}) are.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 */
public abstract class AbstractProcess extends DescribedObject {

    public static final String JSON_PROPERTY_DEFINITION = "definition";
    private String definition;

    public static final String JSON_PROPERTY_TYPE_OF = "typeOf";
    private Link typeOf;

    public static final String JSON_PROPERTY_CONFIGURATION = "configuration";
    private Settings configuration;

    public static final String JSON_PROPERTY_FEATURES_OF_INTEREST = "featuresOfInterest";
    private List<Link> featuresOfInterest;

    public static final String JSON_PROPERTY_INPUTS = "inputs";
    private List<AbstractDataComponent> inputs;

    public static final String JSON_PROPERTY_OUTPUTS = "outputs";
    private List<AbstractDataComponent> outputs;

    public static final String JSON_PROPERTY_PARAMETERS = "parameters";
    private List<AbstractDataComponent> parameters;

    public static final String JSON_PROPERTY_MODES = "modes";
    private List<Mode> modes;

    /**
     * URI of the process type/procedure this instance implements. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DEFINITION)
    public String getDefinition() {
        return definition;
    }

    @JsonProperty(JSON_PROPERTY_DEFINITION)
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public AbstractProcess definition(String definition) {
        setDefinition(definition);
        return this;
    }

    /**
     * Link to the generic type/model this process instance is a specialization of. (optional)
     */
    @JsonProperty(JSON_PROPERTY_TYPE_OF)
    public Link getTypeOf() {
        return typeOf;
    }

    @JsonProperty(JSON_PROPERTY_TYPE_OF)
    public void setTypeOf(Link typeOf) {
        this.typeOf = typeOf;
    }

    public AbstractProcess typeOf(Link typeOf) {
        setTypeOf(typeOf);
        return this;
    }

    /**
     * Current configuration/settings of the process. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CONFIGURATION)
    public Settings getConfiguration() {
        return configuration;
    }

    @JsonProperty(JSON_PROPERTY_CONFIGURATION)
    public void setConfiguration(Settings configuration) {
        this.configuration = configuration;
    }

    public AbstractProcess configuration(Settings configuration) {
        setConfiguration(configuration);
        return this;
    }

    /**
     * Links to the features of interest this process observes or acts upon. (optional)
     */
    @JsonProperty(JSON_PROPERTY_FEATURES_OF_INTEREST)
    public List<Link> getFeaturesOfInterest() {
        return featuresOfInterest;
    }

    @JsonProperty(JSON_PROPERTY_FEATURES_OF_INTEREST)
    public void setFeaturesOfInterest(List<Link> featuresOfInterest) {
        this.featuresOfInterest = featuresOfInterest;
    }

    public AbstractProcess featuresOfInterest(List<Link> featuresOfInterest) {
        setFeaturesOfInterest(featuresOfInterest);
        return this;
    }

    /**
     * Input data components. Simplification: the standard's {@code IOComponentChoice} union
     * (a named {@code AnyComponent} or a named {@code ObservableProperty}) is modeled here just
     * as {@link AbstractDataComponent}, since the {@code ObservableProperty} variant is rare in
     * practice. (optional)
     */
    @JsonProperty(JSON_PROPERTY_INPUTS)
    public List<AbstractDataComponent> getInputs() {
        return inputs;
    }

    @JsonProperty(JSON_PROPERTY_INPUTS)
    public void setInputs(List<AbstractDataComponent> inputs) {
        this.inputs = inputs;
    }

    public AbstractProcess inputs(List<AbstractDataComponent> inputs) {
        setInputs(inputs);
        return this;
    }

    /**
     * Output data components. Same {@code IOComponentChoice} simplification as
     * {@link #getInputs()}. (optional)
     */
    @JsonProperty(JSON_PROPERTY_OUTPUTS)
    public List<AbstractDataComponent> getOutputs() {
        return outputs;
    }

    @JsonProperty(JSON_PROPERTY_OUTPUTS)
    public void setOutputs(List<AbstractDataComponent> outputs) {
        this.outputs = outputs;
    }

    public AbstractProcess outputs(List<AbstractDataComponent> outputs) {
        setOutputs(outputs);
        return this;
    }

    /**
     * Parameter data components. Same {@code IOComponentChoice} simplification as
     * {@link #getInputs()}. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    public List<AbstractDataComponent> getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    public void setParameters(List<AbstractDataComponent> parameters) {
        this.parameters = parameters;
    }

    public AbstractProcess parameters(List<AbstractDataComponent> parameters) {
        setParameters(parameters);
        return this;
    }

    /**
     * Operating modes this process can be configured with. (optional)
     */
    @JsonProperty(JSON_PROPERTY_MODES)
    public List<Mode> getModes() {
        return modes;
    }

    @JsonProperty(JSON_PROPERTY_MODES)
    public void setModes(List<Mode> modes) {
        this.modes = modes;
    }

    public AbstractProcess modes(List<Mode> modes) {
        setModes(modes);
        return this;
    }

}
