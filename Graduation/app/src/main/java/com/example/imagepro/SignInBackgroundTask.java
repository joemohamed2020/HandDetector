package com.example.imagepro;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.function.Consumer;

class SignInBackgroundTask extends AsyncTask<String, Void, String> {

    private Context context;
    private Runnable onEmailDoesNotExist;
    private Runnable onIncorrectPassword;
    private Runnable onLoginSuccess;
    private Consumer<String> onTaskFailed;

    public SignInBackgroundTask(Context ctx, Runnable onEmailDoesNotExist, Runnable onIncorrectPassword,
                                Runnable onLoginSuccess, Consumer<String> onTaskFailed) {
        context = ctx;
        this.onEmailDoesNotExist = onEmailDoesNotExist;
        this.onIncorrectPassword = onIncorrectPassword;
        this.onLoginSuccess = onLoginSuccess;
        this.onTaskFailed = onTaskFailed;
    }


    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String loginUrl = "http://192.168.1.7:80/login.php";


        if (type.equals("login")) {
            try {
                String email = params[1];
                String password = params[2];

                URL url = new URL(loginUrl);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream os = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
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
            } catch (MalformedURLException e) {

            } catch (IOException e) {

            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null) {
            if (s.equals("email does not exist")) {
                // Email does not exist
                if (onEmailDoesNotExist != null) {
                    onEmailDoesNotExist.run();
                }
            } else if (s.equals("incorrect password")) {
                // Incorrect password
                if (onIncorrectPassword != null) {
                    onIncorrectPassword.run();
                }
            } else if (s.equals("login successfully")) {
                // Login successful
                if (onLoginSuccess != null) {
                    onLoginSuccess.run();
                }
            } else {
                // Handle other scenarios or errors
                if (onTaskFailed != null) {
                    onTaskFailed.accept(s);
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