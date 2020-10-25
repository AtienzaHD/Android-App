/**
 * The Account Page class is used to present a user's account details.
 *
 * @author  J.Woodhouse, D.B.Dawson, I.J.Atienza, M.J.T.Makunda
 * @version 1.07
 */

package msds.group.project.msds;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;

public class Account_Page extends AppCompatActivity
{
    private RequestQueue volleyQueue;
    private SharedVariables sharedVariables;
    private String username;
    private String authToken;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvDOB;
    private TextView tvRank;
    private TextView tvEmail;
    private TextView tvAddress;

    private Logger logger;

    /**
     * This method is executed upon loading of the Account Page.
     * It creates a JSON object consisting of the following inputs:
     *
     * username - retrieved using the getter method from the sharedVariables instance.
     * authToken - retrieved using the getter method from the sharedVariables instance.
     *
     * The above JSON object is sent in a POST request to the postURL URL.
     *
     * The return from the POST request is a JSON object with 7 fields:
     *
     * loginSuccessful : boolean.
     * firstName : string.
     * lastName : string.
     * gender : string.
     * DOB : string.
     * rank : string.
     * contact : string.
     * address : string.
     *
     * Upon receiving a TRUE loginSuccessful response, the following is executed:
     *
     * tvName, tvGender, tvDOB, tvRank, tvEmail, tvAddress are set to the above received values.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account__page);

        volleyQueue = Volley.newRequestQueue(this);
        sharedVariables = SharedVariables.getInstance();
        username = sharedVariables.getUsername();
        authToken = sharedVariables.getToken();

        tvName = findViewById(R.id.tvName);
        tvGender = findViewById(R.id.tvGender);
        tvDOB = findViewById(R.id.tvDOB);
        tvRank = findViewById(R.id.tvRank);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);

        logger = new Logger(volleyQueue);

        HashMap<String, String> postData = new HashMap<>();
        postData.put("username", username);
        postData.put("authToken", authToken);

        String postURL = "https://msdsdb.000webhostapp.com/android_webservice/GetUserInfo.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postURL, new JSONObject(postData), new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    Boolean getPersonnelSuccess = response.getBoolean("success");

                    if(getPersonnelSuccess)
                    {
                        tvName.setText(response.getString("firstName") + " " + response.getString("lastName"));
                        tvGender.setText(response.getString("gender"));
                        tvDOB.setText(response.getString("DOB"));
                        tvRank.setText(response.getString("rank"));
                        tvEmail.setText(response.getString("contact"));
                        tvAddress.setText(response.getString("address"));

                        logger.sendLog("Account page loaded with data: " + response.getString("firstName") + " " + response.getString("lastName")
                        + " " + response.getString("gender") + " " + response.getString("DOB") + " " + response.getString("rank") + " " + response.getString("contact")
                        + " " + response.getString("address"));
                    }
                    else
                    {
                        showToast("Failed to retrieve account data");
                        logger.sendLog("Failed to retrieve account data");
                        Intent intent = new Intent(Account_Page.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                catch (JSONException ex)
                {
                    showToast("JSON Exception: " + ex.getMessage());
                    logger.sendLog("JSON Exception encountered: " + ex.getMessage());

                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                showToast("Volley error: " + error.toString());
                logger.sendLog("Volley Error encountered: " + error.toString());
            }
        });

        volleyQueue.add(request);
    }

    /**
     * Method used to show an android style Toast notification.
     * @param text text parameter, this is what will be shown in the Toast notification.
     */
    private void showToast(String text)
    {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
