package itsmy.com.br.listacontatossdm.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import itsmy.com.br.listacontatossdm.R;
import itsmy.com.br.listacontatossdm.model.Contato;

public class ContatoActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nomeEditText;
    private EditText enderecoEditText;
    private EditText telefoneEditText;
    private EditText emailEditText;
    private Button cancelarButton;
    private Button salvarButton;
    private int flag;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contato);

        this.nomeEditText = findViewById(R.id.nomeEditText);
        this.enderecoEditText = findViewById(R.id.enderecoEditText);
        this.telefoneEditText = findViewById(R.id.telefoneEditText);
        this.emailEditText = findViewById(R.id.emailEditText);
        this.cancelarButton = findViewById(R.id.cancelarButton);
        this.salvarButton = findViewById(R.id.salvarButton);

        this.cancelarButton.setOnClickListener(this);
        this.salvarButton.setOnClickListener(this);

        dadosLayoutContato();

    }

    private void dadosLayoutContato() {

        Contato contato = (Contato) getIntent().getSerializableExtra(ListaContatosActivity.CONTATO_EXTRA);
        this.flag = getIntent().getIntExtra(ListaContatosActivity.FLAG, 0);
        this.position = getIntent().getIntExtra(ListaContatosActivity.POSITION, 0);

        String subtitulo = "";

        switch (this.flag) {
            case ListaContatosActivity.MOSTRAR_CONTATO:
                if (contato != null) {
                    subtitulo = "Detalhes do Contato";
                    modoDetalhes(contato);
                } else {
                    subtitulo = "Novo Contato";
                }
                break;
            case ListaContatosActivity.EDITAR_CONTATO:
                subtitulo = "Editar Contato";
                editarContatoDetalhes(contato);
                break;
        }

        getSupportActionBar().setSubtitle(subtitulo);

    }

    private void editarContatoDetalhes(Contato contato) {

        this.nomeEditText.setText(contato.getNome());
        this.enderecoEditText.setText(contato.getEndereco());
        this.telefoneEditText.setText(contato.getTelefone());
        this.emailEditText.setText(contato.getEmail());

    }

    private void modoDetalhes(Contato contato) {

        this.nomeEditText.setText(contato.getNome());
        this.nomeEditText.setEnabled(false);
        this.enderecoEditText.setText(contato.getEndereco());
        this.enderecoEditText.setEnabled(false);
        this.telefoneEditText.setText(contato.getTelefone());
        this.telefoneEditText.setEnabled(false);
        this.emailEditText.setText(contato.getEmail());
        this.emailEditText.setEnabled(false);
        this.salvarButton.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelarButton:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.salvarButton:
                setResult(RESULT_OK, createNewIntent());
                finish();
                break;
        }
    }

    private Intent createNewIntent() {
        Contato contato = new Contato(this.nomeEditText.getText().toString(),
                this.enderecoEditText.getText().toString(),
                this.telefoneEditText.getText().toString(),
                this.emailEditText.getText().toString());
        Intent resultadoIntent = new Intent();
        resultadoIntent.putExtra(ListaContatosActivity.CONTATO_EXTRA, contato);
        if (this.flag == ListaContatosActivity.EDITAR_CONTATO){
            resultadoIntent.putExtra(ListaContatosActivity.POSITION, this.position);
        }
        return resultadoIntent;
    }

}
