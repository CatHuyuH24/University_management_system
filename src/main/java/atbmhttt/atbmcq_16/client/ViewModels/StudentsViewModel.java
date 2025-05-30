package atbmhttt.atbmcq_16.client.ViewModels;

import atbmhttt.atbmcq_16.client.Repositories.StudentsRepository;
import atbmhttt.atbmcq_16.helpers.InputValidator;

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

    public String[] updateStudentAttributeAndReturnStudentToBeRendered(
            String studentID, String column, String newValue)
            throws Exception {
        // Validation based on schema

        InputValidator.validateInput(column);
        InputValidator.validateInput(studentID);
        InputValidator.validateInput(newValue);
        studentID = studentID.toUpperCase();

        // reach here means inputs are clean
        switch (column) {
            case "studentID":
                if (newValue.length() > 10 || newValue.isEmpty())
                    throw new IllegalArgumentException("Student ID (studentID) must be 1-10 characters.");
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
                    java.time.LocalDate.parse(newValue, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Date of Birth (NGSINH) must be in format DD-MM-YYYY.");
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

        try {
            int rowCount = studentsRepository.updateStudentAttribute(studentID, column, newValue);
            if (rowCount == 0) {
                throw new Exception(
                        "Exeption when updating\nMASV: " + studentID + "\nColumn: " + column + "\nNew value: "
                                + newValue + "\nNo student information was updated!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        // reaching here means db update operation is successful
        String[] student = null;
        // Update the local students list without querying the repository
        for (int i = 0; i < students.size(); i++) {
            student = students.get(i);

            if (student[0].equals(studentID)) { // Assuming studentID is at index 0
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
                students.set(i, student);
                return student;
            }
        }

        return null;

    }

    public void addStudent(String[] studentFields) throws Exception {
        // studentFields: [MASV, HOTEN, PHAI, NGSINH, DCHI, DT, KHOA]
        if (studentFields.length != 7)
            throw new IllegalArgumentException("All 7 fields are required.");
        String[] columns = { "MASV", "HOTEN", "PHAI", "NGSINH", "DCHI", "DT", "KHOA" };
        for (int i = 0; i < columns.length; i++) {
            InputValidator.validateInput(studentFields[i]);
        }
        studentFields[0] = studentFields[0].toUpperCase();

        // Field-specific validation (reuse logic from
        // updateStudentAttributeAndReturnUpdatedStudent)
        if (studentFields[0].length() > 10 || studentFields[0].isEmpty())
            throw new IllegalArgumentException("Student ID (MASV) must be 1-10 characters.");
        if (studentFields[1].length() > 50 || studentFields[1].isEmpty())
            throw new IllegalArgumentException("Full Name (HOTEN) must be 1-50 characters.");
        if (!(studentFields[2].equals("Nam") || studentFields[2].equals("Nu")))
            throw new IllegalArgumentException("Gender (PHAI) must be 'Nam' or 'Nu'.");
        try {
            java.time.LocalDate.parse(studentFields[3], java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Date of Birth (NGSINH) must be in format DD-MM-YYYY.");
        }
        if (studentFields[4].length() > 100)
            throw new IllegalArgumentException("Address (DCHI) must be at most 100 characters.");
        if (studentFields[5].length() > 15)
            throw new IllegalArgumentException("Phone (DT) must be at most 15 characters.");
        if (studentFields[6].length() > 10 || studentFields[6].isEmpty())
            throw new IllegalArgumentException("Department (KHOA) must be 1-10 characters.");

        try {
            studentsRepository.addStudent(studentFields);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        // No need to add to observable list, as inserter can't see the records
    }

    public void deleteStudentByMASV(String masv) throws Exception {
        InputValidator.validateInput(masv);
        masv = masv.toUpperCase();
        int affected = 0;
        try {
            affected = studentsRepository.deleteStudentByMASV(masv);
            if (affected == 0) {
                throw new Exception("No student information was deleted! MASV: " + masv + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        // No need to update observable list, as deleter can't see the records
    }
}
