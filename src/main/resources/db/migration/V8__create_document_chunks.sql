
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE document_chunks (
    id BIGSERIAL PRIMARY KEY,

    document_id BIGINT NOT NULL
        REFERENCES documents(id) ON DELETE CASCADE,

    user_id BIGINT NOT NULL
        REFERENCES users(id) ON DELETE CASCADE,

    chunk_index INTEGER NOT NULL,
    content TEXT NOT NULL,

    embedding VECTOR(768) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_document_chunk
        UNIQUE (document_id, chunk_index)
);

CREATE INDEX idx_document_chunks_user
    ON document_chunks(user_id);

CREATE INDEX idx_document_chunks_document
    ON document_chunks(document_id);
