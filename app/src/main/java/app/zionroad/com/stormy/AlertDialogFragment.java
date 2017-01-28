package app.zionroad.com.stormy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Define the AlertDialogFragment, which extends the common DialogFragment class.
 * This class build a dialog, what get the current activity as context.
 * (Builder is a nested factory in the AlertDialog class)
 * The builder get the dialog title, message and positive button as string resources.
 * Finally, return the built dialog.
 * (This class should be public!)
 */
public class AlertDialogFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.error_title)
                .setMessage(R.string.error_message)
                // this method wait a second onClickListener argument, what is null in this case
                // it means, when the user tap this button, the dialog will be closed
                .setPositiveButton(R.string.error_ok, null);
        return builder.create();
    }
}
