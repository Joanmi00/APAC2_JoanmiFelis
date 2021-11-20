package com.ieseljust.ad.myDBMS;

import java.sql.*;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

class databaseManager{
    
    String server;
    String port;
    String user;
    String pass;
    String dbname;

    databaseManager(String server, String port, String user, String pass, String dbname){
        // TO-DO:   Inicialització dels atributs de la classe
        //          amb els valors indicats
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.dbname = dbname;
    }

    public Connection connectDatabase(){
        
        Connection conn = null;
        
        try {
            // TO-DO:   Crea una connexió a la base de dades, i retorna aquesta o null, si no s'ha pogut connectar.

            // Passos:
            // 1. Carreguem el driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Crear la connexió a la BD
            String connectionUrl = "jdbc:mysql://" + this.server + ":" + this.port + "/" + this.dbname + "? useUnicode=true&characterEncoding=UTF-8&user="+ this.user + "&password=" + this.pass;

            // 3. Retornar la connexió
            conn = DriverManager.getConnection(connectionUrl);

            // Recordeu el tractament d'errors
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(databaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public void showTables() {
        Connection conn = null;
        try {
            conn = connectDatabase();
            DatabaseMetaData metaData = conn.getMetaData();

            System.out.println();
            System.out.println(ConsoleColors.PURPLE_BOLD + "-------------------------------" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE_BOLD + "|            Tables           |" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE_BOLD + "-------------------------------" + ConsoleColors.RESET);

            ResultSet rs = metaData.getTables(this.dbname, null, null, null);
            while (rs.next()) {
                System.out.print(ConsoleColors.BLUE_BOLD_BRIGHT + rs.getString(3) + ConsoleColors.RESET);
                System.out.println();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if (!conn.isClosed())
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertIntoTable(String table){
        // TO-DO: Afig informació a la taula indicada

        // Passos
        // 1. Estableix la connexió amb la BD
        // 2. Obtenim les columnes que formen la taula (ens interessa el nom de la columna i el tipus de dada)
        // 3. Demanem a l'usuari el valor per a cada columna de la taula
        // 4. Construim la sentència d'inserció a partir de les dades obtingudes
        //    i els valors proporcionats per l'usuari

        // Caldrà tenir en compte:
        // - Els tipus de dada de cada camp
        // - Si es tracta de columnes generades automàticament per la BD (Autoincrement)
        //   i no demanar-les
        // - Gestionar els diferents errors
        // - Si la clau primària de la taula és autoincremental, que ens mostre el valor d'aquesta quan acabe.

        ArrayList<String> bdTables = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> type = new ArrayList<>();
        ArrayList<Boolean> nullables = new ArrayList<>();
        ArrayList<Boolean> increment = new ArrayList<>();

        System.out.println();
        System.out.println(ConsoleColors.PURPLE_BOLD + "-------------------------------" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE_BOLD + "|           Insertar          |" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE_BOLD + "-------------------------------" + ConsoleColors.RESET);

        Connection conn;
        try {
            // Conectar con DB y conseguir los meta datos
            conn = connectDatabase();
            DatabaseMetaData metaData = conn.getMetaData();

            ResultSet rsmd = metaData.getTables(this.dbname, null, null, null);
            while (rsmd.next()) {
                bdTables.add(rsmd.getString(3).toUpperCase());
            }
            rsmd.close();

            if (bdTables.contains(table.toUpperCase())) {
                // Obtener meta datos de las columnas de la tabla
                ResultSet columnes = metaData.getColumns(this.dbname, null, table, null);
                while (columnes.next()) {
                    String nom = columnes.getString(4);
                    String tipo = columnes.getString(6);
                    String nul = columnes.getString(18);
                    String auto = columnes.getString(23);

                    names.add(nom);
                    type.add(tipo);
                    if (nul.equalsIgnoreCase("YES")) {
                        nullables.add(true);
                    } else {
                        nullables.add(false);
                    }

                    if (auto.equalsIgnoreCase("YES")) {
                        increment.add(true);
                    } else {
                        increment.add(false);
                    }

                    System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + String.format("%-25s %-15s %-15s %-15s", nom, tipo, nul, auto));
                }

                columnes.close();

                // Añadir '?'
                StringBuilder sql = new StringBuilder("INSERT INTO " + table + " VALUES (");
                int interrogantes = names.size();
                for (boolean b : increment) {
                    if (b) {
                        interrogantes--;
                    }
                }

                for (int i = 0; i < names.size() - 1; i++) {
                    sql.append("?, ");
                }
                sql.append("?);");

                PreparedStatement pst = conn.prepareStatement(String.valueOf(sql));

                System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT + "INSERTAR DATOS" + ConsoleColors.RESET);
                int cursor = 0;
                for (int i = 0; i < names.size(); i++) {
                    if (increment.get(i)) {
                        cursor++;
                        pst.setInt(cursor, 0);
                        System.out.println(ConsoleColors.YELLOW_BOLD_BRIGHT + "El campo " + names.get(i) + " es autoincremental, no es necesario introducir datos." + ConsoleColors.RESET);
                    } else {
                        cursor++;
                        boolean check = true;
                        if (nullables.get(i)) {
                            System.out.print(ConsoleColors.YELLOW_BOLD_BRIGHT + "El campo " + names.get(i) + " puede ser nulo. ");
                            check = Leer.leerBoolean(ConsoleColors.CYAN_BOLD_BRIGHT + "¿Quieres añadir un valor? (true/false): "+ConsoleColors.RESET);
                        }
                        if (check) {
                            String msg ="[" + ConsoleColors.BLUE_BOLD_BRIGHT + type.get(i) + ConsoleColors.RESET + "]\n" + ConsoleColors.BLUE_BOLD_BRIGHT + " Introduce un valor para el campo " + names.get(i) + ": " + ConsoleColors.RESET;
                            switch (type.get(i).toUpperCase()) {
                                case "INT":
                                    int intValue = Leer.leerEntero(msg);
                                    pst.setInt(cursor, intValue);
                                    break;
                                case "DATETIME":
                                    Date dateValue = Leer.leerFecha();
                                    pst.setDate(cursor, dateValue);
                                    break;
                                default:
                                    String stringValue = Leer.leerTexto(msg);
                                    pst.setString(cursor, stringValue);
                                    break;
                            }
                        } else {
                            switch (type.get(i).toUpperCase()) {
                                case "INT":
                                    pst.setInt(cursor, Integer.parseInt(""));
                                    break;
                                case "DATETIME":
                                    pst.setDate(cursor, Date.valueOf(""));
                                    break;
                                default:
                                    pst.setString(cursor, "");
                                    break;
                            }
                        }
                    }
                }

                int res = pst.executeUpdate();

                System.out.println("Resultado: " + res);
            } else {
                System.out.println(ConsoleColors.RED_BOLD + "La tabla no existe en la base de datos.");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void showDescTable(String table){

        Connection conn = null;

        try {

            // TO-DO: Mostra la descripció de la taula indicada,
            //        mostrant: nom, tipus de dada i si pot tindre valor no nul
            //        Informeu també de les Claus Primàries i externes

            System.out.println();
            System.out.println(ConsoleColors.PURPLE_BOLD + "--------------------------------------------------------------" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE_BOLD + "|                   Describe Table " + table + "                   |" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE_BOLD + "--------------------------------------------------------------" + ConsoleColors.RESET);

            conn = connectDatabase();
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT + String.format("%-25s %-15s %15s", "Nombre", "Tipo", "Nullable") + ConsoleColors.RESET);

            ResultSet rspk = metaData.getPrimaryKeys(this.dbname, null, table);
            ArrayList<String> pks = new ArrayList<>();

            while (rspk.next())
                pks.add(rspk.getString(4));

            rspk.close();

            ResultSet rsfk = metaData.getImportedKeys(this.dbname, null, table);
            ArrayList<String> fks = new ArrayList<>();
            ArrayList<String> fksExt = new ArrayList<>();
            while (rsfk.next()) {
                fks.add(rsfk.getString(8));
                fksExt.add(rsfk.getString(3));
            }

            rsfk.close();

            ResultSet columnes = metaData.getColumns(this.dbname, null, table, null);

            while (columnes.next()) {
                String columnName = columnes.getString(4);

                if (pks.contains(columnName))
                    columnName += " (PK)";

                if (fks.contains(columnName))
                    columnName += " (FK) --> " + fksExt.get(fks.indexOf(columnName));


                String tipus = columnes.getString(6);
                String nullable = columnes.getString(18);
                System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT + String.format("%-25s %-15s %15s", columnName, tipus, nullable));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if (!conn.isClosed())
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void select(String sentencia){

        Connection con;
        try {
            con = connectDatabase();
            ResultSet rs = con.createStatement().executeQuery(sentencia);

            System.out.println();
            System.out.println(ConsoleColors.PURPLE_BOLD + "------------------------------------" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE_BOLD + "|             Consultas            |" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE_BOLD + "------------------------------------" + ConsoleColors.RESET);
            ResultSetMetaData rsm = rs.getMetaData();
            for (int i = 0; i < rsm.getColumnCount(); i++) {
                System.out.print(ConsoleColors.BLUE_BOLD_BRIGHT + String.format("%-15s", rsm.getColumnName(i+1)) + ConsoleColors.RESET);
            }
            System.out.println();

            while (rs.next()){
                for (int i = 0; i < rsm.getColumnCount(); i++) {
                    System.out.printf(ConsoleColors.CYAN_BOLD_BRIGHT + "%-15s", rs.getString(i+1) + ConsoleColors.RESET);
                }
                System.out.println();
            }
        } catch (Exception e){
            System.out.println(ConsoleColors.RED_BOLD+"Error al ejecutar la consulta."+ConsoleColors.RESET);
        }
    }

    public void startShell(){

        // TO-DO: Inicia la shell del mode base de dades
            Scanner keyboard = new Scanner(System.in);
        String command;

        do {

            System.out.print(ConsoleColors.GREEN_BOLD_BRIGHT+"# ("+this.user+") on "+this.server+":"+this.port+ "[" + this.dbname + "]" +"> "+ConsoleColors.RESET);
            command = keyboard.nextLine();

                        
            switch (command){
                case "sh tables":
                case "show tables":
                    this.showTables();
                    break;
                    
                case "quit":
                    break;
                    
                default:
                    // Com que no podem utilitzar expressions
                    // regulars en un case (per capturar un "use *")
                    // busquem aquest cas en el default:

                    String[] subcommand=command.split(" ");
                    switch (subcommand[0]){
                        case "describe":
                            this.showDescTable(subcommand[1]);
                            break;
                        case "insert":
                            this.insertIntoTable(subcommand[1]);
                            break;
                        case "select":
                            this.select(command);
                            break;
                        case "quit":
                            break;
                        
                        default:
                            System.out.println(ConsoleColors.RED+"Unknown option"+ConsoleColors.RESET);
                            break;
                    }
            }
        }while(!command.equals("quit"));

    }
}