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
package org.geotoolkit.storage.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.storage.Resource;
import org.opengis.util.GenericName;

/**
 * Default Coverage collection references.<br>
 * This implementation uses the children nodes which are coverages to form a collection.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultCollectionCoverageResource extends AbstractCollectionCoverageResource {

    public DefaultCollectionCoverageResource(CoverageStore store, GenericName name) {
        super(store,name);
    }

    @Override
    public Collection<CoverageResource> getCoverages(GridCoverageReadParam readParam) {
        final List<CoverageResource> resources = new ArrayList<>();
        for (Resource res : components()) {
            if (res instanceof CoverageResource) {
                resources.add((CoverageResource) res);
            }
        }
        return Collections.unmodifiableCollection(resources);
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

}
