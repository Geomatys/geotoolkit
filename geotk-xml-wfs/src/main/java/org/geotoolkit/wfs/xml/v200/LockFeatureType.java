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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ogc.xml.v200.AbstractQueryExpressionType;
import org.geotoolkit.wfs.xml.AllSomeType;
import org.geotoolkit.wfs.xml.LockFeature;


/**
 * <p>Java class for LockFeatureType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LockFeatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wfs/2.0}BaseRequestType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}AbstractQueryExpression" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="lockId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expiry" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="300" />
 *       &lt;attribute name="lockAction" type="{http://www.opengis.net/wfs/2.0}AllSomeType" default="ALL" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LockFeatureType", propOrder = {
    "abstractQueryExpression"
})
public class LockFeatureType extends BaseRequestType implements LockFeature {

    @XmlElementRef(name = "AbstractQueryExpression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractQueryExpressionType>> abstractQueryExpression;
    @XmlAttribute
    private String lockId;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private int expiry = 300;
    @XmlAttribute
    private AllSomeType lockAction;

    public LockFeatureType() {

    }

    public LockFeatureType(final String service, final String version, final String handle, final List<QueryType> lock, final Integer expiry, final AllSomeType lockAction) {
        super(service, version, handle);
        this.expiry     = expiry;
        if (lock != null) {
            final ObjectFactory factory = new ObjectFactory();
            this.abstractQueryExpression =  new ArrayList<JAXBElement<? extends AbstractQueryExpressionType>>();
            for (QueryType q : lock) {
                this.abstractQueryExpression.add(factory.createQuery(q));
            }
        }

        this.lockAction = lockAction;
    }

    /**
     * Gets the value of the abstractQueryExpression property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link StoredQueryType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractQueryExpressionType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractAdhocQueryExpressionType }{@code >}
     * {@link JAXBElement }{@code <}{@link QueryType }{@code >}
     *
     */
    public List<JAXBElement<? extends AbstractQueryExpressionType>> getAbstractQueryExpression() {
        if (abstractQueryExpression == null) {
            abstractQueryExpression = new ArrayList<JAXBElement<? extends AbstractQueryExpressionType>>();
        }
        return this.abstractQueryExpression;
    }

    /**
     * Gets the value of the lockId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLockId() {
        return lockId;
    }

    /**
     * Sets the value of the lockId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLockId(String value) {
        this.lockId = value;
    }

    /**
     * Gets the value of the expiry property.
     *
     * @return
     *     possible object is
     *     {@link int }
     *
     */
    public int getExpiry() {
        return expiry;
    }

    /**
     * Sets the value of the expiry property.
     *
     * @param value
     *     allowed object is
     *     {@link int }
     *
     */
    public void setExpiry(int value) {
        this.expiry = value;
    }

    /**
     * Gets the value of the lockAction property.
     *
     * @return
     *     possible object is
     *     {@link AllSomeType }
     *
     */
    public AllSomeType getLockAction() {
        if (lockAction == null) {
            return AllSomeType.ALL;
        } else {
            return lockAction;
        }
    }

    /**
     * Sets the value of the lockAction property.
     *
     * @param value
     *     allowed object is
     *     {@link AllSomeType }
     *
     */
    public void setLockAction(AllSomeType value) {
        this.lockAction = value;
    }

}
