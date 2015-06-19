package hk.edu.cityu.appslab.caladvancedweatherapp;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class WeatherFragment extends Fragment {

    // Data Source
    List<Weather> data;

    // Adapter
    WeatherAdapter weatherAdapter;

    private ListView weatherList;
    private SwipeRefreshLayout swipeRefreshLayout;


    //it is a method like onCreate() at Activity
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Fragment is no setContentView().
        // it need to inflater the layout and return it at this method
        // instead of using setContentView() to select the layout
        View rootView = inflater.inflate(R.layout.fragment_weather_list, container, false);
        //find the view from the layout
        weatherList = (ListView) rootView.findViewById(R.id.weather_list);
        //set up the Toolbar as ActionBar
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        //using getActivity to access the activity which contain this Fragment and set the Toolbar as ActionBar
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //setElevation to config the shadow of toolbar
        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.elevation));

        //set up the emptyView of weatherList
        weatherList.setEmptyView(rootView.findViewById(R.id.empty_view));
        //find swipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        //download the weather from Yahoo API
        startDownloadWeather();
        //setup the Drawer since the toolbar should be finished the setup to be ActionBar
        //it can setUpDrawer to config the ActionBar
        ((MainActivity) getActivity()).setUpDrawer();

        //set the color of the loading animation of swipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.accent_dark, R.color.accent, R.color.accent_light);

        //add the listener to trigger the action of swipeRefreshLayout pull down refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                startDownloadWeather();
            }
        });

        //show the loading animation at the fragment occur until finish the download
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });


        return rootView;
    }

    public void startDownloadWeather() {
        swipeRefreshLayout.setRefreshing(true);
        new WeatherQueryTask().execute();
    }

    //AsyncTask is to prevent no UI response to the user while data is downloading
    //Remember using multi-threading is not a choice and it is a "must" of Android system
    //if you try to use UI thread to have a network access
    //your app will be force closed
    //p.s. AsyncTask is only one of the ways to use multi-threading to do network access.
    //there are some many library to do network access, like Volley
    //but the logic behind is similar with AsyncTask
    //using AsyncTask as a example is because it is build-in library
    private class WeatherQueryTask extends AsyncTask<Void, Void, Void> {

        //onPreExecute method will run before the download start
        @Override
        protected void onPreExecute() {
            //show the animation of swipeRefreshLayout
            swipeRefreshLayout.setRefreshing(true);
            //set null adapter to weatherList so that the emptyView will be show again if refreshing
            weatherList.setAdapter(null);
        }

        //this part will run in the background and download the information from Yahoo Weather
        @Override
        protected Void doInBackground(Void... arg0) {

            // 1. Populate the Data Source
            String xml = YWeatherAPI.getForecastXml();

            // 1.1 Convert the xml to List of Weather Object
            try {
                WeatherParser parser = new WeatherParser(xml);
                data = parser.getWeatherForecastList();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        //onPostExecute will run after the downloading
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // 2. Fill the adapter with data
            weatherAdapter = new WeatherAdapter(data);

            // 3. Setup the ListView
            //set the adapter to weatherList
            weatherList.setAdapter(weatherAdapter);
            //end the loading animation of swipeRefreshLayout
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
