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
package org.geotoolkit.ogcapi.dto.processes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * Request body of {@code POST /processes/{processID}/execution}.
 * {@code inputs} values are left as {@link Object} (plain scalar, embedded
 * object/array, or an inline/href-referenced qualified value) rather than
 * modeled as a dedicated union type, matching the {@code Object}-typed
 * polymorphic-scalar convention already used elsewhere in this module (e.g.
 * {@code InputDescription#maxOccurs}).
 */
@JsonPropertyOrder({
    Execute.JSON_PROPERTY_INPUTS,
    Execute.JSON_PROPERTY_OUTPUTS,
    Execute.JSON_PROPERTY_RESPONSE,
    Execute.JSON_PROPERTY_SUBSCRIBER
})
@XmlRootElement(name = "Execute")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Execute")
public class Execute extends DataTransferObject {

    public static final String JSON_PROPERTY_INPUTS = "inputs";
    @XmlElement(name = "inputs")
    @jakarta.annotation.Nullable
    private Map<String, Object> inputs;

    public static final String JSON_PROPERTY_OUTPUTS = "outputs";
    @XmlElement(name = "outputs")
    @jakarta.annotation.Nullable
    private Map<String, OutputSelection> outputs;

    public static final String JSON_PROPERTY_RESPONSE = "response";
    @XmlElement(name = "response")
    @jakarta.annotation.Nullable
    private ResponseType response = ResponseType.RAW;

    public static final String JSON_PROPERTY_SUBSCRIBER = "subscriber";
    @XmlElement(name = "subscriber")
    @jakarta.annotation.Nullable
    private Subscriber subscriber;

    public Execute() {
    }

    public Execute inputs(@jakarta.annotation.Nullable Map<String, Object> inputs) {
        this.inputs = inputs;
        return this;
    }

    public Execute putInputsItem(String key, Object inputsItem) {
        if (this.inputs == null) {
            this.inputs = new HashMap<>();
        }
        this.inputs.put(key, inputsItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_INPUTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "inputs")
    public Map<String, Object> getInputs() {
        return inputs;
    }

    @JsonProperty(JSON_PROPERTY_INPUTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "inputs")
    public void setInputs(@jakarta.annotation.Nullable Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public Execute outputs(@jakarta.annotation.Nullable Map<String, OutputSelection> outputs) {
        this.outputs = outputs;
        return this;
    }

    public Execute putOutputsItem(String key, OutputSelection outputsItem) {
        if (this.outputs == null) {
            this.outputs = new HashMap<>();
        }
        this.outputs.put(key, outputsItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_OUTPUTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "outputs")
    public Map<String, OutputSelection> getOutputs() {
        return outputs;
    }

    @JsonProperty(JSON_PROPERTY_OUTPUTS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "outputs")
    public void setOutputs(@jakarta.annotation.Nullable Map<String, OutputSelection> outputs) {
        this.outputs = outputs;
    }

    public Execute response(@jakarta.annotation.Nullable ResponseType response) {
        this.response = response;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_RESPONSE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "response")
    public ResponseType getResponse() {
        return response;
    }

    @JsonProperty(JSON_PROPERTY_RESPONSE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "response")
    public void setResponse(@jakarta.annotation.Nullable ResponseType response) {
        this.response = response;
    }

    public Execute subscriber(@jakarta.annotation.Nullable Subscriber subscriber) {
        this.subscriber = subscriber;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_SUBSCRIBER)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "subscriber")
    public Subscriber getSubscriber() {
        return subscriber;
    }

    @JsonProperty(JSON_PROPERTY_SUBSCRIBER)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "subscriber")
    public void setSubscriber(@jakarta.annotation.Nullable Subscriber subscriber) {
        this.subscriber = subscriber;
    }
}
