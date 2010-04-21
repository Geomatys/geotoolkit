/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractCapabilities;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.DataRecord;
import org.geotoolkit.swe.xml.SimpleDataRecord;
import org.geotoolkit.swe.xml.v100.AbstractDataRecordType;
import org.geotoolkit.swe.xml.v100.DataRecordType;
import org.geotoolkit.swe.xml.v100.SimpleDataRecordType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}AbstractDataRecord"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "abstractDataRecord"
})
@XmlRootElement(name = "capabilities")
public class Capabilities implements AbstractCapabilities {

    @XmlElementRef(name = "AbstractDataRecord", namespace = "http://www.opengis.net/swe/1.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataRecordType> abstractDataRecord;
    @XmlAttribute
    private List<String> nilReason;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;

    public Capabilities() {

    }

    public Capabilities(DataRecordType dataRecord) {
        org.geotoolkit.swe.xml.v100.ObjectFactory facto = new org.geotoolkit.swe.xml.v100.ObjectFactory();
        this.abstractDataRecord = facto.createDataRecord(dataRecord);
    }

    public Capabilities(AbstractCapabilities capa) {
        if (capa != null) {
            if (capa.getDataRecord() != null) {
                AbstractDataRecord record = capa.getDataRecord();
                org.geotoolkit.swe.xml.v100.ObjectFactory factory = new org.geotoolkit.swe.xml.v100.ObjectFactory();
                if (record instanceof SimpleDataRecord) {
                    abstractDataRecord = factory.createSimpleDataRecord(new SimpleDataRecordType((SimpleDataRecord)record));
                } else if (record instanceof DataRecord) {
                    abstractDataRecord = factory.createDataRecord(new DataRecordType((DataRecord)record));
                } else {
                    System.out.println("UNINPLEMENTED CASE:" + record);
                }
            }
            this.actuate = capa.getActuate();
            this.arcrole = capa.getArcrole();
            this.href    = capa.getHref();
            this.remoteSchema = capa.getRemoteSchema();
            this.role    = capa.getRole();
            this.show    = capa.getShow();
            this.title   = capa.getTitle();
            this.type    = capa.getType();
        }
    }

    /**
     * Gets the value of the abstractDataRecord property.
     */
    public JAXBElement<? extends AbstractDataRecordType> getAbstractDataRecord() {
        return abstractDataRecord;
    }

     public AbstractDataRecordType getDataRecord() {
         if (abstractDataRecord != null) {
            return abstractDataRecord.getValue();
         }
         return null;
    }

    /**
     * Sets the value of the abstractDataRecord property.
     */
    public void setAbstractDataRecord(JAXBElement<? extends AbstractDataRecordType> value) {
        this.abstractDataRecord = ((JAXBElement<? extends AbstractDataRecordType> ) value);
    }

    /**
     * Sets the value of the abstractDataRecord property.
     */
    public void setAbstractDataRecord(AbstractDataRecordType value) {
        org.geotoolkit.swe.xml.v100.ObjectFactory facto = new org.geotoolkit.swe.xml.v100.ObjectFactory();
        if (value instanceof SimpleDataRecordType) {
            this.abstractDataRecord = facto.createSimpleDataRecord((SimpleDataRecordType) value);
        } else if (value instanceof DataRecordType) {
            this.abstractDataRecord = facto.createDataRecord((DataRecordType) value);
        } else if (value instanceof AbstractDataRecordType) {
            this.abstractDataRecord = facto.createAbstractDataRecord((AbstractDataRecordType) value);
        }
    }

    /**
     * Gets the value of the nilReason property.
     * 
     */
    public List<String> getNilReason() {
        if (nilReason == null) {
            nilReason = new ArrayList<String>();
        }
        return this.nilReason;
    }

    /**
     * Gets the value of the remoteSchema property.
     * 
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     */
    public void setRemoteSchema(String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the actuate property.
     */
    public String getActuate() {
        return actuate;
    }

    public String getName() {
        return null;
    }

    /**
     * Sets the value of the actuate property.
     */
    public void setActuate(String value) {
        this.actuate = value;
    }

    /**
     * Gets the value of the arcrole property.
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     * 
     */
    public void setArcrole(String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the href property.
    */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Gets the value of the show property.
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     */
    public void setShow(String value) {
        this.show = value;
    }

    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the type property.
     * 
     */
    public String getType() {
        return type;
     }

    /**
     * Sets the value of the type property.
     */
    public void setType(String value) {
        this.type = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Capabilities]").append("\n");
        if (abstractDataRecord != null)
            sb.append("process: ").append(abstractDataRecord.getValue()).append('\n');

        if (nilReason != null) {
            sb.append("nilReason:").append('\n');
            for (String k : nilReason) {
                sb.append("nilReason: ").append(k).append('\n');
            }
        }
        if (remoteSchema != null)
            sb.append("remoteSchema: ").append(remoteSchema).append('\n');
        if (actuate != null)
            sb.append("actuate: ").append(actuate).append('\n');
        if (arcrole != null)
            sb.append("actuate: ").append(arcrole).append('\n');
        if (href != null)
            sb.append("href: ").append(href).append('\n');
        if (role != null)
            sb.append("role: ").append(role).append('\n');
        if (show != null)
            sb.append("show: ").append(show).append('\n');
        if (title != null)
            sb.append("title: ").append(title).append('\n');
        if (type != null)
            sb.append("type: ").append(type).append('\n');
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof Capabilities) {
            final Capabilities that = (Capabilities) object;
            boolean proc = false;
            if (this.abstractDataRecord != null && that.abstractDataRecord != null) {
                proc = Utilities.equals(this.abstractDataRecord.getValue(), that.abstractDataRecord.getValue());
            } else if (this.abstractDataRecord == null && that.abstractDataRecord == null) {
                proc = true;
            }
            return Utilities.equals(this.actuate,      that.actuate)       &&
                   Utilities.equals(this.arcrole,      that.arcrole)       &&
                   Utilities.equals(this.href,         that.href)          &&
                   Utilities.equals(this.nilReason,    that.nilReason)     &&
                   proc                                                    &&
                   Utilities.equals(this.remoteSchema, that.remoteSchema)  &&
                   Utilities.equals(this.role,         that.role)          &&
                   Utilities.equals(this.show,         that.show)          &&
                   Utilities.equals(this.title,        that.title)         &&
                   Utilities.equals(this.type,         that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.abstractDataRecord != null ? this.abstractDataRecord.hashCode() : 0);
        hash = 79 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        hash = 79 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 79 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 79 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 79 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 79 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 79 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 79 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
}
