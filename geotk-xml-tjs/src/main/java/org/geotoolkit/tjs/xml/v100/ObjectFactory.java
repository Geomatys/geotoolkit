/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.tjs.xml.v100;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element
 * interface generated in the org.geotoolkit.tjs.xml.v100 package.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetCapabilities_QNAME = new QName("http://www.opengis.net/tjs/1.0", "GetCapabilities");
    private final static QName _Capabilities_QNAME = new QName("http://www.opengis.net/tjs/1.0", "Capabilities");
    private final static QName _DescribeFrameworks_QNAME = new QName("http://www.opengis.net/tjs/1.0", "DescribeFrameworks");
    private final static QName _FrameworkDescriptions_QNAME = new QName("http://www.opengis.net/tjs/1.0", "FrameworkDescriptions");
    private final static QName _DescribeDatasets_QNAME = new QName("http://www.opengis.net/tjs/1.0", "DescribeDatasets");
    private final static QName _DatasetDescriptions_QNAME = new QName("http://www.opengis.net/tjs/1.0", "DatasetDescriptions");
    private final static QName _DescribeData_QNAME = new QName("http://www.opengis.net/tjs/1.0", "DescribeData");
    private final static QName _DataDescriptions_QNAME = new QName("http://www.opengis.net/tjs/1.0", "DataDescriptions");
    private final static QName _GetData_QNAME = new QName("http://www.opengis.net/tjs/1.0", "GetData");
    private final static QName _GDAS_QNAME = new QName("http://www.opengis.net/tjs/1.0", "GDAS");
    private final static QName _DescribeJoinAbilities_QNAME = new QName("http://www.opengis.net/tjs/1.0", "DescribeJoinAbilities");
    private final static QName _JoinAbilities_QNAME = new QName("http://www.opengis.net/tjs/1.0", "JoinAbilities");
    private final static QName _DescribeKey_QNAME = new QName("http://www.opengis.net/tjs/1.0", "DescribeKey");
    private final static QName _FrameworkKeyDescription_QNAME = new QName("http://www.opengis.net/tjs/1.0", "FrameworkKeyDescription");
    private final static QName _JoinData_QNAME = new QName("http://www.opengis.net/tjs/1.0", "JoinData");
    private final static QName _JoinDataResponse_QNAME = new QName("http://www.opengis.net/tjs/1.0", "JoinDataResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived
     * classes for package: org.geotoolkit.tjs.xml.v100
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "GetCapabilities")
    public JAXBElement<GetCapabilitiesType> createGetCapabilities(GetCapabilitiesType value) {
        return new JAXBElement<GetCapabilitiesType>(_GetCapabilities_QNAME, GetCapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CapabilitiesType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "Capabilities")
    public JAXBElement<CapabilitiesType> createCapabilities(CapabilitiesType value) {
        return new JAXBElement<CapabilitiesType>(_Capabilities_QNAME, CapabilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeFrameworksType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "DescribeFrameworks")
    public JAXBElement<DescribeFrameworksType> createDescribeFrameworks(DescribeFrameworksType value) {
        return new JAXBElement<DescribeFrameworksType>(_DescribeFrameworks_QNAME, DescribeFrameworksType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FrameworkDescriptionsType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "FrameworkDescriptions")
    public JAXBElement<FrameworkDescriptionsType> createFrameworkDescriptions(FrameworkDescriptionsType value) {
        return new JAXBElement<FrameworkDescriptionsType>(_FrameworkDescriptions_QNAME, FrameworkDescriptionsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeDatasetsType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "DescribeDatasets")
    public JAXBElement<DescribeDatasetsType> createDescribeDatasets(DescribeDatasetsType value) {
        return new JAXBElement<DescribeDatasetsType>(_DescribeDatasets_QNAME, DescribeDatasetsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DatasetDescriptionsType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "DatasetDescriptions")
    public JAXBElement<DatasetDescriptionsType> createDatasetDescriptions(DatasetDescriptionsType value) {
        return new JAXBElement<DatasetDescriptionsType>(_DatasetDescriptions_QNAME, DatasetDescriptionsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeDataType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "DescribeData")
    public JAXBElement<DescribeDataType> createDescribeData(DescribeDataType value) {
        return new JAXBElement<DescribeDataType>(_DescribeData_QNAME, DescribeDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataDescriptionsType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "DataDescriptions")
    public JAXBElement<DataDescriptionsType> createDataDescriptions(DataDescriptionsType value) {
        return new JAXBElement<DataDescriptionsType>(_DataDescriptions_QNAME, DataDescriptionsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDataType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "GetData")
    public JAXBElement<GetDataType> createGetData(GetDataType value) {
        return new JAXBElement<GetDataType>(_GetData_QNAME, GetDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GDASType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "GDAS")
    public JAXBElement<GDASType> createGDAS(GDASType value) {
        return new JAXBElement<GDASType>(_GDAS_QNAME, GDASType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeJoinAbilitiesType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "DescribeJoinAbilities")
    public JAXBElement<DescribeJoinAbilitiesType> createDescribeJoinAbilities(DescribeJoinAbilitiesType value) {
        return new JAXBElement<DescribeJoinAbilitiesType>(_DescribeJoinAbilities_QNAME, DescribeJoinAbilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JoinAbilitiesType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "JoinAbilities")
    public JAXBElement<JoinAbilitiesType> createJoinAbilities(JoinAbilitiesType value) {
        return new JAXBElement<JoinAbilitiesType>(_JoinAbilities_QNAME, JoinAbilitiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescribeKeyType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "DescribeKey")
    public JAXBElement<DescribeKeyType> createDescribeKey(DescribeKeyType value) {
        return new JAXBElement<DescribeKeyType>(_DescribeKey_QNAME, DescribeKeyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FrameworkKeyDescriptionType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "FrameworkKeyDescription")
    public JAXBElement<FrameworkKeyDescriptionType> createFrameworkKeyDescription(FrameworkKeyDescriptionType value) {
        return new JAXBElement<FrameworkKeyDescriptionType>(_FrameworkKeyDescription_QNAME, FrameworkKeyDescriptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JoinDataRequestType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "JoinData")
    public JAXBElement<JoinDataRequestType> createJoinData(JoinDataRequestType value) {
        return new JAXBElement<JoinDataRequestType>(_JoinData_QNAME, JoinDataRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JoinDataResponseType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/tjs/1.0", name = "JoinDataResponse")
    public JAXBElement<JoinDataResponseType> createJoinDataResponse(JoinDataResponseType value) {
        return new JAXBElement<JoinDataResponseType>(_JoinDataResponse_QNAME, JoinDataResponseType.class, null, value);
    }
}
