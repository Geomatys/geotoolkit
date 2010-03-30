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
package org.geotoolkit.swe.xml.v101;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractDataRecord;

/**
 *
 * @version $Id:
 * @author Guilhem Legal
 * @module pending
 */
@XmlSeeAlso({SimpleDataRecordEntry.class})
@XmlType(name="AbstractDataRecord")
public class AbstractDataRecordEntry extends AbstractDataComponentEntry implements AbstractDataRecord {
    
    /**
     * constructor used by JAXB.
     */
    public AbstractDataRecordEntry() {}
            
    /**
     * super-constructor called by sub-classes.
     */
    public AbstractDataRecordEntry(final String id, final String definition, boolean fixed) {
        super(id, definition, fixed);
    }

    /**
     * super-constructor called by sub-classes.
     */
    public AbstractDataRecordEntry(final String definition) {
        super(definition);
    }

    /**
     * super-constructor called by sub-classes.
     */
    public AbstractDataRecordEntry(final AbstractDataRecord record) {
        super(record);
    }
    
}
