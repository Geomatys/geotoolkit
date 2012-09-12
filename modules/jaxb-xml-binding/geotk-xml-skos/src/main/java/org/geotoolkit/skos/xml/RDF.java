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
package org.geotoolkit.skos.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="RDF", namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#")
public class RDF implements Serializable {

    @XmlElement(name="Description", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private List<Concept> description;

    @XmlElement(name="Concept", namespace = "http://www.w3.org/2004/02/skos/core#")
    private List<Concept> concept;

    public RDF() {
        concept = new ArrayList<Concept>();
    }

    public RDF(final List<Concept> concept) {
        this.concept = concept;
    }

    public RDF(final List<Concept> concept, final List<Concept> description) {
        this.concept = concept;
        this.description = description;
    }

    public List<Concept> getConcept() {
        if (concept == null) {
            concept = new ArrayList<Concept>();
        }
        return concept;
    }

    public void setConcept(final List<Concept> concept) {
        this.concept = concept;
    }

    /**
     * @return the description
     */
    public List<Concept> getDescription() {
        if (description == null) {
            description = new ArrayList<Concept>();
        }
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(List<Concept> description) {
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[RDF]:").append('\n');
        if (concept != null) {
            sb.append("nb concept: ").append(concept.size()).append('\n');
            for (Concept c : concept) {
                sb.append(c).append('\n');
            }
        }
        if (description != null) {
            sb.append("nb description: ").append(description.size()).append('\n');
            for (Concept c : description) {
                sb.append(c).append('\n');
            }
        }
        return sb.toString();
    }

    /*
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof RDF) {
            final RDF that = (RDF) object;

            return Utilities.equals(this.concept, that.concept) &&
                   Utilities.equals(this.description, that.description);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 73 * hash + (this.concept != null ? this.concept.hashCode() : 0);
        return hash;
    }

}
