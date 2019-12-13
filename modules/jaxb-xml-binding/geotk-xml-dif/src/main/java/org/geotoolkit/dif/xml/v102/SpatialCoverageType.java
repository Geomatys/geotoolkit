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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *                 Required by UMM-C, DIF, and ECHO.
 *
 *                 | DIF 9            | ECHO 10                      | DIF 10                         | Notes                                                                 |
 *                 | ---------------- | ---------------------------- | ------------------------------ | --------------------------------------------------------------------- |
 *                 |  Southernmost_L**|              -               |               -                | Moved Lat and Long fields to Geometry/Bounding_Rectangle              |
 *                 |         -        | SpatialCoverageType          | Spatial_Coverage_Type          | Added from ECHO Spatial                                               |
 *                 |         -        | GranuleSpatialRepresentation | Granule_Spatial_Representation | Added from ECHO Spatial and made required                             |
 *                 |         -        |              -               | Zone_Identifier                | Added from ECHO HorizontalSpatialDomain                               |
 *                 |         -        |              -               | Geometry                       | Added from ECHO HorizontalSpatialDomain                               |
 *                 |         -        | OrbitParameters              | Orbit_Parameters               | Added from ECHO Spatial                                               |
 *                 |         -        | VerticalSpatialDomain        | Vertical_Spatial_Info          | Added from ECHO, inside Vertical_Spatial_Info, use type               |
 *                 |         -        | SpatialInfo                  | Spatial_Info                   | Added from ECHO fields, composite field                               |
 *
 *                 * Spatial_Coverage_Type : This attribute denotes whether the collection's spatial coverage requires horizontal, vertical, or both in the spatial domain and coordinate system definitions.
 *                 * Granule_Spatial_Representation : Required if mapping granules to a DIF.
 *                 * Zone_Identifier : The appropriate numeric or alpha code used to identify the various zones in this grid coordinate system.
 *                 * Spatial_Info : This composite field stores the reference frame or system from which altitudes (elevations) are measured.
 *                 * todo: future versions should include units for the min and max values.
 *                 (**Southernmost_Latitude, Westernmost_Longitude, ..., all have "ternmost_L" in the middle.)
 *
 *
 *
 * <p>Classe Java pour SpatialCoverageType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="SpatialCoverageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Spatial_Coverage_Type" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}SpatialCoverageTypeEnum" minOccurs="0"/>
 *         &lt;element name="Granule_Spatial_Representation" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}GranuleSpatialRepresentationEnum"/>
 *         &lt;element name="Zone_Identifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Geometry" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}Geometry" minOccurs="0"/>
 *         &lt;element name="Orbit_Parameters" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}OrbitParameters" minOccurs="0"/>
 *         &lt;element name="Vertical_Spatial_Info" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}VerticalSpatialInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Spatial_Info" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}SpatialInfo" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpatialCoverageType", propOrder = {
    "spatialCoverageType",
    "granuleSpatialRepresentation",
    "zoneIdentifier",
    "geometry",
    "orbitParameters",
    "verticalSpatialInfo",
    "spatialInfo"
})
public class SpatialCoverageType {

    @XmlElement(name = "Spatial_Coverage_Type")
    @XmlSchemaType(name = "string")
    protected SpatialCoverageTypeEnum spatialCoverageType;
    @XmlElement(name = "Granule_Spatial_Representation", required = true)
    @XmlSchemaType(name = "string")
    protected GranuleSpatialRepresentationEnum granuleSpatialRepresentation;
    @XmlElement(name = "Zone_Identifier")
    protected String zoneIdentifier;
    @XmlElement(name = "Geometry")
    protected Geometry geometry;
    @XmlElement(name = "Orbit_Parameters")
    protected OrbitParameters orbitParameters;
    @XmlElement(name = "Vertical_Spatial_Info")
    protected List<VerticalSpatialInfo> verticalSpatialInfo;
    @XmlElement(name = "Spatial_Info")
    protected SpatialInfo spatialInfo;

    public SpatialCoverageType() {

    }

    public SpatialCoverageType(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * Obtient la valeur de la propriété spatialCoverageType.
     *
     * @return
     *     possible object is
     *     {@link SpatialCoverageTypeEnum }
     *
     */
    public SpatialCoverageTypeEnum getSpatialCoverageType() {
        return spatialCoverageType;
    }

    /**
     * Définit la valeur de la propriété spatialCoverageType.
     *
     * @param value
     *     allowed object is
     *     {@link SpatialCoverageTypeEnum }
     *
     */
    public void setSpatialCoverageType(SpatialCoverageTypeEnum value) {
        this.spatialCoverageType = value;
    }

    /**
     * Obtient la valeur de la propriété granuleSpatialRepresentation.
     *
     * @return
     *     possible object is
     *     {@link GranuleSpatialRepresentationEnum }
     *
     */
    public GranuleSpatialRepresentationEnum getGranuleSpatialRepresentation() {
        return granuleSpatialRepresentation;
    }

    /**
     * Définit la valeur de la propriété granuleSpatialRepresentation.
     *
     * @param value
     *     allowed object is
     *     {@link GranuleSpatialRepresentationEnum }
     *
     */
    public void setGranuleSpatialRepresentation(GranuleSpatialRepresentationEnum value) {
        this.granuleSpatialRepresentation = value;
    }

    /**
     * Obtient la valeur de la propriété zoneIdentifier.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getZoneIdentifier() {
        return zoneIdentifier;
    }

    /**
     * Définit la valeur de la propriété zoneIdentifier.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setZoneIdentifier(String value) {
        this.zoneIdentifier = value;
    }

    /**
     * Obtient la valeur de la propriété geometry.
     *
     * @return
     *     possible object is
     *     {@link Geometry }
     *
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Définit la valeur de la propriété geometry.
     *
     * @param value
     *     allowed object is
     *     {@link Geometry }
     *
     */
    public void setGeometry(Geometry value) {
        this.geometry = value;
    }

    /**
     * Obtient la valeur de la propriété orbitParameters.
     *
     * @return
     *     possible object is
     *     {@link OrbitParameters }
     *
     */
    public OrbitParameters getOrbitParameters() {
        return orbitParameters;
    }

    /**
     * Définit la valeur de la propriété orbitParameters.
     *
     * @param value
     *     allowed object is
     *     {@link OrbitParameters }
     *
     */
    public void setOrbitParameters(OrbitParameters value) {
        this.orbitParameters = value;
    }

    /**
     * Gets the value of the verticalSpatialInfo property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the verticalSpatialInfo property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVerticalSpatialInfo().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VerticalSpatialInfo }
     *
     *
     */
    public List<VerticalSpatialInfo> getVerticalSpatialInfo() {
        if (verticalSpatialInfo == null) {
            verticalSpatialInfo = new ArrayList<VerticalSpatialInfo>();
        }
        return this.verticalSpatialInfo;
    }

    /**
     * Obtient la valeur de la propriété spatialInfo.
     *
     * @return
     *     possible object is
     *     {@link SpatialInfo }
     *
     */
    public SpatialInfo getSpatialInfo() {
        return spatialInfo;
    }

    /**
     * Définit la valeur de la propriété spatialInfo.
     *
     * @param value
     *     allowed object is
     *     {@link SpatialInfo }
     *
     */
    public void setSpatialInfo(SpatialInfo value) {
        this.spatialInfo = value;
    }

}
