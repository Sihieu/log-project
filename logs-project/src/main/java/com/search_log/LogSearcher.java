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

public class LogSearcher {

    private static final String OPENSEARCH_HOST = "localhost";
    private static final int OPENSEARCH_PORT = 9200;
    private static final String INDEX_NAME = "storage-events";
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);

    public static void main(String[] args) throws Exception {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("admin", "Hus@334nt"));

        RestClient restClient = RestClient.builder(
                new HttpHost(OPENSEARCH_HOST, OPENSEARCH_PORT, "http")
        ).setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
        ).build();

        OpenSearchTransport transport = new RestClientTransport(restClient, new org.opensearch.client.json.jackson.JacksonJsonpMapper());
        OpenSearchClient client = new OpenSearchClient(transport);


        System.out.println("--- 1. Tìm tất cả hoạt động của user 'user_alice_101' ---");
        String aliceActivityResult = searchByFieldValue(client, "subject_id", "user_alice_101");
        System.out.println(aliceActivityResult);

        System.out.println("\n--- 2. Tìm tất cả các lần đăng nhập thất bại ---");
        String failedLoginsResult = searchByFieldValue(client, "action", "UserLoginFailed");
        System.out.println(failedLoginsResult);

        System.out.println("\n--- 3. Tìm các sự kiện từ địa chỉ IP '198.51.100.10' (tấn công brute-force) ---");
        String bruteForceIpResult = searchByNestedFieldValue(client, "data.ip_address", "198.51.100.10");
        System.out.println(bruteForceIpResult);

        System.out.println("\n--- 4. Truy vết toàn bộ luồng công việc theo correlation_id 'corr_carol_session_ghi' ---");
        String carolFlowResult = searchByFieldValue(client, "correlation_id", "corr_carol_session_ghi");
        System.out.println(carolFlowResult);
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
                        ).size(10)
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