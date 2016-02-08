--
-- Corrections to deprecated CRS. EPSG is not likely to fix those CRS, because they are
-- deprecated. However Geotk can not instantiate some CRS without those corrections.
--

--
-- For the "Scale factor at natural origin" parameter (EPSG:8805), replace the wrong "m"
-- units (EPSG:9001) by the dimensionless unit (EPSG:9201). This affect the following CRS:
--
--   * EPSG:3143    Fiji 1986 / Fiji Map Grid
--   * EPSG:3774    NAD27 / Alberta 3TM ref merid 120 W
--   * EPSG:3778    NAD83 / Alberta 3TM ref merid 120 W
--   * EPSG:3782    NAD83(CSRS) / Alberta 3TM ref merid 120 W
--
UPDATE epsg_coordoperationparamvalue SET uom_code = 9201 WHERE parameter_code = 8805 AND uom_code = 9001;
