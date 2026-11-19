package com.example.studentmanagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private final JdbcTemplate jdbcTemplate;

    public StudentService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. Validate student scores
    private void validateScores(Student student) {

        if (student.getProgrammingScore() < 0 ||
                student.getProgrammingScore() > 100) {

            throw new IllegalArgumentException(
                    "Programming score must be between 0 and 100");
        }

        if (student.getMathematicsScore() < 0 ||
                student.getMathematicsScore() > 100) {

            throw new IllegalArgumentException(
                    "Mathematics score must be between 0 and 100");
        }
    }

    // 2. Add student with validation and duplicate ID prevention
    public Student addStudent(Student student) {

        validateScores(student);

        String checkSql =
                "SELECT COUNT(*) FROM students WHERE student_id = ?";

        Integer count = jdbcTemplate.queryForObject(
                checkSql, Integer.class, student.getStudentId());

        if (count != null && count > 0) {
            throw new IllegalArgumentException(
                    "Student ID already exists!");
        }

        String sql = "INSERT INTO students VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                student.getStudentId(),
                student.getStudentName(),
                student.getEmailAddress(),
                student.getCourseName(),
                student.getProgrammingScore(),
                student.getMathematicsScore(),
                String.join(",", student.getTechnicalSkills()));

        return student;
    }

    // 3. Get all students
    public List<Student> getAllStudents() {

        String sql = "SELECT * FROM students";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> mapStudent(rs));
    }

    // 4. Get student by ID
    public Student getStudentById(int id) {

        String sql = "SELECT * FROM students WHERE student_id = ?";

        List<Student> result = jdbcTemplate.query(
                sql, (rs, rowNum) -> mapStudent(rs), id);

        return result.isEmpty() ? null : result.get(0);
    }

    // 5. Update student
    public Student updateStudent(int id, Student student) {

        validateScores(student);

        String sql = "UPDATE students SET student_name=?, email_address=?, course_name=?, programming_score=?, mathematics_score=?, technical_skills=? WHERE student_id=?";

        int rows = jdbcTemplate.update(sql,
                student.getStudentName(),
                student.getEmailAddress(),
                student.getCourseName(),
                student.getProgrammingScore(),
                student.getMathematicsScore(),
                String.join(",", student.getTechnicalSkills()),
                id);

        return rows > 0 ? getStudentById(id) : null;
    }

    // 6. Delete student
    public boolean deleteStudent(int id) {

        String sql = "DELETE FROM students WHERE student_id = ?";

        return jdbcTemplate.update(sql, id) > 0;
    }

    // 7. Calculate average score
    public double calculateAverageScore(Student student) {

        return (student.getProgrammingScore()
                + student.getMathematicsScore()) / 2.0;
    }

    // 8. Assign performance category
    public String getPerformanceCategory(Student student) {

        double average = calculateAverageScore(student);

        if (average >= 85) {
            return "Excellent";
        } else if (average >= 70) {
            return "Good";
        } else if (average >= 50) {
            return "Satisfactory";
        } else {
            return "Needs Improvement";
        }
    }

    // 9. Identify stronger subject
    public String getStrongerSubject(Student student) {

        if (student.getProgrammingScore()
                > student.getMathematicsScore()) {

            return "Programming";

        } else if (student.getMathematicsScore()
                > student.getProgrammingScore()) {

            return "Mathematics";

        } else {
            return "Equal";
        }
    }

    // 10. Search students by email
    public List<Student> getStudentsByEmail(String email) {

        String sql = "SELECT * FROM students WHERE email_address = ?";

        return jdbcTemplate.query(
                sql, (rs, rowNum) -> mapStudent(rs), email);
    }

    // 11. Search students by course
    public List<Student> getStudentsByCourse(String course) {

        String sql = "SELECT * FROM students WHERE course_name LIKE ?";

        return jdbcTemplate.query(
                sql, (rs, rowNum) -> mapStudent(rs), "%" + course + "%");
    }

    // 12. Generate merit list
    public List<Student> getMeritList() {

        String sql = "SELECT * FROM students "
                + "ORDER BY (programming_score + mathematics_score) / 2.0 DESC";

        return jdbcTemplate.query(
                sql, (rs, rowNum) -> mapStudent(rs));
    }

    // 13. Find students sharing at least one technical skill
    public List<String> getStudentsWithCommonSkills() {

        List<Student> students = getAllStudents();

        List<String> result = new ArrayList<>();

        for (int i = 0; i < students.size(); i++) {

            for (int j = i + 1; j < students.size(); j++) {

                Student s1 = students.get(i);
                Student s2 = students.get(j);

                List<String> commonSkills = new ArrayList<>(
                        s1.getTechnicalSkills());

                commonSkills.retainAll(s2.getTechnicalSkills());

                if (!commonSkills.isEmpty()) {

                    result.add(
                            s1.getStudentName() + " and "
                            + s2.getStudentName()
                            + " share skills: " + commonSkills);
                }
            }
        }

        return result;
    }

    // 14. Find students with programming < 50 and mathematics >= 75
    public List<Student> getStudentsByScoreCondition() {

        String sql = "SELECT * FROM students "
                + "WHERE programming_score < 50 "
                + "AND mathematics_score >= 75";

        return jdbcTemplate.query(
                sql, (rs, rowNum) -> mapStudent(rs));
    }

    // 15. Convert database result into Student object
    private Student mapStudent(ResultSet rs) throws SQLException {

        Student student = new Student();

        student.setStudentId(rs.getInt("student_id"));
        student.setStudentName(rs.getString("student_name"));
        student.setEmailAddress(rs.getString("email_address"));
        student.setCourseName(rs.getString("course_name"));

        student.setProgrammingScore(rs.getDouble("programming_score"));
        student.setMathematicsScore(rs.getDouble("mathematics_score"));

        String skills = rs.getString("technical_skills");

        List<String> skillList = new ArrayList<>();

        if (skills != null && !skills.isEmpty()) {
            skillList = List.of(skills.split(","));
        }

        student.setTechnicalSkills(skillList);

        return student;
    }
}