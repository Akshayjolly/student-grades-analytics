-- Student Grades Analytics schema + demo data
-- Demo logins (password for all: password123): lecturer / akshay / ciara / liam

CREATE TABLE IF NOT EXISTS app_user (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(10)  NOT NULL CHECK (role IN ('STUDENT', 'LECTURER'))
);

CREATE TABLE IF NOT EXISTS module (
    module_code VARCHAR(10) PRIMARY KEY,
    title       VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS grade (
    grade_id    INT AUTO_INCREMENT PRIMARY KEY,
    student_id  INT NOT NULL,
    module_code VARCHAR(10) NOT NULL,
    assessment  VARCHAR(50) NOT NULL,
    semester    INT NOT NULL CHECK (semester >= 1),
    score       DECIMAL(5,2) NOT NULL CHECK (score BETWEEN 0 AND 100),
    CONSTRAINT fk_grade_student FOREIGN KEY (student_id) REFERENCES app_user(user_id),
    CONSTRAINT fk_grade_module  FOREIGN KEY (module_code) REFERENCES module(module_code)
);

CREATE INDEX idx_grade_student ON grade(student_id);
CREATE INDEX idx_grade_module ON grade(module_code);

INSERT INTO app_user (username, password_hash, full_name, role) VALUES
  ('lecturer', '120000:HSi/yIMbr98H5e5O6i3dRg==:FiFaB+tR6Du6qU+elg4s22eDLY8IZzBSOepIcaFLVug=', 'Dr. Mary Murphy', 'LECTURER'),
  ('akshay', '120000:7X2UH0TsqpQx37tT7MRU8g==:EMomV1VcSfeQ6EFmqte6Sa9XusGpD/NMxrhoWenbFYY=', 'Akshay Jolly', 'STUDENT'),
  ('ciara', '120000:jq/pha8SBEV2lJLG33TgmQ==:D12F0MSlpHwZbwcTcRv2Gl+ipHLD1ksv7Sq/6G+CMaQ=', 'Ciara Walsh', 'STUDENT'),
  ('liam', '120000:Msn2h2G9bDNV5bcUXkyS1w==:uJu/1/mwoA8mZtxg+kcQ1ZKD0wJSpRq9RfKjSyZRLQA=', 'Liam O''Brien', 'STUDENT');

INSERT INTO module (module_code, title) VALUES
  ('CS201', 'Data Structures and Algorithms'),
  ('CS202', 'Relational Databases'),
  ('CS203', 'Operating Systems Design'),
  ('MA201', 'Probability and Statistics');

INSERT INTO grade (student_id, module_code, assessment, semester, score) VALUES
  (2, 'CS201', 'CA1 S1', 1, 60),
  (2, 'CS201', 'Exam S1', 1, 71),
  (2, 'CS202', 'CA1 S1', 1, 70),
  (2, 'CS202', 'Exam S1', 1, 57),
  (2, 'CS203', 'CA1 S1', 1, 64),
  (2, 'CS203', 'Exam S1', 1, 68),
  (2, 'MA201', 'CA1 S1', 1, 71),
  (2, 'MA201', 'Exam S1', 1, 55),
  (2, 'CS201', 'CA1 S2', 2, 57),
  (2, 'CS201', 'Exam S2', 2, 72),
  (2, 'CS202', 'CA1 S2', 2, 65),
  (2, 'CS202', 'Exam S2', 2, 74),
  (2, 'CS203', 'CA1 S2', 2, 64),
  (2, 'CS203', 'Exam S2', 2, 63),
  (2, 'MA201', 'CA1 S2', 2, 72),
  (2, 'MA201', 'Exam S2', 2, 74),
  (2, 'CS201', 'CA1 S3', 3, 78),
  (2, 'CS201', 'Exam S3', 3, 76),
  (2, 'CS202', 'CA1 S3', 3, 73),
  (2, 'CS202', 'Exam S3', 3, 65),
  (2, 'CS203', 'CA1 S3', 3, 68),
  (2, 'CS203', 'Exam S3', 3, 65),
  (2, 'MA201', 'CA1 S3', 3, 77),
  (2, 'MA201', 'Exam S3', 3, 73),
  (3, 'CS201', 'CA1 S1', 1, 46),
  (3, 'CS201', 'Exam S1', 1, 48),
  (3, 'CS202', 'CA1 S1', 1, 51),
  (3, 'CS202', 'Exam S1', 1, 64),
  (3, 'CS203', 'CA1 S1', 1, 47),
  (3, 'CS203', 'Exam S1', 1, 55),
  (3, 'MA201', 'CA1 S1', 1, 46),
  (3, 'MA201', 'Exam S1', 1, 54),
  (3, 'CS201', 'CA1 S2', 2, 65),
  (3, 'CS201', 'Exam S2', 2, 62),
  (3, 'CS202', 'CA1 S2', 2, 63),
  (3, 'CS202', 'Exam S2', 2, 62),
  (3, 'CS203', 'CA1 S2', 2, 68),
  (3, 'CS203', 'Exam S2', 2, 64),
  (3, 'MA201', 'CA1 S2', 2, 54),
  (3, 'MA201', 'Exam S2', 2, 61),
  (3, 'CS201', 'CA1 S3', 3, 57),
  (3, 'CS201', 'Exam S3', 3, 55),
  (3, 'CS202', 'CA1 S3', 3, 58),
  (3, 'CS202', 'Exam S3', 3, 69),
  (3, 'CS203', 'CA1 S3', 3, 60),
  (3, 'CS203', 'Exam S3', 3, 62),
  (3, 'MA201', 'CA1 S3', 3, 67),
  (3, 'MA201', 'Exam S3', 3, 63),
  (4, 'CS201', 'CA1 S1', 1, 48),
  (4, 'CS201', 'Exam S1', 1, 51),
  (4, 'CS202', 'CA1 S1', 1, 47),
  (4, 'CS202', 'Exam S1', 1, 53),
  (4, 'CS203', 'CA1 S1', 1, 46),
  (4, 'CS203', 'Exam S1', 1, 52),
  (4, 'MA201', 'CA1 S1', 1, 53),
  (4, 'MA201', 'Exam S1', 1, 48),
  (4, 'CS201', 'CA1 S2', 2, 49),
  (4, 'CS201', 'Exam S2', 2, 38),
  (4, 'CS202', 'CA1 S2', 2, 41),
  (4, 'CS202', 'Exam S2', 2, 31),
  (4, 'CS203', 'CA1 S2', 2, 39),
  (4, 'CS203', 'Exam S2', 2, 36),
  (4, 'MA201', 'CA1 S2', 2, 41),
  (4, 'MA201', 'Exam S2', 2, 48),
  (4, 'CS201', 'CA1 S3', 3, 45),
  (4, 'CS201', 'Exam S3', 3, 45),
  (4, 'CS202', 'CA1 S3', 3, 30),
  (4, 'CS202', 'Exam S3', 3, 33),
  (4, 'CS203', 'CA1 S3', 3, 45),
  (4, 'CS203', 'Exam S3', 3, 35),
  (4, 'MA201', 'CA1 S3', 3, 36),
  (4, 'MA201', 'Exam S3', 3, 30);
