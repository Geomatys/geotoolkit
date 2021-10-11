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
package org.geotoolkit.wmts.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractCapabilitiesBase;
import org.geotoolkit.ows.xml.Sections;
import org.geotoolkit.ows.xml.v110.CapabilitiesBaseType;
import org.geotoolkit.ows.xml.v110.OnlineResourceType;
import org.geotoolkit.ows.xml.v110.OperationsMetadata;
import org.geotoolkit.ows.xml.v110.ServiceIdentification;
import org.geotoolkit.ows.xml.v110.ServiceProvider;
import org.geotoolkit.wmts.xml.WMTSResponse;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}CapabilitiesBaseType">
 *       &lt;sequence>
 *         &lt;element name="Contents" type="{http://www.opengis.net/wmts/1.0}ContentsType" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}Themes" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="WSDL" type="{http://www.opengis.net/ows/1.1}OnlineResourceType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ServiceMetadataURL" type="{http://www.opengis.net/ows/1.1}OnlineResourceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "contents",
    "themes",
    "wsdl",
    "serviceMetadataURL"
})
@XmlRootElement(name = "Capabilities")
public class Capabilities extends CapabilitiesBaseType implements WMTSResponse {

    @XmlElement(name = "Contents")
    private ContentsType contents;
    @XmlElement(name = "Themes")
    private List<Themes> themes;
    @XmlElement(name = "WSDL")
    private List<OnlineResourceType> wsdl;
    @XmlElement(name = "ServiceMetadataURL")
    private List<OnlineResourceType> serviceMetadataURL;

    public Capabilities() {

    }

    public Capabilities(final String version, final String updateSequence) {
        super(null, null, null, version, updateSequence);
    }

    public Capabilities(final ServiceIdentification serviceIdentification, final ServiceProvider serviceProvider,
            final OperationsMetadata operationsMetadata, final String version, final String updateSequence, final ContentsType con, final List<Themes> them) {
        super(serviceIdentification, serviceProvider, operationsMetadata, version, updateSequence);
        this.contents = con;
        this.themes = them;
    }

    /**
     * Gets the value of the contents property.
     *
     * @return
     *     possible object is
     *     {@link ContentsType }
     *
     */
    public ContentsType getContents() {
        return contents;
    }

    /**
     * Sets the value of the contents property.
     *
     * @param value
     *     allowed object is
     *     {@link ContentsType }
     *
     */
    public void setContents(final ContentsType value) {
        this.contents = value;
    }

    @Override
    public void updateURL(String url) {
        super.updateURL(url);
        if (contents != null) {
            for (LayerType layer : contents.getLayers()) {
                if (layer.getResourceURL() != null) {
                    for (URLTemplateType template : layer.getResourceURL()) {
                        if (template.getTemplate() != null) {
                            final String templateURL = template.getTemplate();
                            final int index = templateURL.indexOf(layer.getIdentifier().getValue()) - 1;
                            if (index != -1) {
                                final String s = templateURL.substring(index);
                                template.setTemplate(url.substring(0, url.length() - 1) + s);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Metadata describing a theme hierarchy for the layers
     * Gets the value of the themes property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link Themes }
     *
     */
    public List<Themes> getThemes() {
        if (themes == null) {
            themes = new ArrayList<Themes>();
        }
        return this.themes;
    }

    /**
     * Gets the value of the wsdl property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link OnlineResourceType }
     *
     *
     */
    public List<OnlineResourceType> getWSDL() {
        if (wsdl == null) {
            wsdl = new ArrayList<OnlineResourceType>();
        }
        return this.wsdl;
    }

    /**
     * Gets the value of the serviceMetadataURL property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link OnlineResourceType }
     *
     */
    public List<OnlineResourceType> getServiceMetadataURL() {
        if (serviceMetadataURL == null) {
            serviceMetadataURL = new ArrayList<OnlineResourceType>();
        }
        return this.serviceMetadataURL;
    }

    @Override
    public AbstractCapabilitiesBase applySections(final Sections sections) {
        ServiceIdentification si = null;
        ServiceProvider       sp = null;
        OperationsMetadata    om = null;
        ContentsType        cont = null;
        List<Themes>          th = null;
        if (sections.containsSection("ServiceIdentification") || sections.containsSection("All")) {
            si = getServiceIdentification();
        }
        if (sections.containsSection("ServiceProvider") || sections.containsSection("All")) {
            sp = getServiceProvider();
        }
        if (sections.containsSection("OperationsMetadata") || sections.containsSection("All")) {
            om = getOperationsMetadata();
        }
        if (sections.containsSection("Contents") || sections.containsSection("All")) {
            cont = contents;
        }
        if (sections.containsSection("Themes") || sections.containsSection("All")) {
            th = themes;
        }
        return new Capabilities(si, sp, om, "1.0.0", getUpdateSequence(), cont, th);
    }
}
