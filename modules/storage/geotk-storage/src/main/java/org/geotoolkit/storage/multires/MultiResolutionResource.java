/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.multires;

import java.util.Collection;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;

/**
 * A MultiResolutionResource is a ressource which content can be accessed by
 * smaller chunks called Tiles.
 * <p>
 * The resource may expose multiple differents models, a model is a defined tile
 * organisation structure. The most common one being the {@linkplain Pyramid} model.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 */
public interface MultiResolutionResource extends Resource {

    /**
     * Returns the collection of available {@linkplain  MultiResolutionModel}.
     *
     * @return Collection of available models, never null, can be empty.
     * @throws org.apache.sis.storage.DataStoreException
     */
    Collection<? extends MultiResolutionModel> getModels() throws DataStoreException;

    /**
     * Create a new {@linkplain  MultiResolutionModel} based on given model.
     * The created model may have differences.
     * Model identifier may be preserved or not, behavior is implementation specific.
     * If the id is already used a new one will be generated instead.
     *
     * @param template a template model which structure will be used as reference.
     * @return created {@linkplain  MultiResolutionModel}
     * @throws DataStoreException
     */
    MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException;

    /**
     * Remove an existing model.
     *
     * @param identifier not null
     * @throws DataStoreException
     */
    void removeModel(String identifier) throws DataStoreException;

}
