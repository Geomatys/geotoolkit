/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.referencing.dggs.internal.shared;

import org.geotoolkit.referencing.dggs.ZonalReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultZonalReferenceSystem implements ZonalReferenceSystem {

    private final String identifier;
    private final String description;
    private final boolean supportUInt64;

    public DefaultZonalReferenceSystem(String identifier, String description, boolean supportUInt64) {
        this.identifier = identifier;
        this.description = description;
        this.supportUInt64 = supportUInt64;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean supportUInt64Form() {
        return supportUInt64;
    }

}
