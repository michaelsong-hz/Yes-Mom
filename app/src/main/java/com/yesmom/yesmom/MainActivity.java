package com.yesmom.yesmom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private List<ParseObject> jobList;
    private ArrayList<HashMap<String, String>> fillMaps;
    private SwipeRefreshLayout swipeContainer;
    private boolean toRefresh;
    private String m_Text = "";
    private List<ParseObject> cityJobs;
    private Menu mMenu;
    boolean locationOn;

    @Override
    protected void onResume() {
        super.onResume();
        if (toRefresh == true) {
            refreshCalled();
            toRefresh = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toRefresh = false;
        locationOn = false;

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCalled();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toRefresh = true;
                startActivity(new Intent(view.getContext(), new_submission.class));
            }
        });

        final ListView listview = (ListView) findViewById(R.id.listview);

        Parse.initialize(this, "QEG3LT8qgBgCBGA5WfBBjdi6eAlkcih7bCgdIIIt", "niLlEu6g5uYiDYhs3McMS0Gp4EDwmQfjKQ27LluJ");

        fillMaps = new ArrayList<HashMap<String, String>>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Jobs");
        query.findInBackground(
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            jobList = list;
                            for (int i = jobList.size() - 1; i >= 0; i--) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                Log.d("My App", jobList.get(i).getString("jobTitle"));
                                map.put("rowid", "" + jobList.get(i).getString("jobTitle"));

                                String smalldescription = jobList.get(i).getString("city") + ", " +
                                        "Pay: " + jobList.get(i).getString("pay") +
                                        ", ~" + jobList.get(i).getString("time");

                                map.put("col_1", "" + smalldescription);
                                fillMaps.add(map);
                            }
                            insertData(listview);
                        }
                    }
                });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, JobDescriptionActivity.class);
                intent.putExtra("PostName", jobList.get(jobList.size() - 1 - position).getString("jobTitle"));
                intent.putExtra("Description", jobList.get(jobList.size() - 1 - position).getString("description"));
                intent.putExtra("Pay", jobList.get(jobList.size() - 1 - position).getString("pay"));
                intent.putExtra("EstTime", jobList.get(jobList.size() - 1 - position).getString("time"));
                intent.putExtra("City", jobList.get(jobList.size() - 1 - position).getString("city"));
                intent.putExtra("PhoneNumber", jobList.get(jobList.size() - 1 - position).getString("phone"));
                intent.putExtra("EmailAddress", jobList.get(jobList.size() - 1 - position).getString("email"));
                intent.putExtra("Password", jobList.get(jobList.size() - 1 - position).getString("password"));
                intent.putExtra("objectId", jobList.get(jobList.size() - 1 - position).getObjectId());
                Log.d("our object ID: ", jobList.get(jobList.size() - 1 - position).getObjectId());
                startActivity(intent);
            }
        });


    }

    private void insertData(ListView listview) {
        String[] from = new String[]{"rowid", "col_1"};
        int[] to = new int[]{R.id.firstLine, R.id.secondLine};

        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.list_item, from, to);
        listview.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.search) {

            final MenuItem clearMenu = mMenu.findItem(R.id.action_clear);
//            clearMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//            clearMenu.setVisible(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Search for a location!");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected;
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();

                    locationOn = true;
                    //refreshCalled();
                    clearMenu.setVisible(true);
                    setTitle("Results for: \"" + m_Text + "\"");

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Jobs");
                    query.whereStartsWith("city", m_Text);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> cityJobsList, ParseException e) {
                            if (e == null) {
                                cityJobs = cityJobsList;
                                refreshCalled();
                                //only show these on main screen
                            } else {
                                Log.d("city", "Error: " + e.getMessage());
                            }
                        }
                    });
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

        if (id == R.id.action_clear){
            locationOn = false;
            refreshCalled();
            setTitle("Yes, Mom!");
            final MenuItem clearMenu = mMenu.findItem(R.id.action_clear);
            clearMenu.setVisible(false);
        }

        return super.onOptionsItemSelected(item);
    }


    public void refreshCalled() {
        //Refreshes the list
        final ListView listview = (ListView) findViewById(R.id.listview);

        if (!locationOn) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Jobs");
            query.findInBackground(
                    new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {
                                jobList = list;
                                //We have to clear the list before we re-add the elements when refreshing
                                fillMaps.clear();
                                for (int i = jobList.size() - 1; i >= 0; i--) {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    Log.d("My App", jobList.get(i).getString("jobTitle"));
                                    map.put("rowid", "" + jobList.get(i).getString("jobTitle"));
                                    String smalldescription = jobList.get(i).getString("city") + ", " +
                                            "Pay: " + jobList.get(i).getString("pay") +
                                            ", ~" + jobList.get(i).getString("time");

                                    map.put("col_1", "" + smalldescription);
                                    fillMaps.add(map);
                                }
                                swipeContainer.setRefreshing(false);
                                insertData(listview);
                            }
                        }
                    });

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent(MainActivity.this, JobDescriptionActivity.class);
                    intent.putExtra("PostName", jobList.get(jobList.size() - 1 - position).getString("jobTitle"));
                    intent.putExtra("Description", jobList.get(jobList.size() - 1 - position).getString("description"));
                    intent.putExtra("Pay", jobList.get(jobList.size() - 1 - position).getString("pay"));
                    intent.putExtra("EstTime", jobList.get(jobList.size() - 1 - position).getString("time"));
                    intent.putExtra("City", jobList.get(jobList.size() - 1 - position).getString("city"));
                    intent.putExtra("PhoneNumber", jobList.get(jobList.size() - 1 - position).getString("phone"));
                    intent.putExtra("EmailAddress", jobList.get(jobList.size() - 1 - position).getString("email"));
                    intent.putExtra("Password", jobList.get(jobList.size() - 1 - position).getString("password"));
                    intent.putExtra("objectId", jobList.get(jobList.size() - 1 - position).getObjectId());
                    Log.d("our object ID: ", jobList.get(jobList.size() - 1 - position).getObjectId());
                    startActivity(intent);
                }
            });
        } else {
            //We have to clear the list before we re-add the elements when refreshing
            fillMaps.clear();
            for (int i = cityJobs.size() - 1; i >= 0; i--) {
                HashMap<String, String> map = new HashMap<String, String>();
                Log.d("My App", cityJobs.get(i).getString("jobTitle"));
                map.put("rowid", "" + cityJobs.get(i).getString("jobTitle"));
                String smalldescription = cityJobs.get(i).getString("city") + ", " +
                        "Pay: " + cityJobs.get(i).getString("pay") +
                        ", ~" + cityJobs.get(i).getString("time");
                map.put("col_1", "" + smalldescription);
                fillMaps.add(map);
            }
            swipeContainer.setRefreshing(false);
            insertData(listview);
            
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent(MainActivity.this, JobDescriptionActivity.class);
                    intent.putExtra("PostName", cityJobs.get(cityJobs.size() - 1 - position).getString("jobTitle"));
                    intent.putExtra("Description", cityJobs.get(cityJobs.size() - 1 - position).getString("description"));
                    intent.putExtra("Pay", cityJobs.get(cityJobs.size() - 1 - position).getString("pay"));
                    intent.putExtra("EstTime", cityJobs.get(cityJobs.size() - 1 - position).getString("time"));
                    intent.putExtra("City", cityJobs.get(cityJobs.size() - 1 - position).getString("city"));
                    intent.putExtra("PhoneNumber", cityJobs.get(cityJobs.size() - 1 - position).getString("phone"));
                    intent.putExtra("EmailAddress", cityJobs.get(cityJobs.size() - 1 - position).getString("email"));
                    intent.putExtra("Password", cityJobs.get(cityJobs.size() - 1 - position).getString("password"));
                    intent.putExtra("objectId", cityJobs.get(cityJobs.size() - 1 - position).getObjectId());
                    Log.d("our object ID: ", cityJobs.get(cityJobs.size() - 1 - position).getObjectId());
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(false);
            }
        }, 5000);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//        MenuItem item = menu.findItem(R.id.action_clear);
//        item.setVisible(true);
        return true;
    }
}
