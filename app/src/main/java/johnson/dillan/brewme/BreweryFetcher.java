package johnson.dillan.brewme;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BreweryFetcher {

    private static final String TAG = "BreweryFetcher";

    public byte[] getUrlBytes( String urlSpec ) throws IOException {
      URL url = new URL(urlSpec);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();

      try {
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          InputStream in = connection.getInputStream();

          if ( connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
              throw new IOException( connection.getResponseMessage() + ": with " + urlSpec );
          }

          int bytesRead = 0;
          byte[] buffer = new byte[1024];

          while ( (bytesRead = in.read(buffer)) > 0 ){
              out.write( buffer, 0, bytesRead );
          }
          out.close();
          return out.toByteArray();
      } finally {
          connection.disconnect();
      }

    } // end of getURLBytes

    public String getUrlString( String urlSpec ) throws IOException {
        return new String( getUrlBytes(urlSpec) );
    }

    public BreweryLab fetchItems( String state ) {
        BreweryLab breweries = BreweryLab.get( );
        breweries.clearExisting();
        try {
            String url = Uri.parse("https://api.openbrewerydb.org/breweries?")
                    .buildUpon()
                    .appendQueryParameter("by_state", state )
                    .appendQueryParameter("per_page", "50")
                    .build().toString();
//            .toString();
//            Log.i(TAG," the string is: " + url );
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONArray jsonBody = new JSONArray(jsonString);
            parseItems( breweries, jsonBody );
        } catch ( IOException ioe ){
            Log.e( TAG, "Failed to fetch items", ioe );
        } catch ( JSONException je ){
            Log.e(TAG, "Failed to parse JSON", je );
        }
        return breweries;

    } // end of fetchItems()

    private void parseItems(BreweryLab breweries, JSONArray jsonBody ) throws IOException, JSONException {

        for ( int i = 0; i < jsonBody.length(); i++ ) {
            JSONObject breweryJsonObject = jsonBody.getJSONObject(i);

            BreweryItem brewery = new BreweryItem();
            brewery.setId( breweryJsonObject.getString("id") );
            brewery.setName( breweryJsonObject.getString("name") );
            brewery.setType( breweryJsonObject.getString("brewery_type") );
            brewery.setStreetAddress( breweryJsonObject.getString("street") );
            brewery.setCity( breweryJsonObject.getString("city") );
            brewery.setState( breweryJsonObject.getString("state") );
//            Log.d("LONG/LAT", "long, lat is: " + breweryJsonObject.getString("longitude")+ breweryJsonObject.getString("latitude") );
            if ( breweryJsonObject.getString("longitude") != null ){ brewery.setLongitude( breweryJsonObject.getString("longitude") ); }
            if ( breweryJsonObject.getString("latitude") != null ){ brewery.setLatitude( breweryJsonObject.getString("latitude") );}
            brewery.setPhone( breweryJsonObject.getString("phone") );
            brewery.setWebsite( breweryJsonObject.getString("website_url") );

            JSONArray tagsJsonArray = breweryJsonObject.getJSONArray("tag_list");
            for ( int tag = 0; tag < tagsJsonArray.length(); tag++ ){
                brewery.addTag( tagsJsonArray.getString( tag ) );
            }
            breweries.addBrewery(brewery);
        }


    }

} // end of brewery fetcher;
