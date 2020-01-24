package com.edwardvanraak.train_api;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email,password;
    Button register,login,btn_list;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        email =findViewById(R.id.editText);
        password=findViewById(R.id.editText2);
        register =findViewById(R.id.register);
        login=findViewById(R.id.login);
        btn_list=findViewById(R.id.button3);

        login.setOnClickListener(this);
        register.setOnClickListener(this);
        btn_list.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                url="https://reqres.in/api/register";
                if (email.getText().toString().isEmpty()||password.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"fill missing data",Toast.LENGTH_SHORT).show();
                }else {
                    requestAccount(url);
                }
                break;
            case R.id.login:
                url="https://reqres.in/api/login";
                if (email.getText().toString().isEmpty()||password.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"fill missing data",Toast.LENGTH_SHORT).show();
                }else {
                    requestAccount(url);
                }
                break;
            case R.id.button3:
                startActivity(new Intent(this,UserList.class));
                break;

        }
    }



    private void requestAccount(String url) {
        StringRequest request = new StringRequest(Request.Method.POST,url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.getMessage());
            }

        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                String Email = email.getText().toString();
                String Pass = password.getText().toString();
                params.put("email",Email);
                params.put("password",Pass);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }




}
