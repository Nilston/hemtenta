package com.example.anton.hemtenta;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager manager;
    private Sensor compassMagSensor;
    private Sensor compassAccSensor;
    private Sensor stepSensor;
    private Sensor stepDetSensor;
    private Sensor barometerSensor;
    private TextView stepsTV;
    private TextView stepsDetTV;
    private TextView compassTV;
    private TextView barometerTV;
    private Button pressureButton;
    private EditText pressureInput;
    private float[] accelArr = new float[3];
    private float[] magnetArr = new float[3];
    private boolean accelerometerSet = false;
    private boolean magnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float degrees = 0f;
    private float pressurelevel = 0f;
    private float s = 0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        compassMagSensor = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        compassAccSensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepSensor = manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        barometerSensor = manager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        stepDetSensor = manager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        setContentView(R.layout.activity_main);
        initGui();
    }

    private void initGui() {
        stepsTV = (TextView) findViewById(R.id.stepTv);
        stepsDetTV = (TextView)findViewById(R.id.appstartStep);
        compassTV = (TextView) findViewById(R.id.compassTv);
        barometerTV = (TextView) findViewById(R.id.barometerTv);
        pressureButton = (Button) findViewById(R.id.addPressureButton);
        pressureInput = (EditText) findViewById(R.id.pressureEt);

        pressureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String press = pressureInput.getText().toString();
                if(!press.equals("")){
                    pressurelevel = (float) Integer.parseInt(press);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        manager.registerListener(this, compassMagSensor, SensorManager.SENSOR_DELAY_UI);
        manager.registerListener(this, compassAccSensor, SensorManager.SENSOR_DELAY_UI);
        manager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(this, stepDetSensor, SensorManager.SENSOR_DELAY_UI);
        manager.registerListener(this, barometerSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        manager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if(sensor == stepSensor){
            int steps = Math.round(event.values[0]);
            stepsTV.setText("Stegräknare: " + String.valueOf(steps));
        }else if(sensor == stepDetSensor){
            s++;
            stepsDetTV.setText("Stegräknare(sedan appstart): " + Math.round(s));
        }
        else if (sensor == compassMagSensor) {
            System.arraycopy(event.values, 0, magnetArr, 0, event.values.length);
            magnetometerSet = true;
        } else if (sensor == compassAccSensor) {
            System.arraycopy(event.values, 0, accelArr, 0, event.values.length);
            accelerometerSet = true;
        } else if(sensor == barometerSensor){
            float pressure_value = event.values[0];
            if(pressurelevel == 0f){
                barometerTV.setText("Höjd(över havet): Fyll i trycket vid havsytan");
            }else {
                float height = SensorManager.getAltitude(pressurelevel, pressure_value);
                barometerTV.setText("Höjd(över havet): " + Math.round(height) + "m");
            }
        }
        if (accelerometerSet && magnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, accelArr, magnetArr);
            SensorManager.getOrientation(mR, mOrientation);
            float radians = mOrientation[0];
            float convertTo360 = (float)(Math.toDegrees(radians)+360)%360;
            degrees = -convertTo360;
            degrees = Math.abs(degrees);
            compassTV.setText("Kompassriktning: " + Math.round(degrees)+"°");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
