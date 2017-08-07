/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

import org.opengis.geometry.Geometry;

/**
 * Base class for {@link org.geotoolkit.storage.DataStoreFactory} metadata. It should be retrived via {@link DataStoreFactory#getMetadata()}.
 *
 * @author Alexis Manin (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public interface FactoryMetadata {

    /**
     * Data types stores.
     *
     * @return DataType
     */
    DataType getDataType();

    /**
     * Indicate if this factory can open existing and read datas.
     *
     * @return true if reading is supported
     */
    boolean supportStoreReading();

    /**
     * Indicate if this factory can create new stores.
     *
     * @return true if writing is supported
     */
    boolean supportStoreCreation();

    /**
     * Indicate if this factory can write datas is stores.
     *
     * @return true if writing is supported
     */
    boolean supportStoreWriting();

    /**
     * This method is only for stores of data type : feature.
     * Indicate which kind of geometry a store can write.
     * Some format are limited to points, lines, or can support support the
     * global type 'Geometry'
     *
     * @return
     */
    Class<Geometry>[] supportedGeometryTypes();

    /**
     * Feature store may produce 2 kinds of features.
     * Standard unstyled features like : shapefile, postgresql,
     *
     * @return true if features are styled.
     */
    boolean produceStyledFeature();


}
