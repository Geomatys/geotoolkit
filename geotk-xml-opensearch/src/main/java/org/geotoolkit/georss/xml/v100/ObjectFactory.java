/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.georss.xml.v100;


import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.georss.georss package.
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
    public final static QName _WhereType_QNAME = new QName("http://www.georss.org/georss", "where");

    public final static QName _Elev_QNAME = new QName("http://www.georss.org/georss", "elev");
    public final static QName _Polygon_QNAME = new QName("http://www.georss.org/georss", "polygon");
    public final static QName _Circle_QNAME = new QName("http://www.georss.org/georss", "circle");
    public final static QName _FeatureProperty_QNAME = new QName("http://www.georss.org/georss", "_featureProperty");
    public final static QName _FeatureName_QNAME = new QName("http://www.georss.org/georss", "featureName");
    public final static QName _FeatureTypeTag_QNAME = new QName("http://www.georss.org/georss", "featureTypeTag");
    public final static QName _Floor_QNAME = new QName("http://www.georss.org/georss", "floor");
    public final static QName _Radius_QNAME = new QName("http://www.georss.org/georss", "radius");
    public final static QName _RelationshipTag_QNAME = new QName("http://www.georss.org/georss", "relationshipTag");
    public final static QName _Line_QNAME = new QName("http://www.georss.org/georss", "line");
    public final static QName _Point_QNAME = new QName("http://www.georss.org/georss", "point");
    public final static QName _Box_QNAME = new QName("http://www.georss.org/georss", "box");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.georss.georss
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link WhereType }
     *
     */
    public WhereType createWhereType() {
        return new WhereType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "elev")
    public JAXBElement<Double> createElev(Double value) {
        return new JAXBElement<Double>(_Elev_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "polygon")
    public JAXBElement<List<Double>> createPolygon(List<Double> value) {
        return new JAXBElement<List<Double>>(_Polygon_QNAME, ((Class) List.class), null, ((List<Double> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "circle")
    public JAXBElement<List<Double>> createCircle(List<Double> value) {
        return new JAXBElement<List<Double>>(_Circle_QNAME, ((Class) List.class), null, ((List<Double> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractFeaturePropertyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "_featureProperty", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractObject")
    public JAXBElement<AbstractFeaturePropertyType> createFeatureProperty(AbstractFeaturePropertyType value) {
        return new JAXBElement<AbstractFeaturePropertyType>(_FeatureProperty_QNAME, AbstractFeaturePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "featureName")
    public JAXBElement<QName> createFeatureName(QName value) {
        return new JAXBElement<QName>(_FeatureName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "featureTypeTag")
    public JAXBElement<QName> createFeatureTypeTag(QName value) {
        return new JAXBElement<QName>(_FeatureTypeTag_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "floor")
    public JAXBElement<Integer> createFloor(Integer value) {
        return new JAXBElement<Integer>(_Floor_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "radius")
    public JAXBElement<Double> createRadius(Double value) {
        return new JAXBElement<Double>(_Radius_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WhereType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "where", substitutionHeadNamespace = "http://www.georss.org/georss", substitutionHeadName = "_featureProperty")
    public JAXBElement<WhereType> createWhere(WhereType value) {
        return new JAXBElement<WhereType>(_WhereType_QNAME, WhereType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "relationshipTag")
    public JAXBElement<QName> createRelationshipTag(QName value) {
        return new JAXBElement<QName>(_RelationshipTag_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "line")
    public JAXBElement<List<Double>> createLine(List<Double> value) {
        return new JAXBElement<List<Double>>(_Line_QNAME, ((Class) List.class), null, ((List<Double> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "point")
    public JAXBElement<List<Double>> createPoint(List<Double> value) {
        return new JAXBElement<List<Double>>(_Point_QNAME, ((Class) List.class), null, ((List<Double> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.georss.org/georss", name = "box")
    public JAXBElement<List<Double>> createBox(List<Double> value) {
        return new JAXBElement<List<Double>>(_Box_QNAME, ((Class) List.class), null, ((List<Double> ) value));
    }
}
