

create table if not exists users
(
    id BIGSERIAL,
    name varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    CONSTRAINT pk_users primary key (id)
);

create table if not exists projects (
    id BIGSERIAL,
    name_project varchar(255) not null,
    status varchar(255) not null,
    user_id BIGINT,
    CONSTRAINT pk_projects PRIMARY KEY (id),
    CONSTRAINT fk_projects_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

create table if not exists repositories (
    id BIGSERIAL,
    url varchar(255) not null,
    name_repository varchar(255) not null,
    language varchar(255) not null,
    owner varchar(255) not null,
    project_id BIGINT,
    CONSTRAINT pk_repositories PRIMARY KEY (id),
    CONSTRAINT fk_repositories_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

create table if not exists files (
    id BIGSERIAL,
    full_filename varchar(255) not null,
    filename varchar(255) not null,
    content TEXT,
    repository_id BIGINT,
    CONSTRAINT pk_files PRIMARY KEY (id),
    CONSTRAINT fk_files_repository FOREIGN KEY (repository_id) REFERENCES repositories(id) ON DELETE CASCADE
);

create table if not exists matches (
    id BIGSERIAL,
    percentage FLOAT not null,
    first_file_id BIGINT,
    second_file_id BIGINT,
    first_repository_id BIGINT,
    second_repository_id BIGINT,
    project_id BIGINT,
    CONSTRAINT pk_matches PRIMARY KEY (id),
    CONSTRAINT fk_matches_first_file FOREIGN KEY (first_file_id) REFERENCES files(id) ON DELETE CASCADE,
    CONSTRAINT fk_matches_second_file FOREIGN KEY (second_file_id) REFERENCES files(id) ON DELETE CASCADE,
    CONSTRAINT fk_matches_first_repository FOREIGN KEY (first_repository_id) REFERENCES repositories(id) ON DELETE CASCADE,
    CONSTRAINT fk_matches_second_repository FOREIGN KEY (second_repository_id) REFERENCES repositories(id) ON DELETE CASCADE,
    CONSTRAINT fk_matches_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

create table if not exists tiles (
    id BIGSERIAL,
    start_line_in_first_file BIGINT,
    end_line_in_first_file BIGINT,
    start_line_in_second_file BIGINT,
    end_line_in_second_file BIGINT,
    text_in_first_file TEXT,
    text_in_second_file TEXT,
    match_id BIGINT,
    CONSTRAINT pk_tiles PRIMARY KEY (id),
    CONSTRAINT fk_tiles_match FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE
);

create table if not exists statistics (
    id BIGSERIAL,
    number_of_repositories BIGINT,
    number_of_files BIGINT,
    number_of_suspicious_files BIGINT,
    max_similarity FLOAT,
    average_similarity FLOAT,
    project_id BIGINT,
    CONSTRAINT pk_statistics PRIMARY KEY (id),
    CONSTRAINT fk_statistics_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);






