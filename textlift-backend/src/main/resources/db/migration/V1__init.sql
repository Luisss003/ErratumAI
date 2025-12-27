-- V1__init.sql
-- Initial schema for TextLift (PostgreSQL)

-- Users
create table if not exists users (
  id uuid primary key,
  email varchar(255) not null unique,
  full_name varchar(255) not null,
  password varchar(255) not null,
  enabled boolean not null default false,
  verification_code varchar(255),
  verification_expiration timestamp
);

-- Textbook
create table if not exists textbook (
  id uuid primary key,
  textbook_name varchar(255),
  edition varchar(255),
  isbn varchar(255),
  authors text[]
);

-- Document
create table if not exists document (
  id uuid primary key,
  textbook_id uuid,
  status varchar(255)
    check (status in ('READY','SCANNING','TEXTBOOK_IDENTIFIED','ANNOTATIONS_GENERATING','ANNOTATIONS_READY')),
  file_path varchar(255) not null,
  original_file_name varchar(255) not null,
  hash varchar(255),
  created_at timestamptz,
  updated_at timestamptz,
  constraint fk_document_textbook
    foreign key (textbook_id) references textbook(id)
);

-- Annotation
create table if not exists annotation (
  id uuid primary key,
  textbook_id uuid unique,
  version integer,
  created_at timestamptz,
  updated_at timestamptz,
  constraint fk_annotation_textbook
    foreign key (textbook_id) references textbook(id)
);

-- Annotation notes
create table if not exists annotation_note (
  id uuid primary key,
  annotation_id uuid,
  note varchar(255),
  reason varchar(255),
  quote varchar(255),
  page_num varchar(255),
  created_at timestamptz,
  updated_at timestamptz,
  constraint fk_note_annotation
    foreign key (annotation_id) references annotation(id)
);

-- Upload sessions
create table if not exists upload_session (
  id uuid primary key,
  upload_status varchar(255) not null
    check (upload_status in ('UPLOADING','UPLOADED','PENDING','FAILED')),
  original_file_name varchar(255),
  hash varchar(255)
);

-- Helpful indexes
create index if not exists idx_document_hash on document(hash);
create index if not exists idx_upload_session_hash on upload_session(hash);
create index if not exists idx_users_email on users(email);
