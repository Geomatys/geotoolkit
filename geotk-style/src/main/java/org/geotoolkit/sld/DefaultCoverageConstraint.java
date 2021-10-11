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

import java.util.Objects;

import org.opengis.sld.CoverageConstraint;
import org.opengis.sld.CoverageExtent;
import org.opengis.sld.SLDVisitor;

import static org.apache.sis.util.ArgumentChecks.*;

/**
 * Default imumutable coverage constraint, thread safe.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
class DefaultCoverageConstraint implements CoverageConstraint{

    private final String coverageName;
    private final CoverageExtent extent;

    /**
     * default constructor.
     */
    DefaultCoverageConstraint(final String coverageName, final CoverageExtent extent){
        ensureNonNull("coverage name", coverageName);
        this.coverageName = coverageName;
        this.extent = extent;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getCoverageName() {
        return coverageName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoverageExtent getCoverageExtent() {
        return extent;
    }

    /**
     * {@inheritDoc }
     */
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

        DefaultCoverageConstraint other = (DefaultCoverageConstraint) obj;

        return Objects.equals(this.coverageName, other.coverageName)
                && Objects.equals(this.extent, other.extent);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 1;
        if(coverageName != null) hash *= coverageName.hashCode();
        if(extent != null)  hash *= extent.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[coverageConstraint : Name=");
        builder.append(coverageName);
        if(extent != null){
            builder.append(" Extent=");
            builder.append(extent);
        }
        builder.append(']');
        return builder.toString();
    }

}
