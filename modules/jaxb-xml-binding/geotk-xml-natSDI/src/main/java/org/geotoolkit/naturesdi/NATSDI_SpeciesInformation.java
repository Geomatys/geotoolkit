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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.opengis.metadata.citation.Citation;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NATSDI_SpeciesInformation implements org.opengis.metadata.naturesdi.NATSDI_SpeciesInformation {

    private NATSDI_TaxonomicClassification taxonomicClassification;

    private List<DefaultCitation> classificationSystemAuthority;

    private List<DefaultCitation> authorCitation;

    private String speciesVernacularName;

    public NATSDI_SpeciesInformation() {

    }

    public NATSDI_SpeciesInformation(NATSDI_TaxonomicClassification taxonomicClassification, List<DefaultCitation> classificationSystemAuthority,
            List<DefaultCitation> authorCitation, String speciesVernacularName) {
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
    public List<? extends Citation> getClassificationSystemAuthority() {
        return classificationSystemAuthority;
    }

    /**
     * @param classificationSystemAuthority the classificationSystemAuthority to set
     */
    public void setClassificationSystemAuthority(List<? extends Citation> classificationSystemAuthority) {
        this.classificationSystemAuthority = (List<DefaultCitation>) classificationSystemAuthority;
    }

    /**
     * @param classificationSystemAuthority the classificationSystemAuthority to set
     */
    public void setClassificationSystemAuthority(DefaultCitation classificationSystemAuthority) {
        if (this.classificationSystemAuthority == null) {
            this.classificationSystemAuthority = new ArrayList<DefaultCitation>();
        }
        this.classificationSystemAuthority.add(classificationSystemAuthority);
    }

    /**
     * @return the authorCitation
     */
    public List<? extends Citation> getAuthorCitation() {
        return authorCitation;
    }

    /**
     * @param authorCitation the authorCitation to set
     */
    public void setAuthorCitation(List<? extends Citation> authorCitation) {
        this.authorCitation = (List<DefaultCitation>) authorCitation;
    }

    /**
     * @param authorCitation the authorCitation to set
     */
    public void setAuthorCitation(DefaultCitation authorCitation) {
        if (this.authorCitation == null) {
            this.authorCitation = new ArrayList<DefaultCitation>();
        }
        this.authorCitation.add(authorCitation);
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
