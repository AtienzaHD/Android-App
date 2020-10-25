/**
 * The Inventory Page Requests page is used to allow a user to request new
 * items to be placed into their inventory.
 *
 * @author  J.Woodhouse, D.B.Dawson, I.J.Atienza, M.J.T.Makunda
 * @version 1.03
 */

package msds.group.project.msds;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
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

public class Inventory_Page_Requests extends AppCompatActivity {

    private NumberPicker numPicker;
    private Button pickerButton;
    private TextView tvItemName;
    private String itemName;
    private int quantity;
    private RequestQueue volleyQueue;
    private SharedVariables sharedVariables;
    private String username;
    private String authToken;

    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory__page__requests);

        volleyQueue = Volley.newRequestQueue(this);
        sharedVariables = SharedVariables.getInstance();
        username = sharedVariables.getUsername();
        authToken = sharedVariables.getToken();

        numPicker = findViewById(R.id.numpicker);
        tvItemName = findViewById(R.id.tvItemName);
        pickerButton = findViewById(R.id.confButton);

        numPicker.setMinValue(1);
        numPicker.setMaxValue(10);

        /**
         * getExtras() is used here to carry forward data from the previously active class.
         */
        itemName = getIntent().getExtras().getString("ItemName");
        tvItemName.setText("Request Item: " + itemName);

        quantity = 0;

        logger = new Logger(volleyQueue);
        logger.sendLog("Inventory requests page loaded with data: " + itemName);
    }

    /**
     * This method is executed upon clicking the Confirmation button within the GUI.
     *
     * It creates a JSON object consisting of the following inputs:
     *
     * username - retrieved using the getter method from the sharedVariables instance.
     * authToken - retrieved using the getter method from the sharedVariables instance.
     * itemName - the name of the current item being requested.
     * quantity - the amount of the current item that the user wants to request.
     *
     * The above JSON object is sent in a POST request to the postURL URL.
     *
     * The returned JSON object has 1 field:
     *
     * success : boolean
     *
     * On success, the user will be returned back to their Inventory page.
     *
     * @param view
     */
    public void InventoryUpdateRequest(View view)
    {
        quantity = numPicker.getValue();

        HashMap<String, String> postData = new HashMap<>();
        postData.put("username", username);
        postData.put("authToken", authToken);
        postData.put("itemName", itemName);
        postData.put("quantity", Integer.toString(quantity));

        String postURL = "https://msdsdb.000webhostapp.com/android_webservice/NewRequest.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postURL, new JSONObject(postData), new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    Boolean newRequestSuccess = response.getBoolean("success");

                    if(newRequestSuccess)
                    {
                        showToast("Request Sent Successfully");
                        logger.sendLog("Inventory update request created with data: " + itemName + ":" + Integer.toString(quantity));

                        Intent intent = new Intent(Inventory_Page_Requests.this, Inventory_Page.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else
                    {
                        showToast("Error submitting request");
                        logger.sendLog("Failed to submit inventory update request");
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
