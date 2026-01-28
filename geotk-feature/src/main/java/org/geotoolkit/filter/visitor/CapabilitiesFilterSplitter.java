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
import java.util.regex.Pattern;
import org.apache.sis.filter.visitor.FunctionNames;
import org.apache.sis.filter.visitor.Visitor;
import org.geotoolkit.filter.capability.DefaultFilterCapabilities;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.feature.FeatureType;
import org.opengis.filter.BetweenComparisonOperator;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.Expression;
import org.opengis.filter.LikeOperator;
import org.opengis.filter.Literal;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.NullOperator;
import org.opengis.filter.ResourceId;
import org.opengis.filter.SpatialOperator;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.ValueReference;
import org.opengis.util.CodeList;

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
 * @author ported to work upon {@code org.geotools.filter.Capabilities} by Gabriel Roldan
 */
public class CapabilitiesFilterSplitter extends Visitor<Object,Object> {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.filter.visitor");
    private static final Pattern ID_PATTERN       = Pattern.compile("@(\\w+:)?id");
    private static final Pattern PROPERTY_PATTERN = Pattern.compile("(\\w+:)?(.+)");

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
    private Filter original;

    /**
     * Create a new instance.
     *
     * @param fcs
     *            The FilterCapabilties that describes what Filters/Expressions the server can
     *            process.
     * @param parent The FeatureType that this filter involves. Why is this needed?
     */
    public CapabilitiesFilterSplitter(final DefaultFilterCapabilities fcs, final FeatureType parent) {
        this.ff = FilterUtilities.FF;
        this.fcs = fcs;
        this.parent = parent;
        setLogicalHandlers(         (f, data) -> visitLogicOperator((LogicalOperator<Object>) f));
        setBinaryComparisonHandlers((f, data) -> visitBinaryComparisonOperator((BinaryComparisonOperator<Object>) f));
        setSpatialHandlers(         (f, data) -> visitBinarySpatialOperator((BinarySpatialOperator<Object>) f));
        setFilterHandler(SpatialOperatorName.BBOX, (f, data) -> {
            final SpatialOperator<Object> filter = (SpatialOperator<Object>) f;
            if (!fcs.supports(filter)) {
                postStack.addFirst(filter);
            } else {
                preStack.addFirst(filter);
            }
        });
        setFilterHandler(Filter.exclude().getOperatorType(), (f, data) -> {
            if (fcs.supports(Filter.exclude())) {
                preStack.addFirst(f);
            } else {
                postStack.addFirst(f);
            }
        });
        setFilterHandler(AbstractVisitor.RESOURCEID_NAME,                                   (f, data) -> visit((ResourceId) f));
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_BETWEEN), (f, data) -> visit((BetweenComparisonOperator<Object>) f));
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_LIKE),    (f, data) -> visit((LikeOperator<Object>) f));
        setFilterHandler(ComparisonOperatorName.valueOf(FunctionNames.PROPERTY_IS_NULL),    (f, data) -> visit((NullOperator<Object>) f));
        setExpressionHandler(FunctionNames.ValueReference,  (e, data) -> {
            final ValueReference expression = (ValueReference) e;
            if(!support(expression.getXPath())){
                //not a simple propert name, xpath or something else.
                postStack.addFirst(expression);
            }
            // JD: use an expression to get at the attribute type intead of accessing directly
            if (parent != null && expression.apply(parent) == null) {
                throw new IllegalArgumentException("Property '" + expression.getXPath() + "' could not be found in " + parent.getName());
            }
            preStack.addFirst(expression);
        });
        setExpressionHandler(FunctionNames.Literal,  (e, data) -> {
            final Literal expression = (Literal) e;
            if (expression.getValue() == null) {
                postStack.addFirst(expression);
            }
            preStack.addFirst(expression);
        });
        setMathHandlers((e, data) -> {
            visitMathExpression(e);
        });
    }

    @Override
    protected void typeNotFound(final String name, final Expression<Object,?> e, final Object data) {
        //we don't have informations yet on supported function so we consider they are not supported.
        if (true){
            postStack.addFirst(e);
        }
        /*if (!fcs.fullySupports(expression)) {
        postStack.addFirst(expression);
        return null;
        }*/
        if (e.getFunctionName() == null) {
            postStack.addFirst(e);
        }
        final int i = postStack.size();
        final int j = preStack.size();
        final List<Expression<Object,?>> parameters = e.getParameters();
        for (int k = 0; k < parameters.size(); k++) {
            visit(parameters.get(k), null);
            if (i < postStack.size()) {
                while (j < preStack.size()) {
                    preStack.removeFirst();
                }
                postStack.removeFirst();
                postStack.addFirst(e);
            }
        }
        while (j < preStack.size()) {
            preStack.removeFirst();
        }
        preStack.addFirst(e);
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
        return (postStack.isEmpty()) ? Filter.include() : (Filter) postStack.peek();
    }

    /**
     * Gets the filter that can be sent to the server for pre-processing.
     *
     * @return the filter that can be sent to the server for pre-processing.
     */
    public Filter getPreFilter() {
        if (preStack.isEmpty()) {
            return Filter.include();
        }
        if (preStack.size() > 1) {
            LOGGER.warning("Too many pre stack items after run: " + preStack.size());
        }
        // JE: Changed to peek because get implies that the value can be retrieved multiple times
        final Filter f = preStack.isEmpty() ? Filter.include() : (Filter) preStack.peek();
        if (changedStack.isEmpty()) {
            return f;
        }
        Iterator iter = changedStack.iterator();
        Filter updateFilter = (Filter) iter.next();
        while (iter.hasNext()) {
            Filter next = (Filter) iter.next();
            if (next == Filter.include()) {
                updateFilter = next;
                break;
            } else {
                updateFilter = (Filter) ff.or(updateFilter, next);
            }
        }
        if (updateFilter == Filter.include() || f == Filter.include()) {
            return Filter.include();
        }
        return ff.or(f, updateFilter);
    }

    /**
     * @see FilterVisitor#visit(PropertyIsBetween, Object) NOTE: This method is extra documented as
     *      an example of how all the other methods are implemented. If you want to know how this
     *      class works read this method first!
     *
     * @param filter the {@link Filter} to visit
     */
    private void visit(final BetweenComparisonOperator filter) {
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
        }
        // Do we support this filter type at all?
        // Yes, we do. Now, can we support the sub-filters?
        // first, remember how big the current list of "I can't support these" filters is.
        final int i = postStack.size();
        final Expression lowerBound = filter.getLowerBoundary();
        final Expression expr       = filter.getExpression();
        final Expression upperBound = filter.getUpperBoundary();
        if (lowerBound == null || upperBound == null || expr == null) {
            // Well, one of the boundaries is null, so I guess
            // we're saying that *no* featurestore could support this.
            postStack.addFirst(filter);
        }
        // Ok, here's the magic. We know how big our list of "can't support"
        // filters is. Now we send off the lowerBound Expression to see if
        // it can be supported.
        visit(lowerBound, null);

        // Now we're back, and we check. Did the postStack get bigger?
        if (i < postStack.size()) {
            // Yes, it did. Well, that means we can't support
            // this particular filter. Let's back out anything that was
            // added by the lowerBound.accept() and add ourselves.
            postStack.removeFirst(); // lowerBound.accept()'s bum filter
            postStack.addFirst(filter);
        }
        // Aha! The postStack didn't get any bigger, so we're still
        // all good. Now try again with the middle expression itself...
        visit(expr, null);

        // Did postStack get bigger?
        if (i < postStack.size()) {
            // Yes, it did. So that means we can't support
            // this particular filter. We need to back out what we've
            // done, which is BOTH the lowerbounds filter *and* the
            // thing that was added by expr.accept() when it failed.
            preStack.removeFirst(); // lowerBound.accept()'s success
            postStack.removeFirst(); // expr.accept()'s bum filter
            postStack.addFirst(filter);
        }

        // Same deal again...
        visit(upperBound, null);
        if (i < postStack.size()) {
            // post process it
            postStack.removeFirst(); // upperBound.accept()'s bum filter
            preStack.removeFirst(); // expr.accept()'s success
            preStack.removeFirst(); // lowerBound.accept()'s success
            postStack.addFirst(filter);
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
    }

    private void visitBinaryComparisonOperator(final BinaryComparisonOperator<Object> filter) {
        if (original == null) {
            original = filter;
        }
        // supports it as a group -- no need to check the type
        if (!fcs.supports(filter)) {
            postStack.addFirst(filter);
            return;
        }
        final int i = postStack.size();
        final Expression leftValue  = filter.getOperand1();
        final Expression rightValue = filter.getOperand2();
        if (leftValue == null || rightValue == null) {
            postStack.addFirst(filter);
            return;
        }
        visit(leftValue, null);
        if (i < postStack.size()) {
            postStack.removeFirst();
            postStack.addFirst(filter);
            return;
        }
        visit(rightValue, null);
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

    private void visitBinarySpatialOperator(final BinarySpatialOperator<Object> filter) {
        if (original == null) {
            original = filter;
        }
        if (!fcs.supports(filter)) {
            postStack.addFirst(filter);
            return;
        }
        final int i = postStack.size();
        final Expression leftGeometry  = filter.getOperand1();
        final Expression rightGeometry = filter.getOperand2();
        if (leftGeometry == null || rightGeometry == null) {
            postStack.addFirst(filter);
            return;
        }
        visit(leftGeometry, null);
        if (i < postStack.size()) {
            postStack.removeFirst();
            postStack.addFirst(filter);
            return;
        }
        visit(rightGeometry, null);
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

    private void visit(final LikeOperator<Object> filter) {
        if (original == null) {
            original = filter;
        }
        // if (!fcs.supports(PropertyIsLike.class)) {
        if (!fcs.supports(filter)) {
            postStack.addFirst(filter);
        }
        final int i = postStack.size();
        visit(filter.getExpressions().get(0), null);
        if (i < postStack.size()) {
            postStack.removeFirst();
            postStack.addFirst(filter);
        }
        preStack.removeFirst(); // value
        preStack.addFirst(filter);
    }

    private void visitLogicOperator(final LogicalOperator<Object> filter) {
        if (original == null) {
            original = filter;
        }
        if (!fcs.supports(filter)) {
            postStack.addFirst(filter);
            return;
        }
        final int i = postStack.size();
        final int j = preStack.size();
        LogicalOperatorName type = filter.getOperatorType();
        if (type == LogicalOperatorName.NOT) {
            final Filter subFilter = filter.getOperands().get(0);
            if (subFilter != null) {
                visit(subFilter, null);
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
        } else if (type == LogicalOperatorName.OR) {
            final Filter orReplacement = translateOr(filter);
            visit(orReplacement, null);

            if (postStack.size() > i) {
                removeFirstToSize(postStack, i);
                postStack.addFirst(filter);
                return;
            }
            preStack.removeFirst();
            preStack.addFirst(filter);
        } else if (type == LogicalOperatorName.AND) {
            // it's an AND
            final Iterator it = filter.getOperands().iterator();
            while (it.hasNext()) {
                final Filter next = (Filter) it.next();
                visit(next, null);
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
        } else {
            throw new IllegalArgumentException("LogicFilter found which is not 'and, or, not : " + filter);
        }
    }

    private void removeFirstToSize(final Deque stack, final int j) {
        while (j < stack.size()) {
            stack.removeFirst();
        }
    }

    private Object visit(final NullOperator<Object> filter) {
        if (original == null) {
            original = filter;
        }
        if (!fcs.supports(filter)) {
            postStack.addFirst(filter);
            return null;
        }
        final int i = postStack.size();
        visit(filter.getExpressions().get(0), null);
        if (i < postStack.size()) {
            postStack.removeFirst();
            postStack.addFirst(filter);
        }
        preStack.removeFirst(); // null
        preStack.addFirst(filter);
        return null;
    }

    private Object visit(final ResourceId filter) {
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

    private static boolean support(String xpath) {
        return !xpath.startsWith("/") &&
               !xpath.startsWith("*") &&
               (PROPERTY_PATTERN.matcher(xpath).matches() || ID_PATTERN.matcher(xpath).matches());
    }

    private void visitMathExpression(final Expression<Object,?> expression) {
        // if (!fcs.supports(Add.class) && !fcs.supports(Subtract.class)
        // && !fcs.supports(Multiply.class) && !fcs.supports(Divide.class)) {
        /*if (!fcs.fullySupports(expression)) {
        postStack.addFirst(expression);
        return;
        }*/

        final int i = postStack.size();
        final Expression leftValue = expression.getParameters().get(0);
        final Expression rightValue = expression.getParameters().get(1);
        if (leftValue == null || rightValue == null) {
            postStack.addFirst(expression);
            return;
        }
        visit(leftValue, null);
        if (i < postStack.size()) {
            postStack.removeFirst();
            postStack.addFirst(expression);
            return;
        }
        visit(rightValue, null);
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

    private Filter translateOr(final LogicalOperator<Object> filter) {

        // a|b == ~~(a|b) negative introduction
        // ~(a|b) == (~a + ~b) modus ponens
        // ~~(a|b) == ~(~a + ~b) substitution
        // a|b == ~(~a + ~b) negative simpilification
        final Iterator<Filter<Object>> ite = filter.getOperands().iterator();
        final List translated = new ArrayList();
        while (ite.hasNext()) {
            Filter f = ite.next();
            if (f.getOperatorType() == LogicalOperatorName.NOT) {
                // simplify it
                final Filter next = ((LogicalOperator<Object>) f).getOperands().get(0);
                translated.add(next);
            } else {
                translated.add(ff.not(f));
            }
        }
        final Filter and = ff.and(translated);
        return ff.not(and);
    }

    @Override
    protected void typeNotFound(final CodeList<?> type, final Filter<Object> filter, final Object accumulator) {
    }
}
