package ru.ravens.models.InnerModel;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;


@XmlRootElement
public class Group implements Serializable
{
    private int groupID;
    private String name;
    private int adminID;
    private int sum;

    public static Group parseGroup(ResultSet resultSet) throws Exception
    {
        Group group = new Group();

        group.setGroupID(resultSet.getInt("GroupID"));
        group.setName(resultSet.getString("Name"));

        //нужно, чтобы отметить что я админ в той ии иной беседе
        group.setAdminID(resultSet.getInt("AdminID"));
        group.setSum(resultSet.getInt("Sum"));
        return group;

    }
    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {

        this.adminID = adminID;
    }
    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
