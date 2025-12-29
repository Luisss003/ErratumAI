ALTER TABLE annotation_note
  ADD COLUMN IF NOT EXISTS location text;

ALTER TABLE annotation_note
  ALTER COLUMN note TYPE text,
  ALTER COLUMN quote TYPE text,
  ALTER COLUMN reason TYPE text;
