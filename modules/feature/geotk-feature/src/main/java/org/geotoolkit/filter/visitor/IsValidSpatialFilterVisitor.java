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

import org.geotoolkit.feature.type.DefaultName;

import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.PropertyDescriptor;
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
import org.opengis.filter.PropertyIsNil;
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
import org.opengis.filter.temporal.After;
import org.opengis.filter.temporal.AnyInteracts;
import org.opengis.filter.temporal.Before;
import org.opengis.filter.temporal.Begins;
import org.opengis.filter.temporal.BegunBy;
import org.opengis.filter.temporal.During;
import org.opengis.filter.temporal.EndedBy;
import org.opengis.filter.temporal.Ends;
import org.opengis.filter.temporal.Meets;
import org.opengis.filter.temporal.MetBy;
import org.opengis.filter.temporal.OverlappedBy;
import org.opengis.filter.temporal.TContains;
import org.opengis.filter.temporal.TEquals;
import org.opengis.filter.temporal.TOverlaps;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class IsValidSpatialFilterVisitor implements FilterVisitor,ExpressionVisitor {

    private final FeatureType ft;

    public IsValidSpatialFilterVisitor(final FeatureType ft) {
        this.ft = ft;
    }

    @Override
    public Object visitNullFilter(final Object o) {
        return true;
    }

    @Override
    public Object visit(final ExcludeFilter ef, final Object o) {
        return true;
    }

    @Override
    public Object visit(final IncludeFilter i, final Object o) {
        return true;
    }

    @Override
    public Object visit(final And and, final Object o) {
        return true;
    }

    @Override
    public Object visit(final Id id, final Object o) {
        return true;
    }

    @Override
    public Object visit(final Not not, final Object o) {
        return true;
    }

    @Override
    public Object visit(final Or or, final Object o) {
        return true;
    }

    @Override
    public Object visit(final PropertyIsBetween pib, final Object o) {
        return true;
    }

    @Override
    public Object visit(final PropertyIsEqualTo piet, final Object o) {
        return true;
    }

    @Override
    public Object visit(final PropertyIsNotEqualTo pinet, final Object o) {
        return true;
    }

    @Override
    public Object visit(final PropertyIsGreaterThan pigt, final Object o) {
        return true;
    }

    @Override
    public Object visit(final PropertyIsGreaterThanOrEqualTo pgt, final Object o) {
        return true;
    }

    @Override
    public Object visit(final PropertyIsLessThan pilt, final Object o) {
        return true;
    }

    @Override
    public Object visit(final PropertyIsLessThanOrEqualTo plt, final Object o) {
        return true;
    }

    @Override
    public Object visit(final PropertyIsLike pil, final Object o) {
        return true;
    }

    @Override
    public Object visit(final PropertyIsNull pin, final Object o) {
        return true;
    }

    @Override
    public Object visit(final PropertyIsNil pin, final Object o) {
        return true;
    }

    private GenericName getNameFromString(final String fullName) {
        return DefaultName.valueOf(fullName);
    }

    @Override
    public Object visit(final BBOX bbox, final Object o) {
        if (bbox.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) bbox.getExpression1();
            //for the bbox filter the propertyName can be empty
            if (pt.getPropertyName().equals("")) return true;
            
            GenericName name = getNameFromString(pt.getPropertyName());
            PropertyDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    @Override
    public Object visit(final Beyond beyond, final Object o) {
        if (beyond.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) beyond.getExpression1();
            GenericName name = getNameFromString(pt.getPropertyName());
            PropertyDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    @Override
    public Object visit(final Contains cntns, final Object o) {
        if (cntns.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) cntns.getExpression1();
            GenericName name = getNameFromString(pt.getPropertyName());
            PropertyDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    @Override
    public Object visit(final Crosses crs, final Object o) {
        if (crs.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) crs.getExpression1();
            GenericName name = getNameFromString(pt.getPropertyName());
            PropertyDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    @Override
    public Object visit(final Disjoint dsjnt, final Object o) {
        if (dsjnt.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) dsjnt.getExpression1();
            GenericName name = getNameFromString(pt.getPropertyName());
            PropertyDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    @Override
    public Object visit(final DWithin dw, final Object o) {
        if (dw.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) dw.getExpression1();
            GenericName name = getNameFromString(pt.getPropertyName());
            PropertyDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    @Override
    public Object visit(final Equals equals, final Object o) {
        if (equals.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) equals.getExpression1();
            GenericName name = getNameFromString(pt.getPropertyName());
            PropertyDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    @Override
    public Object visit(final Intersects i, final Object o) {
        if (i.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) i.getExpression1();
            GenericName name = getNameFromString(pt.getPropertyName());
            PropertyDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    @Override
    public Object visit(final Overlaps ovrlps, final Object o) {
        if (ovrlps.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) ovrlps.getExpression1();
            GenericName name = getNameFromString(pt.getPropertyName());
            PropertyDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    @Override
    public Object visit(final Touches tchs, final Object o) {
        if (tchs.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) tchs.getExpression1();
            GenericName name = getNameFromString(pt.getPropertyName());
            PropertyDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    @Override
    public Object visit(final Within within, final Object o) {
        if (within.getExpression1() instanceof PropertyName) {
            PropertyName pt = (PropertyName) within.getExpression1();
            GenericName name = getNameFromString(pt.getPropertyName());
            PropertyDescriptor desc = ft.getDescriptor(name);
            return desc instanceof GeometryDescriptor;
        }
        return true;
    }

    @Override
    public Object visit(final NilExpression ne, final Object o) {
        return true;
    }

    @Override
    public Object visit(final Add add, final Object o) {
        return true;
    }

    @Override
    public Object visit(final Divide divide, final Object o) {
        return true;
    }

    @Override
    public Object visit(final Function fnctn, final Object o) {
        return true;
    }

    @Override
    public Object visit(final Literal ltrl, final Object o) {
        return true;
    }

    @Override
    public Object visit(final Multiply mltpl, final Object o) {
        return true;
    }

    @Override
    public Object visit(final PropertyName pn, final Object o) {
        return true;
    }

    @Override
    public Object visit(final Subtract sbtrct, final Object o) {
        return true;
    }
    
    @Override
    public Object visit(After filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(AnyInteracts filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(Before filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(Begins filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(BegunBy filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(During filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(EndedBy filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(Ends filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(Meets filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(MetBy filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(OverlappedBy filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(TContains filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(TEquals filter, Object extraData) {
        return true;
    }

    @Override
    public Object visit(TOverlaps filter, Object extraData) {
        return true;
    }

}
