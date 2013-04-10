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
package org.geotoolkit.data.s57.model;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DataDictionarySchemaIdentifier extends S57ModelObject {
    
    //7.5.3.1 Data dictionary schema identifier field structure
    public static final String DDSI = "DDSI";
    public static final String DDSI_RCNM = "RCNM";
    public static final String DDSI_RCID = "RCID"; 
    public static final String DDSI_OBLB = "OBLB"; 
        
    public static class DataDictionarySchemaField extends S57ModelObject {
        //7.5.3.2 Data dictionary schema field structure
        public static final String DDSI_DDSC = "DDSC";
        public static final String DDSI_DDSC_ATLB = "ATLB";
        public static final String DDSI_DDSC_ASET = "ASET";
        public static final String DDSI_DDSC_AUTH = "AUTH";
        
    }
    
}
