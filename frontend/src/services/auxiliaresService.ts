import { apiClient } from './api';
import type { CursoResponseDTO, HabilidadeResponseDTO } from '../types/api';

export const cursoService = {
  async listar(): Promise<CursoResponseDTO[]> {
    const res = await apiClient.get<CursoResponseDTO[]>('/cursos');
    return res.data;
  },
};

export const habilidadeService = {
  async listar(): Promise<HabilidadeResponseDTO[]> {
    const res = await apiClient.get<HabilidadeResponseDTO[]>('/habilidades');
    return res.data;
  },
};
