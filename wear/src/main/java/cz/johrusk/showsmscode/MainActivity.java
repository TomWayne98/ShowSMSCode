    package cz.johrusk.showsmscode;

    import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

    /**
     * Main application activity.
     *
     * @author Josef Hruska (pepa.hruska@gmail.com)
     */


    public class MainActivity extends Activity  {


    private TextView mTextView;
    static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();

    }

        public void onEvent(Bundle bundle){
            Log.d(TAG,"MainActivity");
            String[] sArray  = bundle.getStringArray("key");
             mTextView.setText(sArray[0]);
        }

    }

