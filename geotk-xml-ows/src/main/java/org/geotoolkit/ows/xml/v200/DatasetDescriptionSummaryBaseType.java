/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * Typical dataset metadata in typical Contents section of
 *       an OWS service metadata (Capabilities) document. This type shall be
 *       extended and/or restricted if needed for specific OWS use, to include
 *       the specific Dataset description metadata needed.
 *
 * <p>Java class for DatasetDescriptionSummaryBaseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DatasetDescriptionSummaryBaseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/2.0}DescriptionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}WGS84BoundingBox" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Identifier" type="{http://www.opengis.net/ows/2.0}CodeType"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}BoundingBox" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}DatasetDescriptionSummary" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatasetDescriptionSummaryBaseType", propOrder = {
    "wgs84BoundingBox",
    "identifier",
    "boundingBox",
    "metadata",
    "datasetDescriptionSummary"
})
public class DatasetDescriptionSummaryBaseType extends DescriptionType {

    @XmlElement(name = "WGS84BoundingBox")
    private List<WGS84BoundingBoxType> wgs84BoundingBox;
    @XmlElement(name = "Identifier", required = true)
    private CodeType identifier;
    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows/2.0", type = JAXBElement.class)
    private List<JAXBElement<? extends BoundingBoxType>> boundingBox;
    @XmlElementRef(name = "Metadata", namespace = "http://www.opengis.net/ows/2.0", type = JAXBElement.class)
    private List<JAXBElement<? extends MetadataType>> metadata;
    @XmlElement(name = "DatasetDescriptionSummary")
    private List<DatasetDescriptionSummaryBaseType> datasetDescriptionSummary;

    /**
     * Unordered list of zero or more minimum bounding
     *               rectangles surrounding coverage data, using the WGS 84 CRS with
     *               decimal degrees and longitude before latitude. If no WGS 84
     *               bounding box is recorded for a coverage, any such bounding boxes
     *               recorded for a higher level in a hierarchy of datasets shall
     *               apply to this coverage. If WGS 84 bounding box(es) are recorded
     *               for a coverage, any such bounding boxes recorded for a higher
     *               level in a hierarchy of datasets shall be ignored. For each
     *               lowest-level coverage in a hierarchy, at least one applicable
     *               WGS84BoundingBox shall be either recorded or inherited, to
     *               simplify searching for datasets that might overlap a specified
     *               region. If multiple WGS 84 bounding boxes are included, this
     *               shall be interpreted as the union of the areas of these bounding
     *               boxes.Gets the value of the wgs84BoundingBox property.
     *
     */
    public List<WGS84BoundingBoxType> getWGS84BoundingBox() {
        if (wgs84BoundingBox == null) {
            wgs84BoundingBox = new ArrayList<WGS84BoundingBoxType>();
        }
        return this.wgs84BoundingBox;
    }

    /**
     * Gets the value of the identifier property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setIdentifier(CodeType value) {
        this.identifier = value;
    }

    /**
     * Unordered list of zero or more minimum bounding
     *               rectangles surrounding coverage data, in AvailableCRSs. Zero or
     *               more BoundingBoxes are allowed in addition to one or more
     *               WGS84BoundingBoxes to allow more precise specification of the
     *               Dataset area in AvailableCRSs. These Bounding Boxes shall not
     *               use any CRS not listed as an AvailableCRS. However, an
     *               AvailableCRS can be listed without a corresponding Bounding Box.
     *               If no such bounding box is recorded for a coverage, any such
     *               bounding boxes recorded for a higher level in a hierarchy of
     *               datasets shall apply to this coverage. If such bounding box(es)
     *               are recorded for a coverage, any such bounding boxes recorded
     *               for a higher level in a hierarchy of datasets shall be ignored.
     *               If multiple bounding boxes are included with the same CRS, this
     *               shall be interpreted as the union of the areas of these bounding
     *               boxes.Gets the value of the boundingBox property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link BoundingBoxType }{@code >}
     * {@link JAXBElement }{@code <}{@link WGS84BoundingBoxType }{@code >}
     *
     *
     */
    public List<JAXBElement<? extends BoundingBoxType>> getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new ArrayList<>();
        }
        return this.boundingBox;
    }

    @Override
    public List<MetadataType> getMetadata() {
        final List<MetadataType> result = new ArrayList<>();
        if (metadata != null) {
            for (JAXBElement<? extends MetadataType> meta : metadata) {
                result.add(meta.getValue());
            }
        }
        return result;
    }
    /**
     * Optional unordered list of additional metadata
     *               about this dataset. A list of optional metadata elements for
     *               this dataset description could be specified in the
     *               Implementation Specification for this service.Gets the value of the metadata property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link MetadataType }{@code >}
     * {@link JAXBElement }{@code <}{@link AdditionalParametersType }{@code >}
     *
     *
     */
    public List<JAXBElement<? extends MetadataType>> getJbMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return this.metadata;
    }

    /**
     * Metadata describing zero or more unordered
     *               subsidiary datasets available from this server.Gets the value of the datasetDescriptionSummary property.
     *
     */
    public List<DatasetDescriptionSummaryBaseType> getDatasetDescriptionSummary() {
        if (datasetDescriptionSummary == null) {
            datasetDescriptionSummary = new ArrayList<DatasetDescriptionSummaryBaseType>();
        }
        return this.datasetDescriptionSummary;
    }

}
