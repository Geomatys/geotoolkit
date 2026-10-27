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
 * Conversions between unsigned 64 bits cell ids and hexadecimal strings.
 *
 * <p>Port of upstream {@code core/hex.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Hex {

    private Hex() {
    }

    /**
     * @param hex hexadecimal string, without {@code 0x} prefix
     * @return unsigned 64 bits value
     */
    public static long hexToU64(String hex) {
        return Long.parseUnsignedLong(hex, 16);
    }

    /**
     * @param index unsigned 64 bits value
     * @return lower case hexadecimal string, without {@code 0x} prefix nor leading zeros
     */
    public static String u64ToHex(long index) {
        return Long.toUnsignedString(index, 16);
    }
}
