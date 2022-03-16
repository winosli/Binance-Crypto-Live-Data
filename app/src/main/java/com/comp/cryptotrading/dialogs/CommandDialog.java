package com.comp.cryptotrading.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.comp.cryptotrading.R;
import com.comp.cryptotrading.objects.Crypto;
import com.comp.cryptotrading.pickers.DecimalPicker;

public class CommandDialog {
    private Dialog dialog;
    private Crypto crypto;

    public CommandDialog(Context context, Crypto crypto, String alertMessage){
        dialog = new Dialog(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.command_crypto_dialog);
        TextView tvMessage = dialog.findViewById(R.id.tvCommandMessageCryptoDialog);
        tvMessage.setText(alertMessage);
        this.crypto = crypto;

        DecimalPicker dpAmount = (DecimalPicker) dialog.findViewById(R.id.np_action_price);

        Button btnBuy = dialog.findViewById(R.id.btnBuyCommandDialog);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Buy option is not available on this Demo version", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnSell = dialog.findViewById(R.id.btnSellCommandDialog);
        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Sell option is not available on this Demo version", Toast.LENGTH_SHORT).show();
            }
        });

        TextView tvDetails = dialog.findViewById(R.id.tvCommandCryptoFullDetailsDialog);

        String fullDetails = "Change percent: " + crypto.getPriceChangePercent() + " | " + "Ask: " + crypto.getAsk() + " | Bid: " + crypto.getBid();
        tvDetails.setText(fullDetails);

        TextView tvPrice = dialog.findViewById(R.id.tvCommandCryptoPrice);
        tvPrice.setText(crypto.getLastPrice() + "$");
    }

    public void showDialog(){
        dialog.show();
    }
}
