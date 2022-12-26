package Utils;

import com.codeborne.selenide.Selenide;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class Helpers {

    public static int generateRandNum(){
        Random random = new Random();
        return random.nextInt();
    }
    public static void openPage(String url){
        Selenide.open(url);
    };
    public static String getProperty(Class classFile, String propertyName) throws IOException {
        InputStream is = classFile.getClassLoader().getResourceAsStream("Properties.properties");
        Properties properties = new Properties();
        properties.load(is);
        return properties.getProperty(propertyName);
    }
}
