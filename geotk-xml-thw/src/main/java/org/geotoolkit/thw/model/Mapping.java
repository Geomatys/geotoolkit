/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

package org.geotoolkit.thw.model;

import java.util.Objects;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class Mapping {
    /**
     * The URI of the concept that is the parent of the relation
     */
    private String source;

    /**
     * The URI of the relationship
     */
    private String relation;

    /**
     * The URI of the child
     */
    private String target;


    public Mapping() {

    }

    public Mapping(final String source, final String relation, final String target) {
        this.relation = relation;
        this.source   = source;
        this.target   = target;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(final String source) {
        this.source = source;
    }

    /**
     * @return the relation
     */
    public String getRelation() {
        return relation;
    }

    /**
     * @param relation the relation to set
     */
    public void setRelation(final String relation) {
        this.relation = relation;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(final String target) {
        this.target = target;
    }

    /*
     * Verifie si cette entree est identique a l'objet specifie.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Mapping) {
            final Mapping that = (Mapping) object;

            return Objects.equals(this.relation, that.relation)   &&
                   Objects.equals(this.source,   that.source)     &&
                   Objects.equals(this.target,   that.target);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 79 * hash + (this.relation != null ? this.relation.hashCode() : 0);
        hash = 79 * hash + (this.target != null ? this.target.hashCode() : 0);
        return hash;
    }
}
