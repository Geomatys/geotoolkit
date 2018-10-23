/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.coverage.sql;


/**
 * Whether adding a new raster is allowed to create a new product.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public enum AddOption {
    /**
     * Fails if no product of the given name exists.
     */
    NO_CREATE,

    /**
     * Creates a new product if it does not already exists.
     */
    CREATE_PRODUCT,

    /**
     * Creates a new product, failing if a product of the same name already exists.
     */
    CREATE_NEW_PRODUCT
}
