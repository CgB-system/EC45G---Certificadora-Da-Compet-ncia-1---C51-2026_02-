import { apiClient } from './api';
import type {
  TokenDTO,
  UsuarioLoginDTO,
  UsuarioResponseDTO,
} from '../types/api';

export const authService = {
  async login(credenciais: UsuarioLoginDTO): Promise<TokenDTO> {
    const res = await apiClient.post<TokenDTO>('/auth/login', credenciais);
    return res.data;
  },

  async refresh(refreshToken: string): Promise<TokenDTO> {
    const res = await apiClient.post<TokenDTO>('/auth/refresh', { refreshToken });
    return res.data;
  },

  async me(): Promise<UsuarioResponseDTO> {
    const res = await apiClient.get<UsuarioResponseDTO>('/auth/me');
    return res.data;
  },

  async logout(refreshToken: string): Promise<void> {
    await apiClient.post('/auth/logout', { refreshToken });
  },
};
