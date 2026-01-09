package com.dedigo.pmdm06_juego;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btJugar;
    TextView txtRecord;
    Toast toast;

    int record = 0;
    // Récord inicial

    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    activityResult -> {

                        int result = activityResult.getResultCode();
                        Intent data = activityResult.getData();

                        if (result == RESULT_OK && data != null) {
                            record = data.getIntExtra("record", 0);
                            txtRecord.setText("" + record);
                        }
                    }
            );
    // Sustituye a startActivityForResult (API moderna)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtRecord = findViewById(R.id.txtRecord);
        txtRecord.setText("" + record);

        btJugar = findViewById(R.id.btnJugar);

        btJugar.setOnClickListener(v -> {
            Toast.makeText(this,
                    "¡A jugar!",
                    Toast.LENGTH_SHORT).show();
            jugar();
        });
    }

    public void jugar() {
        // Lanza la actividad del juego
        Intent gameActivity = new Intent(this, GameActivity.class);
        gameActivity.putExtra("record", record);
        activityResultLauncher.launch(gameActivity);
    }
}
