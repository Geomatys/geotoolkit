/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.metadata.iso;

import java.net.URI;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.ApplicationSchemaInformation;
import org.opengis.metadata.citation.Citation;


/**
 * Information about the application schema used to build the dataset.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@Deprecated
@ThreadSafe
@XmlType(name = "MD_ApplicationSchemaInformation_Type", propOrder={
    "name",
    "schemaLanguage",
    "constraintLanguage",
    "schemaAscii",
    "graphicsFile",
    "softwareDevelopmentFile",
    "softwareDevelopmentFileFormat"
})
@XmlRootElement(name = "MD_ApplicationSchemaInformation")
public class DefaultApplicationSchemaInformation extends MetadataEntity
        implements ApplicationSchemaInformation
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -3109191272905767382L;

    /**
     * Name of the application schema used.
     */
    private Citation name;

    /**
     * Identification of the schema language used.
     */
    private String schemaLanguage;

    /**
     * Formal language used in Application Schema.
     */
    private String constraintLanguage;

    /**
     * Full application schema given as an ASCII file.
     */
    private URI schemaAscii;

    /**
     * Full application schema given as a graphics file.
     */
    private URI graphicsFile;

    /**
     * Full application schema given as a software development file.
     */
    private URI softwareDevelopmentFile;

    /**
     * Software dependent format used for the application schema software dependent file.
     */
    private String softwareDevelopmentFileFormat;

    /**
     * Construct an initially empty application schema information.
     */
    public DefaultApplicationSchemaInformation() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultApplicationSchemaInformation(final ApplicationSchemaInformation source) {
        super(source);
    }

    /**
     * Creates a application schema information initialized to the specified values.
     *
     * @param name               The name of the application schema used.
     * @param schemaLanguage     The the identification of the schema language used.
     * @param constraintLanguage The formal language used in application schema.
     */
    public DefaultApplicationSchemaInformation(final Citation name,
                                               final String schemaLanguage,
                                               final String constraintLanguage)
    {
        setName              (name);
        setSchemaLanguage    (schemaLanguage);
        setConstraintLanguage(constraintLanguage);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultApplicationSchemaInformation castOrCopy(final ApplicationSchemaInformation object) {
        return (object == null) || (object instanceof DefaultApplicationSchemaInformation)
                ? (DefaultApplicationSchemaInformation) object : new DefaultApplicationSchemaInformation(object);
    }

    /**
     * Name of the application schema used.
     */
    @Override
    @XmlElement(name = "name", required = true)
    public synchronized Citation getName() {
        return name;
    }

    /**
     * Sets the name of the application schema used.
     *
     * @param newValue The new name.
     */
    public synchronized void setName(final Citation newValue) {
        checkWritePermission();
        name = newValue;
    }

    /**
     * Identification of the schema language used.
     */
    @Override
    @XmlElement(name = "schemaLanguage", required = true)
    public synchronized String getSchemaLanguage() {
        return schemaLanguage;
    }

    /**
     * Sets the identification of the schema language used.
     *
     * @param newValue The new schema language.
     */
    public synchronized void setSchemaLanguage(final String newValue) {
        checkWritePermission();
        schemaLanguage = newValue;
    }

    /**
     * Formal language used in Application Schema.
     */
    @Override
    @XmlElement(name = "constraintLanguage", required = true)
    public synchronized String getConstraintLanguage()  {
        return constraintLanguage;
    }

    /**
     * Sets the formal language used in application schema.
     *
     * @param newValue The new constraint language.
     */
    public synchronized void setConstraintLanguage(final String newValue) {
        checkWritePermission();
        constraintLanguage = newValue;
    }

    /**
     * Full application schema given as an ASCII file.
     */
    @Override
    @XmlElement(name = "schemaAscii")
    public synchronized URI getSchemaAscii()  {
        return schemaAscii;
    }

    /**
     * Sets the full application schema given as an ASCII file.
     *
     * @param newValue The new ASCII file.
     */
    public synchronized void setSchemaAscii(final URI newValue) {
        checkWritePermission();
        schemaAscii = newValue;
    }

    /**
     * Full application schema given as a graphics file.
     */
    @Override
    @XmlElement(name = "graphicsFile")
    public synchronized URI getGraphicsFile()  {
        return graphicsFile;
    }

    /**
     * Sets the full application schema given as a graphics file.
     *
     * @param newValue The new graphics file.
     */
    public synchronized void setGraphicsFile(final URI newValue) {
        checkWritePermission();
        graphicsFile = newValue;
    }

    /**
     * Full application schema given as a software development file.
     */
    @Override
    @XmlElement(name = "softwareDevelopmentFile")
    public synchronized URI getSoftwareDevelopmentFile()  {
        return softwareDevelopmentFile;
    }

    /**
     * Sets the full application schema given as a software development file.
     *
     * @param newValue The new software development file.
     */
    public synchronized void setSoftwareDevelopmentFile(final URI newValue) {
        checkWritePermission();
        softwareDevelopmentFile = newValue;
    }

    /**
     * Software dependent format used for the application schema software dependent file.
     */
    @Override
    @XmlElement(name = "softwareDevelopmentFileFormat")
    public synchronized String getSoftwareDevelopmentFileFormat()  {
        return softwareDevelopmentFileFormat;
    }

    /**
     * Sets the software dependent format used for the application schema software dependent file.
     *
     * @param newValue The new software development file format.
     */
    public synchronized void setSoftwareDevelopmentFileFormat(final String newValue) {
        checkWritePermission();
        softwareDevelopmentFileFormat = newValue;
    }
}
