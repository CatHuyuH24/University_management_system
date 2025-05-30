package atbmhttt.atbmcq_16.client.ViewModels;

import atbmhttt.atbmcq_16.client.Repositories.EnrollmentAndGradesRepository;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.ArrayList;

public class EnrollmentAndGradesViewModel {
    private final EnrollmentAndGradesRepository enrollmentRepository;
    private final SimpleListProperty<String[]> enrollments;

    public EnrollmentAndGradesViewModel() {
        this.enrollmentRepository = new EnrollmentAndGradesRepository();
        this.enrollments = new SimpleListProperty<>(FXCollections.observableArrayList());
        try {
            this.enrollments.setAll(enrollmentRepository.getAllEnrollments());
        } catch (SQLException e) {
            e.printStackTrace();
            this.enrollments.setAll(new ArrayList<>());
        }
    }

    public ObservableList<String[]> getEnrollments() {
        return enrollments.get();
    }

    public void addEnrollment(String masv, String mamm) throws Exception {
        try {
            atbmhttt.atbmcq_16.helpers.InputValidator.validateInput(masv);
            atbmhttt.atbmcq_16.helpers.InputValidator.validateInput(mamm);
            masv = masv.toUpperCase();
            mamm = mamm.toUpperCase();
            enrollmentRepository.addEnrollment(masv, mamm);
            enrollments.add(new String[] { masv, mamm, null, null, null, null });

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

    public void deleteEnrollment(String masv, String mamm) throws Exception {
        try {
            atbmhttt.atbmcq_16.helpers.InputValidator.validateInput(masv);
            atbmhttt.atbmcq_16.helpers.InputValidator.validateInput(mamm);
            masv = masv.toUpperCase();
            mamm = mamm.toUpperCase();
            int affected = enrollmentRepository.deleteEnrollment(masv, mamm);
            if (affected == 0) {
                throw new Exception(
                        "No enrollment information was deleted! Either MASV: " + masv + " or MAMM: " + mamm
                                + " was not found.");
            } else {
                // to avoid lambda error
                final String masvUp = masv;
                final String mammUp = mamm;

                enrollments.removeIf(enrollment -> enrollment[0] != null && enrollment[1] != null &&
                        enrollment[0].equalsIgnoreCase(masvUp) && enrollment[1].equalsIgnoreCase(mammUp));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void updateGrades(String masv, String mamm, Double diemth, Double diemqt, Double diemck, Double diemtk)
            throws Exception {
        try {
            atbmhttt.atbmcq_16.helpers.InputValidator.validateInput(masv);
            atbmhttt.atbmcq_16.helpers.InputValidator.validateInput(mamm);
            masv = masv.toUpperCase();
            mamm = mamm.toUpperCase();
            if (!isGradeValid(diemth))
                throw new IllegalArgumentException("DIEMTH must be between 0 and 10 or empty.");
            if (!isGradeValid(diemqt))
                throw new IllegalArgumentException("DIEMQT must be between 0 and 10 or empty.");
            if (!isGradeValid(diemck))
                throw new IllegalArgumentException("DIEMCK must be between 0 and 10 or empty.");
            if (!isGradeValid(diemtk))
                throw new IllegalArgumentException("DIEMTK must be between 0 and 10 or empty.");
            int affected = enrollmentRepository.updateGrades(masv, mamm, diemth, diemqt, diemck, diemtk);
            if (affected == 0) {
                throw new Exception("No enrollment found for MASV: " + masv + ", MAMM: " + mamm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean isGradeValid(Double grade) {
        return grade == null || (grade.compareTo(0.0) >= 0 && grade.compareTo(10.0) <= 0);
    }
}
