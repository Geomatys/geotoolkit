/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml.model;

import java.net.URI;
import java.util.List;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.kml.xsd.Cdata;
import org.geotoolkit.data.xal.model.AddressDetails;

/**
 * <p>This interface maps AbstractFeatureGroup element.</p>
 *
 * <pre>
 * &lt;element name="AbstractFeatureGroup" type="kml:AbstractFeatureType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * 
 * &lt;complexType name="AbstractFeatureType" abstract="true">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:name" minOccurs="0"/>
 *              &lt;element ref="kml:visibility" minOccurs="0"/>
 *              &lt;element ref="kml:open" minOccurs="0"/>
 *              &lt;element ref="atom:author" minOccurs="0"/>
 *              &lt;element ref="atom:link" minOccurs="0"/>
 *              &lt;element ref="kml:address" minOccurs="0"/>
 *              &lt;element ref="xal:AddressDetails" minOccurs="0"/>
 *              &lt;element ref="kml:phoneNumber" minOccurs="0"/>
 *              &lt;choice>
 *                  &lt;annotation>
 *                      &lt;documentation>Snippet deprecated in 2.2&lt;/documentation>
 *                  &lt;/annotation>
 *                  &lt;element ref="kml:Snippet" minOccurs="0"/>
 *                  &lt;element ref="kml:snippet" minOccurs="0"/>
 *              &lt;/choice>
 *              &lt;element ref="kml:description" minOccurs="0"/>
 *              &lt;element ref="kml:AbstractViewGroup" minOccurs="0"/>
 *              &lt;element ref="kml:AbstractTimePrimitiveGroup" minOccurs="0"/>
 *              &lt;element ref="kml:styleUrl" minOccurs="0"/>
 *              &lt;element ref="kml:AbstractStyleSelectorGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:Region" minOccurs="0"/>
 *              &lt;choice>
 *                  &lt;annotation>
 *                      &lt;documentation>Metadata deprecated in 2.2&lt;/documentation>
 *                  &lt;/annotation>
 *                  &lt;element ref="kml:Metadata" minOccurs="0"/>
 *                  &lt;element ref="kml:ExtendedData" minOccurs="0"/>
 *              &lt;/choice>
 *              &lt;element ref="kml:AbstractFeatureSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:AbstractFeatureObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="AbstractFeatureObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * &lt;element name="AbstractFeatureSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 *</pre>
 *
 * @author Samuel Andrés
 */
public interface AbstractFeature extends AbstractObject {

    /**
     *
     * @return The AbstractFeature name.
     */
    public String getName();

    /**
     *
     * @return The AbstractFeature visibility.
     */
    public boolean getVisibility();

    /**
     *
     * @return The AbstractFeature open.
     */
    public boolean getOpen();

    /**
     *
     * @return The AbstractFeature author.
     */
    public AtomPersonConstruct getAuthor();

    /**
     *
     * @return The AbstractFeature link.
     */
    public AtomLink getAtomLink();

    /**
     *
     * @return The AbstractFeature address.
     */
    public String getAddress();

    /**
     *
     * @return The AbstractFeature address details.
     */
    public AddressDetails getAddressDetails();

    /**
     *
     * @return The AbstractFeature phone number.
     */
    public String getPhoneNumber();

    /**
     *
     * @return The AbstractFeature snippet.
     */
    public Object getSnippet();

    /**
     *
     * @return The AbstractFeature description.
     */
    public Object getDescription();

    /**
     *
     * @return The AbstractFeature view.
     */
    public AbstractView getView();

    /**
     *
     * @return The AbstractFeature time primitive.
     */
    public AbstractTimePrimitive getTimePrimitive();

    /**
     *
     * @return The AbstractFeature style url.
     */
    public URI getStyleUrl();

    /**
     *
     * @return The AbstractFeature list of style selectors.
     */
    public List<AbstractStyleSelector> getStyleSelectors();

    /**
     *
     * @return The AbstractFeature region.
     */
    public Region getRegion();

    /**
     *
     * @return The AbstractFeature extended data.
     */
    public Object getExtendedData();

    /**
     * 
     * @param name
     */
    public void setName(String name);

    /**
     *
     * @param visibility
     */
    public void setVisibility(boolean visibility);

    /**
     *
     * @param open
     */
    public void setOpen(boolean open);

    /**
     *
     * @param author
     */
    public void setAuthor(AtomPersonConstruct author);

    /**
     *
     * @param link
     */
    public void setAtomLink(AtomLink atomLink);

    /**
     *
     * @param address
     */
    public void setAddress(String address);

    /**
     *
     * @param addressDetails
     */
    public void setAddressDetails(AddressDetails addressDetails);

    /**
     *
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber);

    /**
     *
     * @param snippet
     */
    public void setSnippet(String snippet);

    /**
     * 
     * @param snippet
     */
    public void setSnippet(Cdata snippet);

    /**
     *
     * @param snippet
     * @deprecated
     */
    @Deprecated
    public void setSnippet(Snippet snippet);

    /**
     *
     * @param description
     */
    public void setDescription(Object description);

    /**
     *
     * @param view
     */
    public void setView(AbstractView view);

    /**
     *
     * @param timePrimitive
     */
    public void setTimePrimitive(AbstractTimePrimitive timePrimitive);

    /**
     *
     * @param styleUrl
     */
    public void setStyleUrl(URI styleUrl);

    /**
     *
     * @param styleSelectors
     */
    public void setStyleSelectors(List<AbstractStyleSelector> styleSelectors);

    /**
     *
     * @param region
     */
    public void setRegion(Region region);

    /**
     *
     * @param extendedData
     */
    public void setExtendedData(ExtendedData extendedData);
    
    /**
     * 
     * @param metaData
     * @deprecated
     */
    @Deprecated
    public void setExtendedData(Metadata metaData);

}
