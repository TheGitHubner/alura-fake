CREATE TABLE Task (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  createdAt datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  taskType varchar(20) NOT NULL,
  statement varchar(255) NOT NULL,
  taskOrder int NOT NULL,
  course_id bigint(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UQ_Task_Course_Order (course_id, taskOrder),
  UNIQUE KEY UQ_Task_Course_Statement (course_id, statement),
  CONSTRAINT FK_Task_Course FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;