/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;

import chatbotcliente.ChatBot;
import chatbotcliente.Mensagem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * FXML Controller class
 *
 * @author Rafael Rutsatz
 */
public class ChatBotController implements Initializable {

	@FXML
	private Button btEnviar;

	@FXML
	private Button btConectar;

	@FXML
	private Button btDesconectar;

	@FXML
	public TextArea taEntrada;

	@FXML
	public TextArea taSaida;

	@FXML
	public TextField txPorta;

	@FXML
	public TextField txServidor;

	private Worker<String> worker;

	public BooleanProperty isConnectedToServer = new SimpleBooleanProperty(false);

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		/**
		 * Implementa Worker rodando por thread. (EstÃ¡ usando classe anÃ´nima
		 * para rodar uma Task.)
		 */
		worker = new Service<String>() {
			@Override
			protected Task<String> createTask() {
				return new Task<String>() {
					@Override
					protected String call() throws Exception {
						ChatBot.aplicacao.rodarCliente();
						return "iniciado";
					}
				};
			}

			@Override
			/**
			 * Função chamada ao usuário cancelar a Task.
			 */
			protected void cancelled() {
				ChatBot.aplicacao.fecharConexao();

				System.out.println("cancelou");
			}

		};

		// realiza os binds
		btConectar.disableProperty().bind(isConnectedToServer);
		btDesconectar.disableProperty().bind(isConnectedToServer.not());
		btEnviar.disableProperty().bind(isConnectedToServer.not());
		taEntrada.disableProperty().bind(isConnectedToServer.not());

	}

	@FXML
	private void conectar(ActionEvent e) {
		// Inicializa a execução da Thread.
		((Service) worker).restart();
	}

	@FXML
	private void desconectar(ActionEvent e) {
		// Cancela a execução da Thread.
		worker.cancel();
	}

	@FXML
	private void enviarMensagem(Event e) {
		// Tratamento para enviar a mensagem quando cliquei no botão
		// ou quando apertei Enter.
		if (e.getEventType() == KeyEvent.KEY_RELEASED) {
			KeyEvent ke = (KeyEvent) e;
			if (ke.getCode() == KeyCode.ENTER) {
				// remove o \n quando aperta enter
				Mensagem mensagem = new Mensagem(taEntrada.getText(0, taEntrada.getText().length() - 1));
				mensagem.enviar();
				taEntrada.clear();
			}
		} else if (e.getEventType() == ActionEvent.ACTION) {
			Mensagem mensagem = new Mensagem(taEntrada.getText());
			mensagem.enviar();
			taEntrada.clear();
		}

	}
}
