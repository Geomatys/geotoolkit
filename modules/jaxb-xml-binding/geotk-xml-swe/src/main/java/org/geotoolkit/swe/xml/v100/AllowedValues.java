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
package org.geotoolkit.swe.xml.v100;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.AbstractAllowedValues;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;choice>
 *           &lt;element name="min" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *           &lt;element name="max" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;/choice>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="interval" type="{http://www.opengis.net/swe/1.0}decimalPair"/>
 *           &lt;element name="valueList" type="{http://www.opengis.net/swe/1.0}decimalList"/>
 *         &lt;/choice>
 *       &lt;/choice>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
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
    "min",
    "max",
    "intervalOrValueList"
})
@XmlRootElement(name = "AllowedValues")
public class AllowedValues implements AbstractAllowedValues {

    private Double min;
    private Double max;
    @XmlElementRefs({
        @XmlElementRef(name = "interval", namespace = "http://www.opengis.net/swe/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "valueList", namespace = "http://www.opengis.net/swe/1.0", type = JAXBElement.class)
    })
    private List<JAXBElement<List<Double>>> intervalOrValueList;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    public AllowedValues() {

    }

    public AllowedValues(AbstractAllowedValues av) {
        if (av != null) {
            this.id = av.getId();
            this.max = av.getMax();
            this.min = av.getMin();
            this.setInterval(av.getInterval());
            this.setValueList(av.getValueList());
        }
    }

    public List<Double> getInterval() {
        for (JAXBElement<List<Double>> jb : getIntervalOrValueList()) {
            if (jb.getName().getLocalPart().equals("interval")) {
                return jb.getValue();
            } 
        }
        return null;
    }

    public void setInterval(List<Double> interval) {
        if (interval != null) {
            if (this.intervalOrValueList == null) {
                this.intervalOrValueList = new ArrayList<JAXBElement<List<Double>>>();
            }
            ObjectFactory factory = new ObjectFactory();
            this.intervalOrValueList.add(factory.createAllowedValuesInterval(interval));
        }
    }

    public void setInterval(Double interval) {
        if (interval != null) {
            if (this.intervalOrValueList == null) {
                this.intervalOrValueList = new ArrayList<JAXBElement<List<Double>>>();
            }
            ObjectFactory factory = new ObjectFactory();
            boolean found = false;
            for (int i = 0; i< intervalOrValueList.size() && !found; i++) {
                JAXBElement<List<Double>> jb = intervalOrValueList.get(i);
                if (jb.getName().getLocalPart().equals("interval")) {
                    List<Double> oldList = jb.getValue();
                    intervalOrValueList.remove(i);
                    List<Double> newList = new ArrayList<Double>();
                    for (Double s : oldList) {
                        newList.add(s);
                    }
                    newList.add(interval);
                    intervalOrValueList.add(i, factory.createAllowedValuesInterval(newList));
                    found = true;
                }
            }

            if (!found) {
                this.intervalOrValueList.add(factory.createAllowedValuesInterval(Arrays.asList(interval)));
            } 
        }
    }
    
    public List<Double> getValueList() {
        for (JAXBElement<List<Double>> jb : getIntervalOrValueList()) {
            if (jb.getName().getLocalPart().equals("valueList")) {
                return jb.getValue();
            }
        }
        return null;
    }

    public void setValueList(List<Double> valueList) {
        if (valueList != null) {
            if (this.intervalOrValueList == null) {
                this.intervalOrValueList = new ArrayList<JAXBElement<List<Double>>>();
            }
            ObjectFactory factory = new ObjectFactory();
            this.intervalOrValueList.add(factory.createAllowedValuesValueList(valueList));
        }
    }

   public void setValueList(Double interval) {
        if (interval != null) {
            if (this.intervalOrValueList == null) {
                this.intervalOrValueList = new ArrayList<JAXBElement<List<Double>>>();
            }
            ObjectFactory factory = new ObjectFactory();
            boolean found = false;
            for (int i = 0; i< intervalOrValueList.size() && !found; i++) {
                JAXBElement<List<Double>> jb = intervalOrValueList.get(i);
                if (jb.getName().getLocalPart().equals("valueList")) {
                    List<Double> oldList = jb.getValue();
                    intervalOrValueList.remove(i);
                    List<Double> newList = new ArrayList<Double>();
                    for (Double s : oldList) {
                        newList.add(s);
                    }
                    newList.add(interval);
                    intervalOrValueList.add(i, factory.createAllowedValuesValueList(newList));
                    found = true;
                }
            }

            if (!found) {
                this.intervalOrValueList.add(factory.createAllowedValuesValueList(Arrays.asList(interval)));
            }
        }
    }

    /**
     * Gets the value of the min property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMin() {
        return min;
    }

    /**
     * Sets the value of the min property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMin(Double value) {
        this.min = value;
    }

    /**
     * Gets the value of the max property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMax() {
        return max;
    }

    /**
     * Sets the value of the max property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMax(Double value) {
        this.max = value;
    }

    /**
     * Gets the value of the intervalOrValueList property.
     * {@link JAXBElement }{@code <}{@link List }{@code <}{@link Double }{@code >}{@code >}
     */
    public List<JAXBElement<List<Double>>> getIntervalOrValueList() {
        if (intervalOrValueList == null) {
            intervalOrValueList = new ArrayList<JAXBElement<List<Double>>>();
        }
        return this.intervalOrValueList;
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

}
