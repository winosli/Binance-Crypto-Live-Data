package com.comp.cryptotrading.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.comp.cryptotrading.R;
import com.comp.cryptotrading.MyUtils.Constants;
import com.comp.cryptotrading.MyUtils.SharedPref;
import com.comp.cryptotrading.objects.Crypto;

public class AlertCryptoDialog {
    // This dialog display an alert base on the user settings (price / change percents etc)
    private Dialog dialog;
    private Crypto crypto;
    EditText etHighPrice, etLowPrice;
    EditText etHighPercent, etLowPercent;
    Button btnSaveAlert;
    double highPrice = Constants.DEFAULT_SP_NUM;
    double lowPrice = Constants.DEFAULT_SP_NUM;
    double highPercent = Constants.DEFAULT_SP_NUM;
    double lowPercent = Constants.DEFAULT_SP_NUM;
    String exceptionMessage = "";
    boolean isSomethingSet = false;
    RadioButton rbVoice, rbPopupWindow, rbCommandWindow;

    public AlertCryptoDialog(Context context, Crypto crypto) {
        this.crypto = crypto;
        dialog = new Dialog(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.alert_crypto_dialog);
        TextView tvAlert = dialog.findViewById(R.id.tvAlertDialogSymbol);
        tvAlert.setText(crypto.getSymbol() + "");

        TextView tvPrice = dialog.findViewById(R.id.tvPriceAlertCryptoDialog);
        tvPrice.setText("Last price: " + crypto.getLastPrice());

        rbVoice = dialog.findViewById(R.id.rbVoice);
        rbPopupWindow = dialog.findViewById(R.id.rbPopupWindow);
        rbCommandWindow = dialog.findViewById(R.id.rbCommandWindow);

        btnSaveAlert = dialog.findViewById(R.id.btnSaveAlert);
        etHighPrice = dialog.findViewById(R.id.etHighPriceForAlert);
        etLowPrice = dialog.findViewById(R.id.etLowPriceForAlert);
        etHighPercent = dialog.findViewById(R.id.etHighPercentForAlert);
        etLowPercent = dialog.findViewById(R.id.etLowPercentForAlert);

        // Paste last saved values
        double lastHighPrice = SharedPref.getValue(context, (crypto.getSymbol() + Constants.HIGH_PRICE));
        if (lastHighPrice != Constants.DEFAULT_SP_NUM) {
            etHighPrice.setText(lastHighPrice + "");
        }

        double lastLowPrice = SharedPref.getValue(context, (crypto.getSymbol() + Constants.LOW_PRICE));
        if (lastLowPrice != Constants.DEFAULT_SP_NUM) {
            etLowPrice.setText(lastLowPrice + "");
        }

        double lastHighPercent = SharedPref.getValue(context, (crypto.getSymbol() + Constants.HIGH_PERCENT));
        if (lastHighPercent != Constants.DEFAULT_SP_NUM) {
            etHighPercent.setText(lastHighPercent + "");
        }

        double lastLowPercent = SharedPref.getValue(context, (crypto.getSymbol() + Constants.LOW_PERCENT));
        if (lastLowPercent != Constants.DEFAULT_SP_NUM) {
            etLowPercent.setText(lastLowPercent + "");
        }

        // Check Alert Type
        String alertType = SharedPref.getValueStr(context, Constants.ALERT_TYPE + crypto.getSymbol());

        switch (alertType) {
            case Constants.VOICE:
                rbVoice.setChecked(true);
                break;
            case Constants.POPUP_WINDOW:
                rbPopupWindow.setChecked(true);
                break;
            case Constants.POPUP_COMMAND:
                rbCommandWindow.setChecked(true);
                break;
        }

        btnSaveAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    highPrice = Double.parseDouble(etHighPrice.getText().toString());
                } catch (NumberFormatException e) {
                    if ((Double.toString(highPrice).length() > 0) && highPrice != Constants.DEFAULT_SP_NUM) {
                        exceptionMessage = "High price was not set properly";
                        Toast.makeText(context, exceptionMessage, Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                try {
                    lowPrice = Double.parseDouble(etLowPrice.getText().toString());
                } catch (NumberFormatException e) {
                    if ((Double.toString(lowPercent).length() > 0) && lowPercent != Constants.DEFAULT_SP_NUM) {
                        exceptionMessage = "Low price was not set properly";
                        Toast.makeText(context, exceptionMessage, Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                try {
                    highPercent = Double.parseDouble(etHighPercent.getText().toString());
                } catch (NumberFormatException e) {
                    if ((Double.toString(highPercent).length() > 0) && highPercent != Constants.DEFAULT_SP_NUM) {
                        exceptionMessage = "High percent was not set properly";
                        Toast.makeText(context, exceptionMessage, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                try {
                    lowPercent = Double.parseDouble(etLowPercent.getText().toString());
                } catch (NumberFormatException e) {
                    if ((Double.toString(lowPercent).length() > 0) && lowPercent != Constants.DEFAULT_SP_NUM) {
                        exceptionMessage = "Low percent was not set properly";
                        Toast.makeText(context, exceptionMessage, Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (lowPercent != Constants.DEFAULT_SP_NUM || lowPrice != Constants.DEFAULT_SP_NUM || highPercent != Constants.DEFAULT_SP_NUM || highPrice != Constants.DEFAULT_SP_NUM) {
                    isSomethingSet = true;
                    SharedPref.save(context, crypto.getSymbol(), true);
                } else {
                    isSomethingSet = false;
                    SharedPref.save(context, crypto.getSymbol(), false);
                }

                SharedPref.save(context, (crypto.getSymbol() + Constants.HIGH_PRICE), highPrice);
                SharedPref.save(context, (crypto.getSymbol() + Constants.LOW_PRICE), lowPrice);
                SharedPref.save(context, (crypto.getSymbol() + Constants.HIGH_PERCENT), highPercent);
                SharedPref.save(context, (crypto.getSymbol() + Constants.LOW_PERCENT), lowPercent);

                if (rbVoice.isChecked()) {
                    SharedPref.save(context, Constants.ALERT_TYPE + crypto.getSymbol(), Constants.VOICE);
                } else if (rbPopupWindow.isChecked()) {
                    SharedPref.save(context, Constants.ALERT_TYPE + crypto.getSymbol(), Constants.POPUP_WINDOW);
                } else if (rbCommandWindow.isChecked()) {
                    SharedPref.save(context, Constants.ALERT_TYPE + crypto.getSymbol(), Constants.POPUP_COMMAND);
                }

                Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void show() {
        dialog.show();
    }
}
