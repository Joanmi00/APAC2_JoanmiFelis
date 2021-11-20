package com.ieseljust.ad.myDBMS;

// Imports per a entrada de dades
import java.util.Scanner;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

// Imports per a entrada de dades
import java.util.Scanner;

public class DBMan {
    /*
    Esta és la classe llançadora de l'aplicació
    Conté el mètode main que recull la informació del servidor
    i inicia una instància de connectionManager per 
    gestionar les connexions
    */
    
    public static void main(String[] args){
            
        connectionManager cm = new connectionManager();

        Scanner keyboard = new Scanner(System.in);

        String user, pass, ip, port;
        
        do {

            System.out.print(ConsoleColors.GREEN_BOLD_BRIGHT+"# Server: "+ConsoleColors.RESET);
            ip = keyboard.nextLine();

            System.out.print(ConsoleColors.GREEN_BOLD_BRIGHT+"# Port: "+ConsoleColors.RESET);
            port = keyboard.nextLine();

            System.out.print(ConsoleColors.GREEN_BOLD_BRIGHT+"# Username: "+ConsoleColors.RESET);
            user = keyboard.nextLine();

            System.out.print(ConsoleColors.GREEN_BOLD_BRIGHT+"# Password: "+ConsoleColors.BLACK);
            pass = keyboard.nextLine();
            System.out.print(ConsoleColors.RESET);
            if (!"".equals(ip) && !"".equals(port) && !"".equals(user) && !"".equals(pass)){
                cm=new connectionManager(ip, port, user, pass);
            }
            else {
                
                cm=new connectionManager();
                
            } 
            
                
        } while(cm.connectDBMS()==null);

        cm.startShell();

    }

}
