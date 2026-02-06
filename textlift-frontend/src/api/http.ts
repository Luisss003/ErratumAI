import axios from "axios";
import { clearSession } from "../auth/token";

//Create an Axios instance with base URL and credentials included
export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
});

//Intercepts responses to handle 401 errors
//Redirects user to login page if not able to be authenticated
http.interceptors.response.use(
  (res) => res,
  (error) => {
    const status = error?.response?.status;
    if (status === 401) {
      clearSession();
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

//Catch Axios errors
export function getErrorMsg(err: unknown) {
  if (axios.isAxiosError(err)) {
    const msg =
      (err.response?.data as any)?.message ||
      (typeof err.response?.data === "string" ? err.response?.data : null) ||
      err.message;

    return msg || "Registration failed.";
  }
  return "Registration failed due to an unknown error.";
}