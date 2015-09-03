package se.kmdev.tvepg.epg.misc;

import android.content.Context;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.joda.time.LocalDate;

/**
 * Created by Kristoffer.
 */
public class EPGUtil {
    private static final String TAG = "EPGUtil";
    private static Picasso picasso = null;

    public static String getShortTime(long timeMillis) {
        int hour = (int) (timeMillis / 1000 / 60 / 60) % 24;
        int min = (int) (timeMillis / 1000 / 60) % 60;
        return hour + ":" + min;
    }

    public static String getShortHour(long timeMillis) {
        int hour = (int) (timeMillis / 1000 / 60 / 60) % 24;
        if (hour != 0)
            return hour + "h";
        return "  " + hour + "h";
    }

    public static String getShortMin(long timeMillis) {
        int min = (int) (timeMillis / 1000 / 60) % 60;
        return "" + min;
    }

    public static String getWeekdayName(long dateMillis) {
        LocalDate date = new LocalDate(dateMillis);
        return date.dayOfWeek().getAsText();
    }

    public static void loadImageInto(Context context, String url, int width, int height, Target target) {
        initPicasso(context);

        picasso.load(url)
                .resize(width, height)
                .centerInside()
                .into(target);
    }

    private static void initPicasso(Context context) {
        if (picasso == null) {
            picasso = new Picasso.Builder(context)
                    .downloader(new OkHttpDownloader(new OkHttpClient()))
                    .listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            Log.e(TAG, exception.getMessage());
                        }
                    })
                    .build();
        }
    }

    public static long getCurrentTime() {
        Time time = new Time();
        time.setToNow();
        return (time.hour * 60 * 60 + time.minute * 60) * 1000;
    }

}
