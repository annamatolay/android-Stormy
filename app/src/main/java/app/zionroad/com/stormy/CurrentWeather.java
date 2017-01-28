package app.zionroad.com.stormy;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Define the CurrentWeather model, what store the data, based on the forecast API.
 * Each fields have a setter and getter, and if it necessary the app can formatting the return data.
 * The fields corresponding to the json data and the return type corresponding to the app.
 * More info look:
 * (scroll down)
 * @see MainActivity#updateDisplay()
 * Dark Sky API Documentation: https://darksky.net/dev/docs/
 */
class CurrentWeather {
    private String mIcon;
    private Long mTime;
    private Double mTempereture;
    private Double mHumidity;
    private Double mPrecipChance;
    private String mSummary;
    private String mTimeZone;

    private String getIcon() {
        return mIcon;
    }

    Integer getIconId() {
        // Possible icons (Based on doc -> https://darksky.net/dev/docs/response):
        // clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, partly-cloudy-night

        Integer iconId = null;

        // check the icon "type" then return the correct icon image (from the resource, drawable folder)
        switch (getIcon()) {
            case "clear-day": iconId = R.drawable.clear_day;
                break;
            case "clear-night": iconId = R.drawable.clear_night;
                break;
            case "rain": iconId = R.drawable.rain;
                break;
            case "snow": iconId = R.drawable.snow;
                break;
            case "sleet": iconId = R.drawable.sleet;
                break;
            case "wind": iconId = R.drawable.wind;
                break;
            case "fog": iconId = R.drawable.fog;
                break;
            case "cloudy": iconId = R.drawable.cloudy;
                break;
            case "partly-cloudy-day": iconId = R.drawable.partly_cloudy;
                break;
            case "partly-cloudy-night": iconId = R.drawable.cloudy_night;
                break;
        }
        return iconId;
    }

    void setIcon(String icon) {
        mIcon = icon;
    }

    private Long getTime() {
        return mTime;
    }

    String getFormattedTime() {
        // formatting the time (what get in second)
        // (Thanks God, finally working!!)
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimeZone));
        return formatter.format(getTime()*1000);
    }

    void setTime(Long time) {
        mTime = time;
    }

    // return as int, because for more readable and don't screw the screen
    Integer getTempereture() {return mTempereture.intValue();}

    void setTempereture(Double tempereture) {
        mTempereture = tempereture;
    }

    Double getHumidity() {
        return mHumidity;
    }

    void setHumidity(Double humidity) {
        mHumidity = humidity;
    }

    // (originally this number spreads 0-1, therefore the app multiply by 100)
    // return as int, because for more readable and don't screw the screen
    Integer getPrecipChance() {
        Double precipPercentage = mPrecipChance*100;
        return precipPercentage.intValue();
    }

    void setPrecipChance(Double precipChance) {
        mPrecipChance = precipChance;
    }

    String getSummary() {
        return mSummary;
    }

    void setSummary(String summary) {
        mSummary = summary;
    }

    String getTimeZone() {
        return mTimeZone;
    }

    void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }
}
