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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.DistributedSearch;


/**
 *
 *             Governs the behaviour of a distributed search.
 *             hopCount     - the maximum number of message hops before
 *                            the search is terminated. Each catalogue node
 *                            decrements this value when the request is received,
 *                            and must not forward the request if hopCount=0.
 *
 *
 * <p>Classe Java pour DistributedSearchType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="DistributedSearchType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="federatedCatalogues" type="{http://www.opengis.net/cat/csw/3.0}FederatedCatalogueType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="hopCount" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="2" />
 *       &lt;attribute name="clientId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="distributedSearchId" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="distributedSearchIdTimout" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" default="600" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DistributedSearchType", propOrder = {
    "federatedCatalogues"
})
public class DistributedSearchType implements DistributedSearch {

    protected List<FederatedCatalogueType> federatedCatalogues;
    @XmlAttribute(name = "hopCount")
    @XmlSchemaType(name = "positiveInteger")
    protected Integer hopCount;
    @XmlAttribute(name = "clientId", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String clientId;
    @XmlAttribute(name = "distributedSearchId", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String distributedSearchId;
    @XmlAttribute(name = "distributedSearchIdTimout")
    @XmlSchemaType(name = "unsignedLong")
    protected Integer distributedSearchIdTimout;

    /**
     * An empty constructor used by JAXB
     */
    public DistributedSearchType(){

    }

    /**
     * Build a new Distributed search
     */
    public DistributedSearchType(final Integer hopCount){
        this.hopCount = hopCount;
    }

    public DistributedSearchType(final DistributedSearchType other){
        if (other != null) {
            this.hopCount                  = other.hopCount;
            this.clientId                  = other.clientId;
            this.distributedSearchId       = other.distributedSearchId;
            this.distributedSearchIdTimout = other.distributedSearchIdTimout;
        }
    }
    /**
     * Gets the value of the federatedCatalogues property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the federatedCatalogues property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFederatedCatalogues().add(newItem);
     * </pre>
     */
    public List<FederatedCatalogueType> getFederatedCatalogues() {
        if (federatedCatalogues == null) {
            federatedCatalogues = new ArrayList<>();
        }
        return this.federatedCatalogues;
    }

    /**
     * Obtient la valeur de la propriété hopCount.
     */
    @Override
    public Integer getHopCount() {
        if (hopCount == null) {
            return 2;
        } else {
            return hopCount;
        }
    }

    /**
     * Définit la valeur de la propriété hopCount.
     */
    @Override
    public void setHopCount(Integer value) {
        this.hopCount = value;
    }

    /**
     * Obtient la valeur de la propriété clientId.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Définit la valeur de la propriété clientId.
     */
    public void setClientId(String value) {
        this.clientId = value;
    }

    /**
     * Obtient la valeur de la propriété distributedSearchId.
     */
    public String getDistributedSearchId() {
        return distributedSearchId;
    }

    /**
     * Définit la valeur de la propriété distributedSearchId.
     */
    public void setDistributedSearchId(String value) {
        this.distributedSearchId = value;
    }

    /**
     * Obtient la valeur de la propriété distributedSearchIdTimout.
     */
    public Integer getDistributedSearchIdTimout() {
        if (distributedSearchIdTimout == null) {
            return 600;
        } else {
            return distributedSearchIdTimout;
        }
    }

    /**
     * Définit la valeur de la propriété distributedSearchIdTimout.
     */
    public void setDistributedSearchIdTimout(Integer value) {
        this.distributedSearchIdTimout = value;
    }
}
