/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.sos.netcdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.sampling.SamplingFeature;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ExtractionResult {

    public final GeoSpatialBound spatialBound = new GeoSpatialBound();

    public final List<Observation> observations = new ArrayList<>();

    public final List<String> featureOfInterestNames = new ArrayList<>();

    public final List<SamplingFeature> featureOfInterest = new ArrayList<>();

    public final List<String> fields = new ArrayList<>();

    public final List<Phenomenon> phenomenons = new ArrayList<>();

    public final List<ProcedureTree> procedures = new ArrayList<>();

    public void addFeatureOfInterest(final org.geotoolkit.sampling.xml.SamplingFeature sf) {
        this.featureOfInterest.add(sf);
        this.featureOfInterestNames.add(sf.getId());
    }

    public static class ProcedureTree {

        public final String id;

        public final String name;

        public final String description;

        /**
         * The SML type of the process (System, Component, ...)
         */
        public final String type;

        /**
         * The observation type of the process (timeseries, trajectory, profile...)
         */
        public final String omType;

        public final List<ProcedureTree> children = new ArrayList<>();

        public final GeoSpatialBound spatialBound = new GeoSpatialBound();

        public final List<String> fields = new ArrayList<>();

        public ProcedureTree(final String id, final String name, final String description, final String type, final String omType) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = type;
            this.omType = omType;
        }

        public ProcedureTree(final String id, final String name, final String description, final String type, final String omType, final Collection<String> fields) {
            this.id   = id;
            this.name = name;
            this.description = description;
            this.type = type;
            this.omType = omType;
            this.fields.addAll(fields);
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof ProcedureTree) {
                final ProcedureTree that = (ProcedureTree) obj;
                return Objects.equals(this.id,       that.id)   &&
                       Objects.equals(this.name,     that.name)   &&
                       Objects.equals(this.type,     that.type) &&
                       Objects.equals(this.omType,   that.omType) &&
                       Objects.equals(this.children, that.children) &&
                       Objects.equals(this.description, that.description);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 79 * hash + Objects.hashCode(this.id);
            hash = 79 * hash + Objects.hashCode(this.name);
            hash = 79 * hash + Objects.hashCode(this.description);
            hash = 79 * hash + Objects.hashCode(this.type);
            hash = 79 * hash + Objects.hashCode(this.omType);
            hash = 79 * hash + Objects.hashCode(this.children);
            return hash;
        }
    }
}
