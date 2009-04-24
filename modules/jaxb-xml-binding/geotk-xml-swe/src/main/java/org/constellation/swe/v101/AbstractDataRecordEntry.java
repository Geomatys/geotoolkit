/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.constellation.swe.v101;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @version $Id:
 * @author Guilhem Legal
 */
@XmlSeeAlso({SimpleDataRecordEntry.class})
@XmlType(name="AbstractDataRecord")
public class AbstractDataRecordEntry extends AbstractDataComponentEntry {
    
    /**
     * constructeur utilisé par jaxB.
     */
    public AbstractDataRecordEntry() {}
            
    /**
     * super-constructeur appellé par les sous-classes.
     */
    public AbstractDataRecordEntry(final String id, final String definition, boolean fixed) {
        super(id, definition, fixed);
    }
    
}
