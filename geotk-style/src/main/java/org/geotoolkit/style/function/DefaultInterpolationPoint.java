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
package org.geotoolkit.style.function;

import java.util.Objects;
import org.opengis.filter.Expression;

import static org.apache.sis.util.ArgumentChecks.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class DefaultInterpolationPoint implements InterpolationPoint{

    private final Expression value;
    private final Number data;

    public DefaultInterpolationPoint(final Number data, final Expression value){
        ensureNonNull("value", value);
        this.value = value;
        this.data = data;
    }

    @Override
    public Expression getValue() {
        return value;
    }

    @Override
    public Number getData() {
        return data;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.value);
        hash = 97 * hash + Objects.hashCode(this.data);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultInterpolationPoint other = (DefaultInterpolationPoint) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.data, other.data)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "InterpolationPoint "+data+" = "+value;
    }



}
