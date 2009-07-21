/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
 * Root package for various metadata implementations. This root package is not
 * linked to any particular metadata standard. It assumes that a standard is
 * defined through a set of Java interfaces (for example {@link org.opengis.metadata})
 * and uses reflection for performing basic operations like comparisons and copies.
 * <p>
 * Possible metadata implementations (already available or planed in a future
 * Geotoolkit version) are:
 * <UL>
 *   <LI><P>{@link org.geotoolkit.metadata.iso}: concrete implementation of ISO
 *       interfaces, including ISO 19115.</P></LI>
 *   <LI><P>{@code org.geotoolkit.metadata.dublin}: concrete implementation of
 *       Dublin core interfaces. <EM>Not yet implemented.</EM></P></LI>
 *   <LI><P>{@link org.geotoolkit.metadata.sql}: implementation of metadata interfaces
 *       backed by a SQL database. The metadata interfaces doesn't need to be ISO ones,
 *       which is why this package is not a sub-package of the ISO's one.</P></LI>
 * </UL>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.02
 *
 * @since 2.0
 * @module
 */
package org.geotoolkit.metadata;
