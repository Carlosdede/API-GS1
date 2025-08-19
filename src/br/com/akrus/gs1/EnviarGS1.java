package br.com.akrus.gs1;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EnviarGS1 implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao contexto) throws Exception {
        // 1 - Autenticar na GS1 e obter token
        LoginGS1.Login login = new LoginGS1.Login(
                "ae14b2eb-37ac-4e82-9d83-445176ff5027",
                "640cf746-712b-4b21-99c7-883dd1f26998",
                "ti@safrasulsementes.com.br",
                "Sup@2852"
        );
        login.autenticar();

        String accessToken = LoginGS1.Login.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            throw new Exception("Falha ao obter Access Token da GS1");
        }

        // 2 - Montar JSON do produto
        ProdutoGS1 produtoGS1 = new ProdutoGS1();
        String jsonProduto = produtoGS1.montarJsonProduto(contexto);

        // 3 - Enviar para API da GS1
        URL url = new URL("https://api.gs1br.org/gs1/v2/products");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        // Headers exigidos pela GS1
        con.setRequestProperty("client_id", "ae14b2eb-37ac-4e82-9d83-445176ff5027");
        con.setRequestProperty("access_token", accessToken);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        // Corpo da requisição
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonProduto.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Captura resposta
        int status = con.getResponseCode();
        System.out.println("Status HTTP: " + status);

        BufferedReader reader;
        if (status >= 200 && status < 300) {
            reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
        } else {
            reader = new BufferedReader(new InputStreamReader(con.getErrorStream(), "utf-8"));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        System.out.println("Resposta da GS1: " + response.toString());


    }
}
