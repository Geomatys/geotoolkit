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

package org.geotoolkit.sml.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SettingsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SettingsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorml/2.0}AbstractSettingsType">
 *       &lt;sequence>
 *         &lt;element name="setValue" type="{http://www.opengis.net/sensorml/2.0}ValueSettingPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="setArrayValues" type="{http://www.opengis.net/sensorml/2.0}ArraySettingPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="setConstraint" type="{http://www.opengis.net/sensorml/2.0}ConstraintSettingPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="setMode" type="{http://www.opengis.net/sensorml/2.0}ModeSettingPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="setStatus" type="{http://www.opengis.net/sensorml/2.0}StatusSettingPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SettingsType", propOrder = {
    "setValue",
    "setArrayValues",
    "setConstraint",
    "setMode",
    "setStatus"
})
public class SettingsType
    extends AbstractSettingsType
{

    protected List<ValueSettingPropertyType> setValue;
    protected List<ArraySettingPropertyType> setArrayValues;
    protected List<ConstraintSettingPropertyType> setConstraint;
    protected List<ModeSettingPropertyType> setMode;
    protected List<StatusSettingPropertyType> setStatus;

    /**
     * Gets the value of the setValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSetValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValueSettingPropertyType }
     * 
     * 
     */
    public List<ValueSettingPropertyType> getSetValue() {
        if (setValue == null) {
            setValue = new ArrayList<ValueSettingPropertyType>();
        }
        return this.setValue;
    }

    /**
     * Gets the value of the setArrayValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setArrayValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSetArrayValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArraySettingPropertyType }
     * 
     * 
     */
    public List<ArraySettingPropertyType> getSetArrayValues() {
        if (setArrayValues == null) {
            setArrayValues = new ArrayList<ArraySettingPropertyType>();
        }
        return this.setArrayValues;
    }

    /**
     * Gets the value of the setConstraint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setConstraint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSetConstraint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConstraintSettingPropertyType }
     * 
     * 
     */
    public List<ConstraintSettingPropertyType> getSetConstraint() {
        if (setConstraint == null) {
            setConstraint = new ArrayList<ConstraintSettingPropertyType>();
        }
        return this.setConstraint;
    }

    /**
     * Gets the value of the setMode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setMode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSetMode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ModeSettingPropertyType }
     * 
     * 
     */
    public List<ModeSettingPropertyType> getSetMode() {
        if (setMode == null) {
            setMode = new ArrayList<ModeSettingPropertyType>();
        }
        return this.setMode;
    }

    /**
     * Gets the value of the setStatus property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the setStatus property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSetStatus().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StatusSettingPropertyType }
     * 
     * 
     */
    public List<StatusSettingPropertyType> getSetStatus() {
        if (setStatus == null) {
            setStatus = new ArrayList<StatusSettingPropertyType>();
        }
        return this.setStatus;
    }

}
