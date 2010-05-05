/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
 * The policy to apply during a table update if a record already exists for the same
 * primary key.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @since 3.12 (derived from Seagis)
 * @module
 */
enum UpdatePolicy {
    /**
     * The new record is discarted and the existing one is keept unchanged.
     */
    SKIP_EXISTING,

    /**
     * The old record is deleted and the new record is inserted as a replacement.
     */
    REPLACE_EXISTING,

    /**
     * Remove all previous records before to insert new ones.
     */
    CLEAR_BEFORE_UPDATE
}
