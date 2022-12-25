package Utils;

import com.codeborne.selenide.Selenide;

import java.util.Random;

public class Helpers {
    public static int generateRandNum(){
        Random random = new Random();
        return random.nextInt();
    }
    public static void openPage(String url){
        Selenide.open(url);
    };
}
