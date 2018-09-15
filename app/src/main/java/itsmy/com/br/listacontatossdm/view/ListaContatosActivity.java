package itsmy.com.br.listacontatossdm.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import itsmy.com.br.listacontatossdm.R;
import itsmy.com.br.listacontatossdm.adapter.ListaContatosAdapter;
import itsmy.com.br.listacontatossdm.model.Contato;

public class ListaContatosActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final int NOVO_CONTATO_REQUEST_CODE = 0;
    private final int EDIT_CONTATO_REQUEST_CODE = 1;

    public static final int MOSTRAR_CONTATO = 0;
    public static final int EDITAR_CONTATO = 1;

    public static final String CONTATO_EXTRA = "CONTATO_EXTRA";
    public static final String FLAG = "FLAG";
    public static final String POSITION = "POSITION";

    private ListView listaContatosListView;

    private List<Contato> listaContatos;
    private ListaContatosAdapter listaContatosAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contatos);

        this.listaContatosListView = findViewById(R.id.listaContatosListView);
        this.listaContatos = new ArrayList<>();

        //preencheListaContatos();

        /*List<String> listaNomes = new ArrayList<>();
        for(Contato contato : listaContatos) listaNomes.add(contato.getNome());*/

        //ArrayAdapter<String> listaContatosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaNomes);
        listaContatosAdapter = new ListaContatosAdapter(this, this.listaContatos);
        this.listaContatosListView.setAdapter(this.listaContatosAdapter);

        registerForContextMenu(this.listaContatosListView);
        this.listaContatosListView.setOnItemClickListener(this);
    }

    public void preencheListaContatos() {
        for (int i = 0; i < 20; i++) {
            listaContatos.add(new Contato("C" + i, "ifsp", "1234", "i@ifsp"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.configuracaoMenuItem:
                return true;
            case R.id.novoContatoMenuItem:
                //Intent novoContatoIntent = new Intent(this, ContatoActivity.class);
                Intent novoContatoIntent = new Intent("NOVO_CONTATO_ACTION");
                startActivityForResult(novoContatoIntent, this.NOVO_CONTATO_REQUEST_CODE);
                return true;
            case R.id.sairMenuItem:
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case NOVO_CONTATO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    addNovoContatoToListaContatos(data);
                } else {
                    if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "Cadastro cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case EDIT_CONTATO_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    editContatoToListaContatos(data);
                } else {
                    if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(this, "Edicao cancelada", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void editContatoToListaContatos(Intent data) {
        Contato editContato = (Contato) data.getSerializableExtra(this.CONTATO_EXTRA);
        int positon = data.getIntExtra(POSITION, 0);
        if (editContato != null) {
            this.listaContatos.set(positon, editContato);
            this.listaContatosAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Contato editado", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNovoContatoToListaContatos(@Nullable Intent data) {
        // recupero contato, atualizo lista e notifico adapter
        Contato novoContato = (Contato) data.getSerializableExtra(this.CONTATO_EXTRA);
        if (novoContato != null) {
            this.listaContatos.add(novoContato);
            this.listaContatosAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Novo contato adicionado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_contexto, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo infoMenu = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Contato contato = listaContatos.get(infoMenu.position);

        switch (item.getItemId()) {
            case R.id.editarContatoMenuItem:
                detalhesContatoOpen("NOVO_CONTATO_ACTION", CONTATO_EXTRA, EDITAR_CONTATO, infoMenu.position, contato);
                return true;
            case R.id.ligarContatoMenuItem:
                return true;
            case R.id.verEnderecoMenuItem:
                return true;
            case R.id.enviarEmailContatoMenuItem:
                return true;
            case R.id.removerContatoMenuItem:
                return true;
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Contato contato = listaContatos.get(position);
        detalhesContatoOpen("NOVO_CONTATO_ACTION", CONTATO_EXTRA, MOSTRAR_CONTATO, position, contato);
    }

    private void detalhesContatoOpen(String action, String tag, int flag, int position, Contato contato) {
        Intent detalhesContatoIntent = new Intent(action);
        detalhesContatoIntent.putExtra(tag, contato);
        detalhesContatoIntent.putExtra(FLAG, flag);
        detalhesContatoIntent.putExtra(POSITION, position);
        switch (flag) {
            case MOSTRAR_CONTATO:
                startActivity(detalhesContatoIntent);
                break;
            case EDITAR_CONTATO:
                startActivityForResult(detalhesContatoIntent, this.EDIT_CONTATO_REQUEST_CODE);
                break;
        }

    }
}
