/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.display2d.style;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Logger;
import static org.apache.sis.util.ArgumentChecks.*;

/**
 * General interface for cached style element.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class Cache<T extends Object> {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.display2d.style");

    /**
     * Empty collection used for attributes when the style needs
     * no feature attributes or if the style can not be visible.
     */
    public static final Collection<String> EMPTY_ATTRIBUTS = Collections.emptyList();

    /**
     * flag used to see is the style has already been evaluate.
     * true if not yet evaluate
     */
    protected boolean isNotEvaluated = true;

    /**
     * flag to know if the style is static, if static
     * it means every possible cache is use.
     */
    protected boolean isStatic = false;

    /**
     * Visibility of this style.
     * NOT_DEFINED : when the style has not been evaluated yet
     * VISIBLE : we are sure the style is always visible even if
     * some attributes are dynamic, CAUTION this doesn't test very small
     * size. a width of 0.00000000001 is considered VISIBLE
     * DYNAMIC : the visibility of this style depends of the feature
     * UNVISIBLE : whatever feature or parameter you give this style
     * will never be visible. nothing is cache is this case.
     */
    protected VisibilityState isStaticVisible = VisibilityState.NOT_DEFINED;

    /**
     * collection of all required feature attributes needed by this style.
     * this should be empty if
     */
    protected Collection<String> requieredAttributs = new HashSet<String>();

    /**
     * original style element
     */
    protected final T styleElement;

    protected Cache(final T element){
        ensureNonNull("style element", element);
        styleElement = element;
    }

    /**
     * @return the original object
     */
    public T getSource(){
        return styleElement;
    }

    /**
     * Called only once when a value is requested, this
     * evaluate the style element and store what can be cached.
     *
     * first code line should be : if(!isNotEvaluated) return;
     */
    protected abstract void evaluate();

    /**
     * Evaluate if this style is visible on the given feature.
     * this method shoud be called only if
     * isStaticVisible == VISIBLE OR DYNAMIC
     *
     * @param feature : feature to evaluate
     * @return true is the feature is visible
     */
    public abstract boolean isVisible(Object candidate);

    /**
     * Returns the list of attributs requiered by this style.
     * This can be used to help caching a light version of the feature.
     *
     * @return Collection<String> : all requiered feature attributs name
     */
    public Collection<String> getRequieredAttributsName(final Collection<String> buffer){
        evaluate();
        if(buffer != null){
            buffer.addAll(requieredAttributs);
            return buffer;
        }else{
            return requieredAttributs;
        }
    }

    /**
     * <p>Returns true if the style is static.
     * If the style is static, it is fully cached
     * and whatever parameter you give him the result
     * will always be the same on any feature.</p>
     *
     * <p>if istatic == true and isStaticVsible == UNVISIBLE
     * there is nothing to paint and nothing to cache</p>
     *
     * @return true if this style is static
     */
    public boolean isStatic(){
        evaluate();
        return isStatic;
    }

    /**
     * See the visibility extend of this style.
     *
     * <p>if istatic == true and isStaticVsible == UNVISIBLE
     * there is nothing to paint</p>
     *
     * @return VISIBLE, UNVISIBLE or DYNAMIC
     */
    public VisibilityState isStaticVisible(){
        evaluate();
        return isStaticVisible;
    }
}
