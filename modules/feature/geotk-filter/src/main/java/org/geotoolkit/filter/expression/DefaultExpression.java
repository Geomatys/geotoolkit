/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.spi.ServiceRegistry;
import javax.xml.bind.annotation.XmlTransient;
import org.geotoolkit.filter.accessor.PropertyAccessor;
import org.geotoolkit.filter.accessor.PropertyAccessorFactory;

/**
 * Implements a default expression, with helpful variables and static methods.
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlTransient
public abstract class DefaultExpression extends AbstractExpression {

    private static final List<PropertyAccessorFactory> ACCESSOR_FACTORIES = new ArrayList<PropertyAccessorFactory>();

    static{
        final Iterator<PropertyAccessorFactory> factories = ServiceRegistry.lookupProviders(PropertyAccessorFactory.class);

        while(factories.hasNext()){
            ACCESSOR_FACTORIES.add(factories.next());
        }

    }

    /** Defines the type of this expression. */
    protected final ExpressionType type;

    protected DefaultExpression(ExpressionType type){
        this.type = type;
    }

    /**
     * Gets the type of this expression.
     *
     * @return The short representation of the expression type.
     */
    public ExpressionType getType() {
        return type;
    }

    protected static final PropertyAccessor getAccessor(Class type, String xpath, Class target){
        for(PropertyAccessorFactory pf : ACCESSOR_FACTORIES){
            PropertyAccessor pa = pf.createPropertyAccessor(type, xpath, target,null);
            if(pa != null) return pa;
        }
        return null;
    }

}
