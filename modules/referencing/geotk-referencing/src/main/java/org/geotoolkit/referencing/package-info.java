/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 * {@linkplain org.geotoolkit.referencing.AbstractReferenceSystem Reference system} implementations.
 * An explanation for this package is provided in the {@linkplain org.opengis.referencing OpenGIS&reg;
 * javadoc}. The remaining discussion on this page is specific to the Geotk implementation.
 * <p>
 * This package provides implementations for general positioning, coordinate reference systems (CRS),
 * and coordinate transformations. Coordinates can have any number of dimensions. So this implementation
 * can handle 2D and 3D coordinates, as well as 4D, 5D, <i>etc.</i>
 * <p>
 * This package provides a special implementation of
 * {@linkplain org.geotoolkit.referencing.NamedIdentifier identifier}, which is also a
 * {@linkplain org.opengis.util.GenericName generic name}. By implementing those two
 * interfaces, it is possible to use the same kind of object for specifying both the
 * {@linkplain org.geotoolkit.referencing.AbstractIdentifiedObject#getName main identifier} and the
 * {@linkplain org.geotoolkit.referencing.AbstractIdentifiedObject#getAlias aliases} of an
 * {@linkplain org.geotoolkit.referencing.AbstractIdentifiedObject identified object}.
 * <p>
 * All factory methods are capable to find an object using an unscoped (or local) name. However,
 * in order to avoid potential conflict, it is recommended to use scoped name when possible. For
 * example even if both can work, prefer {@code "EPSG:9624"} instead of {@code "9624"} for the
 * affine transform in order to avoid potential conflict with an other authority using the same
 * code number.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.18
 *
 * @see <a href="http://www.geotoolkit.org/modules/referencing/supported-codes.html">List of authority codes</a>
 *
 * @since 2.0
 * @module
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED, namespace = Namespaces.GML, xmlns = {
    @XmlNs(prefix = "gml", namespaceURI = Namespaces.GML),
    @XmlNs(prefix = "gmd", namespaceURI = Namespaces.GMD)
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(CI_Citation.class),
    @XmlJavaTypeAdapter(RS_Identifier.class),

    // Java types, primitive types and basic OGC types handling
    @XmlJavaTypeAdapter(StringAdapter.class),
    @XmlJavaTypeAdapter(InternationalStringConverter.class)
})
package org.geotoolkit.referencing;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.apache.sis.xml.Namespaces;
import org.apache.sis.internal.jaxb.gco.*;
import org.apache.sis.internal.jaxb.metadata.*;
