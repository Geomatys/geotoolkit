/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.v200.FilterType;
import org.geotoolkit.ogc.xml.v200.AbstractAdhocQueryExpressionType;
import org.geotoolkit.ogc.xml.v200.SortByType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wfs.xml.Query;


/**
 * <p>Java class for QueryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="QueryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}AbstractAdhocQueryExpressionType">
 *       &lt;attribute name="srsName" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="featureVersion" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryType")
@XmlRootElement(name="Query")
public class QueryType extends AbstractAdhocQueryExpressionType implements Query {

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String srsName;
    @XmlAttribute
    private String featureVersion;

    public QueryType() {

    }

    public QueryType(final QueryType that) {
        super(that);
        if (that != null) {
            this.featureVersion = that.featureVersion;
            this.srsName        = that.srsName;
        }
    }

    public QueryType(final FilterType filter, final List<QName> typeName, final String featureVersion) {
        super(filter, typeName);
        this.featureVersion = featureVersion;
    }

    public QueryType(final FilterType filter, final List<QName> typeName, final String featureVersion, final String srsName,
            final SortByType sort, final List<String> propertyNames) {
        super(filter, typeName);
        this.featureVersion = featureVersion;
        this.srsName        = srsName;
        setSortBy(sort);
        setPropertyNames(propertyNames);
    }

    public final void setPropertyNames(final List<String> properties) {
        if (properties != null) {
            if (this.abstractProjectionClause == null) {
                this.abstractProjectionClause = new ArrayList<JAXBElement<?>>();
            }
            final ObjectFactory factory = new ObjectFactory();
            for (String property : properties) {
                this.abstractProjectionClause.add(factory.createPropertyName(new PropertyName(new QName(property))));
            }
        }
    }
    
    @Override
    protected  List<JAXBElement<?>> cloneProjectionClause(final List<JAXBElement<?>> toClone) {
        final List<JAXBElement<?>> result = new ArrayList<JAXBElement<?>>();
        for (JAXBElement<?> prjClause : toClone) {
            final Object value = prjClause.getValue();
            if (value instanceof PropertyName) {
                final ObjectFactory factory = new ObjectFactory();
                final PropertyName pn = new PropertyName((PropertyName)value);
                result.add(factory.createPropertyName(pn));
            } else {
                throw new IllegalArgumentException("Unexpected abstract Projection type:" + value.getClass().getName());
            }
        }
        return result;
    }
    
    /**
     * Gets the value of the srsName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getSrsName() {
        return srsName;
    }

    /**
     * Sets the value of the srsName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setSrsName(String value) {
        this.srsName = value;
    }

    /**
     * Gets the value of the featureVersion property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFeatureVersion() {
        return featureVersion;
    }

    /**
     * Sets the value of the featureVersion property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFeatureVersion(String value) {
        this.featureVersion = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof QueryType && super.equals(object)) {
            final QueryType that = (QueryType) object;

            return Utilities.equals(this.featureVersion, that.featureVersion) &&
                   Utilities.equals(this.srsName, that.srsName);
            }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.featureVersion != null ? this.featureVersion.hashCode() : 0);
        hash = 37 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if(featureVersion != null) {
            s.append("featureVersion:").append(featureVersion).append('\n');
        }
        if (srsName != null) {
            s.append("srsName:").append(srsName).append('\n');
        }
        return s.toString();
    }
}
