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
package org.geotoolkit.ogcwxs.request.process;

import java.util.List;
import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.wps.xml.v200.DataInput;
import org.geotoolkit.wps.xml.v200.Execute.Mode;
import org.geotoolkit.wps.xml.v200.Execute.Response;
import org.geotoolkit.wps.xml.v200.OutputDefinition;

/**
 * To execute an identified process, synchronously or asynchronously.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class Execute extends RequestParameters {

    private String version;
    private String identifier;
    private List<DataInput> inputs;
    private List<OutputDefinition> outputs;
    private Response response;
    private Mode mode;

    /**
     * The WPS version to request. Defaults to {@code "2.0.0"} if left {@code null}.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public Execute version(String version) {
        setVersion(version);
        return this;
    }

    /**
     * Identifier of the process to execute. (required)
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     * @see #getIdentifier()
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @param identifier the identifier to set
     * @see #getIdentifier()
     */
    public Execute identifier(String identifier) {
        setIdentifier(identifier);
        return this;
    }

    /**
     * Input values to feed the process with. (optional)
     *
     * @return the inputs
     */
    public List<DataInput> getInputs() {
        return inputs;
    }

    /**
     * @param inputs the inputs to set
     * @see #getInputs()
     */
    public void setInputs(List<DataInput> inputs) {
        this.inputs = inputs;
    }

    /**
     * @param inputs the inputs to set
     * @see #getInputs()
     */
    public Execute inputs(List<DataInput> inputs) {
        setInputs(inputs);
        return this;
    }

    /**
     * Definition of the outputs to return. (optional)
     *
     * @return the outputs
     */
    public List<OutputDefinition> getOutputs() {
        return outputs;
    }

    /**
     * @param outputs the outputs to set
     * @see #getOutputs()
     */
    public void setOutputs(List<OutputDefinition> outputs) {
        this.outputs = outputs;
    }

    /**
     * @param outputs the outputs to set
     * @see #getOutputs()
     */
    public Execute outputs(List<OutputDefinition> outputs) {
        setOutputs(outputs);
        return this;
    }

    /**
     * Whether the response should be a raw output value or a full result document. Defaults to
     * {@link Response#document} if left {@code null}. (optional)
     *
     * @return the response
     */
    public Response getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     * @see #getResponse()
     */
    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     * @param response the response to set
     * @see #getResponse()
     */
    public Execute response(Response response) {
        setResponse(response);
        return this;
    }

    /**
     * Desired execution mode: synchronous, asynchronous, or left to the server to decide. Defaults to
     * {@link Mode#auto} if left {@code null}. (optional)
     *
     * @return the mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     * @see #getMode()
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * @param mode the mode to set
     * @see #getMode()
     */
    public Execute mode(Mode mode) {
        setMode(mode);
        return this;
    }

}
