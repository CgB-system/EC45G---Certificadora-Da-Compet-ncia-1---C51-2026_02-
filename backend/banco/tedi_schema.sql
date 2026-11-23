CREATE DATABASE IF NOT EXISTS tedi
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'tedi'@'localhost' IDENTIFIED BY 'Tedi@2026';
GRANT ALL PRIVILEGES ON tedi.* TO 'tedi'@'localhost';
FLUSH PRIVILEGES;

USE tedi;

CREATE TABLE tbl_Usuario (
  usu_id        INT          NOT NULL AUTO_INCREMENT,
  usu_nome      VARCHAR(50)  NOT NULL,
  usu_sobrenome VARCHAR(60)  NOT NULL,
  usu_email     VARCHAR(80)  NOT NULL,
  usu_senha     VARCHAR(150) NOT NULL,
  usu_ativo     TINYINT(1)   NOT NULL DEFAULT 1,
  PRIMARY KEY (usu_id),

  UNIQUE KEY uq_usuario_email (usu_email)
) ENGINE=InnoDB;

CREATE TABLE tbl_Curso (
  cur_id   INT         NOT NULL AUTO_INCREMENT,
  cur_nome VARCHAR(45) NOT NULL,
  PRIMARY KEY (cur_id)
) ENGINE=InnoDB;

CREATE TABLE tbl_Habilidade (
  hab_id   INT         NOT NULL AUTO_INCREMENT,
  hab_nome VARCHAR(60) NOT NULL,
  PRIMARY KEY (hab_id)
) ENGINE=InnoDB;

CREATE TABLE tbl_Administrador (
  usu_id INT     NOT NULL,
  adm_id TINYINT NOT NULL,
  PRIMARY KEY (usu_id),
  CONSTRAINT fk_adm_usuario
    FOREIGN KEY (usu_id) REFERENCES tbl_Usuario (usu_id)
) ENGINE=InnoDB;

CREATE TABLE tbl_Voluntario (
  usu_id          INT           NOT NULL,
  vol_ra          CHAR(7)       NOT NULL,
  vol_periodo     TINYINT       NOT NULL,
  vol_coef        DECIMAL(5,4)  NOT NULL,
  cur_id          INT           NOT NULL,
  vol_experiencia VARCHAR(250)  NULL,

  vol_status      ENUM('ATIVO','INATIVO') NULL,
  PRIMARY KEY (usu_id),
  UNIQUE KEY uq_voluntario_ra (vol_ra),
  KEY idx_voluntario_curso (cur_id),
  CONSTRAINT fk_vol_usuario
    FOREIGN KEY (usu_id) REFERENCES tbl_Usuario (usu_id),
  CONSTRAINT fk_vol_curso
    FOREIGN KEY (cur_id) REFERENCES tbl_Curso (cur_id)
) ENGINE=InnoDB;

CREATE TABLE tbl_Telefone (
  tel_id     INT        NOT NULL AUTO_INCREMENT,
  tel_cod    VARCHAR(4) NOT NULL,
  tel_ddd    CHAR(2)    NOT NULL,
  tel_numero CHAR(9)    NOT NULL,
  usu_id     INT        NOT NULL,
  PRIMARY KEY (tel_id),
  KEY idx_telefone_usuario (usu_id),
  CONSTRAINT fk_tel_usuario
    FOREIGN KEY (usu_id) REFERENCES tbl_Usuario (usu_id)
) ENGINE=InnoDB;

CREATE TABLE tbl_Voluntario_Habilidade (
  usu_id INT NOT NULL,
  hab_id INT NOT NULL,
  PRIMARY KEY (usu_id, hab_id),
  KEY idx_vh_habilidade (hab_id),
  CONSTRAINT fk_vh_voluntario
    FOREIGN KEY (usu_id) REFERENCES tbl_Voluntario (usu_id),
  CONSTRAINT fk_vh_habilidade
    FOREIGN KEY (hab_id) REFERENCES tbl_Habilidade (hab_id)
) ENGINE=InnoDB;

CREATE TABLE tbl_Gerenciador (
  adm_usu_id      INT  NOT NULL,
  vol_usu_id      INT  NOT NULL,
  ger_data_inicio DATE NOT NULL,
  ger_data_fim    DATE NULL,
  PRIMARY KEY (adm_usu_id, vol_usu_id),
  KEY idx_ger_voluntario (vol_usu_id),
  CONSTRAINT fk_ger_administrador
    FOREIGN KEY (adm_usu_id) REFERENCES tbl_Administrador (usu_id),
  CONSTRAINT fk_ger_voluntario
    FOREIGN KEY (vol_usu_id) REFERENCES tbl_Voluntario (usu_id)
) ENGINE=InnoDB;

INSERT INTO tbl_Usuario (usu_nome, usu_sobrenome, usu_email, usu_senha, usu_ativo)
VALUES ('Administrador', 'TEDI', 'credencial',
        'credencial', 1);

INSERT INTO tbl_Administrador (usu_id, adm_id)
VALUES (LAST_INSERT_ID(), 1);

INSERT INTO tbl_Curso (cur_nome) VALUES
  ('Engenharia de Computação'),
  ('Ciência da Computação'),
  ('Engenharia Elétrica');

INSERT INTO tbl_Habilidade (hab_nome) VALUES
  ('Desenvolvimento de Jogos'), ('Ensino'), ('Administração');
