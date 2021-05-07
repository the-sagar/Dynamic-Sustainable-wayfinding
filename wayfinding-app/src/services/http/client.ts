import axios from 'axios'

const SERVER_URL = "http://localhost:8090"

const AxiosInstance = axios.create({
  baseURL: SERVER_URL,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json'
  }
})

type HandleErrorProps = {
  message: string,
  data?: any,
  status?: string,
}

export const handleError = ({ message, data, status }: HandleErrorProps) => {
  return Promise.reject({ message, data, status })
}

let currentAuthToken = "";

export function setAuthToken(token: string) {
  currentAuthToken = token;
}

AxiosInstance.interceptors.request.use(function (config) {
  if(currentAuthToken !== "")
    config.headers.Authorization = 'Bearer ' + currentAuthToken;
  return config;
});

AxiosInstance.interceptors.response.use(
  (response) => response,
  ({ message, response: { data, status } }) => {
    return handleError({ message, data, status })
  }
)

export default AxiosInstance
