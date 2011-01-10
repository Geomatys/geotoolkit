/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.opengis.filter.And;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
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
import org.opengis.filter.expression.Expression;
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
 * Expression visitor that returns a list of all Feature attributs requiered by this expression.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class ListingPropertyVisitor implements FilterVisitor,ExpressionVisitor {
    
    public static final ListingPropertyVisitor VISITOR = new ListingPropertyVisitor();
    
    /** 
     * visit each expression and return all requiered Attributs 
     */
    protected ListingPropertyVisitor() {
    }
    
    /** 
     * visit each expression and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Collection<String> visit( final NilExpression expression, final Object data ) {
        if(data instanceof Collection) return (Collection<String>)data;
        return Collections.emptyList();
    }
    
    /** 
     * visit each expression and return all requiered Attributs 
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Collection<String> visit( final Add expression, final Object data ) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
    	expression.getExpression1().accept(this, names);
        expression.getExpression2().accept(this, names);
    	return names;
    }
    
    /** 
     * visit each expression and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Collection<String> visit( final Divide expression, final Object data ) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
    	expression.getExpression1().accept(this, names);
        expression.getExpression2().accept(this, names);
    	return names;
    }
    
    /** 
     * visit each expression and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Collection<String> visit( final Function expression, final Object data ) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();

        if( expression.getParameters() != null ){
            for( Expression parameter : expression.getParameters() ){
                parameter.accept(this, names);
            }
        }
        return names;
    }

    /**
     * Literal expressions are always static.
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Collection<String> visit( final Literal expression, final Object data ) {
        if(data instanceof Collection) return (Collection<String>)data;
        return Collections.emptyList();
    }
    
    /** 
     * visit each expression and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Collection<String> visit( final Multiply expression, final Object data ) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
    	expression.getExpression1().accept(this, names);
        expression.getExpression2().accept(this, names);
    	return names;
    }
    
    /** 
     * visit each expression and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Collection<String> visit( final PropertyName expression, final Object data ) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        String propName = expression.getPropertyName();
        if(!propName.trim().isEmpty()){
            names.add(propName);
        }
        return names;
    }
    
    /** 
     * visit each expression and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Collection<String> visit( final Subtract expression, final Object data ) {
    	final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
    	expression.getExpression1().accept(this, names);
        expression.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visitNullFilter(final Object data) {
        if(data instanceof Collection) return (Collection<String>)data;
        return Collections.emptyList();
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final ExcludeFilter filter, final Object data) {
        if(data instanceof Collection) return (Collection<String>)data;
        return Collections.emptyList();
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final IncludeFilter filter, final Object data) {
        if(data instanceof Collection) return (Collection<String>)data;
        return Collections.emptyList();
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final And filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        for(Filter child : filter.getChildren()){
            child.accept(this, names);
        }
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final Id filter, final Object data) {
        if(data instanceof Collection) return (Collection<String>)data;
        return Collections.emptyList();
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final Not filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        Filter child = filter.getFilter();
        if(child != null) child.accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final Or filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        for(Filter child : filter.getChildren()){
            child.accept(this, names);
        }
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final PropertyIsBetween filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression().accept(this, names);
        filter.getLowerBoundary().accept(this, names);
        filter.getUpperBoundary().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final PropertyIsEqualTo filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final PropertyIsNotEqualTo filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final PropertyIsGreaterThan filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final PropertyIsGreaterThanOrEqualTo filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final PropertyIsLessThan filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final PropertyIsLessThanOrEqualTo filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final PropertyIsLike filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final PropertyIsNull filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final BBOX filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final Beyond filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final Contains filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final Crosses filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final Disjoint filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final DWithin filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final Equals filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final Intersects filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final Overlaps filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final Touches filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(final Within filter, final Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }
    
}
