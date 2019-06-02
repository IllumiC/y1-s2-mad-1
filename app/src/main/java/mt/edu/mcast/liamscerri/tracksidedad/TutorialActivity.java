package mt.edu.mcast.liamscerri.tracksidedad;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class TutorialActivity extends AppCompatActivity {

    int lap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        TextView lapcount = (TextView)findViewById(R.id.counter_tutorial);

        Intent tutorialIntent = getIntent();
        Bundle bundle = tutorialIntent.getExtras();
        if (bundle != null){
            lap = bundle.getInt("Laps", -1);
        }
        else if (savedInstanceState!=null){
            lap = savedInstanceState.getInt("Laps", -1);
        }
        else
        {
            lap = 0;
        }
        lapcount.setText("Nice, you've completed " +Integer.toString(lap) +" laps this session.");
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Laps", lap);
    }
}
