package supersavvyme;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import baileyae.gimbal24example.R;


public class RegistrationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Button mRtnButton = (Button) findViewById(R.id.rtn);
        mRtnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        WebView myWebView = (WebView) findViewById(R.id.regist);
        myWebView.loadUrl("https://www.supersavvyme.co.uk/page/registration");
    }


}
