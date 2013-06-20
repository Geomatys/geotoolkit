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
 * {@linkplain org.geotoolkit.metadata.iso.acquisition.DefaultAcquisitionInformation Acquisition} implementation. An explanation
 * for this package is provided in the {@linkplain org.opengis.metadata.acquisition OpenGIS&reg; javadoc}.
 * The remaining discussion on this page is specific to the Geotk implementation.
 *
 * {@section Overview}
 * For a global overview of metadata in Geotk, see the
 * <a href="{@docRoot}/../modules/metadata/index.html">Metadata page on the project web site</a>.
 *
 * {@section Parameterized types}
 * In GeoAPI interfaces, most collections are typed with wildcards, for example
 * {@code Collection<? extends Citation>}. The Geotk implementation removes the
 * wildcards and declares {@code Collection<Citation>} instead. This allows collections
 * to be <cite>live</cite>. Consequently it is possible to add new elements directly in
 * an existing collection using code like {@code getCitations().add(myCitation)} instead
 * than setting the collection as a whole with {@code setCitations(myCitations)}.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.03
 * @module
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED, namespace = Namespaces.GMI, xmlns = {
    @XmlNs(prefix = "gmi", namespaceURI = Namespaces.GMI),
    @XmlNs(prefix = "gmd", namespaceURI = Namespaces.GMD),
    @XmlNs(prefix = "gco", namespaceURI = Namespaces.GCO),
    @XmlNs(prefix = "xsi", namespaceURI = Namespaces.XSI)
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(CI_Citation.class),
    @XmlJavaTypeAdapter(CI_ResponsibleParty.class),
    @XmlJavaTypeAdapter(EX_Extent.class),
    @XmlJavaTypeAdapter(GM_Object.class),
    @XmlJavaTypeAdapter(MD_Identifier.class),
    @XmlJavaTypeAdapter(MD_ProgressCode.class),
    @XmlJavaTypeAdapter(MI_ContextCode.class),
    @XmlJavaTypeAdapter(MI_EnvironmentalRecord.class),
    @XmlJavaTypeAdapter(MI_Event.class),
    @XmlJavaTypeAdapter(MI_GeometryTypeCode.class),
    @XmlJavaTypeAdapter(MI_Instrument.class),
    @XmlJavaTypeAdapter(MI_Objective.class),
    @XmlJavaTypeAdapter(MI_ObjectiveTypeCode.class),
    @XmlJavaTypeAdapter(MI_Operation.class),
    @XmlJavaTypeAdapter(MI_OperationTypeCode.class),
    @XmlJavaTypeAdapter(MI_Plan.class),
    @XmlJavaTypeAdapter(MI_Platform.class),
    @XmlJavaTypeAdapter(MI_PlatformPass.class),
    @XmlJavaTypeAdapter(MI_PriorityCode.class),
    @XmlJavaTypeAdapter(MI_RequestedDate.class),
    @XmlJavaTypeAdapter(MI_Requirement.class),
    @XmlJavaTypeAdapter(MI_SequenceCode.class),
    @XmlJavaTypeAdapter(MI_TriggerCode.class),

    // Java types, primitive types and basic OGC types handling
    @XmlJavaTypeAdapter(GO_DateTime.class),
    @XmlJavaTypeAdapter(StringAdapter.class),
    @XmlJavaTypeAdapter(InternationalStringAdapter.class)
})
package org.geotoolkit.metadata.iso.acquisition;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.gco.*;
import org.apache.sis.internal.jaxb.code.*;
import org.geotoolkit.internal.jaxb.metadata.*;
import org.geotoolkit.internal.jaxb.geometry.GM_Object;
