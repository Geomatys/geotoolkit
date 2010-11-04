/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultResourceMap extends DefaultAbstractObject implements ResourceMap {

    private List<Alias> aliases;

    /**
     * 
     */
    public DefaultResourceMap() {
        this.aliases = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param aliases
     * @param resourceMapSimpleExtensions
     * @param resourceMapObjectExtensions
     */
    public DefaultResourceMap(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<Alias> aliases,
            List<SimpleTypeContainer> resourceMapSimpleExtensions,
            List<Object> resourceMapObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.aliases = (aliases == null) ? EMPTY_LIST : aliases;
        if (resourceMapSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.RESOURCE_MAP).addAll(resourceMapSimpleExtensions);
        }
        if (resourceMapObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.RESOURCE_MAP).addAll(resourceMapObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Alias> getAliases() {
        return this.aliases;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAliases(List<Alias> aliases) {
        this.aliases = (aliases == null) ? EMPTY_LIST : aliases;
    }
}
