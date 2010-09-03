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
 * <p>This interface maps SubpremiseType type.</p>
 *
 * <pre>
 * &lt;xs:complexType name="SubPremiseType">
 *  &lt;xs:sequence>
 *      &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;xs:element name="SubPremiseName" minOccurs="0" maxOccurs="unbounded">...
 *      &lt;/xs:element>
 *      &lt;xs:choice minOccurs="0">
 *          &lt;xs:element name="SubPremiseLocation">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="SubPremiseNumber" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *      &lt;/xs:choice>
 *      &lt;xs:element name="SubPremiseNumberPrefix" minOccurs="0" maxOccurs="unbounded">...
 *      &lt;/xs:element>
 *      &lt;xs:element name="SubPremiseNumberSuffix" minOccurs="0" maxOccurs="unbounded">...
 *      &lt;/xs:element>
 *      &lt;xs:element name="BuildingName" type="BuildingNameType" minOccurs="0" maxOccurs="unbounded">...
 *      &lt;/xs:element>
 *      &lt;xs:element name="Firm" type="FirmType" minOccurs="0">...
 *      &lt;/xs:element>
 *      &lt;xs:element name="MailStop" type="MailStopType" minOccurs="0">...
 *      &lt;/xs:element>
 *      &lt;xs:element ref="PostalCode" minOccurs="0"/>
 *      &lt;xs:element name="SubPremise" type="SubPremiseType" minOccurs="0">...
 *      &lt;/xs:element>
 *      &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/xs:sequence>
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface SubPremise {

    /**
     * 
     * @return
     */
    List<GenericTypedGrPostal> getAddressLines();

    /**
     * <p>Name of the SubPremise.</p>
     * @return
     */
    List<SubPremiseName> getSubPremiseNames();

    /*
     * === CHOICE: ===
     */

    SubPremiseLocation getSubPremiseLocation();

    /**
     * 
     * @return
     */
    List<SubPremiseNumber> getSubPremiseNumbers();

    /*
     * === END OF CHOICE ===
     */

    /**
     * <p>Prefix of the sub premise number. eg. A in A-12.</p>
     *
     * @return
     */
    List<SubPremiseNumberPrefix> getSubPremiseNumberPrefixes();

    /**
     * <p>Suffix of the sub premise number. eg. A in 12A.</p>
     *
     * @return
     */
    List<SubPremiseNumberSuffix> getSubPremiseNumberSuffixes();

    /**
     * <p>Name of the building.</p>
     *
     * @return
     */
    List<BuildingName> getBuildingNames();

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
     * <p>A MailStop is where the the mail is delivered to within a premise/subpremise/firm or a facility.</p>
     *
     * @return
     */
    MailStop getMailStop();

    /**
     *
     * @return
     */
    PostalCode getPostalCode();

    /**
     * <p>Specification of a single sub-premise.
     * Examples of sub-premises are apartments and suites.
     * Each sub-premise should be uniquely identifiable.
     * SubPremiseType: Specification of the name of a sub-premise type.
     * Possible values not limited to: Suite, Appartment, Floor,
     * Unknown Multiple levels within a premise by recursively
     * calling SubPremise Eg. Level 4, Suite 2, Block C</p>
     *
     * @return
     */
    SubPremise getSubPremise();

    /**
     * 
     * @return
     */
    String getType();

    /**
     *
     * @param addressLines
     */
    void setAddressLines(List<GenericTypedGrPostal> addressLines);

    /**
     *
     * @param premiseNames
     */
    void setSubPremiseNames(List<SubPremiseName> subPremiseNames);

    /*
     * === CHOICE: ===
     */

    /**
     *
     * @param subPremiseLocation
     */
    void setSubPremiseLocation(SubPremiseLocation subPremiseLocation);

    /**
     *
     * @param subPremiseNumbers
     */
    void setSubPremiseNumbers(List<SubPremiseNumber> subPremiseNumbers);

    /*
     * === END OF CHOICE ===
     */

    /**
     *
     * @param subPremiseNumberPrefixes
     */
    void setSubPremiseNumberPrefixes(List<SubPremiseNumberPrefix> subPremiseNumberPrefixes);

    /**
     *
     * @param subPremiseNumberSuffixes
     */
    void setSubPremiseNumberSuffixes(List<SubPremiseNumberSuffix> subPremiseNumberSuffixes);

    /**
     *
     * @param buildingNames
     */
    void setBuildingNames(List<BuildingName> buildingNames);

    /**
     *
     * @param firm
     */
    void setFirm(Firm firm);

    /**
     *
     * @param mailStop
     */
    void setMailStop(MailStop mailStop);

    /**
     *
     * @param postalCode
     */
    void setPostalCode(PostalCode postalCode);

    /**
     *
     * @param subPremise
     */
    void setSubPremise(SubPremise subPremise);

    /**
     * 
     * @param type
     */
    void setType(String type);
}
