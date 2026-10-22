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
 * Orientation of the space-filling curve. The curve fills a space defined by the triangle with
 * vertices u, v &amp; w. The orientation describes which corner the curve starts and ends at, e.g. wv
 * is a curve that starts at w and ends at v.
 *
 * <p>Port of the {@code Orientation} type of upstream {@code lattice/types.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public enum Orientation {
    uv, vu, uw, wu, vw, wv
}
