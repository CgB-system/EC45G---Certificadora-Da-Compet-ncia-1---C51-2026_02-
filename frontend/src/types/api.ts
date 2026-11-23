export interface UsuarioLoginDTO {
  email: string;
  senha: string;
}

export interface TokenDTO {
  accessToken: string;
  refreshToken: string;
  tipo: string;
  expiraEmSegundos: number;
}

export interface UsuarioResponseDTO {
  id: number;
  nome: string;
  sobrenome: string;
  email: string;
  ativo: boolean;
  tipo: "ADMINISTRADOR" | "VOLUNTARIO" | "USUARIO";
}

export interface CursoResponseDTO {
  id: number;
  nome: string;
}

export interface HabilidadeResponseDTO {
  id: number;
  nome: string;
}

export interface TelefoneDTO {
  codigo: string;
  ddd: string;
  numero: string;
}

export interface TelefoneResponseDTO {
  id: number;
  codigo: string;
  ddd: string;
  numero: string;
}

export type StatusVoluntario = "ATIVO" | "INATIVO";

export interface VoluntarioResponseDTO {
  id: number;
  nome: string;
  sobrenome: string;
  email: string;
  ra: string;
  periodo: number;
  coeficiente: number;
  experiencia: string | null;
  status: StatusVoluntario;
  ativo: boolean;
  curso: CursoResponseDTO;
  habilidades: HabilidadeResponseDTO[];
  telefones: TelefoneResponseDTO[];
}

export interface VoluntarioCadastroDTO {
  nome: string;
  sobrenome: string;
  email: string;
  ra: string;
  periodo: number;
  coeficiente: number;
  cursoId: number;
  experiencia?: string;
  status: StatusVoluntario;
  habilidadesIds?: number[];
  telefones?: TelefoneDTO[];
}

export interface VoluntarioAtualizacaoDTO {
  nome: string;
  sobrenome: string;
  email: string;
  ra: string;
  periodo: number;
  coeficiente: number;
  cursoId: number;
  experiencia?: string;
  habilidadesIds?: number[];
}

export interface VoluntarioFiltroDTO {
  nome?: string;
  sobrenome?: string;
  ra?: string;
  cursoId?: number;
  periodo?: number;
  coeficienteMinimo?: number;
  coeficienteMaximo?: number;
  habilidadeId?: number;
  status?: string;
  ativo?: boolean;
  page?: number;
  size?: number;
  sort?: string;
}

export interface PaginaDTO<T> {
  conteudo: T[];
  pagina: number;
  tamanho: number;
  totalElementos: number;
  totalPaginas: number;
}

export interface ResumoDTO {
  totalVoluntarios: number;
  voluntariosAtivos: number;
  voluntariosInativos: number;
  totalCursos: number;
  totalHabilidades: number;
}

export interface ContagemDTO {
  rotulo: string;
  total: number;
}

export interface FormularioCriacaoDTO {
  titulo: string;
  descricao?: string;
}

export interface FormularioDTO {
  id: string;
  titulo: string;
  descricao: string;
  urlResposta: string;
  urlEdicao: string;
}

export type StatusResposta = "PENDENTE" | "CONVERTIDA" | "DESCARTADA";

export interface RespostaResponseDTO {
  id: string;
  formularioId: string;
  criadaEm: string;
  respostas: Record<string, string>;
  status: StatusResposta;
}

export interface ImportacaoRespostasDTO {
  total: number;
  pendentes: number;
  convertidas: number;
  descartadas: number;
  respostas: RespostaResponseDTO[];
}

export interface ValidacaoRespostaDTO {
  valida: boolean;
  problemas: string[];
}

export interface ConversaoRespostaDTO {
  formularioId: string;
  respostaId: string;
  voluntario: VoluntarioResponseDTO;
}

export interface EnvioFormularioDTO {
  destinatarios: string[];
  assunto: string;
  mensagem?: string;
  formularioId?: string;
  linkFormulario?: string;
}

export interface ResultadoEnvioDTO {
  destinatario: string;
  enviado: boolean;
  erro: string | null;
}

export interface CampoErro {
  field: string;
  message: string;
}

export interface ErroResposta {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  errors?: CampoErro[];
}
