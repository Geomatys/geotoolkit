/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.inspire.xml.vs;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.service.ServiceType;

/**
 *
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "resourcelocator",
    "metadataUrl",
    "resourceType",
    "temporalRefererence",
    "conformity",
    "metadataPointOfContact",
    "metadataDate",
    "spatialDataService",
    "inpireKeywords",
    "languages",
    "currentLanguage"
})
public class ExtendedCapabilitiesType {

    @XmlElement(name="Resourcelocator", namespace="http://inspira.europa.eu/networkservice/view/1.0")
    private OnlineResource resourcelocator;

    @XmlElement(name="MetadataUrl", namespace="http://inspira.europa.eu/networkservice/view/1.0")
    private OnlineResource metadataUrl;

    @XmlElement(name="ResourceType", namespace="http://inspira.europa.eu/networkservice/view/1.0")
    private ScopeCode resourceType;

    @XmlElement(name="TemporalRefererence", namespace="http://inspira.europa.eu/networkservice/view/1.0")
    private Extent temporalRefererence;

    @XmlElement(name="Conformity", namespace="http://inspira.europa.eu/networkservice/view/1.0")
    private ConformanceResult conformity;

    @XmlElement(name="MetadataPointOfContact", namespace="http://inspira.europa.eu/networkservice/view/1.0")
    private ResponsibleParty metadataPointOfContact;

    @XmlElement(name="MetadataDate", namespace="http://inspira.europa.eu/networkservice/view/1.0")
    private Date metadataDate;

    @XmlElement(name="SpatialDataService", namespace="http://inspira.europa.eu/networkservice/view/1.0")
    private ServiceType spatialDataService;

    @XmlElement(name="InpireKeywords", namespace="http://inspira.europa.eu/networkservice/view/1.0")
    private Keywords inpireKeywords;

    @XmlElement(name="Languages", namespace="http://inspira.europa.eu/networkservice/view/1.0")
    private LanguagesType languages;
    
    @XmlElement(name="currentLanguage", namespace="http://inspira.europa.eu/networkservice/view/1.0")
    private String currentLanguage;

    /**
     * @return the resourcelocator
     */
    public OnlineResource getResourcelocator() {
        return resourcelocator;
    }

    /**
     * @param resourcelocator the resourcelocator to set
     */
    public void setResourcelocator(final OnlineResource resourcelocator) {
        this.resourcelocator = resourcelocator;
    }

    /**
     * @return the metadataUrl
     */
    public OnlineResource getMetadataUrl() {
        return metadataUrl;
    }

    /**
     * @param metadataUrl the metadataUrl to set
     */
    public void setMetadataUrl(final OnlineResource metadataUrl) {
        this.metadataUrl = metadataUrl;
    }

    /**
     * @return the resourceType
     */
    public ScopeCode getResourceType() {
        return resourceType;
    }

    /**
     * @param resourceType the resourceType to set
     */
    public void setResourceType(final ScopeCode resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * @return the temporalRefererence
     */
    public Extent getTemporalRefererence() {
        return temporalRefererence;
    }

    /**
     * @param temporalRefererence the temporalRefererence to set
     */
    public void setTemporalRefererence(final Extent temporalRefererence) {
        this.temporalRefererence = temporalRefererence;
    }

    /**
     * @return the conformity
     */
    public ConformanceResult getConformity() {
        return conformity;
    }

    /**
     * @param conformity the conformity to set
     */
    public void setConformity(final ConformanceResult conformity) {
        this.conformity = conformity;
    }

    /**
     * @return the metadataPointOfContact
     */
    public ResponsibleParty getMetadataPointOfContact() {
        return metadataPointOfContact;
    }

    /**
     * @param metadataPointOfContact the metadataPointOfContact to set
     */
    public void setMetadataPointOfContact(final ResponsibleParty metadataPointOfContact) {
        this.metadataPointOfContact = metadataPointOfContact;
    }

    /**
     * @return the metadataDate
     */
    public Date getMetadataDate() {
        return metadataDate;
    }

    /**
     * @param metadataDate the metadataDate to set
     */
    public void setMetadataDate(final Date metadataDate) {
        this.metadataDate = metadataDate;
    }

    /**
     * @return the spatialDataService
     */
    public ServiceType getSpatialDataService() {
        return spatialDataService;
    }

    /**
     * @param spatialDataService the spatialDataService to set
     */
    public void setSpatialDataService(final ServiceType spatialDataService) {
        this.spatialDataService = spatialDataService;
    }

    /**
     * @return the languages
     */
    public LanguagesType getLanguages() {
        return languages;
    }

    /**
     * @param languages the languages to set
     */
    public void setLanguages(final LanguagesType languages) {
        this.languages = languages;
    }

    /**
     * @return the currentLanguage
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * @param currentLanguage the currentLanguage to set
     */
    public void setCurrentLanguage(final String currentLanguage) {
        this.currentLanguage = currentLanguage;
    }

    /**
     * @return the inpireKeywords
     */
    public Keywords getInpireKeywords() {
        return inpireKeywords;
    }

    /**
     * @param inpireKeywords the inpireKeywords to set
     */
    public void setInpireKeywords(final Keywords inpireKeywords) {
        this.inpireKeywords = inpireKeywords;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        if (conformity!= null) {
            s.append("conformity:").append(conformity).append('\n');
        }
        if (currentLanguage != null) {
            s.append("currentLanguage:").append(currentLanguage).append('\n');
        }
        if (inpireKeywords != null) {
            s.append("inpireKeywords:").append(inpireKeywords).append('\n');
        }
        if (languages!= null) {
            s.append("languages:").append(languages).append('\n');
        }
        if (metadataDate != null) {
            s.append("metadataDate:").append(metadataDate).append('\n');
        }
        if (metadataPointOfContact != null) {
            s.append("metadataPointOfContact:").append(metadataPointOfContact).append('\n');
        }
        if (metadataUrl!= null) {
            s.append("metadataUrl:").append(metadataUrl).append('\n');
        }
        if (resourceType != null) {
            s.append("resourceType:").append(resourceType).append('\n');
        }
        if (resourcelocator != null) {
            s.append("resourcelocator:").append(resourcelocator).append('\n');
        }
        if (spatialDataService != null) {
            s.append("spatialDataService:").append(spatialDataService).append('\n');
        }
        if (temporalRefererence != null) {
            s.append("temporalRefererence:").append(temporalRefererence).append('\n');
        }

        return s.toString();
    }

    /**
     * Verifie si cette entree est identique a l'objet specifie.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ExtendedCapabilitiesType) {
            final ExtendedCapabilitiesType that = (ExtendedCapabilitiesType) object;

            return Utilities.equals(this.conformity, that.conformity) &&
                   Utilities.equals(this.currentLanguage, that.currentLanguage) &&
                   Utilities.equals(this.inpireKeywords, that.inpireKeywords) &&
                   Utilities.equals(this.languages, that.languages) &&
                   Utilities.equals(this.metadataDate, that.metadataDate) &&
                   Utilities.equals(this.metadataPointOfContact, that.metadataPointOfContact) &&
                   Utilities.equals(this.metadataUrl, that.metadataUrl) &&
                   Utilities.equals(this.resourcelocator, that.resourcelocator) &&
                   Utilities.equals(this.spatialDataService, that.spatialDataService) &&
                   Utilities.equals(this.temporalRefererence, that.temporalRefererence) &&
                   Utilities.equals(this.resourceType, that.resourceType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.resourcelocator != null ? this.resourcelocator.hashCode() : 0);
        hash = 89 * hash + (this.metadataUrl != null ? this.metadataUrl.hashCode() : 0);
        hash = 89 * hash + (this.resourceType != null ? this.resourceType.hashCode() : 0);
        hash = 89 * hash + (this.temporalRefererence != null ? this.temporalRefererence.hashCode() : 0);
        hash = 89 * hash + (this.conformity != null ? this.conformity.hashCode() : 0);
        hash = 89 * hash + (this.metadataPointOfContact != null ? this.metadataPointOfContact.hashCode() : 0);
        hash = 89 * hash + (this.metadataDate != null ? this.metadataDate.hashCode() : 0);
        hash = 89 * hash + (this.spatialDataService != null ? this.spatialDataService.hashCode() : 0);
        hash = 89 * hash + (this.inpireKeywords != null ? this.inpireKeywords.hashCode() : 0);
        hash = 89 * hash + (this.languages != null ? this.languages.hashCode() : 0);
        hash = 89 * hash + (this.currentLanguage != null ? this.currentLanguage.hashCode() : 0);
        return hash;
    }

}
