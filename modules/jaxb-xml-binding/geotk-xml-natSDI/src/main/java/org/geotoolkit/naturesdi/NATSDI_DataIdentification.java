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

package org.geotoolkit.naturesdi;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.jaxb.naturesdi.SpeciesInformationAdapter;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="NATSDI_DataIdentification")
public class NATSDI_DataIdentification extends DefaultDataIdentification implements org.opengis.metadata.naturesdi.NATSDI_DataIdentification {

    @XmlJavaTypeAdapter(SpeciesInformationAdapter.class)
    private List<NATSDI_SpeciesInformation> speciesInformation;

    /**
     * @return the speciesInformation
     */
    public synchronized List<NATSDI_SpeciesInformation> getSpeciesInformation() {
        return speciesInformation = nonNullList(speciesInformation, NATSDI_SpeciesInformation.class);
    }

    /**
     * @param speciesInformation the speciesInformation to set
     */
    public synchronized void setSpeciesInformation(final List<NATSDI_SpeciesInformation> newValues) {
        speciesInformation = copyList(newValues, speciesInformation, NATSDI_SpeciesInformation.class);
    }

}
