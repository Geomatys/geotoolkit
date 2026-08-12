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
import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.opengis.filter.Filter;

/**
 * To retrieve only the result data of observations, either matching an observation template
 * (SOS 1.0.0) or an offering/observedProperty/foid selection (SOS 2.0.0).
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetResult extends RequestParameters {

    private String observationTemplateId;
    private List<Filter> temporalFilters;
    private String offering;
    private String observedProperty;
    private List<String> featureOfInterestIds;
    private Filter spatialFilter;
    private SOSVersion version;

    /**
     * Identifier of the observation template to query. (required in SOS 1.0.0)
     *
     * @return the observationTemplateId
     */
    public String getObservationTemplateId() {
        return observationTemplateId;
    }

    public void setObservationTemplateId(String observationTemplateId) {
        this.observationTemplateId = observationTemplateId;
    }

    public GetResult observationTemplateId(String observationTemplateId) {
        setObservationTemplateId(observationTemplateId);
        return this;
    }

    /**
     * Temporal filters restricting the result data to retrieve.
     *
     * @return the temporalFilters
     */
    public List<Filter> getTemporalFilters() {
        return temporalFilters;
    }

    public void setTemporalFilters(List<Filter> temporalFilters) {
        this.temporalFilters = temporalFilters;
    }

    public GetResult temporalFilters(List<Filter> temporalFilters) {
        setTemporalFilters(temporalFilters);
        return this;
    }

    /**
     * Observation offering to query. (required in SOS 2.0.0)
     *
     * @return the offering
     */
    public String getOffering() {
        return offering;
    }

    public void setOffering(String offering) {
        this.offering = offering;
    }

    public GetResult offering(String offering) {
        setOffering(offering);
        return this;
    }

    /**
     * Observed property to query. (required in SOS 2.0.0)
     *
     * @return the observedProperty
     */
    public String getObservedProperty() {
        return observedProperty;
    }

    public void setObservedProperty(String observedProperty) {
        this.observedProperty = observedProperty;
    }

    public GetResult observedProperty(String observedProperty) {
        setObservedProperty(observedProperty);
        return this;
    }

    /**
     * Identifiers of the features-of-interest to restrict the result data to. (SOS 2.0.0 only)
     *
     * @return the featureOfInterestIds
     */
    public List<String> getFeatureOfInterestIds() {
        return featureOfInterestIds;
    }

    public void setFeatureOfInterestIds(List<String> featureOfInterestIds) {
        this.featureOfInterestIds = featureOfInterestIds;
    }

    public GetResult featureOfInterestIds(List<String> featureOfInterestIds) {
        setFeatureOfInterestIds(featureOfInterestIds);
        return this;
    }

    /**
     * Spatial filter on the feature-of-interest location. (SOS 2.0.0 only)
     *
     * @return the spatialFilter
     */
    public Filter getSpatialFilter() {
        return spatialFilter;
    }

    public void setSpatialFilter(Filter spatialFilter) {
        this.spatialFilter = spatialFilter;
    }

    public GetResult spatialFilter(Filter spatialFilter) {
        setSpatialFilter(spatialFilter);
        return this;
    }

    public SOSVersion getVersion() {
        return version;
    }

    public void setVersion(SOSVersion version) {
        this.version = version;
    }

    public GetResult version(SOSVersion version) {
        setVersion(version);
        return this;
    }

}
