package com.NccAPI.Issues;

import com.NccAPI.NccAPI;
import com.NccIssues.NccIssueData;
import com.NccIssues.NccIssues;

import java.util.ArrayList;

/**
 * Created by root on 17.11.16.
 */
public class IssuesServiceImpl implements IssuesService {

    public ApiIssueData getOpenIssues(
            String login,
            String key) {

        ApiIssueData apiIssueData = new ApiIssueData();

        apiIssueData.data = new ArrayList<>();
        apiIssueData.status = 1;
        apiIssueData.message = "Error";

        if (!new NccAPI().checkPermission(login, key, "GetOpenIssues")) {
            apiIssueData.message = "Permission denied";
            return apiIssueData;
        }

        ArrayList<NccIssueData> data = new NccIssues().getOpenIssues();

        if (data != null) {
            apiIssueData.data = data;
            apiIssueData.status = 0;
            apiIssueData.message = "success";
        }

        return apiIssueData;
    }
}
