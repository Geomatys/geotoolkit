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
package org.geotoolkit.storage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Additional informations for store capabilities.
 * TODO : merge with StoreMetadata from SIS
 *
 * @author Johann Sorel (Geomatys)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StoreMetadataExt {

    /**
     * Indicates what type of resource this store can handle.
     *
     * @return list of supported resource types.
     */
    ResourceType[] resourceTypes();

    /**
     * This method is only for stores of data type : feature.
     * Indicate which kind of geometry a store can write.
     * Some format are limited to points, lines, or can support support the
     * global type 'Geometry'
     *
     * @return list of supported geometry types.
     */
    Class[] geometryTypes() default {};

    /**
     * Indicate if this factory can create new stores.
     *
     * @return true if writing is supported
     */
    boolean canCreate() default false;

    /**
     * Indicate if this factory can write datas is stores.
     *
     * @return true if writing is supported
     */
    boolean canWrite() default false;

    /**
     * Feature store may produce 2 kinds of features.
     * Standard unstyled features like : shapefile, postgresql,
     *
     * @return true if features are styled.
     */
    boolean styledFeature() default false;

}
