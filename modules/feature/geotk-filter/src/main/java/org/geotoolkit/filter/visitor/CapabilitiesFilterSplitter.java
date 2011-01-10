/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.capability.DefaultFilterCapabilities;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.type.FeatureType;
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
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
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.BinaryExpression;
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
import org.opengis.filter.spatial.BinarySpatialOperator;
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
 * Determines what queries can be processed server side and which can be processed client side.
 * <p>
 * IMPLEMENTATION NOTE: This class is implemented as a stack processor. If you're curious how it
 * works, compare it with the old SQLUnpacker class, which did the same thing using recursion in a
 * more straightforward way.
 * </p>
 * <p>
 * Here's a non-implementors best-guess at the algorithm: Starting at the top of the filter, split
 * each filter into its constituent parts. If the given FilterCapabilities support the given
 * operator, then keep checking downwards.
 * </p>
 * <p>
 * The key is in knowing whether or not something "down the tree" from you wound up being supported
 * or not. This is where the stacks come in. Right before handing off to accept() the sub- filters,
 * we count how many things are currently on the "can be proccessed by the underlying datastore"
 * stack (the preStack) and we count how many things are currently on the "need to be post-
 * processed" stack.
 * </p>
 * <p>
 * After the accept() call returns, we look again at the preStack.size() and postStack.size(). If
 * the postStack has grown, that means that there was stuff down in the accept()-ed filter that
 * wasn't supportable. Usually this means that our filter isn't supportable, but not always.
 * 
 * In some cases a sub-filter being unsupported isn't necessarily bad, as we can 'unpack' OR
 * statements into AND statements (DeMorgans rule/modus poens) and still see if we can handle the
 * other side of the OR. Same with NOT and certain kinds of AND statements.
 * </p>
 * 
 * @author dzwiers
 * @author commented and ported from gt to ogc filters by saul.farber
 * @author ported to work upon {@code org.geotoolkit.filter.Capabilities} by Gabriel Roldan
 * @module pending
 * @since 2.5.3
 */
public class CapabilitiesFilterSplitter implements FilterVisitor, ExpressionVisitor {

    private static final Logger LOGGER = Logging.getLogger(CapabilitiesFilterSplitter.class);

    private static final Class[] SPATIAL_OPS = new Class[]{Beyond.class, Contains.class, Crosses.class,
            Disjoint.class, DWithin.class, Equals.class, Intersects.class, Overlaps.class,
            Touches.class, Within.class};

    /**
     * The stack holding the bits of the filter that are not processable by something with the given
     * {@link FilterCapabilities}
     */
    private final Deque postStack = new ArrayDeque();
    /**
     * The stack holding the bits of the filter that <b>are</b> processable by something with the
     * given {@link FilterCapabilities}
     */
    private final Deque preStack = new ArrayDeque();
    /**
     * Operates similar to postStack. When a update is determined to affect an attribute expression
     * the update filter is pushed on to the stack, then ored with the filter that contains the
     * expression.
     */
    private final Set changedStack = new HashSet();
    /**
     * The given filterCapabilities that we're splitting on.
     */
    private final DefaultFilterCapabilities fcs;
    private final FeatureType parent;
    private final FilterFactory ff;
    private Filter original = null;

    /**
     * Create a new instance.
     * 
     * @param fcs
     *            The FilterCapabilties that describes what Filters/Expressions the server can
     *            process.
     * @param parent The FeatureType that this filter involves. Why is this needed?
     */
    public CapabilitiesFilterSplitter(final DefaultFilterCapabilities fcs, final FeatureType parent) {
        this.ff = FactoryFinder.getFilterFactory(null);
        this.fcs = fcs;
        this.parent = parent;
    }

    /**
     * Gets the filter that cannot be sent to the server and must be post-processed on the client by
     * geotoolkit.
     * 
     * @return the filter that cannot be sent to the server and must be post-processed on the client
     *         by geotoolkit.
     */
    public Filter getPostFilter() {
        if (!changedStack.isEmpty()){
            // Return the original filter to ensure that
            // correct features are filtered
            return original;
        }

        if (postStack.size() > 1){
            LOGGER.warning("Too many post stack items after run: " + postStack.size());
        }

        // JE: Changed to peek because get implies that the value can be retrieved multiple times
        return (postStack.isEmpty()) ? Filter.INCLUDE : (Filter) postStack.peek();
    }

