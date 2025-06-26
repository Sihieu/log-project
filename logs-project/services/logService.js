import client from './opensearchClient';

export const fetchLogs = async () => {
  const query = {
    query: {
      match_all: {}
    },
    size: 50,
    sort: [
      { "@timestamp": { order: "desc" } }
    ]
  };

  const response = await client.post('/storage-events/_search', query);
  return response.data.hits.hits.map(hit => hit._source);
};
