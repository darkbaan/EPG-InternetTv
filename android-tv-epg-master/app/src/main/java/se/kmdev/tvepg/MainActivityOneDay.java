package se.kmdev.tvepg;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import se.kmdev.tvepg.EPGApiCaller.EPGCaller;
import se.kmdev.tvepg.EPGApiCaller.ServerLink;
import se.kmdev.tvepg.epg.EPGClickListener;
import se.kmdev.tvepg.epg.EPGData;
import se.kmdev.tvepg.epg.EPGoneDay;
import se.kmdev.tvepg.epg.domain.EPGChannel;
import se.kmdev.tvepg.epg.domain.EPGEvent;
import se.kmdev.tvepg.epg.misc.EPGDataImpl;
import se.kmdev.tvepg.epg.misc.MockDataServiceOneDay;
import se.kmdev.tvepg.epg.model.InfoChannel;


public class MainActivityOneDay extends ActionBarActivity {

    private EPGoneDay epgOneDay;
    private InfoChannel[] channels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_one_day);

        epgOneDay = (EPGoneDay) findViewById(R.id.epg);
        epgOneDay.setEPGClickListener(new EPGClickListener() {
            @Override
            public void onChannelClicked(int channelPosition, EPGChannel epgChannel) {
                Toast.makeText(MainActivityOneDay.this, epgChannel.getName() + " clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEventClicked(int channelPosition, int programPosition, EPGEvent epgEvent) {
                Toast.makeText(MainActivityOneDay.this, epgEvent.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResetButtonClicked() {

            }
        });

        getData();
    }

    private void getData() {
        EPGCaller.get(ServerLink.GET_EPG_API, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (response != null) {
                    Gson gson = new Gson();
                    channels = gson.fromJson(response.toString(), InfoChannel[].class);
                    setContent();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // showError(ErrorTag.LOAD_DATA_ERROR);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // showErrorWrongInput();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                // showError(ErrorTag.LOAD_DATA_ERROR);
            }
        });
    }

    private void setContent() {
        // Do initial load of data.
        new AsyncLoadEPGData(epgOneDay, channels).execute();
    }

    @Override
    protected void onDestroy() {
        if (epgOneDay != null) {
            epgOneDay.clearEPGImageCache();
        }
        super.onDestroy();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class AsyncLoadEPGData extends AsyncTask<Void, Void, EPGData> {

        EPGoneDay epg;
        InfoChannel[] channels;

        public AsyncLoadEPGData(EPGoneDay epg, InfoChannel[] channels) {
            this.epg = epg;
            this.channels = channels;
        }

        @Override
        protected EPGData doInBackground(Void... voids) {
            return new EPGDataImpl(MockDataServiceOneDay.getMockData(channels));
        }

        @Override
        protected void onPostExecute(EPGData epgData) {
            epg.setEPGData(epgData);
            epg.recalculateAndRedraw();
        }
    }
}
