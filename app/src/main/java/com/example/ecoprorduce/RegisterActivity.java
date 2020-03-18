package com.example.ecoprorduce;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    ArrayList<User> allUsers;

    EditText emailEditText;
    EditText passwordEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText zipCodeEditText;
    Spinner citySpinner;
    Spinner accountSpinner;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        allUsers = new ArrayList<>();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        zipCodeEditText = findViewById(R.id.zipCodeEditText);
        citySpinner = findViewById(R.id.townSpinner);
        accountSpinner = findViewById(R.id.accountSpinner);
        registerButton = findViewById(R.id.RegisterButton);

        getAllUsers(new VolleyCallBack() {
            @Override
            public void onSuccess(ArrayList<User> x) {
                allUsers.addAll(x);
            }
        });



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String zipCode = zipCodeEditText.getText().toString().trim();
                int state = citySpinner.getSelectedItemPosition();
                int account = accountSpinner.getSelectedItemPosition();

                boolean validationSuccessful = true;
                if (firstName.isEmpty()) {
                    firstNameEditText.setError("Your first name is required");
                    firstNameEditText.requestFocus();
                    validationSuccessful = false;
                }

                if (firstName.contains("[0-9]+")) {
                    firstNameEditText.setError("The first name should not contain numbers");
                    firstNameEditText.requestFocus();
                    validationSuccessful = false;
                }

                if (lastName.isEmpty()) {
                    lastNameEditText.setError("Your last name is required");
                    lastNameEditText.requestFocus();
                    validationSuccessful = false;
                }

                if (lastName.contains("[0-9]+")) {
                    lastNameEditText.setError("The last name should not contain numbers");
                    lastNameEditText.requestFocus();
                    validationSuccessful = false;
                }


                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    validationSuccessful = false;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailEditText.setError("Please enter a valid email");
                    emailEditText.requestFocus();
                    validationSuccessful = false;
                }

                for (int i = 0; i < allUsers.size(); i++) {
                    if (allUsers.get(i).getEmail().compareTo(email) == 0) {
                        Log.e ("yoyo", allUsers.get(i).getEmail());
                        emailEditText.setError("An account with this email already exists");
                        emailEditText.requestFocus();
                        validationSuccessful = false;
                    }
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    validationSuccessful = false;
                }

                if (password.length() < 6) {
                    passwordEditText.setError("Minimum length of password is 6");
                    passwordEditText.requestFocus();
                    validationSuccessful = false;
                }

                if (zipCode.isEmpty()) {
                    zipCodeEditText.setError("The zip code should not be empty");
                    zipCodeEditText.requestFocus();
                    validationSuccessful = false;
                }

                if (zipCode.length() != 4) {
                    zipCodeEditText.setError("The zip code needs to be 4 digits");
                    zipCodeEditText.requestFocus();
                    validationSuccessful = false;
                }

                if (!Pattern.matches("[0-9]+", zipCode)) {
                    zipCodeEditText.setError("The zip code should contain only numbers");
                    zipCodeEditText.requestFocus();
                    validationSuccessful = false;
                }

                if(validationSuccessful) {

                    int tempZipCode = Integer.parseInt(zipCode);

                  /*  Log.e ("firstName", firstName);
                    Log.e ("lastName", lastName);
                    Log.e ("emaik", email);
                    Log.e ("password", password);
                    Log.e ("zipcode", tempZipCode);
                    Log.e ("state", Integer.toString(state));
                    Log.e ("account", Integer.toString(account));*/

                    addUser(firstName, lastName, email, password, tempZipCode, state, account);

                    allUsers.clear();
                    finish();
                }

            }
        });

    }

    public void getAllUsers(final VolleyCallBack callBack) {


        requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "http://ecoproduce.eu/api/User",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Rest response", response.toString());

                        Gson gson = new Gson();
                        User user = new User();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                user = gson.fromJson(response.getJSONObject(i).toString(), User.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            allUsers.add(user);
                        }
                        callBack.onSuccess(allUsers);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error response", error.toString());
                    }
                }
        );
        requestQueue.add(arrayRequest);
    }

    private void addUser(String firstName, String lastName, String email, String password, int zipCode, int state, int account) {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("firstName", firstName);
            jsonObject.put("lastName", lastName);
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("zipCode", zipCode);
            jsonObject.put("state", state);
            jsonObject.put("account", account);

            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://ecoproduce.eu/api/User/register", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);
                Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                Toast.makeText(getApplicationContext(), "Unable to register", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringRequest);
    }

    public interface VolleyCallBack {
        void onSuccess(ArrayList<User> x);
    }

}
