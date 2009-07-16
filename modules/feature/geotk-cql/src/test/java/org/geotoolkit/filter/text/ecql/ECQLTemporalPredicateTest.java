/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.text.ecql;

import org.geotoolkit.filter.text.commons.Language;
import org.geotoolkit.filter.text.cql2.CQLTemporalPredicateTest;


/**
 * ECQL Temporal predicate
 *
 * <p>
 * Executes all cql temporal predicate test using the ECQL parser
 * </p>
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @since 2.6
 */
public class ECQLTemporalPredicateTest extends CQLTemporalPredicateTest {

    public ECQLTemporalPredicateTest() {
        // sets the TXT language used to execute this test case
        super();
        language = Language.ECQL;
    }

}
