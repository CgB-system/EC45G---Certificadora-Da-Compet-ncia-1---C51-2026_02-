import { apiClient } from './api';
import { voluntarioService } from './voluntarioService';
import type { ResumoDTO, ContagemDTO, VoluntarioResponseDTO } from '../types/api';

let inFlightVoluntariosAtivos: Promise<VoluntarioResponseDTO[]> | null = null;
let inFlightStatus: Promise<ContagemDTO[]> | null = null;

async function buscarTodosVoluntariosAtivos(): Promise<VoluntarioResponseDTO[]> {
  if (inFlightVoluntariosAtivos) {
    return inFlightVoluntariosAtivos;
  }

  inFlightVoluntariosAtivos = (async () => {
    try {
      const primeiraPagina = await voluntarioService.listar({
        status: 'ATIVO',
        ativo: true,
        page: 0,
        size: 100,
        sort: 'id,asc',
      });

      const acumulado: VoluntarioResponseDTO[] = [...(primeiraPagina?.conteudo ?? [])];
      const totalPaginas = primeiraPagina?.totalPaginas ?? 1;

      for (let pagina = 1; pagina < totalPaginas; pagina++) {
        const prox = await voluntarioService.listar({
          status: 'ATIVO',
          ativo: true,
          page: pagina,
          size: 100,
          sort: 'id,asc',
        });
        if (prox?.conteudo) {
          acumulado.push(...prox.conteudo);
        }
      }

                                                                    
      return acumulado.filter((v) => v.status === 'ATIVO' && v.ativo !== false);
    } finally {
      inFlightVoluntariosAtivos = null;
    }
  })();

  return inFlightVoluntariosAtivos;
}

async function buscarContagemStatusSingleFlight(): Promise<ContagemDTO[]> {
  if (inFlightStatus) {
    return inFlightStatus;
  }

  inFlightStatus = (async () => {
    try {
      const res = await apiClient.get<ContagemDTO[]>('/dashboard/voluntarios/status');
      return res.data ?? [];
    } finally {
      inFlightStatus = null;
    }
  })();

  return inFlightStatus;
}

export const dashboardService = {
  async obterResumo(): Promise<ResumoDTO> {
    const [resumoRes, statusList] = await Promise.all([
      apiClient.get<ResumoDTO>('/dashboard/resumo'),
      buscarContagemStatusSingleFlight().catch(() => [] as ContagemDTO[]),
    ]);

    const base = resumoRes.data;
    const voluntariosAtivos = statusList
      .filter((s) => s.rotulo?.trim().toUpperCase() === 'ATIVO')
      .reduce((acc, s) => acc + (s.total ?? 0), 0);

    const voluntariosInativos = statusList
      .filter((s) => s.rotulo?.trim().toUpperCase() === 'INATIVO')
      .reduce((acc, s) => acc + (s.total ?? 0), 0);

    const totalVoluntarios = voluntariosAtivos + voluntariosInativos;

    return {
      totalVoluntarios,
      voluntariosAtivos,
      voluntariosInativos,
      totalCursos: base?.totalCursos ?? 0,
      totalHabilidades: base?.totalHabilidades ?? 0,
    };
  },

  async obterStatus(): Promise<ContagemDTO[]> {
    const statusList = await buscarContagemStatusSingleFlight();
    const ativos = statusList
      .filter((s) => s.rotulo?.trim().toUpperCase() === 'ATIVO')
      .reduce((acc, s) => acc + (s.total ?? 0), 0);
    const inativos = statusList
      .filter((s) => s.rotulo?.trim().toUpperCase() === 'INATIVO')
      .reduce((acc, s) => acc + (s.total ?? 0), 0);

    if (ativos === 0 && inativos === 0) {
      return [];
    }

    return [
      { rotulo: 'ATIVO', total: ativos },
      { rotulo: 'INATIVO', total: inativos },
    ];
  },

  async obterCursos(): Promise<ContagemDTO[]> {
    const ativos = await buscarTodosVoluntariosAtivos();
    const contagemPorCurso = new Map<string, number>();

    for (const v of ativos) {
      const nomeCurso = v.curso?.nome?.trim();
      if (nomeCurso) {
        contagemPorCurso.set(nomeCurso, (contagemPorCurso.get(nomeCurso) ?? 0) + 1);
      }
    }

    return Array.from(contagemPorCurso.entries())
      .map(([rotulo, total]) => ({ rotulo, total }))
      .sort((a, b) => b.total - a.total || a.rotulo.localeCompare(b.rotulo));
  },

  async obterPeriodos(): Promise<ContagemDTO[]> {
    const ativos = await buscarTodosVoluntariosAtivos();
    const contagemPorPeriodo = new Map<number, number>();

    for (const v of ativos) {
      if (typeof v.periodo === 'number') {
        contagemPorPeriodo.set(v.periodo, (contagemPorPeriodo.get(v.periodo) ?? 0) + 1);
      }
    }

    return Array.from(contagemPorPeriodo.entries())
      .sort((a, b) => a[0] - b[0])
      .map(([periodo, total]) => ({
        rotulo: `${periodo}º Período`,
        total,
      }));
  },

  async obterHabilidades(): Promise<ContagemDTO[]> {
    const ativos = await buscarTodosVoluntariosAtivos();
    const contagemPorHabilidade = new Map<string, number>();

    for (const v of ativos) {
      for (const hab of v.habilidades ?? []) {
        const nomeHab = hab.nome?.trim();
        if (nomeHab) {
          contagemPorHabilidade.set(nomeHab, (contagemPorHabilidade.get(nomeHab) ?? 0) + 1);
        }
      }
    }

    return Array.from(contagemPorHabilidade.entries())
      .map(([rotulo, total]) => ({ rotulo, total }))
      .sort((a, b) => b.total - a.total || a.rotulo.localeCompare(b.rotulo));
  },
};
