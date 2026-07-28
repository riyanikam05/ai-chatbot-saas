CREATE TABLE documents (

    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    original_filename VARCHAR(255) NOT NULL,

    stored_filename VARCHAR(255) NOT NULL,

    file_path TEXT NOT NULL,

    uploaded_at TIMESTAMP NOT NULL

);