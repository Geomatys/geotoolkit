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
import org.geotoolkit.sampling.xml.SamplingFeature;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;

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
    
    public void addFeatureOfInterest(final SamplingFeature sf) {
        this.featureOfInterest.add(sf);
        this.featureOfInterestNames.add(sf.getId());
    }
    
    public static class ProcedureTree {
        
        public final String id;
        
        public final String type;
         
        public final List<ProcedureTree> children = new ArrayList<>();
        
        public final GeoSpatialBound spatialBound = new GeoSpatialBound();
        
        public final List<String> fields = new ArrayList<>();
        
        public ProcedureTree(final String id, final String type) {
            this.id   = id;
            this.type = type;
        }
        
        public ProcedureTree(final String id, final String type, final Collection<String> fields) {
            this.id   = id;
            this.type = type;
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
                       Objects.equals(this.type,     that.type) &&
                       Objects.equals(this.children, that.children);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 79 * hash + Objects.hashCode(this.id);
            hash = 79 * hash + Objects.hashCode(this.type);
            hash = 79 * hash + Objects.hashCode(this.children);
            return hash;
        }
    }
}
