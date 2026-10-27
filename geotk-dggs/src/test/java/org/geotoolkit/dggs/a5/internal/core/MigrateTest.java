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

import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.core.Hex.hexToU64;
import static org.geotoolkit.dggs.a5.internal.core.Hex.u64ToHex;
import static org.geotoolkit.dggs.a5.internal.core.Migrate.migrate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/migrate.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class MigrateTest {

    private static final JsonNode fixtures = Fixtures.load("fixtures/migrate.json");

    // ---- describe: migrate ----
    @Test
    void maps_v0_cell_ids_to_v1_cell_ids() {
        for (final JsonNode f : fixtures) {
            final String v0 = f.get("v0").asText();
            assertEquals(f.get("v1").asText(), u64ToHex(migrate(hexToU64(v0))), "v0 " + v0);
        }
    }
}
