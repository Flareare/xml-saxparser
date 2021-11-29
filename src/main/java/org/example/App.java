package org.example;

import org.example.model.Catalog;


public class App
{
    public static void main( String[] args ) {
    //
        MySAXparser saxParser = new MySAXparser();
        Catalog catalog = saxParser.parse();

    }
}
