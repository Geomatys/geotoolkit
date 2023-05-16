/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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


package org.geotoolkit.dif.xml.v102;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 *
 *                 Required by UMM-C; DIF and ECHO traditionally did not require.
 *
 *                 This entity contains records which describe the basis of the
 *                 time system used for a specific collection.
 *
 *                 Changes:
 *
 *                 | DIF 9      | DIF 10               | Notes                                   |
 *                 | ---------- | -------------------- | --------------------------------------- |
 *                 | Start_Date | Moved                | See Choices                             |
 *                 | Stop_Date  | Moved                | See Choices                             |
 *                 |     -      | Time_Type            | Added from ECHO 10                      |
 *                 |     -      | Date_Type            | Added from ECHO 10                      |
 *                 |     -      | Temporal_Range_Type  | Added from ECHO 10                      |
 *                 |     -      | Precision_Of_Seconds | Added from ECHO 10                      |
 *                 |     -      | Ends_At_Present_Flag | Added from ECHO 10                      |
 *                 |     -      | Temporal_Info        | Added to hold other ECHO 10 Time Fields |
 *
 *                 * Time_Type: This attribute provides the time system which the values found in temporal subclasses represent.
 *                 * Date_Type: This attribute specifies the type of date represented by the value in the date attributes of the temporal subclasses.
 *                 * Temporal_Range_Type: This attribute tells the system and ultimately the end user how temporal coverage is specified for the collection.
 *                 * Precision_Of_Seconds: The precision (position in number of places to right of decimal point) of seconds used in measurement.
 *                 * Ends_At_Present_Flag: This attribute will denote that a data collection which covers, temporally, a discontinuous range, currently ends at the present date. This way, the granules, which comprise the data collection, that are continuously being added to inventory need not update the data collection metadata for each one.
 *                 * TemporalInfo: unknown
 *
 *
 *
 * <p>Classe Java pour TemporalCoverageType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="TemporalCoverageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Time_Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Date_Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Temporal_Range_Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Precision_Of_Seconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Ends_At_Present_Flag" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="Range_DateTime" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}RangeDateTimeType" maxOccurs="unbounded"/>
 *           &lt;element name="Single_DateTime" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}DateOrTimeOrEnumType" maxOccurs="unbounded"/>
 *           &lt;element name="Periodic_DateTime" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}PeriodicDateTimeType" maxOccurs="unbounded"/>
 *           &lt;element name="Paleo_DateTime" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}PaleoDateTimeType" maxOccurs="unbounded"/>
 *         &lt;/choice>
 *         &lt;element name="Temporal_Info" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}TemporalInfoType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemporalCoverageType", propOrder = {
    "timeType",
    "dateType",
    "temporalRangeType",
    "precisionOfSeconds",
    "endsAtPresentFlag",
    "rangeDateTime",
    "singleDateTime",
    "periodicDateTime",
    "paleoDateTime",
    "temporalInfo"
})
public class TemporalCoverageType {

    @XmlElement(name = "Time_Type")
    protected String timeType;
    @XmlElement(name = "Date_Type")
    protected String dateType;
    @XmlElement(name = "Temporal_Range_Type")
    protected String temporalRangeType;
    @XmlElement(name = "Precision_Of_Seconds")
    protected Integer precisionOfSeconds;
    @XmlElement(name = "Ends_At_Present_Flag")
    protected Boolean endsAtPresentFlag;
    @XmlElement(name = "Range_DateTime")
    protected List<RangeDateTimeType> rangeDateTime;
    @XmlElement(name = "Single_DateTime")
    protected List<String> singleDateTime;
    @XmlElement(name = "Periodic_DateTime")
    protected List<PeriodicDateTimeType> periodicDateTime;
    @XmlElement(name = "Paleo_DateTime")
    protected List<PaleoDateTimeType> paleoDateTime;
    @XmlElement(name = "Temporal_Info")
    protected TemporalInfoType temporalInfo;

    public TemporalCoverageType() {

    }

    public TemporalCoverageType(RangeDateTimeType range) {
        if (range != null) {
            this.rangeDateTime = new ArrayList<>();
            this.rangeDateTime.add(range);
        }
    }

    /**
     * Obtient la valeur de la propriété timeType.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTimeType() {
        return timeType;
    }

    /**
     * Définit la valeur de la propriété timeType.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTimeType(String value) {
        this.timeType = value;
    }

    /**
     * Obtient la valeur de la propriété dateType.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDateType() {
        return dateType;
    }

    /**
     * Définit la valeur de la propriété dateType.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDateType(String value) {
        this.dateType = value;
    }

    /**
     * Obtient la valeur de la propriété temporalRangeType.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTemporalRangeType() {
        return temporalRangeType;
    }

    /**
     * Définit la valeur de la propriété temporalRangeType.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTemporalRangeType(String value) {
        this.temporalRangeType = value;
    }

    /**
     * Obtient la valeur de la propriété precisionOfSeconds.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getPrecisionOfSeconds() {
        return precisionOfSeconds;
    }

    /**
     * Définit la valeur de la propriété precisionOfSeconds.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setPrecisionOfSeconds(Integer value) {
        this.precisionOfSeconds = value;
    }

    /**
     * Obtient la valeur de la propriété endsAtPresentFlag.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isEndsAtPresentFlag() {
        return endsAtPresentFlag;
    }

    /**
     * Définit la valeur de la propriété endsAtPresentFlag.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setEndsAtPresentFlag(Boolean value) {
        this.endsAtPresentFlag = value;
    }

    /**
     * Gets the value of the rangeDateTime property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rangeDateTime property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRangeDateTime().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RangeDateTimeType }
     *
     *
     */
    public List<RangeDateTimeType> getRangeDateTime() {
        if (rangeDateTime == null) {
            rangeDateTime = new ArrayList<RangeDateTimeType>();
        }
        return this.rangeDateTime;
    }

    /**
     * Gets the value of the singleDateTime property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the singleDateTime property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSingleDateTime().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getSingleDateTime() {
        if (singleDateTime == null) {
            singleDateTime = new ArrayList<String>();
        }
        return this.singleDateTime;
    }

    /**
     * Gets the value of the periodicDateTime property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the periodicDateTime property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPeriodicDateTime().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PeriodicDateTimeType }
     *
     *
     */
    public List<PeriodicDateTimeType> getPeriodicDateTime() {
        if (periodicDateTime == null) {
            periodicDateTime = new ArrayList<PeriodicDateTimeType>();
        }
        return this.periodicDateTime;
    }

    /**
     * Gets the value of the paleoDateTime property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paleoDateTime property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaleoDateTime().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaleoDateTimeType }
     *
     *
     */
    public List<PaleoDateTimeType> getPaleoDateTime() {
        if (paleoDateTime == null) {
            paleoDateTime = new ArrayList<PaleoDateTimeType>();
        }
        return this.paleoDateTime;
    }

    /**
     * Obtient la valeur de la propriété temporalInfo.
     *
     * @return
     *     possible object is
     *     {@link TemporalInfoType }
     *
     */
    public TemporalInfoType getTemporalInfo() {
        return temporalInfo;
    }

    /**
     * Définit la valeur de la propriété temporalInfo.
     *
     * @param value
     *     allowed object is
     *     {@link TemporalInfoType }
     *
     */
    public void setTemporalInfo(TemporalInfoType value) {
        this.temporalInfo = value;
    }

}
