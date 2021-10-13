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
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.wfs.xml.AllSomeType;
import org.geotoolkit.wfs.xml.Transaction;


/**
 * <p>Java class for TransactionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TransactionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wfs/2.0}BaseRequestType">
 *       &lt;sequence>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/wfs/2.0}AbstractTransactionAction"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="lockId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="releaseAction" type="{http://www.opengis.net/wfs/2.0}AllSomeType" default="ALL" />
 *       &lt;attribute name="srsName" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionType", propOrder = {
    "abstractTransactionAction"
})
@XmlRootElement(name="Transaction")
public class TransactionType extends BaseRequestType implements Transaction {

    @XmlElementRef(name = "AbstractTransactionAction", namespace = "http://www.opengis.net/wfs/2.0", type = JAXBElement.class)
    private List<JAXBElement<?>> abstractTransactionAction;
    @XmlAttribute
    private String lockId;
    @XmlAttribute
    private AllSomeType releaseAction;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String srsName;

    public TransactionType() {

    }

    public TransactionType(final String service, final String version, final String handle, final AllSomeType releaseAction, final DeleteType delete) {
        super(service, version, handle);
        this.releaseAction = releaseAction;
        this.abstractTransactionAction = new ArrayList<JAXBElement<?>>();
        if (delete != null) {
            final ObjectFactory factory = new ObjectFactory();
            final JAXBElement<?> jb = factory.createDelete(delete);
            this.abstractTransactionAction.add(jb);
        }
    }

    public TransactionType(final String service, final String version, final String handle, final AllSomeType releaseAction, final InsertType insert) {
        super(service, version, handle);
        this.releaseAction = releaseAction;
        this.abstractTransactionAction = new ArrayList<JAXBElement<?>>();
        if (insert != null) {
            final ObjectFactory factory = new ObjectFactory();
            final JAXBElement<?> jb = factory.createInsert(insert);
            this.abstractTransactionAction.add(jb);
        }
    }

    public TransactionType(final String service, final String version, final String handle, final AllSomeType releaseAction, final UpdateType udpate) {
        super(service, version, handle);
        this.releaseAction = releaseAction;
        this.abstractTransactionAction = new ArrayList<JAXBElement<?>>();
        if (udpate != null) {
            final ObjectFactory factory = new ObjectFactory();
            final JAXBElement<?> jb = factory.createUpdate(udpate);
            this.abstractTransactionAction.add(jb);
        }
    }

    public TransactionType(final String service, final String version, final String handle, final AllSomeType releaseAction, final ReplaceType replace) {
        super(service, version, handle);
        this.releaseAction = releaseAction;
        this.abstractTransactionAction = new ArrayList<JAXBElement<?>>();
        if (replace != null) {
            final ObjectFactory factory = new ObjectFactory();
            final JAXBElement<?> jb = factory.createReplace(replace);
            this.abstractTransactionAction.add(jb);
        }
    }

    /**
     * Gets the value of the abstractTransactionAction property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link DeleteType }{@code >}
     * {@link JAXBElement }{@code <}{@link AbstractTransactionActionType }{@code >}
     * {@link JAXBElement }{@code <}{@link InsertType }{@code >}
     * {@link JAXBElement }{@code <}{@link NativeType }{@code >}
     * {@link JAXBElement }{@code <}{@link UpdateType }{@code >}
     * {@link JAXBElement }{@code <}{@link ReplaceType }{@code >}
     *
     *
     */
    public List<JAXBElement<?>> getAbstractTransactionAction() {
        if (abstractTransactionAction == null) {
            abstractTransactionAction = new ArrayList<JAXBElement<?>>();
        }
        return this.abstractTransactionAction;
    }

    @Override
    public List<Object> getTransactionAction() {
        final List<Object> result = new ArrayList<Object>();
        if (abstractTransactionAction != null) {
            for (JAXBElement<?> jb : abstractTransactionAction) {
                result.add(jb.getValue());
            }
        }
        return result;
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
     * Gets the value of the releaseAction property.
     *
     * @return
     *     possible object is
     *     {@link AllSomeType }
     *
     */
    public AllSomeType getReleaseAction() {
        if (releaseAction == null) {
            return AllSomeType.ALL;
        } else {
            return releaseAction;
        }
    }

    /**
     * Sets the value of the releaseAction property.
     *
     * @param value
     *     allowed object is
     *     {@link AllSomeType }
     *
     */
    public void setReleaseAction(AllSomeType value) {
        this.releaseAction = value;
    }

    /**
     * Gets the value of the srsName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
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
    public void setSrsName(String value) {
        this.srsName = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (lockId != null) {
            sb.append("lockId=").append(lockId).append('\n');
        }
        if (releaseAction != null) {
            sb.append("releaseAction=").append(releaseAction).append('\n');
        }
        if (srsName != null) {
            sb.append("srsName=").append(srsName).append('\n');
        }
        if (abstractTransactionAction != null) {
            sb.append("abstractTransactionAction:").append('\n');
            for (JAXBElement obj : abstractTransactionAction) {
                sb.append(obj.getValue()).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TransactionType && super.equals(object)) {
            final TransactionType that = (TransactionType) object;

            boolean ok = false;
            if (this.abstractTransactionAction.size() == that.abstractTransactionAction.size()) {
                ok = true;
                for (int i = 0; i < abstractTransactionAction.size(); i++) {
                    Object thisO = this.abstractTransactionAction.get(i).getValue();
                    Object thatO = that.abstractTransactionAction.get(i).getValue();
                    if (!Objects.equals(thisO, thatO)) {
                        ok = false;
                        break;
                    }
                }
            }

            return  ok &&
                    Objects.equals(this.lockId, that.lockId) &&
                    Objects.equals(this.srsName, that.srsName) &&
                    Objects.equals(this.releaseAction, that.releaseAction);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.lockId != null ? this.lockId.hashCode() : 0);
        hash = 37 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        hash = 37 * hash + (this.abstractTransactionAction != null ? this.abstractTransactionAction.hashCode() : 0);
        hash = 37 * hash + (this.releaseAction != null ? this.releaseAction.hashCode() : 0);
        return hash;
    }

}
