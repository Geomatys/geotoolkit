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
public class DataDictionaryDefinition extends S57ModelObject {
    
    //7.5.1.1 Data dictionary definition field structure
    public static final String DDDF = "DDDF";
    public static final String DDDF_RCNM = "RCNM";
    public static final String DDDF_RCID = "RCID";
    public static final String DDDF_OORA = "OORA";
    public static final String DDDF_OAAC = "OAAC";
    public static final String DDDF_OACO = "OACO";
    public static final String DDDF_OALL = "OALL";
    public static final String DDDF_OATY = "OATY";
    public static final String DDDF_DEFN = "DEFN";
    public static final String DDDF_AUTH = "AUTH";    
    public static final String DDDF_COMT = "COMT";
    
    
    
    public static class DataDictionaryDefinitionReference extends S57ModelObject {
        
        //7.5.1.2 Data dictionary definition reference field structure
        public static final String DDDF_DDDR = "DDDR";
        public static final String DDDF_DDDR_RFTP = "RFTP";
        public static final String DDDF_DDDR_RFVL = "RFVL";
        
    }
    
}
