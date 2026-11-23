import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Users,
  Award,
  Layers,
  GraduationCap,
} from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { dashboardService } from '../../services/dashboardService';
import type { ResumoDTO, ContagemDTO } from '../../types/api';

export const ConsultarDashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const [resumo, setResumo] = useState<ResumoDTO | null>(null);
  const [statusList, setStatusList] = useState<ContagemDTO[]>([]);
  const [cursosList, setCursosList] = useState<ContagemDTO[]>([]);
  const [periodosList, setPeriodosList] = useState<ContagemDTO[]>([]);
  const [habilidadesList, setHabilidadesList] = useState<ContagemDTO[]>([]);

  useEffect(() => {
    Promise.all([
      dashboardService.obterResumo().catch(() => null),
      dashboardService.obterStatus().catch(() => []),
      dashboardService.obterCursos().catch(() => []),
      dashboardService.obterPeriodos().catch(() => []),
      dashboardService.obterHabilidades().catch(() => []),
    ]).then(([r, st, c, p, h]) => {
      if (r) setResumo(r);
      if (st) setStatusList(st);
      if (c) setCursosList(c);
      if (p) setPeriodosList(p);
      if (h) setHabilidadesList(h);
    });
  }, []);

  const totalVoluntarios = resumo?.totalVoluntarios ?? 0;
  const voluntariosAtivos = resumo?.voluntariosAtivos ?? 0;
  const totalCursos = resumo?.totalCursos ?? 0;
  const totalHabilidades = resumo?.totalHabilidades ?? 0;

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-16 animate-in fade-in duration-200">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-extrabold text-white tracking-tight">
            Consultar Dashboard Analítico
          </h1>
          <p className="text-sm text-[#9CA3AF] mt-1 font-medium font-sub">
            Síntese estatística do perfil acadêmico e competências alimentadas pelo backend do TEDI
          </p>
        </div>

        <Button
          variant="outline"
          onClick={() => navigate('/voluntarios')}
          className="rounded-full px-5 text-xs font-sub"
        >
          Ver Panorama
        </Button>
      </div>

      {/* KPI Highlights Bar */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <Card className="p-5 bg-[#181919] border-[#262828]">
          <span className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
            Total Cadastrado
          </span>
          <div className="mt-2 text-3xl font-black text-white">
            {totalVoluntarios}
          </div>
          <span className="text-[11px] text-[#71C3A5] font-sub">Base de integrantes</span>
        </Card>

        <Card className="p-5 bg-[#181919] border-[#262828]">
          <span className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
            Voluntários Ativos
          </span>
          <div className="mt-2 text-3xl font-black text-[#71C3A5]">
            {voluntariosAtivos}
          </div>
          <span className="text-[11px] text-[#9CA3AF] font-sub">Em atuação no semestre</span>
        </Card>

        <Card className="p-5 bg-[#181919] border-[#262828]">
          <span className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
            Cursos Vinculados
          </span>
          <div className="mt-2 text-3xl font-black text-white">
            {totalCursos}
          </div>
          <span className="text-[11px] text-[#9CA3AF] font-sub">UTFPR Campus CP</span>
        </Card>

        <Card className="p-5 bg-[#181919] border-[#262828]">
          <span className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
            Habilidades Mapeadas
          </span>
          <div className="mt-2 text-3xl font-black text-white">
            {totalHabilidades}
          </div>
          <span className="text-[11px] text-[#9CA3AF] font-sub">Competências no banco</span>
        </Card>
      </div>

      {/* Grid de 4 Gráficos: Curso, Período, Habilidades, Status */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Gráfico 1: Distribuição por Cursos */}
        <Card className="p-6 bg-[#181919] border-[#262828] space-y-4">
          <div className="flex items-center justify-between pb-3 border-b border-[#242626]">
            <h3 className="font-bold text-white text-base flex items-center gap-2">
              <GraduationCap className="w-4 h-4 text-[#71C3A5]" />
              Distribuição por Curso
            </h3>
            <span className="text-xs text-[#8E9090] font-sub">Total de alunos</span>
          </div>

          <div className="space-y-3 pt-2">
            {cursosList.length > 0 ? (
              cursosList.map((c, i) => {
                const max = Math.max(...cursosList.map((x) => x.total), 1);
                return (
                  <div key={i} className="space-y-1">
                    <div className="flex justify-between text-xs font-medium">
                      <span className="text-[#CECECE]">{c.rotulo}</span>
                      <span className="text-white font-bold font-sub">{c.total} alunos</span>
                    </div>
                    <div className="h-2 w-full bg-[#242626] rounded-full overflow-hidden">
                      <div
                        className="h-full bg-[#71C3A5] rounded-full"
                        style={{ width: `${(c.total / max) * 100}%` }}
                      />
                    </div>
                  </div>
                );
              })
            ) : (
              <div className="py-12 text-center text-xs text-[#8E9090] font-sub">
                Nenhum registro por curso retornado pelo endpoint /api/v1/dashboard/voluntarios/cursos.
              </div>
            )}
          </div>
        </Card>

        {/* Gráfico 2: Distribuição por Período */}
        <Card className="p-6 bg-[#181919] border-[#262828] space-y-4">
          <div className="flex items-center justify-between pb-3 border-b border-[#242626]">
            <h3 className="font-bold text-white text-base flex items-center gap-2">
              <Layers className="w-4 h-4 text-[#71C3A5]" />
              Distribuição por Período Acadêmico
            </h3>
            <span className="text-xs text-[#8E9090] font-sub">Faixa letiva</span>
          </div>

          {periodosList.length > 0 ? (
            <div className="h-44 flex items-end justify-between gap-4 px-4 pt-4">
              {periodosList.map((p, i) => {
                const max = Math.max(...periodosList.map((x) => x.total), 1);
                const heightPct = Math.round((p.total / max) * 85);
                return (
                  <div key={i} className="flex-1 flex flex-col items-center gap-2 h-full justify-end">
                    <span className="text-xs font-bold text-white font-sub">{p.total}</span>
                    <div
                      className="w-full max-w-[50px] bg-[#71C3A5] rounded-t-lg transition-all duration-300"
                      style={{ height: `${heightPct}%` }}
                    />
                    <span className="text-[10px] text-[#8E9090] font-medium text-center truncate max-w-[80px] font-sub">
                      {p.rotulo}
                    </span>
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="py-16 text-center text-xs text-[#8E9090] font-sub">
              Nenhum registro de período retornado pelo endpoint /api/v1/dashboard/voluntarios/periodos.
            </div>
          )}
        </Card>

        {/* Gráfico 3: Matriz de Habilidades */}
        <Card className="p-6 bg-[#181919] border-[#262828] space-y-4">
          <div className="flex items-center justify-between pb-3 border-b border-[#242626]">
            <h3 className="font-bold text-white text-base flex items-center gap-2">
              <Award className="w-4 h-4 text-[#71C3A5]" />
              Competências e Habilidades
            </h3>
            <span className="text-xs text-[#8E9090] font-sub">Mapeamento técnico</span>
          </div>

          {habilidadesList.length > 0 ? (
            <div className="grid grid-cols-2 gap-3 pt-2">
              {habilidadesList.map((h, i) => (
                <div
                  key={i}
                  className="p-3.5 rounded-xl bg-[#202222] border border-[#2a2c2c] flex items-center justify-between"
                >
                  <div>
                    <div className="text-sm font-semibold text-white">{h.rotulo}</div>
                    <div className="text-xs text-[#8E9090] font-sub">voluntários vinculados</div>
                  </div>
                  <span className="text-lg font-black text-[#71C3A5] font-sub">{h.total}</span>
                </div>
              ))}
            </div>
          ) : (
            <div className="py-12 text-center text-xs text-[#8E9090] font-sub">
              Nenhuma habilidade vinculada encontrada no banco de dados.
            </div>
          )}
        </Card>

        {/* Gráfico 4: Status da Equipe (Ativo vs Inativo) */}
        <Card className="p-6 bg-[#181919] border-[#262828] space-y-4">
          <div className="flex items-center justify-between pb-3 border-b border-[#242626]">
            <h3 className="font-bold text-white text-base flex items-center gap-2">
              <Users className="w-4 h-4 text-[#71C3A5]" />
              Status de Vínculo
            </h3>
            <span className="text-xs text-[#8E9090] font-sub">Proporção da equipe</span>
          </div>

          {statusList.length > 0 ? (
            <div className="pt-4 flex flex-col justify-center space-y-4">
              {statusList.map((st, i) => {
                const isAtivo = st.rotulo === 'ATIVO';
                const total = totalVoluntarios || 1;
                const pct = Math.round((st.total / total) * 100);
                return (
                  <div key={i} className="p-4 rounded-xl bg-[#202222] border border-[#2b2d2d] space-y-2">
                    <div className="flex items-center justify-between">
                      <span className="text-sm font-bold text-white flex items-center gap-2">
                        <span
                          className={`w-2.5 h-2.5 rounded-full ${
                            isAtivo ? 'bg-[#71C3A5]' : 'bg-[#6B7280]'
                          }`}
                        />
                        {isAtivo ? 'Voluntários Ativos' : 'Voluntários Inativos / Egressos'}
                      </span>
                      <span className="text-sm font-black text-white font-sub">
                        {st.total} ({pct}%)
                      </span>
                    </div>
                    <div className="h-2 w-full bg-[#171818] rounded-full overflow-hidden">
                      <div
                        className={`h-full rounded-full ${
                          isAtivo ? 'bg-[#71C3A5]' : 'bg-[#6B7280]'
                        }`}
                        style={{ width: `${pct}%` }}
                      />
                    </div>
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="py-12 text-center text-xs text-[#8E9090] font-sub">
              Nenhum dado de status retornado pelo backend.
            </div>
          )}
        </Card>
      </div>
    </div>
  );
};
