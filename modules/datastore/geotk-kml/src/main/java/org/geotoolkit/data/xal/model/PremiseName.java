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
package org.geotoolkit.data.xal.model;

/**
 * <p>This interface maps PremiseNae element.</p>
 *
 * <p>Specification of the name of the premise (house, building, park, farm, etc).
 * A premise name is specified when the premise cannot be addressed
 * using a street name plus premise (house) number.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="Type"/>
 *  &lt;xs:attribute name="TypeOccurrence">
 *  &lt;xs:simpleType>
 *      &lt;xs:restriction base="xs:NMTOKEN">
 *          &lt;xs:enumeration value="Before"/>
 *          &lt;xs:enumeration value="After"/>
 *      &lt;/xs:restriction>
 *  &lt;/xs:simpleType>
 *  &lt;/xs:attribute>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PremiseName extends GenericTypedGrPostal {

    /**
     * <p>EGIS Building where EGIS occurs before Building, DES JARDINS occurs after COMPLEXE DES JARDINS.</p>
     * 
     * @return
     */
    public AfterBeforeEnum getTypeOccurrence();

}
