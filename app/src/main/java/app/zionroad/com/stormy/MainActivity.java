package app.zionroad.com.stormy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    // define the fields for the forecast api -> https://darksky.net/dev/
    private final static String API_URL = "https://api.darksky.net/forecast/";
    private final static String API_KEY = "41fa2073a5a3614726f140e1deec75d8";
    // set tag for logging
    private final String TAG = this.getClass().getSimpleName();
    // for more info look at the CurrentWeather class
    // (and the updateDisplay method at the bottom of this class)
    private CurrentWeather mCurrentWeather;

    // Define injection for the butter knife (look this below)
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.timeView) TextView mTimeView;
    @BindView(R.id.temperatureView) TextView mTemperatureView;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryView) TextView mSummaryView;
    @BindView(R.id.locationView) TextView mLocationView;
    @BindView(R.id.refreshImageView) ImageView mRefreshImage;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inject this activity to the butter knife (so the InjectViews given to its)
        // (butter knife 8.0 added to the gradle dependency)
        ButterKnife.bind(this);

        // first of all, the app hides the progress bar
        mProgressBar.setVisibility(View.INVISIBLE);
        // set a listener for the refreshing "button"
        // (what is an image, but from that will behaves like a button)
        mRefreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the relevant data from the API
                // (for more info scroll down)
                getForecast();
            }
        });
        getForecast();
    }

    private void getForecast() {
        // Define the location (in this case it is Budapest, Hungary)
        // (I know, this is an ugly hard-coding part, but now this wasn't in focus)
        Double latitude = 47.495976;
        Double longitude = 19.046525;
        // build the final url for api
        String forecastUrl = API_URL+API_KEY+"/"+latitude+","+longitude;

        if (isNetworkAvailable()) {
            // for refresh the "button"
            // (more info below)
            toggleRefresh();
            // define a new client as OkHttpClient
            // (added to gradle dependency)
            // (INTERNET permission added to AndroidManifest)
            OkHttpClient client = new OkHttpClient();
            // make a request with the built url
            Request request = new Request.Builder().url(forecastUrl).build();
            // call the request
            Call call = client.newCall(request);
            // make a callback for the call
            // it means the app continues to running, while the client response
            // so the app and the api independent from each other
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // get back and use the API answer in the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    // notify the user if something went really wrong
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    // this happens, when the app gets a response
                    try {
                        if (!response.isSuccessful()) {
                            alertUserAboutError();
                        }
                        // get json from response
                        String jsonData = response.body().string();
                        // get details from the received json
                        mCurrentWeather = getCurrentDetails(jsonData);
                        // get back and use the API answer in the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDisplay();
                            }
                        });
                        // every case the app try logging the response
                        Log.i(TAG, ">>> RESPONSE BODY: " + jsonData);
                    } catch (IOException | JSONException e) {
                        // catch and logging the exceptions
                        Log.e(TAG, ">>> Exception caught: ", e);
                    }
                }
            });
        } else {
            // notify the user, if the internet is unavailable
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        // define a new manager, what use the connectivity service
        // get the info from that service
        // check the networkInfo not null (it means the app have internet access) and the app can connected
        // finally, return the result as boolean
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void toggleRefresh() {
        // manage the progress bar and the refresh image visibility, these depends from each other
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImage.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImage.setVisibility(View.VISIBLE);
        }
    }

    private void alertUserAboutError() {
        // when the response wasn't successfully, the app create a new dialog, what informs the user about the error
        // (more info look at the AlertDialogFragment class)
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject currently = forecast.getJSONObject("currently");
        // create a new currentWeather option from the json objects and assign these to the class fields
        // (finally, return the object)
        // (for more info look at the CurrentWeather class)
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setTempereture(currently.getDouble("temperature"));
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTimeZone(forecast.getString("timezone"));
        return currentWeather;
    }

    private void updateDisplay() {
        // get the widgets, then set their image or text and format the strings resource if it necessary
        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
        mTimeView.setText(String.format(getString(R.string.default_time), mCurrentWeather.getFormattedTime()));
        mTemperatureView.setText(String.valueOf(mCurrentWeather.getTempereture()));
        mHumidityValue.setText(String.valueOf(mCurrentWeather.getHumidity()));
        mPrecipValue.setText(String.format(
                getString(R.string.precip_value), String.valueOf(mCurrentWeather.getPrecipChance())));
        mSummaryView.setText(mCurrentWeather.getSummary());
        mLocationView.setText(mCurrentWeather.getTimeZone());

    }
}
