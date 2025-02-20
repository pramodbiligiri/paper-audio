create table arxiv_oai (
  id bigserial NOT NULL UNIQUE,
  oai_xml text,
  src jsonb,
  batch_id bigint,

  PRIMARY KEY (id)
);
create sequence seq_arxiv_oai START 1;
create index batch_id_index on arxiv_oai(batch_id);

create table paper_data (
  id bigserial NOT NULL UNIQUE,
  arxiv_id varchar (30),
  title text,
  abstract text,
  link varchar(100),
  authors text,

  -- the date associated with the max version for the paper in the arxiv feed
  pubdate timestamp with time zone,

  PRIMARY KEY (id)
);
create sequence seq_paper_data START 1;
create index pubdate_index on paper_data (pubdate);

create table paper_category (
  id bigserial NOT NULL UNIQUE,
  paper_id bigint,
  category varchar(50),

  PRIMARY KEY (id),
  FOREIGN KEY (paper_id) REFERENCES paper_data(id)
);
create sequence seq_paper_category START 1;
create index category_paper_index on paper_category (paper_id);
create index category_index on paper_category (category);

create table paper_audio (
  id bigserial NOT NULL UNIQUE,
  paper_id bigint,
  audio varchar(45), -- currently UUID of object (36 chars) + file format extension
  duration int, -- seconds
  create_time timestamp with time zone,

  PRIMARY KEY (id),
  FOREIGN KEY (paper_id) REFERENCES paper_data(id)
);
create sequence seq_paper_audio START 1;
create index paper_audio_paper_index on paper_audio (paper_id);

create table paper_tts_task (
  id bigserial NOT NULL UNIQUE,

  paper_id bigint,
  paper_audio_id bigint,
  status smallint,

  start_time timestamp,
  end_time timestamp,

  PRIMARY KEY (id)

  -- Not actually adding these foreign keys, as this is just an
  -- operations-related table
  -- FOREIGN KEY (paper_id) REFERENCES paper_data(id),
  -- FOREIGN KEY (paper_audio_id) REFERENCES paper_audio(id)
);
create sequence seq_paper_tts_task START 1;

create table email_sub (
  id bigserial NOT NULL UNIQUE,

  email_id VARCHAR(100),
  PRIMARY KEY (id)
);
create sequence seq_email_sub START 1;

create table feedback (
  id bigserial NOT NULL UNIQUE,

  data VARCHAR(5000),
  PRIMARY KEY (id)
);
create sequence seq_feedback START 1;

-- create sequence hibernate_sequence START 1;