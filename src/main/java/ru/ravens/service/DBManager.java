package ru.ravens.service;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import ru.ravens.models.InnerModel.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBManager {

    public static Connection connection = null;

    public static Connection getConnection() throws SQLServerException {
        if (connection == null) {
            SQLServerDataSource ds = new SQLServerDataSource();
            ds.setServerName("walletmsg.database.windows.net");
            ds.setPortNumber(1433);
            ds.setDatabaseName("WalletMessenger");
            ds.setEncrypt(true);
            ds.setPassword("hsepassword16)");
            ds.setUser("HSEADMIN");
            ds.setHostNameInCertificate("*.database.windows.net");
            ds.setTrustServerCertificate(false);
            ds.setLoginTimeout(1000);

            connection = ds.getConnection();
        }

        return connection;
    }

    public static void execCommand(String command) throws Exception {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        statement.execute(command);
    }

    public static ResultSet getSelectResultSet(String query) throws Exception {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static void loadPhoto(String command, byte[] array) throws Exception {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(command);
        preparedStatement.setBytes(1, array);

        preparedStatement.executeUpdate();
    }

    //Здесь будут находиться методы типа get<Class>ByQuery, которые в свою очередь будут получать getSelectResultSet,
    // а его подавать в <Class>.parse<Class>FromResultSet(ResultSet resultSet)
    public static User getUserByQuery (String query) throws Exception
    {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        return User.parseUser(statement.executeQuery(query));
    }

}
