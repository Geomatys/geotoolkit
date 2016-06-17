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

package org.geotoolkit.metadata;

/**
 * This class is a copy of the enum in csw xml binding in order to use it without havinf to import all the dependencies of this module.
 * 
 * @author Guilhem Legal
 */
@Deprecated
public enum ElementSetType {

    BRIEF("brief"),
    SUMMARY("summary"),
    FULL("full");
    private final String value;

    ElementSetType(final String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ElementSetType fromValue(final String v) {
        for (ElementSetType c: ElementSetType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
