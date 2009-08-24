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
package org.geotoolkit.metadata.iso.quality;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.distribution.Format;
import org.opengis.metadata.quality.CoverageResult;
import org.opengis.metadata.distribution.DataFile;
import org.opengis.metadata.spatial.SpatialRepresentation;
import org.opengis.metadata.spatial.SpatialRepresentationType;


/**
 * Result of a data quality measure organising the measured values as a coverage.
 *
 * @author Cédric Briançon (Geomatys)
 * @version 3.03
 *
 * @since 3.00
 * @module
 */
@XmlType(propOrder={
    "spatialRepresentationType",
    "resultSpatialRepresentation",
    "resultContentDescription",
    "resultFormat",
    "resultFile"
})
@XmlRootElement(name = "QE_CoverageResult")
public class DefaultCoverageResult extends AbstractResult implements CoverageResult {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -5014701989643853577L;

    /**
     * Method used to spatially represent the coverage result.
     */
    private SpatialRepresentationType spatialRepresentationType;

    /**
     * Provides the digital representation of data quality measures composing the coverage result.
     */
    private SpatialRepresentation resultSpatialRepresentation;

    /**
     * Provides the description of the content of the result coverage, i.e. semantic definition
     * of the data quality measures.
     */
    private CoverageDescription resultContentDescription;

    /**
     * Provides information about the format of the result coverage data.
     */
    private Format resultFormat;

    /**
     * Provides information about the data file containing the result coverage data.
     */
    private DataFile resultFile;

    /**
     * Constructs an initially empty coverage result.
     */
    public DefaultCoverageResult() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     */
    public DefaultCoverageResult(final CoverageResult source) {
        super(source);
    }

    /**
     * Returns the method used to spatially represent the coverage result.
     */
    @Override
    @XmlElement(name = "spatialRepresentationType")
    public synchronized SpatialRepresentationType getSpatialRepresentationType() {
        return spatialRepresentationType;
    }

    /**
     * Sets the method used to spatially represent the coverage result.
     *
     * @param newValue The new spatial representation type value.
     */
    public synchronized void setSpatialRepresentationType(final SpatialRepresentationType newValue) {
        checkWritePermission();
        spatialRepresentationType = newValue;
    }

    /**
     * Returns the digital representation of data quality measures composing the coverage result.
     */
    @Override
    @XmlElement(name = "resultSpatialRepresentation")
    public synchronized SpatialRepresentation getResultSpatialRepresentation() {
        return resultSpatialRepresentation;
    }

    /**
     * Sets the digital representation of data quality measures composing the coverage result.
     *
     * @param newValue The new spatial representation value.
     */
    public synchronized void setResultSpatialRepresentation(final SpatialRepresentation newValue) {
        checkWritePermission();
        resultSpatialRepresentation = newValue;
    }

    /**
     * Returns the description of the content of the result coverage, i.e. semantic definition
     * of the data quality measures.
     */
    @Override
    @XmlElement(name = "resultContentDescription")
    public synchronized CoverageDescription getResultContentDescription() {
        return resultContentDescription;
    }

    /**
     * Sets the description of the content of the result coverage, i.e. semantic definition
     * of the data quality measures.
     *
     * @param newValue The new content description value.
     */
    public synchronized void setResultContentDescription(final CoverageDescription newValue) {
        checkWritePermission();
        resultContentDescription = newValue;
    }

    /**
     * Returns the information about the format of the result coverage data.
     */
    @Override
    @XmlElement(name = "resultFormat")
    public synchronized Format getResultFormat() {
        return resultFormat;
    }

    /**
     * Sets the information about the format of the result coverage data.
     *
     * @param newValue The new result format value.
     */
    public synchronized void setResultFormat(final Format newValue) {
        checkWritePermission();
        resultFormat = newValue;
    }

    /**
     * Returns the information about the data file containing the result coverage data.
     */
    @Override
    @XmlElement(name = "resultFile")
    public synchronized DataFile getResultFile() {
        return resultFile;
    }

    /**
     * Sets the information about the data file containing the result coverage data.
     *
     * @param newValue The new result file value.
     */
    public synchronized void setResultFile(final DataFile newValue) {
        checkWritePermission();
        resultFile = newValue;
    }
}
