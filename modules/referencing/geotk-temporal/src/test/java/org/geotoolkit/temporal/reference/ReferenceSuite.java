/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.temporal.reference;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({org.geotoolkit.temporal.reference.DefaultTemporalReferenceSystemTest.class, org.geotoolkit.temporal.reference.DefaultOrdinalEraTest.class, org.geotoolkit.temporal.reference.DefaultTemporalCoordinateSystemTest.class, org.geotoolkit.temporal.reference.DefaultClockTest.class, org.geotoolkit.temporal.reference.DefaultOrdinalReferenceSystemTest.class, org.geotoolkit.temporal.reference.DefaultCalendarEraTest.class, org.geotoolkit.temporal.reference.DefaultCalendarTest.class})
public class ReferenceSuite {
}
