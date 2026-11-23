import { apiClient } from './api';
import type {
  VoluntarioResponseDTO,
  VoluntarioCadastroDTO,
  VoluntarioAtualizacaoDTO,
  VoluntarioFiltroDTO,
  PaginaDTO,
} from '../types/api';

export const voluntarioService = {
  async listar(filtros: VoluntarioFiltroDTO = {}): Promise<PaginaDTO<VoluntarioResponseDTO>> {
    const params: Record<string, unknown> = {};
    if (filtros.nome) params.nome = filtros.nome;
    if (filtros.sobrenome) params.sobrenome = filtros.sobrenome;
    if (filtros.ra) params.ra = filtros.ra;
    if (filtros.cursoId) params.cursoId = filtros.cursoId;
    if (filtros.periodo) params.periodo = filtros.periodo;
    if (filtros.status && filtros.status !== 'TODOS') params.status = filtros.status;
    if (filtros.ativo !== undefined) params.ativo = filtros.ativo;
    if (filtros.page !== undefined) params.page = filtros.page;
    if (filtros.size !== undefined) params.size = filtros.size;
    if (filtros.sort) params.sort = filtros.sort;

    const res = await apiClient.get<PaginaDTO<VoluntarioResponseDTO>>('/voluntarios', { params });
    return res.data;
  },

  async buscarPorId(id: number): Promise<VoluntarioResponseDTO> {
    const res = await apiClient.get<VoluntarioResponseDTO>(`/voluntarios/${id}`);
    return res.data;
  },

  async cadastrar(dados: VoluntarioCadastroDTO): Promise<VoluntarioResponseDTO> {
    const res = await apiClient.post<VoluntarioResponseDTO>('/voluntarios', dados);
    return res.data;
  },

  async atualizar(id: number, dados: VoluntarioAtualizacaoDTO): Promise<VoluntarioResponseDTO> {
    const res = await apiClient.put<VoluntarioResponseDTO>(`/voluntarios/${id}`, dados);
    return res.data;
  },

  async alterarStatus(id: number, status: 'ATIVO' | 'INATIVO'): Promise<VoluntarioResponseDTO> {
    const res = await apiClient.patch<VoluntarioResponseDTO>(`/voluntarios/${id}/status`, { status });
    return res.data;
  },

  async excluir(id: number): Promise<void> {
    await apiClient.delete(`/voluntarios/${id}`);
  },
};
