package com.londonappbrewery.bitcointicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    // Constants:
    // TODO: Create the base URL
//    private final String BASE_URL = "https://apiv2.bitcoin ...";
    private final String BASE_URL = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC";
    // Member Variables:
    TextView mPriceTextView;
    String mCurrency = "NZD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPriceTextView = (TextView) findViewById(R.id.priceLabel);
        Spinner spinner = (Spinner) findViewById(R.id.currency_spinner);

        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrency = (String) parent.getSelectedItem();
                getPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getPrice();
    }

    // TODO: complete the letsDoSomeNetworking() method
    private void getPrice() {
        String url = BASE_URL + mCurrency;
        AsyncHttpClient client = new AsyncHttpClient();
        debug("Initial connect timeout: " + client.getConnectTimeout());
        client.setConnectTimeout(1000);
        debug("New connect timeout: " + client.getConnectTimeout());
        RequestParams params = new RequestParams();
        debug("Calling url: " + url);
        mPriceTextView.setText(R.string.label_default_text);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                debug("onSuccess called");
                double last;
                try {
                    last = response.getDouble("last");
                    mPriceTextView.setText(String.valueOf(last));
                } catch (JSONException e) {
                    error("Failed to parse response", e);
//                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                error("onFailure called statusCode: " + statusCode, throwable);
                mPriceTextView.setText(R.string.label_error_text);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                error("onFailure called statusCode: " + statusCode, throwable);
                mPriceTextView.setText(R.string.label_error_text);
            }
        });

//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                // called when response HTTP status is "200 OK"
//                Log.d("Clima", "JSON: " + response.toString());
//                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
//                updateUI(weatherData);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
//                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                Log.d("Clima", "Request fail! Status code: " + statusCode);
//                Log.d("Clima", "Fail response: " + response);
//                Log.e("ERROR", e.toString());
//                Toast.makeText(WeatherController.this, "Request Failed", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void error(String message, Throwable throwable) {
        Log.e("bitcointicker", message);
        throwable.printStackTrace();
        showShortToast(message);
    }

    private void showShortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    void debug(String message) {
        Log.d("bitcointicker", message);
    }

}
