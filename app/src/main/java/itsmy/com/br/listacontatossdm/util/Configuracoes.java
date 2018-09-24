package itsmy.com.br.listacontatossdm.util;

public class Configuracoes {

    public static final int ARMAZENAMENTO_INTERNO = 0;
    public static final int ARMAZENAMENTO_EXTERNO = 1;
    public static final int ARMAZENAMENTO_BANCO_DADOS = 2;

    private int tipoArmazenamento;

    private static final Configuracoes ourInstance = new Configuracoes();

    public static Configuracoes getInstance() {
        return ourInstance;
    }

    private Configuracoes() {
        this.tipoArmazenamento = this.ARMAZENAMENTO_INTERNO;
    }

    public int getTipoArmazenamento() {
        return tipoArmazenamento;
    }

    public void setTipoArmazenamento(int tipoArmazenamento) {
        this.tipoArmazenamento = tipoArmazenamento;
    }

}
