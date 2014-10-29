/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2014, Geomatys
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
package org.geotoolkit.temporal.reference.xmlAdapter;

import javax.xml.bind.annotation.XmlElement;
import org.apache.sis.internal.jaxb.gco.PropertyType;
import org.apache.sis.xml.Namespaces;
import org.geotoolkit.temporal.reference.DefaultCalendar;
import org.opengis.temporal.Calendar;

/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Remi Marechal (Geomatys).
 * @version 4.0
 * @since   4.0
 */
public class CalendarAdapter extends PropertyType<CalendarAdapter, Calendar>{

    /**
     * Empty constructor for JAXB only.
     */
    public CalendarAdapter() {
    }
    
    /**
     * Constructor for the {@link #wrap} method only.
     */
    private CalendarAdapter(final Calendar calendar) {
        super(calendar);
    }
    
    /**
     * Invoked by JAXB at marshalling time for getting the actual element to write
     * inside the {@code <gml:Calendar>} XML element.
     * This is the value or a copy of the value given in argument to the {@code wrap} method.
     *
     * @return The element to be marshalled.
     */
    @XmlElement(name = "TimeCalendar", namespace = Namespaces.GML)
    public DefaultCalendar getElement() {
        return DefaultCalendar.castOrCopy(metadata);
    }
    
    /**
     * Returns the GeoAPI interface which is bound by this adapter.
     * This method is indirectly invoked by the private constructor
     * below, so it shall not depend on the state of this object.
     *
     * @return {@code Calendar.class}
     */
    @Override
    protected Class<Calendar> getBoundType() {
        return Calendar.class;
    }

    /**
     * Invoked by {@link PropertyType} at marshalling time for wrapping the given value
     * in a {@code <gml:Calendar>} XML element.
     *
     * @param  instant The element to marshall.
     * @return A {@code PropertyType} wrapping the given the element.
     */
    @Override
    protected CalendarAdapter wrap(Calendar cal) {
        return new CalendarAdapter(cal);
    }
    
    /**
     * Invoked by JAXB at unmarshalling time for storing the result temporarily.
     *
     * @param calendar The unmarshalled element.
     */
    public void setElement(final DefaultCalendar calendar) {
        metadata = calendar;
    }
}
