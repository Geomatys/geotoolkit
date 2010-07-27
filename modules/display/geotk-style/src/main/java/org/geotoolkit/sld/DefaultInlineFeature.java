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
import java.util.Collection;
import java.util.Collections;

import org.opengis.feature.Feature;
import org.opengis.sld.InlineFeature;
import org.opengis.sld.SLDVisitor;

/**
 * Default immutable inline feature, thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class DefaultInlineFeature implements InlineFeature{

    private final Collection<Collection<Feature>> features;
    
    /**
     * Default constructor
     */
    DefaultInlineFeature(Collection<Collection<Feature>> features){
        if(features != null){
            Collection<Collection<Feature>> copy = new ArrayList<Collection<Feature>>();
            for(Collection<Feature> col : features){
                copy.add(Collections.unmodifiableCollection(new ArrayList<Feature>(col)));
            }
            this.features = Collections.unmodifiableCollection(copy);
        }else{
            this.features = Collections.emptyList();
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Collection<Feature>> features() {
        return features;
    }

    /**
     * {@inheritDoc }
     */
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

        DefaultInlineFeature other = (DefaultInlineFeature) obj;

        return this.features.equals(other.features);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return 17*features.hashCode() ;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[InlineFeature : Size=");
        builder.append(features.size());
        builder.append(']');
        return builder.toString();
    }
}
