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
public class AMLAttribute {
    
    public int Attribute_Code;
    public String Attribute_Name;
    public String Attribute_Definition;
    public String Attribute_Description;
    public String Attribute_Acronym;
    public String UOM;
    public String Resolution;
    public String Domain;
    public String Attribute_Type;

    public AMLAttribute(final String[] parts) {
        Attribute_Code = Integer.parseInt(parts[0]);
        Attribute_Name = parts[1];
        Attribute_Definition = parts[2];
        Attribute_Description = parts[3];
        Attribute_Acronym = parts[4];
        UOM = parts[5];
        Resolution = parts[6];
        Domain = parts[7];
        Attribute_Type = parts[8];
    }
        
}
