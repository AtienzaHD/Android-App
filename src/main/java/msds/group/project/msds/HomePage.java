/**
 * The HomePage class is used to display the home page of the App.
 * This is where the bulk of user navigation takes place.
 *
 * @author  J.Woodhouse, D.B.Dawson, I.J.Atienza, M.J.T.Makunda
 * @version 1.04
 */

package msds.group.project.msds;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.TimeUnit;


public class HomePage extends AppCompatActivity
{
    private Long currentTimeStamp;
    private Long tokenTimeStamp;

    private SharedVariables sharedVariables;
    private RequestQueue volleyQueue;

    private TextView tvSessionTimer;

    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        currentTimeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        sharedVariables = SharedVariables.getInstance();
        volleyQueue = Volley.newRequestQueue(this);
        tokenTimeStamp = sharedVariables.getSessionTimeStamp();
        tvSessionTimer = findViewById(R.id.tvSessionTimer);

        logger = new Logger(volleyQueue);
        logger.sendLog("Accessed home page");
        sessionTimeout();
    }

    /**
     * This function is executed when the Account button is pressed.
     * This function is only used to present the user with the Accounts Page.
     * @param view
     */
    public void showAccount(View view)
    {
        logger.sendLog("Accessed account page");
        Intent intent = new Intent(this, Account_Page.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * This function is executed when the Inventory button is pressed.
     * This function is only used to present the user with the Inventory Page.
     * @param view
     */
    public void showInventory(View view)
    {
        logger.sendLog("Accessed inventory page");
        Intent intent = new Intent(this, Inventory_Page.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * This function is used to automatically log the user out of the Application
     * after the session time limit is met.
     *
     * The current session length is set to 30 minutes.
     *
     * The remaining session time is calculated each time the user is returned to the
     * home page.
     */
    private void sessionTimeout()
    {
        Long timerValue = (tokenTimeStamp + 1800 - currentTimeStamp) * 1000;


        CountDownTimer sessionTimer = new CountDownTimer(timerValue, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            tvSessionTimer.setText("Session ends in: " + millisUntilFinished / 60000 + ":" + millisUntilFinished / 1000 % 60);

            }

            @Override
            public void onFinish() {
                showToast("Session Ended");
                logger.sendLog("Session timed out, Logged Out");
                Intent intent = new Intent(HomePage.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        sessionTimer.start();
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
