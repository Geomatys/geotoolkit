/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

package org.geotoolkit.display2d.style.labeling.candidate;

import org.geotoolkit.display2d.style.labeling.LabelDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class Candidate<T extends LabelDescriptor> {

    private final T desc;
    private int priority = 0;
    private int cost = 0;

    public Candidate(final T desc) {
        this.desc = desc;
    }

    public T getDescriptor(){
        return desc;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(final int priority) {
        this.priority = priority;
    }

    public void setCost(final int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }
    
}
