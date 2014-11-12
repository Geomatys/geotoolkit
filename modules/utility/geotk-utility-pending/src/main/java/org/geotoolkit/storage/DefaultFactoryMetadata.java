/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2014, Geomatys
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

package org.geotoolkit.storage;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultFactoryMetadata implements FactoryMetadata {

    public static final Class[] GEOMS_NONE = new Class[0];
    
    private final DataType dataType;
    private final boolean styledFeature;
    private final boolean supportStoreReading;
    private final boolean supportStoreCreation;
    private final boolean supportStoreWriting;
    private final Class[] supportedGeomClasses;

    public DefaultFactoryMetadata(DataType dataType, boolean supportStoreReading, 
            boolean supportStoreCreation, boolean supportStoreWriting) {
        this(dataType,supportStoreReading,supportStoreCreation,supportStoreWriting,false,GEOMS_NONE);
    }
    
    public DefaultFactoryMetadata(DataType dataType, boolean supportStoreReading, 
            boolean supportStoreCreation, boolean supportStoreWriting, 
            boolean styledFeature, Class[] supportedGeomClasses) {
        this.dataType = dataType;
        this.supportStoreReading = supportStoreReading;
        this.supportStoreCreation = supportStoreCreation;
        this.supportStoreWriting = supportStoreWriting;
        this.styledFeature = styledFeature;
        this.supportedGeomClasses = supportedGeomClasses == null ? GEOMS_NONE : supportedGeomClasses;
    }
    
    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public boolean produceStyledFeature() {
        return styledFeature;
    }

    @Override
    public boolean supportStoreReading() {
        return supportStoreReading;
    }

    @Override
    public boolean supportStoreCreation() {
        return supportStoreCreation;
    }

    @Override
    public boolean supportStoreWriting() {
        return supportStoreWriting;
    }

    @Override
    public Class[] supportedGeometryTypes() {
        return supportedGeomClasses.clone();
    }
    
}
