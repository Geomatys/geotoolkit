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

import java.util.List;
import javax.xml.namespace.QName;
import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.sos.xml.ResponseModeType;
import org.opengis.filter.Filter;

/**
 * To retrieve observations matching an offering, a time period and a set of observed properties.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetObservation extends RequestParameters {

    private String srsName;
    private String offering;
    private List<Filter> temporalFilters;
    private String[] procedures;
    private String[] observedProperties;
    private List<String> featureOfInterestIds;
    private Filter spatialFilter;
    private Filter resultFilter;
    private String responseFormat;
    private QName resultModel;
    private ResponseModeType responseMode;
    private SOSVersion version;

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

    /**
     * Temporal filters restricting the observations to retrieve.
     *
     * @return the temporalFilters
     */
    public List<Filter> getTemporalFilters() {
        return temporalFilters;
    }

    public void setTemporalFilters(List<Filter> temporalFilters) {
        this.temporalFilters = temporalFilters;
    }

    public GetObservation temporalFilters(List<Filter> temporalFilters) {
        setTemporalFilters(temporalFilters);
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

    /**
     * Identifiers of the features-of-interest to restrict the observations to.
     *
     * @return the featureOfInterestIds
     */
    public List<String> getFeatureOfInterestIds() {
        return featureOfInterestIds;
    }

    public void setFeatureOfInterestIds(List<String> featureOfInterestIds) {
        this.featureOfInterestIds = featureOfInterestIds;
    }

    public GetObservation featureOfInterestIds(List<String> featureOfInterestIds) {
        setFeatureOfInterestIds(featureOfInterestIds);
        return this;
    }

    /**
     * Spatial filter on the feature-of-interest location.
     *
     * @return the spatialFilter
     */
    public Filter getSpatialFilter() {
        return spatialFilter;
    }

    public void setSpatialFilter(Filter spatialFilter) {
        this.spatialFilter = spatialFilter;
    }

    public GetObservation spatialFilter(Filter spatialFilter) {
        setSpatialFilter(spatialFilter);
        return this;
    }

    /**
     * Filter on the observation result value.
     *
     * @return the resultFilter
     */
    public Filter getResultFilter() {
        return resultFilter;
    }

    public void setResultFilter(Filter resultFilter) {
        this.resultFilter = resultFilter;
    }

    public GetObservation resultFilter(Filter resultFilter) {
        setResultFilter(resultFilter);
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

    public SOSVersion getVersion() {
        return version;
    }

    public void setVersion(SOSVersion version) {
        this.version = version;
    }

    public GetObservation version(SOSVersion version) {
        setVersion(version);
        return this;
    }

}
