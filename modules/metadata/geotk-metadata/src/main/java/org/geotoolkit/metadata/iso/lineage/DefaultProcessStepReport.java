/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.metadata.iso.lineage;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.geotoolkit.metadata.iso.MetadataEntity;
import org.opengis.metadata.lineage.ProcessStepReport;
import org.opengis.util.InternationalString;


/**
 * Report of what occurred during the process step.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@XmlType(propOrder={
    "name",
    "description",
    "fileType"
})
@XmlRootElement(name = "LE_ProcessStepReport")
public class DefaultProcessStepReport extends MetadataEntity implements ProcessStepReport {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -7054783651586763896L;

    /**
     * Name of the processing report.
     */
    private InternationalString name;

    /**
     * Textual description of what occurred during the process step.
     */
    private InternationalString description;

    /**
     * Type of file that contains the processing report.
     */
    private InternationalString fileType;

    /**
     * Constructs an initially empty process step report.
     */
    public DefaultProcessStepReport() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     */
    public DefaultProcessStepReport(final ProcessStepReport source) {
        super(source);
    }

    /**
     * Returns the name of the processing report.
     */
    @Override
    @XmlElement(name = "name")
    public synchronized InternationalString getName() {
        return name;
    }

    /**
     * Sets the name of the processing report.
     *
     * @param newValue The new name value.
     */
    public synchronized void setName(final InternationalString newValue) {
        checkWritePermission();
        name = newValue;
    }

    /**
     * Returns the textual description of what occurred during the process step.
     * {@code null} if unspecified.
     */
    @Override
    @XmlElement(name = "description")
    public synchronized InternationalString getDescription() {
        return description;
    }

    /**
     * Sets the textual description of what occurred during the process step.
     *
     * @param newValue The new description value.
     */
    public synchronized void setDescription(final InternationalString newValue) {
        checkWritePermission();
        description = newValue;
    }

    /**
     * Returns the type of file that contains the processing report. {@code null} if
     * unspecified.
     */
    @Override
    @XmlElement(name = "fileType")
    public synchronized InternationalString getFileType() {
        return fileType;
    }

    /**
     * Sets the type of file that contains the processing report.
     *
     * @param newValue The new file type value.
     */
    public synchronized void setFileType(final InternationalString newValue) {
        checkWritePermission();
        fileType = newValue;
    }
}
