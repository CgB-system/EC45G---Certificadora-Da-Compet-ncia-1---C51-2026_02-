import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Plus,
  ExternalLink,
  Users,
  CheckCircle,
  Clock,
  ArrowRight,
  FileQuestion,
} from 'lucide-react';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { formularioService } from '../../services/formularioService';
import type { FormularioDTO } from '../../types/api';

export const FormulariosPage: React.FC = () => {
  const navigate = useNavigate();
  const [formularios, setFormularios] = useState<FormularioDTO[]>([]);
  const [totalRespostas, setTotalRespostas] = useState(0);

  useEffect(() => {
    formularioService
      .listar()
      .then(async (data) => {
        if (data) {
          setFormularios(data);
          if (data.length > 0) {
            const resps = await formularioService.listarRespostas(data[0].id).catch(() => []);
            setTotalRespostas(resps.length);
          }
        }
      })
      .catch(() => setFormularios([]));
  }, []);

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-16 animate-in fade-in duration-200">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-extrabold text-white tracking-tight">
            Processos Seletivos & Formulários
          </h1>
          <p className="text-sm text-[#9CA3AF] mt-1 font-medium font-sub">
            Gerenciamento e envio de formulários do Google Forms para alunos da UTFPR
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Button
            variant="outline"
            onClick={() => navigate('/formularios/respostas')}
            className="rounded-full px-5 text-xs font-sub"
          >
            Consultar Respostas
          </Button>
          <Button
            onClick={() => navigate('/formularios/novo')}
            className="rounded-full px-5 font-bold font-sub"
          >
            <Plus className="w-4 h-4" />
            Novo Formulário
          </Button>
        </div>
      </div>

      {/* Cards de Status de Seletivos */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="p-6 bg-[#181919] border-[#262828]">
          <div className="flex items-center justify-between text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
            <span>Processos Registrados</span>
            <Clock className="w-4 h-4 text-[#71C3A5]" />
          </div>
          <div className="mt-4 text-3xl font-black text-white">{formularios.length}</div>
          <p className="mt-2 text-xs text-[#9CA3AF] font-sub">
            Formulários integrados ao backend
          </p>
        </Card>

        <Card className="p-6 bg-[#181919] border-[#262828]">
          <div className="flex items-center justify-between text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
            <span>Respostas Recebidas</span>
            <CheckCircle className="w-4 h-4 text-[#71C3A5]" />
          </div>
          <div className="mt-4 text-3xl font-black text-[#71C3A5]">{totalRespostas}</div>
          <p className="mt-2 text-xs text-[#9CA3AF] font-sub">
            Inscrições captadas no seletivo
          </p>
        </Card>

        <Card className="p-6 bg-[#181919] border-[#262828]">
          <div className="flex items-center justify-between text-xs font-semibold text-[#8E9090] uppercase tracking-wider font-sub">
            <span>Status da Integração</span>
            <Users className="w-4 h-4 text-[#71C3A5]" />
          </div>
          <div className="mt-4 text-xl font-bold text-white">Google Forms API</div>
          <p className="mt-2 text-xs text-[#9CA3AF] font-sub">
            Endpoint /api/v1/formularios ativo
          </p>
        </Card>
      </div>

      {/* Lista de Formulários */}
      <div className="space-y-4">
        <h2 className="text-xl font-bold text-white tracking-tight">Formulários Registrados</h2>

        {formularios.length > 0 ? (
          <div className="space-y-4">
            {formularios.map((f, idx) => (
              <Card
                key={f.id || idx}
                className="p-6 bg-[#181919] border-[#262828] hover:border-[#383a3a] transition-colors"
              >
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                  <div className="space-y-2 max-w-3xl">
                    <div className="flex items-center gap-3">
                      <span className="font-bold text-lg text-white">{f.titulo}</span>
                      <Badge variant="ativo">Ativo</Badge>
                    </div>
                    <p className="text-sm text-[#9CA3AF] leading-relaxed">{f.descricao}</p>
                    <div className="text-xs text-[#8E9090] font-mono">ID: {f.id}</div>
                  </div>

                  <div className="flex flex-wrap items-center gap-3">
                    {f.urlResposta && (
                      <a
                        href={f.urlResposta}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center gap-1.5 px-4 py-2 rounded-xl text-xs font-medium border border-[#383a3a] text-[#CECECE] hover:text-white hover:bg-[#202222] transition-colors font-sub"
                      >
                        Abrir Forms <ExternalLink className="w-3.5 h-3.5" />
                      </a>
                    )}

                    <Button
                      onClick={() => navigate('/formularios/respostas')}
                      className="rounded-xl px-4 py-2 text-xs font-bold font-sub"
                    >
                      Ver Respostas <ArrowRight className="w-3.5 h-3.5 ml-1" />
                    </Button>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <Card className="p-12 text-center text-[#8E9090] bg-[#181919] border-[#262828] space-y-3">
            <FileQuestion className="w-8 h-8 text-[#8E9090] mx-auto opacity-70" />
            <div className="text-sm font-medium text-white font-sub">
              Nenhum formulário de processo seletivo cadastrado no banco de dados.
            </div>
            <p className="text-xs text-[#8E9090] max-w-md mx-auto font-sub">
              Clique em "+ Novo Formulário" para registrar um Google Forms e dispará-lo por e-mail para os estudantes da universidade.
            </p>
          </Card>
        )}
      </div>
    </div>
  );
};
