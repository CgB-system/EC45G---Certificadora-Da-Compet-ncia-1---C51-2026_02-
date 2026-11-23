import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import type { ErroResposta, TokenDTO } from '../types/api';
import { tokenStorage } from './tokenStorage';

export const API_BASE_URL =
  import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

interface RetryableRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

                                                                        
let refreshPromise: Promise<string> | null = null;

async function renovarAccessTokenSingleFlight(): Promise<string> {
  if (refreshPromise) {
    return refreshPromise;
  }

  const refreshToken = tokenStorage.getRefreshToken();
  if (!refreshToken) {
    throw new Error('Refresh token ausente.');
  }

  refreshPromise = axios
    .post<TokenDTO>(
      `${API_BASE_URL}/auth/refresh`,
      { refreshToken },
      { headers: { 'Content-Type': 'application/json' }, timeout: 10000 }
    )
    .then((res) => {
      const { accessToken, refreshToken: novoRefresh } = res.data;
      tokenStorage.setTokens(accessToken, novoRefresh);
      return accessToken;
    })
    .finally(() => {
      refreshPromise = null;
    });

  return refreshPromise;
}

function notificarSessaoExpirada(): void {
  tokenStorage.clearTokens();
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new Event('tedi:unauthorized'));
  }
}

                                                                       
apiClient.interceptors.request.use((config) => {
  const token = tokenStorage.getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

                                                                       
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ErroResposta>) => {
    const originalRequest = error.config as RetryableRequestConfig | undefined;
    const status = error.response?.status;
    const url = originalRequest?.url || '';

    const isAuthPublicEndpoint =
      url.includes('/auth/login') || url.includes('/auth/refresh');

    if (
      status === 401 &&
      originalRequest &&
      !originalRequest._retry &&
      !isAuthPublicEndpoint
    ) {
      const hasRefresh = Boolean(tokenStorage.getRefreshToken());
      if (!hasRefresh) {
        notificarSessaoExpirada();
        return Promise.reject(error);
      }

      originalRequest._retry = true;

      try {
        const novoAccessToken = await renovarAccessTokenSingleFlight();
        originalRequest.headers.Authorization = `Bearer ${novoAccessToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        notificarSessaoExpirada();
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as ErroResposta | undefined;
    if (data?.message) return data.message;
    if (data?.errors && data.errors.length > 0) {
      return data.errors.map((e) => `${e.field}: ${e.message}`).join(', ');
    }
    if (error.response?.status === 401) {
      return 'Sessão expirada ou credenciais inválidas.';
    }
    if (error.response?.status === 403) {
      return 'Acesso negado. Esta operação exige perfil de Administrador.';
    }
    if (error.response?.status === 429) {
      return 'Muitas tentativas de login. Aguarde alguns minutos e tente novamente.';
    }
    if (error.code === 'ERR_NETWORK') {
      return 'Não foi possível conectar ao servidor backend (http://localhost:8080). Verifique se o Spring Boot está em execução.';
    }
    if (error.response?.statusText) {
      return `Erro ${error.response.status}: ${error.response.statusText}`;
    }
    if (error.message) return error.message;
  }
  if (error instanceof Error) {
    return error.message;
  }
  return 'Ocorreu um erro inesperado ao se comunicar com o servidor.';
}
