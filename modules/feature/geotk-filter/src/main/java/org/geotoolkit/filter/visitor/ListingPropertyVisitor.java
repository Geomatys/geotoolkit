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
    public Collection<String> visit( NilExpression expression, Object data ) {
        if(data instanceof Collection) return (Collection<String>)data;
        return Collections.emptyList();
    }
    
    /** 
     * visit each expression and return all requiered Attributs 
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Collection<String> visit( Add expression, Object data ) {
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
    public Collection<String> visit( Divide expression, Object data ) {
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
    public Collection<String> visit( Function expression, Object data ) {
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
    public Collection<String> visit( Literal expression, Object data ) {
        if(data instanceof Collection) return (Collection<String>)data;
        return Collections.emptyList();
    }
    
    /** 
     * visit each expression and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Collection<String> visit( Multiply expression, Object data ) {
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
    public Collection<String> visit( PropertyName expression, Object data ) {
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
    public Collection<String> visit( Subtract expression, Object data ) {
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
    public Object visitNullFilter(Object data) {
        if(data instanceof Collection) return (Collection<String>)data;
        return Collections.emptyList();
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(ExcludeFilter filter, Object data) {
        if(data instanceof Collection) return (Collection<String>)data;
        return Collections.emptyList();
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(IncludeFilter filter, Object data) {
        if(data instanceof Collection) return (Collection<String>)data;
        return Collections.emptyList();
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(And filter, Object data) {
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
    public Object visit(Id filter, Object data) {
        if(data instanceof Collection) return (Collection<String>)data;
        return Collections.emptyList();
    }

    /** 
     * visit each filter and return all requiered Attributs
     * @param data if is a Collection, this collection will be filled and returned.
     */
    @Override
    public Object visit(Not filter, Object data) {
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
    public Object visit(Or filter, Object data) {
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
    public Object visit(PropertyIsBetween filter, Object data) {
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
    public Object visit(PropertyIsEqualTo filter, Object data) {
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
    public Object visit(PropertyIsNotEqualTo filter, Object data) {
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
    public Object visit(PropertyIsGreaterThan filter, Object data) {
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
    public Object visit(PropertyIsGreaterThanOrEqualTo filter, Object data) {
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
    public Object visit(PropertyIsLessThan filter, Object data) {
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
    public Object visit(PropertyIsLessThanOrEqualTo filter, Object data) {
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
    public Object visit(PropertyIsLike filter, Object data) {
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
    public Object visit(PropertyIsNull filter, Object data) {
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
    public Object visit(BBOX filter, Object data) {
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
    public Object visit(Beyond filter, Object data) {
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
    public Object visit(Contains filter, Object data) {
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
    public Object visit(Crosses filter, Object data) {
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
    public Object visit(Disjoint filter, Object data) {
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
    public Object visit(DWithin filter, Object data) {
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
    public Object visit(Equals filter, Object data) {
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
    public Object visit(Intersects filter, Object data) {
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
    public Object visit(Overlaps filter, Object data) {
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
    public Object visit(Touches filter, Object data) {
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
    public Object visit(Within filter, Object data) {
        final Collection<String> names;
        if(data instanceof Collection) names = (Collection<String>) data;
        else names = new HashSet<String>();
        filter.getExpression1().accept(this, names);
        filter.getExpression2().accept(this, names);
    	return names;
    }
    
}
