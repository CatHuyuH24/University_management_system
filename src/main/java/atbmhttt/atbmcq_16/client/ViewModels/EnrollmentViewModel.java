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
}
