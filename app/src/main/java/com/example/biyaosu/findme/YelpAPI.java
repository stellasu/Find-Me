package com.example.biyaosu.findme;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import android.util.Log;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Code sample for accessing the Yelp API V2.
 *
 * This program demonstrates the capability of the Yelp API version 2.0 by using the Search API to
 * query for businesses by a search term and location, and the Business API to query additional
 * information about the top result from the search query.
 *
 * <p>
 * See <a href="http://www.yelp.com/developers/documentation">Yelp Documentation</a> for more info.
 *
 */
public class YelpAPI {

    private YelpAPI instance = null;

    private static final String API_HOST = "api.yelp.com";
    private static final String DEFAULT_TERM = "dinner";
    private static final String DEFAULT_LOCATION = "San Francisco, CA";
    private static final int SEARCH_LIMIT = 10;
    private static final String SEARCH_PATH = "/v2/search";

    /*
     * Update OAuth credentials below from the Yelp Developers API site:
     * http://www.yelp.com/developers/getting_started/api_access
     */
    private static final String CONSUMER_KEY = "eruy1w-2lpuctwHVveQvFw";
    private static final String CONSUMER_SECRET = "5Rans4971viCXgqD_xdhF7kmxyM";
    private static final String TOKEN = "mnPrx8ofCBUDwvNgK7v1vursTyDunqOU";
    private static final String TOKEN_SECRET = "OeLA773Ua0uKF7V1wktAhpO8vZ8";

    OAuthService service;
    Token accessToken;

    String classtag = YelpAPI.class.getName();

    /**
     * Setup the Yelp API OAuth credentials.
     */
    public YelpAPI()
    {
        this.service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(CONSUMER_KEY)
                        .apiSecret(CONSUMER_SECRET).build();
        this.accessToken = new Token(TOKEN, TOKEN_SECRET);
    }

    /**
     * Creates and sends a request to the Search API by term and location.
     * <p>
     * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
     * for more info.
     *
     * @param lat <tt>String</tt> of the latitude
     * @param lng <tt>String</tt> of the longitude
     * @return <tt>String</tt> JSON Response
     */
    public String searchForBusinessesByLocation(String lat, String lng)
    {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("ll", lat+","+lng);
        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
        return sendRequestAndGetResponse(request);
    }

    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param path API endpoint to be queried
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(String path)
    {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
        return request;
    }

    /**
     * Sends an {@link OAuthRequest} and returns the {@link Response} body.
     *
     * @param request {@link OAuthRequest} corresponding to the API request
     * @return <tt>String</tt> body of API response
     */
    private String sendRequestAndGetResponse(OAuthRequest request)
    {
        Log.i(classtag, "Querying " + request.getCompleteUrl() + " ...");
        this.service.signRequest(this.accessToken, request);
        Log.i(classtag, "request: "+request.toString());

        Response response = request.send();
        return response.getBody();
    }

    /**
     * Queries the Search API based on the command line arguments and takes the first result to query
     * the Business API.
     *
     * @param lat <tt>String</tt> of the latitude
     * @param lng <tt>String</tt> of the longitude
     */
    public ArrayList<HashMap<String, String>> queryAPI(String lat, String lng)
    {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        String searchResponseJSON =
                searchForBusinessesByLocation(lat, lng);
        Log.i(classtag, "searchResponseJson: "+searchResponseJSON);

        JSONObject response = null;
        try {
            response = new JSONObject(searchResponseJSON);
            JSONArray businesses = response.optJSONArray("businesses");
            int number = businesses.length();
            for(int i=0; i<number; i++){
                HashMap<String, String> map = new HashMap<>();
                JSONObject business = (JSONObject)businesses.opt(i);
                String name = business.optString("name");
                String url = business.optString("url");
                String snippet = business.optString("snippet_image_url");
                JSONArray categories = business.optJSONArray("categories");
                JSONObject location = business.optJSONObject("location");
                JSONObject coordinate = location.optJSONObject("coordinate");
                String businessLat = String.valueOf(coordinate.opt("latitude"));
                String businessLng = String.valueOf(coordinate.opt("longitude"));
                String businessCategories = "";
                if(categories != null){
                    for(int j=0; j<categories.length(); j++){
                        JSONArray categoryItem = categories.optJSONArray(j);
                        if(businessCategories == ""){
                            businessCategories = businessCategories+categoryItem.optString(0);
                        }else{
                            businessCategories = businessCategories+", "+categoryItem.optString(0);
                        }

                    }
                }
                map.put("name", name);
                map.put("url", url);
                map.put("snippet", snippet);
                map.put("businessLat", businessLat);
                map.put("businessLng", businessLng);
                map.put("businessCategories", businessCategories);
                list.add(map);
            }
        } catch (JSONException e) {
            System.out.println("Error: could not parse JSON response:");
            System.out.println(searchResponseJSON);
            System.exit(1);
        }

        return list;
    }


}
