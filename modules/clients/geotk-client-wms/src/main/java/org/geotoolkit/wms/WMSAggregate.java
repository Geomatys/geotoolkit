/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.storage.AbstractResource;
import org.apache.sis.storage.Resource;
import org.geotoolkit.wms.xml.AbstractLayer;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class WMSAggregate extends AbstractResource implements Aggregate {

    private final List<Resource> children;

    public WMSAggregate(final WebMapClient client, final AbstractLayer layer) throws DataStoreException {
        super(Names.createLocalName(null, ":", layer.getName() == null? "anonymous" : layer.getName()));

        final ArrayList tmp = new ArrayList<>();
        for (final AbstractLayer child : layer.getLayer()) {
            client.asResource(child)
                    .ifPresent(tmp::add);
        }

        children = Collections.unmodifiableList(tmp);
    }

    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        return (Collection) children;
    }
}
