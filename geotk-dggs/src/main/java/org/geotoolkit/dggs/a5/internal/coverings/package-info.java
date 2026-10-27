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
 * A5 coverings (sets of cells at one resolution): compact/uncompact, set operations and measures.
 *
 * <p>Port of upstream a5 {@code modules/coverings}.</p>
 *
 * <p>The TS type {@code Cells = bigint[] | BigUint64Array} is {@code long[]} (raw unsigned 64 bits
 * patterns, compaction markers are negative as signed longs). The TS type {@code SlotRuns} is the
 * growable {@link org.geotoolkit.dggs.a5.internal.coverings.SlotRuns} class, which also holds the
 * functions of {@code slot-runs.ts}.</p>
 */
package org.geotoolkit.dggs.a5.internal.coverings;
