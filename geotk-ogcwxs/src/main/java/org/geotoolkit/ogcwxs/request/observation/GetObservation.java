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
package org.geotoolkit.ogcwxs.request.observation;

import javax.xml.namespace.QName;
import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.sos.xml.ResponseModeType;
import org.geotoolkit.sos.xml.v100.EventTime;

/**
 * To retrieve observations matching an offering, a time period and a set of observed properties.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetObservation extends RequestParameters {

    private String srsName;
    private String offering;
    private EventTime[] eventTimes;
    private String[] procedures;
    private String[] observedProperties;
    private org.geotoolkit.sos.xml.v100.GetObservation.FeatureOfInterest featureOfInterest;
    private org.geotoolkit.sos.xml.v100.GetObservation.Result result;
    private String responseFormat;
    private QName resultModel;
    private ResponseModeType responseMode;

    public String getSrsName() {
        return srsName;
    }

    public void setSrsName(String srsName) {
        this.srsName = srsName;
    }

    public GetObservation srsName(String srsName) {
        setSrsName(srsName);
        return this;
    }

    /**
     * Observation offering to query. (required)
     *
     * @return the offering
     */
    public String getOffering() {
        return offering;
    }

    public void setOffering(String offering) {
        this.offering = offering;
    }

    public GetObservation offering(String offering) {
        setOffering(offering);
        return this;
    }

    public EventTime[] getEventTimes() {
        return eventTimes;
    }

    public void setEventTimes(EventTime... eventTimes) {
        this.eventTimes = eventTimes;
    }

    public GetObservation eventTimes(EventTime... eventTimes) {
        setEventTimes(eventTimes);
        return this;
    }

    public String[] getProcedures() {
        return procedures;
    }

    public void setProcedures(String... procedures) {
        this.procedures = procedures;
    }

    public GetObservation procedures(String... procedures) {
        setProcedures(procedures);
        return this;
    }

    /**
     * Observed properties to query. (required)
     *
     * @return the observedProperties
     */
    public String[] getObservedProperties() {
        return observedProperties;
    }

    public void setObservedProperties(String... observedProperties) {
        this.observedProperties = observedProperties;
    }

    public GetObservation observedProperties(String... observedProperties) {
        setObservedProperties(observedProperties);
        return this;
    }

    public org.geotoolkit.sos.xml.v100.GetObservation.FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    public void setFeatureOfInterest(org.geotoolkit.sos.xml.v100.GetObservation.FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    public GetObservation featureOfInterest(org.geotoolkit.sos.xml.v100.GetObservation.FeatureOfInterest featureOfInterest) {
        setFeatureOfInterest(featureOfInterest);
        return this;
    }

    public org.geotoolkit.sos.xml.v100.GetObservation.Result getResult() {
        return result;
    }

    public void setResult(org.geotoolkit.sos.xml.v100.GetObservation.Result result) {
        this.result = result;
    }

    public GetObservation result(org.geotoolkit.sos.xml.v100.GetObservation.Result result) {
        setResult(result);
        return this;
    }

    /**
     * Requested response format. (required)
     *
     * @return the responseFormat
     */
    public String getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public GetObservation responseFormat(String responseFormat) {
        setResponseFormat(responseFormat);
        return this;
    }

    public QName getResultModel() {
        return resultModel;
    }

    public void setResultModel(QName resultModel) {
        this.resultModel = resultModel;
    }

    public GetObservation resultModel(QName resultModel) {
        setResultModel(resultModel);
        return this;
    }

    public ResponseModeType getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(ResponseModeType responseMode) {
        this.responseMode = responseMode;
    }

    public GetObservation responseMode(ResponseModeType responseMode) {
        setResponseMode(responseMode);
        return this;
    }

}
