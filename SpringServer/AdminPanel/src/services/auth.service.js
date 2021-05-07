import axios from "axios";
import { sha256 } from 'js-sha256';

const API_URL = window.location.origin + "/api/auth/";

class AuthService {
  login(emailId, password) {
    password = sha256(password)
    return axios
      .post(API_URL + "login", {
        emailId,
        password
      })
      .then(response => {
        if (response.data.token) {
          localStorage.setItem("user", JSON.stringify(response.data));
        }

        return response.data;
      });
  }

  logout() {
    localStorage.removeItem("user");
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));;
  }
}

export default new AuthService();
