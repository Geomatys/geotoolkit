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

import java.util.Collection;
import org.geotoolkit.coverage.io.GridCoverageReadParam;

/**
 * Subtype of coverage reference which contains a collection of coverage references.
 * All coverages are expected to be similar.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CollectionCoverageReference extends CoverageReference {

    /**
     * Get coverage references which may match the given read parameters.
     *
     * @param readParam, can be null, all references will be returned in this case
     * @return collection of coverage references. never null, can be empty
     */
    Collection<CoverageReference> getCoverages(GridCoverageReadParam readParam);

}
