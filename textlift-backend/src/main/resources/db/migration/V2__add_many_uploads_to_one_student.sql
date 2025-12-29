ALTER TABLE upload_session
ADD COLUMN user_id UUID;

CREATE INDEX IF NOT EXISTS idx_upload_session_user_id
ON upload_session(user_id);

ALTER TABLE upload_session
ADD CONSTRAINT fk_upload_session_user
FOREIGN KEY (user_id) REFERENCES users(id)
ON DELETE RESTRICT;
