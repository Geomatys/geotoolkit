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

import static org.geotoolkit.dggs.a5.internal.core.Hex.hexToU64;
import static org.geotoolkit.dggs.a5.internal.core.Hex.u64ToHex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/hex.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class HexTest {

    // ---- describe: HexToU64 ----
    @Test
    void converts_hex_strings_to_BigInt() {
        assertEquals(1715004L, hexToU64("1a2b3c"));
        assertEquals(0L, hexToU64("0"));
        assertEquals(255L, hexToU64("ff"));
        assertEquals(4294967295L, hexToU64("ffffffff"));
    }

    // ---- describe: U64ToHex ----
    @Test
    void converts_BigInt_to_hex_strings() {
        assertEquals("1a2b3c", u64ToHex(1715004L));
        assertEquals("0", u64ToHex(0L));
        assertEquals("ff", u64ToHex(255L));
        assertEquals("ffffffff", u64ToHex(4294967295L));
    }

    // ---- describe: RoundTripConversion ----
    @Test
    void preserves_values_when_converting_back_and_forth() {
        final String[] testValues = {"1a2b3c", "0", "ff", "ffffffff"};

        for (final String hexStr : testValues) {
            final long bigInt = hexToU64(hexStr);
            final String result = u64ToHex(bigInt);
            assertEquals(hexStr, result);
        }
    }
}
