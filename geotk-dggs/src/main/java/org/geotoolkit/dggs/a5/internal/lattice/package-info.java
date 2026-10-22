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
/**
 * The A5 triangular lattice and its space-filling curve.
 * Port of upstream a5 {@code modules/lattice}.
 *
 * <p>The canonical A5 curve is the non-self-intersecting L-system curve
 * ({@code lsystem} subpackage + {@link org.geotoolkit.dggs.a5.internal.lattice.Curve}): point location via
 * {@code roundToTriple}, s &lt;-&gt; cell mappings via sToCell / sToTriple / tripleToS. This is a
 * breaking change from previous releases — cell IDs differ from the original construction. The
 * original curve remains available bit-for-bit via the compat* functions for migration.</p>
 *
 * <p>Upstream {@code lattice/index.ts} is a barrel module; its exports are found in:</p>
 * <ul>
 *   <li>{@code Orientation} (type) : {@link org.geotoolkit.dggs.a5.internal.lattice.Orientation}</li>
 *   <li>{@code Triple} (type) : {@link org.geotoolkit.dggs.a5.internal.lattice.Triple}</li>
 *   <li>{@code roundToTriple} : {@link org.geotoolkit.dggs.a5.internal.lattice.Curve}</li>
 *   <li>{@code curveChild, sToCell, sToTriple, tripleToCurveNode} : {@link org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem}</li>
 *   <li>{@code Cell, CurveNode} (types) : {@link org.geotoolkit.dggs.a5.internal.lattice.lsystem.Cell},
 *       {@link org.geotoolkit.dggs.a5.internal.lattice.lsystem.CurveNode}</li>
 *   <li>{@code tripleParity, tripleInBounds, tripleFlavor, tripleToS} : {@link org.geotoolkit.dggs.a5.internal.lattice.Triples}</li>
 *   <li>{@code compatSToCell, compatSToTriple, compatTripleToS, compatIJToS} : {@link org.geotoolkit.dggs.a5.internal.lattice.Compat}</li>
 * </ul>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
package org.geotoolkit.dggs.a5.internal.lattice;
