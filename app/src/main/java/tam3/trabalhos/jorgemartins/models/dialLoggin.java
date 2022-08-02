package tam3.trabalhos.jorgemartins.models;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import tam3.trabalhos.jorgemartins.R;

public class dialLoggin {
        private Activity activity;
        private AlertDialog dialog;


        public dialLoggin(Activity myActivity) {
            activity = myActivity;
        }

        @SuppressLint("InflateParams")
        public void startDial() {

            // adding ALERT Dialog builder object and passing activity as parameter
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            // layoutinflater object and use activity to get layout inflater
            LayoutInflater inflater = activity.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.c_dialog, null));
            builder.setCancelable(true);

            dialog = builder.create();
            dialog.show();
        }

        public void finishDial() {
            dialog.dismiss();
        }
}
