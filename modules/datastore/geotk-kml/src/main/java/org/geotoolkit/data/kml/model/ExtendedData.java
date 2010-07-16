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

import java.util.List;

/**
 * <p>This interface maps ExtendedDate element.</p>
 *
 * <pre>
 * &lt;element name="ExtendedData" type="kml:ExtendedDataType"/>
 *
 * &lt;complexType name="ExtendedDataType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:Data" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;element ref="kml:SchemaData" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;any namespace="##other" processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface ExtendedData {

    /**
     *
     * @return
     */
    public List<Data> getDatas();

    /**
     *
     * @return
     */
    public List<SchemaData> getSchemaData();

    /**
     * 
     * @return
     */
    public List<Object> getAnyOtherElements();

    /**
     *
     * @param datas
     */
    public void setDatas(List<Data> datas);

    /**
     *
     * @param schemaDatas
     */
    public void setSchemaData(List<SchemaData> schemaDatas);

    /**
     *
     * @param anyOtherElements
     */
    public void setAnyOtherElements(List<Object> anyOtherElements);
}
