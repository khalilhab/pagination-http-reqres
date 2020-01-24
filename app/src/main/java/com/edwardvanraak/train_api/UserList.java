package com.edwardvanraak.train_api;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shamanland.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserList extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    String url;
    ArrayList<User> list= new ArrayList<>();
    RecyclerView recyclerview;
    private LinearLayoutManager mLayoutManager;
    ListAdapter adapter;
    protected Handler handler;
    public static final int PAGE_START =1;
    int lastPage,currentPage=PAGE_START,itemCount=0;
    private boolean isLastPage = false;
    private int totalPage = 4;
    private boolean isLoading = false;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton addTask;
    AlertDialog.Builder dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        recyclerview=findViewById(R.id.recyclerview);
        swipeRefreshLayout =  findViewById(R.id.refresh);
        addTask = findViewById(R.id.float_btn);
        dialog=new AlertDialog.Builder(this);

        handler = new Handler();
        swipeRefreshLayout.setOnRefreshListener(this);
        addTask.setOnClickListener(this);

        recyclerview.setHasFixedSize(true);
        mLayoutManager= new LinearLayoutManager(this);
        adapter=new ListAdapter(this,new ArrayList<User>());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(adapter);
        requestList();
        recyclerview.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                requestList();

            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }
    private void requestList() {
        final StringRequest request = new StringRequest(Request.Method.GET,"https://reqres.in/api/users?page="+ currentPage,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject=null;
                        Log.v("99999",url+currentPage);
                        try{

                            jsonObject = new JSONObject(response);
                            list = new ArrayList<>();
                            lastPage=jsonObject.getInt("total_pages");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject2=jsonArray.getJSONObject(i);
                                String id = jsonObject2.getString("id");
                                String firstName = jsonObject2.getString("first_name");
                                String lastName = jsonObject2.getString("last_name");
                                String email = jsonObject2.getString("email");
                                String image = jsonObject2.getString("avatar");
                                list.add( new User(id,firstName,lastName,email,image));
                                Log.v("22222",list.get(0).id);

                            }
                            if (currentPage != PAGE_START) {
                                Log.v("ppp",currentPage+PAGE_START+"");
                                adapter.removeLoading();
                            }

                            adapter.addAll(list);
                            if (currentPage < totalPage) adapter.addLoading();
                            else isLastPage = true;
                            isLoading = false;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.float_btn:
                LayoutInflater inflater = (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                View view = inflater.inflate(R.layout.add_user_dialog,null);

                final EditText new_name=view.findViewById(R.id.name_edit);
                final EditText new_job=view.findViewById(R.id.job_edit);
                Button create = view.findViewById(R.id.create);

                dialog.setView(view);

                final AlertDialog mdialog = dialog.create();
                mdialog.show();

                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = new_name.getText().toString();
                        String job = new_job.getText().toString();
                        requestCreate("https://reqres.in/api/users",name,job);
                        mdialog.dismiss();
                    }
                });
                break;
        }
    }

    private void requestCreate(String url, final String name, final String job) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("name", name);
                params.put("job", job);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }
}
