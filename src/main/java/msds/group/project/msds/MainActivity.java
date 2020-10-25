/**
 * The MainActivity class is used to represent the initial
 * login page for the MSDS Android app.
 *
 * @author  J.Woodhouse, D.B.Dawson, I.J.Atienza, M.J.T.Makunda
 * @version 1.13
 */

package msds.group.project.msds;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
{
    private EditText editText_Username;
    private EditText editText_Password;
    private RequestQueue volleyQueue;
    private SharedVariables sharedVariables;
    private Logger logger;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_Username = findViewById(R.id.editText3);
        editText_Password = findViewById(R.id.editText4);

        volleyQueue = Volley.newRequestQueue(this);

        sharedVariables = SharedVariables.getInstance();

    }

    /**
     * This method is executed when the login button is pressed.
     * It creates a JSON object consisting of the following inputs:
     *
     * username - from editText_Username
     * password - from editText_Password
     * authToken - created during execution using the android randomUUID function
     * timestamp - the current timestamp in UNIX epoch time format
     *
     * The above JSON object is sent in a POST request to the postURL URL.
     *
     * The return from the POST request is a JSON object with a single field:
     *
     * loginSuccessful : boolean.
     *
     * Upon receiving a TRUE loginSuccessful response, the following is executed:
     *
     * the Token field of sharedVariables is set to the authToken.
     * The Username field of sharedVariables is set to the Username.
     * the SessionTimeStamp field of sharedVariables is set to the timestamp.
     *
     * The above is done to allow other App pages to easily access the data,
     * for later use.
     *
     * The App screen is then transferred to the Home Page.
     *
     * @param view
     */
    public void sendLoginRequest(View view)
    {
        final String uniqueID = UUID.randomUUID().toString();
        final Long uTimeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        HashMap<String, String> postData = new HashMap<>();
        postData.put("username", editText_Username.getText().toString());
        postData.put("password", editText_Password.getText().toString());
        postData.put("authToken", uniqueID);
        postData.put("timestamp", String.valueOf(uTimeStamp));

        String postURL = "https://msdsdb.000webhostapp.com/android_webservice/Login.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postURL, new JSONObject(postData), new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    Boolean loginSuccessful = response.getBoolean("loginSuccessful");

                    if(loginSuccessful)
                    {
                            showToast("Login Successful!");

                            sharedVariables.setToken(uniqueID);
                            sharedVariables.setUsername(editText_Username.getText().toString());
                            sharedVariables.setSessionTimeStamp(uTimeStamp);

                            logger = new Logger(volleyQueue);
                            logger.sendLog("Logged In");

                            Intent intent = new Intent(MainActivity.this, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                    }
                    else
                    {
                        showToast("Invalid Credentials");
                    }
                }
                catch (JSONException ex)
                {
                    showToast("JSON Exception: " + ex.getMessage());
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                showToast("Volley error: " + error.toString());
            }
        });

        //Make sure user has input data before continuing.
        if(isEmpty(editText_Username) || isEmpty(editText_Password))
        {
            showToast("Please provide a Username and Password");
            return;
        }

        hideKeyboard();
        volleyQueue.add(request);
    }

    /**
     * Method used to validate that a text input field is not empty.
     * @param editText pass the editText object to be checked.
     * @return returns a boolean that identifies whether or not the editText object is empty or not.
     */
    private boolean isEmpty(EditText editText)
    {
        if(editText.getText().toString().trim().length() > 0)
        {
            return false;
        }
        return true;
    }

    /**
     * Method used to hide the android keyboard.
     */
    private void hideKeyboard()
    {
        //Hide onscreen keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
