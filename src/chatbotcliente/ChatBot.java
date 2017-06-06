package chatbotcliente;

import controller.ChatBotController;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Rafael Rutsatz
 */
public class ChatBot extends Application {

	ChatBotController controller;

	public static ChatBot aplicacao;

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket client;

	private String message = "";

	@Override
	public void start(Stage stage) throws Exception {

		aplicacao = this;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChatBotLayout.fxml"));

		Parent root = loader.load();

		controller = loader.getController();

		Scene scene = new Scene(root);
		stage.setTitle("Chat Bot");

		stage.setScene(scene);
		stage.show();

		controller.taEntrada.requestFocus();

	}

	public void rodarCliente() {
		try {
			conectarComServidor();
			getStreams();
			processaConexao(); // Processa as mensagens recebidas
		} catch (EOFException eOFException) {
			exibirMensagem("Cliente encerrou a conexão.");
		} catch (IOException iOException) {
			controller.isConnectedToServer.set(false);
			iOException.printStackTrace();
		} finally {
			fecharConexao();
		}
	}

	private void conectarComServidor() throws IOException {
		exibirMensagem("Tentando conectar");

		String host = controller.txServidor.getText();
		int porta = Integer.parseInt(controller.txPorta.getText());

		// cria o Socket para fazer a conexão com o servidor.
		client = new Socket(InetAddress.getByName(host), porta);

		exibirMensagem("\nConectado com: " + client.getInetAddress().getHostName()+"\n");
		Platform.runLater(() -> {
			controller.isConnectedToServer.set(true);
			controller.taEntrada.requestFocus();
		});

	}

	private void getStreams() throws IOException {

		output = new ObjectOutputStream(client.getOutputStream());
		output.flush();

		input = new ObjectInputStream(client.getInputStream());

	}

	private void processaConexao() throws IOException {

		do {
			try {
				message = (String) input.readObject();
				exibirMensagem("\nServidor>>> " + message);
				
			}catch (SocketException socketException) {
				Platform.runLater(() -> {
					controller.taSaida.setText("\nDesconectado do servidor.");
				});
			} 
			catch (ClassNotFoundException classNotFoundException) {
				exibirMensagem("Tipo de objeto desconhecido recebido.");
			}
		} while (controller.isConnectedToServer.get());

	}

	public void fecharConexao() {
		
		try {
//			enviarDados("encerrar");
			output.close();
			input.close();
			client.close();
		} catch (IOException iOException) {
			iOException.printStackTrace();
		}

		exibirMensagem("\nConexão encerrada");
		controller.isConnectedToServer.set(false);
	}

	public void enviarDados(String message) {
		try {
			output.writeObject(message);
			output.flush();
			exibirMensagem("\nVocê>>>" + message);
		} catch (IOException iOException) {
			exibirMensagem("Erro escrevendo objeto");
		}
	}

	private void exibirMensagem(final String message) {
		Platform.runLater(() -> {
			controller.taSaida.appendText(message);
		});
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
