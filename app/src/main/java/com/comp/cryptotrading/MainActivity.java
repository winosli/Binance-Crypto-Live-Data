package com.comp.cryptotrading;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.comp.cryptotrading.adapters.CryptoAdapter;
import com.comp.cryptotrading.dialogs.AlertCryptoDialog;
import com.comp.cryptotrading.objects.Crypto;
import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

public class MainActivity extends Activity implements CryptoAdapter.onCryptoListener {

    private String TAG = "MainActivity_";
    private WebSocketClient mWebSocketClient;
    private String mMsg = "Empty";
    RecyclerView rvMain;
    private CryptoAdapter adapter;
    List<Crypto> list;
    LinkedHashMap<String,Crypto>linkedHashMap;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        linkedHashMap = new LinkedHashMap<>();
        rvMain = findViewById(R.id.rvMain);
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvMain.addItemDecoration(dividerItemDecoration);

        list = new ArrayList<>();
        adapter = new CryptoAdapter(MainActivity.this, linkedHashMap, this);
        adapter.setCryptoHashMap(linkedHashMap);
        rvMain.setAdapter(adapter);

        // Api end point for live data
        String realTimeCoinsAddress = "wss://stream.binance.com:9443/ws/!ticker@arr";

        recyclerViewAutoScroll();

        try {
            connect(realTimeCoinsAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recyclerViewAutoScroll() {
        rvMain.post(new Runnable() {
            @Override
            public void run() {
                // Call smooth scroll
                int count = adapter.getItemCount();
                if (count > 0) {
                    rvMain.smoothScrollToPosition(adapter.getItemCount() - 1);
                }
            }
        });
    }

    // Using websocket to get the live data
    private void connect(String websocketEndPointUrl) throws Exception {
        URI uri;
        try {
            Log.i(TAG, " WSURL: " + websocketEndPointUrl);

            uri = new URI(websocketEndPointUrl);
        } catch (URISyntaxException e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String msg) {
                // Message called each time we have a new data, we parse the JSON to specific objects
                mMsg = msg;
                try {
                    // Parse json into our "crypto" object
                    JSONArray jsonArray = new JSONArray(mMsg);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        String symbol = jObject.getString("s");
                        String stockExchange = "?";
                        String lastPrice = jObject.getString("c");
                        String priceChangePercent = jObject.getString("P");
                        int totalNumberOfTrades = jObject.getInt("n");
                        String ask = jObject.getString("a");
                        String bid = jObject.getString("b");
                        String eventTime = jObject.getString("E");

                        Crypto crypto = new Crypto(symbol, stockExchange, lastPrice, priceChangePercent, totalNumberOfTrades, ask, bid, eventTime);
                        //Log.d("symbol>", symbol);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                linkedHashMap.put(symbol,crypto);
                                if(progressBar.getVisibility() == View.VISIBLE){
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });

                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // tvMessage.setText(mMsg);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };

        if (websocketEndPointUrl.indexOf("wss") == 0) {
            try {
                SSLContext sslContext = SSLContext.getDefault();
                mWebSocketClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sslContext));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        mWebSocketClient.connect();
    }

    @Override
    public void onCryptoListener(int position) {
        int i = 0;
        Crypto crypto = null;
        for (Map.Entry<String, Crypto> entry : linkedHashMap.entrySet()) {
            if (position == i) {
                String key = entry.getKey();
                crypto = entry.getValue();
                break;
            }
            i++;
        }

        AlertCryptoDialog dialog = new AlertCryptoDialog(MainActivity.this, crypto);
        dialog.show();
    }

    int backCounter = 0;

    @Override
    public void onBackPressed() {
        if(backCounter > 0){
            super.onBackPressed();
        }else{
            Toast.makeText(MainActivity.this, "Click again to Exit",Toast.LENGTH_SHORT).show();
            backCounter++;
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    backCounter = 0;
                }
            };
            handler.postDelayed(runnable, 1800);
        }
    }
}