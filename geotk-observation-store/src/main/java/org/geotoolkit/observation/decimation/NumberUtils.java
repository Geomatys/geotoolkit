/*
 *    Examind - An open source and standard compliant SDI
 *    https://community.examind.com
 *
 * Copyright 2026 Geomatys.
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
package org.geotoolkit.observation.decimation;

import java.util.Comparator;

/**
 * Utilties to work on Number that are either all Double or all Long.
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NumberUtils {

    public static class NumberComparator implements Comparator<Number> {

        @Override
        public int compare(Number a, Number b) {
            if (a instanceof Long) {
                if (b instanceof Long) {
                    return Long.compare(a.longValue(), b.longValue());
                } else if (b instanceof Integer) {
                    return Long.compare(a.longValue(), b.intValue());
                }
            }
            if (a instanceof Double) {
                if (b instanceof Double) {
                    return Double.compare(a.doubleValue(), b.doubleValue());
                } else if (b instanceof Integer) {
                    return Double.compare(a.doubleValue(), b.intValue());
                }
            }
            throw new IllegalArgumentException("Inconsistent mathematic operation (main values sort)");
        }
    }

    public static Number add(Number n1, Number n2) {
        if (n1 instanceof Long l1 && n2 instanceof Long l2) {
            return l1 + l2;
        }
        if (n1 instanceof Double d1 && n2 instanceof Double d2) {
            return d1 + d2;
        }
        throw new IllegalArgumentException("Inconsistent mathematic operation (add)");
    }

    public static Number div(Number n1, Number n2) {
        if (n1 instanceof Long l1) {
            if (n2 instanceof Long l2) {
                return l1 / l2;
            } else if (n2 instanceof Integer i2) {
                return l1 / i2;
            }
        }
        if (n1 instanceof Double d1) {
            if (n2 instanceof Double d2) {
                return d1 / d2;
            } else if (n2 instanceof Integer i2) {
                return d1 / i2;
            }
        }
        throw new IllegalArgumentException("Inconsistent mathematic operation (div)");
    }

    public static Number mul(Number n1, Number n2) {
        if (n1 instanceof Long l1) {
            if (n2 instanceof Long l2) {
                return l1 * l2;
            } else if (n2 instanceof Integer i2) {
                return l1 * i2;
            }
        }
        if (n1 instanceof Double d1) {
            if (n2 instanceof Double d2) {
                return d1 * d2;
            } else if (n2 instanceof Integer i2) {
                return d1 * i2;
            }
        }
        throw new IllegalArgumentException("Inconsistent mathematic operation (div)");
    }
}
