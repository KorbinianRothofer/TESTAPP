package com.example.testapp;

import android.os.StrictMode;
import android.util.Log;

import java.sql.*;
import java.util.regex.Pattern;

 class mssqlconnection {

    //LOG STYLE: "{function} PROMPT task: ","'ERROR or long german(task)'"
    private static Boolean Error = false;   //detects error
    private static String value = "";   //Tmp saving value from ResultSet
    private static String rvalue = null; //Return value ERROR or "Select ResultSet"
     private Connection conn = null;
    //determinants which sql-function has to be called
    public String selectORupdate(String query,String columnlabel){


        //Pattern.compile in the line below is not case sensitive to get the results 4 sure (other method would be .contains())
        try {
            if (Pattern.compile(Pattern.quote("select"), Pattern.CASE_INSENSITIVE).matcher(query).find()) {
                    if (openconnection()){
                        Log.e("{selectORupdate-openconnection-select} PASS Datenbankverbindung","#Datenbank verbunden");
                    }else{
                        Log.e("{selectORupdate-openconnection-select} FEHLER Datenbankverbindung", "'Verbindung zur Datenbank konnte nicht hergestellt werden'");
                        Error = true;
                    }

                    value = select(query,columnlabel);

                    if (closeconnection()){
                        Log.e("{selectORupdate-closeconnection-select} PASS Datenbankverbindung","#Datenbank getrennt");
                    }else{
                        if(!Error) {            //only if database-connection was opened
                            Log.e("{selectORupdate-closeconnection-select} FEHLER Datenbankverbindung", "'Die Verbindung zur Datenbank konnte nicht geschlossen werden'");
                        }
                        Error = true;
                    }

            } else if (Pattern.compile(Pattern.quote("update"), Pattern.CASE_INSENSITIVE).matcher(query).find()) {
                    if (openconnection()) {
                        Log.e("{selectORupdate-openconnection-update} PASS Datenbankverbindung","#Datenbank verbunden");
                    }else{
                        Log.e("{selectORupdate-openconnection-update} FEHLER Datenbankverbindung", "'Verbindung zur Datenbank konnte nicht hergestellt werden'");
                        Error = true;
                    }

                    if(update(query)){value = "updated";}

                    if (closeconnection()){
                        Log.e("{selectORupdate-closeconnection-update} PASS Datenbankverbindung","#Datenbank getrennt");
                    }else{
                        if(!Error) {        //only if database-connection was opened
                            Log.e("{selectORupdate-closeconnection-update} FEHLER Datenbankverbindung", "'Die Verbindung zur Datenbank konnte nicht geschlossen werden'");
                        }
                        Error = true;
                    }

            } else if (!(Pattern.compile(Pattern.quote("select"), Pattern.CASE_INSENSITIVE).matcher(query).find())
                  && !(Pattern.compile(Pattern.quote("update"), Pattern.CASE_INSENSITIVE).matcher(query).find())) {
                    Log.e("{selectORupdate} FEHLER Datenabfrage", "'query does not contain SELECT or UPDATE'");
                    Error = true;
            }

        }catch(Exception e){
            Log.e("{selectORupdate} FEHLER Exception", e.getMessage());
            Error  = true;
        }finally {
            closeconnection();
        }

        if(!Error){rvalue = value;}else{rvalue="error";}//error detected?:    yes-true->assign value   |   no-false->assign error
        return rvalue;  //"ResultSet";"updated";"error"
    }


     private Boolean openconnection(){

         Boolean rvalue  = false;
         try{
                String _user = "brabenderls";
                String _pass = "brabender";
                String _DB = "BRABENDERls";
                String _server = "192.168.10.40:1433";
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                String ConnURL = null;

                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                ConnURL = "jdbc:jtds:sqlserver://" + _server + ";"
                        + "databaseName=" + _DB + ";user=" + _user + ";password="
                        + _pass + ";";
                conn = DriverManager.getConnection(ConnURL);
                if(conn != null){rvalue = true;}

            } catch (SQLException se) {
                Log.e("{openconnection-sql} FEHLER Exception", se.getMessage());
                rvalue = false;
                conn = null;
            } catch (ClassNotFoundException e) {
                Log.e("{openconnection-classnotfound} FEHLER Exception", e.getMessage());
                rvalue = false;
                conn = null;
            } catch (Exception e) {
                Log.e("{openconnection-exception} FEHLER Exception", e.getMessage());
                rvalue = false;
                conn = null;
            }
        return rvalue;
    }

    private Boolean closeconnection(){
        Boolean rvalue = false;
        try{
            conn.close();
            rvalue = true;
        } catch (SQLException se) {
            Log.e("{closeconnection-sql} FEHLER Exception", se.getMessage());
            rvalue = false;
        } catch (Exception e) {
            Log.e("{closeconnection-exception} FEHLER Exception", e.getMessage());
            rvalue = false;
        }
        return rvalue;
    }

    //selection with connection conn from openconnection
    private String select(String q,String columnlabel){
        String rvalue  = "";
        if(conn !=null){
            try{
                Statement smt = conn.createStatement();
                ResultSet set = smt.executeQuery(q);
                while (set.next()){
                    rvalue = rvalue + set.getString(columnlabel)+"|||||";
                }
            }catch(Exception e){
                Log.e("{select} FEHLER Exception",e.getMessage());
            }
        }
        return rvalue;
    }

    //updating with connection conn from openconnection
    private Boolean update(String q){
        Boolean rvalue  = false;    //TODO: update return statement false == error

        return rvalue;
    }
}
