package atbmhttt.atbmcq_16.client.ViewModels;

import atbmhttt.atbmcq_16.client.Repositories.EnrollmentRepository;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.ArrayList;

public class EnrollmentViewModel {
    private final EnrollmentRepository enrollmentRepository;
    private final SimpleListProperty<String[]> enrollments;

    public EnrollmentViewModel() {
        this.enrollmentRepository = new EnrollmentRepository();
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
            enrollmentRepository.addEnrollment(masv, mamm);
            enrollments.add(new String[] { masv, mamm, null, null, null, null });

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
}
