/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
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


package org.geotoolkit.sml.xml.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractOutputList;
import org.geotoolkit.sml.xml.AbstractOutputs;
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
 *         &lt;element name="OutputList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="output" type="{http://www.opengis.net/sensorML/1.0.1}IoComponentPropertyType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "outputList"
})
@XmlRootElement(name = "outputs")
public class Outputs implements AbstractOutputs {

    @XmlElement(name = "OutputList")
    private Outputs.OutputList outputList;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlSchemaType(name = "anyURI")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;

    public Outputs() {

    }

    public Outputs(OutputList outputList) {
        this.outputList = outputList;
    }

    public Outputs(AbstractOutputs outputs) {
        this.outputList    = new OutputList(outputs.getOutputList());
        this.actuate      = outputs.getActuate();
        this.arcrole      = outputs.getArcrole();
        this.href         = outputs.getHref();
        this.remoteSchema = outputs.getRemoteSchema();
        this.role         = outputs.getRole();
        this.show         = outputs.getShow();
        this.title        = outputs.getTitle();
        this.type         = outputs.getType();

    }

    /**
     * Gets the value of the outputList property.
     * 
     * @return
     *     possible object is
     *     {@link Outputs.OutputList }
     *     
     */
    public Outputs.OutputList getOutputList() {
        return outputList;
    }

    /**
     * Sets the value of the outputList property.
     * 
     * @param value
     *     allowed object is
     *     {@link Outputs.OutputList }
     *     
     */
    public void setOutputList(Outputs.OutputList value) {
        this.outputList = value;
    }

    /**
     * Gets the value of the remoteSchema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteSchema(String value) {
        this.remoteSchema = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        if (type == null) {
            return "simple";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Gets the value of the arcrole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArcrole(String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the show property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShow(String value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActuate(String value) {
        this.actuate = value;
    }


    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof Outputs) {
            final Outputs that = (Outputs) object;

            return Utilities.equals(this.actuate, that.actuate)           &&
                   Utilities.equals(this.href, that.href)                 &&
                   Utilities.equals(this.outputList, that.outputList)       &&
                   Utilities.equals(this.remoteSchema, that.remoteSchema) &&
                   Utilities.equals(this.role, that.role)                 &&
                   Utilities.equals(this.show, that.show)                 &&
                   Utilities.equals(this.title, that.title)               &&
                   Utilities.equals(this.type, that.type)                 &&
                   Utilities.equals(this.arcrole, that.arcrole);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.outputList != null ? this.outputList.hashCode() : 0);
        hash = 97 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 97 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 97 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 97 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 97 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 97 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 97 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
    
    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="output" type="{http://www.opengis.net/sensorML/1.0.1}IoComponentPropertyType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "output"
    })
    public static class OutputList implements AbstractOutputList {

        @XmlElement(required = true)
        private List<IoComponentPropertyType> output;
        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        private String id;

        public OutputList()  {

        }

        public OutputList(List<IoComponentPropertyType> output)  {
            this.output = output;
        }

        public OutputList(AbstractOutputList outputList) {
            this.output = (List<IoComponentPropertyType>) outputList.getOutput();
            this.id    = outputList.getId();
        }


        /**
         * Gets the value of the output property.
         */
        public List<IoComponentPropertyType> getOutput() {
            if (output == null) {
                output = new ArrayList<IoComponentPropertyType>();
            }
            return this.output;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

        /**
         * Verify if this entry is identical to specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }

            if (object instanceof OutputList) {
                final OutputList that = (OutputList) object;

                return Utilities.equals(this.output, that.output) &&
                       Utilities.equals(this.id, that.id);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 29 * hash + (this.output != null ? this.output.hashCode() : 0);
            hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }
    }
}
