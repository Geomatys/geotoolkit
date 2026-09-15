/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
 */
package org.geotoolkit.ogcwxs.request.catalog;

import java.util.List;
import org.geotoolkit.csw.xml.CSWVersion;
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * To retrieve one or more records (metadata) by their identifier.
 *
 * <blockquote><q>
 * The getResourceById operation is inherited from OWS Common and supports the request of one
 * or more resources – in this case full, structured metadata records – from the catalogue.
 * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_7">OGC 12-168r6, &sect;7.2.3.3</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 * @see #version(CSWVersion)
 * @see #ids(List)
 * @see #elementSetName(ElementSetType)
 * @see #outputSchema(String)
 * @see #outputFormat(String)
 */
public final class GetRecordById extends RequestParameters {

    private CSWVersion version;
    private List<String> ids;
    private ElementSetType elementSetName;
    private String outputSchema;
    private String outputFormat;

    /**
     * The CSW version to request. Defaults to {@link CSWVersion#v300} if left {@code null}.
     *
     * @return the version
     * @see #version(CSWVersion)
     */
    public CSWVersion getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     * @see #version(CSWVersion)
     */
    public void setVersion(CSWVersion version) {
        this.version = version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     * @see #setVersion(CSWVersion)
     */
    public GetRecordById version(CSWVersion version) {
        setVersion(version);
        return this;
    }

    /**
     * Identifiers of the records to retrieve.
     *
     * <p>CSW 2.x supports several identifiers in a single request; CSW 3.0 only supports one
     * ({@link org.geotoolkit.csw.xml.CswXmlFactory#createGetRecordById} keeps the first identifier
     * of this list for that version).</p>
     *
     * <blockquote><q>
     * Records are discovered through the query operation whose response includes the
     * identifier(s) of the record(s) meeting the conditions of the query. These identifiers
     * are passed via the getResourceById to retrieve records from the catalogue in bulk.
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_7">OGC 12-168r6, &sect;7.2.3.3</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>CSW 2.x: one or more identifiers accepted.</li>
     * <li>CSW 3.0: only the first identifier of the list is used.</li>
     * </ul>
     *
     * @return the ids
     * @see #ids(List)
     */
    public List<String> getIds() {
        return ids;
    }

    /**
     * @param ids the ids to set
     * @see #ids(List)
     */
    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    /**
     * @param ids the ids to set
     * @see #getIds()
     * @see #setIds(List)
     */
    public GetRecordById ids(List<String> ids) {
        setIds(ids);
        return this;
    }

    /**
     * Level of detail of the returned records. (optional, server-dependent default)
     *
     * @return the elementSetName
     * @see #elementSetName(ElementSetType)
     */
    public ElementSetType getElementSetName() {
        return elementSetName;
    }

    /**
     * @param elementSetName the elementSetName to set
     * @see #elementSetName(ElementSetType)
     */
    public void setElementSetName(ElementSetType elementSetName) {
        this.elementSetName = elementSetName;
    }

    /**
     * @param elementSetName the elementSetName to set
     * @see #getElementSetName()
     * @see #setElementSetName(ElementSetType)
     */
    public GetRecordById elementSetName(ElementSetType elementSetName) {
        setElementSetName(elementSetName);
        return this;
    }

    /**
     * Schema in which the records are described. (optional, server-dependent default)
     *
     * @return the outputSchema
     * @see #outputSchema(String)
     */
    public String getOutputSchema() {
        return outputSchema;
    }

    /**
     * @param outputSchema the outputSchema to set
     * @see #outputSchema(String)
     */
    public void setOutputSchema(String outputSchema) {
        this.outputSchema = outputSchema;
    }

    /**
     * @param outputSchema the outputSchema to set
     * @see #getOutputSchema()
     * @see #setOutputSchema(String)
     */
    public GetRecordById outputSchema(String outputSchema) {
        setOutputSchema(outputSchema);
        return this;
    }

    /**
     * Requested output format. (optional, server-dependent default)
     *
     * @return the outputFormat
     * @see #outputFormat(String)
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat the outputFormat to set
     * @see #outputFormat(String)
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * @param outputFormat the outputFormat to set
     * @see #getOutputFormat()
     * @see #setOutputFormat(String)
     */
    public GetRecordById outputFormat(String outputFormat) {
        setOutputFormat(outputFormat);
        return this;
    }

}
