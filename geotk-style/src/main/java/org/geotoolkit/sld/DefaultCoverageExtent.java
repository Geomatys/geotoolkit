/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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

import org.geotoolkit.util.StringUtilities;

import org.opengis.sld.CoverageExtent;
import org.opengis.sld.RangeAxis;
import org.opengis.sld.SLDVisitor;

/**
 * Default imumutable coverage extent, thread safe.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
class DefaultCoverageExtent implements CoverageExtent{

    private final String timePeriod;
    private final List<RangeAxis> ranges;

    DefaultCoverageExtent(final String timeperiod, final List<RangeAxis> ranges){
        if(timeperiod != null && ranges != null){
            throw new IllegalArgumentException("You can not have a timeperiod and range axis, only one allow");
        }
        this.timePeriod = timeperiod;

        if(ranges != null){
            List<RangeAxis> copy = new ArrayList<RangeAxis>(ranges);
            this.ranges = Collections.unmodifiableList(copy);
        }else{
            this.ranges = null;
        }

    }

    @Override
    public List<RangeAxis> rangeAxis() {
        return ranges;
    }

    @Override
    public String getTimePeriod() {
        return timePeriod;
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

        DefaultCoverageExtent  other = (DefaultCoverageExtent) obj;

        return Objects.equals(this.timePeriod, other.timePeriod)
                && Objects.equals(this.ranges, other.ranges);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash =1;
        if(timePeriod != null) hash *= timePeriod.hashCode();
        if(ranges != null) hash *= ranges.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[CoverageExtent : ");
        if(timePeriod != null){
            builder.append(" TimePeriod=");
            builder.append(timePeriod);
        }
        if(ranges != null){
            builder.append(" RangeAxis=");
            builder.append(StringUtilities.toCommaSeparatedValues(ranges));
        }
        builder.append(']');
        return builder.toString();
    }

}
