/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.observation.xml.v100;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.AbstractFeatureEntry;
import org.geotoolkit.util.Utilities;
import org.opengis.observation.Observation;
import org.opengis.observation.ObservationCollection;

/**
 *
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObservationCollection")
@XmlRootElement(name = "ObservationCollection")
public class ObservationCollectionEntry extends AbstractFeatureEntry implements ObservationCollection {

    /**
     *  The observation collection
     */
    @XmlElement(required=true)
    private List<ObservationPropertyType> member;
    
    /**
     * A JAXB constructor. 
     */
    public ObservationCollectionEntry() {
        super(null, null, null);
        member = new ArrayList<ObservationPropertyType>();
    }
    
    
    public ObservationCollectionEntry(String title) {
        super(null, null, null);
        member = new ArrayList<ObservationPropertyType>();
        member.add(new ObservationPropertyType(title));
    }
    
    /**
     * Add a new Observation to the collection. 
     */
    public void add(ObservationEntry observation) {
        if (observation != null) {
            this.member.add(new ObservationPropertyType(observation));
        }
    }
    
    /**
     * Return a collection of Observation
     */
    public List<Observation> getMember() {
        List result = new ArrayList<Observation>();
        
        for (ObservationPropertyType obprop: member) {
            result.add(obprop.getObservation());
        }
        return result;
    }
    
     /**
     * Vérifie si cette entré est identique à l'objet spécifié.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object) && object instanceof ObservationCollectionEntry) {
            final ObservationCollectionEntry that = (ObservationCollectionEntry) object;
            if (this.member != null && that.member != null) {
                if (this.member.size() != that.member.size())
                    return false;
            } else if (this.member == null && that.member == null) {
                return true;
            }
            int i = 0;
            for (ObservationPropertyType thisOp: this.member) {
                if (!Utilities.equals(thisOp.getObservation(), that.member.get(i).getObservation()))
                     return false ;  
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
        s.append("[ObservationCollectionEnry]:").append('\n');
        s.append("super:").append(super.toString());
        int i = 1;
        for (ObservationPropertyType obs:member) {
            s.append("observation n" + i + ":").append('\n').append(obs.getObservation().toString());
            i++;
        }
        return s.toString();
    }
    

}
