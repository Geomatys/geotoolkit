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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.opengis.metadata.citation.Citation;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NATSDI_SpeciesInformation implements org.opengis.metadata.naturesdi.NATSDI_SpeciesInformation {

    private NATSDI_TaxonomicClassification taxonomicClassification;

    private Citation classificationSystemAuthority;

    private Citation authorCitation;

    private String speciesVernacularName;

    public NATSDI_SpeciesInformation() {

    }

    public NATSDI_SpeciesInformation(NATSDI_TaxonomicClassification taxonomicClassification, Citation classificationSystemAuthority,
            Citation authorCitation, String speciesVernacularName) {
        this.authorCitation = authorCitation;
        this.classificationSystemAuthority = classificationSystemAuthority;
        this.speciesVernacularName = speciesVernacularName;
        this.taxonomicClassification = taxonomicClassification;
    }

    /**
     * @return the taxonomicClassification
     */
    public NATSDI_TaxonomicClassification getTaxonomicClassification() {
        return taxonomicClassification;
    }

    /**
     * @param taxonomicClassification the taxonomicClassification to set
     */
    public void setTaxonomicClassification(NATSDI_TaxonomicClassification taxonomicClassification) {
        this.taxonomicClassification = taxonomicClassification;
    }

    /**
     * @return the classificationSystemAuthority
     */
    public Citation getClassificationSystemAuthority() {
        return classificationSystemAuthority;
    }

    /**
     * @param classificationSystemAuthority the classificationSystemAuthority to set
     */
    public void setClassificationSystemAuthority(Citation classificationSystemAuthority) {
        this.classificationSystemAuthority = classificationSystemAuthority;
    }

    /**
     * @return the authorCitation
     */
    public Citation getAuthorCitation() {
        return authorCitation;
    }

    /**
     * @param authorCitation the authorCitation to set
     */
    public void setAuthorCitation(Citation authorCitation) {
        this.authorCitation = authorCitation;
    }

    /**
     * @return the speciesVernacularName
     */
    public String getSpeciesVernacularName() {
        return speciesVernacularName;
    }

    /**
     * @param speciesVernacularName the speciesVernacularName to set
     */
    public void setSpeciesVernacularName(String speciesVernacularName) {
        this.speciesVernacularName = speciesVernacularName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[NATSDI_SpeciesInformation]\n");
        if (speciesVernacularName != null) {
            sb.append("speciesVernacularName").append(speciesVernacularName).append('\n');
        }
        if (authorCitation != null) {
            sb.append("authorCitation").append(authorCitation).append('\n');
        }
        if (classificationSystemAuthority != null) {
            sb.append("classificationSystemAuthority").append(classificationSystemAuthority).append('\n');
        }
        if (taxonomicClassification != null) {
            sb.append("taxonomicClassification").append(taxonomicClassification).append('\n');
        }
        return sb.toString();
    }
}
