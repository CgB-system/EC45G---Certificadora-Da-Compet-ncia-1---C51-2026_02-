import { apiClient } from './api';
import type {
  FormularioDTO,
  FormularioCriacaoDTO,
  RespostaResponseDTO,
  ImportacaoRespostasDTO,
  ValidacaoRespostaDTO,
  ConversaoRespostaDTO,
  EnvioFormularioDTO,
  ResultadoEnvioDTO,
} from '../types/api';

export const formularioService = {
  async listar(): Promise<FormularioDTO[]> {
    const res = await apiClient.get<FormularioDTO[]>('/formularios');
    return res.data;
  },

  async buscarPorId(id: string): Promise<FormularioDTO> {
    const res = await apiClient.get<FormularioDTO>(`/formularios/${id}`);
    return res.data;
  },

  async criar(dados: FormularioCriacaoDTO): Promise<FormularioDTO> {
    const res = await apiClient.post<FormularioDTO>('/formularios', dados);
    return res.data;
  },

  async publicar(id: string): Promise<FormularioDTO> {
    const res = await apiClient.post<FormularioDTO>(`/formularios/${id}/publicar`);
    return res.data;
  },

  async encerrar(id: string): Promise<FormularioDTO> {
    const res = await apiClient.post<FormularioDTO>(`/formularios/${id}/encerrar`);
    return res.data;
  },

  async listarRespostas(formId: string, signal?: AbortSignal): Promise<RespostaResponseDTO[]> {
    const res = await apiClient.get<RespostaResponseDTO[]>(`/formularios/${formId}/respostas`, {
      signal,
    });
    return res.data;
  },

  async importarRespostas(formId: string): Promise<ImportacaoRespostasDTO> {
    const res = await apiClient.post<ImportacaoRespostasDTO>(`/formularios/${formId}/respostas/importar`);
    return res.data;
  },

  async validarResposta(formId: string, respId: string): Promise<ValidacaoRespostaDTO> {
    const res = await apiClient.post<ValidacaoRespostaDTO>(`/formularios/${formId}/respostas/${respId}/validar`);
    return res.data;
  },

  async converterEmVoluntario(formId: string, respId: string): Promise<ConversaoRespostaDTO> {
    const res = await apiClient.post<ConversaoRespostaDTO>(`/formularios/${formId}/respostas/${respId}/converter`);
    return res.data;
  },

  async descartarResposta(formId: string, respId: string): Promise<RespostaResponseDTO> {
    const res = await apiClient.post<RespostaResponseDTO>(`/formularios/${formId}/respostas/${respId}/descartar`);
    return res.data;
  },
};

export const emailService = {
  async enviarFormulario(dados: EnvioFormularioDTO): Promise<ResultadoEnvioDTO[]> {
    const res = await apiClient.post<ResultadoEnvioDTO[]>('/email/enviar-formulario', dados);
    return res.data;
  },
};
