import { apiClient } from './api';
import type { ResumoDTO, ContagemDTO } from '../types/api';

export const dashboardService = {
  async obterResumo(): Promise<ResumoDTO> {
    const res = await apiClient.get<ResumoDTO>('/dashboard/resumo');
    return res.data;
  },

  async obterStatus(): Promise<ContagemDTO[]> {
    const res = await apiClient.get<ContagemDTO[]>('/dashboard/voluntarios/status');
    return res.data;
  },

  async obterCursos(): Promise<ContagemDTO[]> {
    const res = await apiClient.get<ContagemDTO[]>('/dashboard/voluntarios/cursos');
    return res.data;
  },

  async obterPeriodos(): Promise<ContagemDTO[]> {
    const res = await apiClient.get<ContagemDTO[]>('/dashboard/voluntarios/periodos');
    return res.data;
  },

  async obterHabilidades(): Promise<ContagemDTO[]> {
    const res = await apiClient.get<ContagemDTO[]>('/dashboard/voluntarios/habilidades');
    return res.data;
  },
};
