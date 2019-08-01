/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.filter.binarycomparison;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import org.apache.sis.util.ArgumentChecks;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.filter.AbstractFilter;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.MatchAction;
import org.opengis.filter.expression.Expression;

/**
 * Immutable abstract "binary comparison operator".
 *
 * @author Johann Sorel (Geomatys)
 * @param <E> Expression or subclass
 * @param <F> Expression or subclass
 * @module
 */
public abstract class AbstractBinaryComparisonOperator<E extends Expression,F extends Expression>
                                                extends AbstractFilter implements BinaryComparisonOperator,Serializable{

    protected final E left;
    protected final F right;
    protected final boolean match;
    protected final MatchAction matchAction;

    public AbstractBinaryComparisonOperator(final E left, final F right, final boolean match, final MatchAction matchAction) {
        ensureNonNull("left", left);
        ensureNonNull("right", right);
        this.left = left;
        this.right = right;
        this.match = match;
        this.matchAction = matchAction;
        ArgumentChecks.ensureNonNull("matchAction", matchAction);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public E getExpression1() {
        return left;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F getExpression2() {
        return right;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isMatchingCase() {
        return match;
    }

    @Override
    public MatchAction getMatchAction() {
        return matchAction;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final boolean evaluate(final Object candidate) {
        final Object objleft = left.evaluate(candidate);
        final Object objright = right.evaluate(candidate);

        if (objleft instanceof Collection && objright instanceof Collection) {
            return evaluateOne(objleft, objright);
        } else if (objleft instanceof Collection) {
            final Collection col = (Collection) objleft;
            if(col.isEmpty()) return false;
            switch(matchAction){
                case ALL:
                    for(Object o : col){
                        if(!evaluateOne(o, objright)) return false;
                    }
                    return true;
                case ANY:
                    for(Object o : col){
                        if(evaluateOne(o, objright)) return true;
                    }
                    return false;
                case ONE:
                    boolean found = false;
                    for(Object o : col){
                        if(evaluateOne(o, objright)){
                            if(found) return false;
                            found = true;
                        }
                    }
                    return found;
                default: return false;
            }
        } else if (objright instanceof Collection) {
            final Collection col = (Collection) objright;
            if(col.isEmpty()) return false;
            switch(matchAction){
                case ALL:
                    for(Object o : col){
                        if(!evaluateOne(objleft,o)) return false;
                    }
                    return true;
                case ANY:
                    for(Object o : col){
                        if(evaluateOne(objleft,o)) return true;
                    }
                    return false;
                case ONE:
                    boolean found = false;
                    for(Object o : col){
                        if(evaluateOne(objleft,o)){
                            if(found) return false;
                            found = true;
                        }
                    }
                    return found;
                default: return false;
            }
        } else {
            return evaluateOne(objleft, objright);
        }
    }

    protected abstract boolean evaluateOne(Object objleft,Object objright);

    protected Integer compare(Object objleft, Object objright){

        if(!(objleft instanceof Comparable)){
            return null;
        }

        //see if the right type might be more appropriate for test
        if( !(objleft instanceof Date) ){

            if(objright instanceof Date){
                //object right class is more appropriate

                Object cdLeft = ObjectConverters.convert(objleft, Date.class);
                if(cdLeft != null){
                    return ((Comparable)cdLeft).compareTo(objright);
                }

            }

        }

        objright = ObjectConverters.convert(objright, objleft.getClass());

        if (objleft instanceof java.sql.Date && objright instanceof java.sql.Date) {
            final Calendar cal1 = Calendar.getInstance();
            cal1.setTime((java.sql.Date)objleft);
            cal1.set(Calendar.HOUR_OF_DAY, 0);
            cal1.set(Calendar.MINUTE, 0);
            cal1.set(Calendar.SECOND, 0);
            cal1.set(Calendar.MILLISECOND, 0);

            final Calendar cal2 = Calendar.getInstance();
            cal2.setTime((java.sql.Date)objright);
            cal2.set(Calendar.HOUR_OF_DAY, 0);
            cal2.set(Calendar.MINUTE, 0);
            cal2.set(Calendar.SECOND, 0);
            cal2.set(Calendar.MILLISECOND, 0);

            return cal1.compareTo(cal2);
        }

        if(objright == null){
            return null;
        }
        return ((Comparable)objleft).compareTo(objright);
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.left != null ? this.left.hashCode() : 0);
        hash = 61 * hash + (this.right != null ? this.right.hashCode() : 0);
        hash = 61 * hash + (this.match ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractBinaryComparisonOperator<E, F> other = (AbstractBinaryComparisonOperator<E, F>) obj;
        if (this.left != other.left && (this.left == null || !this.left.equals(other.left))) {
            return false;
        }
        if (this.right != other.right && (this.right == null || !this.right.equals(other.right))) {
            return false;
        }
        if (this.match != other.match) {
            return false;
        }
        return true;
    }

}
