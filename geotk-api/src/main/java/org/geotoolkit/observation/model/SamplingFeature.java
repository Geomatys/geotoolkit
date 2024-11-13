/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.observation.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.locationtech.jts.geom.Geometry;
import org.opengis.observation.AnyFeature;
import org.opengis.observation.sampling.SamplingFeatureRelation;
import org.opengis.observation.sampling.SurveyProcedure;

/**
 *
 *  @author Guilhem Legal (Geomatys)
 */
public class SamplingFeature extends AbstractOMEntity implements org.opengis.observation.sampling.SamplingFeature {

    private String sampledFeatureId;
    private Geometry geometry;

    private SamplingFeature() {}

    public SamplingFeature(String id, String name, String description, Map<String, Object> properties, String sampledFeatureId, Geometry geometry) {
        super(id, name, description, properties);
        this.sampledFeatureId = sampledFeatureId;
        this.geometry = geometry;
    }

    public String getSampledFeatureId() {
        return sampledFeatureId;
    }

    public void setSampledFeatureId(String sampledFeatureId) {
        this.sampledFeatureId = sampledFeatureId;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public List<SamplingFeatureRelation> getRelatedSamplingFeature() {
        return null;
    }

    @Override
    public SurveyProcedure getSurveyDetail() {
        return null;
    }

    @Override
    public List<AnyFeature> getSampledFeature() {
        return null;
    }

    @Override
    public List<org.opengis.observation.Observation> getRelatedObservation() {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("sampledFeatureId=").append(sampledFeatureId).append('\n');
        sb.append("geometry=").append(geometry).append('\n');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SamplingFeature that && super.equals(obj)) {
            return Objects.equals(this.geometry,         that.geometry) &&
                   Objects.equals(this.sampledFeatureId, that.sampledFeatureId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + super.hashCode();
        hash = 23 * hash + Objects.hashCode(this.sampledFeatureId);
        hash = 23 * hash + Objects.hashCode(this.geometry);
        return hash;
    }
}
