-- Flyway Migration V2: Seed System Data & Initial Admin Account

-- Ensure tables exist in case V1 baseline history entry was recorded without tables
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    branch VARCHAR(50),
    semester INT,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    INDEX idx_user_email (email),
    INDEX idx_user_branch_sem (branch, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS branches (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    short_name VARCHAR(50),
    code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS semesters (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    short_name VARCHAR(20),
    year_group VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS subjects (
    id VARCHAR(100) PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    branch_id VARCHAR(20) NOT NULL,
    semester_id INT NOT NULL,
    year_id INT NOT NULL,
    description TEXT,
    syllabus JSON,
    important_topics JSON,
    popular BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (branch_id) REFERENCES branches(id),
    FOREIGN KEY (semester_id) REFERENCES semesters(id),
    INDEX idx_subject_code (code),
    INDEX idx_subject_branch_sem (branch_id, semester_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question_papers (
    id VARCHAR(100) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subject_id VARCHAR(100) NOT NULL,
    subject_code VARCHAR(20) NOT NULL,
    subject_name VARCHAR(255) NOT NULL,
    branch_id VARCHAR(20) NOT NULL,
    semester_id INT NOT NULL,
    academic_year VARCHAR(20) DEFAULT '2025-26',
    exam_year INT NOT NULL,
    exam_type VARCHAR(50) NOT NULL,
    paper_type VARCHAR(50) DEFAULT 'Regular',
    file_url VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size VARCHAR(50) DEFAULT '1.8 MB',
    pages INT DEFAULT 4,
    downloads INT DEFAULT 0,
    views INT DEFAULT 0,
    status VARCHAR(30) DEFAULT 'PUBLISHED',
    description TEXT,
    tags JSON,
    uploaded_by VARCHAR(100) DEFAULT 'Admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (branch_id) REFERENCES branches(id),
    FOREIGN KEY (semester_id) REFERENCES semesters(id),
    INDEX idx_paper_status (status),
    INDEX idx_paper_subject_year (subject_code, exam_year),
    INDEX idx_paper_branch_sem (branch_id, semester_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS notes (
    id VARCHAR(100) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    subject_id VARCHAR(100) NOT NULL,
    subject_code VARCHAR(20) NOT NULL,
    subject_name VARCHAR(255) NOT NULL,
    branch_id VARCHAR(20) NOT NULL,
    semester_id INT NOT NULL,
    unit VARCHAR(50) DEFAULT 'All Units (1-5)',
    note_type VARCHAR(50) DEFAULT 'Revision Notes',
    author VARCHAR(150) DEFAULT 'QuestionVault Faculty',
    pages INT DEFAULT 24,
    file_url VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size VARCHAR(50) DEFAULT '4.2 MB',
    downloads INT DEFAULT 0,
    views INT DEFAULT 0,
    status VARCHAR(30) DEFAULT 'PUBLISHED',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (branch_id) REFERENCES branches(id),
    FOREIGN KEY (semester_id) REFERENCES semesters(id),
    INDEX idx_note_status (status),
    INDEX idx_note_subject (subject_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 1. Roles
INSERT INTO roles (id, name) VALUES 
(1, 'ROLE_USER'),
(2, 'ROLE_ADMIN')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 2. Initial Admin User (password: AdminPass2026!)
INSERT INTO users (id, name, email, password, branch, semester, enabled) VALUES
(1, 'QuestionVault Admin', 'admin@questionvault.com', '$2a$10$wNq.OvhHh4k76Y.Q6vG1O.N2vD8F1g.j4d7H1S4G6O8J0L9K1M2N3', 'CSE', 8, TRUE),
(2, 'Student Demo User', 'student@aktu.ac.in', '$2a$10$wNq.OvhHh4k76Y.Q6vG1O.N2vD8F1g.j4d7H1S4G6O8J0L9K1M2N3', 'CSE', 4, TRUE)
ON DUPLICATE KEY UPDATE email=VALUES(email);

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 2),
(1, 1),
(2, 1)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id);

-- 3. Branches
INSERT INTO branches (id, name, short_name, code, description, active) VALUES
('CSE', 'Computer Science & Engineering', 'CSE', '010', 'Software engineering, algorithms, data structures, database systems, and web technologies.', TRUE),
('AIML', 'AI & Machine Learning', 'AI/ML', '013', 'Artificial intelligence, deep learning, neural networks, machine learning algorithms, and robotics.', TRUE),
('IT', 'Information Technology', 'IT', '012', 'Information processing, network architecture, cloud computing, cybersecurity, and enterprise systems.', TRUE),
('ECE', 'Electronics & Comm. Engg.', 'ECE', '031', 'VLSI design, digital signal processing, embedded systems, microprocessors, and wireless networks.', TRUE),
('EE', 'Electrical Engineering', 'EE', '020', 'Power systems, electrical machines, control engineering, renewable energy, and power electronics.', TRUE),
('ME', 'Mechanical Engineering', 'ME', '040', 'Thermodynamics, fluid mechanics, CAD/CAM, machine design, thermal engineering, and robotics.', TRUE),
('CE', 'Civil Engineering', 'CE', '000', 'Structural engineering, surveying, geotechnical analysis, transportation, and environmental engineering.', TRUE),
('DS', 'Data Science', 'DS', '014', 'Big data analytics, data visualization, statistical modelling, predictive analysis, and Python for DS.', TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 4. Semesters
INSERT INTO semesters (id, name, short_name, year_group, active) VALUES
(1, 'Semester 1', 'Sem 1', '1st Year', TRUE),
(2, 'Semester 2', 'Sem 2', '1st Year', TRUE),
(3, 'Semester 3', 'Sem 3', '2nd Year', TRUE),
(4, 'Semester 4', 'Sem 4', '2nd Year', TRUE),
(5, 'Semester 5', 'Sem 5', '3rd Year', TRUE),
(6, 'Semester 6', 'Sem 6', '3rd Year', TRUE),
(7, 'Semester 7', 'Sem 7', '4th Year', TRUE),
(8, 'Semester 8', 'Sem 8', '4th Year', TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 5. Core AKTU Subjects
INSERT INTO subjects (id, code, name, branch_id, semester_id, year_id, description, popular, active) VALUES
('kas101-engineering-physics', 'KAS101', 'Engineering Physics', 'CSE', 1, 1, 'Quantum Mechanics, Relativistic Mechanics, Electromagnetic Field Theory, Wave Optics, and Fiber Optics.', TRUE, TRUE),
('kas102-engineering-mathematics-1', 'KAS102', 'Engineering Mathematics-I', 'CSE', 1, 1, 'Differential Calculus, Infinite Series, Multivariable Calculus, Vector Calculus, and Matrices.', TRUE, TRUE),
('kcs101-programming-for-problem-solving', 'KCS101', 'Programming for Problem Solving (C Programming)', 'CSE', 1, 1, 'Basics of Computer Architecture, Flowcharts, C Language Syntax, Control Statements, Functions, Pointers, Arrays, and File Handling.', TRUE, TRUE),
('kee101-basic-electrical-engineering', 'KEE101', 'Basic Electrical Engineering', 'CSE', 1, 1, 'DC Circuit Analysis, AC Circuits, Transformers, Electrical Machines, and Electrical Installations.', FALSE, TRUE),
('kas201-engineering-chemistry', 'KAS201', 'Engineering Chemistry', 'CSE', 2, 1, 'Atomic & Molecular Structure, Spectroscopy, Water Technology, Polymer Science, and Corrosion Engineering.', FALSE, TRUE),
('kcs301-data-structures', 'KCS301', 'Data Structures', 'CSE', 3, 2, 'Arrays, Linked Lists, Stacks, Queues, Binary Trees, BST, AVL Trees, Graphs, Sorting & Searching Algorithms, Hashing.', TRUE, TRUE),
('kcs302-computer-organization-and-architecture', 'KCS302', 'Computer Organization & Architecture', 'CSE', 3, 2, 'Register Transfer, Microoperations, Basic Computer Design, Pipeline Processing, Memory Organization, I/O Organization.', TRUE, TRUE),
('kcs303-discrete-mathematics', 'KCS303', 'Discrete Mathematics', 'CSE', 3, 2, 'Set Theory, Relations, Functions, Propositional Logic, Group Theory, Posets, Lattices, Recurrence Relations, Graph Theory.', FALSE, TRUE),
('kcs401-operating-systems', 'KCS401', 'Operating Systems', 'CSE', 4, 2, 'Process Management, CPU Scheduling, Process Synchronization, Deadlocks, Memory Management, Virtual Memory, File Systems.', TRUE, TRUE),
('kcs402-theory-of-automata-and-formal-languages', 'KCS402', 'Theory of Automata & Formal Languages (TAFL)', 'CSE', 4, 2, 'Finite Automata (DFA, NFA), Regular Expressions, Context-Free Grammars (CFG), Pushdown Automata (PDA), Turing Machines.', TRUE, TRUE),
('kcs501-database-management-system', 'KCS501', 'Database Management System (DBMS)', 'CSE', 5, 3, 'ER Modelling, Relational Algebra, SQL, Normalization (1NF to BCNF), Transaction Processing, Concurrency Control.', TRUE, TRUE),
('kcs503-design-and-analysis-of-algorithms', 'KCS503', 'Design & Analysis of Algorithms (DAA)', 'CSE', 5, 3, 'Asymptotic Notation, Divide & Conquer, Greedy Algorithms, Dynamic Programming, Backtracking, NP-Completeness.', TRUE, TRUE),
('kcs603-computer-networks', 'KCS603', 'Computer Networks', 'CSE', 6, 3, 'OSI Reference Model, TCP/IP Suite, Data Link Protocols, Framing, IP Addressing (IPv4/IPv6), Routing Algorithms, TCP/UDP.', TRUE, TRUE),
('kcs701-artificial-intelligence', 'KCS701', 'Artificial Intelligence', 'CSE', 7, 4, 'Problem Solving Spaces, Uninformed & Informed Search (A*, AO*), Game Playing (Minimax), Knowledge Representation, Fuzzy Logic.', TRUE, TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 6. Initial Question Papers
INSERT INTO question_papers (id, title, subject_id, subject_code, subject_name, branch_id, semester_id, academic_year, exam_year, exam_type, paper_type, file_url, file_name, file_size, pages, downloads, views, status, description, uploaded_by) VALUES
('pyq-kcs301-2025-regular', 'Data Structures - AKTU End Semester Paper', 'kcs301-data-structures', 'KCS301', 'Data Structures', 'CSE', 3, '2025-26', 2025, 'End Semester', 'Regular', 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'AKTU_KCS301_Data_Structures_2025.pdf', '1.8 MB', 4, 1420, 3890, 'PUBLISHED', 'Official AKTU end-semester examination question paper.', 'Admin'),
('pyq-kcs301-2024-regular', 'Data Structures - Regular Exam Question Paper', 'kcs301-data-structures', 'KCS301', 'Data Structures', 'CSE', 3, '2024-25', 2024, 'End Semester', 'Regular', 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'AKTU_KCS301_Data_Structures_2024.pdf', '1.6 MB', 4, 2150, 5120, 'PUBLISHED', 'Official AKTU end-semester examination question paper.', 'Admin'),
('pyq-kcs501-2025-regular', 'Database Management System - End Semester Exam', 'kcs501-database-management-system', 'KCS501', 'Database Management System', 'CSE', 5, '2025-26', 2025, 'End Semester', 'Regular', 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'AKTU_KCS501_DBMS_2025.pdf', '2.1 MB', 4, 1890, 4500, 'PUBLISHED', 'Official AKTU end-semester examination question paper.', 'Admin'),
('pyq-kcs401-2025-regular', 'Operating Systems - AKTU 2025 Question Paper', 'kcs401-operating-systems', 'KCS401', 'Operating Systems', 'CSE', 4, '2025-26', 2025, 'End Semester', 'Regular', 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'AKTU_KCS401_OS_2025.pdf', '1.7 MB', 4, 1650, 3900, 'PUBLISHED', 'Official AKTU end-semester examination question paper.', 'Admin'),
('pyq-kas101-2025-regular', 'Engineering Physics - 1st Year AKTU 2025 Paper', 'kas101-engineering-physics', 'KAS101', 'Engineering Physics', 'CSE', 1, '2025-26', 2025, 'End Semester', 'Regular', 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'AKTU_KAS101_Physics_2025.pdf', '2.0 MB', 4, 3400, 8200, 'PUBLISHED', 'Official AKTU 1st year physics paper.', 'Admin')
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 7. Initial Notes
INSERT INTO notes (id, title, subject_id, subject_code, subject_name, branch_id, semester_id, unit, note_type, author, pages, file_url, file_name, file_size, downloads, views, status, description) VALUES
('note-kcs301-complete', 'Data Structures Complete Topper Handwritten Notes', 'kcs301-data-structures', 'KCS301', 'Data Structures', 'CSE', 3, 'All Units (1-5)', 'Handwritten Notes', 'Shubham Sharma (AKTU Gold Medalist)', 48, 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'Data_Structures_Complete_Notes_AKTU.pdf', '8.4 MB', 3200, 7800, 'PUBLISHED', 'Comprehensive unit 1 to 5 handwritten notes with solved diagrams.'),
('note-kcs501-complete', 'DBMS Complete Revision & One-Shot Notes', 'kcs501-database-management-system', 'KCS501', 'Database Management System', 'CSE', 5, 'All Units (1-5)', 'Revision Notes', 'Er. Ankit Verma', 36, 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'DBMS_Complete_OneShot_Notes.pdf', '6.2 MB', 4100, 9200, 'PUBLISHED', 'One-shot revision notes covering ER diagrams, relational algebra, and 3NF/BCNF normalization.')
ON DUPLICATE KEY UPDATE title=VALUES(title);
