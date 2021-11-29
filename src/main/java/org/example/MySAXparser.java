package org.example;

import org.example.model.Catalog;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MySAXparser {

    public Catalog parse(){
    //вызов фабрики
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXparserHandler handler = new SAXparserHandler();
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        } catch (Exception e) {
            System.out.println("Ошибка "+ e.toString());
            return null;
        }
    //выбор файла
        Scanner in = new Scanner(System.in);
        System.out.print("Введите имя файла [filename.xml]: ");
        String fileName = in.nextLine();
        File file = new File("data/"+fileName);
    //парсинг файла
        try {
            parser.parse(file,handler);
        } catch (SAXException e) {
            System.out.println("Ошибка парсинга "+ e.toString());
            return null;
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода парсинга "+ e.toString());
            return null;
        }
    //подключение к бд и вставка данных
        handler.BdConnect();


        return null;
    }
}
