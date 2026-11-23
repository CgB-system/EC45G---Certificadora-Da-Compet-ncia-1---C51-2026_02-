import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Users, BookOpen, Clock, ArrowRight, Plus } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { dashboardService } from '../../services/dashboardService';
import { voluntarioService } from '../../services/voluntarioService';
import type { ResumoDTO, ContagemDTO, VoluntarioResponseDTO } from '../../types/api';

export const VoluntariosOverviewPage: React.FC = () => {
  const navigate = useNavigate();
  const [resumo, setResumo] = useState<ResumoDTO | null>(null);
  const [distCursos, setDistCursos] = useState<ContagemDTO[]>([]);
  const [distPeriodos, setDistPeriodos] = useState<ContagemDTO[]>([]);
  const [recentes, setRecentes] = useState<VoluntarioResponseDTO[]>([]);
  const [tabDistribuicao, setTabDistribuicao] = useState<'curso' | 'periodo'>('curso');

  const carregarDados = async () => {
    try {
      const [resumoData, cursosData, periodosData, voluntariosData] = await Promise.all([
        dashboardService.obterResumo().catch(() => null),
        dashboardService.obterCursos().catch(() => []),
        dashboardService.obterPeriodos().catch(() => []),
        voluntarioService.listar({ size: 4, sort: 'id,desc' }).catch(() => null),
      ]);

      if (resumoData) {
        setResumo(resumoData);
      }
      if (cursosData) {
        setDistCursos(cursosData);
      }
      if (periodosData) {
        setDistPeriodos(periodosData);
      }
      if (voluntariosData?.conteudo) {
        setRecentes(voluntariosData.conteudo);
      }
    } catch {
      // Nenhum dado falso injetado
    }
  };

  useEffect(() => {
    carregarDados();
  }, []);

  const totalAtivos = resumo?.voluntariosAtivos ?? 0;
  const totalCursos = resumo?.totalCursos ?? 0;
  const totalInativos = resumo?.voluntariosInativos ?? 0;

  const listaAtiva = tabDistribuicao === 'curso' ? distCursos : distPeriodos;
  const maxTotal = listaAtiva.length > 0 ? Math.max(...listaAtiva.map((i) => i.total), 1) : 1;

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-12 animate-in fade-in duration-200">
      {/* Top Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-extrabold text-white tracking-tight">Voluntários</h1>
          <p className="text-sm text-[#9CA3AF] mt-1 font-sub">
            Panorama geral da equipe ativa e histórico de participação no TEDI
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Button
            variant="outline"
            onClick={() => navigate('/voluntarios/gerenciar')}
            className="rounded-full px-5"
          >
            Gerenciar Lista
          </Button>
          <Button
            onClick={() => navigate('/voluntarios/novo')}
            className="rounded-full px-5 font-bold"
          >
            <Plus className="w-4 h-4" />
            Novo Voluntário
          </Button>
        </div>
      </div>

      {/* KPI Cards (3 Cards) */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Card 1: Voluntários Ativos */}
        <Card className="p-6 relative overflow-hidden bg-[#181919] border-[#262828] hover:border-[#383a3a] transition-colors">
          <div className="flex items-center justify-between text-xs font-semibold text-[#8C9090] tracking-wider uppercase font-sub">
            <span>Voluntários Ativos</span>
            <Users className="w-4 h-4 text-[#8C9090]" />
          </div>
          <div className="mt-4 flex items-baseline gap-3">
            <span className="text-4xl font-black text-white">{totalAtivos}</span>
            {totalAtivos > 0 && (
              <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-[#153428] text-[#71C3A5] font-sub">
                Ativos no sistema
              </span>
            )}
          </div>
          <p className="mt-2 text-xs text-[#9CA3AF] font-sub">Integrantes ativos cadastrados no banco</p>
        </Card>

        {/* Card 2: Cursos da UTFPR */}
        <Card className="p-6 relative overflow-hidden bg-[#181919] border-[#262828] hover:border-[#383a3a] transition-colors">
          <div className="flex items-center justify-between text-xs font-semibold text-[#8C9090] tracking-wider uppercase font-sub">
            <span>Cursos da UTFPR</span>
            <BookOpen className="w-4 h-4 text-[#8C9090]" />
          </div>
          <div className="mt-4 flex items-baseline gap-3">
            <span className="text-4xl font-black text-white">{totalCursos}</span>
          </div>
          <p className="mt-2 text-xs text-[#9CA3AF] font-sub">Cursos vinculados na plataforma</p>
        </Card>

        {/* Card 3: Inativos / Egressos */}
        <Card className="p-6 relative overflow-hidden bg-[#181919] border-[#262828] hover:border-[#383a3a] transition-colors">
          <div className="flex items-center justify-between text-xs font-semibold text-[#8C9090] tracking-wider uppercase font-sub">
            <span>Inativos / Egressos</span>
            <Clock className="w-4 h-4 text-[#8C9090]" />
          </div>
          <div className="mt-4 flex items-baseline gap-3">
            <span className="text-4xl font-black text-white">{totalInativos}</span>
          </div>
          <p className="mt-2 text-xs text-[#9CA3AF] font-sub">Voluntários no histórico com status inativo</p>
        </Card>
      </div>

      {/* Main Grid: Distribuição da Equipe + Ingressos por Semestre */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left Column: Distribuição da Equipe Ativa (7 cols) */}
        <Card className="lg:col-span-7 p-6 bg-[#181919] border-[#262828]">
          <div className="flex items-center justify-between pb-4 border-b border-[#242626]">
            <h2 className="text-lg font-bold text-white tracking-tight">
              Distribuição da Equipe Ativa
            </h2>
            <div className="flex items-center gap-3 text-xs font-sub">
              <button
                onClick={() => setTabDistribuicao('curso')}
                className={`font-medium transition-colors pb-0.5 cursor-pointer ${
                  tabDistribuicao === 'curso'
                    ? 'text-[#71C3A5] border-b-2 border-[#71C3A5]'
                    : 'text-[#8E9090] hover:text-white'
                }`}
              >
                Por Curso
              </button>
              <button
                onClick={() => setTabDistribuicao('periodo')}
                className={`font-medium transition-colors pb-0.5 cursor-pointer ${
                  tabDistribuicao === 'periodo'
                    ? 'text-[#71C3A5] border-b-2 border-[#71C3A5]'
                    : 'text-[#8E9090] hover:text-white'
                }`}
              >
                Por Período
              </button>
            </div>
          </div>

          <div className="mt-6 space-y-4">
            {listaAtiva.length > 0 ? (
              listaAtiva.map((item, idx) => {
                const pct = Math.round((item.total / maxTotal) * 100);
                return (
                  <div key={idx} className="space-y-1.5">
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-[#CECECE] font-medium">{item.rotulo}</span>
                      <span className="text-white font-bold font-sub">({item.total})</span>
                    </div>
                    <div className="h-2.5 w-full bg-[#242626] rounded-full overflow-hidden">
                      <div
                        className="h-full bg-[#71C3A5] rounded-full transition-all duration-500"
                        style={{ width: `${pct}%` }}
                      />
                    </div>
                  </div>
                );
              })
            ) : (
              <div className="py-12 text-center text-sm text-[#8E9090]">
                Nenhum registro encontrado para exibição da distribuição no momento.
              </div>
            )}
          </div>
        </Card>

        {/* Right Column: Ingressos por Semestre & Recém-Cadastrados (5 cols) */}
        <div className="lg:col-span-5 space-y-6">
          <Card className="p-6 bg-[#181919] border-[#262828]">
            <h2 className="text-lg font-bold text-white tracking-tight">Ingressos por Semestre</h2>

            {/* Ingressos recentes baseados em dados reais */}
            <div className="mt-6 h-36 flex items-center justify-center border border-dashed border-[#2b2d2d] rounded-xl text-center p-4">
              <span className="text-xs text-[#8E9090] font-sub">
                {totalAtivos > 0
                  ? `${totalAtivos} voluntários ativos cadastrados no semestre atual.`
                  : 'Aguardando cadastro de voluntários para compor o histórico de ingressos.'}
              </span>
            </div>

            {/* Recém-Cadastrados */}
            <div className="mt-8 pt-6 border-t border-[#242626]">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
                  Recém-cadastrados
                </span>
                <button
                  onClick={() => navigate('/voluntarios/gerenciar')}
                  className="text-xs font-medium text-[#71C3A5] hover:underline flex items-center gap-1 cursor-pointer font-sub"
                >
                  Ver todos <ArrowRight className="w-3 h-3" />
                </button>
              </div>

              <div className="space-y-2">
                {recentes.length > 0 ? (
                  recentes.slice(0, 3).map((vol) => (
                    <div
                      key={vol.id}
                      className="flex items-center justify-between p-3 rounded-xl bg-[#202222] border border-[#2b2d2d] text-sm"
                    >
                      <span className="text-white font-medium">
                        {vol.nome} {vol.sobrenome ? `${vol.sobrenome.charAt(0)}.` : ''}
                      </span>
                      <span className="text-xs text-[#9CA3AF] truncate max-w-[180px] font-sub">
                        {vol.curso?.nome || '—'}
                      </span>
                    </div>
                  ))
                ) : (
                  <div className="py-4 text-center text-xs text-[#8E9090] font-sub">
                    Nenhum voluntário cadastrado recentemente.
                  </div>
                )}
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};
