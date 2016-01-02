package com.ahmeddonkl.nbeticket;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchGetTicketData extends AsyncTask<String, Void, String>
    {

        private ProgressDialog dialog;

        public FetchGetTicketData(Select_Date activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute()
        {
            dialog.setMessage("Loading....");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s)
        {

            if (dialog.isShowing())
            {
                dialog.dismiss();
            }
        }


        @Override
        protected  String doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String GetTicketJsonStr = null;

        try {

            final String GET_TICKET_BASE_URL =
                    "http://192.168.30.1/TestWebAPI/api/queues/getTicket?";
            final String PAR_DATE = "VDate";
            final String PAR_BRANCH_ID = "Branch_id";

            //Url of json file no need to uri builder
            Uri builtUri = Uri.parse(GET_TICKET_BASE_URL).buildUpon()
                    .appendQueryParameter(PAR_DATE, params[0])
                    .appendQueryParameter(PAR_BRANCH_ID, params[1])
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d("link",builtUri.toString());
            // Create the request to url, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            GetTicketJsonStr = buffer.toString();

        } catch (IOException e) {
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                }
            }
        }

        try
        {
            return GetTicketResponseFromJson(GetTicketJsonStr);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

        //Json Parsing
        private String GetTicketResponseFromJson(String ticketJsonStr) throws JSONException
        {

            // These are the names of the JSON objects that need to be extracted.
            final String obj_Key = "Item1";
            final String obj_Value= "Item2";
            String final_result = "";
            JSONArray TicketJson = new JSONArray(ticketJsonStr);

            for(int i = 0; i < TicketJson.length(); i++)
            {
                //Get the JSON object
                JSONObject statuses_obj = TicketJson.getJSONObject(i);
                final_result = statuses_obj.getString(obj_Key);
            }

            return final_result;
        }


    }

