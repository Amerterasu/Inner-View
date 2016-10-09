/*
 * Copyright (c) Microsoft. All rights reserved.
 * Licensed under the MIT license.
 * //
 * Project Oxford: http://ProjectOxford.ai
 * //
 * ProjectOxford SDK GitHub:
 * https://github.com/Microsoft/ProjectOxford-ClientSDK
 * //
 * Copyright (c) Microsoft Corporation
 * All rights reserved.
 * //
 * MIT License:
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * //
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * //
 * THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package khoa.p.le.innerview;

import android.app.Activity;
import android.app.AlertDialog;
import java.net.URI;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.microsoft.bing.speech.SpeechClientStatus;
import com.microsoft.cognitiveservices.speechrecognition.DataRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionResult;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionStatus;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.StringEntityHC4;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import java.net.URI;
import java.util.jar.Attributes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
//import khoa.p.le.innerview.R;


public class MainActivity extends Activity implements ISpeechRecognitionServerEvents
{
    int m_waitSeconds = 0;
    DataRecognitionClient dataClient = null;
    MicrophoneRecognitionClient micClient = null;
    FinalResponseStatus isReceivedResponse = FinalResponseStatus.NotReceived;
    TextView questionTextView;
    Button _startButton;
    SpeechRecognitionMode MODE = SpeechRecognitionMode.ShortPhrase;
    public enum FinalResponseStatus { NotReceived, OK, Timeout }
    Intent gotoResults;
    SurfaceView mSurfaceView;
    /**
     * Gets the primary subscription key
     */
    public String getPrimaryKey() {
        return this.getString(R.string.subscription_key);
    }

    /**
     * Gets the LUIS application identifier.
     * @return The LUIS application identifier.
     */
    private String getLuisAppId() {
        return this.getString(R.string.luisAppID);
    }

    /**
     * Gets the LUIS subscription identifier.
     * @return The LUIS subscription identifier.
     */
    private String getLuisSubscriptionID() {
        return this.getString(R.string.luisSubscriptionID);
    }

    /**
     * Gets a value indicating whether or not to use the microphone.
     * @return true if [use microphone]; otherwise, false.
     */


    /**
     * Gets the default locale.
     * @return The default locale.
     */
    private String getDefaultLocale() {
        return "en-us";
    }

    /**
     * Gets the short wave file path.
     * @return The short wave file.
     */
    private String getShortWaveFile() {
        return "whatstheweatherlike.wav";
    }

    /**
     * Gets the long wave file path.
     * @return The long wave file.
     */
    private String getLongWaveFile() {
        return "batman.wav";
    }
    static final int REQUEST_VIDEO_CAPTURE = 1;

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gotoResults=new Intent(getApplicationContext(), ResultsActivity.class);
        setContentView(R.layout.activity_main);
        this._startButton = (Button) findViewById(R.id.button1);
        this.questionTextView=(TextView) findViewById(R.id.question_textView);
        // setup the buttons
        final MainActivity This = this;
        this._startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                This.StartButton_Click(arg0);
            }
        });


    }

    /**
     * Handles the Click event of the _startButton control.
     */
    private void StartButton_Click(View arg0) {
        this._startButton.setEnabled(false);
        this.m_waitSeconds =  200;
        if (this.micClient == null) {
                this.micClient =
                        SpeechRecognitionServiceFactory.createMicrophoneClientWithIntent(
                                this,
                                this.getDefaultLocale(),
                                this,
                                this.getPrimaryKey(),
                                this.getLuisAppId(),
                                this.getLuisSubscriptionID());
            }
            else
            {
                this.micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(
                        this,
                        MODE,
                        this.getDefaultLocale(),
                        this,
                        this.getPrimaryKey());
            }


        this.micClient.startMicAndRecognition();


            if (null == this.dataClient) {
                this.dataClient = SpeechRecognitionServiceFactory.createDataClient(
                        this,
                        MODE,
                        this.getDefaultLocale(),
                        this,
                        this.getPrimaryKey());

            }

            this.SendAudioHelper((MODE == SpeechRecognitionMode.ShortPhrase) ? this.getShortWaveFile() : this.getLongWaveFile());
    }


    private void SendAudioHelper(String filename) {
        RecognitionTask doDataReco = new RecognitionTask(this.dataClient, MODE, filename);
        try
        {
            doDataReco.execute().get(m_waitSeconds, TimeUnit.SECONDS);
        }
        catch (Exception e)
        {
            doDataReco.cancel(true);
            isReceivedResponse = FinalResponseStatus.Timeout;
        }
    }
    class FillerWord{
        private String word;
        private int count;
        public FillerWord(String word, int count){
            this.word = word;
            this.count=count;
        }
        public String getWord(){
            return word;
        }
        public int getCount(){
            return count;
        }
        public int inc(){
            return ++count;
        }
    }
    private void findFillers(String answer){
        String nonoWords[]=new String[]{"like", "so", "yeah"};
        FillerWord fillers[]= new FillerWord[3];

        for(int i = 0; i < 3; i++){
            FillerWord newWord = new FillerWord(nonoWords[i], 0);
            fillers[i]=newWord;
        }

        for(String word: answer.split(" ")){
            for(int i = 0; i < 3; i++){
                if(word.equals(fillers[i].getWord())){
                    System.out.println(fillers[i].inc());
                }
            }
        }

        for (FillerWord fillerword: fillers){
            gotoResults.putExtra(fillerword.getWord(), fillerword.getCount());
        }

    }
    public void onFinalResponseReceived(final RecognitionResult response) {
        String finalResponse="";
        boolean isFinalDicationMessage = MODE == SpeechRecognitionMode.LongDictation &&
                (response.RecognitionStatus == RecognitionStatus.EndOfDictation ||
                        response.RecognitionStatus == RecognitionStatus.DictationEndSilenceTimeout);
        if (null != this.micClient  && ((MODE == SpeechRecognitionMode.ShortPhrase) || isFinalDicationMessage)) {
            // we got the final result, so it we can end the mic reco.  No need to do this
            // for dataReco, since we already called endAudio() on it as soon as we were done
            // sending all the data.
            this.micClient.endMicAndRecognition();
            for(int i=0; i < response.Results.length; i++){
                finalResponse+=response.Results[i].DisplayText;
            }
            questionTextView.setText(finalResponse);
            findFillers(finalResponse);
            Topics thetopics = new Topics();
            thetopics.execute(finalResponse);
            JavaSample test = new JavaSample();
            test.execute(finalResponse);
        }

        if (isFinalDicationMessage) {
            this._startButton.setEnabled(true);
            this.isReceivedResponse = FinalResponseStatus.OK;
        }


    }

    @Override
    public void onIntentReceived(String s) {

    }


    public void onPartialResponseReceived(final String response) {
        Log.v("PARTIALRESPONSE", "--- Partial result received by onPartialResponseReceived() ---");
    }

    public void onError(final int errorCode, final String response) {
        this._startButton.setEnabled(true);
        Log.e("ERROR", "--- Error received by onError() ---");
        Log.e("ERROR", "Error code: " + SpeechClientStatus.fromInt(errorCode) + " " + errorCode);
        Log.e("ERROR", "Error text: " + response);
    }

    /**
     * Called when the microphone status has changed.
     * @param recording The current recording state
     */
    public void onAudioEvent(boolean recording) {
        Log.v("START","--- Microphone status change received by onAudioEvent() ---");
        Log.v("START", "********* Microphone status: " + recording + " *********");
        if (recording) {
            questionTextView.setText("Please start speaking.");
        }

        if (!recording) {
            this.micClient.endMicAndRecognition();
            this._startButton.setEnabled(true);
        }
    }

    private class Topics extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpClient = HttpClients.createDefault();

            try{
                URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/keyPhrases");

                URI uri = builder.build();
                HttpPost request = new HttpPost(uri);
                request.setHeader("Content-Type", "application/json");
                request.setHeader("Ocp-Apim-Subscription-Key", getString(R.string.text_subscription_key));

                //Request body
                StringEntity se = null;
                JSONObject params = new JSONObject();
                JSONArray documents = new JSONArray();
                JSONObject userresponse = new JSONObject();
                userresponse.put("language", "en");
                userresponse.put("id", 1);
                userresponse.put("text", strings[0]);
                documents.put(userresponse);
                params.put("documents", documents);
                se = new StringEntity(params.toString(), "UTF-8");
                request.setEntity(se);

                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();

                String jsonStr = EntityUtils.toString(entity);

                JSONObject obj = new JSONObject(jsonStr);

                if(entity != null){
                    ArrayList<String> keyWords = new ArrayList<String>();
                    JSONArray jsonArray = obj.getJSONArray("documents").getJSONObject(0).getJSONArray("keyPhrases");
                    if(jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            keyWords.add(jsonArray.get(i).toString());
                            Log.v("LIST ITEM", keyWords.get(i));
                        }
                    }
                    gotoResults.putExtra("keywords", keyWords);
                }
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
            return null;
        }
    }

    private class JavaSample extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpclient = HttpClients.createDefault();

            try
            {
                URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment");

                URI uri = builder.build();
                HttpPost request = new HttpPost(uri);
                request.setHeader("Content-Type", "application/json");
                request.setHeader("Ocp-Apim-Subscription-Key", getString(R.string.text_subscription_key));
                request.setHeader("Accept", "application/json");


                // Request body
                StringEntity se = null;
                JSONObject params = new JSONObject();
                JSONArray documents = new JSONArray();
                JSONObject userresponse = new JSONObject();
                userresponse.put("language", "en");
                userresponse.put("id", 1);
                userresponse.put("text", strings[0]);
                documents.put(userresponse);
                params.put("documents", documents);
                se = new StringEntity(params.toString(), "UTF-8");
                request.setEntity(se);

                HttpResponse response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity);
                JSONObject data = new JSONObject(jsonStr);
                //String[] sentscore = gson.fromJson("documents", String[].class);
                if (entity != null)
                {
                    double score = data.getJSONArray("documents").getJSONObject(0).getDouble("score");
                   System.out.println("FINALLY SUCCESS"+ score);
                    gotoResults.putExtra("sentscore", score);
                    getApplicationContext().startActivity(gotoResults);
                }
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
            return null;
        }
    }

    /*
     * Speech recognition with data (for example from a file or audio source).
     * The data is broken up into buffers and each buffer is sent to the Speech Recognition Service.
     * No modification is done to the buffers, so the user can apply their
     * own VAD (Voice Activation Detection) or Silence Detection
     *
     * @param dataClient
     * @param recoMode
     * @param filename
     */
    private class RecognitionTask extends AsyncTask<Void, Void, Void> {
        DataRecognitionClient dataClient;
        SpeechRecognitionMode recoMode;
        String filename;

        RecognitionTask(DataRecognitionClient dataClient, SpeechRecognitionMode recoMode, String filename) {
            this.dataClient = dataClient;
            this.recoMode = recoMode;
            this.filename = filename;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Note for wave files, we can just send data from the file right to the server.
                // In the case you are not an audio file in wave format, and instead you have just
                // raw data (for example audio coming over bluetooth), then before sending up any
                // audio data, you must first send up an SpeechAudioFormat descriptor to describe
                // the layout and format of your raw audio data via DataRecognitionClient's sendAudioFormat() method.
                // String filename = recoMode == SpeechRecognitionMode.ShortPhrase ? "whatstheweatherlike.wav" : "batman.wav";
                InputStream fileStream = getAssets().open(filename);
                int bytesRead = 0;
                byte[] buffer = new byte[1024];

                do {
                    // Get  Audio data to send into byte buffer.
                    bytesRead = fileStream.read(buffer);

                    if (bytesRead > -1) {
                        // Send of audio data to service.
                        dataClient.sendAudio(buffer, bytesRead);
                    }
                } while (bytesRead > 0);

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            finally {
                dataClient.endAudio();
            }

            return null;
        }
    }
}

