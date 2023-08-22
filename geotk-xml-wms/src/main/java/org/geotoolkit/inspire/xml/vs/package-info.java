/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
    @XmlJavaTypeAdapter(MD_Keywords.class),
    @XmlJavaTypeAdapter(CI_ResponsibleParty.class)
})
package org.geotoolkit.inspire.xml.vs;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.apache.sis.xml.bind.metadata.code.MD_ScopeCode;
import org.apache.sis.xml.bind.metadata.CI_Contact;
import org.apache.sis.xml.bind.metadata.EX_Extent;
import org.geotoolkit.inspire.xml.adapter.MD_Keywords;
import org.geotoolkit.inspire.xml.adapter.CI_OnlineResource;
import org.apache.sis.xml.bind.metadata.CI_ResponsibleParty;
import org.apache.sis.xml.bind.metadata.DQ_Result;

import org.apache.sis.xml.Namespaces;
import org.apache.sis.xml.bind.gco.GO_DateTime;
