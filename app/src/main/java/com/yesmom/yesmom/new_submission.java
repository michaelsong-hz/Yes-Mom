package com.yesmom.yesmom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class new_submission extends AppCompatActivity {
    private List<ParseObject> jobList;
    private ArrayList<HashMap<String, String>> fillMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_submission);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    //sendMessage();
                    handled = true;
                }
                return handled;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back button on submission page
    }


    //Adds our submit button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submission, menu);
        return true;
    }

    //Handles clicking for our submit button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_submit) {
            EditText edittext = (EditText) findViewById(R.id.editText);
            final String jobTitle = edittext.getText().toString();

            EditText edittext2 = (EditText) findViewById(R.id.editText2);
            final String description = edittext2.getText().toString();

            EditText edittext3 = (EditText) findViewById(R.id.editText3);
            final String pay = edittext3.getText().toString();

            EditText edittext4 = (EditText) findViewById(R.id.editText4);
            final String estTime = edittext4.getText().toString();

            EditText edittext5 = (EditText) findViewById(R.id.editText5);
            final String city = edittext5.getText().toString();

            EditText edittext6 = (EditText) findViewById(R.id.editText6);
            final String phoneNumber = edittext6.getText().toString();

            EditText edittext7 = (EditText) findViewById(R.id.editText7);
            final String emailAddress = edittext7.getText().toString();

            EditText edittext8 = (EditText) findViewById(R.id.editPassword);
            final String password = edittext8.getText().toString();

            String errorString = "";

            if (jobTitle.replaceAll(" ", "").equals("")) {
                errorString += " job title, ";
            }
            if (description.replaceAll(" ", "").equals("")) {
                errorString += " description, ";
            }
            if (pay.replaceAll(" ", "").equals("")) {
                errorString += " payment, ";
            }
            if (estTime.replaceAll(" ", "").equals("")) {
                errorString += " time estimate, ";
            }
            if (city.replaceAll(" ", "").equals("")) {
                errorString += " city, ";
            }
            if (phoneNumber.replaceAll(" ", "").equals("") && emailAddress.replaceAll(" ", "").equals("")) {
                errorString += " a phone number or email, ";
            }

            if (!errorString.equals("")) {
                errorString = errorString.substring(0, errorString.length() - 2);
                new AlertDialog.Builder(this)
                        .setTitle("Error with input!")
                        .setMessage("Please fill in:" + errorString)
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.ic_error_black_24px)
                        .show();
                return false;
            } else {
                ParseObject job = new ParseObject("Jobs");
                job.put("jobTitle", jobTitle);
                job.put("description", description);
                job.put("pay", pay);
                job.put("time", estTime);
                job.put("city", city);
                job.put("phone", phoneNumber);
                job.put("email", emailAddress);
                if (!password.equals(""))
                    job.put("password", password);
                job.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("new submission", "Success!");
                        } else {
                            Log.d("new submission", "Fail!");
                        }
                    }
                });
            }

            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Jobs");
            query.findInBackground(
                    new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {
                                jobList = list;
                                if (jobList.get(jobList.size() - 1).getString("jobTitle").equals(jobTitle) &&
                                        jobList.get(jobList.size() - 1).getString("description").equals(description) &&
                                        jobList.get(jobList.size() - 1).getString("pay").equals(pay) &&
                                        jobList.get(jobList.size() - 1).getString("time").equals(estTime) &&
                                        jobList.get(jobList.size() - 1).getString("city").equals(city) &&
                                        jobList.get(jobList.size() - 1).getString("phone").equals(phoneNumber) &&
                                        jobList.get(jobList.size() - 1).getString("email").equals(emailAddress)){
                                    Context context = getApplicationContext();
                                    CharSequence text = "Successfully Posted!";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                    finish();
                                } else {
                                    SystemClock.sleep(250);
                                    if (jobList.get(jobList.size() - 1).getString("jobTitle").equals(jobTitle) &&
                                            jobList.get(jobList.size() - 1).getString("description").equals(description) &&
                                            jobList.get(jobList.size() - 1).getString("pay").equals(pay) &&
                                            jobList.get(jobList.size() - 1).getString("time").equals(estTime) &&
                                            jobList.get(jobList.size() - 1).getString("city").equals(city) &&
                                            jobList.get(jobList.size() - 1).getString("phone").equals(phoneNumber) &&
                                            jobList.get(jobList.size() - 1).getString("email").equals(emailAddress)) {
                                        Context context = getApplicationContext();
                                        CharSequence text = "Successfully Posted!";
                                        int duration = Toast.LENGTH_SHORT;

                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();
                                        finish();
                                    }

/*                                    Context context = getApplicationContext();
                                    CharSequence text = "Posting Unsuccessful!";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();*/
                                    finish();
                                }
                            }
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        }
                    });

        } else {
            finish();
        }

        return true;
    }
}

