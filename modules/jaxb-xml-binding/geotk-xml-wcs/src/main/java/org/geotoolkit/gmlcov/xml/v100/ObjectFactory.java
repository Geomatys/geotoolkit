/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.gmlcov.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.v321.GeometryPropertyType;
import org.geotoolkit.gml.xml.v321.VectorType;
import org.geotoolkit.swe.xml.v200.DataRecordPropertyType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.opengis.gmlcov._1 package. 
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

    private final static QName _MultiSurfaceCoverage_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "MultiSurfaceCoverage");
    private final static QName _MultiPointCoverage_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "MultiPointCoverage");
    private final static QName _MultiCurveCoverage_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "MultiCurveCoverage");
    private final static QName _Extension_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "Extension");
    private final static QName _AbstractReferenceableGrid_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "AbstractReferenceableGrid");
    private final static QName _AbstractDiscreteCoverage_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "AbstractDiscreteCoverage");
    private final static QName _RangeType_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "rangeType");
    private final static QName _AbstractContinuousCoverage_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "AbstractContinuousCoverage");
    private final static QName _ReferenceableGridCoverage_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "ReferenceableGridCoverage");
    private final static QName _RectifiedGridCoverage_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "RectifiedGridCoverage");
    private final static QName _ParameterValue_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "ParameterValue");
    private final static QName _SimpleMultiPoint_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "SimpleMultiPoint");
    private final static QName _GridCoverage_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "GridCoverage");
    private final static QName _MultiSolidCoverage_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "MultiSolidCoverage");
    private final static QName _GeometryValue_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "geometryValue");
    private final static QName _ReferenceableGridProperty_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "referenceableGridProperty");
    private final static QName _VectorValue_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "vectorValue");
    private final static QName _AbstractCoverage_QNAME = new QName("http://www.opengis.net/gmlcov/1.0", "AbstractCoverage");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.gmlcov._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SimpleMultiPointType }
     * 
     */
    public SimpleMultiPointType createSimpleMultiPointType() {
        return new SimpleMultiPointType();
    }

    /**
     * Create an instance of {@link AbstractDiscreteCoverageType }
     * 
     */
    public AbstractDiscreteCoverageType createAbstractDiscreteCoverageType() {
        return new AbstractDiscreteCoverageType();
    }

    /**
     * Create an instance of {@link ParameterValueType }
     * 
     */
    public ParameterValueType createParameterValueType() {
        return new ParameterValueType();
    }

    /**
     * Create an instance of {@link ReferenceableGridPropertyType }
     * 
     */
    public ReferenceableGridPropertyType createReferenceableGridPropertyType() {
        return new ReferenceableGridPropertyType();
    }

    /**
     * Create an instance of {@link AbstractCoverageType }
     * 
     */
    public AbstractCoverageType createAbstractCoverageType() {
        return new AbstractCoverageType();
    }

    /**
     * Create an instance of {@link ExtensionType }
     * 
     */
    public ExtensionType createExtensionType() {
        return new ExtensionType();
    }

    /**
     * Create an instance of {@link Metadata }
     * 
     */
    public Metadata createMetadata() {
        return new Metadata();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "MultiSurfaceCoverage", substitutionHeadNamespace = "http://www.opengis.net/gmlcov/1.0", substitutionHeadName = "AbstractDiscreteCoverage")
    public JAXBElement<AbstractDiscreteCoverageType> createMultiSurfaceCoverage(AbstractDiscreteCoverageType value) {
        return new JAXBElement<AbstractDiscreteCoverageType>(_MultiSurfaceCoverage_QNAME, AbstractDiscreteCoverageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "MultiPointCoverage", substitutionHeadNamespace = "http://www.opengis.net/gmlcov/1.0", substitutionHeadName = "AbstractDiscreteCoverage")
    public JAXBElement<AbstractDiscreteCoverageType> createMultiPointCoverage(AbstractDiscreteCoverageType value) {
        return new JAXBElement<AbstractDiscreteCoverageType>(_MultiPointCoverage_QNAME, AbstractDiscreteCoverageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "MultiCurveCoverage", substitutionHeadNamespace = "http://www.opengis.net/gmlcov/1.0", substitutionHeadName = "AbstractDiscreteCoverage")
    public JAXBElement<AbstractDiscreteCoverageType> createMultiCurveCoverage(AbstractDiscreteCoverageType value) {
        return new JAXBElement<AbstractDiscreteCoverageType>(_MultiCurveCoverage_QNAME, AbstractDiscreteCoverageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtensionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "Extension")
    public JAXBElement<ExtensionType> createExtension(ExtensionType value) {
        return new JAXBElement<ExtensionType>(_Extension_QNAME, ExtensionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractReferenceableGridType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "AbstractReferenceableGrid", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "Grid")
    public JAXBElement<AbstractReferenceableGridType> createAbstractReferenceableGrid(AbstractReferenceableGridType value) {
        return new JAXBElement<AbstractReferenceableGridType>(_AbstractReferenceableGrid_QNAME, AbstractReferenceableGridType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "AbstractDiscreteCoverage", substitutionHeadNamespace = "http://www.opengis.net/gmlcov/1.0", substitutionHeadName = "AbstractCoverage")
    public JAXBElement<AbstractDiscreteCoverageType> createAbstractDiscreteCoverage(AbstractDiscreteCoverageType value) {
        return new JAXBElement<AbstractDiscreteCoverageType>(_AbstractDiscreteCoverage_QNAME, AbstractDiscreteCoverageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataRecordPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "rangeType")
    public JAXBElement<DataRecordPropertyType> createRangeType(DataRecordPropertyType value) {
        return new JAXBElement<DataRecordPropertyType>(_RangeType_QNAME, DataRecordPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractContinuousCoverageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "AbstractContinuousCoverage", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractCoverage")
    public JAXBElement<AbstractContinuousCoverageType> createAbstractContinuousCoverage(AbstractContinuousCoverageType value) {
        return new JAXBElement<AbstractContinuousCoverageType>(_AbstractContinuousCoverage_QNAME, AbstractContinuousCoverageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "ReferenceableGridCoverage", substitutionHeadNamespace = "http://www.opengis.net/gmlcov/1.0", substitutionHeadName = "AbstractCoverage")
    public JAXBElement<AbstractDiscreteCoverageType> createReferenceableGridCoverage(AbstractDiscreteCoverageType value) {
        return new JAXBElement<AbstractDiscreteCoverageType>(_ReferenceableGridCoverage_QNAME, AbstractDiscreteCoverageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "RectifiedGridCoverage", substitutionHeadNamespace = "http://www.opengis.net/gmlcov/1.0", substitutionHeadName = "AbstractCoverage")
    public JAXBElement<AbstractDiscreteCoverageType> createRectifiedGridCoverage(AbstractDiscreteCoverageType value) {
        return new JAXBElement<AbstractDiscreteCoverageType>(_RectifiedGridCoverage_QNAME, AbstractDiscreteCoverageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "ParameterValue", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractGeneralParameterValue")
    public JAXBElement<ParameterValueType> createParameterValue(ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_ParameterValue_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleMultiPointType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "SimpleMultiPoint", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractGeometricAggregate")
    public JAXBElement<SimpleMultiPointType> createSimpleMultiPoint(SimpleMultiPointType value) {
        return new JAXBElement<SimpleMultiPointType>(_SimpleMultiPoint_QNAME, SimpleMultiPointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "GridCoverage", substitutionHeadNamespace = "http://www.opengis.net/gmlcov/1.0", substitutionHeadName = "AbstractCoverage")
    public JAXBElement<AbstractDiscreteCoverageType> createGridCoverage(AbstractDiscreteCoverageType value) {
        return new JAXBElement<AbstractDiscreteCoverageType>(_GridCoverage_QNAME, AbstractDiscreteCoverageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "MultiSolidCoverage", substitutionHeadNamespace = "http://www.opengis.net/gmlcov/1.0", substitutionHeadName = "AbstractDiscreteCoverage")
    public JAXBElement<AbstractDiscreteCoverageType> createMultiSolidCoverage(AbstractDiscreteCoverageType value) {
        return new JAXBElement<AbstractDiscreteCoverageType>(_MultiSolidCoverage_QNAME, AbstractDiscreteCoverageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeometryPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "geometryValue")
    public JAXBElement<GeometryPropertyType> createGeometryValue(GeometryPropertyType value) {
        return new JAXBElement<GeometryPropertyType>(_GeometryValue_QNAME, GeometryPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceableGridPropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "referenceableGridProperty")
    public JAXBElement<ReferenceableGridPropertyType> createReferenceableGridProperty(ReferenceableGridPropertyType value) {
        return new JAXBElement<ReferenceableGridPropertyType>(_ReferenceableGridProperty_QNAME, ReferenceableGridPropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VectorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "vectorValue")
    public JAXBElement<VectorType> createVectorValue(VectorType value) {
        return new JAXBElement<VectorType>(_VectorValue_QNAME, VectorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractCoverageType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/gmlcov/1.0", name = "AbstractCoverage", substitutionHeadNamespace = "http://www.opengis.net/gml/3.2", substitutionHeadName = "AbstractFeature")
    public JAXBElement<AbstractCoverageType> createAbstractCoverage(AbstractCoverageType value) {
        return new JAXBElement<AbstractCoverageType>(_AbstractCoverage_QNAME, AbstractCoverageType.class, null, value);
    }

}