    /**
     * Gets the filter that can be sent to the server for pre-processing.
     * 
     * @return the filter that can be sent to the server for pre-processing.
     */
    public Filter getPreFilter() {
        if (preStack.isEmpty()) {
            return Filter.INCLUDE;
        }

        if (preStack.size() > 1) {
            LOGGER.warning("Too many pre stack items after run: " + preStack.size());
        }

        // JE: Changed to peek because get implies that the value can be retrieved multiple
        // times
        final Filter f = preStack.isEmpty() ? Filter.INCLUDE : (Filter) preStack.peek();
        
        if (changedStack.isEmpty()) {
            return f;
        }

        Iterator iter = changedStack.iterator();
        Filter updateFilter = (Filter) iter.next();
        while (iter.hasNext()) {
            Filter next = (Filter) iter.next();
            if (next == Filter.INCLUDE) {
                updateFilter = next;
                break;
            } else {
                updateFilter = (Filter) ff.or(updateFilter, next);
            }
        }
        if (updateFilter == Filter.INCLUDE || f == Filter.INCLUDE) {
            return Filter.INCLUDE;
        }
        return ff.or(f, updateFilter);
    }

    /**
     * @see FilterVisitor#visit(IncludeFilter, Object)
     * 
     * @param filter the {@link Filter} to visit
     */
    public void visit(final IncludeFilter filter) {
        return;
    }

    /**
     * @see FilterVisitor#visit(ExcludeFilter, Object)
     * 
     * @param filter the {@link Filter} to visit
     */
    public void visit(final ExcludeFilter filter) {
        if (fcs.supports(Filter.EXCLUDE)) {
            preStack.addFirst(filter);
        } else {
            postStack.addFirst(filter);
        }
    }

    /**
     * @see FilterVisitor#visit(PropertyIsBetween, Object) NOTE: This method is extra documented as
     *      an example of how all the other methods are implemented. If you want to know how this
     *      class works read this method first!
     * 
     * @param filter the {@link Filter} to visit
     */
    @Override
    public Object visit(final PropertyIsBetween filter, final Object extradata) {
        if (original == null) {
            original = filter;
        }

        if (!(fcs.supports(filter))) {
            // No, we don't support this filter.
            // So we push it onto the postStack, saying
            // "Hey, here's one more filter that we don't support.
            // Someone who called us may look at this and say,
            // "Hmm, I called accept() on this filter and now
            // the postStack is taller than it was...I guess this
            // filter wasn't accepted.
            postStack.addFirst(filter);
            return null;
        }

        // Do we support this filter type at all?
        // Yes, we do. Now, can we support the sub-filters?
        // first, remember how big the current list of "I can't support these" filters is.
        final int i = postStack.size();

        final Expression lowerBound = filter.getLowerBoundary();
        final Expression expr = filter.getExpression();
        final Expression upperBound = filter.getUpperBoundary();
        if (lowerBound == null || upperBound == null || expr == null) {
            // Well, one of the boundaries is null, so I guess
            // we're saying that *no* datastore could support this.
            postStack.addFirst(filter);
            return null;
        }

        // Ok, here's the magic. We know how big our list of "can't support"
        // filters is. Now we send off the lowerBound Expression to see if
        // it can be supported.
        lowerBound.accept(this, null);

        // Now we're back, and we check. Did the postStack get bigger?
        if (i < postStack.size()) {
            // Yes, it did. Well, that means we can't support
            // this particular filter. Let's back out anything that was
            // added by the lowerBound.accept() and add ourselves.
            postStack.removeFirst(); // lowerBound.accept()'s bum filter
            postStack.addFirst(filter);
            return null;
        }

        // Aha! The postStack didn't get any bigger, so we're still
        // all good. Now try again with the middle expression itself...

        expr.accept(this, null);

        // Did postStack get bigger?
        if (i < postStack.size()) {
            // Yes, it did. So that means we can't support
            // this particular filter. We need to back out what we've
            // done, which is BOTH the lowerbounds filter *and* the
            // thing that was added by expr.accept() when it failed.
            preStack.removeFirst(); // lowerBound.accept()'s success
            postStack.removeFirst(); // expr.accept()'s bum filter
            postStack.addFirst(filter);
            return null;
        }

        // Same deal again...
        upperBound.accept(this, null);

        if (i < postStack.size()) {
            // post process it
            postStack.removeFirst(); // upperBound.accept()'s bum filter
            preStack.removeFirst(); // expr.accept()'s success
            preStack.removeFirst(); // lowerBound.accept()'s success
            postStack.addFirst(filter);
            return null;
        }

        // Well, by getting here it means that postStack didn't get
        // taller, even after accepting all three middle filters. This
        // means that this whole filter is totally pre-filterable.

        // Let's clean up the pre-stack (which got one added to it
        // for the success at each of the three above .accept() calls)
        // and add us to the stack.

        preStack.removeFirst(); // upperBounds.accept()'s success
        preStack.removeFirst(); // expr.accept()'s success
        preStack.removeFirst(); // lowerBounds.accept()'s success

        // finally we add ourselves to the "can be pre-proccessed" filter
        // stack. Now when we return we've added exactly one thing to
        // the preStack...namely, the given filter.
        preStack.addFirst(filter);
        
        return null;
    }

