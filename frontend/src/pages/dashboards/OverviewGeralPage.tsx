import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Users, FileText, ClipboardCheck, ArrowRight } from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { dashboardService } from '../../services/dashboardService';
import { formularioService } from '../../services/formularioService';
import { useAuth } from '../../contexts/AuthContext';
import type { ResumoDTO, ContagemDTO } from '../../types/api';

export const OverviewGeralPage: React.FC = () => {
  const navigate = useNavigate();
  const { usuario } = useAuth();
  const [resumo, setResumo] = useState<ResumoDTO | null>(null);
  const [cursosVoluntarios, setCursosVoluntarios] = useState<ContagemDTO[]>([]);
  const [totalRespostasForms, setTotalRespostasForms] = useState<number>(0);
  const [totalPendentes, setTotalPendentes] = useState<number>(0);

  useEffect(() => {
                                      
    dashboardService
      .obterResumo()
      .then((res) => {
        if (res) setResumo(res);
      })
      .catch(() => setResumo(null));

    dashboardService
      .obterCursos()
      .then((cursos) => {
        if (cursos) setCursosVoluntarios(cursos);
      })
      .catch(() => setCursosVoluntarios([]));

                                                             
    formularioService
      .listar()
      .then(async (forms) => {
        if (forms && forms.length > 0) {
          const resps = await formularioService.listarRespostas(forms[0].id).catch(() => []);
          setTotalRespostasForms(resps.length);
          setTotalPendentes(resps.filter((r) => r.status === 'PENDENTE').length);
        }
      })
      .catch(() => {
        setTotalRespostasForms(0);
        setTotalPendentes(0);
      });
  }, []);

  const totalVoluntarios = resumo?.voluntariosAtivos ?? 0;
  const totalCursos = resumo?.totalCursos ?? 0;
  const maxCursoTotal =
    cursosVoluntarios.length > 0 ? Math.max(...cursosVoluntarios.map((c) => c.total), 1) : 1;

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-12 animate-in fade-in duration-200">
                        
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-extrabold text-white tracking-tight">
            Olá, {usuario ? `${usuario.nome}${usuario.sobrenome ? ` ${usuario.sobrenome.charAt(0)}.` : ''}` : 'Administrador'}
          </h1>
          <p className="text-sm text-[#9CA3AF] mt-1 font-medium font-sub">
            Semestre 2026.2 • Painel de Gestão e Monitoramento do TEDI
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Button
            variant="outline"
            onClick={() => navigate('/voluntarios/novo')}
            className="rounded-full px-5 font-sub"
          >
            + Cadastrar Voluntário
          </Button>
          <Button
            onClick={() => navigate('/formularios/novo')}
            className="rounded-full px-5 font-bold font-sub"
          >
            + Novo Formulário UTFPR
          </Button>
        </div>
      </div>

                             
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                                          
        <Card className="p-6 bg-[#181919] border-[#262828] hover:border-[#383a3a] transition-colors">
          <div className="flex items-center justify-between text-xs font-semibold text-[#8C9090] tracking-wider uppercase font-sub">
            <span>Voluntários Ativos</span>
            <Users className="w-4 h-4 text-[#8C9090]" />
          </div>
          <div className="mt-4 flex items-baseline gap-3">
            <span className="text-4xl font-black text-white">{totalVoluntarios}</span>
            {totalVoluntarios > 0 && (
              <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium text-[#71C3A5] font-sub">
                Ativos
              </span>
            )}
          </div>
          <p className="mt-2 text-xs text-[#9CA3AF] font-sub">
            Em {totalCursos} cursos da UTFPR
          </p>
        </Card>

                                       
        <Card className="p-6 bg-[#181919] border-[#262828] hover:border-[#383a3a] transition-colors">
          <div className="flex items-center justify-between text-xs font-semibold text-[#8C9090] tracking-wider uppercase font-sub">
            <span>Respostas UTFPR</span>
            <FileText className="w-4 h-4 text-[#8C9090]" />
          </div>
          <div className="mt-4 flex items-baseline gap-3">
            <span className="text-4xl font-black text-white">{totalRespostasForms}</span>
            <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-medium bg-[#153428] text-[#71C3A5] font-sub">
              <span className="w-1.5 h-1.5 rounded-full bg-[#71C3A5]" />
              Forms
            </span>
          </div>
          <p className="mt-2 text-xs text-[#9CA3AF] font-sub">
            Formulários preenchidos por candidatos
          </p>
        </Card>

                                      
        <Card className="p-6 bg-[#181919] border-[#262828] hover:border-[#383a3a] transition-colors">
          <div className="flex items-center justify-between text-xs font-semibold text-[#8C9090] tracking-wider uppercase font-sub">
            <span>Para Avaliação</span>
            <ClipboardCheck className="w-4 h-4 text-[#8C9090]" />
          </div>
          <div className="mt-4 flex items-baseline gap-3">
            <span className="text-4xl font-black text-white">{totalPendentes}</span>
          </div>
          <p className="mt-2 text-xs text-[#9CA3AF] font-sub">
            Aguardando análise de professores • Fichas de candidatos
          </p>
        </Card>
      </div>

                                                 
      <Card className="p-8 bg-[#181919] border-[#262828]">
        <div className="flex items-center justify-between pb-6 border-b border-[#242626]">
          <h2 className="text-xl font-bold text-white tracking-tight">Perfil Acadêmico</h2>
          <button
            onClick={() => navigate('/dashboards/consultar')}
            className="text-xs font-semibold text-[#71C3A5] hover:underline flex items-center gap-1.5 cursor-pointer font-sub"
          >
            Ver dashboard completo <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>

                                                                         
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 mt-6">
                                           
          <div className="space-y-6">
            <div className="font-bold text-base text-white">
              Voluntários Ativos ({totalVoluntarios})
            </div>

                                    
            {cursosVoluntarios.length > 0 ? (
              <div className="space-y-3.5">
                {cursosVoluntarios.map((c, i) => (
                  <div key={i} className="space-y-1">
                    <div className="flex justify-between text-sm">
                      <span className="text-[#CECECE] font-medium">{c.rotulo}</span>
                      <span className="text-white font-bold font-sub">{c.total}</span>
                    </div>
                    <div className="h-2 w-full bg-[#242626] rounded-full overflow-hidden">
                      <div
                        className="h-full bg-[#71C3A5] rounded-full"
                        style={{ width: `${(c.total / maxCursoTotal) * 100}%` }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="py-8 text-center text-xs text-[#8E9090] font-sub">
                Nenhum voluntário vinculado por curso cadastrado no momento.
              </div>
            )}
          </div>

                                               
          <div className="space-y-6">
            <div className="font-bold text-base text-white">
              Candidatos do Seletivo ({totalRespostasForms})
            </div>

            {totalRespostasForms > 0 ? (
              <div className="space-y-3.5">
                <div className="p-4 bg-[#202222] border border-[#2c2e2e] rounded-xl text-xs text-[#CECECE] font-sub">
                  Total de {totalRespostasForms} inscrições submetidas no formulário.{' '}
                  {totalPendentes} aguardando validação para integração ao projeto.
                </div>
              </div>
            ) : (
              <div className="py-8 text-center text-xs text-[#8E9090] font-sub">
                Nenhum candidato do processo seletivo registrado no momento.
              </div>
            )}
          </div>
        </div>
      </Card>
    </div>
  );
};
