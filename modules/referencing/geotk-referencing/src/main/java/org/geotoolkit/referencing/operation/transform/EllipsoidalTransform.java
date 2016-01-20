/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.referencing.operation.transform;

import org.opengis.referencing.operation.MathTransform;


/**
 * A {@link MathTransform} where the source and/or the target coordinate system is ellipsoidal.
 * Those transforms can work on two- or three- dimensional coordinates, where the third coordinate
 * (the height) is assumed to be zero in the two-dimensional case.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @since   0.7
 * @version 0.7
 * @module
 *
 * @deprecated This approach does not work anymore in Apache SIS architecture.
 */
@Deprecated
public interface EllipsoidalTransform extends MathTransform {
    /**
     * Returns a transform performing the same calculation than {@code this}, but using the
     * specified number of dimensions. {@code EllipsoidalTransform}s work conceptually on
     * three-dimensional coordinates, but the ellipsoidal height can be omitted resulting
     * in two-dimensional coordinates. No dimensions other than 2 or 3 are allowed.
     *
     * <ul>
     *   <li>If the height is omitted from the input coordinates ({@code source3D} = {@code false}),
     *       then the {@linkplain #getSourceDimensions() source dimensions} is 2 and the height is
     *       assumed to be zero.</li>
     *   <li>If the height is omitted from the output coordinates ({@code target3D} = {@code false}),
     *       then the {@linkplain #getTargetDimensions() target dimensions} is 2 and the computed
     *       height (typically non-zero even if the input height was zero) is lost.</li>
     * </ul>
     *
     * @param  source3D {@code true} if the source coordinates have a height.
     * @param  target3D {@code true} if the target coordinates have a height.
     * @return A transform having the requested source and target dimensions (may be {@code this}).
     * @throws IllegalArgumentException if a dimension can not be changed.
     */
    EllipsoidalTransform withHeights(boolean source3D, boolean target3D) throws IllegalArgumentException;
}
