/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.data.shapefile;

import org.geotoolkit.data.AbstractDataStoreFactory;
import org.geotoolkit.data.DataStoreFinder;
import org.geotoolkit.data.FileDataStoreFactory;
import org.geotoolkit.data.folder.AbstractFolderDataStoreFactory;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.util.ResourceInternationalString;
import org.geotoolkit.util.collection.MapUtilities;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * DataStore for a folder of Shapefiles.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ShapefileFolderDataStoreFactory extends AbstractFolderDataStoreFactory{
    
    /** factory identification **/
    public static final DefaultServiceIdentification IDENTIFICATION = derivateIdentification(ShapefileDataStoreFactory.IDENTIFICATION);
    public static final String NAME = IDENTIFICATION.getCitation().getTitle().toString();
    
    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);
    
    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR = 
            derivateDescriptor(IDENTIFIER,ShapefileDataStoreFactory.PARAMETERS_DESCRIPTOR);
        
    public ShapefileFolderDataStoreFactory(){
        super(PARAMETERS_DESCRIPTOR);
    }
    
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
    
    @Override
    public FileDataStoreFactory getSingleFileFactory() {
        return DataStoreFinder.getAllFactories(ShapefileDataStoreFactory.class).next();
    }

    @Override
    public CharSequence getDescription() {
        return new ResourceInternationalString("org/geotoolkit/shapefile/bundle", "datastoreFolderDescription");
    }

    @Override
    public CharSequence getDisplayName() {
        return new ResourceInternationalString("org/geotoolkit/shapefile/bundle", "datastoreFolderTitle");
    }
    
    
    
}
