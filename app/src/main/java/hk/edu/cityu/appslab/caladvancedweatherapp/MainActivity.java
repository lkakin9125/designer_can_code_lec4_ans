package hk.edu.cityu.appslab.caladvancedweatherapp;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends AppCompatActivity implements DrawerFragment.NavigationDrawerCallbacks {

    // for example
    TextView xml;


    // Data Source
    List<Weather> data;

    // Adapter
    WeatherAdapter weatherAdapter;

    private ListView weatherList;
    private DrawerLayout drawerLayout;
    private DrawerFragment drawerFragment;
    private String[] woeidArray;
    private WeatherFragment weatherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        //set up the woeid array to select the location
        //woeid is a parameter of Yahoo Weather to choose the location
        woeidArray = new String[]{"2165352", "2151330", "2306179", "44418", "615702", "2459115"};

        //find the the DrawerFragment
        //since it is a Fragment, it need to find like below
        drawerFragment = (DrawerFragment)
                getFragmentManager().findFragmentById(R.id.drawer_fragment);
        //select the first location,which is HK, when the app start
        onNavigationDrawerItemSelected(0);

        //In onCreate will not setUp the drawer since it need to config the ActionBar
        //However, the ActionBar will occur after the WeatherFragment config,
        // since the Toolbar is drawn at the WeatherFragment
        //it is not wonder that the WeatherFragment finish config or not
        //Therefore, the setUpDrawer should be called at WeatherFragment via getActivity()
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        int id = item.getItemId();
        if (id == R.id.action_refresh){

            weatherFragment.startDownloadWeather();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void setUpDrawer() {
        //find the drawer_layout in order to know device type
        View layout = findViewById(R.id.drawer_layout);
        if (layout != null && layout instanceof DrawerLayout)// a case for phone since phone will use DrawerLayout
            drawerLayout = (DrawerLayout) layout;
        else// case for tablet since it will use LinearLayout
            drawerLayout = null;
        //do the setUp
        drawerFragment.setUp(
                R.id.drawer_fragment,
                drawerLayout);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //change the location
        YWeatherAPI.WOEID = woeidArray[position];
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        //set the replace animation
        fragmentTransaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
        //create the new Fragment and replace it into weather_fragment_container
        weatherFragment = new WeatherFragment();
        fragmentTransaction.replace(R.id.weather_fragment_container, weatherFragment);
        //the replace action and the animation will occur after commit()
        fragmentTransaction.commit();
    }
}
