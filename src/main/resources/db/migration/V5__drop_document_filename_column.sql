-- The 'documents' table has a leftover NOT NULL 'filename' column from an
-- earlier schema version, before it was split into 'original_filename' and
-- 'stored_filename'. The Document entity never mapped it, so every insert
-- fails with a not-null constraint violation. Dropping it here since nothing
-- reads or writes it.

ALTER TABLE documents DROP COLUMN IF EXISTS filename;