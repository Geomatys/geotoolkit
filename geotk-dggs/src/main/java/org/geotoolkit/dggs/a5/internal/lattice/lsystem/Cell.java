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
package org.geotoolkit.dggs.a5.internal.lattice.lsystem;

import org.geotoolkit.dggs.a5.internal.lattice.Triple;

/**
 * A cell as the descent identifies it: its triple + its pentagon flavor.
 *
 * <p>Port of the {@code Cell} interface of upstream {@code lattice/lsystem/index.ts}.</p>
 *
 * @param triple triple coordinates
 * @param flavor pentagon flavor (0-3)
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public record Cell(Triple triple, int flavor) {
}
