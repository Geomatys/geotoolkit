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
final class BaseCells {

    private static final int NUM_BASE_CELLS = 122;

    private static record FaceIJK(int f, int[] ijk){}
    /**
     * @struct BaseCellData
     * @brief information on a single base cell
     */
    private static record BaseCellData(
        FaceIJK homeFijk,   ///< "home" face and normalized ijk coordinates on that face
        int isPentagon, ///< is this base cell a pentagon?
        int[] cwOffsetPent ///< if a pentagon, what are its two clockwise offset
            ){}

    /** @brief Resolution 0 base cell data table.
    *
    * For each base cell, gives the "home" face and ijk+ coordinates on that face,
    * whether or not the base cell is a pentagon. Additionally, if the base cell
    * is a pentagon, the two cw offset rotation adjacent faces are given (-1
    * indicates that no cw offset rotation faces exist for this base cell).
    */
   private static BaseCellData[] baseCellData = new BaseCellData[]{

       new BaseCellData(new FaceIJK( 1, new int[]{1, 0, 0}), 0, new int[]{0, 0}),     // base cell 0
       new BaseCellData(new FaceIJK( 2, new int[]{1, 1, 0}), 0, new int[]{0, 0}),     // base cell 1
       new BaseCellData(new FaceIJK( 1, new int[]{0, 0, 0}), 0, new int[]{0, 0}),     // base cell 2
       new BaseCellData(new FaceIJK( 2, new int[]{1, 0, 0}), 0, new int[]{0, 0}),     // base cell 3
       new BaseCellData(new FaceIJK( 0, new int[]{2, 0, 0}), 1, new int[]{-1, -1}),   // base cell 4
       new BaseCellData(new FaceIJK( 1, new int[]{1, 1, 0}), 0, new int[]{0, 0}),     // base cell 5
       new BaseCellData(new FaceIJK( 1, new int[]{0, 0, 1}), 0, new int[]{0, 0}),     // base cell 6
       new BaseCellData(new FaceIJK( 2, new int[]{0, 0, 0}), 0, new int[]{0, 0}),     // base cell 7
       new BaseCellData(new FaceIJK( 0, new int[]{1, 0, 0}), 0, new int[]{0, 0}),     // base cell 8
       new BaseCellData(new FaceIJK( 2, new int[]{0, 1, 0}), 0, new int[]{0, 0}),     // base cell 9
       new BaseCellData(new FaceIJK( 1, new int[]{0, 1, 0}), 0, new int[]{0, 0}),     // base cell 10
       new BaseCellData(new FaceIJK( 1, new int[]{0, 1, 1}), 0, new int[]{0, 0}),     // base cell 11
       new BaseCellData(new FaceIJK( 3, new int[]{1, 0, 0}), 0, new int[]{0, 0}),     // base cell 12
       new BaseCellData(new FaceIJK( 3, new int[]{1, 1, 0}), 0, new int[]{0, 0}),     // base cell 13
       new BaseCellData(new FaceIJK(11, new int[]{2, 0, 0}), 1, new int[]{2, 6}),    // base cell 14
       new BaseCellData(new FaceIJK( 4, new int[]{1, 0, 0}), 0, new int[]{0, 0}),     // base cell 15
       new BaseCellData(new FaceIJK( 0, new int[]{0, 0, 0}), 0, new int[]{0, 0}),     // base cell 16
       new BaseCellData(new FaceIJK( 6, new int[]{0, 1, 0}), 0, new int[]{0, 0}),     // base cell 17
       new BaseCellData(new FaceIJK( 0, new int[]{0, 0, 1}), 0, new int[]{0, 0}),     // base cell 18
       new BaseCellData(new FaceIJK( 2, new int[]{0, 1, 1}), 0, new int[]{0, 0}),     // base cell 19
       new BaseCellData(new FaceIJK( 7, new int[]{0, 0, 1}), 0, new int[]{0, 0}),     // base cell 20
       new BaseCellData(new FaceIJK( 2, new int[]{0, 0, 1}), 0, new int[]{0, 0}),     // base cell 21
       new BaseCellData(new FaceIJK( 0, new int[]{1, 1, 0}), 0, new int[]{0, 0}),     // base cell 22
       new BaseCellData(new FaceIJK( 6, new int[]{0, 0, 1}), 0, new int[]{0, 0}),     // base cell 23
       new BaseCellData(new FaceIJK(10, new int[]{2, 0, 0}), 1, new int[]{1, 5}),    // base cell 24
       new BaseCellData(new FaceIJK( 6, new int[]{0, 0, 0}), 0, new int[]{0, 0}),     // base cell 25
       new BaseCellData(new FaceIJK( 3, new int[]{0, 0, 0}), 0, new int[]{0, 0}),     // base cell 26
       new BaseCellData(new FaceIJK(11, new int[]{1, 0, 0}), 0, new int[]{0, 0}),    // base cell 27
       new BaseCellData(new FaceIJK( 4, new int[]{1, 1, 0}), 0, new int[]{0, 0}),     // base cell 28
       new BaseCellData(new FaceIJK( 3, new int[]{0, 1, 0}), 0, new int[]{0, 0}),     // base cell 29
       new BaseCellData(new FaceIJK( 0, new int[]{0, 1, 1}), 0, new int[]{0, 0}),     // base cell 30
       new BaseCellData(new FaceIJK( 4, new int[]{0, 0, 0}), 0, new int[]{0, 0}),     // base cell 31
       new BaseCellData(new FaceIJK( 5, new int[]{0, 1, 0}), 0, new int[]{0, 0}),     // base cell 32
       new BaseCellData(new FaceIJK( 0, new int[]{0, 1, 0}), 0, new int[]{0, 0}),     // base cell 33
       new BaseCellData(new FaceIJK( 7, new int[]{0, 1, 0}), 0, new int[]{0, 0}),     // base cell 34
       new BaseCellData(new FaceIJK(11, new int[]{1, 1, 0}), 0, new int[]{0, 0}),    // base cell 35
       new BaseCellData(new FaceIJK( 7, new int[]{0, 0, 0}), 0, new int[]{0, 0}),     // base cell 36
       new BaseCellData(new FaceIJK(10, new int[]{1, 0, 0}), 0, new int[]{0, 0}),    // base cell 37
       new BaseCellData(new FaceIJK(12, new int[]{2, 0, 0}), 1, new int[]{3, 7}),    // base cell 38
       new BaseCellData(new FaceIJK( 6, new int[]{1, 0, 1}), 0, new int[]{0, 0}),     // base cell 39
       new BaseCellData(new FaceIJK( 7, new int[]{1, 0, 1}), 0, new int[]{0, 0}),     // base cell 40
       new BaseCellData(new FaceIJK( 4, new int[]{0, 0, 1}), 0, new int[]{0, 0}),     // base cell 41
       new BaseCellData(new FaceIJK( 3, new int[]{0, 0, 1}), 0, new int[]{0, 0}),     // base cell 42
       new BaseCellData(new FaceIJK( 3, new int[]{0, 1, 1}), 0, new int[]{0, 0}),     // base cell 43
       new BaseCellData(new FaceIJK( 4, new int[]{0, 1, 0}), 0, new int[]{0, 0}),     // base cell 44
       new BaseCellData(new FaceIJK( 6, new int[]{1, 0, 0}), 0, new int[]{0, 0}),     // base cell 45
       new BaseCellData(new FaceIJK(11, new int[]{0, 0, 0}), 0, new int[]{0, 0}),    // base cell 46
       new BaseCellData(new FaceIJK( 8, new int[]{0, 0, 1}), 0, new int[]{0, 0}),     // base cell 47
       new BaseCellData(new FaceIJK( 5, new int[]{0, 0, 1}), 0, new int[]{0, 0}),     // base cell 48
       new BaseCellData(new FaceIJK(14, new int[]{2, 0, 0}), 1, new int[]{0, 9}),    // base cell 49
       new BaseCellData(new FaceIJK( 5, new int[]{0, 0, 0}), 0, new int[]{0, 0}),     // base cell 50
       new BaseCellData(new FaceIJK(12, new int[]{1, 0, 0}), 0, new int[]{0, 0}),    // base cell 51
       new BaseCellData(new FaceIJK(10, new int[]{1, 1, 0}), 0, new int[]{0, 0}),    // base cell 52
       new BaseCellData(new FaceIJK( 4, new int[]{0, 1, 1}), 0, new int[]{0, 0}),     // base cell 53
       new BaseCellData(new FaceIJK(12, new int[]{1, 1, 0}), 0, new int[]{0, 0}),    // base cell 54
       new BaseCellData(new FaceIJK( 7, new int[]{1, 0, 0}), 0, new int[]{0, 0}),     // base cell 55
       new BaseCellData(new FaceIJK(11, new int[]{0, 1, 0}), 0, new int[]{0, 0}),    // base cell 56
       new BaseCellData(new FaceIJK(10, new int[]{0, 0, 0}), 0, new int[]{0, 0}),    // base cell 57
       new BaseCellData(new FaceIJK(13, new int[]{2, 0, 0}), 1, new int[]{4, 8}),    // base cell 58
       new BaseCellData(new FaceIJK(10, new int[]{0, 0, 1}), 0, new int[]{0, 0}),    // base cell 59
       new BaseCellData(new FaceIJK(11, new int[]{0, 0, 1}), 0, new int[]{0, 0}),    // base cell 60
       new BaseCellData(new FaceIJK( 9, new int[]{0, 1, 0}), 0, new int[]{0, 0}),     // base cell 61
       new BaseCellData(new FaceIJK( 8, new int[]{0, 1, 0}), 0, new int[]{0, 0}),     // base cell 62
       new BaseCellData(new FaceIJK( 6, new int[]{2, 0, 0}), 1, new int[]{11, 15}),   // base cell 63
       new BaseCellData(new FaceIJK( 8, new int[]{0, 0, 0}), 0, new int[]{0, 0}),     // base cell 64
       new BaseCellData(new FaceIJK( 9, new int[]{0, 0, 1}), 0, new int[]{0, 0}),     // base cell 65
       new BaseCellData(new FaceIJK(14, new int[]{1, 0, 0}), 0, new int[]{0, 0}),    // base cell 66
       new BaseCellData(new FaceIJK( 5, new int[]{1, 0, 1}), 0, new int[]{0, 0}),     // base cell 67
       new BaseCellData(new FaceIJK(16, new int[]{0, 1, 1}), 0, new int[]{0, 0}),    // base cell 68
       new BaseCellData(new FaceIJK( 8, new int[]{1, 0, 1}), 0, new int[]{0, 0}),     // base cell 69
       new BaseCellData(new FaceIJK( 5, new int[]{1, 0, 0}), 0, new int[]{0, 0}),     // base cell 70
       new BaseCellData(new FaceIJK(12, new int[]{0, 0, 0}), 0, new int[]{0, 0}),    // base cell 71
       new BaseCellData(new FaceIJK( 7, new int[]{2, 0, 0}), 1, new int[]{12, 16}),   // base cell 72
       new BaseCellData(new FaceIJK(12, new int[]{0, 1, 0}), 0, new int[]{0, 0}),    // base cell 73
       new BaseCellData(new FaceIJK(10, new int[]{0, 1, 0}), 0, new int[]{0, 0}),    // base cell 74
       new BaseCellData(new FaceIJK( 9, new int[]{0, 0, 0}), 0, new int[]{0, 0}),     // base cell 75
       new BaseCellData(new FaceIJK(13, new int[]{1, 0, 0}), 0, new int[]{0, 0}),    // base cell 76
       new BaseCellData(new FaceIJK(16, new int[]{0, 0, 1}), 0, new int[]{0, 0}),    // base cell 77
       new BaseCellData(new FaceIJK(15, new int[]{0, 1, 1}), 0, new int[]{0, 0}),    // base cell 78
       new BaseCellData(new FaceIJK(15, new int[]{0, 1, 0}), 0, new int[]{0, 0}),    // base cell 79
       new BaseCellData(new FaceIJK(16, new int[]{0, 1, 0}), 0, new int[]{0, 0}),    // base cell 80
       new BaseCellData(new FaceIJK(14, new int[]{1, 1, 0}), 0, new int[]{0, 0}),    // base cell 81
       new BaseCellData(new FaceIJK(13, new int[]{1, 1, 0}), 0, new int[]{0, 0}),    // base cell 82
       new BaseCellData(new FaceIJK( 5, new int[]{2, 0, 0}), 1, new int[]{10, 19}),   // base cell 83
       new BaseCellData(new FaceIJK( 8, new int[]{1, 0, 0}), 0, new int[]{0, 0}),     // base cell 84
       new BaseCellData(new FaceIJK(14, new int[]{0, 0, 0}), 0, new int[]{0, 0}),    // base cell 85
       new BaseCellData(new FaceIJK( 9, new int[]{1, 0, 1}), 0, new int[]{0, 0}),     // base cell 86
       new BaseCellData(new FaceIJK(14, new int[]{0, 0, 1}), 0, new int[]{0, 0}),    // base cell 87
       new BaseCellData(new FaceIJK(17, new int[]{0, 0, 1}), 0, new int[]{0, 0}),    // base cell 88
       new BaseCellData(new FaceIJK(12, new int[]{0, 0, 1}), 0, new int[]{0, 0}),    // base cell 89
       new BaseCellData(new FaceIJK(16, new int[]{0, 0, 0}), 0, new int[]{0, 0}),    // base cell 90
       new BaseCellData(new FaceIJK(17, new int[]{0, 1, 1}), 0, new int[]{0, 0}),    // base cell 91
       new BaseCellData(new FaceIJK(15, new int[]{0, 0, 1}), 0, new int[]{0, 0}),    // base cell 92
       new BaseCellData(new FaceIJK(16, new int[]{1, 0, 1}), 0, new int[]{0, 0}),    // base cell 93
       new BaseCellData(new FaceIJK( 9, new int[]{1, 0, 0}), 0, new int[]{0, 0}),     // base cell 94
       new BaseCellData(new FaceIJK(15, new int[]{0, 0, 0}), 0, new int[]{0, 0}),    // base cell 95
       new BaseCellData(new FaceIJK(13, new int[]{0, 0, 0}), 0, new int[]{0, 0}),    // base cell 96
       new BaseCellData(new FaceIJK( 8, new int[]{2, 0, 0}), 1, new int[]{13, 17}),   // base cell 97
       new BaseCellData(new FaceIJK(13, new int[]{0, 1, 0}), 0, new int[]{0, 0}),    // base cell 98
       new BaseCellData(new FaceIJK(17, new int[]{1, 0, 1}), 0, new int[]{0, 0}),    // base cell 99
       new BaseCellData(new FaceIJK(19, new int[]{0, 1, 0}), 0, new int[]{0, 0}),    // base cell 100
       new BaseCellData(new FaceIJK(14, new int[]{0, 1, 0}), 0, new int[]{0, 0}),    // base cell 101
       new BaseCellData(new FaceIJK(19, new int[]{0, 1, 1}), 0, new int[]{0, 0}),    // base cell 102
       new BaseCellData(new FaceIJK(17, new int[]{0, 1, 0}), 0, new int[]{0, 0}),    // base cell 103
       new BaseCellData(new FaceIJK(13, new int[]{0, 0, 1}), 0, new int[]{0, 0}),    // base cell 104
       new BaseCellData(new FaceIJK(17, new int[]{0, 0, 0}), 0, new int[]{0, 0}),    // base cell 105
       new BaseCellData(new FaceIJK(16, new int[]{1, 0, 0}), 0, new int[]{0, 0}),    // base cell 106
       new BaseCellData(new FaceIJK( 9, new int[]{2, 0, 0}), 1, new int[]{14, 18}),   // base cell 107
       new BaseCellData(new FaceIJK(15, new int[]{1, 0, 1}), 0, new int[]{0, 0}),    // base cell 108
       new BaseCellData(new FaceIJK(15, new int[]{1, 0, 0}), 0, new int[]{0, 0}),    // base cell 109
       new BaseCellData(new FaceIJK(18, new int[]{0, 1, 1}), 0, new int[]{0, 0}),    // base cell 110
       new BaseCellData(new FaceIJK(18, new int[]{0, 0, 1}), 0, new int[]{0, 0}),    // base cell 111
       new BaseCellData(new FaceIJK(19, new int[]{0, 0, 1}), 0, new int[]{0, 0}),    // base cell 112
       new BaseCellData(new FaceIJK(17, new int[]{1, 0, 0}), 0, new int[]{0, 0}),    // base cell 113
       new BaseCellData(new FaceIJK(19, new int[]{0, 0, 0}), 0, new int[]{0, 0}),    // base cell 114
       new BaseCellData(new FaceIJK(18, new int[]{0, 1, 0}), 0, new int[]{0, 0}),    // base cell 115
       new BaseCellData(new FaceIJK(18, new int[]{1, 0, 1}), 0, new int[]{0, 0}),    // base cell 116
       new BaseCellData(new FaceIJK(19, new int[]{2, 0, 0}), 1, new int[]{-1, -1}),  // base cell 117
       new BaseCellData(new FaceIJK(19, new int[]{1, 0, 0}), 0, new int[]{0, 0}),    // base cell 118
       new BaseCellData(new FaceIJK(18, new int[]{0, 0, 0}), 0, new int[]{0, 0}),    // base cell 119
       new BaseCellData(new FaceIJK(19, new int[]{1, 0, 1}), 0, new int[]{0, 0}),    // base cell 120
       new BaseCellData(new FaceIJK(18, new int[]{1, 0, 0}), 0, new int[]{0, 0})     // base cell 121
   };

    private BaseCells(){}

    /**
     * Return whether or not the indicated base cell is a pentagon.
     */
    public static boolean isBaseCellPentagon(int baseCell) {
        if (baseCell < 0 || baseCell >= NUM_BASE_CELLS) {
            // Base cells less than zero can not be represented in an index
            return false;
        }
        return baseCellData[baseCell].isPentagon != 0;
    }
}
