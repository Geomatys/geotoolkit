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

import org.geotoolkit.util.Utilities;

import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.sld.Extent;
import org.opengis.sld.FeatureTypeConstraint;
import org.opengis.sld.SLDVisitor;

/**
 * Default imumutable feature type constraint, thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 */
class DefaultFeatureTypeConstraint implements FeatureTypeConstraint{

    private final Name featureName;
    private final Filter filter;
    private final List<Extent> extents;
    
    /**
     * default constructor.
     */
    DefaultFeatureTypeConstraint(Name name, Filter filter, List<Extent> extents){
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
    public Name getFeatureTypeName() {
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
    public Object accept(SLDVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {

        if(this == obj){
            return true;
        }

        if(obj == null || !this.getClass().equals(obj.getClass()) ){
            return false;
        }

        DefaultFeatureTypeConstraint other = (DefaultFeatureTypeConstraint) obj;

        return Utilities.equals(this.featureName, other.featureName)
                && Utilities.equals(this.filter, other.filter)
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
        StringBuilder builder = new StringBuilder();
        builder.append("[FeatureTypeConstraint : ");
        if(featureName != null){
            builder.append(" FeatureName=");
            builder.append(featureName.toString());
        }
        if(filter != null){
            builder.append(" Filter=");
            builder.append(filter.toString());
        }
        builder.append(" Extent size=");
        builder.append(extents.size());
        builder.append(']');
        return builder.toString();
    }
    
}
