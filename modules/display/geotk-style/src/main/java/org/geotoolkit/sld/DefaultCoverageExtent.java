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

import org.opengis.sld.CoverageExtent;
import org.opengis.sld.RangeAxis;
import org.opengis.sld.SLDVisitor;

/**
 * Default imumutable coverage extent, thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
class DefaultCoverageExtent implements CoverageExtent{

    private final String timePeriod;
    private final List<RangeAxis> ranges;
    
    DefaultCoverageExtent(String timeperiod, List<RangeAxis> ranges){
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

        DefaultCoverageExtent  other = (DefaultCoverageExtent) obj;

        return Utilities.equals(this.timePeriod, other.timePeriod)
                && Utilities.equals(this.ranges, other.ranges);

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
        StringBuilder builder = new StringBuilder();
        builder.append("[CoverageExtent : ");
        if(timePeriod != null){
            builder.append(" TimePeriod=");
            builder.append(timePeriod.toString());
        }
        if(ranges != null){
            builder.append(" RangeAxis=");
            builder.append(ranges.toArray().toString());
        }
        builder.append(']');
        return builder.toString();
    }
    
}
