/*
 * Copyright 2016-2021, 2024 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.dggs.h3.internal.shared;

/**
 * Most of the code here is a java port from H3geo.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Constants {

    /**
     * max H3 resolution; H3 version 1 has 16 resolutions, numbered 0 through 15
     */
    public static final int MAX_H3_RES = 15;
    /**
     * Invalid index used to indicate an error from latLngToCell and related
     * functions or missing data in arrays of H3 indices. Analogous to NaN in
     * floating point.
     */
    public static final long H3_NULL = 0;

    private Constants() {}
    /**
    * _ipow does integer exponentiation efficiently. Taken from StackOverflow.
    *
    * @param base the integer base (can be positive or negative)
    * @param exp the integer exponent (should be nonnegative)
    *
    * @return the exponentiated value
    */
   public static long ipow(long base, long exp) {
       long result = 1;
       while (exp != 0) {
           if ((exp & 1) != 0) result *= base;
           exp >>= 1;
           base *= base;
       }
       return result;
   }
}