    @Override
    public Object visit(final PropertyIsEqualTo filter, final Object notUsed) {
        visitBinaryComparisonOperator(filter);
        return null;
    }

    @Override
    public Object visit(final PropertyIsGreaterThan filter, final Object notUsed) {
        visitBinaryComparisonOperator(filter);
        return null;
    }

    @Override
    public Object visit(final PropertyIsGreaterThanOrEqualTo filter, final Object notUsed) {
        visitBinaryComparisonOperator(filter);
        return null;
    }

    @Override
    public Object visit(final PropertyIsLessThan filter, final Object notUsed) {
        visitBinaryComparisonOperator(filter);
        return null;
    }

    @Override
    public Object visit(final PropertyIsLessThanOrEqualTo filter, final Object notUsed) {
        visitBinaryComparisonOperator(filter);
        return null;
    }

    @Override
    public Object visit(final PropertyIsNotEqualTo filter, final Object notUsed) {
        visitBinaryComparisonOperator(filter);
        return null;
    }

    private void visitBinaryComparisonOperator(final BinaryComparisonOperator filter) {
        if (original == null) {
            original = filter;
        }

        // supports it as a group -- no need to check the type
        if (!fcs.supports(filter)) {
            postStack.addFirst(filter);
            return;
        }

        final int i = postStack.size();
        final Expression leftValue = filter.getExpression1();
        final Expression rightValue = filter.getExpression2();
        if (leftValue == null || rightValue == null) {
            postStack.addFirst(filter);
            return;
        }

        leftValue.accept(this, null);
        if (i < postStack.size()) {
            postStack.removeFirst();
            postStack.addFirst(filter);
            return;
        }

        rightValue.accept(this, null);
        if (i < postStack.size()) {
            preStack.removeFirst(); // left
            postStack.removeFirst();
            postStack.addFirst(filter);
            return;
        }

        preStack.removeFirst(); // left side
        preStack.removeFirst(); // right side
        preStack.addFirst(filter);
    }

    @Override
    public Object visit(final BBOX filter, final Object notUsed) {
        if (!fcs.supports(filter)) {
            postStack.addFirst(filter);
        } else {
            preStack.addFirst(filter);
        }
        return null;
    }

    @Override
    public Object visit(final Beyond filter, final Object notUsed) {
        visitBinarySpatialOperator(filter);
        return null;
    }

    @Override
    public Object visit(final Contains filter, final Object notUsed) {
        visitBinarySpatialOperator(filter);
        return null;
    }

    @Override
    public Object visit(final Crosses filter, final Object notUsed) {
        visitBinarySpatialOperator(filter);
        return null;
    }

    @Override
    public Object visit(final Disjoint filter, final Object notUsed) {
        visitBinarySpatialOperator(filter);
        return null;
    }

    @Override
    public Object visit(final DWithin filter, final Object notUsed) {
        visitBinarySpatialOperator(filter);
        return null;
    }

    @Override
    public Object visit(final Equals filter, final Object notUsed) {
        visitBinarySpatialOperator(filter);
        return null;
    }

    @Override
    public Object visit(final Intersects filter, final Object notUsed) {
        visitBinarySpatialOperator(filter);
        return null;
    }

    @Override
    public Object visit(final Overlaps filter, final Object notUsed) {
        visitBinarySpatialOperator(filter);
        return null;
    }

    @Override
    public Object visit(final Touches filter, final Object notUsed) {
        visitBinarySpatialOperator(filter);
        return null;
    }

    @Override
    public Object visit(final Within filter, final Object notUsed) {
        visitBinarySpatialOperator(filter);
        return null;
    }

