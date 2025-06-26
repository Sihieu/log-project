import axios from 'axios';

const client = axios.create({
  baseURL: 'http://localhost:9200',
  auth: {
    username: 'admin',
    password: 'Hus@334nt' /
  },
  headers: {
    'Content-Type': 'application/json'
  }
});

export default client;
