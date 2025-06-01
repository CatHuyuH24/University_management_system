package atbmhttt.atbmcq_16.client;

import atbmhttt.atbmcq_16.dialogs.AlertDialog;

public class ClientAlertDialogs {
    public static void displayGeneralSQLErrorDialog() {
        AlertDialog.showErrorAlert("ERROR", null,
                "You cannot perform this action!\nIf you think this is a mistake or need help, please reach out to authorized staff members or higher-level personnel.",
                null, 400, 200);
    }

    public static void displayUnexpectedErrorDialog() {
        AlertDialog.showErrorAlert("ERROR", null,
                "An unexpected error occurred.\nPlease contact your supervisor or authorized personnel.", null,
                400, 200);
    }
}
