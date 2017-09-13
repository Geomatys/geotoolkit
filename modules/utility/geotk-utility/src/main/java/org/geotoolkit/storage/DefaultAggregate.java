/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlTransient;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.Resource;
import org.opengis.metadata.Identifier;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlTransient
public class DefaultAggregate extends AbstractResource implements Aggregate {

    protected final List<Resource> resources = new CopyOnWriteArrayList<Resource>();

    public DefaultAggregate(GenericName name) {
        super(name);
    }

    public DefaultAggregate(Identifier identifier) {
        super(identifier);
    }

    public void addResource(Resource res) {
        resources.add(res);
    }

    public void removeResource(Resource res) {
        resources.remove(res);
    }

    @Override
    public Collection<Resource> components() {
        return Collections.unmodifiableList(resources);
    }

}
