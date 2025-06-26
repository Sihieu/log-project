package com.search_log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.RestClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;

import java.util.ArrayList;
import java.util.List;

public class LogDashboard {

    private static final String OPENSEARCH_HOST = "localhost";
    private static final int OPENSEARCH_PORT = 9200;
    private static final String INDEX_NAME = "storage-events";
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);

    public static void main(String[] args) throws Exception {
        OpenSearchClient client = createClient();

        System.out.println("===== DASHBOARD TRUY VẤN LOG =====");

        String allUserLogin = searchByFieldValue(client, "action", "UserLoggedIn");
        System.out.println("🔍 UserLoggedIn:");
        System.out.println(allUserLogin);

        String allUserLogout = searchByFieldValue(client, "action", "UserLoggedOut");
        System.out.println("\n🔍 UserLoggedOut:");
        System.out.println(allUserLogout);

        String activityFromIP = searchByNestedFieldValue(client, "data.ip_address", "203.0.113.25");
        System.out.println("\n🔍 Hoạt động từ IP 203.0.113.25:");
        System.out.println(activityFromIP);

        String traceFlow = searchByFieldValue(client, "correlation_id", "corr_carol_session_ghi");
        System.out.println("\n🔍 Truy vết theo correlation_id 'corr_carol_session_ghi':");
        System.out.println(traceFlow);
    }

    private static OpenSearchClient createClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("admin", "Hus@334nt"));

        RestClient restClient = RestClient.builder(
                new HttpHost(OPENSEARCH_HOST, OPENSEARCH_PORT, "http")
        ).setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
        ).build();

        OpenSearchTransport transport = new RestClientTransport(restClient, new org.opensearch.client.json.jackson.JacksonJsonpMapper());
        return new OpenSearchClient(transport);
    }

    public static String searchByFieldValue(OpenSearchClient client, String fieldName, String value) throws Exception {
        SearchResponse<ObjectNode> response = client.search(s -> s
                        .index(INDEX_NAME)
                        .query(q -> q
                                .match(m -> m
                                        .field(fieldName)
                                        .query(fieldValue -> fieldValue.stringValue(value))
                                )
                        ).size(20)
                        .sort(so -> so.field(f -> f.field("@timestamp").order(org.opensearch.client.opensearch._types.SortOrder.Desc)))
                , ObjectNode.class
        );
        return formatResponseToJson(response);
    }

    public static String searchByNestedFieldValue(OpenSearchClient client, String nestedField, String value) throws Exception {
        SearchResponse<ObjectNode> response = client.search(s -> s
                        .index(INDEX_NAME)
                        .query(q -> q
                                .match(m -> m
                                        .field(nestedField + ".keyword")
                                        .query(fieldValue -> fieldValue.stringValue(value))
                                )
                        ).size(20)
                        .sort(so -> so.field(f -> f.field("@timestamp").order(org.opensearch.client.opensearch._types.SortOrder.Desc)))
                , ObjectNode.class
        );
        return formatResponseToJson(response);
    }

    private static String formatResponseToJson(SearchResponse<ObjectNode> response) throws Exception {
        List<ObjectNode> results = new ArrayList<>();
        for (Hit<ObjectNode> hit : response.hits().hits()) {
            results.add(hit.source());
        }
        return objectMapper.writeValueAsString(results);
    }
}
