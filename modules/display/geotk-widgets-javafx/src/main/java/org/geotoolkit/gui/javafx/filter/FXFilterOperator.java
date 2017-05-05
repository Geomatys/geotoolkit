/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.filter;

import java.util.Optional;
import javafx.scene.Node;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.util.InternationalString;

/**
 * An operator which generate a filter over a given expression. To parameterize
 * the filter (Ex : if the filter is an equality, the object to compare expression
 * over must be specified), an editor can be provided by current operator.
 *
 * Note : To define the best editor to provide, the operator must know the property
 * type of the expression to evaluate.
 *
 * @author Alexis Manin (Geomatys)
 */
public interface FXFilterOperator {

    /**
     * Check if a property of a specific class can be filtered by current class.
     * @param target The descriptor of the property to test.
     * @return True if current filter can handle the property, false otherwise.
     */
    boolean canHandle(PropertyType target);

    /**
     * A display title for current filter operation.
     * @return A character sequence (Ex: {@link InternationalString} to use as display name.
     */
    CharSequence getTitle();

    /**
     * If the current filter can be parameterized by user, this operator will provide
     * a new javafx editor to set those parameters.
     *
     * @param target property descriptor of the expression to filter over.
     *
     * @return A node containing editor for filtering setting, or an empty object if
     * no setting can be done on current operator.
     */
    Optional<Node> createFilterEditor(PropertyType target);

    /**
     * Check if current operator can extract settings from input node being compatible with a specific property type.
     *
     * i.e The filter operator can read filter parameters from the node, so it can
     * be used at filter built (in {@link #getFilterOver(org.opengis.filter.expression.Expression, javafx.scene.Node)}).
     *
     * @param propertyType Type of the property filtered with parameters extracted from input node.
     * @param settingsContainer The node to scan.
     * @return True if given node is compatible, false otherwise.
     */
    boolean canExtractSettings(final PropertyType propertyType, final Node settingsContainer);

    /**
     * Build a filter operation to apply on the given expression
     * @param toApplyOn The property destined to be filtered.
     * @param filterEditor The node which have been used to parameterize filter.
     * Should be the node given by {@link #createFilterEditor(org.opengis.feature.PropertyType) }, or null if no editor is available.
     * @return A filter operation ready to work on any object matching input expression requirements.
     */
    Filter getFilterOver(final Expression toApplyOn, final Node filterEditor);
}
