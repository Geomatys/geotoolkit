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
package org.geotoolkit.ogcapi.storage;

import java.util.Optional;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.ogcapi.model.common.CollectionDescription;
import org.opengis.util.GenericName;

/**
 * Collection item view as a Resource.
 *
 * TODO : detect item type and map it to the correct Resource type.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CollectionItemResource extends AbstractResource {

    protected final CollectionResource parent;
    protected final CollectionDescription description;

    public CollectionItemResource(CollectionResource parent, CollectionDescription description) {
        super(parent);
        this.parent = parent;
        this.description = description;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(Names.createLocalName(null, null, description.getId()));
    }

}
