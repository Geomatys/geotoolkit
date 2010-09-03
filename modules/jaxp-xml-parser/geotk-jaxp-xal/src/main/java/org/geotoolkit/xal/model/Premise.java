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
package org.geotoolkit.xal.model;

import java.util.List;

/**
 * <p>This interface maps Premise element.</p>
 *
 * <p>Specification of a single premise, for example a house or a building.
 * The premise as a whole has a unique premise (house) number or a premise name.
 * There could be more than one premise in a street referenced in an address.
 * For example a building address near a major shopping centre or raiwlay station</p>
 *
 * <pre>
 * &lt;xs:element name="Premise">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="PremiseName" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:choice minOccurs="0">
 *              &lt;xs:element name="PremiseLocation">...
 *              &lt;/xs:element>
 *              &lt;xs:choice>
 *                  &lt;xs:element ref="PremiseNumber" maxOccurs="unbounded"/>
 *                  &lt;xs:element name="PremiseNumberRange">...
 *                  &lt;/xs:element>
 *              &lt;/xs:choice>
 *          &lt;/xs:choice>
 *          &lt;xs:element ref="PremiseNumberPrefix" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element ref="PremiseNumberSuffix" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="BuildingName" type="BuildingNameType" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:choice>
 *              &lt;xs:element name="SubPremise" type="SubPremiseType" minOccurs="0" maxOccurs="unbounded">...
 *              &lt;/xs:element>
 *              &lt;xs:element name="Firm" type="FirmType" minOccurs="0">...
 *              &lt;/xs:element>
 *          &lt;/xs:choice>
 *          &lt;xs:element name="MailStop" type="MailStopType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element ref="PostalCode" minOccurs="0"/>
 *          &lt;xs:element ref="Premise" minOccurs="0"/>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="PremiseDependency">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="PremiseDependencyType">
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="PremiseThoroughfareConnector">...
 *      &lt;/xs:attribute>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Premise {

    /**
     *
     * @return
     */
    List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @param addressLines
     */
    void setAddressLines(List<GenericTypedGrPostal> addressLines);

    /**
     *
     * @return
     */
    List<PremiseName> getPremiseNames();

    /**
     *
     * @param premiseNames
     */
    void setPremiseNames(List<PremiseName> premiseNames);

    /*
     * === CHOIX: ===
     */

    /**
     *
     * @return
     */
    PremiseLocation getPremiseLocation();

    /**
     *
     * @param premiseLocation
     */
    void setPremiseLocation(PremiseLocation premiseLocation);

    /**
     *
     * @return
     */
    List<PremiseNumber> getPremiseNumbers();

    /**
     *
     * @param premiseNumbers
     */
    void setPremiseNumbers(List<PremiseNumber> premiseNumbers);

    /**
     *
     * @return
     */
    PremiseNumberRange getPremiseNumberRange();

    /**
     *
     * @param premiseNumberRange
     */
    void setPremiseNumberRange(PremiseNumberRange premiseNumberRange);

    /*
     * === END OF CHOICE ===
     */

    /**
     *
     * @return
     */
    List<PremiseNumberPrefix> getPremiseNumberPrefixes();

    /**
     *
     * @param premiseNumberPrefixes
     */
    void setPremiseNumberPrefixes(List<PremiseNumberPrefix> premiseNumberPrefixes);

    /**
     *
     * @return
     */
    List<PremiseNumberSuffix> getPremiseNumberSuffixes();

    /**
     *
     * @param premiseNumberSuffixes
     */
    void setPremiseNumberSuffixes(List<PremiseNumberSuffix> premiseNumberSuffixes);
    
    /**
     * <p>Specification of the name of a building.</p>
     * 
     * @return
     */
    List<BuildingName> getBuildingNames();

    /**
     * 
     * @param buildingNames
     */
    void setBuildingNames(List<BuildingName> buildingNames);

    /*
     * === CHOICE : ===
     */

    /**
     * <p>Specification of a single sub-premise.
     * Examples of sub-premises are apartments and suites.
     * Each sub-premise should be uniquely identifiable.</p>
     *
     * @return
     */
    List<SubPremise> getSubPremises();

    /**
     *
     * @param subPremises
     */
    void setSubPremises(List<SubPremise> subPremises);

    /**
     * <p>Specification of a firm, company, organization, etc.
     * It can be specified as part of an address that contains a street or a postbox.
     * It is therefore different from a large mail user address,
     * which contains no street.</p>
     *
     * @return
     */
    Firm getFirm();

    /**
     *
     * @param firm
     */
    void setFirm(Firm firm);

    /*
     * === END OF CHOICE ===
     */

    /**
     * <p>A MailStop is where the the mail is delivered to within a premise/subpremise/firm or a facility.</p>
     *
     * @return
     */
    MailStop getMailStop();

    /**
     *
     * @param mailStop
     */
    void setMailStop(MailStop mailStop);

    /**
     *
     * @return
     */
    PostalCode getPostalCode();

    /**
     *
     * @param postalCode
     */
    void setPostalCode(PostalCode postalCode);

    /**
     *
     * @return
     */
    Premise getPremise();

    /**
     *
     * @param premise
     */
    void setPremise(Premise premise);

    /*
     * === ATTRIBUTES ===
     */

    /**
     * <p>COMPLEXE in COMPLEX DES JARDINS, A building, station, etc.</p>
     *
     * @return
     */
    String getType();

    /**
     *
     * @param type
     */
    void setType(String type);

    /**
     * <p>STREET, PREMISE, SUBPREMISE, PARK, FARM, etc.</p>
     *
     * @return
     */
    String getPremiseDependency();

    /**
     *
     * @param premiseDependency
     */
    void setPremiseDependency(String premiseDependency);

    /**
     * <p>NEAR, ADJACENT TO, etc.</p>
     *
     * @return
     */
    String getPremiseDependencyType();

    /**
     *
     * @param premiseDpendencyType
     */
    void setPremiseDependencyType(String premiseDependencyType);

    /**
     * <p>DES, DE, LA, LA, DU in RUE DU BOIS.
     * These terms connect a premise/thoroughfare type and premise/thoroughfare name.
     * Terms may appear with names AVE DU BOIS</p>
     *
     * @return
     */
    String getPremiseThoroughfareConnector();

    /**
     * 
     * @param premiseThoroughfarConnctor
     */
    void setPremiseThoroughfareConnector(String premiseThoroughfareConnector);
}
