package com.NccIssues;

import java.util.ArrayList;

/**
 * Created by root on 17.11.16.
 */
public class NccIssues {

    public ArrayList<NccIssueData> getOpenIssues(){
        return new NccIssueData().getDataList("SELECT * FROM ncc_issues WHERE istatus='ST-Open'");
    }
}
