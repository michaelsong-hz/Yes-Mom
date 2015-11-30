package com.yesmom.yesmom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class JobDescriptionActivity extends AppCompatActivity {

    private String m_Text = "";
    private ParseObject job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_description);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Gets the stuff
        Bundle extras = getIntent().getExtras();
        String postName = extras.getString("PostName");
        String description = extras.getString("Description");
        String pay = extras.getString("Pay");
        String estTime = extras.getString("EstTime");
        String city = extras.getString("City");
        String phoneNumber = extras.getString("PhoneNumber");
        String emailAddress = extras.getString("EmailAddress");

        setTitle(postName);

        TextView descriptionT = (TextView) findViewById(R.id.description);
        descriptionT.setText(description);
        TextView payT = (TextView) findViewById(R.id.pay);
        payT.setText(pay);
        TextView estTimeT = (TextView) findViewById(R.id.staticestimatedtime);  //we use staticestimatedtime because michael s fucked up and was too lazy to fix it the proper way
        estTimeT.setText(estTime);
        TextView cityT = (TextView) findViewById(R.id.city);
        cityT.setText(city);
        TextView phoneNumberT = (TextView) findViewById(R.id.number);
        phoneNumberT.setText(phoneNumber);
        TextView emailAddressT = (TextView) findViewById(R.id.email);
        emailAddressT.setText(emailAddress);

    }

    //Handles clicking for our submit button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_delete) {

            Bundle extras = getIntent().getExtras();
            final String password = extras.getString("Password");
            final String objectID = extras.getString("objectId");

            if (password == null) {
                Context context = getApplicationContext();
                CharSequence text = "Unable to remove this file!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return false;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please enter the password");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();

                    if (m_Text.equals(password)) {

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Jobs");
                        query.getInBackground(objectID, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null) {
                                    job = parseObject;
                                    deleteMethod();
                                } else {
                                    Log.d("city", "Error: " + e.getMessage());
                                }
                            }
                        });



                    } else {
                        Context context = getApplicationContext();
                        CharSequence text = "Incorrect password";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();


        }

        return true;
    }

    private void deleteMethod() {
        job.put("toDelete", true);
        job.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Context context = getApplicationContext();
                    CharSequence text = "File has been marked for removal!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Removal failed: Network communication error";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_description, menu);
        return true;
    }

}
