/**
 * The Inventory Page is used to display the current user's
 * available inventory.
 *
 * Also allows interaction to request more equipment.
 *
 * @author  J.Woodhouse, D.B.Dawson, I.J.Atienza, M.J.T.Makunda
 * @version 1.10
 */

package msds.group.project.msds;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Inventory_Page extends AppCompatActivity
    {
        private RequestQueue volleyQueue;
        private SharedVariables sharedVariables;
        private String username;
        private String authToken;

        private Logger logger;

        /**
         * This method is executed upon creation of an instance of this class. This occurs anytime the class is loaded
         * through an activity change.
         *
         * It creates a JSON object consisting of the following inputs:
         *
         * username - retrieved using the getter method from the sharedVariables instance.
         * authToken - retrieved using the getter method from the sharedVariables instance.
         *
         * The above JSON object is sent in a POST request to the postURL URL.
         *
         * The returned JSON object has 3 fields:
         *
         * success : boolean
         * itemName : array
         * quantity : array
         *
         * An ArrayList of custom objects is created, which holds the values of itemName and quantity.
         *
         * A two row table is then created, which is populated with the data held within the inventory ArrayList.
         *
         * An onClick listener is attached to each table row, which allows for intuitive transitioning to the Inventory_Page_Requests class.
         *
         * @param savedInstanceState
         */
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_inventory_page);

            volleyQueue = Volley.newRequestQueue(this);
            sharedVariables = SharedVariables.getInstance();
            username = sharedVariables.getUsername();
            authToken = sharedVariables.getToken();

            logger = new Logger(volleyQueue);

            HashMap<String, String> postData = new HashMap<>();
            postData.put("username", username);
            postData.put("authToken", authToken);

            String postURL = "https://msdsdb.000webhostapp.com/android_webservice/GetInventory.php";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postURL, new JSONObject(postData), new Response.Listener<JSONObject>()
            {
                @Override
                public void onResponse(JSONObject response)
                {
                    try
                    {
                        Boolean getInventorySuccess = response.getBoolean("success");

                        if(getInventorySuccess)
                        {
                            JSONArray itemNames = response.getJSONArray("itemName");
                            JSONArray quantities = response.getJSONArray("quantity");

                            ArrayList<InventoryItem> inventory = new ArrayList<>();
                            String inventoryToString = "";

                            for(int i = 0; i < itemNames.length(); i++)
                            {
                                inventory.add(new InventoryItem(itemNames.getString(i), quantities.getString(i)));
                            }

                            ScrollView scrollView = new ScrollView(Inventory_Page.this);
                            TableLayout inventoryTable = new TableLayout(Inventory_Page.this);

                            inventoryTable.setStretchAllColumns(true);
                            inventoryTable.setShrinkAllColumns(true);

                            //Title
                            TableRow titleRow = new TableRow(Inventory_Page.this);
                            TextView titleName = new TextView(Inventory_Page.this);
                            TextView titleQuantity = new TextView(Inventory_Page.this);

                            titleName.setTypeface(Typeface.DEFAULT_BOLD);
                            titleName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                            titleName.setPadding(20, 20, 20, 20);
                            titleName.setText("Name");

                            titleQuantity.setTypeface(Typeface.DEFAULT_BOLD);
                            titleQuantity.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                            titleQuantity.setPadding(20, 20, 20, 20);
                            titleQuantity.setText("Quantity");

                            titleRow.addView(titleName);
                            titleRow.addView(titleQuantity);

                            inventoryTable.addView(titleRow);

                            //Body
                            for(InventoryItem item : inventory)
                            {
                                TableRow tableRow = new TableRow(Inventory_Page.this);
                                tableRow.setClickable(true);

                                tableRow.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View view) {
                                        TableRow tablerow = (TableRow) view;
                                        TextView tv = (TextView) tablerow.getChildAt(0);
                                        String itemName = tv.getText().toString();

                                        Intent intent = new Intent(Inventory_Page.this, Inventory_Page_Requests.class);
                                        intent.putExtra("ItemName",itemName);
                                        startActivity(intent);
                                    }
                                });

                                TextView tvName = new TextView(Inventory_Page.this);
                                TextView tvQuantity = new TextView(Inventory_Page.this);

                                tvName.setPadding(20, 20, 20, 20);
                                tvName.setText(item.getItemName());


                                tvQuantity.setPadding(20, 20, 20, 20);
                                tvQuantity.setText(item.getQuantity());

                                tableRow.addView(tvName);
                                tableRow.addView(tvQuantity);

                                inventoryTable.addView(tableRow);

                                View rowStyling = new View(Inventory_Page.this);
                                rowStyling.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
                                rowStyling.setBackgroundColor(Color.rgb(51, 51, 51));
                                inventoryTable.addView(rowStyling);

                                inventoryToString = inventoryToString + item.getItemName() + ":" + item.getQuantity() + " ";
                            }

                                scrollView.addView(inventoryTable);
                                setContentView(scrollView);

                                logger.sendLog("Inventory page loaded with data: " + inventoryToString);
                        }
                        else
                        {
                            showToast("Failed to load inventory data");
                            logger.sendLog("Failed to load inventory data");

                            Intent intent = new Intent(Inventory_Page.this, MainActivity.class);
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

        /**
         * The InventoryItem class is used to make handling inventory objects
         * easier when used within data structures.
         */
        private class InventoryItem
        {
            private String itemName;
            private String quantity;

            public InventoryItem(String itemName, String quantity)
            {
                this.itemName = itemName;
                this.quantity = quantity;
            }

            /**
             * Getter method for the itemName variable
             * @return String of the itemName
             */
            public String getItemName()
            {
                return itemName;
            }

            /**
             * Getter method for the quantity variable
             * @return String of the quantity
             */
            public String getQuantity()
            {
                return quantity;
            }
        }
}
