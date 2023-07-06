/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.wps.xml;

import org.glassfish.jaxb.core.marshaller.CharacterEscapeHandler;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import org.apache.commons.lang.StringEscapeUtils;
import static org.geotoolkit.wps.xml.WPSUtilities.CDATA_START_TAG;

/**
 *
 * @author Guilhem Legal (Geomatys)
 *
 *
 * This class avoid escaping special character that are in CDATA xml tags
 */
public class NoCharacterEscapeHandler implements CharacterEscapeHandler {

    @Override
    public void escape(char[] buf, int start, int len, boolean b, Writer out) throws IOException {
        if (len > CDATA_START_TAG.length()) {
            String s = new String(Arrays.copyOfRange(buf, start, CDATA_START_TAG.length()));
            if(s.equals(CDATA_START_TAG)) {
                out.write(buf, start, len);
                return;
            }
        }

        String string = new String(Arrays.copyOfRange(buf, start, len));

        String escapedString = StringEscapeUtils.escapeXml(string);
        out.write(escapedString);

    }

}
