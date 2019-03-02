package com.sequenceiq.it.cloudbreak.newway.mock.model;

import static com.sequenceiq.it.cloudbreak.newway.Mock.gson;

import java.util.Map;

import com.sequenceiq.cloudbreak.client.AccessToken;
import com.sequenceiq.it.cloudbreak.newway.mock.AbstractModelMock;
import com.sequenceiq.it.cloudbreak.newway.mock.DefaultModel;
import com.sequenceiq.it.spark.DynamicRouteStack;

import spark.Service;

public class IdentityMock extends AbstractModelMock {

    private static final String TOKEN_CREDENTIALS = "/oauth/token";

    private static final String CHECK_TOKEN = "/check_token";

    private DynamicRouteStack dynamicRouteStack;

    public IdentityMock(Service sparkService, DefaultModel defaultModel) {
        super(sparkService, defaultModel);
        dynamicRouteStack = new DynamicRouteStack(sparkService, defaultModel);
    }

    public void addIdentityMappings() {
        postTokenCredentials();
        postCheckToken();
    }

    private void postTokenCredentials() {
        getSparkService().post(TOKEN_CREDENTIALS, (request, response) -> new AccessToken("eyJhbGciOiJIUzI1NiIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiI4MjllMWJhMjEwZWM0OTZlOTIyZTEyOTkxYWRlNzU1YyIsInN1YiI6ImZlMmE2Yjk4LTYyMGMtNGE1My1hYWI1LWFkYzYzMDY2OTNkNyIsInNjb3BlIjpbImNsb3VkYnJlYWsubmV0d29ya3MucmVhZCIsInBlcmlzY29wZS5jbHVzdGVyIiwiY2xvdWRicmVhay51c2FnZXMudXNlciIsImNsb3VkYnJlYWsucmVjaXBlcyIsImNsb3VkYnJlYWsudXNhZ2VzLmdsb2JhbCIsIm9wZW5pZCIsImNsb3VkYnJlYWsucGxhdGZvcm1zIiwiY2xvdWRicmVhay50ZW1wbGF0ZXMucmVhZCIsImNsb3VkYnJlYWsudXNhZ2VzLmFjY291bnQiLCJjbG91ZGJyZWFrLnN0YWNrcy5yZWFkIiwiY2xvdWRicmVhay5ldmVudHMiLCJjbG91ZGJyZWFrLmJsdWVwcmludHMiLCJjbG91ZGJyZWFrLnRlbXBsYXRlcyIsImNsb3VkYnJlYWsuc3NzZGNvbmZpZ3MiLCJjbG91ZGJyZWFrLm5ldHdvcmtzIiwiY2xvdWRicmVhay5wbGF0Zm9ybXMucmVhZCIsImNsb3VkYnJlYWsuY3JlZGVudGlhbHMucmVhZCIsImNsb3VkYnJlYWsuc2VjdXJpdHlncm91cHMucmVhZCIsImNsb3VkYnJlYWsuc2VjdXJpdHlncm91cHMiLCJjbG91ZGJyZWFrLnN0YWNrcyIsImNsb3VkYnJlYWsuY3JlZGVudGlhbHMiLCJjbG91ZGJyZWFrLnNzc2Rjb25maWdzLnJlYWQiLCJjbG91ZGJyZWFrLnJlY2lwZXMucmVhZCIsImNsb3VkYnJlYWsuYmx1ZXByaW50cy5yZWFkIiwiY2xvdWRicmVhay5hdXRvc2NhbGUiXSwiY2xpZW50X2lkIjoiY2xvdWRicmVha19zaGVsbCIsImNpZCI6ImNsb3VkYnJlYWtfc2hlbGwiLCJhenAiOiJjbG91ZGJyZWFrX3NoZWxsIiwidXNlcl9pZCI6ImJjYTNmYjE0LTBmM2YtMzA0OS04NDc4LTcwY2FmYTgwNzVlMiIsIm9yaWdpbiI6InVhYSIsInVzZXJfbmFtZSI6ImdtZXN6YXJvc0Bob3J0b253b3Jrcy5jb20iLCJlbWFpbCI6ImdtZXN6YXJvc0Bob3J0b253b3Jrcy5jb20iLCJhdXRoX3RpbWUiOjE1NTE0NDk3NDQsInJldl9zaWciOiJhYjVkNGJmNiIsImlhdCI6MTU1MTQ0OTc0NCwiZXhwIjo0NTUxNDkyOTQ0LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvdWFhL29hdXRoL3Rva2VuIiwiemlkIjoidWFhIiwiYXVkIjpbImNsb3VkYnJlYWtfc2hlbGwiLCJjbG91ZGJyZWFrLnJlY2lwZXMiLCJvcGVuaWQiLCJjbG91ZGJyZWFrIiwiY2xvdWRicmVhay5wbGF0Zm9ybXMiLCJjbG91ZGJyZWFrLmJsdWVwcmludHMiLCJjbG91ZGJyZWFrLnRlbXBsYXRlcyIsImNsb3VkYnJlYWsubmV0d29ya3MiLCJwZXJpc2NvcGUiLCJjbG91ZGJyZWFrLnNzc2Rjb25maWdzIiwiY2xvdWRicmVhay51c2FnZXMiLCJjbG91ZGJyZWFrLnNlY3VyaXR5Z3JvdXBzIiwiY2xvdWRicmVhay5zdGFja3MiLCJjbG91ZGJyZWFrLmNyZWRlbnRpYWxzIiwiY2xvdWRicmVhay5hdXRvc2NhbGUiXX0.mVfS88m4SDw5EBFB6F4yOwXJIJhTAJDJbXcH8kkPe9w",
                "access", 123123321), gson()::toJson);
    }

    private void postCheckToken() {
        getSparkService().post(CHECK_TOKEN, (request, response) -> Map.of("client_id", "cloudbreak_shell"));
    }

}
