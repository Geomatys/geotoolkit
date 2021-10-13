/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.observation.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractFeatureType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.swes.xml.SOSResponse;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;

/**
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObservationCollection")
@XmlRootElement(name = "ObservationCollection")
public class ObservationCollectionType extends AbstractFeatureType implements ObservationCollection, SOSResponse {

    /**
     *  The observation collection
     */
    @XmlElement(required=true)
    private List<ObservationPropertyType> member;

    /**
     * A JAXB constructor.
     */
    public ObservationCollectionType() {
        super(null, null, null);
        this.member = new ArrayList<>();
    }


    public ObservationCollectionType(final String title) {
        super(null, null, null);
        this.member = new ArrayList<>();
        this.member.add(new ObservationPropertyType(title));
    }

    public ObservationCollectionType(final String id, final EnvelopeType env, final List<ObservationType> observations) {
        super(id, null, null);
        if (observations != null) {
            this.member = new ArrayList<>();
            for (ObservationType observation : observations) {
                this.member.add(new ObservationPropertyType(observation));
            }
        }
        if (env != null) {
            setBoundedBy(env);
        }
    }

    /**
     * Add a new Observation to the collection.
     * @param observation
     */
    public void add(final ObservationType observation) {
        if (observation != null) {
            this.member.add(new ObservationPropertyType(observation));
        }
    }

    /**
     * Return a collection of Observation
     */
    @Override
    public List<Observation> getMember() {
        List result = new ArrayList<>();

        for (ObservationPropertyType obprop: member) {
            result.add(obprop.getObservation());
        }
        return result;
    }

     /**
     * Vérifie si cette entré est identique à l'objet spécifié.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof ObservationCollectionType && super.equals(object, mode)) {
            final ObservationCollectionType that = (ObservationCollectionType) object;
            if (this.member != null && that.member != null) {
                if (this.member.size() != that.member.size()) {
                    return false;
                }
            } else if (this.member == null && that.member == null) {
                return true;
            }
            int i = 0;
            for (ObservationPropertyType thisOp: this.member) {
                if (!Objects.equals(thisOp.getObservation(), that.member.get(i).getObservation())) {
                    return false;
                }
                i++;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.member != null ? this.member.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[ObservationCollection]:").append('\n');
        s.append("super:").append(super.toString());
        int i = 1;
        for (ObservationPropertyType obs:member) {
            s.append("observation n").append(i).append(":\n").append(obs.getObservation());
            i++;
        }
        return s.toString();
    }

    @Override
    public String getSpecificationVersion() {
        return "1.0.0";
    }
}
