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
public class AMLFeatureType {
    
    public int Feature_Type_Code;
    public String Feature_Type_Name;
    public String Feature_Type_Definition;
    public String Comment;
    public String Feature_Themes;
    public String Feature_Type_Acronym;
    public String Feature_Type_Category;
    
    public AMLFeatureType(final String[] parts) {
        Feature_Type_Code = Integer.parseInt(parts[0]);
        Feature_Type_Name = parts[1];
        Feature_Type_Definition = parts[2];
        Comment = parts[3];
        Feature_Themes = parts[4];
        Feature_Type_Acronym = parts[5];
        Feature_Type_Category = parts[6];
    }
    
}
