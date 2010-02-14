/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

/**
 * A schema for storing coverages metadata in a SQL database.
 *
 * {@section Thread-safety}
 * Every classes made available in this package are thread-safe, and when possible concurrent.
 *
 * {@section Caching}
 * This implementation may cache some database records. The mechanism works correctly when new
 * entries are added in the database, but does not work if existing entries have been modified.
 * In such case the caches need to be cleared. The easiest way to clean them is to dispose the
 * old {@code Database} instance and create a new one using the copy constructor.
 *
 * {@section Subclassing}
 * This package is not designed for subclassing, since the underlying implementation is
 * strictly internal and may change in any future version. Only the public API is stable.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
package org.geotoolkit.coverage.sql;
