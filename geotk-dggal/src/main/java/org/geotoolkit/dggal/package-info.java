/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

/**
 * Relationship between DGGAL API and Apache SIS API :
 *
 * DGGAL_DGGRS_getZoneFromTextID
 * DGGAL_DGGRS_getZoneLevel                  Zone.getLocationType().getRefinementLevel()
 * DGGAL_DGGRS_getRefinementRatio            DiscreteGlobalGridSystem.getRefinementRatio()
 * DGGAL_DGGRS_getMaxDGGRSZoneLevel          DiscreteGlobalGridHierarchy.getGrids().size()
 * DGGAL_DGGRS_getZoneWGS84Centroid          Zone.getPosition()
 * DGGAL_DGGRS_getZoneWGS84Vertices          Zone.getGeographicExtent()
 * DGGAL_DGGRS_getZoneArea                   Zone.getAreaMetersSquare()
 * DGGAL_DGGRS_getZoneTextID                 Zone.getIdentifier().toText()
 * DGGAL_DGGRS_getZoneParents                Zone.getParents()
 * DGGAL_DGGRS_getZoneChildren               Zone.getChildren()
 * DGGAL_DGGRS_getZoneNeighbors              Zone.getNeighbors()
 * DGGAL_DGGRS_getZoneWGS84Extent            Zone.getEnvelope()
 * DGGAL_DGGRS_listZones                     DiscreteGlobalGrid.getZones(envelope)
 * DGGAL_DGGRS_getZoneRefinedWGS84Vertices   -
 * DGGAL_DGGRS_getSubZones                   DiscreteGlobalGrid.getZones(parent)
 * DGGAL_DGGRS_getZoneFromWGS84Centroid      DiscreteGlobalGrid.getZone(position)
 * DGGAL_DGGRS_getFirstSubZone               -
 * DGGAL_DGGRS_getSubZoneAtIndex             DiscreteGlobalGrid.getZones(parent).skip(index)
 * DGGAL_DGGRS_getSubZoneIndex               -
 * DGGAL_DGGRS_getSubZoneCRSCentroids        DiscreteGlobalGrid.getZones(parent) + Zone.getPosition() + any transform
 * DGGAL_DGGRS_getSubZoneWGS84Centroids      DiscreteGlobalGrid.getZones(parent) + Zone.getPosition() + any transform
 * DGGAL_DGGRS_getZoneRefinedCRSVertices     -
 * DGGAL_DGGRS_getZoneCRSCentroid            Zone.getPosition() + any transform
 * DGGAL_DGGRS_getZoneCRSExtent              Zone.getEnvelope() + any transform
 * DGGAL_DGGRS_compactZones                  DiscreteGlobalGridHierarchy.compact(zones)
 * DGGAL_DGGRS_areZonesNeighbors             -
 * DGGAL_DGGRS_areZonesSiblings              -
 * DGGAL_DGGRS_doZonesOverlap                -
 * DGGAL_DGGRS_doesZoneContain               -
 * DGGAL_DGGRS_isZoneAncestorOf              -
 * DGGAL_DGGRS_isZoneContainedIn             -
 * DGGAL_DGGRS_isZoneDescendantOf            -
 * DGGAL_DGGRS_isZoneImmediateChildOf        -
 * DGGAL_DGGRS_isZoneImmediateParentOf       -
 * DGGAL_DGGRS_zoneHasSubZone                -
 * DGGAL_DGGRS_getLevelFromMetersPerSubZone  -
 * DGGAL_DGGRS_getLevelFromPixelsAndExtent   -
 * DGGAL_DGGRS_getLevelFromRefZoneArea       -
 * DGGAL_DGGRS_getLevelFromScaleDenominator  -
 * DGGAL_DGGRS_getMetersPerSubZoneFromLevel  -
 * DGGAL_DGGRS_getRefZoneArea                -
 * DGGAL_DGGRS_getScaleDenominatorFromLevel  -
 *
 * The following methods are needed for C-like programing to allocate array sizes
 * They are not available in the Java API.
 *
 * DGGAL_DGGRS_countZones                    -
 * DGGAL_DGGRS_countZoneEdges                -
 * DGGAL_DGGRS_countSubZones                 -
 * DGGAL_DGGRS_getMaxChildren                -
 * DGGAL_DGGRS_getMaxNeighbors               -
 * DGGAL_DGGRS_getMaxParents                 -
 *
 * The folliwing methods are tighly linked to how DGGAL represent DGGRS internaly.
 *
 * DGGAL_DGGRS_getZoneCentroidParent         -
 * DGGAL_DGGRS_getZoneCentroidChild          -
 * DGGAL_DGGRS_isZoneCentroidChild           -
 * DGGAL_DGGRS_getIndexMaxDepth              -
 * DGGAL_DGGRS_get64KDepth                   -
 * DGGAL_DGGRS_getMaxDepth                   -
 *
 */
package org.geotoolkit.dggal;
