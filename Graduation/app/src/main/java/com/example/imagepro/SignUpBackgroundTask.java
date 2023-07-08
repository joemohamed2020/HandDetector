package com.example.imagepro;


import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.function.Consumer;

class SignUpBackgroundTask extends AsyncTask<String, Void, String> {

    private final Runnable onEmailAlreadyExist;
    private final Runnable onSignupSuccess;

    private Context context;

    private Consumer<String> onTaskFailed;

    public SignUpBackgroundTask(Context ctx, Runnable onEmailAlreadyExist, Runnable onSignupSuccess,
                                Consumer<String> onTaskFailed) {
        context = ctx;
        this.onEmailAlreadyExist = onEmailAlreadyExist;
        this.onSignupSuccess = onSignupSuccess;

    }


    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String signupUrl = "http://192.168.1.7:80/signup.php";


        if (type.equals("Signup")) {
            try {
                String name = params[1];
                String email = params[2];
                String password = params[3];
                String confirm = params[4];

                URL url = new URL(signupUrl);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream os = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&"
                        + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&"
                        + URLEncoder.encode("confirm", "UTF-8") + "=" + URLEncoder.encode(confirm, "UTF-8");
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();

                InputStream is = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
                String result = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
                reader.close();
                is.close();
                http.disconnect();
                return result;
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }



    @Override
    protected void onPostExecute(String s) {
        if (s != null) {
            try {
                JSONObject responseJson = new JSONObject(s);
                boolean result = responseJson.optBoolean("result");
                String message = responseJson.optString("message");

                if (result) {
                    // Sign up successful
                    if (onSignupSuccess != null) {
                        onSignupSuccess.run();
                    }
                } else {
                    // Email already exists or other error
                    if (onEmailAlreadyExist != null) {
                        onEmailAlreadyExist.run();
                    } else {
                        // Handle other scenarios or errors
                        if (onTaskFailed != null) {
                            onTaskFailed.accept(message);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON parsing error
                if (onTaskFailed != null) {
                    onTaskFailed.accept("Error: JSON parsing failed");
                }
            }
        } else {
            // Handle error case when response is null
            if (onTaskFailed != null) {
                onTaskFailed.accept("Error: Null response");
            }
        }
    }

}



