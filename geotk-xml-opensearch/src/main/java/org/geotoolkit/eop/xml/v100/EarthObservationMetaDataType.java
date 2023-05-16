/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019
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


package org.geotoolkit.eop.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractMetaDataType;


/**
 * <p>Classe Java pour EarthObservationMetaDataType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="EarthObservationMetaDataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractMetaDataType">
 *       &lt;sequence>
 *         &lt;element ref="{http://earth.esa.int/eop}identifier"/>
 *         &lt;element ref="{http://earth.esa.int/eop}doi" minOccurs="0"/>
 *         &lt;element ref="{http://earth.esa.int/eop}parentIdentifier" minOccurs="0"/>
 *         &lt;element ref="{http://earth.esa.int/eop}acquisitionType"/>
 *         &lt;element ref="{http://earth.esa.int/eop}acquisitionSubType" minOccurs="0"/>
 *         &lt;element ref="{http://earth.esa.int/eop}productType"/>
 *         &lt;element ref="{http://earth.esa.int/eop}status"/>
 *         &lt;element name="downlinkedTo" type="{http://earth.esa.int/eop}DownlinkInformationArrayPropertyType" minOccurs="0"/>
 *         &lt;element name="archivedIn" type="{http://earth.esa.int/eop}ArchivingInformationArrayPropertyType" minOccurs="0"/>
 *         &lt;element ref="{http://earth.esa.int/eop}imageQualityDegradation" minOccurs="0"/>
 *         &lt;element ref="{http://earth.esa.int/eop}imageQualityDegradationQuotationMode" minOccurs="0"/>
 *         &lt;element ref="{http://earth.esa.int/eop}histograms" minOccurs="0"/>
 *         &lt;element ref="{http://earth.esa.int/eop}composedOf" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://earth.esa.int/eop}subsetOf" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://earth.esa.int/eop}linkedWith" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="processing" type="{http://earth.esa.int/eop}ProcessingInformationPropertyType" minOccurs="0"/>
 *         &lt;element name="vendorSpecific" type="{http://earth.esa.int/eop}SpecificInformationArrayPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EarthObservationMetaDataType")
public class EarthObservationMetaDataType
    extends AbstractMetaDataType
{


}
