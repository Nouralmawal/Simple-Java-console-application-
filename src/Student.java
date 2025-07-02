public class Student {
    int id;
    String name, email, courses;
    double midterm, finalExam, gpa;

    public Student(int id, String name, String email, String courses, double midterm, double finalExam, double gpa) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.courses = courses;
        this.midterm = midterm;
        this.finalExam = finalExam;
        this.gpa = gpa;
    }
}
