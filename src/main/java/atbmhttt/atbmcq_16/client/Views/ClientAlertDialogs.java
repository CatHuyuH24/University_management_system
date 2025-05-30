package atbmhttt.atbmcq_16.client.Views;

import atbmhttt.atbmcq_16.dialogs.AlertDialog;

public class ClientAlertDialogs {
    public static void displayGeneralSQLErrorDialog() {
        AlertDialog.showErrorAlert("Error", null,
                "You cannot perform this action!\nIf you think this is a mistake or need help, please reach out to authorized staff members or higher-level personnel.",
                null, 400, 200);
    }

    public static void displayUnexpectedErrorDialog() {
        AlertDialog.showErrorAlert("Error", null,
                "An unexpected error occurred.\nPlease contact your supervisor or authorized personnel.", null,
                400, 200);
    }
}
