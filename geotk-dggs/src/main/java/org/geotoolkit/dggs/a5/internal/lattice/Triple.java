/**
 * Copyright (C) 2026 Geomatys and A5 contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.dggs.a5.internal.lattice;

/**
 * Triple coordinates for the triangular grid underlying the pentagonal A5 grid.
 *
 * Neighbors differ by ±1 in exactly one coordinate while the other two stay constant.
 * Triple coordinates are orientation-independent — the same geometric cell always has
 * the same triple coords regardless of curve orientation.
 *
 * <p>Port of the {@code Triple} type of upstream {@code lattice/types.ts}.
 * Components are {@code int}: they are always integers and bounded by
 * ±2<sup>curveLevel+1</sup> (curve level &lt;= 29, i.e. A5 resolution 30), intermediate values that may exceed
 * the int range (corner sums) are computed in {@code long} or {@code double}.</p>
 *
 * @param x x coordinate
 * @param y y coordinate
 * @param z z coordinate
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public record Triple(int x, int y, int z) {
}
