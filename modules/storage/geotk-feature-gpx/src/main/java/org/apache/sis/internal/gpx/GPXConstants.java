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
package org.apache.sis.internal.gpx;

import org.apache.sis.util.Static;
import org.opengis.feature.FeatureType;


/**
 * Bridge to SIS-private objects (temporary class).
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class GPXConstants extends Static {
    /*
     * WPT tag.
     */
    /** used in version : 1.0 and 1.1 */
    public static final String TAG_WPT = "wpt";

    /**
     * Parent feature type of all gpx types.
     */
    public static final FeatureType TYPE_GPX_ENTITY = Types.DEFAULT.wayPoint.getSuperTypes().iterator().next();

    /**
     * Waypoint GPX feature type.
     */
    public static final FeatureType TYPE_WAYPOINT = Types.DEFAULT.wayPoint;

    /**
     * Track GPX feature type.
     */
    public static final FeatureType TYPE_TRACK = Types.DEFAULT.track;

    /**
     * Route GPX feature type.
     */
    public static final FeatureType TYPE_ROUTE = Types.DEFAULT.route;

    /**
     * Do not allow instantiation of this class.
     */
    private GPXConstants() {
    }
}
