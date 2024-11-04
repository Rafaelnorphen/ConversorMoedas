import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class ConversorMoedas {

    private static final String API_KEY = "0c638e12c360631930573c6d";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] moedasDisponiveis = {"USD", "EUR", "BRL", "JPY", "GBP", "CAD"};

        while (true) {
            System.out.println("\n--- Conversor de Moedas ---");
            System.out.println("Selecione a conversão desejada:");
            for (int i = 0; i < moedasDisponiveis.length; i++) {
                System.out.println((i + 1) + ". " + moedasDisponiveis[i] + " para outras moedas");
            }
            System.out.println("7. Sair");

            System.out.print("Escolha uma opção (1-7): ");
            int escolha = scanner.nextInt();

            if (escolha == 7) {
                System.out.println("Saindo...");
                break;
            }

            if (escolha >= 1 && escolha <= 6) {
                String origem = moedasDisponiveis[escolha - 1];
                System.out.print("Digite o código da moeda de destino (exemplo: USD, EUR, BRL): ");
                String destino = scanner.next().toUpperCase();

                if (!destino.equals(origem)) {
                    double taxa = obterTaxa(origem, destino);
                    if (taxa != -1) {
                        System.out.print("Digite o valor em " + origem + " que deseja converter para " + destino + ": ");
                        double valor = scanner.nextDouble();
                        double convertido = valor * taxa;
                        System.out.printf("%.2f %s é igual a %.2f %s\n", valor, origem, convertido, destino);
                    }
                } else {
                    System.out.println("A moeda de destino não pode ser a mesma que a de origem.");
                }
            } else {
                System.out.println("Opção inválida. Tente novamente.");
            }
        }
        scanner.close();
    }

    private static double obterTaxa(String origem, String destino) {
        try {
            String urlStr = API_URL + origem;
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                double taxa = jsonObject.getJSONObject("conversion_rates").getDouble(destino);
                return taxa;
            } else {
                System.out.println("Erro ao obter a taxa de câmbio: Código de resposta " + responseCode);
                return -1;
            }
        } catch (Exception e) {
            System.out.println("Erro ao conectar à API: " + e.getMessage());
            return -1;
        }
    }
}
