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
public class DataDictionaryDomainIdentifier extends S57ModelObject {
    
    //7.5.2.1 Data dictionary domain identifier field structure
    public static final String DDDI = "DDDI";
    public static final String DDDI_RCNM = "RCNM";
    public static final String DDDI_RCID = "RCID"; 
    public static final String DDDI_ATLB = "ATLB";
    public static final String DDDI_ATDO = "ATDO";
    public static final String DDDI_ADMU = "ADMU";
    public static final String DDDI_ADFT = "ADFT";
    public static final String DDDI_AUTH = "AUTH";
    public static final String DDDI_COMT = "COMT";
    
    public static class DataDictionaryDomainField extends S57ModelObject {
        //7.5.2.2 Data dictionary domain field structure
        public static final String DDDI_DDOM = "DDOM";
        public static final String DDDI_DDOM_RAVA = "RAVA";
        public static final String DDDI_DDOM_DVAL = "DVAL";
        public static final String DDDI_DDOM_DVSD = "DVSD";
        public static final String DDDI_DDOM_DEFN = "DEFN";
        public static final String DDDI_DDOM_AUTH = "AUTH";
    }
    
    public static class DataDictionaryDomainReference extends S57ModelObject{
        //7.5.2.3 Data dictionary domain reference field structure
        public static final String DDDI_DDOM_DDRF = "DDRF";
        public static final String DDDI_DDOM_DDRF_RFTP = "RFTP";
        public static final String DDDI_DDOM_DDRF_RFVL = "RFVL";
    }
    
}
