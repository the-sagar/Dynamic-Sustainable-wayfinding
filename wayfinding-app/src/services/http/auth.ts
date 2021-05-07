import apiClient from './client'
import * as Crypto from 'expo-crypto';

export const loginRequest = async (email: string, password: string) => {
  const digest = await Crypto.digestStringAsync(
    Crypto.CryptoDigestAlgorithm.SHA256,
    email+password
  );
  try {
    const response = await apiClient.post('api/auth/login', {
      "emailId": email,
      "password": digest
    });
    return response?.data;
  } catch (error) {
    return error?.data;
  }
}

export const registerRequest = async (email: string, password: string) => {
  const digest = await Crypto.digestStringAsync(
    Crypto.CryptoDigestAlgorithm.SHA256,
    email+password
  );
  const response = await apiClient.post('api/auth/register', {
    "emailId": email,
    "password": digest
  })
  return response.data
}
