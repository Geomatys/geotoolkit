/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.metadata.dimap;

import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;

/**
 * TODO
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DimapMetadataFormat implements IIOMetadataFormat{

    public static final String NATIVE_FORMAT = DimapConstants.TAG_DIMAP;

    public static final DimapMetadataFormat INSTANCE = new DimapMetadataFormat();

    private DimapMetadataFormat(){}

    @Override
    public String getRootName() {
        return NATIVE_FORMAT;
    }

    @Override
    public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getElementMinChildren(String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getElementMaxChildren(String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getElementDescription(String elementName, Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getChildPolicy(String elementName) {
        return CHILD_POLICY_EMPTY;
    }

    @Override
    public String[] getChildNames(String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getAttributeNames(String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAttributeValueType(String elementName, String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAttributeDataType(String elementName, String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAttributeRequired(String elementName, String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getAttributeDefaultValue(String elementName, String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getAttributeEnumerations(String elementName, String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getAttributeMinValue(String elementName, String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getAttributeMaxValue(String elementName, String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAttributeListMinLength(String elementName, String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAttributeListMaxLength(String elementName, String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getAttributeDescription(String elementName, String attrName, Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getObjectValueType(String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Class<?> getObjectClass(String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getObjectDefaultValue(String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object[] getObjectEnumerations(String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Comparable<?> getObjectMinValue(String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Comparable<?> getObjectMaxValue(String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getObjectArrayMinLength(String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getObjectArrayMaxLength(String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
