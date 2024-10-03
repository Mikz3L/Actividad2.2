package com.example.actividad22;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView sensorDataText;
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private SensorEventListener sensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar componentes de la interfaz
        Button buttonDownload = findViewById(R.id.buttonDownload);
        imageView = findViewById(R.id.imageView);
        sensorDataText = findViewById(R.id.sensorDataText);

        // Implementar el código para el botón onClick usando un Thread
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = loadImageFromNetwork("https://www.gratistodo.com/wp-content/uploads/2016/08/hojas-oto%C3%B1ales-fondo-de-pantalla-hd.jpg");
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                if (bitmap != null) {
                                    imageView.setImageBitmap(bitmap);
                                } else {
                                    // Mostrar mensaje de error si la imagen no se descarga
                                    Toast.makeText(MainActivity.this, "Error al descargar la imagen", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        // Inicializar SensorManager y el sensor de rotación
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Inicializar y registrar el listener del sensor
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                    // Mostrar datos del sensor en el TextView
                    sensorDataText.setText("X: " + event.values[0] +
                            "\nY: " + event.values[1] +
                            "\nZ: " + event.values[2]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // No es necesario en este caso
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registrar el listener del sensor
        if (rotationSensor != null) {
            sensorManager.registerListener(sensorEventListener, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistrar el listener del sensor cuando la actividad está en pausa
        sensorManager.unregisterListener(sensorEventListener);
    }

    // Método para cargar la imagen desde la red
    private Bitmap loadImageFromNetwork(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Devolver null en caso de error
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrar el listener del sensor cuando la actividad se destruya
        sensorManager.unregisterListener(sensorEventListener);
    }
}
