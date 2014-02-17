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
package org.geotoolkit.swe.xml;

import java.util.Collection;
import org.opengis.annotation.UML;
import static org.opengis.annotation.Specification.*;
import static org.opengis.annotation.Obligation.*;


/**
 * Implementation of ISO-11404 Record datatype that takes only simple scalars (i.e. no data aggregates).
 * SimpleDataRecord is a data-type so usually appears "by value" rather than by reference.
 *
 * @version $Id:
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
@UML(identifier="SimpleDataRecord", specification=UNSPECIFIED)
public interface SimpleDataRecord extends AbstractDataRecord {
    
    /**
     * this field is restricted to AnyScalar value.
     */
    Collection<? extends AnyScalar> getField();
    
}
