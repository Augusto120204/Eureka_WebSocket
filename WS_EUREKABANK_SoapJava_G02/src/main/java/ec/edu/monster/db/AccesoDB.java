/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author cesar
 */
public class AccesoDB {

    private AccesoDB() {
    }
    
    public static Connection getConnection() throws SQLException{
        Connection cn = null;
        
        try{
            //DatosMySQL
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://34.61.84.132:3306/eurekabank?useSSL=false&allowPublicKeyRetrieval=true";
            String user = "root";
            String pass = "Anhrx&E^&U$Z;B1e";
            
            Class.forName(driver);
            
            cn = DriverManager.getConnection(url, user, pass);
        }catch (SQLException e){
        throw e;
        }catch (ClassNotFoundException e){
        throw new SQLException("ERROR, no se encontro el driver");
        }catch (Exception e){
        throw new SQLException("ERROR, no se tiene acceso al servidor");
        }
        
        return cn;
    }
}
