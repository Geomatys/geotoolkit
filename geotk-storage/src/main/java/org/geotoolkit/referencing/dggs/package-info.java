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
 * This package contains a API for Discrete Global Grid Reference Systems (DGGRS).
 *
 * Based on specifications  :
 * - ISO-19170 : Geographic Information — Discrete Global Grid Systems Specifications — Core Reference System and Operations, and Equal Area Earth Reference System
 * - OGC Topic 21 - Discrete Global Grid Systems - Part 1 Core Reference system and Operations and Equal Area Earth Reference System : https://docs.ogc.org/as/20-040r3/20-040r3.html
 * - OGC DGGRS API : https://docs.ogc.org/DRAFTS/21-038r1.html
 *
 * ISO-19170:2020 and OGC Topic 21 - Discrete Global Grid Systems are actually the same document.
 *
 * Current DGGRS API do not follow ISO-19170 exactly.
 *
 * More precisely, ISO-19170 define a DGGRS as a sub-type of CRS (https://docs.ogc.org/as/20-040r3/20-040r3.html#tab-DGG_ReferenceSystem).
 * This choice would result a complete review of the CRS API to handle positions which are not numeric.
 *
 * As a matter of fact we have choosen to extend ReferingByIdentifiers (ISO-19112) instead.
 */
package org.geotoolkit.referencing.dggs;
