package org.example;

import org.example.model.Catalog;
import org.example.model.Plant;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SAXparserHandler extends DefaultHandler {

    private static final String TAG_CATALOG = "CATALOG";

        private static final String TAG_PLANT = "PLANT";

            private static final String TAG_COMMON = "COMMON";
            private static final String TAG_BOTANICAL = "BOTANICAL";
            private static final String TAG_ZONE = "ZONE";
            private static final String TAG_LIGHT = "LIGHT";
            private static final String TAG_PRICE = "PRICE";
            private static final String TAG_AVAILABILITY = "AVAILABILITY";

    private Catalog catalog;
    private String uuid;
    private String date;
    private String company;

    private List<Plant> plant = new ArrayList<>();
    private String currentTag;

    private String common;
    private String botanical;
    private String zone;
    private String light;
    private String price;
    private int availability;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentTag = qName;
        //если текущий тэг <CATALOG> - "выцепить" атрибуты
        if (qName.equals(TAG_CATALOG)){
            uuid = attributes.getValue("uuid");
            date = attributes.getValue("date");
            company = attributes.getValue("company");
            catalog = new Catalog (uuid,date,company);
            System.out.println(" uuid: "+uuid+", date: "+date+", company: "+company);
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //если текущий элемент был <PLANT>, то при его закрытии в Catalog добавится Plant
        if (qName.equals(TAG_PLANT)){
           Plant plant = new Plant(common,botanical,zone,light,price,availability);
           catalog.addPlant(plant);
        }
        currentTag = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //если текущий тэг пустой - выход
       if(currentTag == null){
           return;
       }
       //если текущий тэг один из элементов Plant - записать значения в переменную
       switch (currentTag){
           case TAG_COMMON:
               common = new String(ch,start,length);
               System.out.println(common);
               break;
           case TAG_BOTANICAL:
               botanical = new String(ch,start,length);
               System.out.println(botanical);
               break;
           case TAG_ZONE:
               zone = new String(ch,start,length);
               System.out.println(zone);
               break;
           case TAG_LIGHT:
               light = new String(ch,start,length);
               System.out.println(light);
               break;
           case TAG_PRICE:
               //prePrice - цена со знаком "$"
               String prePrice = new String(ch,start,length);
               System.out.println(prePrice);
               //price - цена без знака "$"
               price = prePrice.replace("$","");
               System.out.println(price);
               break;
           case TAG_AVAILABILITY:
               availability = Integer.parseInt(new String(ch,start,length));
               System.out.println(availability);
               System.out.println("");
               break;
       }
    }

    public void BdConnect() {
        try {
            //Подключение к БД
            Class.forName("org.postgresql.Driver");
            Scanner in = new Scanner(System.in);
            System.out.print("Введите данные БД\nIP: ");
            String url = "jdbc:postgresql://" + in.nextLine() + ':';
            System.out.print("Порт: ");
            url += in.nextLine() + '/';
            System.out.print("Имя БД: ");
            url += in.nextLine();
            System.out.print("Введите данные пользователя\nЛогин: ");
            String login = in.nextLine();
            System.out.print("Пароль: ");
            String password = in.nextLine();

//            Class.forName("org.postgresql.Driver");
//            Scanner in = new Scanner(System.in);
//          //  System.out.print("Введите данные БД\nIP: ");
//            String url = "jdbc:postgresql://localhost:5432/PlantsCatalog";
//           // System.out.print("Порт: ");
//          //  url += in.nextLine() + '/';
//           // System.out.print("Имя БД: ");
//           // url += in.nextLine();
//           // System.out.print("Введите данные пользователя\nЛогин: ");
//            String login = "postgres";
//          //  System.out.print("Пароль: ");
//            String password = "d4Hd4p!Qqs7Dk7C";

            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                System.out.println("PostgreSQL JDBC Driver не обнаружен");
                e.printStackTrace();
                return;
            }
            Connection con;
            try {
                con = DriverManager.getConnection(url, login, password);
            } catch (SQLException e) {
                System.out.println("Ошибка подключения!");
                return;
            }

            if (con != null) {
                System.out.println("Подключено...");
            } else {
                System.out.println("Не удалось подключиться");
                return;
            }

            String sql = "";
            PreparedStatement stmt = con.prepareStatement(sql);
            try {
                //Получение данных
                String uuid = catalog.getUuid();
                String date = catalog.getDate();
                //преобразование String в Timestamp
                Timestamp newDateFormat = new Timestamp(new SimpleDateFormat("dd.MM.yyyy").parse(date).getTime());
                String company = catalog.getCompany();

                sql = "INSERT INTO d_cat_catalog (delivery_date, company, uuid) VALUES (?, ?, ?)";
                // Создание запроса.
                stmt = con.prepareStatement(sql);
                // Установка параметров
                stmt.setTimestamp(1, newDateFormat);
                stmt.setString(2, company);
                stmt.setString(3, uuid);
                // запрос на добавление каталога
                try {
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    if (e.getSQLState().equals("23505")) { //Нарушение уникальности первичного ключа
                        System.out.printf("Каталог с uuid='%s' компании %s уже существует\n", uuid, company);
                    } else {
                        e.printStackTrace();
                    }
                    return;
                }
                System.out.printf("Каталог с uuid='%s' компании %s добавлен \n", uuid, company);
                //Получение id добавленного каталога
                sql = "SELECT id FROM d_cat_catalog WHERE uuid='" + uuid + "'";
                Statement stm = con.createStatement();
                ResultSet resultSet = stm.executeQuery(sql);
                int catalogID;
                resultSet.next();
                catalogID = resultSet.getInt("id");

                //Добавление расстения в каталог
                List<Plant> plants = catalog.getPlants();
                for (Plant plant : plants) {
                    // Получение данных о расстении
                    String common = plant.getCommon();
                    String botanical = plant.getBotanical();
                    String zone = plant.getZone();
                    String light = plant.getLight();
                    String price = plant.getPrice();
                    BigDecimal newPriceFormat = BigDecimal.valueOf(Double.parseDouble(price));
                    Integer availability = plant.getAvailability();
                    sql = "INSERT INTO f_cat_plants (common, botanical, zone, light, price, availability, catalog_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, common);
                    stmt.setString(2, botanical);
                    stmt.setString(3, zone);
                    stmt.setString(4, light);
                    stmt.setBigDecimal(5, newPriceFormat);
                    stmt.setInt(6, availability);
                    stmt.setInt(7, catalogID);
                    try {
                        stmt.executeUpdate();
                    } catch (SQLException e) {
                        if (e.getSQLState().equals("23505")) { //Нарушение уникальности первичного ключа
                            System.out.printf("  Расстение %s уже существует в БД. Запись пропущена.\n", common);
                            continue; //Переход на следующее расстение
                        } else {
                            e.printStackTrace();
                            return;
                        }
                    }
                    System.out.printf(" Расстение %s добавлено в каталог с uuid='%s' компании %s.\n", common, uuid, company);
                }
            } finally {
                stmt.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
