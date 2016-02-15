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
package org.geotoolkit.referencing.factory.epsg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.opengis.util.InternationalString;
import org.apache.sis.internal.util.Constants;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.referencing.factory.sql.InstallationScriptProvider;


/**
 * Provides SQL scripts for creating the EPSG database.
 * Note that the EPSG scripts are owned by IOGP.
 * This provider can be included on the classpath only if the user accepted the
 * <a href="http://www.epsg.org/TermsOfUse">EPSG terms of use</a>.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public class ScriptProvider extends InstallationScriptProvider {
    /**
     * Creates a new script provider.
     */
    public ScriptProvider() {
        super("Tables.sql", "Data.sql", "FKeys.sql");
    }

    /**
     * Returns the "EPSG" authority.
     *
     * @return {@code "EPSG"}.
     */
    @Override
    public String getAuthority() {
        return Constants.EPSG;
    }

    /**
     * Returns the license.
     *
     * @param  mimeType Either {@code "text/plain"} or {@code "text/html"}.
     * @return The terms of use in plain text or HTML, or {@code null} if the license is presumed already accepted.
     * @throws IOException if an error occurred while reading the license file.
     */
    @Override
    public InternationalString getLicense(final String mimeType) throws IOException {
        final String filename;
        switch (mimeType) {
            case "text/plain": filename = "LICENSE.txt";  break;
            case "text/html":  filename = "LICENSE.html"; break;
            default: return null;
        }
        final StringBuilder buffer = new StringBuilder();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(
                ScriptProvider.class.getResourceAsStream(filename), StandardCharsets.UTF_8)))
        {
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line).append('\n');
            }
        }
        return new SimpleInternationalString(buffer.toString());
    }

    /**
     * Returns the content for the SQL script of the given name.
     *
     * @param name Either {@code "Tables.sql"}, {@code "Data.sql"} or {@code "FKeys.sql"}.
     * @return The SQL script of the given name, or {@code null} if the given name is not one of the expected names.
     */
    @Override
    protected InputStream open(final String name) {
        return ScriptProvider.class.getResourceAsStream(name);
    }
}
