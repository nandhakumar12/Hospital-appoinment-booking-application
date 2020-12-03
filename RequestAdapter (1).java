package com.arjun.labmasteradmin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.Item> {
    private JSONObject[] jsonObjects;
    private Context context;
    private Activity activity;
    SharedPreferences preferences;
    String slab,sdate,shour,sreason,sid,suser,srole,soverridestate,soverridenstaff,soverridensub,RejectReason="";
    String h,h1;
    String rejectDialogTitle="Enter Reject Reason";


    public JSONObject[] delete(JSONObject[] jsonObjects1,int position)
    {

                if (jsonObjects1 == null
                        || position < 0
                        || position >= jsonObjects1.length) {

                    return jsonObjects1;
                }

                // Create another array of size one less
                JSONObject[] anotherArray = new JSONObject[jsonObjects.length - 1];

                // Copy the elements except the index
                // from original array to the other array
                for (int i = 0, k = 0; i < jsonObjects1.length; i++) {

                    // if the index is
                    // the removal element index
                    if (i == position) {
                        continue;
                    }

                    // if the index is not
                    // the removal element index
                    anotherArray[k++] = jsonObjects1[i];
                }

                // return the resultant array
                return anotherArray;
    }

    public RequestAdapter(Context context, JSONObject[] jsonarray, Activity activity)
    {
        this.context=context;
        this.jsonObjects=jsonarray;
        this.activity=activity;

    }



    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout=(LinearLayout)LayoutInflater.from(context).inflate(R.layout.requestrecyclerlayout,parent,false);
        return new Item(linearLayout);

    }

    @Override
    public void onBindViewHolder(@NonNull final RequestAdapter.Item holder, final int position) {

        preferences = activity.getApplicationContext().getSharedPreferences("User_prefs", Context.MODE_PRIVATE);
        try
        {

            slab = jsonObjects[position].getString("lab");
            sid = jsonObjects[position].getString("id");
            sdate = jsonObjects[position].getString("date");
            shour = jsonObjects[position].getString("hour");
            srole = jsonObjects[position].getString("request_user");
            suser = jsonObjects[position].getString("request_by");
            sreason = jsonObjects[position].getString("reason");
            soverridenstaff = jsonObjects[position].getString("override_staff");
            soverridestate = jsonObjects[position].getString("override");
            soverridensub = jsonObjects[position].getString("override_sub");
            holder.id.setText(sid);
            holder.lab.setText(slab);
            holder.date.setText(sdate);
            holder.hour.setText(shour);
            holder.role.setText(srole);
            holder.user.setText(suser);
            holder.overridestate.setText(soverridestate);
            holder.overridenstaff.setText(soverridenstaff);
            holder.overridensub.setText(soverridensub);
            holder.reason.setText(sreason);



            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try{
                        h=holder.hour.getText().toString();
                        if(h.equals("Hour 1"))
                            h1="h1";
                        else if(h.equals("Hour 2"))
                            h1="h2";
                        else if(h.equals("Hour 3"))
                            h1="h3";
                        else if(h.equals("Hour 4"))
                            h1="h4";
                        else if(h.equals("Hour 5"))
                            h1="h5";
                        else if(h.equals("Hour 6"))
                            h1="h6";
                        else if(h.equals("Hour 7"))
                            h1="h7";

                    acceptRequest(preferences.getString("IP","")+"acceptRequest.php",holder.id.getText().toString(),holder.reason.getText().toString(),
                            holder.lab.getText().toString(),h1,holder.user.getText().toString(),holder.role.getText().toString(),
                            holder.overridenstaff.getText().toString(),holder.date.getText().toString(),holder.overridestate.getText().toString(),
                            holder.overridensub.getText().toString());
                    Notification(preferences.getString("IP","")+"notification.php",RejectReason,"Accepted",holder.id.getText().toString());


                        jsonObjects=delete(jsonObjects,position);
                        notifyItemRemoved(position);
                        notifyItemRangeRemoved(position,jsonObjects.length);


                }
                catch (Exception e)
                {
                    Toast.makeText(context,"Set Error",Toast.LENGTH_LONG).show();
                }
                }
            });



            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.Theme_AppCompat_Light_Dialog_MinWidth).create();
                 alertDialog.setTitle(rejectDialogTitle);
                 final EditText input=new EditText(activity);
                 alertDialog.setView(input);
                 alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Submit", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {

                         RejectReason=input.getText().toString();
                         jsonObjects=delete(jsonObjects,position);
                         notifyItemRemoved(position);
                         notifyItemRangeRemoved(position,jsonObjects.length);
                         rejectRequest(preferences.getString("IP","")+"rejectReason.php",holder.id.getText().toString(),RejectReason);
                         Notification(preferences.getString("IP","")+"notification.php",RejectReason,"Rejected",holder.id.getText().toString());

                     }
                 });
                 alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         RejectReason=input.getText().toString();
                     }
                 });
                 alertDialog.show();
                }
            });




        }
        catch (JSONException e)
        {
            Toast.makeText(context,"Data Error",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return jsonObjects.length;
    }

    public class Item extends RecyclerView.ViewHolder
    {
        TextView lab,date,hour,reason,id,user,role,overridestate,overridenstaff,overridensub;
        Button accept,reject;

        public Item(View itemView) {
            super(itemView);
            user=itemView.findViewById(R.id.user);
            overridestate=itemView.findViewById(R.id.overridestate);
            overridenstaff=itemView.findViewById(R.id.overriddenstaff);
            overridensub=itemView.findViewById(R.id.overridensub);
            role=itemView.findViewById(R.id.role);
            lab=itemView.findViewById(R.id.lab_recycler);
            date=itemView.findViewById(R.id.date_recycler);
            hour=itemView.findViewById(R.id.hour_recycler);
            reason=itemView.findViewById(R.id.reason_recycler);
            accept=itemView.findViewById(R.id.acceptBtn);
            reject=itemView.findViewById(R.id.cancelBtn);
            id=itemView.findViewById(R.id.reqid_recycler);



        }
    }



    private void acceptRequest(final String urlWebService,final String id,final String reason,final String lab,
                               final String hour,final String user,final String role,final String overridenstaff,
                               final String date,final String overridestate,final String overridensub) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(context,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    StringBuilder sb = new StringBuilder();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
                    String data = URLEncoder.encode("id_reason", "UTF-8") + "=" +URLEncoder.encode(reason, "UTF-8") + '&'
                            +URLEncoder.encode("id_id", "UTF-8") + "=" +URLEncoder.encode(id, "UTF-8") + '&'
                            +URLEncoder.encode("id_lab", "UTF-8") + "=" +URLEncoder.encode(lab, "UTF-8")+ '&'
                            +URLEncoder.encode("id_date", "UTF-8") + "=" +URLEncoder.encode(date, "UTF-8")+ '&'
                            +URLEncoder.encode("id_hour", "UTF-8") + "=" +URLEncoder.encode(hour, "UTF-8")+ '&'
                            +URLEncoder.encode("id_user", "UTF-8") + "=" +URLEncoder.encode(user, "UTF-8")+ '&'
                            +URLEncoder.encode("id_role", "UTF-8") + "=" +URLEncoder.encode(role, "UTF-8")+ '&'
                            +URLEncoder.encode("id_overridestate", "UTF-8") + "=" +URLEncoder.encode(overridestate, "UTF-8")+ '&'
                            +URLEncoder.encode("id_overridenstaff", "UTF-8") + "=" +URLEncoder.encode(overridenstaff, "UTF-8")+ '&'
                            +URLEncoder.encode("id_overridensub", "UTF-8") + "=" +URLEncoder.encode(overridensub, "UTF-8");



                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }


    private void rejectRequest(final String urlWebService,final String id,final String rejectreason) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(context,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    StringBuilder sb = new StringBuilder();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
                    String data = URLEncoder.encode("id_id", "UTF-8") + "=" +URLEncoder.encode(id, "UTF-8") + '&'
                            +URLEncoder.encode("id_reject", "UTF-8") + "=" +URLEncoder.encode(rejectreason, "UTF-8");


                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void Notification(final String urlWebService,final String reason,final String status,final String id) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(context,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {

                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    StringBuilder sb = new StringBuilder();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
                    String data = URLEncoder.encode("id_status", "UTF-8") + "=" +URLEncoder.encode(status, "UTF-8") + '&'
                            +URLEncoder.encode("id_reason", "UTF-8") + "=" +URLEncoder.encode(reason, "UTF-8")+ '&'
                            +URLEncoder.encode("id_id", "UTF-8") + "=" +URLEncoder.encode(id, "UTF-8");


                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
}
