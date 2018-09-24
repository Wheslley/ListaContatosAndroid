package itsmy.com.br.listacontatossdm.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import itsmy.com.br.listacontatossdm.R;
import itsmy.com.br.listacontatossdm.adapter.ListaContatosAdapter;
import itsmy.com.br.listacontatossdm.model.Contato;
import itsmy.com.br.listacontatossdm.util.ArmazenamentoHelper;
import itsmy.com.br.listacontatossdm.util.Configuracoes;
import itsmy.com.br.listacontatossdm.util.JsonHelper;

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
    private SharedPreferences sharedPreferences;
    private final String CONFIGURACOES_SHARED_PREFERENCES = "CONFIGURACOES";
    private final String TIPO_ARMAZENAMENTO_SHARED_PREFERENCES = "TIPO_ARMAZENAMENTO";

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

        sharedPreferences = getSharedPreferences(CONFIGURACOES_SHARED_PREFERENCES, MODE_PRIVATE);
        restauraConfiguracoes();
        restauraContatos();
    }

    private void restauraContatos() {
        JSONArray jsonArray = null;
        try{
            jsonArray = ArmazenamentoHelper.buscarContatos(this, Configuracoes.getInstance().getTipoArmazenamento());
            if(jsonArray != null){
                List<Contato> contatosSalvosList = JsonHelper.jsonArrayParaListaContatos(jsonArray);
                if(contatosSalvosList.size() > 0){
                    this.listaContatos.addAll(contatosSalvosList);
                    this.listaContatosAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    private void restauraConfiguracoes() {
        Configuracoes.getInstance().setTipoArmazenamento(this.sharedPreferences.getInt(TIPO_ARMAZENAMENTO_SHARED_PREFERENCES, Configuracoes.ARMAZENAMENTO_INTERNO));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        salvaConfiguracoes();
        salvaContatos();
    }

    private void salvaContatos() {
        JSONArray jsonArray = null;
        try{
            jsonArray = JsonHelper.listaContatosParaJsonArray(listaContatos);
            if(jsonArray != null){
                ArmazenamentoHelper.salvarContatos(this, Configuracoes.getInstance().getTipoArmazenamento(), jsonArray);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void salvaConfiguracoes() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putInt(TIPO_ARMAZENAMENTO_SHARED_PREFERENCES, Configuracoes.getInstance().getTipoArmazenamento());
        editor.commit();
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
                Intent configuracaçoesIntent = new Intent(this, ConfiguracoesActivity.class);
                startActivity(configuracaçoesIntent);
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
                ligarContato(contato.getTelefone());
                return true;
            case R.id.verEnderecoMenuItem:
                localizarContato(contato.getEndereco());
                return true;
            case R.id.enviarEmailContatoMenuItem:
                enviarEmailContato(contato.getEmail());
                return true;
            case R.id.removerContatoMenuItem:
                removerContato(infoMenu.position);
                return true;
        }

        return false;
    }

    private void ligarContato(String telefone){
        Uri telefoneUri = Uri.parse("tel:" + telefone);
        Intent ligarContatoIntent = new Intent(Intent.ACTION_DIAL, telefoneUri);
        startActivity(ligarContatoIntent);
    }

    private void localizarContato(String endereco){
        Uri enderecoUri = Uri.parse("geo:0,0?q=" + endereco);
        Intent localizarContatoIntent = new Intent(Intent.ACTION_VIEW, enderecoUri);
        startActivity(localizarContatoIntent);
    }

    private void enviarEmailContato(String email){
        Uri emailUri = Uri.parse("mailto:" + email);
        Intent emailContatoIntent = new Intent(Intent.ACTION_SENDTO, emailUri);
        startActivity(emailContatoIntent);
    }

    private void removerContato(final int posicao) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true).setMessage("Deseja remover o contato?").setPositiveButton("Remover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listaContatos.remove(posicao);
                listaContatosAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog removeAlertDialog = builder.create();
        removeAlertDialog.show();
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
