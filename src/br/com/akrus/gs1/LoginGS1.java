package br.com.akrus.gs1;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;


import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Base64;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class LoginGS1 implements AcaoRotinaJava {

    // Variáveis para armazenar tokens
    private static String accessToken;
    private static String refreshToken;


    public void doAction(ContextoAcao contexto) throws Exception {

        // Objeto que faz o login e obtém os tokens
        Login login = new Login(
                "client_id",
                "client_secret",
                "ti@safrasulsementes.com.br",
                "senha"
        );

        login.autenticar();

        // Exibe o token obtido
        System.out.println("Access Token: " + accessToken);
        System.out.println("Refresh Token: " + refreshToken);


    }


    // Classe interna responsável pelo login
    static class Login {
        private String clientId;
        private String clientSecret;
        private String username;
        private String password;

        public Login(String clientId, String clientSecret, String username, String password) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.username = username;
            this.password = password;
        }

        public void autenticar() {
            try {
                URL url = new URL("https://api.gs1br.org/oauth/access-token");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");

                String auth = clientId + ":" + clientSecret;
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes("UTF-8"));
                con.setRequestProperty("Authorization", "Basic " + encodedAuth);
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);


                String jsonInputString = "{ \"grant_type\": \"password\", \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

                //obter a saida da requisição

                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                //Tratamento do Status

                int status = con.getResponseCode();
                System.out.println("Status HTTP: " + status);

                StringBuilder response = new StringBuilder();

                if (status >= 200 && status < 300) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                    }
                } else {
                    try (BufferedReader err = new BufferedReader(new InputStreamReader(con.getErrorStream(), "utf-8"))) {
                        String line;
                        while ((line = err.readLine()) != null) {
                            response.append(line);
                        }
                    }
                    System.out.println("Erro na requisição: " + response.toString());
                    return;
                }

                System.out.println("Resposta bruta: " + response.toString());

                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(response.toString());

                if (!element.isJsonObject()) {
                    System.out.println("Resposta não é um JSON válido");
                    return;
                }

                JsonObject jsonObject = element.getAsJsonObject();

                if (jsonObject.has("access_token") && jsonObject.has("refresh_token")) {
                    accessToken = jsonObject.get("access_token").getAsString();
                    refreshToken = jsonObject.get("refresh_token").getAsString();
                    System.out.println("Access Token obtido com sucesso");
                } else {
                    System.out.println("Tokens não encontrados na resposta");
                    System.out.println("Resposta: " + response.toString());
                }

            } catch (Exception e) {
                System.out.println("Exceção ao autenticar:");
                e.printStackTrace();
            }
        }
        public static String getAccessToken() {
            return accessToken;
        }
        public static String getRefreshToken() {
            return refreshToken;
        }

    }
}
