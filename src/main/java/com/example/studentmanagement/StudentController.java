package com.example.studentmanagement;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    // Constructor injection
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    //this above is a di

    // 1. Add student
    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    // 2. Get all students
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // 3. Get student by ID
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable int id) {
        return studentService.getStudentById(id);
    }

    // 4. Update student
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable int id,
                                 @RequestBody Student student) {
        return studentService.updateStudent(id, student);
    }

    // 5. Delete student
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable int id) {

        boolean deleted = studentService.deleteStudent(id);

        if (deleted) {
            return "Student deleted successfully!";
        }

        return "Student not found!";
    }

    // 6. Search students by email
    @GetMapping("/search/email")
    public List<Student> getStudentsByEmail(@RequestParam String email) {
        return studentService.getStudentsByEmail(email);
    }

    // 7. Search students by course
    @GetMapping("/search/course")
    public List<Student> getStudentsByCourse(@RequestParam String course) {
        return studentService.getStudentsByCourse(course);
    }

    // 8. Calculate average score
    @GetMapping("/{id}/average")
    public double calculateAverageScore(@PathVariable int id) {

        Student student = studentService.getStudentById(id);

        if (student == null) {
            throw new RuntimeException("Student not found!");
        }

        return studentService.calculateAverageScore(student);
    }

    // 9. Get performance category
    @GetMapping("/{id}/performance")
    public String getPerformanceCategory(@PathVariable int id) {

        Student student = studentService.getStudentById(id);

        if (student == null) {
            throw new RuntimeException("Student not found!");
        }

        return studentService.getPerformanceCategory(student);
    }

    // 10. Identify stronger subject
    @GetMapping("/{id}/stronger-subject")
    public String getStrongerSubject(@PathVariable int id) {

        Student student = studentService.getStudentById(id);

        if (student == null) {
            throw new RuntimeException("Student not found!");
        }

        return studentService.getStrongerSubject(student);
    }

    // 11. Generate merit list
    @GetMapping("/merit-list")
    public List<Student> getMeritList() {
        return studentService.getMeritList();
    }

    // 12. Find students sharing common technical skills
    @GetMapping("/common-skills")
    public List<String> getStudentsWithCommonSkills() {
        return studentService.getStudentsWithCommonSkills();
    }
    
 // 13. Find students based on score conditions
    @GetMapping("/score-condition")
    public List<Student> getStudentsByScoreCondition() {
        return studentService.getStudentsByScoreCondition();
    }
}