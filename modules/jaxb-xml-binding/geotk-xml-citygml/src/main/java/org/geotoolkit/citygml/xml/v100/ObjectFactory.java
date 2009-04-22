/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.citygml.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.v311.FeaturePropertyType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.citygml._1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GenericApplicationPropertyOfSite_QNAME = new QName("http://www.opengis.net/citygml/1.0", "_GenericApplicationPropertyOfSite");
    private final static QName _CityObjectMember_QNAME = new QName("http://www.opengis.net/citygml/1.0", "cityObjectMember");
    private final static QName _Site_QNAME = new QName("http://www.opengis.net/citygml/1.0", "_Site");
    private final static QName _GenericApplicationPropertyOfAddress_QNAME = new QName("http://www.opengis.net/citygml/1.0", "_GenericApplicationPropertyOfAddress");
    private final static QName _CityModel_QNAME = new QName("http://www.opengis.net/citygml/1.0", "CityModel");
    private final static QName _ImplicitGeometry_QNAME = new QName("http://www.opengis.net/citygml/1.0", "ImplicitGeometry");
    private final static QName _CityObject_QNAME = new QName("http://www.opengis.net/citygml/1.0", "_CityObject");
    private final static QName _GenericApplicationPropertyOfCityObject_QNAME = new QName("http://www.opengis.net/citygml/1.0", "_GenericApplicationPropertyOfCityObject");
    private final static QName _Address_QNAME = new QName("http://www.opengis.net/citygml/1.0", "Address");
    private final static QName _GenericApplicationPropertyOfCityModel_QNAME = new QName("http://www.opengis.net/citygml/1.0", "_GenericApplicationPropertyOfCityModel");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.citygml._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExternalReferenceType }
     * 
     */
    public ExternalReferenceType createExternalReferenceType() {
        return new ExternalReferenceType();
    }

    /**
     * Create an instance of {@link ExternalObjectReferenceType }
     * 
     */
    public ExternalObjectReferenceType createExternalObjectReferenceType() {
        return new ExternalObjectReferenceType();
    }

    /**
     * Create an instance of {@link ImplicitRepresentationPropertyType }
     * 
     */
    public ImplicitRepresentationPropertyType createImplicitRepresentationPropertyType() {
        return new ImplicitRepresentationPropertyType();
    }

    /**
     * Create an instance of {@link AddressPropertyType }
     * 
     */
    public AddressPropertyType createAddressPropertyType() {
        return new AddressPropertyType();
    }

    /**
     * Create an instance of {@link ImplicitGeometryType }
     * 
     */
    public ImplicitGeometryType createImplicitGeometryType() {
        return new ImplicitGeometryType();
    }

    /**
     * Create an instance of {@link CityModelType }
     * 
     */
    public CityModelType createCityModelType() {
        return new CityModelType();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link GeneralizationRelationType }
     * 
     */
    public GeneralizationRelationType createGeneralizationRelationType() {
        return new GeneralizationRelationType();
    }

    /**
     * Create an instance of {@link XalAddressPropertyType }
     * 
     */
    public XalAddressPropertyType createXalAddressPropertyType() {
        return new XalAddressPropertyType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/1.0", name = "_GenericApplicationPropertyOfSite")
    public JAXBElement<Object> createGenericApplicationPropertyOfSite(Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfSite_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeaturePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/1.0", name = "cityObjectMember", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "featureMember")
    public JAXBElement<FeaturePropertyType> createCityObjectMember(FeaturePropertyType value) {
        return new JAXBElement<FeaturePropertyType>(_CityObjectMember_QNAME, FeaturePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractSiteType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/1.0", name = "_Site", substitutionHeadNamespace = "http://www.opengis.net/citygml/1.0", substitutionHeadName = "_CityObject")
    public JAXBElement<AbstractSiteType> createSite(AbstractSiteType value) {
        return new JAXBElement<AbstractSiteType>(_Site_QNAME, AbstractSiteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/1.0", name = "_GenericApplicationPropertyOfAddress")
    public JAXBElement<Object> createGenericApplicationPropertyOfAddress(Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfAddress_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CityModelType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/1.0", name = "CityModel", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeatureCollection")
    public JAXBElement<CityModelType> createCityModel(CityModelType value) {
        return new JAXBElement<CityModelType>(_CityModel_QNAME, CityModelType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImplicitGeometryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/1.0", name = "ImplicitGeometry", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractGML")
    public JAXBElement<ImplicitGeometryType> createImplicitGeometry(ImplicitGeometryType value) {
        return new JAXBElement<ImplicitGeometryType>(_ImplicitGeometry_QNAME, ImplicitGeometryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractCityObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/1.0", name = "_CityObject", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<AbstractCityObjectType> createCityObject(AbstractCityObjectType value) {
        return new JAXBElement<AbstractCityObjectType>(_CityObject_QNAME, AbstractCityObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/1.0", name = "_GenericApplicationPropertyOfCityObject")
    public JAXBElement<Object> createGenericApplicationPropertyOfCityObject(Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfCityObject_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddressType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/1.0", name = "Address", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<AddressType> createAddress(AddressType value) {
        return new JAXBElement<AddressType>(_Address_QNAME, AddressType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/citygml/1.0", name = "_GenericApplicationPropertyOfCityModel")
    public JAXBElement<Object> createGenericApplicationPropertyOfCityModel(Object value) {
        return new JAXBElement<Object>(_GenericApplicationPropertyOfCityModel_QNAME, Object.class, null, value);
    }

}
