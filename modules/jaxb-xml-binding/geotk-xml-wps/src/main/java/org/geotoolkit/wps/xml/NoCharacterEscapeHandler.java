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

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author Guilhem Legal (Geomatys)
 *
 *
 * This class avoid escaping special character that are in CDATA xml tags
 */
public class NoCharacterEscapeHandler implements CharacterEscapeHandler {

    private static final String CDATA_START_TAG = "<![CDATA[";


    @Override
    public void escape(char[] buf, int start, int len, boolean b, Writer out) throws IOException {
        if (len > CDATA_START_TAG.length()) {
            String s = new String(Arrays.copyOfRange(buf, start, CDATA_START_TAG.length()));
            if(s.equals(CDATA_START_TAG)) {
                out.write(buf, start, len);
                return;
            }
        }

        String escapedString = StringEscapeUtils.escapeXml(new String(Arrays.copyOfRange(buf, start, len)));
        out.write(escapedString);

    }

}
