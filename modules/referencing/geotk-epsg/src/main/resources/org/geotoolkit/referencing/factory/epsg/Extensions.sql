--
-- Geotoolkit.org extensions to the EPSG database.
--

--
-- Transformation chain of Geographic CRS:
--
--   * EPSG:4807 (NTF, gradians with Paris meridian)
--   * EPSG:4275 (NTF, degrees with Greenwich meridian)
--   * EPSG:4171 (RGF93)
--
INSERT INTO epsg_coordoperation (coord_op_code, coord_op_name, coord_op_type,
            source_crs_code, target_crs_code, area_of_use_code, coord_op_scope,
            data_source, revision_date, show_operation, deprecated)
VALUES (40000, 'NTF to RGF93', 'concatenated operation', 4807, 4171, 3694,
        'For applications requiring an accuracy of better than 1 metre.',
        'Geotoolkit.org', '2010-05-30', 0, 0);

INSERT INTO epsg_coordoperationpath (concat_operation_code, single_operation_code, op_path_step) VALUES
  (40000, 1763, 1),
  (40000, 1053, 2);
