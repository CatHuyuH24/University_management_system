package atbmhttt.atbmcq_16.client.ViewModels;

import atbmhttt.atbmcq_16.client.Repositories.StudentsRepository;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StudentsViewModel {
    private final StudentsRepository studentsRepository;
    private final SimpleListProperty<String[]> students;

    public StudentsViewModel() {
        this.studentsRepository = new StudentsRepository();
        this.students = new SimpleListProperty<>(FXCollections.observableArrayList());
        try {
            this.students.setAll(studentsRepository.getStudentsWithDetails());
        } catch (SQLException e) {
            System.err.println(e.getErrorCode());
            e.printStackTrace();
            this.students.setAll(new ArrayList<>());
        }
    }

    public ObservableList<String[]> getStudents() {
        return students.get();
    }

    public void updateStudentAttribute(String masv, String column, String newValue) throws Exception {
        // Validation based on schema
        switch (column) {
            case "MASV":
                if (newValue.length() > 10 || newValue.isEmpty())
                    throw new IllegalArgumentException("Student ID (MASV) must be 1-10 characters.");
                break;
            case "HOTEN":
                if (newValue.length() > 50 || newValue.isEmpty())
                    throw new IllegalArgumentException("Full Name (HOTEN) must be 1-50 characters.");
                break;
            case "PHAI":
                if (!(newValue.equals("Nam") || newValue.equals("Nu")))
                    throw new IllegalArgumentException("Gender (PHAI) must be 'Nam' or 'Nu'.");
                break;
            case "NGSINH":
                try {
                    java.time.LocalDate.parse(newValue, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Date of Birth (NGSINH) must be in format yyyy-MM-dd.");
                }
                break;
            case "DCHI":
                if (newValue.length() > 100)
                    throw new IllegalArgumentException("Address (DCHI) must be at most 100 characters.");
                break;
            case "DT":
                if (newValue.length() > 15)
                    throw new IllegalArgumentException("Phone (DT) must be at most 15 characters.");
                break;
            case "KHOA":
                if (newValue.length() > 10 || newValue.isEmpty())
                    throw new IllegalArgumentException("Department (KHOA) must be 1-10 characters.");
                break;
            case "TINHTRANG":
                if (!(newValue.equals("Đang học") || newValue.equals("Nghỉ học") || newValue.equals("Bảo lưu")))
                    throw new IllegalArgumentException(
                            "Status (TINHTRANG) must be one of: Đang học, Nghỉ học, Bảo lưu.");
                break;
            default:
                throw new Exception("Illegal argument!");
        }
        int rowCount = studentsRepository.updateStudentAttribute(masv, column, newValue);
        // Update the local students list without querying the repository
        for (String[] student : students) {
            if (student[0].equals(masv)) { // Assuming MASV is at index 0
                switch (column) {
                    case "MASV":
                        student[0] = newValue;
                        break;
                    case "HOTEN":
                        student[1] = newValue;
                        break;
                    case "PHAI":
                        student[2] = newValue;
                        break;
                    case "NGSINH":
                        student[3] = newValue;
                        break;
                    case "DCHI":
                        student[4] = newValue;
                        break;
                    case "DT":
                        student[5] = newValue;
                        break;
                    case "KHOA":
                        student[6] = newValue;
                        break;
                    case "TINHTRANG":
                        student[7] = newValue;
                        break;
                }
                // Notify listeners about the change
                students.set(students.indexOf(student), student);
                break;
            }
        }

        if (rowCount == 0) {
            System.out.println("No student information was updated!");
            throw new Exception();
        }
    }
}
