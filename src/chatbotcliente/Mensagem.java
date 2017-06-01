package chatbotcliente;

/**
 *
 * @author Rafael Rutsatz
 */
public class Mensagem {

    private String comando;
    private ChatBot cliente;
    
    public Mensagem(String comando) {
        this.comando = comando;
        cliente = ChatBot.aplicacao;
    }
    
    public void enviar(){
        cliente.enviarDados(comando);
    }
}
