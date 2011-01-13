/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso.identification;

import java.net.URI;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.opengis.util.InternationalString;
import org.opengis.metadata.identification.BrowseGraphic;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.MetadataEntity;
import org.geotoolkit.internal.jaxb.text.URINameAdapter;


/**
 * Graphic that provides an illustration of the dataset (should include a legend for the graphic).
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
@ThreadSafe
@XmlType(propOrder={
    "fileName",
    "fileDescription",
    "fileType"
})
@XmlRootElement(name = "MD_BrowseGraphic")
public class DefaultBrowseGraphic extends MetadataEntity implements BrowseGraphic {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 1715873406472953616L;

    /**
     * Name of the file that contains a graphic that provides an illustration of the dataset.
     */
    private URI fileName;

    /**
     * Text description of the illustration.
     */
    private InternationalString fileDescription;

    /**
     * Format in which the illustration is encoded.
     * Examples: CGM, EPS, GIF, JPEG, PBM, PS, TIFF, XWD.
     */
    private String fileType;

    /**
     * Constructs an initially empty browse graphic.
     */
    public DefaultBrowseGraphic() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultBrowseGraphic(final BrowseGraphic source) {
        super(source);
    }

    /**
     * Creates a browse graphics initialized to the specified URI.
     *
     * @param fileName The name of the file that contains a graphic.
     */
    public DefaultBrowseGraphic(final URI fileName) {
        setFileName(fileName);
    }

    /**
     * Returns the name of the file that contains a graphic that provides an illustration of the dataset.
     */
    @Override
    @XmlJavaTypeAdapter(URINameAdapter.class)
    @XmlElement(name = "fileName")
    public synchronized URI getFileName() {
        return fileName;
    }

    /**
     * Sets the name of the file that contains a graphic that provides an illustration of the
     * dataset.
     *
     * @param newValue The new filename.
     */
    public synchronized void setFileName(final URI newValue) {
        checkWritePermission();
        fileName = newValue;
    }

    /**
     * Returns the text description of the illustration.
     */
    @Override
    @XmlElement(name = "fileDescription")
    public synchronized InternationalString getFileDescription() {
        return fileDescription;
    }

    /**
     * Sets the text description of the illustration.
     *
     * @param newValue The new file description.
     */
    public synchronized void setFileDescription(final InternationalString newValue)  {
        checkWritePermission();
        fileDescription = newValue;
    }

    /**
     * Format in which the illustration is encoded.
     * Examples: CGM, EPS, GIF, JPEG, PBM, PS, TIFF, XWD.
     */
    @Override
    @XmlElement(name = "fileType")
    public synchronized String getFileType() {
        return fileType;
    }

    /**
     * Sets the format in which the illustration is encoded.
     *
     * @param newValue The new fime type.
     */
    public synchronized void setFileType(final String newValue)  {
        checkWritePermission();
        fileType = newValue;
    }
}
