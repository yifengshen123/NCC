package com.NccIssues;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;
import java.util.Date;

public class NccIssueData extends NccAbstractData<NccIssueData> {
    public Integer id;
    public Long idata;
    public Integer uid;
    public Integer reporter;
    public Integer closer;
    public Integer itype;
    public String idesc;
    public String istatus;
    public Integer iurgency;
    public Long rdate;
    public String addr;
    public String street;
    public String build;
    public String flat;
    public String fio;
    public String phone;
    public Integer ruid;
    public Integer euid;
    public Long edate;
    public String reason;

    @Override
    public NccIssueData fillData() {
        NccIssueData issueData = new NccIssueData();

        try {
            issueData.id = rs.getInt("id");
            issueData.idata = rs.getDate("idate").getTime();
            issueData.uid = rs.getInt("uid");
            issueData.reporter = rs.getInt("reporter");
            issueData.closer = rs.getInt("closer");
            issueData.itype = rs.getInt("itype");
            issueData.idesc = rs.getString("idesc");
            issueData.istatus = rs.getString("istatus");
            issueData.iurgency = rs.getInt("iurgency");
            Date d = rs.getDate("rdate");
            if (d != null) issueData.rdate = d.getTime();
            issueData.addr = rs.getString("addr");
            issueData.street = rs.getString("street");
            issueData.build = rs.getString("build");
            issueData.flat = rs.getString("flat");
            issueData.fio = rs.getString("fio");
            issueData.phone = rs.getString("phone");
            issueData.ruid = rs.getInt("ruid");
            issueData.euid = rs.getInt("euid");
            d = rs.getDate("edate");
            if (d != null) issueData.edate = d.getTime();
            issueData.reason = rs.getString("reason");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return issueData;
    }
}
