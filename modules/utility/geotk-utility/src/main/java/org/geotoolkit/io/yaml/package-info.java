/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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

/**
 * Read and write objects using the YAML or JSON syntax.
 * In this library, JSON (<cite>JavaScript Object Notation</cite>) is considered as a subset of YAML.
 *
 * {@section Valid objects}
 * This package provides static methods accepting an <code>Object</code> argument.
 * The given object shall be an instance of one of the followings:
 *
 * <ul>
 *   <li>A subclass of {@link org.apache.sis.metadata.AbstractMetadata}.</li>
 *   <li>An implementation of a GeoAPI interface from the {@link org.opengis.metadata} package or sub-packages.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @module
 */
package org.geotoolkit.io.yaml;
