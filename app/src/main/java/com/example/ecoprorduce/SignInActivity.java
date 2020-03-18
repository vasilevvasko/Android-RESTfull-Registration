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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    ArrayList<User> allUsers = new ArrayList<>();

    EditText emailEditText;
    EditText passwordEditText;
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailEditText = findViewById(R.id.EmailEditText);
        passwordEditText = findViewById(R.id.PasswordEditText);
        signInButton = findViewById(R.id.SignInButton);

        getAllUsers(new VolleyCallBack() {
            @Override
            public void onSuccess(ArrayList<User> x) {
                allUsers.addAll(x);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                boolean validationSuccessful = false;

                for (int i = 0; i < allUsers.size(); i++) {
                    Log.e ("ALlusers", allUsers.get(i).getEmail());
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

                String hashedPassword = "";
                try {
                    hashedPassword = hashPassword(password);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                boolean credentialsMatch = false;
                for (int i = 0; i < allUsers.size(); i++) {
                    if ((allUsers.get(i).getEmail().compareTo(email) == 0) && (allUsers.get(i).getPasswordHash().compareTo(hashedPassword) == 0)) {
                        credentialsMatch = true;
                        break;
                    }
                    else {
                        credentialsMatch = false;
                    }
                }
                if (!credentialsMatch) {
                    Toast.makeText(getApplicationContext(), "Email or password is incorrect", Toast.LENGTH_LONG).show();
                    validationSuccessful = false;
                }
                else {
                    validationSuccessful = true;
                }

                if(validationSuccessful) {
                    Toast.makeText(getApplicationContext(), "You have successfully signed in", Toast.LENGTH_LONG).show();

                    allUsers.clear();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("validationSuccessful", validationSuccessful);
                    setResult(Activity.RESULT_OK, resultIntent);

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

    private String hashPassword(String password) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());

        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b  & 0xff));
        }

        return sb.toString();
    }

    public interface VolleyCallBack {
        void onSuccess(ArrayList<User> x);
    }

}
