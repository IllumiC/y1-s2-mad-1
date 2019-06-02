package mt.edu.mcast.liamscerri.tracksidedad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    // RecylerView references
    private RecyclerView recyclerView;
    private RecyclerAdapter<Laptime> mAdapter;
    //private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");

    //Last lap time
    private long lastTime;
    //State
    private int state;
    /* 0 - Main
     * 1 - Timer Running
     * 2 - Finished Running */

    // Decimal Formats used activity wide
    final DecimalFormat df1 = new DecimalFormat("00.000");
    final DecimalFormat df2 = new DecimalFormat("0");

    private DatabaseReference databaseSessions;

    private Session currentSession = new Session();
    private Chronometer chronometer;

    private void initRecyclerView(Session session) {
        // Find Recycler View element in your current Activity
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_session);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));



        // specify an adapter
        mAdapter = new RecyclerAdapter<Laptime>(this, "recycler_item_template", session.GetTimesList()) {
            // Customize the methods hereunder

            @Override
            public void configRecyclerItemModel(Item itemContext, View itemView) {
                // Override this method to configure all properties that each recycler item model should hold within

                itemContext.textView1 = itemView.findViewById(R.id.recycler_item1);
                itemContext.textView2 = itemView.findViewById(R.id.recycler_item2);
                itemContext.linearLayout = itemView.findViewById(R.id.recyclerContainer);
            }

            @Override
            public void paintRecyclerItem(RecyclerView.ViewHolder holder, final int position, Laptime dataObject){
                // Override this method to reflect the data you want to render in your Recycler View items
                ((Item) holder).textView1.setText(Integer.toString(dataObject.GetId()));
                double seconds = ((double)dataObject.GetTime() % 60000)/1000;
                double minutes = ((double)dataObject.GetTime() - (double)dataObject.GetTime() % 60000)/60000;
                String timeString;
                if (minutes > 0){
                    timeString = df2.format(minutes) + ":" + df1.format(seconds);
                } else {
                    timeString = df1.format(seconds);
                }

                ((Item) holder).textView2.setText(timeString);
            }
        };

        // set adapter to recycler view
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentSession = (Session)savedInstanceState.getSerializable("currentSession");
            state = savedInstanceState.getInt("state");
            lastTime = savedInstanceState.getLong("lastTime");
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseSessions = FirebaseDatabase.getInstance().getReference("sessions");

        chronometer = (Chronometer) findViewById(R.id.chronometer);
        if (state == 1){
            chronometer.start();
        }
        ImageButton button1 = (ImageButton)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state == 0){
                    state = 1;
                    lastTime = SystemClock.elapsedRealtime();
                    chronometer.setBase(lastTime);
                    chronometer.start();
                } else if (state == 1){
                    long nowTime = SystemClock.elapsedRealtime();
                    long interval = nowTime-lastTime;
                    currentSession.AddTime(interval);
                    mAdapter.updateItems(currentSession.GetTimesList());
                    mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
                    recyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                    lastTime = nowTime;
                    chronometer.stop();
                    chronometer.setBase(nowTime);
                    chronometer.start();
                }
            }
        });
        button1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (state == 1){
                    state = 2;
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.stop();
                    currentSession.Lock();
                }
                return true;
            }
        });
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mEditor = mPreferences.edit();
        mEditor.putString("date","default");
        mEditor.apply();

        navigationView.setNavigationItemSelectedListener(this);
        initRecyclerView(currentSession);
    }

    private void saveSession(){
        String id = databaseSessions.push().getKey();
        databaseSessions.child(id).setValue(currentSession);
        //Had to abandon firebase since it doesn't support nested children lists
    }

    @Override
    public void onStop(){
        super.onStop();
        Date date = new Date();
        mEditor = mPreferences.edit();
        mEditor.putString("date",dateFormat.format(date));
        mEditor.putInt("state",state);
        mEditor.putLong("time",lastTime);
        mEditor.apply();
    }

    @Override
    public void onPause(){
        super.onPause();
        Date date = new Date();
        mEditor = mPreferences.edit();
        mEditor.putString("date",dateFormat.format(date));
        mEditor.putInt("state",state);
        mEditor.putLong("time",lastTime);
        mEditor.apply();
    }

    @Override
    public void onResume(){
        super.onResume();
        lastTime = mPreferences.getLong("time", SystemClock.elapsedRealtime());
        state = mPreferences.getInt("state", 0);
        Snackbar.make(findViewById(android.R.id.content), mPreferences.getString("date","no date saved"), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onStart(){
        super.onStart();
        lastTime = mPreferences.getLong("time", SystemClock.elapsedRealtime());
        state = mPreferences.getInt("state", 0);
        Snackbar.make(findViewById(android.R.id.content), mPreferences.getString("date","no date saved"), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent dataIntent = new Intent(MainActivity.this, TutorialActivity.class);
            dataIntent.putExtra("Laps", currentSession.GetSize());
            startActivity(dataIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_help) {
            Intent dataIntent = new Intent(MainActivity.this, TutorialActivity.class);
            dataIntent.putExtra("Laps", currentSession.GetSize());
            startActivity(dataIntent);
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Just wanted to share with you how amazing this application is");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_send) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Just couldn't resist sending you a message telling you about my application");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("currentSession", currentSession);
        outState.putInt("state", state);
        outState.putLong("lastTime", lastTime);
    }
}
