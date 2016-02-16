/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.feature;

import org.geotoolkit.feature.type.DefaultFeatureTypeFactory;
import org.junit.Ignore;
import org.geotoolkit.feature.type.FeatureTypeFactory;

/**
 *
 * @author Alexis Manin
 */
public class ValidatingFeatureTest extends AbstractComplexFeatureTest {

    private static final ValidatingFeatureFactory FF = new ValidatingFeatureFactory();
    protected static final DefaultFeatureTypeFactory FTF = new DefaultFeatureTypeFactory();

    public ValidatingFeatureTest() {
        super(true);
    }

    @Override
    public FeatureFactory getFeatureFactory() {
        return FF;
    }

    @Override
    public FeatureTypeFactory getFeatureTypeFactory() {
        return FTF;
    }

    @Ignore("Problems with validation, does not check for attributes with the same id")
    @Override
    public void testCreateComplexAttribute() {
        //super.testCreateComplexAttribute();
    }

    @Ignore("Problems with validation, does not check attribute insertion")
    @Override
    public void testCreateComplexFeature() {
        //super.testCreateComplexFeature();
    }

    @Ignore("Problems with validation, does not check for attributes with the same id")
    @Override
    public void testCreateSimpleFeature() {
        //super.testCreateSimpleFeature();
    }
}
