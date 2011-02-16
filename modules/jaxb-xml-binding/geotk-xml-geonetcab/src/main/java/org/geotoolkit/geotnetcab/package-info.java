/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */

@XmlSchema(namespace = "http://www.mdweb-project.org/files/xsd", elementFormDefault = XmlNsForm.QUALIFIED)
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(InternationalStringAdapter.class),
    @XmlJavaTypeAdapter(StringAdapter.class),
    @XmlJavaTypeAdapter(CI_OnlineResource.class),
    @XmlJavaTypeAdapter(CI_ResponsibleParty.class),
    @XmlJavaTypeAdapter(EX_GeographicExtent.class),
    @XmlJavaTypeAdapter(LI_Lineage.class),
    @XmlJavaTypeAdapter(ApplicationFieldAdapter.class),
    @XmlJavaTypeAdapter(DocumentTypeAdapter.class),
    @XmlJavaTypeAdapter(EOProductTypeAdapter.class),
    @XmlJavaTypeAdapter(OrganisationTypeAdapter.class),
    @XmlJavaTypeAdapter(RelationNameAdapter.class),
    @XmlJavaTypeAdapter(ServicesTypeAdapter.class),
    @XmlJavaTypeAdapter(SoftwareTypeAdapter.class),
    @XmlJavaTypeAdapter(ThematicTypeAdapter.class),
    @XmlJavaTypeAdapter(TrainingDurationAdapter.class),
    @XmlJavaTypeAdapter(TrainingTypeAdapter.class),

    @XmlJavaTypeAdapter(AccessAdapter.class),
    @XmlJavaTypeAdapter(AccessProgramAdapter.class),
    @XmlJavaTypeAdapter(AccessConstraintsAdapter.class),
    @XmlJavaTypeAdapter(DocumentAdapter.class),
    @XmlJavaTypeAdapter(EOProductAdapter.class),
    @XmlJavaTypeAdapter(MaterialResourceAdapter.class),
    @XmlJavaTypeAdapter(OrganisationAdapter.class),
    @XmlJavaTypeAdapter(ProductAdapter.class),
    @XmlJavaTypeAdapter(ReferenceAdapter.class),
    @XmlJavaTypeAdapter(RelationTypeAdapter.class),
    @XmlJavaTypeAdapter(ResourceAdapter.class),
    @XmlJavaTypeAdapter(ServiceAdapter.class),
    @XmlJavaTypeAdapter(SoftwareAdapter.class),
    @XmlJavaTypeAdapter(TrainingAdapter.class),
    @XmlJavaTypeAdapter(ResourceTypeAdapter.class),
    @XmlJavaTypeAdapter(DeliveryModeCodeAdapter.class),
    @XmlJavaTypeAdapter(UserRestrictionAdapter.class),
    @XmlJavaTypeAdapter(GO_URL.class),
    @XmlJavaTypeAdapter(GO_DateTime.class),

    // Primitive type handling
    @XmlJavaTypeAdapter(GO_Boolean.class), @XmlJavaTypeAdapter(type=boolean.class, value=GO_Boolean.class)
})
package org.geotoolkit.geotnetcab;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.geotoolkit.internal.jaxb.code.*;
import org.geotoolkit.internal.jaxb.metadata.*;
import org.geotoolkit.internal.jaxb.geonetcab.*;
import org.geotoolkit.internal.jaxb.gco.GO_Boolean;
import org.geotoolkit.internal.jaxb.gco.InternationalStringAdapter;
import org.geotoolkit.internal.jaxb.gco.StringAdapter;
import org.geotoolkit.internal.jaxb.gco.GO_DateTime;
import org.geotoolkit.internal.jaxb.gmd.GO_URL;
