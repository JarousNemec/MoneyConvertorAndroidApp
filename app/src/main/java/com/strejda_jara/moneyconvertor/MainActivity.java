package com.strejda_jara.moneyconvertor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.yaml.snakeyaml.Yaml;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerFromCurrency;
    Spinner spinnerToCurrency;
    EditText edittextFrom;
    TextView textviewTo;
    Button btnConvert;
    private String apiKey = "602e2ce3b4ad5516605f9c03";
    private String urlString = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerFromCurrency = findViewById(R.id.spinnerFrom);
        spinnerToCurrency = findViewById(R.id.spinnerTo);
        edittextFrom = findViewById(R.id.moneyToConvert);
        textviewTo = findViewById(R.id.labelResult);
        btnConvert = findViewById(R.id.btnConvert);

        ArrayAdapter<String> dataAdapter = getCurrencyesSpinnerDataAdapter();

        spinnerFromCurrency.setAdapter(dataAdapter);
        spinnerToCurrency.setAdapter(dataAdapter);
    }

    private ArrayAdapter<String> getCurrencyesSpinnerDataAdapter() {

        String[] currencies = getResources().getStringArray(R.array.currencies);

        List<String> listOfCurrencies = new ArrayList<>(Arrays.asList(currencies));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfCurrencies);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return dataAdapter;
    }

    public void convertBtnAction(View view){

        String fromCurrency = (String) spinnerFromCurrency.getSelectedItem();
        callApiForCurrencyes(urlString + fromCurrency);

    }

    void callApiForCurrencyes(String url){

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        convert(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                });

        queue.add(request);
    }

    void convert(String response){

        Map<String, Object> currencyesData = getDataOfCurrencyes(response);

        String toCurrency = (String) spinnerToCurrency.getSelectedItem();
        double currency = getValueOfRequiredCurrency(currencyesData, toCurrency);

        double count = 0;
        try {
            count = Double.parseDouble(edittextFrom.getText().toString());
        }catch (Exception e){
            System.out.println("---Reguest error---");
        }

        double result = convertMoneyByCurrency(currency,count);

        showResult(result);

    }

    Map<String, Object> getDataOfCurrencyes(String response) {

        Yaml yaml = new Yaml();
        Map<String, Object> responseData = yaml.load(response);
        Map<String, Object> currencyesData = (Map<String, Object>) responseData.get("conversion_rates");

        return currencyesData;
    }

    double getValueOfRequiredCurrency(Map<String, Object> currencyesData, String toCurrency) {

        String currencyResponseValue = "";
        currencyResponseValue += currencyesData.get(toCurrency);

        double currency = Double.parseDouble(currencyResponseValue);
        return currency;
    }

    double convertMoneyByCurrency(double currency, double count) {
        double result = count * currency;

        return result;
    }

    void showResult(double result) {

        NumberFormat nf = new DecimalFormat("####0.000");
        textviewTo.setText(nf.format(result));
    }

}