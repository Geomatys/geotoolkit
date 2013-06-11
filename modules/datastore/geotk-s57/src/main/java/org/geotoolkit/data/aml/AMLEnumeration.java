/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.aml;

/**
 * 
 * @author Johann Sorel (Geomatys)
 */
public class AMLEnumeration {
    
    public int Attribute_Code;
    public String Attribute_Name;
    public int Enum_Code;
    public String Enum_Name;
    public String Enum_Definition;
    
    public AMLEnumeration(final String[] parts) {
        Attribute_Code = Integer.parseInt(parts[0]);
        Attribute_Name = parts[1];
        Enum_Code = Integer.parseInt(parts[2]);
        Enum_Name = parts[3];
        Enum_Definition = parts[4];
    }
    
}
