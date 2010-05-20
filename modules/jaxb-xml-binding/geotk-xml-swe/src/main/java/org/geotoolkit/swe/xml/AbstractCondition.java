/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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


package org.geotoolkit.swe.xml;

/**
 *
 * @author Guilhem Legal 
 */
public interface AbstractCondition {

    AbstractCount getCount();

    Quantity getQuantity();

    AbstractTime getTime();

    AbstractBoolean getBoolean();

    AbstractCategory getCategory();

    AbstractText getText();

    AbstractQuantityRange getQuantityRange();

    AbstractCountRange getCountRange();

    AbstractTimeRange getTimeRange();

    /**
     * Gets the value of the abstractDataRecord property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EnvelopeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeoLocationArea }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConditionalDataType }{@code >}
     *     {@link JAXBElement }{@code <}{@link NormalizedCurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SimpleDataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PositionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConditionalValueType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
     *
     */
    public AbstractDataRecord getAbstractDataRecord();

    /**
     * Gets the value of the abstractDataArray property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractDataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CurveType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SquareMatrixType }{@code >}
     *
     */
    public AbstractDataArray getAbstractDataArray();

    String getRemoteSchema();

    String getType();

    String getName();

    String getHref();

    String getRole();

    String getArcrole();

    String getTitle();

    String getShow();

    String getActuate();
}
