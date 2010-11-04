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
 * <p>This interface maps Thoroughfare element.</p>
 *
 * <p>Specification of a thoroughfare. A thoroughfare could be a rd, street, canal, river, etc.
 * Note dependentlocality in a street. For example, in some countries, a large street will
 * have many subdivisions with numbers. Normally the subdivision name is the same as the road name,
 * but with a number to identifiy it. Eg. SOI SUKUMVIT 3, SUKUMVIT RD, BANGKOK.</p>
 *
 * <pre>
 * &lt;xs:element name="Thoroughfare">
 *  &lt;xs:complexType>
 *      &lt;xs:sequence>
 *          &lt;xs:element ref="AddressLine" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;xs:choice minOccurs="0" maxOccurs="unbounded">
 *                  &lt;xs:element ref="ThoroughfareNumber"/>
 *                  &lt;xs:element name="ThoroughfareNumberRange">...
 *                  &lt;/xs:element>
 *              &lt;/xs:choice>
 *          &lt;xs:element ref="ThoroughfareNumberPrefix" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element ref="ThoroughfareNumberSuffix" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;xs:element name="ThoroughfarePreDirection" type="ThoroughfarePreDirectionType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="ThoroughfareLeadingType" type="ThoroughfareLeadingTypeType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="ThoroughfareName" type="ThoroughfareNameType" minOccurs="0" maxOccurs="unbounded">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="ThoroughfareTrailingType" type="ThoroughfareTrailingTypeType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="ThoroughfarePostDirection" type="ThoroughfarePostDirectionType" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:element name="DependentThoroughfare" minOccurs="0">...
 *          &lt;/xs:element>
 *          &lt;xs:choice minOccurs="0">
 *              &lt;xs:element name="DependentLocality" type="DependentLocalityType">...
 *              &lt;/xs:element>
 *              &lt;xs:element ref="Premise"/>
 *              &lt;xs:element name="Firm" type="FirmType">...
 *              &lt;/xs:element>
 *              &lt;xs:element ref="PostalCode"/>
 *          &lt;/xs:choice>
 *          &lt;xs:any namespace="##other" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;/xs:sequence>
 *      &lt;xs:attribute name="Type"/>
 *      &lt;xs:attribute name="DependentThoroughfares">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="DependentThoroughfaresIndicator">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="DependentThoroughfaresConnector">...
 *      &lt;/xs:attribute>
 *      &lt;xs:attribute name="DependentThoroughfaresType">...
 *      &lt;/xs:attribute>
 *      &lt;xs:anyAttribute namespace="##other"/>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface Thoroughfare {

    /**
     *
     * @return
     */
    List<GenericTypedGrPostal> getAddressLines();

    /**
     *
     * @param addrressLines
     */
    void setAddressLines(List<GenericTypedGrPostal> addressLines);

    /**
     *
     * @return
     */
    List<Object> getThoroughfareNumbers();

    /**
     *
     * @param thoroughfareNumbers
     */
    void setThoroughfareNumbers(List<Object> thoroughfareNumbers);

    /**
     *
     * @return
     */
    List<ThoroughfareNumberPrefix> getThoroughfareNumberPrefixes();

    /**
     *
     * @param thoroughfareNumberPrefixes
     */
    void setThoroughfareNumberPrefixes(List<ThoroughfareNumberPrefix> thoroughfareNumberPrefixes);

    /**
     *
     * @return
     */
    List<ThoroughfareNumberSuffix> getThoroughfareNumberSuffixes();

    /**
     * 
     * @param throughfareNumberSuffixes
     */
    void setThoroughfareNumberSuffixes(List<ThoroughfareNumberSuffix> throughfareNumberSuffixes);

    /**
     *
     * @return
     */
    GenericTypedGrPostal getThoroughfarePreDirection();

    /**
     *
     * @param thoroughfarePreDirection
     */
    void setThoroughfarePreDirection(GenericTypedGrPostal thoroughfarePreDirection);

    /**
     *
     * @return
     */
    GenericTypedGrPostal getThoroughfareLeadingType();

    /**
     *
     * @param thoroughfareLeadingType
     */
    void setThoroughfareLeadingType(GenericTypedGrPostal thoroughfareLeadingType);

    /**
     * <p>Specification of the name of a Thoroughfare (also dependant street name): street name, canal name, etc.</p>
     * 
     * @return
     */
    List<GenericTypedGrPostal> getThoroughfareNames();

    /**
     * 
     * @param thoroughfareNames
     */
    void setThoroughfareNames(List<GenericTypedGrPostal> thoroughfareNames);

    /**
     *
     * @return
     */
    GenericTypedGrPostal getThoroughfareTrailingType();

    /**
     * 
     * @param thoroughfareTrailingType
     */
    void setThoroughfareTrailingType(GenericTypedGrPostal thoroughfareTrailingType);

    /**
     *
     * @return
     */
    GenericTypedGrPostal getThoroughfarePostDirection();

    /**
     *
     * @param thoroughfarePostDirection
     */
    void setThoroughfarePostDirection(GenericTypedGrPostal thoroughfarePostDirection);

    /**
     *
     * @return
     */
    DependentThoroughfare getDependentThoroughfare();

    /**
     *
     * @param dependentThoroughfare
     */
    void setDependentThoroughfare(DependentThoroughfare dependentThoroughfare);

    /**
     * === CHOICE: ===
     */

    /**
     *
     * @return
     */
    DependentLocality getDependentLocality();

    /**
     *
     * @param dependentLocality
     */
    void setDependentLocality(DependentLocality dependentLocality);

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

    /**
     *
     * @return
     */
    Firm getFirm();

    /**
     *
     * @param firm
     */
    void setFirm(Firm firm);

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

    /*
     * === END OF CHOICE ===
     *
     * === ATTRIBUTES: ===
     */

    /**
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
     * <p>Does this thoroughfare have a a dependent thoroughfare? Corner of street X, etc.</p>
     *
     * @return
     */
    DependentThoroughfares getDependentThoroughfares();

    /**
     *
     * @param dependentThoroughfares
     */
    void setDependentThoroughfares(DependentThoroughfares dependentThoroughfares);

    /**
     * <p>Corner of, Intersection of.</p>
     *
     * @return
     */
    String getDependentThoroughfaresIndicator();

    /**
     *
     * @param dependentThoroughfaresIndicator
     */
    void setDependentThoroughfaresIndicator(String dependentThoroughfaresIndicator);

    /**
     * <p>Corner of Street1 AND Street 2 where AND is the Connector.</p>
     *
     * @return
     */
    String getDependentThoroughfaresConnector();

    /**
     *
     * @param dependentThoroughfaresConnector
     */
    void setDependentThoroughfaresConnector(String dependentThoroughfaresConnector);
		
    /**
     * <p>TS in GEORGE and ADELAIDE STS, RDS IN A and B RDS, etc. Use only when both the street types are the same.</p>
     * 
     * @return
     */
    String getDependentThoroughfaresType();
	
    /**
     * 
     * @param dependentThoroughfaresType
     */
    void setDependentThoroughfaresType(String dependentThoroughfaresType);
}
