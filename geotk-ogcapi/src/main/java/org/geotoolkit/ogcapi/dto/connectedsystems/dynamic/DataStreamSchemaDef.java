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
package org.geotoolkit.ogcapi.dto.connectedsystems.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.AbstractDataComponent;
import org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.Category;
import org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.DataRecord;
import org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.Time;

/**
 * Describes the shape of an individual element (record) of an observation stream: which of
 * {@code phenomenonTime}/{@code resultTime}/{@code featureOfInterest}/{@code parameters} are
 * present, and the schema of the {@code result} value.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public class DataStreamSchemaDef extends DataTransferObject {

    public static final String JSON_PROPERTY_PHENOMENON_TIME = "phenomenonTime";
    private Time phenomenonTime;

    public static final String JSON_PROPERTY_RESULT_TIME = "resultTime";
    private Time resultTime;

    public static final String JSON_PROPERTY_FEATURE_OF_INTEREST = "featureOfInterest";
    private Category featureOfInterest;

    public static final String JSON_PROPERTY_RESULT = "result";
    private AbstractDataComponent result;

    public static final String JSON_PROPERTY_PARAMETERS = "parameters";
    private DataRecord parameters;

    /**
     * Schema of the phenomenon time field. At least one of phenomenonTime/resultTime is expected. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME)
    public Time getPhenomenonTime() {
        return phenomenonTime;
    }

    @JsonProperty(JSON_PROPERTY_PHENOMENON_TIME)
    public void setPhenomenonTime(Time phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    public DataStreamSchemaDef phenomenonTime(Time phenomenonTime) {
        setPhenomenonTime(phenomenonTime);
        return this;
    }

    /**
     * Schema of the result time field. At least one of phenomenonTime/resultTime is expected. (optional)
     */
    @JsonProperty(JSON_PROPERTY_RESULT_TIME)
    public Time getResultTime() {
        return resultTime;
    }

    @JsonProperty(JSON_PROPERTY_RESULT_TIME)
    public void setResultTime(Time resultTime) {
        this.resultTime = resultTime;
    }

    public DataStreamSchemaDef resultTime(Time resultTime) {
        setResultTime(resultTime);
        return this;
    }

    /**
     * Schema of the feature of interest field. (optional)
     */
    @JsonProperty(JSON_PROPERTY_FEATURE_OF_INTEREST)
    public Category getFeatureOfInterest() {
        return featureOfInterest;
    }

    @JsonProperty(JSON_PROPERTY_FEATURE_OF_INTEREST)
    public void setFeatureOfInterest(Category featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    public DataStreamSchemaDef featureOfInterest(Category featureOfInterest) {
        setFeatureOfInterest(featureOfInterest);
        return this;
    }

    /**
     * Schema of the observation result value. (required)
     */
    @JsonProperty(JSON_PROPERTY_RESULT)
    public AbstractDataComponent getResult() {
        return result;
    }

    @JsonProperty(JSON_PROPERTY_RESULT)
    public void setResult(AbstractDataComponent result) {
        this.result = result;
    }

    public DataStreamSchemaDef result(AbstractDataComponent result) {
        setResult(result);
        return this;
    }

    /**
     * Schema of the observation parameters. (optional)
     */
    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    public DataRecord getParameters() {
        return parameters;
    }

    @JsonProperty(JSON_PROPERTY_PARAMETERS)
    public void setParameters(DataRecord parameters) {
        this.parameters = parameters;
    }

    public DataStreamSchemaDef parameters(DataRecord parameters) {
        setParameters(parameters);
        return this;
    }

}
