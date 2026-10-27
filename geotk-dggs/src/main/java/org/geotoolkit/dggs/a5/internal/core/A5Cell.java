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
package org.geotoolkit.dggs.a5.internal.core;

/**
 * Decoded A5 cell.
 *
 * <p>Port of the {@code A5Cell} type of upstream {@code core/utils.ts}.</p>
 *
 * @param origin Origin representing one of pentagon face of the dodecahedron
 * @param segment Index (0-4) of triangular segment within pentagonal dodecahedron face
 * @param S Position along the curve within triangular segment (unsigned 64 bits value)
 * @param resolution Resolution of the cell
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public record A5Cell(
        Origin origin,
        int segment,
        long S,
        int resolution) {
}
