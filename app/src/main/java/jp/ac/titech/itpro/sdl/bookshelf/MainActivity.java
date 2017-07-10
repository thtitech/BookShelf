package jp.ac.titech.itpro.sdl.bookshelf;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String KEY = "resultList";
    private ArrayAdapter<String> resultAdapter;
    private ArrayList<String> resultList;
    private boolean activeFlag = true;
    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultList = new ArrayList<String>();
        //set button
        b = (Button) findViewById(R.id.start_button);
        b.setOnClickListener(this);
        //set list view
        ListView resultView = (ListView)findViewById(R.id.result_list);
        resultAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        resultView.setAdapter(resultAdapter);
        if ((savedInstanceState != null) && (savedInstanceState.get(KEY) != null)){
            resultAdapter.addAll(savedInstanceState.getStringArrayList(KEY));
            resultList.addAll(savedInstanceState.getStringArrayList(KEY));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY, resultList);
    }

    @Override
    public void onClick(View v){
        if (v != null) {
            switch (v.getId()) {
                case R.id.start_button:
                    //Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
                    if (activeFlag) {
                        activeFlag = false;
                        startReader();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void startReader(){
        Log.d("Info", "start reader");
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String content = intentResult.getContents();
            Log.d("Debug", content);
            if (content != null) {
                new GetInformationTask().execute(content.trim());
            }else{
                Log.d("Debug", "content is null");
            }
        }else{
            setContentView(R.layout.activity_main);
        }
    }

    private class GetInformationTask extends AsyncTask <String, Void, List<String> >{

        @Override
        protected List<String> doInBackground(String... params) {
            String isbn = params[0];
            APIConnector connector = new APIConnector();
            List<String> attributes = new ArrayList<String>();
            attributes.add(APIConnector.AUTHOR_TAG);
            attributes.add(APIConnector.TITLE_TAG);
            attributes.add(APIConnector.PUBLISHER_TAG);
            attributes.add(APIConnector.SUBJECT_TAG);
            List<String> informationMap = connector.getAttributes(attributes, isbn);
            return informationMap;
        }

        @Override
        public void onPostExecute(List<String> informationMap){
            resultList.clear();
            resultList.addAll(informationMap);
            resultAdapter.clear();
            resultAdapter.addAll(informationMap);
            activeFlag = true;
        }
    }
}
