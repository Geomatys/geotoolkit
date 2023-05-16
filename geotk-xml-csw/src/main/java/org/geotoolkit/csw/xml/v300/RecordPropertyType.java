/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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
package org.geotoolkit.csw.xml.v300;

import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.csw.xml.CSWMarshallerPool;
import org.geotoolkit.csw.xml.RecordProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * <p>Classe Java pour RecordPropertyType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="RecordPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecordPropertyType", propOrder = {
    "name",
    "value"
})
public class RecordPropertyType implements RecordProperty{

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.csw.xml.v300");

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlAnyElement(lax = true)
    protected Object value;

    public RecordPropertyType() {

    }

    public RecordPropertyType(final String name, final Object value) {
        this.name  = name;
        if (value instanceof String) {
            this.value = buildStringNode((String)value);
        } else if (value instanceof Node) {
            this.value = addValueNode((Node)value);
        } else {
            this.value = buildNode(value);
        }
    }

    private Node buildStringNode(final String s) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element e = doc.createElementNS("http://www.opengis.net/cat/csw/3.0", "Value");
            e.setPrefix("csw");
            e.setTextContent(s);
            return e;
        } catch (ParserConfigurationException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Node buildNode(final Object o) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element e = doc.createElementNS("http://www.opengis.net/cat/csw/3.0", "Value");
            e.setPrefix("csw");
            Marshaller m = CSWMarshallerPool.getInstance().acquireMarshaller();
            m.marshal(o, e);
            CSWMarshallerPool.getInstance().recycle(m);
            return e;
        } catch (ParserConfigurationException | JAXBException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Node addValueNode(final Node o) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element e = doc.createElementNS("http://www.opengis.net/cat/csw/3.0", "Value");
            e.setPrefix("csw");
            final Node clone = e.getOwnerDocument().importNode(o, true);
            e.appendChild(clone);
            return e;
        } catch (ParserConfigurationException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Obtient la valeur de la propriété name.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété value.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    @Override
    public Object getValue() {
        // remove the "csw:Value" node
        if (value instanceof Node) {
            return ((Node)value).getFirstChild();
        }
        return value;
    }

    /**
     * Définit la valeur de la propriété value.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setValue(Object value) {
        this.value = value;
    }

}
