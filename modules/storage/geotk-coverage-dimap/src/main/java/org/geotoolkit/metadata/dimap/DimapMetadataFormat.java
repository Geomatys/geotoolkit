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
    public boolean canNodeAppear(final String elementName, final ImageTypeSpecifier imageType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getElementMinChildren(final String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getElementMaxChildren(final String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getElementDescription(final String elementName, final Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getChildPolicy(final String elementName) {
        return CHILD_POLICY_EMPTY;
    }

    @Override
    public String[] getChildNames(final String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getAttributeNames(final String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAttributeValueType(final String elementName, final String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAttributeDataType(final String elementName, final String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAttributeRequired(final String elementName, final String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getAttributeDefaultValue(final String elementName, final String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getAttributeEnumerations(final String elementName, final String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getAttributeMinValue(final String elementName, final String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getAttributeMaxValue(final String elementName, final String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAttributeListMinLength(final String elementName, final String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAttributeListMaxLength(final String elementName, final String attrName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getAttributeDescription(final String elementName, final String attrName, final Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getObjectValueType(final String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Class<?> getObjectClass(final String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getObjectDefaultValue(final String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object[] getObjectEnumerations(final String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Comparable<?> getObjectMinValue(final String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Comparable<?> getObjectMaxValue(final String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getObjectArrayMinLength(final String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getObjectArrayMaxLength(final String elementName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
