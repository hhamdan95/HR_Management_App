/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.management.app;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Haitam
 */
public class MyConnection {
    
    public Connection con;
    public Statement stmt;
    public ResultSet rs;
    public PreparedStatement pst;
    public CallableStatement cst;
    String url = "jdbc:oracle:thin:@localhost:1521:orcl";
    String driver = "oracle.jdbc.OracleDriver";
    String user = "projet_fin_formation";
    String pass = "ntic";
    
    public MyConnection(boolean navigate) throws ClassNotFoundException, SQLException
    {
        Class.forName(driver);
        con = DriverManager.getConnection(url, user, pass);
        
        if(navigate)
        {
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        }
        else
        {
            stmt = con.createStatement();
        } 
    }
    
    public void MyExecQuery(String cmd) throws SQLException
    {
        rs = stmt.executeQuery(cmd);
    }
    
    public int MyExecUpdate(String cmd) throws SQLException
    {
        return stmt.executeUpdate(cmd);
    }
    
    public PreparedStatement MyExecPrepStat(String cmd) throws SQLException
    {
        return pst = con.prepareStatement(cmd);
    }
    
    public CallableStatement MyExecCallStat(String cmd) throws SQLException
    {
        return cst = con.prepareCall(cmd);
    }
    
    public void Close() throws SQLException
    {
        con.close();
        stmt.close();
        rs.close();
        pst.close();
        cst.close();
    }
}
