/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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

import java.util.Optional;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.IllegalNameException;
import org.geotoolkit.storage.feature.GenericNameIndex;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 * Extends SIS ConcatenatedFeatureSet, adding FeatureCatalogue interface.
 *
 * TODO : move FeatureCatalogue to SIS.
 *
 * @author Johann Sorel (Geomatys)
 */
final class ConcatenatedFeatureSet extends org.apache.sis.storage.aggregate.ConcatenatedFeatureSet implements FeatureCatalogue {

    private final GenericName identifier;
    private final FeatureSet[] sets;
    private GenericNameIndex<FeatureType> allTypes;

    public ConcatenatedFeatureSet(GenericName identifier, FeatureSet[] sets) throws DataStoreException {
        super(null, sets);
        this.identifier = identifier;
        this.sets = sets;
    }

    private synchronized GenericNameIndex<FeatureType> allTypes() throws DataStoreException {
        if (allTypes == null) {
            allTypes = new GenericNameIndex<>();
            for (FeatureSet fs : sets) {
                FeatureType type = fs.getType();
                allTypes.add(type.getName(), type);
                if (fs instanceof FeatureCatalogue fc) {
                    for (GenericName gn : fc.getTypeNames()) {
                        allTypes.add(gn, fc.getFeatureType(gn.toString()));
                    }
                }
            }
        }
        return allTypes;
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        return Optional.ofNullable(identifier);
    }

    @Override
    public Set<GenericName> getTypeNames() throws DataStoreException {
        return allTypes().getNames();
    }

    @Override
    public FeatureType getFeatureType(String name) throws DataStoreException, IllegalNameException {
        return allTypes().get(name);
    }

}