    private void visitBinarySpatialOperator(final BinarySpatialOperator filter) {
        if (original == null) {
            original = filter;
        }

        for(final Class clazz : SPATIAL_OPS) {
            if (clazz.isAssignableFrom(filter.getClass())) {
                // if (!fcs.supports(spatialOps[i])) {
                if (!fcs.supports(filter)) {
                    postStack.addFirst(filter);
                    return;
                } else {
                    // fcs supports this filter, no need to check the rest
                    break;
                }
            }
        }

        final int i = postStack.size();
        final Expression leftGeometry = filter.getExpression1();
        final Expression rightGeometry = filter.getExpression2();

        if (leftGeometry == null || rightGeometry == null) {
            postStack.addFirst(filter);
            return;
        }

        leftGeometry.accept(this, null);
        if (i < postStack.size()) {
            postStack.removeFirst();
            postStack.addFirst(filter);
            return;
        }

        rightGeometry.accept(this, null);
        if (i < postStack.size()) {
            preStack.removeFirst(); // left
            postStack.removeFirst();
            postStack.addFirst(filter);
            return;
        }

        preStack.removeFirst(); // left side
        preStack.removeFirst(); // right side
        preStack.addFirst(filter);
    }

    @Override
    public Object visit(final PropertyIsLike filter, final Object notUsed) {
        if (original == null) {
            original = filter;
        }

        // if (!fcs.supports(PropertyIsLike.class)) {
        if (!fcs.supports(filter)) {
            postStack.addFirst(filter);
            return null;
        }

        final int i = postStack.size();
        filter.getExpression().accept(this, null);

        if (i < postStack.size()) {
            postStack.removeFirst();
            postStack.addFirst(filter);
            return null;
        }

        preStack.removeFirst(); // value
        preStack.addFirst(filter);
        return null;
    }

    @Override
    public Object visit(final And filter, final Object notUsed) {
        visitLogicOperator(filter);
        return null;
    }

    @Override
    public Object visit(final Not filter, final Object notUsed) {
        visitLogicOperator(filter);
        return null;
    }

    @Override
    public Object visit(final Or filter, final Object notUsed) {
        visitLogicOperator(filter);
        return null;
    }

    private void visitLogicOperator(final Filter filter) {
        if (original == null) {
            original = filter;
        }

        if (!fcs.supports(filter)) {
            postStack.addFirst(filter);
            return;
        }

        final int i = postStack.size();
        final int j = preStack.size();

        if (filter instanceof Not) {
            final Filter subFilter = ((Not) filter).getFilter();
            if (subFilter != null) {
                subFilter.accept(this, null);

                if (i < postStack.size()) {
                    // since and can split filter into both pre and post parts
                    // the parts have to be combined since ~(A^B) == ~A | ~B
                    // combining is easy since filter==combined result however both post and pre
                    // stacks must be cleared since both may have components of the filter
                    removeFirstToSize(postStack, i);
                    removeFirstToSize(preStack, j);
                    postStack.addFirst(filter);
                } else {
                    removeFirstToSize(preStack, j);
                    preStack.addFirst(filter);
                }
            }
        } else if (filter instanceof Or) {
            final Filter orReplacement = translateOr((Or) filter);
            orReplacement.accept(this, null);

            if (postStack.size() > i) {
                removeFirstToSize(postStack, i);
                postStack.addFirst(filter);
                return;
            }

            preStack.removeFirst();
            preStack.addFirst(filter);
        } else if (filter instanceof And) {
            // it's an AND
            final Iterator it = ((And) filter).getChildren().iterator();

            while (it.hasNext()) {
                final Filter next = (Filter) it.next();
                next.accept(this, null);
            }

            // combine the unsupported and add to the top
            if (i < postStack.size()) {
                Filter f = (Filter) postStack.removeFirst();

                while (postStack.size() > i) {
                    f = ff.and(f, (Filter) postStack.removeFirst());
                }

                postStack.addFirst(f);

                if (j < preStack.size()) {
                    f = (Filter) preStack.removeFirst();

                    while (preStack.size() > j) {
                        f = ff.and(f, (Filter) preStack.removeFirst());
                    }
                    preStack.addFirst(f);
                }
            } else {
                removeFirstToSize(preStack, j);
                preStack.addFirst(filter);
            }
        }else{
            throw new IllegalArgumentException("LogicFilter found which is not 'and, or, not : " + filter);
        }
    }

    private void removeFirstToSize(final Deque stack, final int j) {
        while (j < stack.size()) {
            stack.removeFirst();
        }
    }

    @Override
    public Object visitNullFilter(final Object notUsed) {
        return null;
    }

    @Override
    public Object visit(final IncludeFilter filter, final Object notUsed) {
        return null;
    }

    @Override
    public Object visit(final ExcludeFilter filter, final Object notUsed) {
        if (fcs.supports(Filter.EXCLUDE)) {
            preStack.addFirst(filter);
        } else {
            postStack.addFirst(filter);
        }
        return null;
    }

