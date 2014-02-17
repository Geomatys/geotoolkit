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
package org.geotoolkit.filter.binding;

import java.util.regex.Pattern;
import static org.geotoolkit.filter.binding.AttributeBinding.stripPrefix;
import org.opengis.feature.type.ComplexType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ComplexTypeBinding extends AbstractBinding<ComplexType>{
    private static final Pattern PROPERTY_PATTERN = Pattern.compile("(\\w+:)?(.+)");

    public ComplexTypeBinding() {
        super(ComplexType.class, 10);
    }

    @Override
    public boolean support(String xpath) {
        return !xpath.startsWith("/") && PROPERTY_PATTERN.matcher(xpath).matches();
    }

    @Override
    public <T> T get(ComplexType candidate, String xpath, Class<T> target) throws IllegalArgumentException {
        if(candidate==null) return null;
        xpath = stripPrefix(xpath);
        return (T) candidate.getDescriptor(xpath);
    }

    @Override
    public void set(ComplexType candidate, String xpath, Object value) throws IllegalArgumentException {
        throw new IllegalArgumentException("Types are immutable");
    }

}
