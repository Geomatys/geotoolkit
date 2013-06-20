/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
 * {@linkplain org.geotoolkit.metadata.iso.content.AbstractContentInformation Content information}
 * implementation. An explanation for this package is provided in the
 * {@linkplain org.opengis.metadata.content OpenGIS&reg; javadoc}.
 * The remaining discussion on this page is specific to the Geotk implementation.
 *
 * {@section Overview}
 * For a global overview of metadata in Geotk, see the
 * <a href="{@docRoot}/../modules/metadata/index.html">Metadata page on the project web site</a>.
 *
 * {@section Bands in gridded data}
 * ISO 19115 defines a {@link org.opengis.metadata.content.Band} interface which
 * expresses the range of wavelengths in the electromagnetic spectrum. For the needs of
 * Image I/O, an additional interface has been defined with a subset of the {@code Band}
 * API and the restriction to electromagnetic spectrum removed. This interface is defined
 * in the {@code geotk-coverageio} module and is named
 * {@link org.geotoolkit.image.io.metadata.SampleDimension}.
 * Both {@code Band} and {@code SampleDimension} interfaces extend the same parent,
 * {@link org.opengis.metadata.content.RangeDimension}.
 *
 * {@section Parameterized types}
 * In GeoAPI interfaces, most collections are typed with wildcards, for example
 * {@code Collection<? extends Citation>}. The Geotk implementation removes the
 * wildcards and declares {@code Collection<Citation>} instead. This allows collections
 * to be <cite>live</cite>. Consequently it is possible to add new elements directly in
 * an existing collection using code like {@code getCitations().add(myCitation)} instead
 * than setting the collection as a whole with {@code setCitations(myCitations)}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.18
 *
 * @since 2.1
 * @module
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED, namespace = Namespaces.GMD, xmlns = {
    @XmlNs(prefix = "gmi", namespaceURI = Namespaces.GMI),
    @XmlNs(prefix = "gmd", namespaceURI = Namespaces.GMD),
    @XmlNs(prefix = "gco", namespaceURI = Namespaces.GCO),
    @XmlNs(prefix = "xsi", namespaceURI = Namespaces.XSI)
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(CI_Citation.class),
    @XmlJavaTypeAdapter(MD_CoverageContentTypeCode.class),
    @XmlJavaTypeAdapter(MD_Identifier.class),
    @XmlJavaTypeAdapter(MD_ImagingConditionCode.class),
    @XmlJavaTypeAdapter(MD_RangeDimension.class),
    @XmlJavaTypeAdapter(MI_BandDefinition.class),
    @XmlJavaTypeAdapter(MI_PolarizationOrientationCode.class),
    @XmlJavaTypeAdapter(MI_RangeElementDescription.class),
    @XmlJavaTypeAdapter(MI_TransferFunctionTypeCode.class),

    // Java types, primitive types and basic OGC types handling
    @XmlJavaTypeAdapter(UnitAdapter.class),
    @XmlJavaTypeAdapter(LocaleAdapter.class),
    @XmlJavaTypeAdapter(InternationalStringAdapter.class),
    @XmlJavaTypeAdapter(GO_GenericName.class),
    @XmlJavaTypeAdapter(GO_RecordType.class),
    @XmlJavaTypeAdapter(GO_Boolean.class),        @XmlJavaTypeAdapter(type=boolean.class, value=GO_Boolean.class),
    @XmlJavaTypeAdapter(GO_Decimal.class),        @XmlJavaTypeAdapter(type=double.class,  value=GO_Decimal.class),
    @XmlJavaTypeAdapter(GO_Integer.class),        @XmlJavaTypeAdapter(type=int.class,     value=GO_Integer.class),
    @XmlJavaTypeAdapter(GO_Integer.AsLong.class), @XmlJavaTypeAdapter(type=long.class,    value=GO_Integer.AsLong.class)
})
package org.geotoolkit.metadata.iso.content;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.gco.*;
import org.geotoolkit.internal.jaxb.gmd.*;
import org.apache.sis.internal.jaxb.code.*;
import org.geotoolkit.internal.jaxb.metadata.*;
