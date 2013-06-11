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
public class AMLFeatureCatalog {
    
    public int Attribute_Code;
    public int Feature_Type_Code;
    public String Feature_Type_Name;
    public String Attribute_Name;
    public String Comment;
    public String Attribute_Status;
    public String Category;
    
    public AMLFeatureCatalog(final String[] parts) {
        Attribute_Code = Integer.parseInt(parts[0]);
        Feature_Type_Code = Integer.parseInt(parts[1]);
        Feature_Type_Name = parts[2];
        Attribute_Name = parts[3];
        Comment = parts[4];
        Attribute_Status = parts[5];
        Category = parts[6];
    }
    
}