    @Override
    public Object visit(final PropertyIsNull filter, final Object notUsed) {
        if (original == null) {
            original = filter;
        }

        if (!fcs.supports(filter)) {
            postStack.addFirst(filter);
            return null;
        }

        final int i = postStack.size();
        filter.getExpression().accept(this, null);

        if (i < postStack.size()) {
            postStack.removeFirst();
            postStack.addFirst(filter);
        }

        preStack.removeFirst(); // null
        preStack.addFirst(filter);
        return null;
    }

    @Override
    public Object visit(final Id filter, final Object notUsed) {
        if (original == null) {
            original = filter;
        }

        // figure out how to check that this is top level.
        // otherwise this is fine
        if (!postStack.isEmpty()) {
            postStack.addFirst(filter);
        }
        preStack.addFirst(filter);
        return null;
    }

    @Override
    public Object visit(final PropertyName expression, final Object notUsed) {
        // JD: use an expression to get at the attribute type intead of accessing directly
        if (parent != null && expression.evaluate(parent) == null) {
            throw new IllegalArgumentException("Property '" + expression.getPropertyName() + "' could not be found in " + parent.getName());
        }

        preStack.addFirst(expression);
        return null;
    }

    @Override
    public Object visit(final Literal expression, final Object notUsed) {
        if (expression.getValue() == null) {
            postStack.addFirst(expression);
        }
        preStack.addFirst(expression);
        return null;
    }

    @Override
    public Object visit(final Add filter, final Object notUsed) {
        visitMathExpression(filter);
        return null;
    }

    @Override
    public Object visit(final Divide filter, final Object notUsed) {
        visitMathExpression(filter);
        return null;
    }

    @Override
    public Object visit(final Multiply filter, final Object notUsed) {
        visitMathExpression(filter);
        return null;
    }

    @Override
    public Object visit(final Subtract filter, final Object notUsed) {
        visitMathExpression(filter);
        return null;
    }

    private void visitMathExpression(final BinaryExpression expression) {
        // if (!fcs.supports(Add.class) && !fcs.supports(Subtract.class)
        // && !fcs.supports(Multiply.class) && !fcs.supports(Divide.class)) {
        /*if (!fcs.fullySupports(expression)) {
        postStack.addFirst(expression);
        return;
        }*/

        final int i = postStack.size();
        final Expression leftValue = expression.getExpression1();
        final Expression rightValue = expression.getExpression2();

        if (leftValue == null || rightValue == null) {
            postStack.addFirst(expression);
            return;
        }

        leftValue.accept(this, null);
        if (i < postStack.size()) {
            postStack.removeFirst();
            postStack.addFirst(expression);
            return;
        }

        rightValue.accept(this, null);
        if (i < postStack.size()) {
            preStack.removeFirst(); // left
            postStack.removeFirst();
            postStack.addFirst(expression);
            return;
        }

        preStack.removeFirst(); // left side
        preStack.removeFirst(); // right side
        preStack.addFirst(expression);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object visit(final Function expression, final Object notUsed) {
        /*if (!fcs.fullySupports(expression)) {
        postStack.addFirst(expression);
        return null;
        }*/

        if (expression.getName() == null) {
            postStack.addFirst(expression);
            return null;
        }

        final int i = postStack.size();
        final int j = preStack.size();

        for (int k = 0; k < expression.getParameters().size(); k++) {
            expression.getParameters().get(k).accept(this, null);

            if (i < postStack.size()) {
                while (j < preStack.size()) {
                    preStack.removeFirst();
                }
                postStack.removeFirst();
                postStack.addFirst(expression);
                return null;
            }
        }
        
        while (j < preStack.size()) {
            preStack.removeFirst();
        }
        preStack.addFirst(expression);
        return null;
    }

    @Override
    public Object visit(final NilExpression nilExpression, final Object notUsed) {
        postStack.addFirst(nilExpression);
        return null;
    }

    private Filter translateOr(final Or filter) {

        // a|b == ~~(a|b) negative introduction
        // ~(a|b) == (~a + ~b) modus ponens
        // ~~(a|b) == ~(~a + ~b) substitution
        // a|b == ~(~a + ~b) negative simpilification
        final Iterator<Filter> ite = filter.getChildren().iterator();
        final List translated = new ArrayList();

        while (ite.hasNext()) {
            Filter f = ite.next();

            if (f instanceof Not) {
                // simplify it
                final Not logic = (Not) f;
                final Filter next = logic.getFilter();
                translated.add(next);
            } else {
                translated.add(ff.not(f));
            }
        }

        final Filter and = ff.and(translated);
        return ff.not(and);
    }
}
