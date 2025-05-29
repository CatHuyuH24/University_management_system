package atbmhttt.atbmcq_16.client.Views;

import atbmhttt.atbmcq_16.dialogs.AlertDialog;

public class ClientAlertDialogs {
    public static void displayGeneralErrorDialog() {
        AlertDialog.showErrorAlert("Error", null,
                "You cannot perform this action!\nIf you think this is a mistake or need help, please reach out to authorized staff members or higher-level personnel.",
                null, 400, 200);
    }
}
