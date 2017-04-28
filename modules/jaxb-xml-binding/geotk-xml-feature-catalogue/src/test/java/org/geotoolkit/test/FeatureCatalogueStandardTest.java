/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.sis.metadata.KeyNamePolicy;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.metadata.ValueExistencePolicy;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.feature.catalog.ConstraintImpl;
import org.geotoolkit.feature.catalog.DefinitionSourceImpl;
import org.geotoolkit.feature.catalog.FeatureAttributeImpl;
import org.geotoolkit.feature.catalog.FeatureCatalogueImpl;
import org.geotoolkit.feature.catalog.FeatureCatalogueStandard;
import org.geotoolkit.feature.catalog.FeatureTypeImpl;
import org.geotoolkit.feature.catalog.ListedValueImpl;
import org.geotoolkit.feature.catalog.util.MultiplicityImpl;
import org.geotoolkit.feature.catalog.util.MultiplicityRangeImpl;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.metadata.MetadataFactory;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.opengis.feature.catalog.Constraint;
import org.opengis.feature.catalog.FeatureType;
import org.opengis.feature.catalog.PropertyType;
import org.opengis.feature.catalog.util.Multiplicity;
import org.opengis.feature.catalog.util.MultiplicityRange;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FeatureCatalogueStandardTest {
 
    @Test
    public void asValueMapTest() {
        MetadataStandard standard = FeatureCatalogueStandard.ISO_19110;
        
        // empty case - should not raise exception
        FeatureCatalogueImpl metadata = new FeatureCatalogueImpl();
        Map<String,Object> map = standard.asValueMap(metadata, KeyNamePolicy.UML_IDENTIFIER, ValueExistencePolicy.NON_EMPTY);
        
        assertEquals(0, map.size());
        
        // more filled object 
        DefinitionSourceImpl definitionSource = new DefinitionSourceImpl("def-src-1", Citations.EPSG);
        metadata.setDefinitionSource(definitionSource);
        
        List<Constraint> cst = new ArrayList<>();
        cst.add(new ConstraintImpl("some constraint"));
        
        List<PropertyType> carrierOfCharacteristics = new ArrayList<>();
        FeatureAttributeImpl fa1 = new FeatureAttributeImpl("fa-1", 
                                                            Names.createMemberName("nmsp", ":", "fa-1", String.class), 
                                                            "some def", 
                                                            new MultiplicityImpl(new MultiplicityRangeImpl(0, Integer.MAX_VALUE)),
                                                            null, 
                                                            cst,
                                                            "cd-2", 
                                                            Arrays.asList(new ListedValueImpl("cd-3", "lab", "def", null)),
                                                            Names.createTypeName("nmsp", ":", "CharacterString"));
        carrierOfCharacteristics.add(fa1);
        FeatureTypeImpl featureType = new FeatureTypeImpl("ft-1", Names.createLocalName("nmsp", ":", "ft-1"), "some def", "cd-1", Boolean.FALSE, new ArrayList<>(), metadata, carrierOfCharacteristics);
        fa1.setFeatureType(featureType);
        
        metadata.setFeatureType(featureType);
        
        map = standard.asValueMap(metadata, KeyNamePolicy.UML_IDENTIFIER, ValueExistencePolicy.NON_EMPTY);
        
        assertEquals(2, map.size());
        
        map = standard.asValueMap(fa1, KeyNamePolicy.UML_IDENTIFIER, ValueExistencePolicy.NON_EMPTY);
        
        assertEquals(9, map.size());
        
    }
    
    @Test
    public void factoryCreateTest() throws Exception {
        MetadataFactory factory = new MetadataFactory(FeatureCatalogueStandard.ISO_19110, MetadataStandard.ISO_19115);
        
        factory.create(FeatureType.class, Collections.<String,Object>emptyMap());
        
        factory.create(Multiplicity.class, Collections.<String,Object>emptyMap());
        
        factory.create(MultiplicityRange.class, Collections.<String,Object>emptyMap());
        
        //factory.create(UnlimitedInteger.class, Collections.<String,Object>emptyMap());
    }
}
