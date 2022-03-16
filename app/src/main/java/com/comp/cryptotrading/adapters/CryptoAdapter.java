package com.comp.cryptotrading.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.comp.cryptotrading.R;
import com.comp.cryptotrading.MyUtils.Constants;
import com.comp.cryptotrading.MyUtils.SharedPref;
import com.comp.cryptotrading.dialogs.CommandDialog;
import com.comp.cryptotrading.objects.Crypto;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.MyViewHolder> {

    private List<Crypto> list;
    private Context context;
    private onCryptoListener onCryptoListener;
    private LinkedHashMap<String, Crypto> linkedHashMap;
    boolean isSoundPlayed = false;
    HashMap<String, String> hashMapAlerts;

    public CryptoAdapter(Context context, List<Crypto> list, onCryptoListener onCryptoListener) {
        this.context = context;
        this.list = list;
        this.onCryptoListener = onCryptoListener;
        hashMapAlerts = new HashMap<>();
    }

    public CryptoAdapter(Context context, LinkedHashMap<String, Crypto> linkedHashMap, onCryptoListener onCryptoListener) {
        this.context = context;
        this.linkedHashMap = linkedHashMap;
        this.onCryptoListener = onCryptoListener;
        hashMapAlerts = new HashMap<>();
    }

    @NonNull
    @Override
    public CryptoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.crypto_recycler_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoAdapter.MyViewHolder holder, int position) {
        Crypto crypto = null;
        if (list != null) {
            crypto = list.get(position);
        } else {
            holder.llCryptoRow.setTag(position);
            // crypto = linkedHashMap.get(position);
            int i = 0;

            for (Map.Entry<String, Crypto> entry : linkedHashMap.entrySet()) {
                if (position == i) {
                    String key = entry.getKey();
                    crypto = entry.getValue();

                    String extraText = "\n";
                    holder.tvSymbol.setText(context.getResources().getText(R.string.symbol) + extraText + crypto.getSymbol());
                    holder.tvStockExchange.setText(context.getResources().getText(R.string.stockExchange) + extraText + crypto.getStockExchange());
                    holder.tvLastPrice.setText(context.getResources().getText(R.string.lastPrice) + extraText + crypto.getLastPrice());
                    holder.tvPriceChangePercent.setText(context.getResources().getText(R.string.priceChangePercent) + extraText + crypto.getPriceChangePercent());
                    holder.tvTotalNumberOfTrades.setText(context.getResources().getText(R.string.totalNumberOfTrades) + extraText + crypto.getTotalNumberOfTrades());
                    holder.tvAsk.setText(context.getResources().getText(R.string.ask) + extraText + crypto.getAsk());
                    holder.tvBid.setText(context.getResources().getText(R.string.bid) + extraText + crypto.getBid());
                    String eventDate = getDate(crypto.getEventTime());
                    holder.tvEventTime.setText(context.getResources().getText(R.string.eventTime) + extraText + eventDate);

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    boolean isCryptoHasAlert = sharedPreferences.getBoolean(crypto.getSymbol(), false);
                    if (isCryptoHasAlert) {
                        holder.tvSymbol.setTypeface(null, Typeface.BOLD);
                        // Check all relevant alerts

                        boolean isLastHighPriceAlert = false;
                        boolean isLastLowPriceAlert = false;
                        boolean isLastHighPercent = false;
                        boolean isLastLowPercent = false;

                        double lastHighPrice = SharedPref.getValue(context, (crypto.getSymbol() + Constants.HIGH_PRICE));
                        if (lastHighPrice != Constants.DEFAULT_SP_NUM) {
                            if (Double.parseDouble(crypto.getLastPrice()) >= lastHighPrice) {
                                isLastHighPriceAlert = true;
                            }
                        }

                        double lastLowPrice = SharedPref.getValue(context, (crypto.getSymbol() + Constants.LOW_PRICE));
                        if (lastLowPrice != Constants.DEFAULT_SP_NUM) {
                            if (Double.parseDouble(crypto.getLastPrice()) <= lastLowPrice) {
                                isLastLowPriceAlert = true;
                            }
                        }

                        double lastHighPercent = SharedPref.getValue(context, (crypto.getSymbol() + Constants.HIGH_PERCENT));
                        if (lastHighPercent != Constants.DEFAULT_SP_NUM) {
                            if (Double.parseDouble(crypto.getPriceChangePercent()) >= lastHighPercent) {
                                isLastHighPercent = true;
                            }
                        }

                        double lastLowPercent = SharedPref.getValue(context, (crypto.getSymbol() + Constants.LOW_PERCENT));
                        if (lastLowPercent != Constants.DEFAULT_SP_NUM) {
                            if (Double.parseDouble(crypto.getPriceChangePercent()) <= lastLowPercent) {
                                isLastLowPercent = true;
                            }
                        }

                        // Check if 1 of the alerts already occur
                        if (isLastHighPriceAlert || isLastLowPriceAlert || isLastHighPercent || isLastLowPercent) {
//                            holder.llCryptoRow.setBackground(context.getDrawable(R.drawable.design_row));
                            holder.llCryptoRow.setBackgroundColor(Color.CYAN);
                            String message = "";
                            if (isLastHighPriceAlert) {
                                //Toast.makeText(context, "isLastHighPriceAlert", Toast.LENGTH_SHORT).show();
                                message = "- High price alert";
                            }

                            if (isLastLowPriceAlert) {
                                //    Toast.makeText(context, "isLastLowPriceAlert", Toast.LENGTH_SHORT).show();
                                message = "- Low price alert";
                            }

                            if (isLastHighPercent) {
                                //       Toast.makeText(context, "isLastHighPercent", Toast.LENGTH_SHORT).show();
                                message = "- High percent alert";
                            }

                            if (isLastLowPercent) {
                                //     Toast.makeText(context, "isLastLowPercent", Toast.LENGTH_SHORT).show();
                                message = "- Low percent alert";
                            }

                            String alertType = SharedPref.getValueStr(context, Constants.ALERT_TYPE + crypto.getSymbol());
                            String alertHashKey = alertType + crypto.getSymbol() + message;

                            switch (alertType) {
                                case Constants.VOICE:
                                    if (hashMapAlerts.containsKey(alertHashKey) == false) {
                                        hashMapAlerts.put(alertHashKey, alertHashKey);
                                        final MediaPlayer mp = MediaPlayer.create(context, R.raw.alert);
                                        mp.start();
                                        isSoundPlayed = true;

                                        Handler handler = new Handler();

                                        Runnable runnable2 = new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.llCryptoRow.setBackground(context.getDrawable(R.drawable.design_row));
                                            }
                                        };
                                        holder.llCryptoRow.setBackgroundColor(Color.YELLOW);
                                        handler.postDelayed(runnable2, 1500);
                                    }
                                    break;
                                case Constants.POPUP_WINDOW:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Alert for " + crypto.getSymbol());
                                    builder.setMessage(message);
                                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User clicked OK button
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    if (hashMapAlerts.containsKey(alertHashKey) == false) {
                                        hashMapAlerts.put(alertHashKey, alertHashKey);
                                        dialog.show();
                                    }
                                    break;
                                case Constants.POPUP_COMMAND:
                                    if (hashMapAlerts.containsKey(alertHashKey) == false) {
                                        hashMapAlerts.put(alertHashKey, alertHashKey);
                                        CommandDialog commandDialog = new CommandDialog(context, crypto, message);
                                        commandDialog.showDialog();
                                    }
                                    break;
                            }
                        } else {
                            holder.llCryptoRow.setBackground(context.getDrawable(R.drawable.design_row));
                            //holder.llCryptoRow.setBackgroundColor(Color.WHITE);
                        }
                    } else {
                        //holder.llCryptoRow.setBackgroundColor(Color.WHITE);
                        holder.llCryptoRow.setBackground(context.getDrawable(R.drawable.design_row));
                        holder.tvSymbol.setTypeface(null, Typeface.NORMAL);
                    }
                    break;
                }
                i++;
            }

        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return this.list.size();
        } else {
            return this.linkedHashMap.size();
        }
    }

    public void setCryptoList(List<Crypto> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setCryptoHashMap(LinkedHashMap<String, Crypto> linkedHashMap) {
        this.linkedHashMap = linkedHashMap;
        notifyDataSetChanged();
    }

    private String getDate(String eventTimeMilliseconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        long eventTimeToLong = Long.parseLong(eventTimeMilliseconds);
        String dateString = simpleDateFormat.format(eventTimeToLong);
        return String.format("%s", dateString);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayoutCompat llCryptoRow;
        TextView tvSymbol;
        TextView tvStockExchange;
        TextView tvLastPrice;
        TextView tvPriceChangePercent;
        TextView tvTotalNumberOfTrades;
        TextView tvAsk;
        TextView tvBid;
        TextView tvEventTime;

        public MyViewHolder(View view) {
            super(view);
            llCryptoRow = view.findViewById(R.id.llCryptoRow);
            tvSymbol = view.findViewById(R.id.tvSymbolRow);
            tvStockExchange = view.findViewById(R.id.tvStockExchangeRow);
            tvLastPrice = view.findViewById(R.id.tvLastPriceRow);
            tvPriceChangePercent = view.findViewById(R.id.tvPriceChangePercentRow);
            tvTotalNumberOfTrades = view.findViewById(R.id.tvTotalNumberOfTradesRow);
            tvAsk = view.findViewById(R.id.tvAskRow);
            tvBid = view.findViewById(R.id.tvBidRow);
            tvEventTime = view.findViewById(R.id.tvEventTimeRow);

            llCryptoRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCryptoListener.onCryptoListener(getAdapterPosition());
                }
            });
        }
    }

    public interface onCryptoListener {
        void onCryptoListener(int position);
    }
}
