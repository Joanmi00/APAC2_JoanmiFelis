package com.ieseljust.ad.myDBMS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class connectionManager{
    
    String server;
    String port;
    String user;
    String pass;
    
    connectionManager(){
        // TO-DO: Inicialització dels atributs de la classe
        //       per defecte
        this.server = "localhost";
        this.port = "3306";
        this.user = "root";
        this.pass = "Root";
    }

    connectionManager(String server, String port, String user, String pass){
        // TO-DO:   Inicialització dels atributs de la classe
        //          amb els valors indicats
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public Connection connectDBMS(){
        
        Connection conn = null;
        
        try {
            // TO-DO:   Crea una connexió a la base de dades,
            //          i retorna aquesta o null, si no s'ha pogut connectar.

            // Passos:
            // 1. Carreguem el driver JDBC
            // 2. Crear la connexió a la BD
            // 3. Retornar la connexió
            Class.forName("com.mysql.cj.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://" + this.server + ":" + this.port + "/?useUnicode=true&characterEncoding=UTF-8&user=" + this.user + "&password=" + this.pass;
            conn = DriverManager.getConnection(connectionUrl);
            // Recordeu el tractament d'errors
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(connectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public void showInfo(){
        try {
            // TO-DO: Mostra la informació del servidor a partir de les metadades

            DatabaseMetaData dbmd = connectDBMS().getMetaData();

            System.out.println("");
            System.out.println(ConsoleColors.PURPLE_BOLD + "-----------------------------------" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE_BOLD + "|               INFO              |" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE_BOLD + "-----------------------------------" + ConsoleColors.RESET);

            // - Nom del SGBD
            System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "- Nom del SGBD: " + ConsoleColors.RESET + dbmd.getDriverName());

            // - Driver utilitzat
            System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "- Driver utilitzat: " + ConsoleColors.RESET + dbmd.getDatabaseProductName());

            // - URL de connexió
            System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "- URL de conexió: " + ConsoleColors.RESET + dbmd.getURL());

            // - Nom de l'usuari connectat
            System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + "- Nom del usuari connectat: " + ConsoleColors.RESET + dbmd.getUserName());
            System.out.println("");
            
            }
        
        // Recordeu el tractament d'errors
        
        catch (SQLException ex) {
            Logger.getLogger(connectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

    public void showDatabases(){
        try {
            // TO-DO: Mostrem les bases de dades del servidor, bé des del catàleg o amb una consulta
            //Creating a Statement object
            
            DatabaseMetaData dbmt = connectDBMS().getMetaData();

            System.out.println("");
            System.out.println(ConsoleColors.PURPLE_BOLD + "------------------------------" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE_BOLD + "|          DataBases         |" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE_BOLD + "------------------------------" + ConsoleColors.RESET);

            ResultSet rs = dbmt.getCatalogs();
            while(rs.next()) {
                System.out.print(ConsoleColors.BLUE_BOLD_BRIGHT + rs.getString(1) + ConsoleColors.RESET);
                System.out.println();
            }
            
            // Recordeu el tractament d'errors
        } catch (SQLException ex) {
            Logger.getLogger(connectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

    public void importDatabase(String archivo){

        System.out.println();
        System.out.println(ConsoleColors.PURPLE_BOLD + "-------------------------------------" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE_BOLD + "|              Importar             |" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE_BOLD + "-------------------------------------" + ConsoleColors.RESET);

        File scriptFile = new File("sql/" +archivo);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(scriptFile));
        } catch (FileNotFoundException e) {
            System.out.println(ConsoleColors.RED_BOLD + archivo + " no existe"+ConsoleColors.RESET);
        }
        String linea;
        StringBuilder sb = new StringBuilder();
        String saltoLinea = System.getProperty("line.separator");

        try {
            while ((linea = br.readLine()) != null){
                sb.append(linea);
                sb.append(saltoLinea);
            }
        } catch (IOException e) {
            System.out.println(ConsoleColors.RED_BOLD_BRIGHT+"ERROR de I/O" + ConsoleColors.RESET);
        }

        String query = sb.toString();
        System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT+"Ejecutando consulta" + ConsoleColors.RESET);

        try {
            Statement st = connectDBMS().createStatement();
            int res = st.executeUpdate(query);
            System.out.println(ConsoleColors.GREEN_BOLD_BRIGHT+"El Script se ha ejecutado, resultado: " + ConsoleColors.RESET + res);
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        
    public void startShell(){

        Scanner keyboard = new Scanner(System.in);
        String command;

        do {

            System.out.print(ConsoleColors.GREEN_BOLD_BRIGHT+"# ("+this.user+") on "+this.server+":"+this.port+"> "+ConsoleColors.RESET);
            command = keyboard.nextLine();

                        
            switch (command){
                case "sh db":
                case "show databases":
                    this.showDatabases();
                    break;
                
                case "info":
                    this.showInfo();
                    break;
                    
                case "quit":
                    break;
                    
                default:
                    // Com que no podem utilitzar expressions
                    // regulars en un case (per capturar un "use *")
                    // busquem aquest cas en el default:

                    String[] subcommand=command.split(" ");
                    switch (subcommand[0]){
                        case "use":
                            // TO-DO:
                                // Creem un objecte de tipus databaseManager per connectar-nos a
                                // la base de dades i iniciar una shell de manipulació de BD..
                            
                            databaseManager db;

                            String database;

                            do {
                                
                                database = subcommand[1];
                                System.out.print(ConsoleColors.RESET);

                                db = new databaseManager(server, port, user, pass, database);


                            } while(db.connectDatabase()==null);

                            db.startShell();
                            break;
                        case "import":
                            
                            this.importDatabase(subcommand[1]);
                            break;

                        default:
                            System.out.println(ConsoleColors.RED+"Unknown option"+ConsoleColors.RESET);
                            break;


                    }
            }
        } while(!command.equals("quit"));
    }
}