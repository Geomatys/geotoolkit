/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.capability;

import java.util.List;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * Immutable function name.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
@Deprecated
@XmlTransient
public class FunctionName {

    private final String name;
    private final List<String> argNames;
    private final int size;

    public FunctionName(final String name, final List<String> argNames, final int size) {
        this.name = name;
        this.argNames = argNames;
        this.size = size;
    }

    public int getArgumentCount() {
        return size;
    }

    public List<String> getArgumentNames() {
        return argNames;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FunctionName other = (FunctionName) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.argNames != other.argNames && (this.argNames == null || !this.argNames.equals(other.argNames))) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + (this.argNames != null ? this.argNames.hashCode() : 0);
        hash = 53 * hash + this.size;
        return hash;
    }
}
