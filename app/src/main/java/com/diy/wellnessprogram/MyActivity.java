package com.diy.wellnessprogram;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class MyActivity extends Activity {

    private String TAG = "PGGURU";
    private static String strMessage, strMessages = "Starting...";
    Button b;
    TextView tv, tvMessages;
    TabHost tabHost;
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter adapter;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        tv = (TextView) findViewById(R.id.tv_result);
        b = (Button) findViewById(R.id.butSubmit);
        setContentView(R.layout.activity_my);
        tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec spec1=tabHost.newTabSpec("Tab 1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Welcome");
        TabHost.TabSpec spec2=tabHost.newTabSpec("Tab 2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Messages");
        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        TextView tv1 = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tv1.setTextColor(Color.parseColor("#ffffff"));
        TextView tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        tv2.setTextColor(Color.parseColor("#ffffff"));
        tabHost.setCurrentTab(1);
        adapter = new ArrayAdapter(
                this,
                R.layout.list_item,
                R.id.item_text,
                list
        );
        listview = (ListView) findViewById(R.id.lvMessages);
        listview.setAdapter(adapter);
        final SwipeDetector sd = new SwipeDetector();
        listview.setOnTouchListener(sd);
        ListView.OnItemClickListener oicl = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
               Log.i(TAG, "COUCOU WAS HERE : "+sd.getAction());
               if(sd.swipeDetected()) {
                    if(sd.getAction() == SwipeDetector.Action.LR) {
                        Log.i(TAG, "Action:" +"DELETE");
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }
            };
        };
        listview.setOnItemClickListener(oicl);
        ListView.OnHoverListener ole = new ListView.OnHoverListener() {
            @Override
            public boolean onHover(View view, MotionEvent motionEvent) {
                Log.i(TAG, "COUCOU WAS HERE TOO : " + listview.getHeight());
                return false;
            }
        };
        listview.setOnHoverListener(ole);
        //OnClickListener ocl
        /*
        OnClickListener ocl = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sd.swipeDetected()) {
                    if (sd.getAction() == SwipeDetector.Action.TB) {
                        Log.i(TAG, "Action:" +"UPDATE");
                    }
                }
            }
        };
        //LinearLayout ll = (LinearLayout) findViewById(R.id.tab2);
        listview.setOnClickListener(ocl);
        //ll.setOnClickListener(ocl);
        */
        /*
        OPTION 1
        listview.setOnTouchListener(
            new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    //return gestureDetector.onTouchEvent(event);
                    Log.i(TAG, "X:"+(int) event.getX()+", Y:"+(int) event.getY());
                    Log.i(TAG, "Pos:" + listview.pointToPosition((int) event.getX(), (int) event.getY()));
                    Log.i(TAG, "Action:" +event.getAction());
                    return true;
                }
            }
        );
        OPTION 2
        listview.setOnTouchListener(swipeDetector);
        listview.setOnItemClickListener(listener);
        OnItemClickListener listener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if(swipeDetector.swipeDetected()) {
                    if(swipeDetector.getAction() == Action.RL) {
                    } else {
                    }
                }
            };
        }
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMessages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void butSubmitPressed(View view) {
        AsyncCallUpdateDataWS task = new AsyncCallUpdateDataWS();
        SeekBar sb = (SeekBar) findViewById(R.id.seekBarGeneral);
        int i = sb.getProgress();
        task.setParameter(""+i);
        task.execute();
        Log.i(TAG, "butSubmitPressed");
    }

    private void getMessages() {
        AsyncCallGetMessagesWS task = new AsyncCallGetMessagesWS();
        task.setParameter("12345");
        task.execute();
        Log.i(TAG, "getMessages called");
    }

    public void callGetMessagesWS(String strMemberId) {
        if (isNetworkAvailable()) {
            try {
                /*
                String NAMESPACE = "http://healthprogram.diy.com/";
                String METHOD_NAME = "getMessages";
                String URL = "http://10.0.2.2:8080/WellnessProgramRegistration/services/ProcessAndroidData";
                */
                String NAMESPACE = "http://healthprogram.diy.com/";
                String METHOD_NAME = "getMessagesByRecipient";
                String URL = "http://10.0.2.2:8080/xDBHelper/services/xDBHelper";
                String SOAP_ACTION = NAMESPACE + METHOD_NAME;
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                PropertyInfo piMemberId = new PropertyInfo();
                piMemberId.setName("strRecipientId");
                piMemberId.setValue(strMemberId);
                piMemberId.setType(String.class);
                request.addProperty(piMemberId);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.bodyIn;
                strMessage = "";
                int count = response.getPropertyCount();
                for (int i = 0; i < count; ++i) {
                    //strMessage += "\n"+response.getProperty(i).toString();
                    ///TextView tv2 = new TextView();
                    //tv2.setShadowLayer(1, 1, 1, Integer.parseInt("#000000"));
                    list.add(response.getProperty(i).toString());
                }
            } catch (SoapFault sf) {
                strMessage = "there was an error talking to the server\n(" + sf.faultstring + ")";
            } catch (IOException e) {
                strMessage = "there was an error talking to the server\n(" + e.getMessage() + ")";
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        else {
            strMessage = "No Connectivity";
        }
    }

    private class AsyncCallGetMessagesWS extends AsyncTask<String, Void, Void> {

        String strMemberId = null;

        public void setParameter(String strParam) {
            this.strMemberId = strParam;
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            callGetMessagesWS(strMemberId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            TextView tvMessages = (TextView) findViewById(R.id.tvMessages);
            tvMessages.setText(strMessage);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute: Contacting Server to get messages...");
            TextView tvMessages = (TextView) findViewById(R.id.tvMessages);
            tvMessages.setText("Trying to retrieve your messages...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
            Log.i(TAG, "v.length: "+values.length);
        }

    }

    private class AsyncCallUpdateDataWS extends AsyncTask<String, Void, Void> {

        String strParameter ="JOHN";

        public void setParameter(String strParam) {
            this.strParameter = strParam;
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");
            callWS(strParameter);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            TextView tv = (TextView) findViewById(R.id.tv_result);
            tv.setText("onPostExecute:\n" + strMessage);
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            TextView tv = (TextView) findViewById(R.id.tv_result);
            tv.setText("onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
           Log.i(TAG, "onProgressUpdate");
        }
    }

    public void callWS(String str) {
        if (isNetworkAvailable()) {
            try {
                String NAMESPACE = "http://healthprogram.diy.com/";
                String METHOD_NAME = "sendUpdatedData";
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                PropertyInfo strPI = new PropertyInfo();
                strPI.setName("str");
                strPI.setValue(str);
                strPI.setType(String.class);
                request.addProperty(strPI);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                //envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                //String URL = "http://xpression45.iigfrance.com/WellnessProgramRegistration/services/ProcessAndroidData";
                //IP to be used to refer to emulator host: 10.0.2.2
                String URL = "http://10.0.2.2:8080/WellnessProgramRegistration/services/ProcessAndroidData";
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                String SOAP_ACTION = "http://healthprogram.diy.com/getResult";
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                strMessage = response.toString();
            } catch (SoapFault sf) {
                //sf.printStackTrace();
                strMessage = "there was an error talking to the server\n(" + sf.faultstring + ")";
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        } else {
            strMessage = "No Connectivity";
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
            Context.CONNECTIVITY_SERVICE
        );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
