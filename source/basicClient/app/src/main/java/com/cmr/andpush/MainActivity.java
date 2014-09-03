package com.cmr.andpush;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.gcm.*;
import com.microsoft.windowsazure.messaging.*;
import com.microsoft.windowsazure.notifications.NotificationsManager;

public class MainActivity extends Activity {

    private final static String TAG = "MainActivity";
    private final static String SENDER_ID = "PROJECT-ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private Button btnRegisterWithGcm, btnRegisterWithNoTags, btnRegisterWithTags, btnRegisterWithTemplates;
        private TextView lblRegistration, lblStatus;
        private GoogleCloudMessaging mGcm;
        private String mRegistrationId;
        private NotificationHub mHub;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            btnRegisterWithGcm = (Button) rootView.findViewById(R.id.btnRegisterWithGcm);
            btnRegisterWithGcm.setOnClickListener(registerWithGcmListener);
            btnRegisterWithNoTags = (Button) rootView.findViewById(R.id.btnRegisterWithNoTags);
            btnRegisterWithNoTags.setOnClickListener(registerWithNoTags);
            btnRegisterWithTags = (Button) rootView.findViewById(R.id.btnRegisterWithTags);
            btnRegisterWithTags.setOnClickListener(registerWithTags);
            btnRegisterWithTemplates = (Button) rootView.findViewById(R.id.btnRegisterWithTemplates);
            btnRegisterWithTemplates.setOnClickListener(registerWithTemplates);
            lblRegistration = (TextView) rootView.findViewById(R.id.lblRegistrationId);
            lblStatus = (TextView) rootView.findViewById(R.id.lblStatus);

            mGcm = GoogleCloudMessaging.getInstance(getActivity());
            String connectionString = "NotificationHubListenSharedAccessSignature";
            mHub = new NotificationHub("NotificationHubName", connectionString, getActivity());
            NotificationsManager.handleNotifications(getActivity(), SENDER_ID, MyHandler.class);

            return rootView;
        }

        private OnClickListener registerWithGcmListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Tapped");
                registerWithGcm();
            }
        };

        @SuppressWarnings("unchecked")
        private void registerWithGcm() {
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object... params) {
                    try {
                        mRegistrationId = mGcm.register(SENDER_ID);
                        Log.i(TAG, "Registered with id: " + mRegistrationId);

                    } catch (Exception e) {
                        return e;
                    }
                    return null;
                }

                protected void onPostExecute(Object result) {
                    lblRegistration.setText(mRegistrationId);
                    lblStatus.setText(getResources().getString(R.string.status_registered));
                };
            }.execute(null, null, null);
        }

        private OnClickListener registerWithNoTags = new OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Tapped register with no tags");
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        try {
                            mHub.register(mRegistrationId);
                        } catch (Exception e) {
                            Log.e(TAG, "Issue registering with hub: " + e.getMessage());
                            return e;
                        }
                        return null;
                    }

                    protected void onPostExecute(Object result) {
                        lblStatus.setText(getResources().getString(R.string.status_registered_with_no_tags));
                    };
                }.execute(null, null, null);
            }
        };

        private OnClickListener registerWithTags = new OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Tapped register with tags");
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        try {
                            mHub.register(mRegistrationId, "MyTag", "AndroidUser", "AllUsers");
                        } catch (Exception e) {
                            Log.e(TAG, "Issue registering with hub with tag: " + e.getMessage());
                            return e;
                        }
                        return null;
                    }
                    protected void onPostExecute(Object result) {
                        lblStatus.setText(getResources().getString(R.string.status_registered_with_tags));
                    };
                }.execute(null, null, null);
            }
        };

        private OnClickListener registerWithTemplates = new OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Tapped register with templates");
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        try {
                            mHub.registerTemplate(mRegistrationId, "messageTemplate",
                                    "{\"data\":{\"msg\":\"$(message)\"}, \"collapse_key\":\"$(collapse_key)\"}",
                                    "MyTag", "AllUsers", "AndroidUser");
                        } catch (Exception e) {
                            Log.e(TAG, "Issue registering with hub with template: " + e.getMessage());
                            return e;
                        }
                        return null;
                    }

                    protected void onPostExecute(Object result) {
                        lblStatus.setText(getResources().getString(R.string.status_registered_with_templates));

                    };
                }.execute(null, null, null);
            }
        };
    }
}
