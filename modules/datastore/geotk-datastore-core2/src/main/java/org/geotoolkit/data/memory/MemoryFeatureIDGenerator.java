/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.memory;

import java.util.concurrent.atomic.AtomicLong;
import org.geotoolkit.data.FeatureIDGenerator;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.opengis.filter.identity.FeatureId;

/**
 * Simple implementation of a feature id generator which
 * contatenate a String with a number.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MemoryFeatureIDGenerator implements FeatureIDGenerator{

    private final String base;
    private final AtomicLong inc = new AtomicLong();
    
    /**
     * @param base string use as start element of the generated ids
     */
    public MemoryFeatureIDGenerator(String base){
        if(base == null) throw new NullPointerException("Base string can not ben ull.");
        this.base = base+"_";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureId next() {
        return new DefaultFeatureId(base+inc.incrementAndGet());
    }

}
