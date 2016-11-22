package com.NccAPI.Issues;

public interface IssuesService {
    public ApiIssueData getOpenIssues(
            String login,
            String key);
}
