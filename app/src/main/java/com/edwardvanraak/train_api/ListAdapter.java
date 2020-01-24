package com.edwardvanraak.train_api;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    AlertDialog.Builder dialog;
    private List<User> list;
    Context context;

    public ListAdapter(Context mContext,List<User> list) {
        this.list = list;
        this.context=mContext;
        dialog=new AlertDialog.Builder(context);

    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.user_row,
                                        parent, false));
            case VIEW_TYPE_LOADING:
                return new FooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_item, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == list.size() - 1
                    ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void add(User response) {
        list.add(response);
        notifyItemInserted(list.size() - 1);
    }

    public void addAll(List<User> postItems) {
        for (User response : postItems) {
            add(response);
        }
    }


    private void remove(User postItems) {
        int position = list.indexOf(postItems);
        if (position > -1) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoading() {
        isLoaderVisible = true;
        add(new User());
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = list.size() - 1;
        User item = getItem(position);
        if (item != null) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    User getItem(int position) {
        return list.get(position);
    }


    public class ViewHolder extends BaseViewHolder {
        TextView textViewFirst,textViewID, textViewLast,textViewEmail;
        ImageView image;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            textViewFirst =itemView.findViewById(R.id.textView4);
            textViewLast =itemView.findViewById(R.id.textView5);
            textViewID=itemView.findViewById(R.id.textView2);
            textViewEmail=itemView.findViewById(R.id.textView3);
            image=itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.cardview);
        }

        protected void clear() {

        }

        public void onBind(final int position) {
            super.onBind(position);
            YoYo.with(Techniques.Landing)
                    .playOn(itemView.findViewById(R.id.cardview));
            User item = list.get(position);
            textViewFirst.setText(item.getFirst());
            textViewLast.setText(item.getLast());
            textViewID.setText(item.getId());
            textViewEmail.setText(item.getEmail());
            Glide.with(context)
                    .load(item.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .override(400, 400)
                    .circleCrop()
                    .into(image);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                    View view = inflater.inflate(R.layout.user_dialog,null);

                    ImageView dialog_image = view.findViewById(R.id.img);
                    TextView name =view.findViewById(R.id.name);
                    TextView id = view.findViewById(R.id.dialog_id);
                    TextView email = view.findViewById(R.id.email_dialog);
                    final EditText new_name=view.findViewById(R.id.name_edit);
                    final EditText new_job=view.findViewById(R.id.job_edit);
                    Button update = view.findViewById(R.id.update);

                    dialog.setView(view);


                    final User item = list.get(position);
                    name.setText(item.getFirst()+" "+item.getLast());
                    id.setText(item.getId());
                    email.setText(item.getEmail());
                    Glide.with(context)
                            .load(item.getImage())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.user)
                            .error(R.drawable.user)
                            .override(400, 400)
                            .circleCrop()
                            .into(dialog_image);
                    final AlertDialog mdialog = dialog.create();
                    mdialog.show();
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name = new_name.getText().toString();
                            String job = new_job.getText().toString();
                            String id = item.getId();
                            requestUpdate("https://reqres.in/api/users/",name,job,id);
                            mdialog.dismiss();
                        }
                    });

                }
            });
        }
    }

    public class FooterHolder extends BaseViewHolder {

        ProgressBar mProgressBar;


        FooterHolder(View itemView) {
            super(itemView);
mProgressBar=itemView.findViewById(R.id.progressBar1
);
        }

        @Override
        protected void clear() {

        }

    }

    private void requestUpdate(String url, final String name, final String job, String id) {
        StringRequest request = new StringRequest(Request.Method.PATCH,url ,
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
                String mName = name;
                String mJaob = job;
                params.put("name",mName);
                params.put("job",mJaob);

                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }
}
