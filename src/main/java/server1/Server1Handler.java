package server1;

import utils.KafkaUtils;
import utils.constants.KafkaConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Server1Handler {



    public void handle(Socket clientSocket) throws IOException, InterruptedException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String headerLine = in.readLine();

        String[] tokens = headerLine.split(" ");
        String method = tokens[0];
        String url = tokens[1];
        String version = tokens[2];

        System.out.println(method + " " + url + " " + version);

        String paramName = "";
        String paramValue = "";

        int questionMarkIndex = url.indexOf('?');
        if (questionMarkIndex != -1) {
            String queryString = url.substring(questionMarkIndex + 1);
            String[] queryParams = queryString.split("&");
            for (String queryParam : queryParams) {
                String[] paramTokens = queryParam.split("=");
                paramName = URLDecoder.decode(paramTokens[0], StandardCharsets.UTF_8);
                paramValue = URLDecoder.decode(paramTokens[1], StandardCharsets.UTF_8);
                System.out.println(paramName + " = " + paramValue);
            }
        }

        if (method.equalsIgnoreCase("GET")) {
            if (paramName.equalsIgnoreCase("action") && paramValue.equalsIgnoreCase(("get-data")))
                handleGetData(clientSocket);
            else handleGetRequest(clientSocket);
        } else if (method.equalsIgnoreCase("POST")) {
            handlePostRequest(clientSocket, in);


        }
    }

    private void handleGetRequest(Socket clientSocket) throws IOException {

        String message = KafkaUtils.consumeMessage(KafkaConstants.GET_HTML_TOPIC, KafkaConstants.GROUP_ID, KafkaConstants.BOOTSTRAP_SERVERS);
        System.out.println("Checked");
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: MyHttpServer");

        out.println("Content-Length: " + message.length());

        out.println("");
        out.println(message);

        clientSocket.close();
    }

    private void handleGetData(Socket clientSocket) throws IOException {

        String message = KafkaUtils.consumeMessage(KafkaConstants.GET_DATA_TOPIC, KafkaConstants.GROUP_ID, KafkaConstants.BOOTSTRAP_SERVERS);
        System.out.println("Checked");
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: MyHttpServer");

        out.println("Content-Length: " + message.length());

        out.println("");
        out.println(message);

        clientSocket.close();
    }

    private void handlePostRequest(Socket clientSocket, BufferedReader in) throws IOException, InterruptedException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        int contentLength = 0;

        String headerLine;
        while ((headerLine = in.readLine()) != null && headerLine.length() > 0) {
            if (headerLine.startsWith("content-length:") || headerLine.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(headerLine.substring(16).trim());
            }
        }

        System.out.println(contentLength);
        StringBuilder requestBodyBuilder = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            requestBodyBuilder.append((char) in.read());
        }
        String requestBody = requestBodyBuilder.toString();
        System.out.println("Request body: " + requestBody);


        // TODO: Produce response to kafka

        KafkaUtils.produceMessage(KafkaConstants.INSERT_DATA_TOPIC, KafkaConstants.BOOTSTRAP_SERVERS, requestBody);
    }

}
