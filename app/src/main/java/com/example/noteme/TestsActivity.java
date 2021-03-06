package com.example.noteme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

class Pregunta {
    String pregunta;
    String respuesta1;
    String respuesta2;
    String relleno1;
    String relleno2;
    String tipo;

    public Pregunta(String pregunta, String respuesta1, String respuesta2, String relleno1, String relleno2, String tipo) {
        this.pregunta = pregunta;
        this.respuesta1 = respuesta1;
        this.respuesta2 = respuesta2;
        this.relleno1 = relleno1;
        this.relleno2 = relleno2;
        this.tipo = tipo;
    }
}

public class TestsActivity extends AppCompatActivity {

    Intent bintent;
    Button btnSiguiente;
    FirebaseFirestore db;
    String tema, subtema;
    TextView lblTema, lblPregunta, lblCounter;
    RadioButton lblResp1, lblResp2, lblResp3;
    CheckBox cbResp1, cbResp2, cbResp3;
    List<Pregunta> preguntas;
    RadioGroup checkGroup, radioGroup;
    // preguntas
    Integer currentQuestion = 1, puntos = 0;

    String tipo;
    String[] correctas;
    protected void showQuestion(Integer npreg) {
        Pregunta preg = preguntas.get(npreg - 1);
        lblPregunta.setText(preg.pregunta);

        tipo = preg.tipo;
        if (preg.tipo == null){
            preg.tipo = "";
        }

        int[] numerosAleatorios = IntStream.rangeClosed(0, 2).toArray();
        //desordenando los elementos
        Random r = new Random();
        for (int i = numerosAleatorios.length; i > 0; i--) {
            int posicion = r.nextInt(i);
            int tmp = numerosAleatorios[i-1];
            numerosAleatorios[i - 1] = numerosAleatorios[posicion];
            numerosAleatorios[posicion] = tmp;
        }

        String[] respustas;
        respustas = new String[3];
        correctas = new String[2];

        if (preg.tipo.equals("cb")){
            respustas[0] = preg.respuesta1;
            respustas[1] = preg.respuesta2;
            respustas[2] = preg.relleno1;

            correctas[0] = preg.respuesta1;
            correctas[1] = preg.respuesta2;

            radioGroup.setVisibility(View.INVISIBLE);
            checkGroup.setVisibility(View.VISIBLE);
        }
        else {
            respustas[0] = preg.respuesta1;
            respustas[1] = preg.relleno1;
            respustas[2] = preg.relleno2;

            correctas[0] = preg.respuesta1;

            radioGroup.setVisibility(View.VISIBLE);
            checkGroup.setVisibility(View.INVISIBLE);
        }

        lblResp1.setText(respustas[numerosAleatorios[0]]);
        lblResp2.setText(respustas[numerosAleatorios[1]]);
        lblResp3.setText(respustas[numerosAleatorios[2]]);

        cbResp1.setText(respustas[numerosAleatorios[0]]);
        cbResp2.setText(respustas[numerosAleatorios[1]]);
        cbResp3.setText(respustas[numerosAleatorios[2]]);

        lblResp1.setChecked(false);
        lblResp2.setChecked(false);
        lblResp3.setChecked(false);

        cbResp1.setChecked(false);
        cbResp2.setChecked(false);
        cbResp3.setChecked(false);

        lblCounter.setText(currentQuestion.toString() + "/" + Long.toString(preguntas.stream().count()));
    }

    protected int getQuestionValue() {
        if (tipo == null){
            tipo = "";
        }
        Log.d("---->", "tipo2:" + tipo);

        if (tipo.equals("cb")){
            if (cbResp1.isChecked() && cbResp2.isChecked() && !cbResp3.isChecked()){
                if ((cbResp1.getText() == correctas[0] || cbResp1.getText() == correctas[1]) && (cbResp2.getText() == correctas[0] || cbResp2.getText() == correctas[1])){
                    return 1;
                }
            }
            else if (cbResp1.isChecked() && cbResp3.isChecked() && !cbResp2.isChecked()){
                if ((cbResp1.getText() == correctas[0] || cbResp1.getText() == correctas[1]) && (cbResp3.getText() == correctas[0] || cbResp3.getText() == correctas[1])){
                    return 1;
                }
            }
            else if (cbResp2.isChecked() && cbResp3.isChecked() && !cbResp1.isChecked()){
                if ((cbResp2.getText() == correctas[0] || cbResp2.getText() == correctas[1]) && (cbResp3.getText() == correctas[0] || cbResp3.getText() == correctas[1])){
                    return 1;
                }
            }
        }
        else {
            if (lblResp1.isChecked()){
                if (lblResp1.getText() == correctas[0]){
                    return 1;
                }
            }
            else if (lblResp2.isChecked()){
                if (lblResp2.getText() == correctas[0]){
                    return 1;
                }
            }
            else {
                if (lblResp3.getText() == correctas[0]){
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tipo = "";

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Hide the action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        lblPregunta = findViewById(R.id.lblPregunta);
        lblResp1 = findViewById(R.id.lblRespuesta1);
        lblResp2 = findViewById(R.id.lblRespuesta2);
        lblResp3 = findViewById(R.id.lblRespuesta3);

        cbResp1 = findViewById(R.id.cbRespuesta1);
        cbResp2 = findViewById(R.id.cbRespuesta2);
        cbResp3 = findViewById(R.id.cbRespuesta3);

        lblCounter = findViewById(R.id.lblCounter);
        radioGroup = findViewById(R.id.radioGroup);
        checkGroup = findViewById(R.id.checkGroup);

        lblTema = findViewById(R.id.lblTema);
        preguntas = new ArrayList<Pregunta>();

        bintent = getIntent();
        tema = bintent.getStringExtra("tema");
        subtema = bintent.getStringExtra("subtema");
        Log.d("Tema", "onCreate: " + tema);
        Log.d("SubTema", "onCreate: " + subtema);
        lblTema.setText(subtema);

        db = FirebaseFirestore.getInstance();
        db.collection("preguntas")
                .whereEqualTo("tema", tema)
                .whereEqualTo("subtema", subtema)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc: task.getResult()) {
                                preguntas.add(new Pregunta(
                                            doc.getString("pregunta"),
                                            doc.getString("respuesta1"),
                                            doc.getString("respuesta2"),
                                            doc.getString("relleno1"),
                                            doc.getString("relleno2"),
                                            doc.getString("tipo")
                                        )
                                );

                            }
                            // Lo demas
                            showQuestion(currentQuestion);
                        }
                    }
                });

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                puntos += getQuestionValue();

                if (currentQuestion == preguntas.stream().count()) {
                    // ya termino -> dashboard
                    Intent i = new Intent(TestsActivity.this, ScoreActivity.class);
                    i.putExtra("score", puntos);
                    startActivity(i);
                } else {
                    currentQuestion += 1;
                    showQuestion(currentQuestion);
                }
            }
        });
    }}