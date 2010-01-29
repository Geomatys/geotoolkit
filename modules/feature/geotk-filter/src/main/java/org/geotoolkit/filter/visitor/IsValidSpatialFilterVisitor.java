/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.filter.visitor;

import org.geotoolkit.feature.DefaultName;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.And;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class IsValidSpatialFilterVisitor implements FilterVisitor,ExpressionVisitor {

    private final SimpleFeatureType ft;

    public IsValidSpatialFilterVisitor(SimpleFeatureType ft) {
        this.ft = ft;
    }
    public Object visitNullFilter(Object o) {
        return true;
    }

    public Object visit(ExcludeFilter ef, Object o) {
        return true;
    }

    public Object visit(IncludeFilter i, Object o) {
        return true;
    }

    public Object visit(And and, Object o) {
        return true;
    }

    public Object visit(Id id, Object o) {
        return true;
    }

    public Object visit(Not not, Object o) {
        return true;
    }

    public Object visit(Or or, Object o) {
        return true;
    }

    public Object visit(PropertyIsBetween pib, Object o) {
        return true;
    }

    public Object visit(PropertyIsEqualTo piet, Object o) {
        return true;
    }

    public Object visit(PropertyIsNotEqualTo pinet, Object o) {
        return true;
    }

    public Object visit(PropertyIsGreaterThan pigt, Object o) {
        return true;
    }

    public Object visit(PropertyIsGreaterThanOrEqualTo pgt, Object o) {
        return true;
    }

    public Object visit(PropertyIsLessThan pilt, Object o) {
        return true;
    }

    public Object visit(PropertyIsLessThanOrEqualTo plt, Object o) {
        return true;
    }

    public Object visit(PropertyIsLike pil, Object o) {
        return true;
    }

    public Object visit(PropertyIsNull pin, Object o) {
        return true;
    }

    private Name getNameFromString(String fullName) {
        if (fullName.indexOf(':') != -1) {
            String namespace = fullName.substring(0, fullName.lastIndexOf(':'));
            return new DefaultName(namespace, fullName.substring(fullName.lastIndexOf(':') + 1, fullName.length()));

        } else {
            return new DefaultName(fullName);
        }
    }

    public Object visit(BBOX bbox, Object o) {
        if (bbox.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) bbox.getExpression1();
            //for the bbox filter the propertyName can be empty
            if (pt.getPropertyName().equals("")) return true;
            
            Name name = getNameFromString(pt.getPropertyName());
            AttributeDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    public Object visit(Beyond beyond, Object o) {
        if (beyond.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) beyond.getExpression1();
            Name name = getNameFromString(pt.getPropertyName());
            AttributeDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    public Object visit(Contains cntns, Object o) {
        if (cntns.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) cntns.getExpression1();
            Name name = getNameFromString(pt.getPropertyName());
            AttributeDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    public Object visit(Crosses crs, Object o) {
        if (crs.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) crs.getExpression1();
            Name name = getNameFromString(pt.getPropertyName());
            AttributeDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    public Object visit(Disjoint dsjnt, Object o) {
        if (dsjnt.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) dsjnt.getExpression1();
            Name name = getNameFromString(pt.getPropertyName());
            AttributeDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    public Object visit(DWithin dw, Object o) {
        if (dw.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) dw.getExpression1();
            Name name = getNameFromString(pt.getPropertyName());
            AttributeDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    public Object visit(Equals equals, Object o) {
        if (equals.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) equals.getExpression1();
            Name name = getNameFromString(pt.getPropertyName());
            AttributeDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    public Object visit(Intersects i, Object o) {
        if (i.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) i.getExpression1();
            Name name = getNameFromString(pt.getPropertyName());
            AttributeDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    public Object visit(Overlaps ovrlps, Object o) {
        if (ovrlps.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) ovrlps.getExpression1();
            Name name = getNameFromString(pt.getPropertyName());
            AttributeDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    public Object visit(Touches tchs, Object o) {
        if (tchs.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) tchs.getExpression1();
            Name name = getNameFromString(pt.getPropertyName());
            AttributeDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    public Object visit(Within within, Object o) {
        if (within.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) within.getExpression1();
            Name name = getNameFromString(pt.getPropertyName());
            AttributeDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    public Object visit(NilExpression ne, Object o) {
        return true;
    }

    public Object visit(Add add, Object o) {
        return true;
    }

    public Object visit(Divide divide, Object o) {
        return true;
    }

    public Object visit(Function fnctn, Object o) {
        return true;
    }

    public Object visit(Literal ltrl, Object o) {
        return true;
    }

    public Object visit(Multiply mltpl, Object o) {
        return true;
    }

    public Object visit(PropertyName pn, Object o) {
        return true;
    }

    public Object visit(Subtract sbtrct, Object o) {
        return true;
    }

}
