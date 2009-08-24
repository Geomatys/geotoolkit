/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
 * {@linkplain org.geotoolkit.metadata.iso.distribution.DefaultDistribution Distribution}
 * implementation. An explanation for this package is provided in the
 * {@linkplain org.opengis.metadata.distribution OpenGIS&reg; javadoc}.
 * The remaining discussion on this page is specific to the Geotk implementation.
 * <p>
 * In GeoAPI interfaces, most collections are typed with wildcards, for example
 * {@code Collection<? extends Citation>}. The Geotk implementation removes the
 * wildcards and declares {@code Collection<Citation>} instead. This allows collections
 * to be <cite>live</cite>. Consequently it is possible to add new elements directly in
 * an existing collection using code like {@code getCitations().add(myCitation)} instead
 * than setting the collection as a whole with {@code setCitations(myCitations)}.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED, namespace = Namespaces.GMD, xmlns = {
    @XmlNs(prefix = "gmd", namespaceURI = Namespaces.GMD),
    @XmlNs(prefix = "gco", namespaceURI = Namespaces.GCO),
    @XmlNs(prefix = "xsi", namespaceURI = Namespaces.XSI)
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(AnchoredInternationalStringAdapter.class),
    @XmlJavaTypeAdapter(OnLineResourceAdapter.class),
    @XmlJavaTypeAdapter(MediumAdapter.class),
    @XmlJavaTypeAdapter(FormatAdapter.class),
    @XmlJavaTypeAdapter(DistributorAdapter.class),
    @XmlJavaTypeAdapter(DigitalTransferOptionsAdapter.class),
    @XmlJavaTypeAdapter(StandardOrderProcessAdapter.class),
    @XmlJavaTypeAdapter(MediumNameAdapter.class),
    @XmlJavaTypeAdapter(UnitAdapter.class),
    @XmlJavaTypeAdapter(ResponsiblePartyAdapter.class),
    @XmlJavaTypeAdapter(MediumFormatAdapter.class),
    @XmlJavaTypeAdapter(DateAdapter.class),

    // Primitive type handling
    @XmlJavaTypeAdapter(DoubleAdapter.class),  @XmlJavaTypeAdapter(type=double.class,  value=DoubleAdapter.class),
    @XmlJavaTypeAdapter(IntegerAdapter.class), @XmlJavaTypeAdapter(type=int.class,     value=IntegerAdapter.class)
})
package org.geotoolkit.metadata.iso.distribution;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.metadata.*;
import org.geotoolkit.internal.jaxb.uom.DateAdapter;
import org.geotoolkit.internal.jaxb.uom.UnitAdapter;
import org.geotoolkit.internal.jaxb.code.MediumNameAdapter;
import org.geotoolkit.internal.jaxb.code.MediumFormatAdapter;
import org.geotoolkit.internal.jaxb.primitive.DoubleAdapter;
import org.geotoolkit.internal.jaxb.primitive.IntegerAdapter;
import org.geotoolkit.internal.jaxb.text.AnchoredInternationalStringAdapter;
