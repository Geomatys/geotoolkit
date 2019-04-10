--
-- Identify entries that should be unique.
-- This query should return an empty result.
--
SELECT "format", "band", "name", "lower", "upper", "scale", "offset" FROM rasters."Categories"
  GROUP BY ("format", "band", "name", "lower", "upper", "scale", "offset") HAVING COUNT("format") > 1
