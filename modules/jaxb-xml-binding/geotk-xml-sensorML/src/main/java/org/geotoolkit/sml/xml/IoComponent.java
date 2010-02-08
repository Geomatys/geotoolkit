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
package org.geotoolkit.sml.xml;

import javax.xml.bind.JAXBElement;
import org.geotoolkit.swe.xml.AbstractCategory;
import org.geotoolkit.swe.xml.AbstractCount;
import org.geotoolkit.swe.xml.AbstractCountRange;
import org.geotoolkit.swe.xml.AbstractBoolean;
import org.geotoolkit.swe.xml.AbstractDataArray;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.AbstractObservableProperty;
import org.geotoolkit.swe.xml.AbstractQuantityRange;
import org.geotoolkit.swe.xml.AbstractText;
import org.geotoolkit.swe.xml.AbstractTime;
import org.geotoolkit.swe.xml.AbstractTimeRange;
import org.geotoolkit.swe.xml.Quantity;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public interface IoComponent {

    public AbstractCount getCount();

    public Quantity getQuantity();

    public AbstractTime getTime();

    public AbstractBoolean getBoolean();

    public AbstractCategory getCategory();

    public AbstractText getText();

    public AbstractQuantityRange getQuantityRange();

    public AbstractCountRange getCountRange();

    public AbstractTimeRange getTimeRange();

    public JAXBElement<? extends AbstractDataRecord> getAbstractDataRecord();

    public JAXBElement<? extends AbstractDataArray> getAbstractDataArray();

    public AbstractObservableProperty getObservableProperty();

    public String getName();

    public String getRemoteSchema();

    public String getActuate();

    public String getArcrole();

    public String getHref();

    public String getRole();

    public String getShow();

    public String getTitle();

    public String getType();
}
