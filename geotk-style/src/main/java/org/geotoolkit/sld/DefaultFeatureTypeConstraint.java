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
package org.geotoolkit.sld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.opengis.filter.Filter;
import org.opengis.sld.Extent;
import org.opengis.sld.FeatureTypeConstraint;
import org.opengis.sld.SLDVisitor;
import org.opengis.util.GenericName;

/**
 * Default imumutable feature type constraint, thread safe.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
class DefaultFeatureTypeConstraint implements FeatureTypeConstraint{

    private final GenericName featureName;
    private final Filter filter;
    private final List<Extent> extents;

    /**
     * default constructor.
     */
    DefaultFeatureTypeConstraint(final GenericName name, final Filter filter, final List<Extent> extents){
        this.featureName = name;
        this.filter = filter;

        if(extents != null){
            List<Extent> copy = new ArrayList<Extent>(extents);
            this.extents = Collections.unmodifiableList(copy);
        }else{
            this.extents = Collections.emptyList();
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GenericName getFeatureTypeName() {
        return featureName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Filter getFilter() {
        return filter;
    }

    /**
     * {@inheritDoc }
     * This is an immutable list.
     */
    @Override
    public List<Extent> getExtent() {
        return extents;
    }

    @Override
    public Object accept(final SLDVisitor visitor, final Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {

        if(this == obj){
            return true;
        }

        if(obj == null || !this.getClass().equals(obj.getClass()) ){
            return false;
        }

        DefaultFeatureTypeConstraint other = (DefaultFeatureTypeConstraint) obj;

        return Objects.equals(this.featureName, other.featureName)
                && Objects.equals(this.filter, other.filter)
                && this.extents.equals(other.extents);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 3;
        if(featureName != null) hash *= featureName.hashCode();
        if(filter != null) hash *= filter.hashCode();
        hash *= extents.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[FeatureTypeConstraint : ");
        if(featureName != null){
            builder.append(" FeatureName=");
            builder.append(featureName);
        }
        if(filter != null){
            builder.append(" Filter=");
            builder.append(filter);
        }
        builder.append(" Extent size=");
        builder.append(extents.size());
        builder.append(']');
        return builder.toString();
    }

}
