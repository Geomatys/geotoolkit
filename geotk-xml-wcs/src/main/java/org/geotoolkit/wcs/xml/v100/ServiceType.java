/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.wcs.xml.v100;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.CodeListType;
import org.geotoolkit.ows.xml.AbstractAdditionalParameters;
import org.geotoolkit.ows.xml.AbstractCodeType;
import org.geotoolkit.ows.xml.AbstractMetadata;
import org.geotoolkit.ows.xml.AbstractOwsContextDescription;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.v110.CodeType;


/**
 *
 * A minimal, human readable rescription of the service.
 *
 * WCS version 1.0.0
 *
 * <p>Java class for ServiceType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ServiceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wcs}AbstractDescriptionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs}keywords" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="responsibleParty" type="{http://www.opengis.net/wcs}ResponsiblePartyType" minOccurs="0"/>
 *         &lt;element name="fees" type="{http://www.opengis.net/gml}CodeListType"/>
 *         &lt;element name="accessConstraints" type="{http://www.opengis.net/gml}CodeListType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *       &lt;attribute name="updateSequence" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceType")
public class ServiceType extends AbstractDescriptionType implements AbstractServiceIdentification {

    @XmlAttribute
    private String version;
    @XmlAttribute
    private String updateSequence;

    private Keywords keywords;

    private ResponsiblePartyType responsibleParty;

    private CodeListType fees;

    private CodeListType accessConstraints;


    /**
     * An empty constructor used by JAXB
     */
    ServiceType(){
    }

    /**
     * Build a new Service Type, without version specified.
     */
    public ServiceType(final List<MetadataLinkType> metadataLink, final String name, final String label, final String description,
            final Keywords keywords, final ResponsiblePartyType responsibleParty, final CodeListType fees, final CodeListType accessConstraints,
            final String updateSequence)
    {
        this(metadataLink, name, label, description, null, keywords, responsibleParty, fees, accessConstraints, updateSequence);
    }

    /**
     * Build a new Service Type
     */
    public ServiceType(final List<MetadataLinkType> metadataLink, final String name, final String label, final String description, final String version,
            final Keywords keywords, final ResponsiblePartyType responsibleParty, final CodeListType fees, final CodeListType accessConstraints,
            final String updateSequence)
    {
        super(metadataLink, name, label, description);
        this.version           = version;
        this.keywords          = keywords;
        this.responsibleParty  = responsibleParty;
        this.fees              = fees;
        this.accessConstraints = accessConstraints;
        this.updateSequence    = updateSequence;

    }

    /**
     * Gets the value of the version property.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the value of the keywords property.
     *
     */
    @Override
    public List<Keywords> getKeywords() {
        if (keywords != null) {
            return Arrays.asList(keywords);
        }
        return null;
    }

    /**
     * Gets the value of the updateSequence property.
     *
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * Gets the value of the responsibleParty property.
     *
     */
    public ResponsiblePartyType getResponsibleParty() {
        return responsibleParty;
    }

    /**
     * Gets the value of the fees property.
     *
     */
    public CodeListType getFullFees() {
        return fees;
    }

    @Override
    public String getFees() {
        if (fees != null && !fees.getValue().isEmpty()) {
            return fees.getValue().get(0);
        }
        return null;
    }

    /**
     * Gets the value of the acces constraint property.
     *
     */
    public CodeListType getFullAccessConstraints() {
        return accessConstraints;
    }

    @Override
    public List<String> getAccessConstraints() {
        if (accessConstraints != null) {
            return accessConstraints.getValue();
        }
        return null;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    @Override
    public List<String> getServiceTypeVersion() {
        return Arrays.asList(version);
    }

    @Override
    public AbstractCodeType getServiceType() {
        return new CodeType("WCS");
    }

    @Override
    public List<String> getProfile() {
        return new ArrayList<>();
    }

    @Override
    public void setProfile(List<String> profiles) {
        // do nothing
    }

    @Override
    public String getFirstTitle() {
        return super.getName();
    }

    @Override
    public String getFirstAbstract() {
        return super.getDescription();
    }

    @Override
    public List<? extends AbstractAdditionalParameters> getAdditionalParameters() {
        return new ArrayList<>();
    }

    /**
     * not implemented
     *
     * @return
     */
    @Override
    public AbstractOwsContextDescription getOwsContext() {
        return null;
    }
}
