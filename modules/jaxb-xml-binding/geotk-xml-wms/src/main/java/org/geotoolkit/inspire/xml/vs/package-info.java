/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
 * @author Guilhem Legal
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED, namespace = "http://inspira.europa.eu/networkservice/view/1.0", xmlns = {
    @XmlNs(prefix = "gmd", namespaceURI = Namespaces.GMD),
    @XmlNs(prefix = "gco", namespaceURI = Namespaces.GCO),
    @XmlNs(prefix = "xsi", namespaceURI = Namespaces.XSI)
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(CI_Contact.class),
    @XmlJavaTypeAdapter(GO_DateTime.class),
    @XmlJavaTypeAdapter(MD_ScopeCode.class),
    @XmlJavaTypeAdapter(EX_Extent.class),
    @XmlJavaTypeAdapter(DQ_Result.class),
    @XmlJavaTypeAdapter(CI_OnlineResource.class),
    @XmlJavaTypeAdapter(ServiceTypeAdapter.class),
    @XmlJavaTypeAdapter(MD_Keywords.class),
    @XmlJavaTypeAdapter(CI_ResponsibleParty.class)
})
package org.geotoolkit.inspire.xml.vs;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.geotoolkit.internal.jaxb.code.MD_ScopeCode;
import org.geotoolkit.internal.jaxb.metadata.CI_Contact;
import org.geotoolkit.internal.jaxb.metadata.EX_Extent;
import org.geotoolkit.internal.jaxb.metadata.direct.MD_Keywords;
import org.geotoolkit.internal.jaxb.metadata.direct.CI_OnlineResource;
import org.geotoolkit.internal.jaxb.metadata.CI_ResponsibleParty;
import org.geotoolkit.internal.jaxb.metadata.DQ_Result;

import org.geotoolkit.xml.Namespaces;
import org.geotoolkit.internal.jaxb.gco.GO_DateTime;
import org.geotoolkit.resources.jaxb.service.ServiceTypeAdapter;

