import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class User {

    public static void main(String[] args) throws IOException {

        Client client = new Client();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while(client.closed()){
            String lineInput = bufferedReader.readLine();
            String answer = client.execute(lineInput);
            System.out.println(answer);
        }
    }
}