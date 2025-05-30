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
}
