/**
 * The Logger class handles the sending of logs to be stored in a Database.
 *
 * @author  J.Woodhouse, D.B.Dawson, I.J.Atienza, M.J.T.Makunda
 * @version 1.04
 */


package msds.group.project.msds;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

public class Logger extends Application
{
    private SharedVariables sharedVariables;
    private String username;
    private String authToken;
    private RequestQueue volleyQueue;

    public Logger(RequestQueue volleyQueue)
    {
        sharedVariables = SharedVariables.getInstance();
        username = sharedVariables.getUsername();
        authToken = sharedVariables.getToken();
        this.volleyQueue = volleyQueue;
    }

    /**
     * This function is used to send a POST request containing the log data to be
     * inserted into the Database.
     * @param activityDescription String of meaningful text to describe the logged event.
     */
    public void sendLog(String activityDescription)
    {
        HashMap<String, String> postData = new HashMap<>();
        postData.put("username", username);
        postData.put("authToken", authToken);
        postData.put("activityDescription", activityDescription);

        String postURL = "https://msdsdb.000webhostapp.com/android_webservice/SubmitLog.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postURL, new JSONObject(postData), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });

        volleyQueue.add(request);
    }
}
