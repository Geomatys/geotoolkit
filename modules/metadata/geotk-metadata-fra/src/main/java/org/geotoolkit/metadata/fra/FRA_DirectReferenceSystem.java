/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.metadata.fra;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.DefaultReferenceIdentifier;
import org.geotoolkit.internal.jaxb.metadata.ReferenceSystemMetadata;


/**
 * AFNOR extension to ISO {@link ReferenceSystem}.
 * The following schema fragment specifies the expected content contained within this class.
 *
 * {@preformat xml
 *   <complexType name="FRA_DirectReferenceSystem_Type">
 *     <complexContent>
 *       <extension base="{http://www.isotc211.org/2005/gmd}MD_ReferenceSystem_Type">
 *       </extension>
 *     </complexContent>
 *   </complexType>
 * }
 *
 * @author Cédric Briançon (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @version 3.21
 *
 * @since 3.00
 * @module
 */
@XmlType(name = "FRA_DirectReferenceSystem_Type")
@XmlRootElement(name= "FRA_DirectReferenceSystem")
public class FRA_DirectReferenceSystem extends ReferenceSystemMetadata {
    /**
     * For serialization purpose.
     */
    private static final long serialVersionUID = 5184347269686376148L;

    /**
     * Empty constructor for JAXB.
     */
    private FRA_DirectReferenceSystem() {
    }

    /**
     * Creates a new reference system from the given one.
     *
     * @param crs The reference system to partially copy.
     */
    public FRA_DirectReferenceSystem(final ReferenceSystem crs) {
        super(crs);
    }

    /**
     * Creates a new reference system from the given code.
     *
     * @param identifier The reference system identifier.
     */
    public FRA_DirectReferenceSystem(final ReferenceIdentifier identifier) {
        super(identifier);
    }

    /**
     * Creates a new reference system from the specified code and authority.
     *
     * @param authority
     *          Organization or party responsible for definition and maintenance of the code space or code.
     * @param codespace
     *          Name or identifier of the person or organization responsible for namespace.
     *          This is often an abbreviation of the authority name.
     * @param code
     *          Identifier code or name, optionally from a controlled list or pattern defined by a code space.
     */
    public FRA_DirectReferenceSystem(final Citation authority, final String codespace, final String code) {
        super(new DefaultReferenceIdentifier(authority, codespace, code));
    }
}
